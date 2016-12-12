package net.kagani.cache.loaders;

import net.kagani.cache.Cache;
import net.kagani.cache.filestore.io.InputStream;

public class BodyDefinitions {

	public static int anInt5276;
	public static int anInt5280;
	public static int[] disabledSlots;
	public static int[] anIntArray5281;
	public static int[] anIntArray5282;

	public static void init() {
		loadBodyDefinitions(); // in case rs stops using it for just this
	}

	public static int getEquipmentContainerSize() {
		return disabledSlots.length;
	}

	/*
	 * suposely in rsclient its an instance but its only used for this archive
	 * lol
	 */
	private static void loadBodyDefinitions() {
		setDefaultsVariableValues();
		byte[] data = Cache.STORE.getIndexes()[28].getFile(6);
		readOpcodeValues(new InputStream(data));
	}

	private static void setDefaultsVariableValues() {
		anInt5280 = -1;
		anInt5276 = -1;
	}

	private static void readOpcodeValues(InputStream stream) {
		while (true) {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	private static void readValues(InputStream stream, int opcode) {
		if (opcode == 1) {
			int containerSize = stream.readUnsignedByte();
			disabledSlots = new int[containerSize];
			for (int i_2_ = 0; i_2_ < disabledSlots.length; i_2_++) {
				disabledSlots[i_2_] = stream.readUnsignedByte();
				if (disabledSlots[i_2_] != 0 && disabledSlots[i_2_] != 2) {
					/* empty */
				}
			}
		} else if (3 == opcode)
			anInt5280 = stream.readUnsignedByte();
		else if (opcode == 4)
			anInt5276 = stream.readUnsignedByte();
		else if (5 == opcode) {
			anIntArray5281 = new int[stream.readUnsignedByte()];
			for (int i_3_ = 0; i_3_ < anIntArray5281.length; i_3_++)
				anIntArray5281[i_3_] = stream.readUnsignedByte();
		} else if (6 == opcode) {
			anIntArray5282 = new int[stream.readUnsignedByte()];
			for (int i_4_ = 0; i_4_ < anIntArray5282.length; i_4_++)
				anIntArray5282[i_4_] = stream.readUnsignedByte();
		}
	}
}