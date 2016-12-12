package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.World;
import net.kagani.game.minigames.clanwars.ClanWars;
import net.kagani.game.minigames.clanwars.ClanWars.Rules;
import net.kagani.game.npc.dungeonnering.DungeonBoss;
import net.kagani.game.npc.godwars.zaros.Nex;
import net.kagani.game.npc.others.WildyWyrm;
import net.kagani.game.npc.qbd.QueenBlackDragon;
import net.kagani.game.npc.randomEvent.CombatEventNPC;
import net.kagani.game.player.content.Combat;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Prayer implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -2082861520556582824L;

	private final static int[][] prayerLvls =
	{
		// normal prayer book
		{1, 4, 7, 8, 8, 9, 9, 19, 22, 25, 35, 37, 40, 43, 46, 49, 52, 60, 65, 70, 70, 70},
		// ancient prayer book
		{ 50, 50, 52, 53, 54, 55, 56, 57, 58, 59, 62, 65, 68, 71, 74, 76, 77, 78, 79, 80, 82, 84, 86, 89, 92, 95, 95, 95} };

	private final static int[][][] closePrayers =
	{
	{ // normal prayer book
		{ 0 }, // defence rating, 0
		{ 1, 4, 6}, // strength, 1
		{ 2, 3, 5}, // attack rating, 2
		{17, 19, 20, 21}, //all rating / str / def, 3
		{10}, //summon prayer, 4
		{11, 12, 13}, //protect prayers, 5
		{14, 15, 16}, //other headicon prayers, 6
		{8, 18} //recover hp prayers, 7
		},
		{ // ancient prayer book
		{ 1, 2, 3, 4, 5, 6, 7, 8 }, //saps, 0
		{ 14, 15, 16, 17, 18, 19, 20, 21, 22 }, //leeches, 1
		{25, 26, 27}, //turmoils, 2
		{10}, //summon prayer, 3
		{11, 12, 13}, //protect prayers, 4
		{23, 24} //other headicon prayers, 5
		} };

	private final static int[][] prayerSlotValues =//												TODO V
	{{ 1, 2, 4, 4096, 16384, 8192, 32768, 8, 16, 32, 65536, 64, 128, 256, 512, 1024, 2048, 131072, 524288, 262144, 2097152, 1048576 }
	, {1, 2, 4, 33554432, 8, 16777216, 16, 134217728, 67108864, 32, 64, 128, 256, 512, 1024, 2048, 1048576, 4096, 2097152, 8192, 16384, 32768, 65536, 131072, 262144, 524288, 4194304, 8388608}};
	

	private final static double[][] prayerDrainRate =
	{
	{0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 6, 6, 6, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.4, 0.6, 0.3, 0.3, 0.3},
	{6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 0.6, 1.2, 0.4, 0.4, 0.4, 0.4, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.2, 0.2, 0.2, 0.2} };

	//1.2, 1.2, 1.2, 1.2, 1.2, 0.6, 0.6, 0.6, 3.6, 1.8, 1.8, 0.6, 0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 1.2, 0.6, 0.18, 0.18, 0.24, 0.15, 0.2, 0.18
	
	private transient Player player;
	private transient boolean[][] onPrayers;
	private transient boolean usingQuickPrayer;
	private transient int onPrayersCount;

	private boolean[][] quickPrayers;
	private int prayerpoints;
	private transient int[] leechBonuses;
	private boolean ancientcurses;
	private transient long[] nextDrain;
	private transient boolean boostedLeech;

	public double getCombatRatingMultiplier(int combatType) {
		if(combatType == Combat.MELEE_TYPE)
			return getAttackRatingMultiplier();
		if(combatType == Combat.RANGE_TYPE)
			return getRangeRatingMultiplier();
		return getMageRatingMultiplier();
	}
	
	public double getCombatDamageMultiplier(int combatType) {
		if(combatType == Combat.MELEE_TYPE)
			return getAttackDamageMultiplier();
		if(combatType == Combat.RANGE_TYPE)
			return getRangeDamageMultiplier();
		return getMageDamageMultiplier();
	}
	
	public double getEnemyCombatRatingMultiplier(int combatType) {
		if(combatType == Combat.MELEE_TYPE)
			return getEnemyAttackRatingMultiplier();
		if(combatType == Combat.RANGE_TYPE)
			return getEnemyRangeRatingMultiplier();
		return getEnemyMageRatingMultiplier();
	}
	
	public double getEnemyCombatDamageMultiplier(int combatType) {
		if(combatType == Combat.MELEE_TYPE)
			return getEnemyAttackDamageMultiplier();
		if(combatType == Combat.RANGE_TYPE)
			return getEnemyRangeDamageMultiplier();
		return getEnemyMageDamageMultiplier();
	}
	
	
	public double getMageRatingMultiplier() {
		if (onPrayersCount == 0)
			return 1.0;
		double value = 1.0;
		if(!ancientcurses) {
			if (usingPrayer(0, 5)) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER);
				value += level >= 45 ? 0.06 : level >= 27 ? 0.04 : 0.02;
			}
			else if (usingPrayer(0, 21))
				value += 0.08;
		} else {
			if (usingPrayer(1, 17)) {
				double d = leechBonuses[2];
				value += (d / 100) + 0.02;
			} else if (usingPrayer(1, 27)) 
				value += 0.1;
		}
		return value;
	}
	
	public double getEnemyMageRatingMultiplier() {
		if (onPrayersCount == 0 || !ancientcurses)
			return 1.0;
		double value = 1.0;
		if (usingPrayer(1, 14)) {
			double d = leechBonuses[2];
			value -= (d / 100) + 0.03;
		}else if (usingPrayer(1, 17)) {
			double d = leechBonuses[2];
			value -= (d / 100) + 0.06;
		} else if (usingPrayer(1, 27)) {
			double d = leechBonuses[2];
			value -= (d / 100) + 0.09;
		}
		return value;
	}
	
	public double getMageDamageMultiplier() {
		if (onPrayersCount == 0)
			return 1.0;
		double value = 1.0;
		if(!ancientcurses) {
			if (usingPrayer(0, 6)) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER);
				value += level >= 45 ? 0.06 : level >= 27 ? 0.04 : 0.02;
			}
			else if (usingPrayer(0, 21))
				value += 0.08;
		} else {
			if (usingPrayer(1, 18)) {
				double d = leechBonuses[2];
				value += (d / 100) + 0.02;
			} else if (usingPrayer(1, 27)) 
				value += 0.1;
		}
		return value;
	}
	
	public double getEnemyMageDamageMultiplier() {
		if (onPrayersCount == 0 || !ancientcurses)
			return 1.0;
		double value = 1.0;
		if (usingPrayer(1, 5)) {
			double d = leechBonuses[2];
			value -= (d / 100) + 0.03;
		}else if (usingPrayer(1, 18)) {
			double d = leechBonuses[2];
			value -= (d / 100) + 0.06;
		} else if (usingPrayer(1, 27)) {
			double d = leechBonuses[2];
			value -= (d / 100) + 0.09;
		}
		return value;
	}

	public double getRangeRatingMultiplier() {
		if (onPrayersCount == 0)
			return 1.0;
		double value = 1.0;
		if(!ancientcurses) {
			if (usingPrayer(0, 3)) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER);
				value += level >= 44 ? 0.06 : level >= 26 ? 0.04 : 0.02;
			}
			else if (usingPrayer(0, 20))
				value += 0.08;
		}else{
			if (usingPrayer(1, 15)) {
				double d = leechBonuses[1];
				value += (d / 100) + 0.02;
			} else if (usingPrayer(1, 26)) 
				value += 0.1;
		}
		return value;
	}
	
	public double getEnemyRangeRatingMultiplier() {
		if (onPrayersCount == 0 || !ancientcurses)
			return 1.0;
		double value = 1.0;
		if (usingPrayer(1, 2)) {
			double d = leechBonuses[1];
			value -= (d / 100) + 0.03;
		}else if (usingPrayer(1, 15)) {
			double d = leechBonuses[1];
			value -= (d / 100) + 0.06;
		} else if (usingPrayer(1, 26)) {
			double d = leechBonuses[1];
			value -= (d / 100) + 0.09;
		}
		return value;
	}
	
	public double getRangeDamageMultiplier() {
		if (onPrayersCount == 0)
			return 1.0;
		double value = 1.0;
		if(!ancientcurses) {
			if (usingPrayer(0, 4)) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER);
				value += level >= 44 ? 0.06 : level >= 26 ? 0.04 : 0.02;
			}
			else if (usingPrayer(0, 20))
				value += 0.08;
		}else{
			if (usingPrayer(1, 16)) {
				double d = leechBonuses[1];
				value += (d / 100) + 0.02;
			} else if (usingPrayer(1, 26)) 
				value += 0.1;
		}
		return value;
	}
	
	public double getEnemyRangeDamageMultiplier() {
		if (onPrayersCount == 0 || !ancientcurses)
			return 1.0;
		double value = 1.0;
		if (usingPrayer(1, 3)) {
			double d = leechBonuses[1];
			value -= (d / 100) + 0.03;
		}else if (usingPrayer(1, 16)) {
			double d = leechBonuses[1];
			value -= (d / 100) + 0.06;
		} else if (usingPrayer(1, 26)) {
			double d = leechBonuses[1];
			value -= (d / 100) + 0.09;
		}
		return value;
	}

	public double getAttackRatingMultiplier() {
		if (onPrayersCount == 0)
			return 1.0;
		double value = 1.0;
		if(!ancientcurses) {
			if (usingPrayer(0, 2)) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER);
				value += level >= 34 ? 0.06 : level >= 16 ? 0.04 : 0.02;
			}
			else if (usingPrayer(0, 17))
				value += 0.07;
			else if (usingPrayer(0, 19))
				value += 0.08;
		}else{
			if (usingPrayer(1, 14)) {
				double d = leechBonuses[0];
				value += (d / 100) + 0.02;
			} else if (usingPrayer(1, 25)) 
				value += 0.1;
		}
		return value;
	}

	public double getEnemyAttackRatingMultiplier() {
		if (onPrayersCount == 0 || !ancientcurses)
			return 1.0;
		double value = 1.0;
		if (usingPrayer(1, 1)) {
			double d = leechBonuses[0];
			value -= (d / 100)+ 0.03;
		}else if (usingPrayer(1, 14)) {
			double d = leechBonuses[0];
			value -= (d / 100) + 0.06;
		} else if (usingPrayer(1, 25)) {
			double d = leechBonuses[0];
			value -= (d / 100) + 0.09;
		}
		return value;
	}
	
	public double getAttackDamageMultiplier() {
		if (onPrayersCount == 0)
			return 1.0;
		double value = 1.0;
		if(!ancientcurses) {
			if (usingPrayer(0, 1)) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER);
				value += level >= 31 ? 0.06 : level >= 13 ? 0.04 : 0.02;
			}
			else if (usingPrayer(0, 17))
				value += 0.07;
			else if (usingPrayer(0, 19))
				value += 0.08;
		}else{
			if (usingPrayer(1, 20)) {
				double d = leechBonuses[2];
				value += (d / 100) + 0.02;
			} else if (usingPrayer(1, 25)) 
				value += 0.1;
		}
		return value;
	}
	
	public double getEnemyAttackDamageMultiplier() {
		if (onPrayersCount == 0 || !ancientcurses)
			return 1.0;
		double value = 1.0;
		if (usingPrayer(1, 8)) {
			double d = leechBonuses[0];
			value -= (d / 100) + 0.03;
		}else if (usingPrayer(1, 20)) {
			double d = leechBonuses[0];
			value -= (d / 100) + 0.06;
		} else if (usingPrayer(1, 25)) {
			double d = leechBonuses[0];
			value -= (d / 100) + 0.09;
		}
		return value;
	}

	public double getDefenceRatingMultiplier() {
		if (onPrayersCount == 0)
			return 1.0;
		double value = 1.0;
		if(!ancientcurses) {
			if (usingPrayer(0, 0)) {
				int level = player.getSkills().getLevelForXp(Skills.PRAYER);
				value += level >= 28 ? 0.06 : level >= 10 ? 0.04 : 0.02;
			}
			else if (usingPrayer(0, 17))
				value += 0.07;
			else if (usingPrayer(0, 19) || usingPrayer(0, 20) || usingPrayer(0, 21))
				value += 0.08;
		}else{
			if (usingPrayer(1, 19)) {
				double d = leechBonuses[3];
				value += (d / 100) + 0.02;
			} else if (usingPrayer(1, 25) || usingPrayer(1, 26) || usingPrayer(1, 27)) 
				value += 0.1;
		}
		return value;
	}
	
	public double getEnemyDefenceRatingMultiplier() {
		if (onPrayersCount == 0 || !ancientcurses)
			return 1.0;
		double value = 1.0;
		if (usingPrayer(1, 7)) {
			double d = leechBonuses[3];
			value -= (d / 100) + 0.03;
		}else if (usingPrayer(1, 19)) {
			double d = leechBonuses[3];
			value -= (d / 100) + 0.06;
		} else if (usingPrayer(1, 25) || usingPrayer(1, 26) || usingPrayer(1, 27)) {
			double d = leechBonuses[3];
			value -= (d / 100) + 0.09;
		}
		return value;
	}

	public boolean reachedMax(int bonus) {
		return leechBonuses[bonus] >= 6;
	}

	public void increaseLeechBonus(int bonus) {
		boostedLeech = true;
		if(reachedMax(bonus))
			return;
		leechBonuses[bonus]++;
	/*		adjustStat(0, leechBonuses[bonus]);*/
	}
	
	public void decreaseLeechBonus() {
		for(int i = 0; i < leechBonuses.length; i++)
			if(leechBonuses[i] > 0)
				leechBonuses[i]--;
	}

	//not used atm
	public void adjustStat(int stat, int percentage) {
		//player.getVarsManager().sendVarBitOld(6857 + stat, 30 + percentage);
	}

	private void closePrayers(int prayerId) {
		//not used atm
	}

	public int getPrayerHeadIcon() {
		if (onPrayersCount == 0)
			return -1;
		int value = -1;
		if(!ancientcurses) {
			if (usingPrayer(0, 10))
				value += 8;
			if (usingPrayer(0, 11))
				value += 3;
			else if (usingPrayer(0, 12))
				value += 2;
			else if (usingPrayer(0, 13))
				value += 1;
			else if (usingPrayer(0, 14))
				value += 4;
			else if (usingPrayer(0, 15))
				value += 6;
			else if (usingPrayer(0, 16))
				value += 5;
		}else{
			if (usingPrayer(1, 10)) {
				value += 16;
				if (usingPrayer(1, 12))
					value += 2;
				else if (usingPrayer(1, 11))
					value += 3;
				else if (usingPrayer(1, 13))
					value += 1;
			} else if (usingPrayer(1, 11))
				value += 14;
			else if (usingPrayer(1, 12))
				value += 15;
			else if (usingPrayer(1, 13))
				value += 13;
			else if (usingPrayer(1, 23))
				value += 20;
			else if (usingPrayer(1, 24))
				value += 21;
		}
		//TODO
		return value;
	}

	public void switchSettingQuickPrayer() {
		usingQuickPrayer = !usingQuickPrayer;
		player.getPackets().sendCSVarInteger(181, usingQuickPrayer ? 1 : 0);// activates
		// quick
		// choose
		unlockPrayerBookButtons();
		if (usingQuickPrayer) {// switchs tab to prayer
			player.getInterfaceManager().openGameTab(InterfaceManager.PRAYER_BOOK_TAB);
			recalculatePrayer();
		}
	}

	private transient boolean quickPrayerOn;

	public void switchQuickPrayers() {
		boolean hasQuickPrayers = false;
		for (boolean prayer : quickPrayers[getPrayerBook()]) {
			if (prayer) {
				hasQuickPrayers = true;
				break;
			}
		}
		if (!hasQuickPrayers) {
			player.getPackets().sendGameMessage("You don't have any quick prayers selected.");
			player.getPackets().sendGameMessage("Right-click the prayer button on the action bar to select some.");
			return;
		}
		if (!checkPrayer())
			return;
		if (player.getCurrentFriendsChat() != null) {
			ClanWars war = player.getCurrentFriendsChat().getClanWars();
			if (war != null && war.get(Rules.NO_PRAYER) && (war.getFirstPlayers().contains(player) || war.getSecondPlayers().contains(player))) {
				player.getPackets().sendGameMessage("Prayer has been disabled during this war.");
				return;
			}
		}
		quickPrayerOn = !quickPrayerOn;
		if (hasPrayersOn())
			closeAllPrayers(!quickPrayerOn);
		if (quickPrayerOn) {
			int index = 0;
			for (boolean prayer : quickPrayers[getPrayerBook()]) {
				if (prayer)
					usePrayer(index, false);
				index++;
			}
			player.getPackets().sendCSVarInteger(182, 1);
			recalculatePrayer();
		}
	}

	private void closePrayers(boolean usingQuickPrayer, int[]... prayers) {
		for (int[] prayer : prayers)
			for (int prayerId : prayer)
				if (usingQuickPrayer)
					quickPrayers[getPrayerBook()][prayerId] = false;
				else {
					if (onPrayers[getPrayerBook()][prayerId])
						onPrayersCount--;
					onPrayers[getPrayerBook()][prayerId] = false;
					closePrayers(prayerId);

				}
	}

	public void closeProtectionPrayers() {
		if (getPrayerBook() == 1)
			closePrayers(false, closePrayers[1][3], closePrayers[1][4]);
		else
			closePrayers(false, closePrayers[0][7], closePrayers[0][8]); //TODO fix
		recalculatePrayer();
		player.getAppearence().generateIconsData();
	}

	public void switchPrayer(int prayerId, boolean setQuickPrayer) {
		if (!setQuickPrayer)
			if (!checkPrayer())
				return;
		usePrayer(prayerId, setQuickPrayer);
		recalculatePrayer();
	}
	
	public void delayUsePrayer(final int prayerId, final boolean usingQuickPrayer) {
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				if (player.isDead() || player.isLocked())
					return;
				switchPrayer(prayerId, usingQuickPrayer);
			}
		});
	}
	
	public void delaySwitchQuickPrayers() {
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				if (player.isDead() || player.isLocked())
					return;
				switchQuickPrayers();
			}
		});
	}

	private boolean usePrayer(int prayerId, boolean usingQuickPrayer) {
		if (prayerId < 0 || prayerId >= prayerLvls[getPrayerBook()].length)
			return false;
		if (getPrayerBook() == 1) {
			if (player.getSkills().getLevelForXp(Skills.DEFENCE) < 30) {
				player.getPackets().sendGameMessage("You need a defence level of at least 30 to use this prayer.");
				return false;
			}
		}
		if (player.getSkills().getLevelForXp(5) < prayerLvls[this.getPrayerBook()][prayerId]) {
			player.getPackets().sendGameMessage("You need a prayer level of at least " + prayerLvls[getPrayerBook()][prayerId] + " to use this prayer.");
			return false;
		}
		if (player.getEffectsManager().hasActiveEffect(EffectType.PROTECTION_DISABLED)) {
			if (prayerId >= 11 && prayerId <= 13) {
				player.getPackets().sendGameMessage("You are currently injured and cannot use protection prayers!");
				return false;
			}
		}
		if (player.getCurrentFriendsChat() != null) {
			ClanWars war = player.getCurrentFriendsChat().getClanWars();
			if (war != null && war.get(Rules.NO_PRAYER) && (war.getFirstPlayers().contains(player) || war.getSecondPlayers().contains(player))) {
				player.getPackets().sendGameMessage("Prayer has been disabled during this war.");
				return false;
			}
		}
		if (!usingQuickPrayer) {
			if (onPrayers[getPrayerBook()][prayerId]) {
				onPrayers[getPrayerBook()][prayerId] = false;
				closePrayers(prayerId);
				onPrayersCount--;
				player.getAppearence().generateIconsData();
				return true;
			}
		} else {
			if (quickPrayers[getPrayerBook()][prayerId]) {
				quickPrayers[getPrayerBook()][prayerId] = false;
				return true;
			}
		}
		boolean needHeadIconsGenerate = false;
		if (getPrayerBook() == 0) {
			switch (prayerId) {
			case 0:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][3]);
				break;
			case 1:
			case 4:
			case 6:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][1], closePrayers[getPrayerBook()][3]);
				break;
			case 2:
			case 3:
			case 5:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][2], closePrayers[getPrayerBook()][3]);
				break;
			case 17:
			case 19:
			case 20:
			case 21:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][0], closePrayers[getPrayerBook()][1], closePrayers[getPrayerBook()][2], closePrayers[getPrayerBook()][3]);
				break;
			case 10:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][6]);
				needHeadIconsGenerate = true;
				break;
			case 11:
			case 12:
			case 13:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][5], closePrayers[getPrayerBook()][6]);
				needHeadIconsGenerate = true;
				break;
			case 14:
			case 15:
			case 16:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][4], closePrayers[getPrayerBook()][5], closePrayers[getPrayerBook()][6]);
				needHeadIconsGenerate = true;
				break;
			case 8:
			case 18:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][7]);
				break;
			default:
				break;
			}
		} else {
			switch (prayerId) {
			case 0:
				if (!usingQuickPrayer) {
					player.setNextAnimationNoPriority(new Animation(12567));
					player.setNextGraphics(new Graphics(2213));
				}
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][1], closePrayers[getPrayerBook()][2]);
				break;
			case 9:
				if (!usingQuickPrayer) {
					player.setNextAnimationNoPriority(new Animation(12589));
					player.setNextGraphics(new Graphics(2266));
				}
				break;
				
				/*
				 * 		{ // ancient prayer book, 0
			{ 1, 2, 3, 4, 5, 6, 7, 8 }, //saps, 1
			{ 14, 15, 16, 17, 18, 19, 20, 21, 22 }, //leeches, 2
			{25, 26, 27}, //turmoils, 3
			{10}, //summon prayer, 4
			{11, 12, 13}, //protect prayers, 5
			{23, 24}, //other headicon prayers, 6
				 */
			case 10:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][5]);
				needHeadIconsGenerate = true;
				break;
			case 11:
			case 12:
			case 13:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][4], closePrayers[getPrayerBook()][5]);
				needHeadIconsGenerate = true;
				break;	
			case 23:
			case 24:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][3], closePrayers[getPrayerBook()][4], closePrayers[getPrayerBook()][5]);
				needHeadIconsGenerate = true;
				break;
				
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][0], closePrayers[getPrayerBook()][2]);
				break;
			case 25:
			case 26:
			case 27:
				// stop changing this idiot. it doesnt stop walk on rs
				if (!usingQuickPrayer) {
					player.setNextAnimationNoPriority(new Animation(12565));
					player.setNextGraphics(new Graphics(2226));
				}
				closePrayers(usingQuickPrayer, closePrayers[getPrayerBook()][0], closePrayers[getPrayerBook()][1], closePrayers[getPrayerBook()][2]);
				break;
			default:
				break;
			}
		}
		if (!usingQuickPrayer) {
			onPrayers[getPrayerBook()][prayerId] = true;
			resetDrainPrayer(prayerId);
			onPrayersCount++;
			if (needHeadIconsGenerate)
				player.getAppearence().generateIconsData();
			if(player.getNextAnimation() == null)
				player.setNextAnimationNoPriority(new Animation((player.getCombatDefinitions().isCombatStance() && !player.isLegacyMode()) ? 18010 : 18018));
		} else {
			quickPrayers[getPrayerBook()][prayerId] = true;
		}
		return true;
	}

	public void processPrayer() {
		if (!hasPrayersOn())
			return;
		boostedLeech = false;
	}


	public void processPrayerDrain() {
		if (!hasPrayersOn())
			return;
		int prayerBook = getPrayerBook();
		long currentTime = Utils.currentTimeMillis();
		int drain = 0;
		int prayerPoints = player.getCombatDefinitions().getStats()[CombatDefinitions.PRAYER_B];
		int hatId = player.getEquipment().getHatId();
		if (hatId >= 18744 && hatId <= 18746) //hallos give hidden effect 15pray bonus
			prayerPoints += 15;
		for (int index = 0; index < onPrayers[prayerBook].length; index++) {
			if (onPrayers[prayerBook][index]) {
				long drainTimer = nextDrain[index];
				if (drainTimer != 0 && drainTimer <= currentTime) {
					int rate = (int) ((prayerDrainRate[getPrayerBook()][index] * 1000) + (prayerPoints * 50));
					int passedTime = (int) (currentTime - drainTimer);
					drain++;
					int count = 0;
					while (passedTime >= rate && count++ < 10) {
						drain++;
						passedTime -= rate;
					}
					nextDrain[index] = (currentTime + rate) - passedTime;
				}
			}
		}
		if (drain > 0) {
			drainPrayer(drain);
			if (!checkPrayer())
				closeAllPrayers();
		}
	}

	public void resetDrainPrayer(int index) {
		nextDrain[index] = (long) (Utils.currentTimeMillis() + (prayerDrainRate[getPrayerBook()][index] * 1000) + (player.getCombatDefinitions().getStats()[CombatDefinitions.PRAYER_B] * 50));
	}

	public int getOnPrayersCount() {
		return onPrayersCount;
	}

	public void closeAllPrayers(boolean reset) {
		onPrayers = new boolean[][]
		{ new boolean[22], new boolean[28] };
		onPrayersCount = 0;
		if (reset) {
			player.getPackets().sendCSVarInteger(182, 0);
			leechBonuses = new int[4]; //melee, range, mage, def
			this.quickPrayerOn = false;
		}
		recalculatePrayer();
		player.getAppearence().generateIconsData();
		resetStatAdjustments();
	}

	public void closeAllPrayers() {
		closeAllPrayers(true);
	}

	public boolean hasPrayersOn() {
		return onPrayersCount > 0;
	}

	private boolean checkPrayer() {
		if (prayerpoints <= 0) {
			player.getPackets().sendGameMessage("Please recharge your prayer at the Lumbridge Church.");
			return false;
		}
		return true;
	}

	private int getPrayerBook() {
		return ancientcurses == false ? 0 : 1;
	}

	private void recalculatePrayer() {
		if(recalculatePrayer(ancientcurses, false))
			player.updateBuffs();
		if(usingQuickPrayer)
			recalculatePrayer(ancientcurses, true);
	}
	
	private boolean recalculatePrayer(boolean ancientCurses, boolean usingQuickPrayer) {
		boolean[] book = !usingQuickPrayer ? onPrayers[ancientCurses ? 1 : 0] : quickPrayers[ancientCurses ? 1 : 0];
		int value = 0;
		for (int slot = 0; slot < book.length; slot++) {
			if (book[slot])
				value += prayerSlotValues[ancientCurses ? 1 : 0][slot];
		}
		return player.getVarsManager().sendVar(ancientCurses ? (usingQuickPrayer ? 1768 : 3275) : (usingQuickPrayer ? 1770 : 3272), value);
	}

	private void refresh() {
		refreshBook();
		unlockPrayerBookButtons();
	}

	public void resetStatAdjustments() {
		/*for (int i = 0; i < 5; i++)
			adjustStat(i, 0);*/
	}
	
	public boolean exists(int id) {
		return id >= 0 && id < onPrayers[ancientcurses ? 1 : 0].length;
	}

	public void init() {
		refreshBook();
		resetStatAdjustments();
	}
	
	private void refreshBook() {
		player.getPackets().sendCSVarInteger(181, usingQuickPrayer ? 1 : 0);
		player.getVarsManager().sendVarBit(16789, ancientcurses ? 1 : 0);
	}

	public void unlockPrayerBookButtons() {
		player.getPackets().sendIComponentSettings(1458, 31, 0, 28, 10320902);
		player.getPackets().sendIComponentSettings(1458, 32, 0, 28, 2);
	}

	public void setPrayerBook(boolean ancientcurses) {
		closeAllPrayers();
		this.ancientcurses = ancientcurses;
		refresh();
	}

	public Prayer() {
		quickPrayers = new boolean[][]
		{ new boolean[22], new boolean[28] };
		prayerpoints = 10;
	}

	public void setPlayer(Player player) {
		this.player = player;
		onPrayers = new boolean[][]
		{ new boolean[22], new boolean[28] };
		nextDrain = new long[28];
		leechBonuses = new int[4];
	}

	public boolean isAncientCurses() {
		return ancientcurses;
	}

	public boolean usingPrayer(int book, int prayerId) {
		return onPrayers[book][prayerId];
	}

	public boolean isUsingQuickPrayer() {
		return usingQuickPrayer;
	}

	public boolean isBoostedLeech() {
		return boostedLeech;
	}

	public void setBoostedLeech(boolean boostedLeech) {
		this.boostedLeech = boostedLeech;
	}

	public int getPrayerpoints() {
		return prayerpoints;
	}
	
	public int getMaxPrayerpoints() {
		return player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
	}

	public void setPrayerpoints(int prayerpoints) {
		this.prayerpoints = prayerpoints;
	}

	public void refreshPrayerPoints() {
		if(player.getVarsManager().sendVarBit(16736, prayerpoints * 10))
			player.updateBuffs();
	}

	public void drainPrayerOnHalf() {
		if (prayerpoints > 0) {
			prayerpoints = prayerpoints / 2;
			refreshPrayerPoints();
		}
	}

	public boolean hasFullPrayerpoints() {
		return getPrayerpoints() >= getMaxPrayerpoints();
	}

	public void drainPrayer(int amount) {
		if ((prayerpoints - amount) >= 0)
			prayerpoints -= amount;
		else
			prayerpoints = 0;
		refreshPrayerPoints();
	}

	public void drainPrayer() {
		prayerpoints = 0;
		refreshPrayerPoints();
	}

	public void restorePrayer(int amount) {
		int maxPrayer = getMaxPrayerpoints();
		if ((prayerpoints + amount) <= maxPrayer)
			prayerpoints += amount;
		else
			prayerpoints = maxPrayer;
		refreshPrayerPoints();
	}

	public void reset() {
		closeAllPrayers();
		prayerpoints = getMaxPrayerpoints();
		refreshPrayerPoints();
	}

	public boolean isUsingProtectionPrayer() {
		return isMageProtecting() || isRangeProtecting() || isMeleeProtecting();
	}

	public boolean isProtectingItem() {
		return ancientcurses ? usingPrayer(1, 0) : usingPrayer(0, 9);
	}

	public boolean isMageProtecting() {
		return ancientcurses ? usingPrayer(1, 11) : usingPrayer(0, 11);
	}

	public boolean isRangeProtecting() {
		return ancientcurses ? usingPrayer(1, 12) : usingPrayer(0, 12);
	}

	public boolean isMeleeProtecting() {
		return ancientcurses ? usingPrayer(1, 13) : usingPrayer(0, 13);
	}

	public boolean canReflect(Entity entity) {
		if (entity instanceof DungeonBoss || entity instanceof WildyWyrm || entity instanceof CombatEventNPC || entity instanceof Nex || entity instanceof QueenBlackDragon) {
			player.getPackets().sendGameMessage("You are unable to reflect damage back to this creature.", true);
			return false;
		}
		return entity.getMaxHitpoints() > 1;
	}

	private static final int CURSE_PROC_CHANCE = 5;
	
	
	private void leech(Entity target, int emote, int gfx, int projectile, int targetGfx) {
		player.setNextAnimation(new Animation(emote));
		if(gfx != -1)
			player.setNextGraphics(new Graphics(gfx));
		target.setNextGraphics(new Graphics(targetGfx, World.sendProjectile(player, target, projectile, 35, 35, 20, 5, 0, 0).getEndTime() / 10, 0));
		
	}
	
	public void handleHitPrayers(final Entity target, Hit hit) {
		if (!hasPrayersOn() || hit.getDamage() == 0)
			return;
		if (!ancientcurses) { //updated to rs3
			if (usingPrayer(0, 16) && target instanceof Player)
				((Player) target).getPrayer().drainPrayer(hit.getDamage() / 150);
		} else if (!boostedLeech) {
			//turmoil
			if (((usingPrayer(1, 25) && hit.getLook() == HitLook.MELEE_DAMAGE)
					|| (usingPrayer(1, 26) && hit.getLook() == HitLook.RANGE_DAMAGE)
					|| (usingPrayer(1, 27) && hit.getLook() == HitLook.MAGIC_DAMAGE)) && Utils.random(CURSE_PROC_CHANCE) == 0) { //turmoil
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's "+(usingPrayer(1, 25) ? "melee" : usingPrayer(1, 26) ? "ranged" : "magic")+" attack, strength and defence.", true);
				increaseLeechBonus(usingPrayer(1, 25) ? 0 : usingPrayer(1, 26) ? 1 : 2);
				increaseLeechBonus(3);
				return;
			}
			//saps
			if ((usingPrayer(1, 1) || usingPrayer(1, 5)) && hit.getLook() == HitLook.MELEE_DAMAGE && Utils.random(CURSE_PROC_CHANCE*2) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's melee "+ (usingPrayer(1, 1) ? "attack" : "strength") +".", true);
				increaseLeechBonus(0);
				leech(target, 12569, 2214, 2215, 2216);
			}
			if ((usingPrayer(1, 2) || usingPrayer(1, 3)) && hit.getLook() == HitLook.RANGE_DAMAGE && Utils.random(CURSE_PROC_CHANCE*2) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's ranged "+ (usingPrayer(1, 2) ? "attack" : "strength") +".", true);
				increaseLeechBonus(1);
				leech(target, 12569, 2217, 2218, 2219);
			}
			if ((usingPrayer(1, 4) || usingPrayer(1, 5)) && hit.getLook() == HitLook.MAGIC_DAMAGE && Utils.random(CURSE_PROC_CHANCE*2) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's magic "+ (usingPrayer(1, 4) ? "attack" : "strength") +".", true);
				increaseLeechBonus(2);
				leech(target, 12569, 2220, 2221, 2222);
			}
			if (usingPrayer(1, 7) && Utils.random(CURSE_PROC_CHANCE*2) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's defence.", true);
				increaseLeechBonus(3);
				leech(target, 12569, 2223, 2224, 2225);
			}
			if (usingPrayer(1, 6) && target instanceof Player && Utils.random(CURSE_PROC_CHANCE*3) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's special attack.", true);
				((Player)target).getCombatDefinitions().increaseSpecialAttack(-10);
				boostedLeech = true;
				leech(target, 12569, 2223, 2224, 2225);
			}
			//curses
			if ((usingPrayer(1, 14) || usingPrayer(1, 20)) && hit.getLook() == HitLook.MELEE_DAMAGE && Utils.random(CURSE_PROC_CHANCE*2) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's melee "+ (usingPrayer(1, 14) ? "attack" : "strength") +".", true);
				increaseLeechBonus(0);
				leech(target, 12575, -1, 2231, 2232);
			}
			if ((usingPrayer(1, 15) || usingPrayer(1, 16)) && hit.getLook() == HitLook.RANGE_DAMAGE && Utils.random(CURSE_PROC_CHANCE*2) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's ranged "+ (usingPrayer(1, 15) ? "attack" : "strength") +".", true);
				increaseLeechBonus(1);
				leech(target, 12575, -1, 2236, 2238);
			}
			if ((usingPrayer(1, 17) || usingPrayer(1, 18)) && hit.getLook() == HitLook.MAGIC_DAMAGE && Utils.random(CURSE_PROC_CHANCE*2) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's magic "+ (usingPrayer(1, 17) ? "attack" : "strength") +".", true);
				increaseLeechBonus(2);
				leech(target, 12575, -1, 2240, 2242);
			}
			if (usingPrayer(1, 19) && Utils.random(CURSE_PROC_CHANCE*2) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's defence.", true);
				increaseLeechBonus(3);
				leech(target, 12575, -1, 2244, 2246);
			}
			if (usingPrayer(1, 21) && target instanceof Player && Utils.random(CURSE_PROC_CHANCE*3) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00>Your curse drains the enemy's run energy.", true);
				((Player)target).setRunEnergy(((Player)target).getRunEnergy() > 10 ? ((Player)target).getRunEnergy() - 10 : 0);
				player.setRunEnergy(player.getRunEnergy() > 90 ? 100 : player.getRunEnergy() + 10);
				boostedLeech = true;
				leech(target, 12575, -1, 2256, 2258);
			}
			if (usingPrayer(1, 22) && target instanceof Player && Utils.random(CURSE_PROC_CHANCE*3) == 0) {
				player.getPackets().sendGameMessage("<col=00FF00Your curse drains the enemy's special attack.", true);
				((Player)target).getCombatDefinitions().increaseSpecialAttack(-10);
				player.getCombatDefinitions().increaseSpecialAttack(10);
				boostedLeech = true;
				leech(target, 12575, -1, 2252, 2254);
			}
		}

	}
}