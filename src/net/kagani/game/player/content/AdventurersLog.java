package net.kagani.game.player.content;

import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.QuestManager.Quests;

public final class AdventurersLog {

	private static String[] REQUIREMENTS = { "120 in Dungeoneering",
			"99 in all other skills", "50 PvP Kills", "One WildyWyrm Kill",
			"Win one game of Stealing Creations", "Win one game of Fight Pits",
			"Completed one game of Castle Wars",
			"Capture a flag in Castle Wars", "Complete Fight Caves",
			"Complete Fight Kiln", "Kill the Queen Black Dragon once",
			"Finished 'Lost City' Mini-quest",
			"Finished 'Nomad's requirement'", "[Trim] 100 PVP Kills",
			"[Trim] 100 Dominion Tower Kills", "[Trim] 25 Castlewars Games",
			"[Trim] 25 Stealing Creation Games", "", "" };

	private static boolean hasRequirement(Player player, int idx) {
		switch (idx) {
		case 0:
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 120)
				return false;
			break;
		case 1:
			for (int skill = 0; skill < Skills.SKILL_NAME.length; skill++) {
				if (player.getSkills().getLevel(skill) < 99)
					return false;
			}
			break;
		case 2:
			if (player.getKillCount() < PVP_KILLS)
				return false;
			break;
		case 3:
			if (!player.isKilledWildyWyrm())
				return false;
			break;
		case 4:
			if (!player.isCompletedStealingCreation())
				return false;
			break;
		case 5:
			if (!player.isWonFightPits())
				return false;
			break;
		case 6:
			if (player.getFinishedCastleWars() == 0)
				return false;
			break;
		case 7:
			if (!player.isCapturedCastleWarsFlag())
				return false;
			break;
		case 8:
			if (!player.isCompletedFightCaves())
				return false;
			break;
		case 9:
			if (!player.isCompletedFightKiln())
				return false;
			break;
		case 10:
			if (!player.isKilledQueenBlackDragon())
				return false;
		case 11:
			if (!player.isKilledLostCityTree())
				return false;
			break;
		case 12:
			if (!player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM))
				return false;
			break;
		case 13:
			if (player.getKillCount() < TRIMMED_PVP_KILLS)
				return false;
			break;
		case 14:
			if (player.getDominionTower().getKilledBossesCount() < DOM_KILLS)
				return false;
			break;
		case 15:
			if (player.getFinishedCastleWars() < CASTLE_WARS_GAMES)
				return false;
			break;
		case 16:
			if (player.getFinishedStealingCreations() < STEALING_CREATION_GAMES)
				return false;
			break;
		}
		return true;
	}

	private static final int PVP_KILLS = 50, TRIMMED_PVP_KILLS = 100,
			CASTLE_WARS_GAMES = 25, STEALING_CREATION_GAMES = 25,
			DOM_KILLS = 100;

	private AdventurersLog() {

	}

	public static void open(Player player) {
		player.getInterfaceManager().sendCentralInterface(623);
		player.getPackets().sendIComponentText(623, 66,
				"Completionist Cape Requirements");
		int value = 0;
		for (int component = 3; component < 41; component += 2) {
			int idx = (component - 3) / 2;
			String req = REQUIREMENTS[idx];
			if (req == "")
				player.getPackets()
						.sendHideIComponent(623, component - 1, true);
			else {
				if (!hasRequirement(player, idx)) {
					req = "<col=FF0000>" + req;
					value += (int) Math.pow(2, idx + (idx > 10 ? 2 : 1));
				}
			}
			player.getPackets().sendIComponentText(623, component, req);
		}
		player.getVarsManager().forceSendVar(2396, value);
		player.getPackets().sendGameMessage("<col=FE2EF7>You have: <col>");
		player.getPackets().sendGameMessage(
				"<col=FFBF00>" + player.getKillCount() + "/"
						+ TRIMMED_PVP_KILLS + " PvP kills.");
		player.getPackets().sendGameMessage(
				"<col=FFBF00>"
						+ player.getDominionTower().getKilledBossesCount()
						+ "/" + DOM_KILLS + " dominion tower boss kills.");
		player.getPackets().sendGameMessage(
				"<col=FFBF00>" + player.getFinishedCastleWars() + "/"
						+ CASTLE_WARS_GAMES + " Castle Wars games completed.");
		player.getPackets().sendGameMessage(
				"<col=FFBF00>" + player.getFinishedStealingCreations() + "/"
						+ STEALING_CREATION_GAMES
						+ " Stealing Creation games completed.");
	}
}