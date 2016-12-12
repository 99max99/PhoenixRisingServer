/**
 * @author Dylan Page - Ventyz Productions
 *
 * SoulWars.java created on 31.des.2014.
 */
package net.kagani.game.minigames.soulwars;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

public class SoulWars {
	// Ints and booleans
	public static int gameTime = 10;
	public static boolean startedGame = false;
	public static List<Player> playing = new ArrayList<Player>();
	// public final static NPC BlueAvatar = new NPC(8597, new WorldTile(1807,
	// 3210, 0),-1, true);
	public final static NPC RedAvatar = new NPC(8596, new WorldTile(1966, 3254,
			0), -1, true);
	public static WorldTile RedDeparture = new WorldTile(1952, 3241, 0);
	public static WorldTile BlueDeparture = new WorldTile(1819, 3228, 0);
	public static int aocDeaths = 0;
	public static int aodDeaths = 0;
	public static int redRequirment;
	public static int blueRequirment;

	/*
	 * Commence game.
	 */

	public static void passPlayersToGame() {
		for (Player player : World.getPlayers()) {
			InitGameData();
			if (player.didPassRed == true || player.didPassBlue == true) {
				InitPlayerData(player);
			}
		}
	}

	/**
	 * Starts Game Data
	 */

	public static void InitGameData() {
		startedGame = true;
		aocDeaths = 0;
		aodDeaths = 0;
		RedAvatar.setHitpoints(1000);
		// BlueAvatar.setHitpoints(1000);
		gameTime = 20;
		redRequirment = 100;
		blueRequirment = 100;
	}

	/**
	 * Starts Player Data
	 */

	public static void InitPlayerData(Player player) {
		startedGame = true;
		player.inSoulWars = true;
		player.inBlueGrave = false;
		player.inRedGrave = false;
		player.setCanPvp(true);
		player.getInterfaceManager().removeMinigameInterface();
		player.getControlerManager().removeControlerWithoutCheck();
		player.getControlerManager().startControler("SoulWarsController");
		player.setCantTrade(true);

		// player.setForceMultiArea(true);
		playing.add(player);
		if (player.didPassBlue == true) {
			player.setNextWorldTile(new WorldTile(BlueDeparture));
			SoulLobby.blueWait.remove(player);
			SoulLobby.allWaiting.remove(player);
		}
		if (player.didPassRed == true) {
			player.setNextWorldTile(new WorldTile(RedDeparture));
			SoulLobby.redWait.remove(player);
			SoulLobby.allWaiting.remove(player);
		}
		player.getDialogueManager().startDialogue("SimpleNPCMessage", 8591,
				"Now fight to your death!");
	}

	public static void leaveGame(Player player) {
		player.getInventory().deleteItem(4049, Integer.MAX_VALUE); // bandages
		player.getInventory().deleteItem(4053, Integer.MAX_VALUE); // barricades
		player.getInterfaceManager().removeMinigameInterface();

		player.inSoulWars = false;
		player.getInterfaceManager().removeMinigameInterface();
		player.setCanPvp(false);
		player.getInterfaceManager().removeMinigameInterface();
		player.getControlerManager().removeControlerWithoutCheck();
		player.setCantTrade(false);

		// player.setForceMultiArea(false);

		SoulLobby.setcape(player, null);
		playing.remove(player);
		if (player.didPassBlue == true) {
			player.setNextWorldTile(new WorldTile(1886, 3169, 0));
			player.didPassBlue = false;
			player.didPassRed = false;
			playing.remove(player);
			player.getInterfaceManager().removeMinigameInterface();
		}
		if (player.didPassRed == true) {
			player.setNextWorldTile(new WorldTile(1894, 3169, 0));
			player.didPassBlue = false;
			player.didPassRed = false;
			playing.remove(player);
			player.getInterfaceManager().removeMinigameInterface();
		}
		if (playing.size() == 1) {
			player.didPassBlue = false;
			player.didPassRed = false;
			player.getInterfaceManager().removeMinigameInterface();
			endGame();
		}
	}

	public static void endGame() {
		for (Player player : World.getPlayers()) {
			if (player.inSoulWars) {
				zeals(player);
				zeal1(player);
				player.getInterfaceManager().removeMinigameInterface();
				player.getControlerManager().removeControlerWithoutCheck();
				playing.clear();
				startedGame = false;
				leaveGame(player);
				player.getInterfaceManager().removeMinigameInterface();
				player.inSoulWars = false;
				player.getInterfaceManager().removeMinigameInterface();
				player.setCanPvp(false);
				player.getInterfaceManager().removeMinigameInterface();
				player.getControlerManager().removeControlerWithoutCheck();
				player.setCantTrade(false);
				SoulLobby.setcape(player, null);
				playing.remove(player);
				// TODO Zeal points
				SoulLobby.minutes = 1;
			}
		}
	}

	public static void removeNpcs() {
		World.removeNPC(RedAvatar);
		// World.removeNPC(BlueAvatar);
	}

	public static void initNpcs() {
		World.addNPC(RedAvatar);
		// World.addNPC(BlueAvatar);
	}

	public static void DoubleCheck() {
		SoulLobby.blueWait.addAll(SoulLobby.allWaiting);
		SoulLobby.redWait.addAll(SoulLobby.allWaiting);
	}

	public static void zeals(Player player) {
		if (aodDeaths == aocDeaths) {
			if (player.didPassBlue == true || player.didPassRed == true) {
				player.zeal += 2;
				player.getPackets().sendGameMessage(
						"It's a draw. You are awarded 2 zeal points.");
			}
		}
	}

	public static void zeal1(Player player) {
		if (aodDeaths > aocDeaths) {
			if (player.didPassRed == true) {
				player.zeal += 3;
				player.getPackets().sendGameMessage(
						"You are awarded 3 zeals for the win!");
			}
			if (player.didPassBlue == true) {
				player.zeal += 1;
				player.getPackets().sendGameMessage(
						"You are awarded 1 zeal for the loss.");
			}
		}
	}

	public static void cantStart(Player player) {
		player.getPackets()
				.sendGameMessage(
						"You need atleast 3 people on each team to start this game of SoulWars.");
	}
}