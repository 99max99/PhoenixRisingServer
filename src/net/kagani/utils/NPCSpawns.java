package net.kagani.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;

public final class NPCSpawns {

	public static final void init() {
		loadSpawnsList("data/npcs/spawnsList.txt");
		loadCustomSpawnList();
	}

	public static void loadCustomSpawnList() {
		// Quercus
		new NPC(17065, new WorldTile(3135, 3521, 0), -1, false);

		// LVL1
		World.spawnObject(new WorldObject(83301, 10, 0, 3029, 3589, 0));
		World.spawnObject(new WorldObject(83310, 10, 0, 3032, 3596, 0));
		World.spawnObject(new WorldObject(83319, 11, 2, 3038, 3594, 0));
		World.spawnObject(new WorldObject(83328, 10, 1, 3038, 3588, 0));
		World.spawnObject(new WorldObject(83336, 10, 1, 3047, 3592, 0));

		// LVL2
		World.spawnObject(new WorldObject(83301, 10, 0, 3299, 3768, 0));
		World.spawnObject(new WorldObject(83310, 10, 0, 3314, 3772, 0));
		World.spawnObject(new WorldObject(83319, 11, 2, 3311, 3767, 0));
		World.spawnObject(new WorldObject(83328, 10, 1, 3310, 3775, 0));
		World.spawnObject(new WorldObject(83336, 10, 1, 3313, 3762, 0));

		// LVL3
		World.spawnObject(new WorldObject(83301, 10, 0, 3124, 3836, 0));
		World.spawnObject(new WorldObject(83310, 10, 0, 3132, 3842, 0));
		World.spawnObject(new WorldObject(83319, 11, 2, 3138, 3839, 0));
		World.spawnObject(new WorldObject(83328, 10, 1, 3134, 3835, 0));
		World.spawnObject(new WorldObject(83336, 10, 1, 3135, 3849, 0));
	}

	private static class NPCSpawn {

		private NPCSpawn(int npcId, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
			this.npcId = npcId;
			this.tile = tile;
			this.mapAreaNameHash = mapAreaNameHash;
			this.canBeAttackFromOutOfArea = canBeAttackFromOutOfArea;
		}

		private int npcId;
		private WorldTile tile;
		private int mapAreaNameHash;
		private boolean canBeAttackFromOutOfArea;
	}

	private static final List<NPCSpawn>[][] spawns = new ArrayList[256][256];

	private static final void loadSpawnsList(String path) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			int count = 0;
			while (true) {
				count++;
				String line = in.readLine();
				if (line == null)
					break;
				if (line.startsWith("//") || line.startsWith("RSBOT"))
					continue;
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length != 2) {
					in.close();
					throw new RuntimeException("Invalid NPC Spawn line: " + line + " , line number: " + count);
				}
				int npcId = Integer.parseInt(splitedLine[0]);
				if (npcId >= Utils.getNPCDefinitionsSize())
					continue;
				String[] splitedLine2 = splitedLine[1].split(" ", 5);
				if (splitedLine2.length != 3 && splitedLine2.length != 5) {
					in.close();
					throw new RuntimeException("Invalid NPC Spawn line: " + line + " , line number: " + count);
				}
				WorldTile tile = new WorldTile(Integer.parseInt(splitedLine2[0]), Integer.parseInt(splitedLine2[1]),
						Integer.parseInt(splitedLine2[2]));
				int mapAreaNameHash = -1;
				boolean canBeAttackFromOutOfArea = true;
				if (splitedLine2.length == 5) {
					mapAreaNameHash = Utils.getNameHash(splitedLine2[3]);
					canBeAttackFromOutOfArea = Boolean.parseBoolean(splitedLine2[4]);
				}
				addNPCSpawn(npcId, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
			}
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void loadNPCSpawns(int regionId) {
		int x = (regionId >> 8);
		int y = (regionId & 0xff);
		if (spawns[x][y] == null)
			return;
		for (NPCSpawn spawn : spawns[x][y])
			World.spawnNPC(spawn.npcId, spawn.tile, spawn.mapAreaNameHash, spawn.canBeAttackFromOutOfArea);
		spawns[x][y] = null;
	}

	private static final void addNPCSpawn(int npcId, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		// World.spawnNPC(npcId, tile, mapAreaNameHash,
		// canBeAttackFromOutOfArea);

		int x = tile.getRegionX();
		int y = tile.getRegionY();
		if (spawns[x][y] == null)
			spawns[x][y] = new ArrayList<NPCSpawn>();
		spawns[x][y].add(new NPCSpawn(npcId, tile, mapAreaNameHash, canBeAttackFromOutOfArea));
	}

	private NPCSpawns() {
	}
}