package net.kagani.game.player;

import java.io.Serializable;
import java.util.Arrays;

import net.kagani.Settings;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.npc.randomEvent.CombatEventNPC;
import net.kagani.utils.Utils;

public final class Skills implements Serializable {

	private static final long serialVersionUID = -7086829989489745985L;

	public static final double MAXIMUM_EXP = 200000000;
	public static final double RANDOM_EVENT_EXP = 50000;

	private boolean[] enabledSkillsTargets;
	private boolean[] skillsTargetsUsingLevelMode;
	private int[] skillsTargetsValues;

	public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6,
			COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13,
			MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20,
			CONSTRUCTION = 22, HUNTER = 21, SUMMONING = 23, DUNGEONEERING = 24, DIVINATION = 25;

	public static final String[] SKILL_NAME = { "Attack", "Defence", "Strength", "Constitution", "Ranged", "Prayer",
			"Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
			"Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction",
			"Summoning", "Dungeoneering", "Divination" };

	public static int[] FIXED_SLOTS = { ATTACK, HITPOINTS, MINING, STRENGTH, AGILITY, SMITHING, DEFENCE, HERBLORE,
			FISHING, RANGE, THIEVING, COOKING, PRAYER, CRAFTING, FIREMAKING, MAGIC, FLETCHING, WOODCUTTING,
			RUNECRAFTING, SLAYER, FARMING, CONSTRUCTION, HUNTER, SUMMONING, DUNGEONEERING, DIVINATION };

	private short level[];
	private double xp[];
	private double[] xpTracks;
	private boolean[] trackSkills;
	private byte[] trackSkillsIds;
	private boolean xpDisplay, xpPopup;
	private int elapsedBonusMinutes;
	private double trackXPREvent;
	private transient double xpBonusTrack;

	private transient int currentCounter;
	private transient Player player;

	public void passLevels(Player p) {
		this.level = p.getSkills().level;
		this.xp = p.getSkills().xp;
	}

	public Skills() {
		level = new short[26];
		xp = new double[26];
		for (int i = 0; i < level.length; i++) {
			level[i] = 1;
			xp[i] = 0;
		}
		level[3] = 10;
		xp[3] = 1184;
		level[HERBLORE] = 3;
		xp[HERBLORE] = 250;
		xpPopup = true;
		xpTracks = new double[3];
		trackSkills = new boolean[3];
		trackSkillsIds = new byte[3];
		trackSkills[0] = true;
		if (enabledSkillsTargets == null)
			enabledSkillsTargets = new boolean[26];
		if (skillsTargetsUsingLevelMode == null)
			skillsTargetsUsingLevelMode = new boolean[26];
		if (skillsTargetsValues == null)
			skillsTargetsValues = new int[26];
		for (int i = 0; i < trackSkillsIds.length; i++)
			trackSkillsIds[i] = 30;

	}

	public void sendXPDisplay() {
		for (int i = 0; i < trackSkills.length; i++) {
			player.getVarsManager().sendVarBit(229 + i, trackSkills[i] ? 1 : 0);
			player.getVarsManager().sendVarBit(225 + i, trackSkillsIds[i] + 1);
			refreshCounterXp(i);
		}
		refreshXpPopup();
		refreshXPDisplay();
		refreshCurrentCounter();
	}

	public void refreshCurrentCounter() {
		player.getVarsManager().sendVar(96, currentCounter + 1);
	}

	public void setCurrentCounter(int counter) {
		if (counter != currentCounter) {
			currentCounter = counter;
			refreshCurrentCounter();
		}
	}

	public void switchTrackCounter() {
		trackSkills[currentCounter] = !trackSkills[currentCounter];
		player.getVarsManager().sendVarBit(229 + currentCounter, trackSkills[currentCounter] ? 1 : 0);
	}

	public void resetCounterXP() {
		xpTracks[currentCounter] = 0;
		refreshCounterXp(currentCounter);
	}

	public void setCounterSkill(int skill) {
		xpTracks[currentCounter] = 0;
		trackSkillsIds[currentCounter] = (byte) skill;
		player.getVarsManager().sendVarBit(225 + currentCounter, trackSkillsIds[currentCounter] + 1);
		refreshCounterXp(currentCounter);
	}

	public void refreshCounterXp(int counter) {
		player.getVarsManager().sendVar(91 + counter, (int) (xpTracks[counter] * 10));
	}

