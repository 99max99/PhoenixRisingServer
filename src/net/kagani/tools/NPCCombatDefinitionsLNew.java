package net.kagani.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public final class NPCCombatDefinitionsLNew {

	public final static HashMap<Integer, NPCCombatDefinitions> npcCombatDefinitions = new HashMap<Integer, NPCCombatDefinitions>();
	public final static NPCCombatDefinitions DEFAULT_DEFINITION = new NPCCombatDefinitions(
			1, -1, -1, -1, 33, -1, -1, 0.2, true, false, false, false, 2);
	private static final String PACKED_PATH = "data/npcs/packedCombatDefinitions.ncd";

	public static void main(String[] args) throws IOException {
		Cache.init();
		createData();
	}

	public static NPCCombatDefinitions getNPCCombatDefinitions(int npcId) {
		NPCCombatDefinitions def = npcCombatDefinitions.get(npcId);
		if (def == null)
			return DEFAULT_DEFINITION;
		return def;
	}

	private static Map<Integer, String> beasts = new HashMap<Integer, String>();
	private static Map<Integer, Integer> agros = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> combatStyles = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> defenceAnims = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> attackProjectiles = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> attackGFXs = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> respawnDelays = new HashMap<Integer, Integer>();

	private static void loadBeastData() throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(new File(
				"./beasts.txt")));
		while (true) {
			String line = in.readLine();
			if (line == null) {
				in.close();
				break;
			}
			int id = Integer.parseInt(getData(line, "id", 3, -1));
			if (id == 0)
				continue;
			beasts.put(id, line);
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
				int hitpoints = buffer.getShort() & 0xffff;
				int attackAnim = buffer.getShort() & 0xffff;
				if (attackAnim == 65535)
					attackAnim = -1;
				int defenceAnim = buffer.getShort() & 0xffff;
				if (defenceAnim == 65535)
					defenceAnim = -1;
				int deathAnim = buffer.getShort() & 0xffff;
				if (deathAnim == 65535)
					deathAnim = -1;
				int attackDelay = buffer.get() & 0xff;
				int deathDelay = buffer.get() & 0xff;
				int respawnDelay = buffer.getInt();
				int maxHit = buffer.getShort() & 0xffff;
				int attackStyle = buffer.get() & 0xff;
				int attackGfx = buffer.getShort() & 0xffff;
				if (attackGfx == 65535)
					attackGfx = -1;
				int attackProjectile = buffer.getShort() & 0xffff;
				if (attackProjectile == 65535)
					attackProjectile = -1;
				int agressivenessType = buffer.get() & 0xff;
				int agroRatio = buffer.get() & 0xff;
				agros.put(npcId, agroRatio);
				combatStyles.put(npcId, attackStyle);
				defenceAnims.put(npcId, defenceAnim);
				attackProjectiles.put(npcId, attackProjectile);
				attackGFXs.put(npcId, attackGfx);
				respawnDelays.put(npcId, respawnDelay);
				npcCombatDefinitions.put(npcId, new NPCCombatDefinitions(
						hitpoints, attackAnim, defenceAnim, deathAnim,
						respawnDelay, attackGfx, attackProjectile, 0.2, false,
						false, false, agressivenessType == 1, agroRatio));
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static void createData() {
		try {

			loadBeastData();
			//loadPackedNPCCombatDefinitions();
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					"unpackedCombatDefinitionsList.txt")));
			try {
				for (int npcId = 0; npcId < Utils.getNPCDefinitionsSize(); npcId++) {
					NPCDefinitions def = NPCDefinitions
							.getNPCDefinitions(npcId);
					NPCCombatDefinitions cDefs = getNPCCombatDefinitions(npcId);
					String line = beasts.remove(npcId);
					if (line == null && cDefs.getHitpoints() == 1)
						continue;
					writer.write("//" + (line == null ? "OLD" : "NEW") + ": "
							+ def.getName() + ", " + def.combatLevel);
					writer.newLine();
					int hitpoints = line == null ? cDefs.getHitpoints() * 10
							: Integer
									.parseInt(getData(line, "lifepoints", 3, 0));
					double xp = line == null ? cDefs.getXp() : Double
							.parseDouble(getData(line, "xp", 3, 1));
					boolean poisonous = line == null ? cDefs.isPoisonous()
							: Boolean.parseBoolean(getData(line, "poisonous",
									3, -1));
					boolean poisonImmune = false;// TODO manually add this.
					boolean agressive = line == null ? cDefs.isAgressive()
							: Boolean.parseBoolean(getData(line, "aggressive",
									3, -1));
					int attackStyle = 0;// Default.
					if (def.clientScriptData != null) {
						if (def.clientScriptData.containsKey(26)) {
							attackStyle = (int) def.clientScriptData.get(26) - 1;
						}
					}

					int attackAnim = line == null ? cDefs.getAttackEmote()
							: Integer.parseInt(getData(line, "attack", 3, 2));
					if (attackAnim == -1)
						attackAnim = line == null ? -1 : Integer
								.parseInt(getData(line, "range", 3, 2));
					int defenceAnim = defenceAnims.containsKey(npcId) ? defenceAnims
							.get(npcId) : -1;
					int deathAnim = line == null ? cDefs.getDeathEmote()
							: Integer.parseInt(getData(line, "death", 3, 2));
					int attackGFX = attackGFXs.containsKey(npcId) ? attackGFXs
							.get(npcId) : -1;
					int attackProjectile = attackProjectiles.containsKey(npcId) ? attackProjectiles
							.get(npcId) : -1;
					int agroRatio = agros.containsKey(npcId) ? agros.get(npcId)
							: 5;
					int oldAtkStyle = combatStyles.containsKey(npcId) ? combatStyles
							.get(npcId) : 0;
					int respawnDelay = respawnDelays.containsKey(npcId) ? respawnDelays
							.get(npcId) : 60;

					boolean hasRangeAttack = false;
					boolean hasMagicAttack = false;
					if (def.clientScriptData != null) {
						hasRangeAttack = def.clientScriptData.containsKey(643);
						hasMagicAttack = def.clientScriptData.containsKey(965);
					}
					boolean follow = (attackStyle == NPCCombatDefinitions.MELEE
							&& !hasRangeAttack && !hasMagicAttack)
							|| oldAtkStyle == 3 || oldAtkStyle == 4;
					writer.write(npcId + " - " + hitpoints + " " + attackAnim
							+ " " + defenceAnim + " " + deathAnim + " "
							+ (respawnDelay / 2) + " " + attackGFX + " "
							+ attackProjectile + " " + xp + " " + follow + " "
							+ poisonImmune + " " + poisonous + " " + agressive
							+ " " + agroRatio);
					writer.newLine();
					writer.flush();
				}
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getData(String line, String search, int extraSkip,
			int number) {
		int baseIdx = line.indexOf("\"" + search + "\"");
		if (baseIdx == -1)
			return number == 0 ? "100" : number == 1 ? "0.2"
					: number == 2 ? "-1" : "";
		return line
				.substring(baseIdx + search.length() + extraSkip,
						line.indexOf(",", baseIdx)).replace("\"", "")
				.replace("}", "");
	}

	private NPCCombatDefinitionsLNew() {

	}

}
