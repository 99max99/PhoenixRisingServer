package net.kagani.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;

import net.kagani.game.npc.combat.NPCCombatDefinitions;

public final class NPCCombatDefinitionsL {

	public final static HashMap<Integer, NPCCombatDefinitions> npcCombatDefinitions = new HashMap<Integer, NPCCombatDefinitions>();
	public final static NPCCombatDefinitions DEFAULT_DEFINITION = new NPCCombatDefinitions(
			1, -1, -1, -1, 33, -1, -1, 0.2, true, false, false, false, 2);
	private static final String PACKED_PATH = "data/npcs/packedCombatDefinitions.ncd";

	public static void init() {
		if (new File(PACKED_PATH).exists())
			loadPackedNPCCombatDefinitions();
		else
			loadUnpackedNPCCombatDefinitions();
	}

	public static NPCCombatDefinitions getNPCCombatDefinitions(int npcId) {
		NPCCombatDefinitions def = npcCombatDefinitions.get(npcId);
		if (def == null)
			return DEFAULT_DEFINITION;
		return def;
	}

	private static void loadUnpackedNPCCombatDefinitions() {
		int count = 0;
		Logger.log("NPCCombatDefinitionsL", "Packing npc combat definitions...");
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					PACKED_PATH));
			BufferedReader in = new BufferedReader(new FileReader(
					"data/npcs/unpackedCombatDefinitionsList.txt"));
			while (true) {
				String line = in.readLine();
				count++;
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length != 2) {
					in.close();
					out.close();
					throw new RuntimeException(
							"Invalid NPC Combat Definitions line: " + count
									+ ", " + line);
				}
				int npcId = Integer.parseInt(splitedLine[0]);
				String[] splitedLine2 = splitedLine[1].split(" ", 13);
				if (splitedLine2.length != 13) {
					in.close();
					out.close();
					throw new RuntimeException(
							"Invalid NPC Combat Definitions line: " + count
									+ ", " + line);
				}
				int hitpoints = Integer.parseInt(splitedLine2[0]);
				int attackAnim = Integer.parseInt(splitedLine2[1]);
				int defenceAnim = Integer.parseInt(splitedLine2[2]);
				int deathAnim = Integer.parseInt(splitedLine2[3]);
				int respawnDelay = Integer.parseInt(splitedLine2[4]);
				int attackGfx = Integer.parseInt(splitedLine2[5]);
				int attackProjectile = Integer.parseInt(splitedLine2[6]);
				double xp = Double.parseDouble(splitedLine2[7]);

				boolean follow = Boolean.parseBoolean(splitedLine2[8]);
				boolean poisonImmune = Boolean.parseBoolean(splitedLine2[9]);
				boolean poisonous = Boolean.parseBoolean(splitedLine2[10]);
				boolean agressivenessType = Boolean
						.parseBoolean(splitedLine2[11]);
				int agroRatio = Integer.parseInt(splitedLine2[12]);
				out.writeShort(npcId);
				out.writeInt(hitpoints);
				out.writeShort(attackAnim);
				out.writeShort(defenceAnim);
				out.writeShort(deathAnim);
				out.writeInt(respawnDelay);
				out.writeShort(attackGfx);
				out.writeShort(attackProjectile);
				out.writeDouble(xp);
				out.writeByte(follow ? 1 : 0);
				out.writeByte(poisonImmune ? 1 : 0);
				out.writeByte(poisonous ? 1 : 0);
				out.writeByte(agressivenessType ? 1 : 0);
				out.writeByte(agroRatio);
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(
						hitpoints, attackAnim, defenceAnim, deathAnim,
						respawnDelay, attackGfx, attackProjectile, xp, follow,
						poisonImmune, poisonous, agressivenessType, agroRatio));
			}
			in.close();
			out.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void loadPackedNPCCombatDefinitions() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0,
					channel.size());
			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				int hitpoints = buffer.getInt();
				int attackAnim = buffer.getShort() & 0xffff;
				if (attackAnim == 65535)
					attackAnim = -1;
				int defenceAnim = buffer.getShort() & 0xffff;
				if (defenceAnim == 65535)
					defenceAnim = -1;
				int deathAnim = buffer.getShort() & 0xffff;
				if (deathAnim == 65535)
					deathAnim = -1;
				int respawnDelay = buffer.getInt();
				int attackGfx = buffer.getShort() & 0xffff;
				if (attackGfx == 65535)
					attackGfx = -1;
				int attackProjectile = buffer.getShort() & 0xffff;
				if (attackProjectile == 65535)
					attackProjectile = -1;
				double xp = buffer.getDouble();
				boolean follow = buffer.get() == 1;
				boolean poisonImmune = buffer.get() == 1;
				boolean poisonous = buffer.get() == 1;
				boolean agressivenessType = buffer.get() == 1;
				int agroRatio = buffer.get() & 0xff;
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(
						hitpoints, attackAnim, defenceAnim, deathAnim,
						respawnDelay, attackGfx, attackProjectile, xp, follow,
						poisonImmune, poisonous, agressivenessType, agroRatio));
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private NPCCombatDefinitionsL() {

	}
}