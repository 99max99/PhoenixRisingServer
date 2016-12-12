package net.kagani.game.player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.kagani.Settings;
import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.player.actions.HomeTeleport;
import net.kagani.game.player.controllers.DungeonController;
import net.kagani.utils.Color;
import net.kagani.utils.Utils;

public class TimersManager implements Serializable {

	/**
	 * @author: Matrix team
	 * @author: Dylan Page
	 */

	private static final long serialVersionUID = -3278352340946510939L;

	private static final int BEAST_MENU_VAR = 4517;

	public static enum RecordKey {

		ARAXXI(0, false, true, 29),

		THE_BARROWS_BROTHERS(1, false, false, 29),

		THE_BARROWS_RISE_OF_THE_FIX(2, false, true, 29),

		CHAOS_ELEMENTAL(3, false, false, 35),

		COMMANDER_ZILYANA(4, true, true, 18),

		CORPOREAL_BEAST(5, false, false, 35),

		DAGANNOTH_KINGS(6, false, false, 31),

		GENERAL_GRAARDOR(7, true, true, 18),

		GIANT_MOLE(8, true, true, 22),

		FIGHT_KILN(9, true, false, 32), // har-aken

		KALPHITE_KING(10, true, false, 4),

		KALPHITE_QUEEN(11, false, false, 4),

		KING_BLACK_DRAGON(12, false, false, 35),

		KREE_ARRA(13, true, true, 18),

		KRIL_TSUTSAROTH(14, true, true, 18),

		LEGIONES(15, true, false, 33),

		NEX(16, true, false, 18),

		QUEEN_BLACK_DRAGON(17, true, false, 24),

		FIGHT_CAVES(18, true, false, 32), // tztok-jad

		VORAGO(19, true, true, 22),

		EVIL_CHICKEN(20, true, true, 22),

		WILDY_WYRM(21, true, true, 22)

		;

		private boolean hasHM, hasFastestKill;
		private int index, lodestoneComponent;

		private RecordKey(int index, boolean hasHM, boolean hasFastestKill,
				int lodestoneComponent) {
			this.index = index;
			this.hasHM = hasHM;
			this.hasFastestKill = hasFastestKill;
			this.lodestoneComponent = lodestoneComponent;
		}

		public int getIndex() {
			return index;
		}
	}

	private Player player;

	private Map<RecordKey, BossRecord> record;
	private int currentTime;

	private static class BossRecord implements Serializable {

		private static final long serialVersionUID = -8783909294743985403L;

		private int totalKills;
		private int totalHmKills;
		private int fastestKill;
	}

	public TimersManager() {
		record = new HashMap<RecordKey, BossRecord>();
		currentTime = -1;
	}

	public void setPlayer(Player player) {
		this.player = player;
		if (record == null) // temporary
			record = new HashMap<RecordKey, BossRecord>();
	}

	public void init() {
		sendBossKills();
		sendSlayerCreatureKills();
	}

	public boolean isActive() {
		return currentTime >= 0;
	}

	public void process() {
		if (isActive() && currentTime < (Integer.MAX_VALUE / 30))
			currentTime++;
	}

	/*
	 * when boss respawns / instance starts depending in type
	 */
	public void sendTimer() {
		if (!isActive()) // starts it if wasnt on
			currentTime = 0;
		sendInterfaces();
	}

	public void sendInterfaces() {
		if (isActive())
			player.getInterfaceManager().sendTimerInterface();
	}

	/*
	 * doesnt save
	 */
	public void removeTimer() {
		removeTimer(null);
	}

	public void removeTimer(RecordKey key, boolean hm) {
		if (!isActive()) // shouldnt be used with this if not a timer boss
			return;
		if (key != null) {
			BossRecord record = getRecord(key);
			if (record == null) {
				record = new BossRecord();
				this.record.put(key, record);
			}
			record.fastestKill = record.fastestKill == 0
					|| record.fastestKill > currentTime ? currentTime
					: record.fastestKill;
			if (key.hasHM && hm)
				record.totalHmKills++;
			else
				record.totalKills++;
			sendBossKills();
		}
		currentTime = -1;
		player.getInterfaceManager().removeTimerInterface();
	}

