package net.kagani.cache.loaders;

import java.util.concurrent.ConcurrentHashMap;

import net.kagani.cache.Cache;
import net.kagani.stream.InputStream;

public class QuickChatOptionDefinition {

	public int id;
	public int[] dynamicDataTypes;
	public int[][] staticData;

	public final QuickChatStringType getType(int i) {
		if (dynamicDataTypes == null || i < 0 || i > dynamicDataTypes.length) {
			return null;
		}
		return findType(dynamicDataTypes[i]);
	}

	static final QuickChatStringType[] listTypes() {

		return new QuickChatStringType[] {
				QuickChatStringType.QC_GENERAL_DATAMAP_0,
				QuickChatStringType.QC_ITEM,
				QuickChatStringType.QC_UNUSED_TYPE_2,
				QuickChatStringType.QC_SKILL,
				QuickChatStringType.QC_SLAYER_ASSIGNMENT_DATAMAP_6,
				QuickChatStringType.QC_UNUSED_DATAMAP_7,
				QuickChatStringType.QC_RANK,
				QuickChatStringType.QC_MINIGAME_POINTS,
				QuickChatStringType.QC_ITEM_TRADE,
				QuickChatStringType.QC_EXPERIENCE,
				QuickChatStringType.QC_FRIEND_CHAT_COUNT,
				QuickChatStringType.QC_COMBAT_LEVEL_AVERAGE,
				QuickChatStringType.QC_SOULWARS_AVATAR,
				QuickChatStringType.QC_COMBAT_LEVEL };
	}

	static final QuickChatStringType findType(int id) {
		QuickChatStringType[] types = listTypes();
		for (int i_0_ = 0; i_0_ < types.length; i_0_++) {
			if (types[i_0_].id == id) {
				return types[i_0_];
			}
		}
		return null;
	}

	public int getTotalResponseSize() {
		int size = 0;
		if (dynamicDataTypes != null) {
			for (int i = 0; i < dynamicDataTypes.length; i++) {
				QuickChatStringType type = getType(i);
				size += type.serverToClientBytes;
			}
		}
		return size;
	}

	private final void readOpcode(InputStream buffer, int opcode) {
		if (opcode == 1) {
			// optionTextParts =
			buffer.readString();
		} else if (opcode == 2) {
			int count = buffer.readUnsignedByte();
			// quickReplyOptions = new int[i_9_];
			for (int i = 0; i < count; i++) {
				// quickReplyOptions[i_10_] =
				buffer.readUnsignedShort();
			}
		} else if (opcode == 3) {
			int count = buffer.readUnsignedByte();
			staticData = new int[count][];
			dynamicDataTypes = new int[count];
			for (int i = 0; i < count; i++) {
				int typeId = buffer.readUnsignedShort();
				QuickChatStringType type = findType(typeId);
				if (type != null) {
					dynamicDataTypes[i] = typeId;
					staticData[i] = new int[type.staticDataCount];
					for (int j = 0; type.staticDataCount > j; j++) {
						staticData[i][j] = buffer.readUnsignedShort();
					}
				}
			}
		} else if (opcode == 4) {
			// isSearchable = false;
		}
	}

	final void readFormat(InputStream buffer) {
		for (;;) {
			int i_2_ = buffer.readUnsignedByte();
			if (i_2_ == 0) {
				break;
			}
			readOpcode(buffer, i_2_);
		}
	}

	private static final ConcurrentHashMap<Integer, QuickChatOptionDefinition> cached = new ConcurrentHashMap<Integer, QuickChatOptionDefinition>();

	public static final QuickChatOptionDefinition loadOption(int id) {
		QuickChatOptionDefinition defs = cached.get(id);
		if (defs != null)
			return defs;
		byte[] data;
		if ((id & 0x8000) == 0) {
			data = Cache.STORE.getIndexes()[24].getFile(1, id);
		} else {
			data = Cache.STORE.getIndexes()[25].getFile(1, id & 0x7FFFF);
		}
		defs = new QuickChatOptionDefinition();
		defs.id = id;
		if (data != null)
			defs.readFormat(new InputStream(data));
		cached.put(id, defs);
		return defs;
	}

	public static class QuickChatStringType {

		public int id;
		public int clientToServerBytes;
		public int serverToClientBytes;
		public int staticDataCount;

		static QuickChatStringType QC_GENERAL_DATAMAP_0 = new QuickChatStringType(
				0, 2, 2, 1);
		static QuickChatStringType QC_ITEM = new QuickChatStringType(1, 2, 2, 0);
		static QuickChatStringType QC_UNUSED_TYPE_2 = new QuickChatStringType(
				2, 4, 4, 0);
		static QuickChatStringType QC_SKILL = new QuickChatStringType(4, 1, 1,
				1);
		static QuickChatStringType QC_SLAYER_ASSIGNMENT_DATAMAP_6 = new QuickChatStringType(
				6, 0, 4, 2);
		static QuickChatStringType QC_UNUSED_DATAMAP_7 = new QuickChatStringType(
				7, 0, 1, 1);
		static QuickChatStringType QC_RANK = new QuickChatStringType(8, 0, 4, 1);
		static QuickChatStringType QC_MINIGAME_POINTS = new QuickChatStringType(
				9, 0, 4, 1);
		static QuickChatStringType QC_ITEM_TRADE = new QuickChatStringType(10,
				2, 2, 0);
		static QuickChatStringType QC_EXPERIENCE = new QuickChatStringType(11,
				0, 1, 2);
		static QuickChatStringType QC_FRIEND_CHAT_COUNT = new QuickChatStringType(
				12, 0, 1, 0);
		static QuickChatStringType QC_COMBAT_LEVEL_AVERAGE = new QuickChatStringType(
				13, 0, 1, 0);
		static QuickChatStringType QC_SOULWARS_AVATAR = new QuickChatStringType(
				14, 0, 4, 1);
		static QuickChatStringType QC_COMBAT_LEVEL = new QuickChatStringType(
				15, 0, 1, 0);

		QuickChatStringType(int i, int i_35_, int i_36_, int i_37_) {
			id = i;
			clientToServerBytes = i_35_;
			serverToClientBytes = i_36_;
			staticDataCount = i_37_;
		}

		@Override
		public final String toString() {
			throw new IllegalStateException();
		}
	}
}