	public void handleSetupXPCounter(int componentId) {
		if (componentId >= 13 && componentId <= 31)
			setCurrentCounter((componentId - 13) / 8);
		else if (componentId == 36)
			switchTrackCounter();
		else if (componentId == 70)
			resetCounterXP();
		else if (componentId == 2)
			switchXPDisplay();
		else if (componentId >= 41 && componentId <= 68)
			if (componentId == 43)
				setCounterSkill(4);
			else if (componentId == 44)
				setCounterSkill(2);
			else if (componentId == 45)
				setCounterSkill(3);
			else if (componentId == 52)
				setCounterSkill(18);
			else if (componentId == 59)
				setCounterSkill(11);
			else
				setCounterSkill(componentId >= 67 ? componentId - 38 : componentId - 41);
	}

	public void unlockSkills(boolean menu) {
		player.getPackets().sendIComponentSettings(menu ? 320 : 1466, menu ? 13 : 11, 0, 26, 30);
	}

	public void refreshXpPopup() {
		player.getVarsManager().sendVarBit(228, xpPopup ? 0 : 1);
	}

	public void refreshXPDisplay() {
		player.getVarsManager().sendVarBit(19964, xpDisplay ? 0 : 1);
	}

	public void switchXPDisplay() {
		xpDisplay = !xpDisplay;
		refreshXPDisplay();
	}

	public void switchXPPopup() {
		xpPopup = !xpPopup;
		refreshXpPopup();
		player.getPackets().sendGameMessage("XP pop-ups are now " + (xpPopup ? "en" : "dis") + "abled.");
	}