	/*
	 * if null, doesnt save the time it done, if not null, saves
	 */
	public void removeTimer(RecordKey key) {
		removeTimer(key, false);
	}

	public void increaseKills(RecordKey key, boolean hm) {
		if (isActive() || key.hasFastestKill)
			return;
		BossRecord record = getRecord(key);
		if (record == null) {
			record = new BossRecord();
			this.record.put(key, record);
		}
		if (key.hasHM && hm)
			record.totalHmKills++;
		else
			record.totalKills++;
		sendBossKills();
		int amount = getBossPoints(key);
		player.setBossPoints(player.getBossPoints() + amount
				* (Settings.DOUBLE_BOSS_POINTS ? 2 : 1));
		player.getPackets().sendGameMessage(
				Color.PURPLE,
				"Your received " + amount
						* (Settings.DOUBLE_BOSS_POINTS ? 2 : 1) + " boss "
						+ (amount > 1 ? "points" : "point") + " and now have "
						+ player.getBossPoints() + " "
						+ (player.getBossPoints() > 1 ? "points" : "point")
						+ ".", true);
		if (Utils.random(70) == 2 * amount) {
			player.getInventory().addItemDrop(3062, 1);
			player.getPackets().sendGameMessage(Color.BROWN,
					"You found a strange box.");
		}
	}

	private int getBossPoints(RecordKey key) {
		switch (key.getIndex()) {
		case 19: // vorago
			return 10;
		case 9: // fight kiln
			return 8;
		case 20: // evil chicken
		case 18: // fight caves
		case 2: // rise of the six
			return 5;
		case 21: // wildy wyrm
		case 16: // nex
		case 17: // queen black dragon
		case 1: // barrows brothers
		case 5: // corporeal beast
			return 4;
		case 15: // legions
			return 3;
		case 4: // commander zilyana
		case 7: // general graador
		case 13: // kree arra
		case 14: // kril trutsaroth
			return 2;
		case 10: // kalphite king
		case 12: // king black dragon
		case 11: // kalphite queen
		case 6: // daganooth kings
		case 8: // giant mole
		case 3: // chaos elemental
			return 1;
		}
		return 1;
	}

	public BossRecord getRecord(RecordKey key) {
		return record.get(key);
	}

	public void setBeastMenu(int index) {
		if (index >= 3 || index < 0)
			return;
		player.getVarsManager().sendVar(BEAST_MENU_VAR, index);
		if (index == 0)
			openBossInfo(0);
	}

