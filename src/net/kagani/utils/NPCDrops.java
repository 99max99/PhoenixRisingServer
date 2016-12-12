package net.kagani.utils;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.kagani.game.npc.Drop;
import net.kagani.game.npc.Drops;

public class NPCDrops {

	private final static String PACKED_PATH = "data/npcs/packedDrops.d";
	private static HashMap<Integer, Drops> npcDrops = new HashMap<Integer, Drops>();

	public static final void init() {
		loadPackedNPCDrops();
	}

	public static Drops getDrops(int npcId) {
		return npcDrops.get(npcId);
	}

	public static void addDrops(int npcId, Drops drops) {
		npcDrops.put(npcId, drops);
	}

	private static void loadPackedNPCDrops() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());

			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				boolean acessRareTable = buffer.get() == 1;
				Drops drops = new Drops(acessRareTable);
				@SuppressWarnings("unchecked")
				List<Drop>[] dList = new ArrayList[Drops.VERY_RARE + 1];
				int size = (buffer.get() & 0xff);
				for (int i = 0; i < size; i++) {
					int itemId = buffer.getShort() & 0xffff;
					int min = buffer.getShort() & 0xffff;
					int max = buffer.getShort() & 0xffff;
					int rarity = buffer.get() & 0xff;
					if (dList[rarity] == null)
						dList[rarity] = new ArrayList<Drop>();
					Drop drop = new Drop(itemId, min, max);
					dList[rarity].add(drop);
				}
				drops.addDrops(dList);
				npcDrops.put(npcId, drops);
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}
}