	public void restoreSkills() {
		for (int skill = 0; skill < level.length; skill++) {
			level[skill] = (short) getLevelForXp(skill);
			refresh(skill);
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
		// temporary
		if (xpTracks == null) {
			xpPopup = true;
			xpTracks = new double[3];
			trackSkills = new boolean[3];
			trackSkillsIds = new byte[3];
			trackSkills[0] = true;
			for (int i = 0; i < trackSkillsIds.length; i++)
				trackSkillsIds[i] = 30;
		}

		if (xp.length != 26) {
			xp = Arrays.copyOf(xp, 26);
			level = Arrays.copyOf(level, 26);
			level[DIVINATION] = 1;
		}
	}

	public short[] getLevels() {
		return level;
	}

	public double[] getXp() {
		return xp;
	}

	public int getLevel(int skill) {
		return level[skill];
	}

	public double getXp(int skill) {
		return xp[skill];
	}

	public boolean hasRequiriments(int... skills) {
		for (int i = 0; i < skills.length; i += 2) {
			int skillId = skills[i];
			if (skillId == DUNGEONEERING)
				continue;
			int skillLevel = skills[i + 1];
			if (getLevelForXp(skillId) < skillLevel)
				return false;

		}
		return true;
	}

	public int getTotalLevel() {
		int level = 0;
		for (int i = 0; i < xp.length; i++)
			level += getLevelForXp(i);
		return level;
	}

	public long getTotalXp() {
		long xp = 0;
		for (int i = 0; i < this.xp.length; i++)
			xp += this.xp[i];
		return xp;
	}

	public int getCombatLevel() {
		int attack = getLevelForXp(0);
		int defence = getLevelForXp(1);
		int strength = getLevelForXp(2);
		int hitpoints = getLevelForXp(3);
		int prayer = getLevelForXp(5);
		int ranged = getLevelForXp(4);
		int magic = getLevelForXp(6);
		double combatLevel = (defence + hitpoints + Math.floor(prayer / 2)) * 0.25;
		double warrior = (attack + strength) * 0.325;
		double ranger = ranged * 0.4875;
		double mage = magic * 0.4875;
		combatLevel += Math.max(warrior, Math.max(ranger, mage));
		return (int) combatLevel;
	}

	public int getCombatLevelWithSummoning() {
		return getCombatLevel() + getSummoningCombatLevel();
	}

	public int getSummoningCombatLevel() {
		double summon = Math.floor(getLevelForXp(Skills.SUMMONING) / 2) * 0.25;
		return (int) summon;
	}

	public void set(int skill, int newLevel) {
		level[skill] = (short) newLevel;
		refresh(skill);
	}

	public int drainLevel(int skill, int drain) {
		int drainLeft = drain - level[skill];
		if (drainLeft < 0) {
			drainLeft = 0;
		}
		level[skill] -= drain;
		if (level[skill] < 0) {
			level[skill] = 0;
		}
		refresh(skill);
		return drainLeft;
	}

	public void drainSummoning(int amt) {
		int level = getLevel(Skills.SUMMONING);
		if (level == 0)
			return;
		set(Skills.SUMMONING, amt > level ? 0 : level - amt);
	}

	public static int getXPForLevel(int level) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= level; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			if (lvl >= level) {
				return output;
			}
			output = (int) Math.floor(points / 4);
		}
		return 0;
	}

	public int getLevelForXp(int skill) {
		return getLevelForXp(xp[skill], skill == DUNGEONEERING ? 120 : 99);
	}

	public static int getLevelForXp(double exp, int max) {
		int points = 0;
		int output = 0;
		for (int lvl = 1; lvl <= max; lvl++) {
			points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
			output = (int) Math.floor(points / 4);
			if ((output - 1) >= exp) {
				return lvl;
			}
		}
		return max;
	}

	public int getHighestSkillLevel() {
		int maxLevel = 1;
		for (int skill = 0; skill < level.length; skill++) {
			int level = getLevelForXp(skill);
			if (level > maxLevel)
				maxLevel = level;
		}
		return maxLevel;
	}

	public void init() {
		for (int skill = 0; skill < level.length; skill++)
			refresh(skill);
		sendXPDisplay();
		if (!Settings.DOUBLE_XP)
			elapsedBonusMinutes = 0;
		else
			refreshXpBonus();
	}

	private double getXpBonusMultiplier() {
		if (elapsedBonusMinutes >= 600)
			return 1.1;
		double hours = elapsedBonusMinutes / 60;
		return Math.pow((hours - 10) / 7.5, 2) + 1.1;
	}

	@SuppressWarnings("deprecation")
	public void refreshBonusXp() {
		player.getVarsManager().sendVar(1878, (int) (xpBonusTrack * 10));
	}

	public void refreshXpBonus() {
		player.getVarsManager().sendVarBit(7232, 1);
		refreshElapsedBonusMinutes();
		refreshBonusXp();
	}

	public void increaseElapsedBonusMinues() {
		elapsedBonusMinutes++;
		refreshElapsedBonusMinutes();
	}

	public void refreshElapsedBonusMinutes() {
		player.getVarsManager().sendVarBit(7233, elapsedBonusMinutes);
	}

	public void refresh(int skill) {
		player.getPackets().sendSkillLevel(skill);
	}

	/*
	 * if(componentId == 33) setCounterSkill(4); else if(componentId == 34)
	 * setCounterSkill(2); else if(componentId == 35) setCounterSkill(3); else
	 * if(componentId == 42) setCounterSkill(18); else if(componentId == 49)
	 * setCounterSkill(11);
	 */

	public static int getCounterSkill(int skill) {
		switch (skill) {
		case ATTACK:
			return 0;
		case STRENGTH:
			return 1;
		case DEFENCE:
			return 4;
		case RANGE:
			return 2;
		case HITPOINTS:
			return 5;
		case PRAYER:
			return 6;
		case AGILITY:
			return 7;
		case HERBLORE:
			return 8;
		case THIEVING:
			return 9;
		case CRAFTING:
			return 10;
		case MINING:
			return 12;
		case SMITHING:
			return 13;
		case FISHING:
			return 14;
		case COOKING:
			return 15;
		case FIREMAKING:
			return 16;
		case WOODCUTTING:
			return 17;
		case SLAYER:
			return 19;
		case FARMING:
			return 20;
		case CONSTRUCTION:
			return 21;
		case HUNTER:
			return 22;
		case SUMMONING:
			return 23;
		case DUNGEONEERING:
			return 24;
		case DIVINATION:
			return 25;
		case MAGIC:
			return 3;
		case FLETCHING:
			return 18;
		case RUNECRAFTING:
			return 11;
		default:
			return -1;
		}

	}

	public double addXp(int skill, double exp) {
		return addXp(skill, exp, false);
	}

	@SuppressWarnings("deprecation")
	public double addXp(int skill, double exp, boolean forceRSXp) {
		player.getControlerManager().trackXP(skill, (int) exp);
		boolean combatSkill = skill >= ATTACK && skill <= MAGIC;
		if (player.isXpLocked())
			return 0;
		if (player.getAuraManager().usingWisdom())
			exp *= 1.025;
		if (skill == SLAYER)
			exp = exp /= 2.475;
		if (skill == SLAYER)
			exp = exp * 0.7;
		if (skill == PRAYER)
			exp = exp / 10;
		xp[skill] += exp;
		if (!forceRSXp && (!player.isCanPvp() || !combatSkill)) {
			if (exp < RANDOM_EVENT_EXP && CombatEventNPC.canRandomEvent(player)) {
				trackXPREvent += exp;
				if (trackXPREvent >= RANDOM_EVENT_EXP) {
					trackXPREvent = 0;
				}
			}
			if (Settings.DOUBLE_XP) {
				if (player.isAnIronMan())
					exp *= Settings.IRON_XP_RATE * 1.5;
				else if (player.isBronzeMember())
					exp *= Settings.XP_RATE * 2.10;
				else if (player.isSilverMember())
					exp *= Settings.XP_RATE * 2.20;
				else if (player.isGoldMember())
					exp *= Settings.XP_RATE * 2.3;
				else if (player.isPlatinumMember())
					exp *= Settings.XP_RATE * 2.4;
				else if (player.isDiamondMember())
					exp *= Settings.XP_RATE * 2.5;
				else {
					exp *= Settings.XP_RATE * 2;
				}
			} else {
				if (player.isAnIronMan())
					exp *= Settings.IRON_XP_RATE;
				else if (player.isBronzeMember())
					exp *= Settings.XP_RATE * 1.10;
				else if (player.isSilverMember())
					exp *= Settings.XP_RATE * 1.20;
				else if (player.isGoldMember())
					exp *= Settings.XP_RATE * 1.30;
				else if (player.isPlatinumMember())
					exp *= Settings.XP_RATE * 1.40;
				else if (player.isDiamondMember())
					exp *= Settings.XP_RATE * 1.50;
				else {
					exp *= Settings.XP_RATE;
				}
			}
		}
		if (player.inMemberZone() && player.isAMember())
			exp *= 1.05;
		if (Settings.DOUBLE_XP)
			player.getVarsManager().sendVar(2044, Settings.XP_RATE * 2);
		int oldLevel = getLevelForXp(skill);
		int oldCombatLevel = getCombatLevelWithSummoning();
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29
								&& (skill == Skills.ATTACK || skill == Skills.DEFENCE || skill == Skills.STRENGTH
										|| skill == Skills.MAGIC || skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}
		if (xp[skill] == MAXIMUM_EXP)
			return 0;
		double oldXp = xp[skill];

		xp[skill] += exp;
		if (xp[skill] > MAXIMUM_EXP)
			xp[skill] = MAXIMUM_EXP;
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			// sendLevelUpInterface(skill);
			player.getDialogueManager().startDialogue("LevelUp", skill);
			player.getAppearence().generateAppearenceData();
			if (combatSkill) {
				if (oldCombatLevel != getCombatLevelWithSummoning())
					sendCombatLevel();
				if (skill == HITPOINTS)
					player.heal(levelDiff * 100);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
			player.getQuestManager().checkCompleted();
			player.getControlerManager().trackLevelUp(skill, level[skill]);
			set(skill, getLevelForXp(skill));
		}
		sendNews(skill, newLevel > oldLevel, oldCombatLevel, oldXp);
		refresh(skill);
		if (player.getClanManager() != null) {
			if (player.getRights() < 2)
				player.getClanManager().addXP((int) exp);
		}
		return exp;
	}

	public void sendCombatLevel() {
		player.getPackets().sendCSVarInteger(1000, getCombatLevelWithSummoning());
	}

	public void sendNews(int skill, boolean levelUP, int combatLevelBefore, double oldXp) {
		boolean combatSkill = skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC);
		if (combatSkill)
			return;
		if (!levelUP) {
			if (xp[skill] > 50000000) { // 50m
				if (getLevelForXp(oldXp, 120) != 120 && getLevelForXp(xp[skill], 120) == 120)
					World.sendNews(player, player.getDisplayName() + " has achieved true skill mastery in the "
							+ Skills.SKILL_NAME[skill] + " skill.", World.WORLD_NEWS);
				else {
					int next = (int) (xp[skill] / 50000000);
					int xpachievement = next * 50000000;
					if (oldXp < xpachievement && xp[skill] >= xpachievement)
						World.sendNews(player, player.getDisplayName() + " has achieved " + (next * 50) + "m "
								+ Skills.SKILL_NAME[skill] + " xp.", World.SERVER_NEWS);
				}
			}
		} else {
			if (combatSkill && combatLevelBefore != 138 && getCombatLevelWithSummoning() == 138) {
				World.sendNews(player, player.getDisplayName() + " has achieved level 138 combat.", World.SERVER_NEWS);
				return;
			}
			int level = getLevelForXp(skill);
			millestone: if (level % 10 == 0 || level == 99) {
				for (int i = 0; i < Skills.SKILL_NAME.length; i++) {
					if (player.getSkills().getLevelForXp(i) < level)
						break millestone;
				}
				World.sendNews(player,
						player.getDisplayName() + " has just achieved at least level " + level + " in all skills!",
						level == 99 ? World.WORLD_NEWS : World.FRIEND_NEWS);
				return;
			}
			if (level == 99)
				World.sendNews(player,
						player.getDisplayName() + " has achieved " + level + " " + Skills.SKILL_NAME[skill] + ".",
						World.SERVER_NEWS);
			else if (level == 120)
				World.sendNews(player, player.getDisplayName() + " has achieved true skill mastery in the "
						+ Skills.SKILL_NAME[skill] + " skill.", World.WORLD_NEWS);
		}
	}

	public static final int[] LEVEL_MUSIC = { 30, 38, 66, 48, 58, 56, 52, 34, 70, 44, 42, 40, 36, 64, 54, 46, 28, 68,
			61, 10, 60, 50, 32, 301, 417, -1 };

	private void sendLevelUpInterface(int skill) {
		int iconValue = getIconValue(skill);
		player.getPackets().sendCSVarInteger(1756, iconValue);
		player.getInterfaceManager().setWindowInterface(InterfaceManager.LEVEL_UP_COMPONENT_ID, 1216);
		int level = player.getSkills().getLevelForXp(skill);
		player.getTemporaryAttributtes().put("leveledUp", skill);
		player.getTemporaryAttributtes().put("leveledUp[" + skill + "]", Boolean.TRUE);
		player.setNextGraphics(new Graphics(199));
		if (level == 99 || level == 120)
			player.setNextGraphics(new Graphics(1765));
		String name = Skills.SKILL_NAME[skill];
		player.getPackets().sendGameMessage("You've just advanced a" + (name.startsWith("A") ? "n" : "") + " " + name
				+ " level! You have reached level " + level + ".", true);
		// player.getVarsManager().sendVarBit(3292, iconValue);
		switchFlash(player, skill, true);
		player.getPackets().sendMusicEffectOld(LEVEL_MUSIC[skill]);
	}

	public static int getIconValue(int skill) {
		if (skill == Skills.ATTACK)
			return 1;
		if (skill == Skills.STRENGTH)
			return 2;
		if (skill == Skills.RANGE)
			return 3;
		if (skill == Skills.MAGIC)
			return 4;
		if (skill == Skills.DEFENCE)
			return 5;
		if (skill == Skills.HITPOINTS)
			return 6;
		if (skill == Skills.PRAYER)
			return 7;
		if (skill == Skills.AGILITY)
			return 8;
		if (skill == Skills.HERBLORE)
			return 9;
		if (skill == Skills.THIEVING)
			return 10;
		if (skill == Skills.CRAFTING)
			return 11;
		if (skill == Skills.RUNECRAFTING)
			return 12;
		if (skill == Skills.MINING)
			return 13;
		if (skill == Skills.SMITHING)
			return 14;
		if (skill == Skills.FISHING)
			return 15;
		if (skill == Skills.COOKING)
			return 16;
		if (skill == Skills.FIREMAKING)
			return 17;
		if (skill == Skills.WOODCUTTING)
			return 18;
		if (skill == Skills.FLETCHING)
			return 19;
		if (skill == Skills.SLAYER)
			return 20;
		if (skill == Skills.FARMING)
			return 21;
		if (skill == Skills.CONSTRUCTION)
			return 22;
		if (skill == Skills.HUNTER)
			return 23;
		if (skill == Skills.SUMMONING)
			return 24;
		else if (skill == Skills.DUNGEONEERING)
			return 25;
		else if (skill == Skills.DIVINATION)
			return 26;
		return 26;
	}

	public static void switchFlash(Player player, int skill, boolean on) {
		int id = 0;
		if (skill == Skills.ATTACK)
			id = 3267;
		else if (skill == Skills.STRENGTH)
			id = 3268;
		else if (skill == Skills.DEFENCE)
			id = 3269;
		else if (skill == Skills.RANGE)
			id = 3270;
		else if (skill == Skills.PRAYER)
			id = 3271;
		else if (skill == Skills.MAGIC)
			id = 3272;
		else if (skill == Skills.HITPOINTS)
			id = 3273;
		else if (skill == Skills.AGILITY)
			id = 3274;
		else if (skill == Skills.HERBLORE)
			id = 3275;
		else if (skill == Skills.THIEVING)
			id = 3276;
		else if (skill == Skills.CRAFTING)
			id = 3277;
		else if (skill == Skills.FLETCHING)
			id = 3278;
		else if (skill == Skills.MINING)
			id = 3279;
		else if (skill == Skills.SMITHING)
			id = 3280;
		else if (skill == Skills.FISHING)
			id = 3281;
		else if (skill == Skills.COOKING)
			id = 3282;
		else if (skill == Skills.FIREMAKING)
			id = 3283;
		else if (skill == Skills.WOODCUTTING)
			id = 3284;
		else if (skill == Skills.RUNECRAFTING)
			id = 3285;
		else if (skill == Skills.SLAYER)
			id = 3286;
		else if (skill == Skills.FARMING)
			id = 3287;
		else if (skill == Skills.CONSTRUCTION)
			id = 3288;
		else if (skill == Skills.HUNTER)
			id = 3289;
		else if (skill == Skills.SUMMONING)
			id = 3290;
		else if (skill == Skills.DUNGEONEERING)
			id = 3291;
		else if (skill == Skills.DIVINATION)
			id = 20114;
		player.getVarsManager().sendVarBit(id, on ? 1 : 0);
	}

	public double addXpStore(int skill, double exp) {
		player.getControlerManager().trackXP(skill, (int) exp);
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29
								&& (skill == Skills.ATTACK || skill == Skills.DEFENCE || skill == Skills.STRENGTH
										|| skill == Skills.MAGIC || skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}

		if (xp[skill] > MAXIMUM_EXP)
			xp[skill] = MAXIMUM_EXP;
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getAppearence().generateAppearenceData();
				if (skill == HITPOINTS)
					player.heal(levelDiff * 100);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
			player.getQuestManager().checkCompleted();
		}
		refresh(skill);
		return exp;
	}

	public double addXpLamp(int skill, double exp) {
		player.getControlerManager().trackXP(skill, (int) exp);
		if (player.isXpLocked())
			return 0;
		exp *= Settings.getLampXpRate();
		int oldLevel = getLevelForXp(skill);
		xp[skill] += exp;
		for (int i = 0; i < trackSkills.length; i++) {
			if (trackSkills[i]) {
				if (trackSkillsIds[i] == 30
						|| (trackSkillsIds[i] == 29
								&& (skill == Skills.ATTACK || skill == Skills.DEFENCE || skill == Skills.STRENGTH
										|| skill == Skills.MAGIC || skill == Skills.RANGE || skill == Skills.HITPOINTS))
						|| trackSkillsIds[i] == getCounterSkill(skill)) {
					xpTracks[i] += exp;
					refreshCounterXp(i);
				}
			}
		}

		if (xp[skill] > MAXIMUM_EXP) {
			xp[skill] = MAXIMUM_EXP;
		}
		int newLevel = getLevelForXp(skill);
		int levelDiff = newLevel - oldLevel;
		if (newLevel > oldLevel) {
			level[skill] += levelDiff;
			player.getDialogueManager().startDialogue("LevelUp", skill);
			if (skill == SUMMONING || (skill >= ATTACK && skill <= MAGIC)) {
				player.getAppearence().generateAppearenceData();
				if (skill == HITPOINTS)
					player.heal(levelDiff * 10);
				else if (skill == PRAYER)
					player.getPrayer().restorePrayer(levelDiff * 10);
			}
			player.getQuestManager().checkCompleted();
		}
		refresh(skill);
		return exp;
	}

	public void addSkillXpRefresh(int skill, double xp) {
		this.xp[skill] += xp;
		level[skill] = (short) getLevelForXp(skill);
	}

	public void resetSkillNoRefresh(int skill) {
		xp[skill] = 0;
		level[skill] = 1;
	}

	public void setXp(int skill, double exp) {
		xp[skill] = exp;
		refresh(skill);
	}

	public boolean canObtainTrimmed() {
		int count99 = 0;
		for (int skill = 0; skill < SKILL_NAME.length; skill++) {
			if (level[skill] == 99)
				count99++;
		}
		return count99 >= 2;
	}

	public void setXp(double[] xp) {
		// TODO remove
		this.xp = xp;
	}

	public String getSkillName(int skill) {
		String skillName = null;
		switch (skill) {
		case 0:
			return "Attack";
		case 1:
			return "Defence";
		case 2:
			return "Strength";
		case 3:
			return "Constitution";
		case 4:
			return "Ranged";
		case 5:
			return "Prayer";
		case 6:
			return "Magic";
		case 7:
			return "Cooking";
		case 8:
			return "Woodcutting";
		case 9:
			return "Fletching";
		case 10:
			return "Fishing";
		case 11:
			return "Firemaking";
		case 12:
			return "Crafting";
		case 13:
			return "Smithing";
		case 14:
			return "Mining";
		case 15:
			return "Herblore";
		case 16:
			return "Agility";
		case 17:
			return "Thieving";
		case 18:
			return "Slayer";
		case 19:
			return "Farming";
		case 20:
			return "Runecrafting";
		case 21:
			return "Hunter";
		case 22:
			return "Construction";
		case 23:
			return "Summoning";
		case 24:
			return "Dungeoneering";
		}
		return skillName;
	}

	public boolean isMaxed() {
		if (getLevelForXp(ATTACK) < 99 || getLevelForXp(STRENGTH) < 99 || getLevelForXp(DEFENCE) < 99
				|| getLevelForXp(RANGE) < 99 || getLevelForXp(PRAYER) < 99 || getLevelForXp(MAGIC) < 99
				|| getLevelForXp(RUNECRAFTING) < 99 || getLevelForXp(CONSTRUCTION) < 99
				|| getLevelForXp(DUNGEONEERING) < 99 || getLevelForXp(HITPOINTS) < 99 || getLevelForXp(AGILITY) < 99
				|| getLevelForXp(HERBLORE) < 99 || getLevelForXp(THIEVING) < 99 || getLevelForXp(CRAFTING) < 99
				|| getLevelForXp(FLETCHING) < 99 || getLevelForXp(SLAYER) < 99 || getLevelForXp(HUNTER) < 99
				|| getLevelForXp(MINING) < 99 || getLevelForXp(SMITHING) < 99 || getLevelForXp(FISHING) < 99
				|| getLevelForXp(COOKING) < 99 || getLevelForXp(FIREMAKING) < 99 || getLevelForXp(WOODCUTTING) < 99
				|| getLevelForXp(FARMING) < 99 || getLevelForXp(SUMMONING) < 99 || getLevelForXp(CONSTRUCTION) < 99
				|| getLevelForXp(DUNGEONEERING) < 99 || getLevelForXp(DIVINATION) < 99) {
			return true;
		}
		return false;
	}

	public int getSkill(int slotId) {
		switch (slotId) {
		case 0:
			return 0;
		case 6:
			return 1;
		case 3:
			return 3;
		case 1:
			return 4;
		case 9:
			return 5;
		case 12:
			return 6;
		case 15:
			return 7;
		case 11:
			return 8;
		case 17:
			return 9;
		case 16:
			return 10;
		case 8:
			return 11;
		case 14:
			return 12;
		case 13:
			return 13;
		case 5:
			return 14;
		case 2:
			return 15;
		case 7:
			return 16;
		case 4:
			return 17;
		case 10:
			return 18;
		case 18:
			return 20;
		case 19:
			return 18;
		case 20:
			return 19;
		case 21:
			return 22;
		case 22:
			return 21;
		case 23:
			return 23;
		case 24:
			return 24;
		case 25:
			return 25;
		default:
			return -1;
		}
	}

	public int getSkillIdByTargetId(int targetId) {
		switch (targetId) {
		case 0: // Attack
			return ATTACK;
		case 1: // Strength
			return STRENGTH;
		case 2: // Range
			return RANGE;
		case 3: // Magic
			return MAGIC;
		case 4: // Defence
			return DEFENCE;
		case 5: // Constitution
			return HITPOINTS;
		case 6: // Prayer
			return PRAYER;
		case 7: // Agility
			return AGILITY;
		case 8: // Herblore
			return HERBLORE;
		case 9: // Thieving
			return THIEVING;
		case 10: // Crafting
			return CRAFTING;
		case 11: // Runecrafting
			return RUNECRAFTING;
		case 12: // Mining
			return MINING;
		case 13: // Smithing
			return SMITHING;
		case 14: // Fishing
			return FISHING;
		case 15: // Cooking
			return COOKING;
		case 16: // Firemaking
			return FIREMAKING;
		case 17: // Woodcutting
			return WOODCUTTING;
		case 18: // Fletching
			return FLETCHING;
		case 19: // Slayer
			return SLAYER;
		case 20: // Farming
			return FARMING;
		case 21: // Construction
			return CONSTRUCTION;
		case 22: // Hunter
			return HUNTER;
		case 23: // Summoning
			return SUMMONING;
		case 24: // Dungeoneering
			return DUNGEONEERING;
		case 25: // Divination
			return DIVINATION;
		default:
			return -1;
		}
	}

	public void refreshEnabledSkillsTargets() {

		int value = Utils.get32BitValue(enabledSkillsTargets, true);
		/*
		 * int value = 0; if (enabledSkillsTargets[0]) // Attack. value += 2; if
		 * (enabledSkillsTargets[1]) // Strength. value += 4; if
		 * (enabledSkillsTargets[2]) // Range. value += 8; if
		 * (enabledSkillsTargets[3]) // Magic. value += 16; if
		 * (enabledSkillsTargets[4]) // Defence. value += 32; if
		 * (enabledSkillsTargets[5]) // Constitution. value += 64; if
		 * (enabledSkillsTargets[6]) // Prayer. value += 128; if
		 * (enabledSkillsTargets[7]) // Agility. value += 256; if
		 * (enabledSkillsTargets[8]) // Herblore. value += 512; if
		 * (enabledSkillsTargets[9]) // Theiving. value += 1024; if
		 * (enabledSkillsTargets[10]) // Crafting. value += 2048; if
		 * (enabledSkillsTargets[11]) // Runecrafting. value += 4096; if
		 * (enabledSkillsTargets[12]) // Mining. value += 8192; if
		 * (enabledSkillsTargets[13]) // Smithing. value += 16384; if
		 * (enabledSkillsTargets[14]) // Fishing. value += 32768; if
		 * (enabledSkillsTargets[15]) // Cooking. value += 65536; if
		 * (enabledSkillsTargets[16]) // Firemaking. value += 131072; if
		 * (enabledSkillsTargets[17]) // Woodcutting. value += 262144; if
		 * (enabledSkillsTargets[18]) // Fletching. value += 524288; if
		 * (enabledSkillsTargets[19]) // Slayer. value += 1048576; if
		 * (enabledSkillsTargets[20]) // Farming. value += 2097152; if
		 * (enabledSkillsTargets[21]) // Construction. value += 4194304; if
		 * (enabledSkillsTargets[22]) // Hunter. value += 8388608; if
		 * (enabledSkillsTargets[23]) // Summoning. value += 16777216; if
		 * (enabledSkillsTargets[24]) // Dungeoneering. value += 33554432;
		 */
		player.getVarsManager().sendVarBit(1966, value);
	}

	public void refreshUsingLevelTargets() {
		// int value = Utils.get32BitValue(skillsTargetsUsingLevelMode, true);
		int value = 0;
		if (skillsTargetsUsingLevelMode[0]) // Attack.
			value += 2;
		if (skillsTargetsUsingLevelMode[1]) // Strength.
			value += 4;
		if (skillsTargetsUsingLevelMode[2]) // Range.
			value += 8;
		if (skillsTargetsUsingLevelMode[3]) // Magic.
			value += 16;
		if (skillsTargetsUsingLevelMode[4]) // Defence.
			value += 32;
		if (skillsTargetsUsingLevelMode[5]) // Constitution.
			value += 64;
		if (skillsTargetsUsingLevelMode[6]) // Prayer.
			value += 128;
		if (skillsTargetsUsingLevelMode[7]) // Agility.
			value += 256;
		if (skillsTargetsUsingLevelMode[8]) // Herblore.
			value += 512;
		if (skillsTargetsUsingLevelMode[9]) // Theiving.
			value += 1024;
		if (skillsTargetsUsingLevelMode[10]) // Crafting.
			value += 2048;
		if (skillsTargetsUsingLevelMode[11]) // Runecrafting.
			value += 4096;
		if (skillsTargetsUsingLevelMode[12]) // Mining.
			value += 8192;
		if (skillsTargetsUsingLevelMode[13]) // Smithing.
			value += 16384;
		if (skillsTargetsUsingLevelMode[14]) // Fishing.
			value += 32768;
		if (skillsTargetsUsingLevelMode[15]) // Cooking.
			value += 65536;
		if (skillsTargetsUsingLevelMode[16]) // Firemaking.
			value += 131072;
		if (skillsTargetsUsingLevelMode[17]) // Woodcutting.
			value += 262144;
		if (skillsTargetsUsingLevelMode[18]) // Fletching.
			value += 524288;
		if (skillsTargetsUsingLevelMode[19]) // Slayer.
			value += 1048576;
		if (skillsTargetsUsingLevelMode[20]) // Farming.
			value += 2097152;
		if (skillsTargetsUsingLevelMode[21]) // Construction.
			value += 4194304;
		if (skillsTargetsUsingLevelMode[22]) // Hunter.
			value += 8388608;
		if (skillsTargetsUsingLevelMode[23]) // Summoning.
			value += 16777216;
		if (skillsTargetsUsingLevelMode[24]) // Dungeoneering.
			value += 33554432;
		if (skillsTargetsUsingLevelMode[25]) // Divination.
			value += 67108864;
		player.getVarsManager().sendVarBit(1968, value);
	}

	public void refreshSkillsTargetsValues() {
		for (int i = 0; i < 26; i++)
			player.getVarsManager().sendVarBit(1969 + i, skillsTargetsValues[i]);
	}

	public void setSkillTargetEnabled(int id, boolean enabled) {
		enabledSkillsTargets[id] = enabled;
		refreshEnabledSkillsTargets();
	}

	public void setSkillTargetUsingLevelMode(int id, boolean using) {
		skillsTargetsUsingLevelMode[id] = using;
		refreshUsingLevelTargets();
	}

	public void setSkillTargetValue(int skillId, int value) {
		skillsTargetsValues[skillId] = value;
		refreshSkillsTargetsValues();
	}

	public void setSkillTarget(boolean usingLevel, int skillId, int target) {
		setSkillTargetEnabled(skillId, true);
		setSkillTargetUsingLevelMode(skillId, usingLevel);
		setSkillTargetValue(skillId, target);
	}
}