	// must be sent before
	public void sendBossKills() {

		BossRecord moleRecord = getRecord(RecordKey.GIANT_MOLE);

		player.getVarsManager().sendVarBit(22946,
				moleRecord != null ? moleRecord.totalKills : 0);

		player.getVarsManager().sendVarBit(22947,
				moleRecord != null ? moleRecord.totalHmKills : 0);

		BossRecord kbdRecord = getRecord(RecordKey.KING_BLACK_DRAGON);

		player.getVarsManager().sendVarBit(22948,
				kbdRecord != null ? kbdRecord.totalKills : 0);

		BossRecord barrowsRecord = getRecord(RecordKey.THE_BARROWS_BROTHERS);

		player.getVarsManager().sendVarBit(22949,
				barrowsRecord != null ? barrowsRecord.totalKills : 0);

		BossRecord chaosElementalRecord = getRecord(RecordKey.CHAOS_ELEMENTAL);

		player.getVarsManager().sendVarBit(
				22950,
				chaosElementalRecord != null ? chaosElementalRecord.totalKills
						: 0);

		BossRecord kalphiteQueenRecord = getRecord(RecordKey.KALPHITE_QUEEN);

		player.getVarsManager().sendVarBit(
				22951,
				kalphiteQueenRecord != null ? kalphiteQueenRecord.totalKills
						: 0);

		BossRecord fightCavesRecord = getRecord(RecordKey.FIGHT_CAVES);

		player.getVarsManager().sendVarBit(22952,
				fightCavesRecord != null ? fightCavesRecord.totalKills : 0);

		BossRecord corperealBeastRecord = getRecord(RecordKey.CORPOREAL_BEAST);

		player.getVarsManager().sendVarBit(
				22953,
				corperealBeastRecord != null ? corperealBeastRecord.totalKills
						: 0);

		BossRecord draganothKingsRecord = getRecord(RecordKey.DAGANNOTH_KINGS);

		player.getVarsManager().sendVarBit(
				22954,
				draganothKingsRecord != null ? draganothKingsRecord.totalKills
						: 0);

		BossRecord qbdRecord = getRecord(RecordKey.QUEEN_BLACK_DRAGON);

		player.getVarsManager().sendVarBit(22955,
				qbdRecord != null ? qbdRecord.totalKills : 0);

		BossRecord czRecord = getRecord(RecordKey.COMMANDER_ZILYANA);

		player.getVarsManager().sendVarBit(22956,
				czRecord != null ? czRecord.totalKills : 0);

		player.getVarsManager().sendVarBit(22957,
				czRecord != null ? czRecord.totalHmKills : 0);

		BossRecord generalGraadorRecord = getRecord(RecordKey.GENERAL_GRAARDOR);

		player.getVarsManager().sendVarBit(
				22958,
				generalGraadorRecord != null ? generalGraadorRecord.totalKills
						: 0);

		player.getVarsManager()
				.sendVarBit(
						22959,
						generalGraadorRecord != null ? generalGraadorRecord.totalHmKills
								: 0);

		BossRecord kreeAraRecord = getRecord(RecordKey.KREE_ARRA);

		player.getVarsManager().sendVarBit(22960,
				kreeAraRecord != null ? kreeAraRecord.totalKills : 0);

		player.getVarsManager().sendVarBit(22961,
				kreeAraRecord != null ? kreeAraRecord.totalHmKills : 0);

		BossRecord krilTsutsarothRecord = getRecord(RecordKey.KRIL_TSUTSAROTH);

		player.getVarsManager().sendVarBit(
				22962,
				krilTsutsarothRecord != null ? krilTsutsarothRecord.totalKills
						: 0);

		player.getVarsManager()
				.sendVarBit(
						22963,
						krilTsutsarothRecord != null ? krilTsutsarothRecord.totalHmKills
								: 0);

		BossRecord kilnRecord = getRecord(RecordKey.FIGHT_KILN);

		player.getVarsManager().sendVarBit(22964,
				kilnRecord != null ? kilnRecord.totalKills : 0);

		BossRecord kalphiteKingRecord = getRecord(RecordKey.KALPHITE_KING);

		player.getVarsManager().sendVarBit(22965,
				kalphiteKingRecord != null ? kalphiteKingRecord.totalKills : 0);

		BossRecord legionesRecord = getRecord(RecordKey.LEGIONES);

		player.getVarsManager().sendVarBit(22966,
				legionesRecord != null ? legionesRecord.totalKills : 0);

		BossRecord nexRecord = getRecord(RecordKey.NEX);

		player.getVarsManager().sendVarBit(22967,
				nexRecord != null ? nexRecord.totalKills : 0);

		BossRecord riseOfTheSixRecord = getRecord(RecordKey.THE_BARROWS_RISE_OF_THE_FIX);

		player.getVarsManager().sendVarBit(22968,
				riseOfTheSixRecord != null ? riseOfTheSixRecord.totalKills : 0);

		BossRecord araxxiRecord = getRecord(RecordKey.ARAXXI);
		player.getVarsManager().sendVarBit(22969, araxxiRecord != null ? araxxiRecord.totalKills : 0);

		BossRecord evilChickenRecord = getRecord(RecordKey.EVIL_CHICKEN);

		player.getVarsManager().sendVarBit(22972,
				evilChickenRecord != null ? evilChickenRecord.totalKills : 0);

		BossRecord voragoRecord = getRecord(RecordKey.VORAGO);

		player.getVarsManager().sendVarBit(22970,
				voragoRecord != null ? voragoRecord.totalKills : 0);

		player.getVarsManager().sendVarBit(22971,
				voragoRecord != null ? voragoRecord.totalHmKills : 0);

	}

	public void sendSlayerCreatureKills() {
		player.getVarsManager().sendVarBit(22921, 0);// Terror Dogs
		player.getVarsManager().sendVarBit(22922, 0);// Bloodvields
		player.getVarsManager().sendVarBit(22923, 0);// Warped Tort
		player.getVarsManager().sendVarBit(22924, 0);// Abberant Specters
		player.getVarsManager().sendVarBit(22925, 0);// Dust Devils
		player.getVarsManager().sendVarBit(22926, 0);// Automations
		player.getVarsManager().sendVarBit(22927, 0);// Skeletal Wyverns
		player.getVarsManager().sendVarBit(22928, 0);// Jungle StykeWyrms
		player.getVarsManager().sendVarBit(22929, 0);// Aquanites
		player.getVarsManager().sendVarBit(22930, 0);// Nechraels
		player.getVarsManager().sendVarBit(22931, 0);// Mutated Jadinkos
		player.getVarsManager().sendVarBit(22932, 0);// Ascension Creatures
		player.getVarsManager().sendVarBit(22933, 0);// Polypore Creatures
		player.getVarsManager().sendVarBit(22934, 0);// Spiritual Mages
		player.getVarsManager().sendVarBit(22935, 0);// Abyssal Demons
		player.getVarsManager().sendVarBit(22936, 0);// Dark beasts
		player.getVarsManager().sendVarBit(22937, 0);// Airut
		player.getVarsManager().sendVarBit(22938, 0);// Ice StykeWyrms
		player.getVarsManager().sendVarBit(22939, 0);// Kal'gerion demons
		player.getVarsManager().sendVarBit(22940, 0);// Glacors
		player.getVarsManager().sendVarBit(22941, 0);// Tormented Demons
		player.getVarsManager().sendVarBit(22942, 0);// Gargoyls
		player.getVarsManager().sendVarBit(22943, 0);// Muspha
		player.getVarsManager().sendVarBit(22944, 0);// Nihil
		player.getVarsManager().sendVarBit(22945, 0);// Desert StykeWyrms
	}

	public void teleportToLodestone() {
		if (player.getVarsManager().getValue(BEAST_MENU_VAR) != 0)
			return;
		if (player.getControlerManager().getControler() instanceof DungeonController) {
			player.getPackets().sendGameMessage(
					"You can't teleport to this location while in a dungeon.");
			return;
		}
		RecordKey key = (RecordKey) player.getTemporaryAttributtes().remove(
				Key.BOSS_INFO);
		if (key == null)
			return;
		player.getInterfaceManager().closeMenu();
		player.stopAll();
		HomeTeleport.useLodestone(player, key.lodestoneComponent);
	}

	public void openBossInfo(int index) {
		if (index >= RecordKey.values().length || index < 0
				|| player.getVarsManager().getValue(BEAST_MENU_VAR) != 0)
			return;

		RecordKey key = RecordKey.values()[index];

		player.getTemporaryAttributtes().put(Key.BOSS_INFO, key);

		BossRecord record = getRecord(key);

		player.getPackets().sendCSVarInteger(4486,
				record == null ? 0 : record.totalKills);
		player.getPackets().sendCSVarInteger(4487,
				!key.hasHM ? -1 : record == null ? 0 : record.totalKills);
		player.getPackets().sendCSVarInteger(
				4488,
				!key.hasFastestKill ? -1 : record == null ? 0
						: record.fastestKill * 30);

		GeneralRequirementMap data = GeneralRequirementMap
				.getMap(ClientScriptMap.getMap(9031).getIntValue(index));
		player.getPackets()
				.sendCSVarInteger(
						4484,
						NPCDefinitions.getNPCDefinitions(data.getIntValue(1347)).renderEmote);
		player.getPackets().sendCSVarInteger(4485, data.getId());
	}

	// ignore this. not related to code. only use removetimer and sendtimer
	// methods
	public void showTimer() {
		if (!isActive()) // shouldnt happen unless u open inter manualy
			return;

		player.getPackets().sendExecuteScript(720);
		player.getPackets().sendCSVarInteger(4258, currentTime * 30);
	}
}