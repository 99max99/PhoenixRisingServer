/**
 * @author Dylan Page - Ventyz Productions
 *
 * SoulLobby.java created on 31.des.2014.
 */
package net.kagani.game.minigames.soulwars;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Player;

// TODO: Auto-generated Javadoc
/**
 * The Class SoulLobby.
 */
public class SoulLobby {
	// Ints & Booleans
	/** The red wait. */
	public static List<Player> redWait = new ArrayList<Player>();

	/** The blue wait. */
	public static List<Player> blueWait = new ArrayList<Player>();

	/** The all waiting. */
	public static List<Player> allWaiting = new ArrayList<Player>();

	/** The minutes. */
	public static int minutes = 1;

	/** The Constant lock. */
	public static final Object lock = new Object();

	/** The lobbytime. */
	public static boolean lobbytime = false;

	//

	/*
	 * Handles the red Lobby
	 */

	/**
	 * Pass red.
	 *
	 * @param player
	 *            the player
	 */
	public static void passRed(Player player) {
		if (player.getX() >= 1900)
			removeRed(player);

		for (int i = 91; i < 2319; i++) {
			if (player.getInventory().containsOneItem(15272)
					|| player.getInventory().containsOneItem(i)
					|| player.getInventory().containsOneItem(385)) {
				player.getPackets().sendGameMessage(
						"You cannot take non combat items in the game.");
				return;
			}
		}

		if (player.getEquipment().getCapeId() != -1) {
			player.getPackets().sendGameMessage(
					"You cannot wear capes in this minigame..");
			return;
		}
		if (redWait.size() > blueWait.size()) {
			player.getPackets().sendGameMessage(
					"Guthix demans balance! Choose the other team.");
			player.stopAll();
			return;
		}
		if (player.getX() <= 1899) {
			addRed(player);
		}

	}

	/*
	 * Handles the blue lobby
	 */

	/**
	 * Pass blue.
	 *
	 * @param player
	 *            the player
	 */
	public static void passBlue(Player player) {
		if (player.getX() <= 1879) {
			removeBlue(player);
		}
		for (int i = 91; i < 2319; i++) {
			if (player.getInventory().containsOneItem(15272)
					|| player.getInventory().containsOneItem(i)
					|| player.getInventory().containsOneItem(385)) {
				player.getPackets().sendGameMessage(
						"You cannot take non combat items in the game.");
				return;
			}
		}
		if (player.getEquipment().getCapeId() != -1) {
			player.getPackets().sendGameMessage(
					"You cannot wear capes in this minigame..");
			return;
		}
		if (blueWait.size() > redWait.size()) {
			player.getPackets().sendGameMessage(
					"Guthix demands balance! Choose the other team.");
			return;
		}
		if (player.getY() == 3162) {
			if (player.getX() >= 1880) {
				addBlue(player);
			}
		}
	}

	/**
	 * Adds red player to lobby.
	 *
	 * @param player
	 *            the player
	 */

	public static void addBlue(Player player) {
		blueWait.add(player);
		allWaiting.add(player);
		player.didPassBlue = true;
		player.setNextWorldTile(new WorldTile(1879, 3162, 0));
		setcape(player, new Item(14642));
		sendInterfaces(player);
		player.getControlerManager().startControler("SoulLobbyControler");
	}

	/*
	 * Remove blue player from lobby.
	 */

	/**
	 * Removes the blue.
	 *
	 * @param player
	 *            the player
	 */
	public static void removeBlue(Player player) {
		player.didPassBlue = false;
		player.setNextWorldTile(new WorldTile(1880, 3162, 0));
		blueWait.remove(player);
		allWaiting.remove(player);
		setcape(player, null);
		sendInterfaces(player);
		player.getInterfaceManager().removeMinigameInterface();
		;// /////
		player.getControlerManager().removeControlerWithoutCheck();
	}

	/*
	 * Adds red player to lobby/
	 */

	/**
	 * Adds the red.
	 *
	 * @param player
	 *            the player
	 */
	public static void addRed(Player player) {
		redWait.add(player);
		allWaiting.add(player);
		player.didPassRed = true;
		player.setNextWorldTile(new WorldTile(1900, 3162, 0));
		setcape(player, new Item(14641));
		sendInterfaces(player);
		player.getControlerManager().startControler("SoulLobbyControler");
	}

	/*
	 * Removes red player from lobby.
	 */

	/**
	 * Removes the red.
	 *
	 * @param player
	 *            the player
	 */
	public static void removeRed(Player player) {
		player.didPassRed = false;
		player.setNextWorldTile(new WorldTile(1899, 3162, 0));
		redWait.remove(player);
		allWaiting.remove(player);
		setcape(player, null);
		player.getInterfaceManager().removeMinigameInterface();
		player.getControlerManager().removeControlerWithoutCheck();
	}

	/*
	 * distinguishes the cape for "so" team.
	 */

	/**
	 * Setcape.
	 *
	 * @param player
	 *            the player
	 * @param cape
	 *            the cape
	 */
	public static void setcape(Player player, Item cape) {
		player.getEquipment().getItems().set(Equipment.SLOT_CAPE, cape);
		player.getEquipment().refresh(Equipment.SLOT_CAPE);
		player.getAppearence().generateAppearenceData();

	}

	/**
	 * Game started time.
	 *
	 * @param player
	 *            the player
	 */
	public static void gameStartedTime(Player player) {
		player.getInterfaceManager().sendMinigameInterface(837);
		player.getPackets().sendIComponentText(837, 8, "Players needed");
		player.getPackets().sendIComponentText(837, 3, "-");
		player.getPackets().sendIComponentText(837, 5, "-");
		player.getPackets().sendIComponentText(837, 9,
				"New game: " + SoulWars.gameTime + " mins");
	}

	/*
	 * Sends Lobby Interfaces(837) //TODO
	 */

	/**
	 * Send interfaces.
	 *
	 * @param player
	 *            the player
	 */
	public static void sendInterfaces(Player player) {
		player.getInterfaceManager().sendMinigameInterface(837);
	}

	/*
	 * Handles the balance between teams. *
	 */

	/**
	 * Balanceportal.
	 *
	 * @param player
	 *            the player
	 */
	public static void balanceportal(Player player) {
		for (int i = 91; i < 2319; i++) {
			if (player.getInventory().containsOneItem(15272)
					|| player.getInventory().containsOneItem(i)
					|| player.getInventory().containsOneItem(385)) {
				player.getPackets().sendGameMessage(
						"You cannot take non combat items in the game.");
				return;
			}
		}
		if (player.getEquipment().getCapeId() != -1) {
			player.getPackets().sendGameMessage(
					"You cannot wear capes in this minigame..");
			return;
		}
		if (blueWait.size() > redWait.size()) {
			addRed(player);
			player.getPackets().sendGameMessage(
					"Guthix demands balance! Join the team of destruction");
		} else if (redWait.size() > blueWait.size()) {
			addBlue(player);
			player.getPackets().sendGameMessage(
					"Guthix demands balance! Join the team of creation!");
		} else if (redWait.size() == blueWait.size()) {
			player.getPackets().sendGameMessage(
					"Teams are even ! Choose a team of your liking.");
		}
	}

	/*
	 * Handles the balance from red to blue.
	 */

	/**
	 * Redbalance.
	 *
	 * @param player
	 *            the player
	 */
	public static void redbalance(Player player) {
		if (redWait.size() > blueWait.size()) {
			player.getPackets().sendGameMessage(
					"Guthix demans balance! Choose the other team.");
			player.stopAll();
			return;
		}
		return;
	}

	/*
	 * Handles the balance from blue to red.
	 */

	/**
	 * Bluebalance.
	 *
	 * @param player
	 *            the player
	 */
	public static void bluebalance(Player player) {
		if (blueWait.size() > redWait.size()) {
			player.getPackets().sendGameMessage(
					"Guthix demands balance! Choose the other team.");
			return;
		}
		return;

	}

	/*
	 * Efficient Way of handling Objects Switch Statements
	 */

	/**
	 * Handle objects.
	 *
	 * @param player
	 *            the player
	 * @param objectId
	 *            the object id
	 * @return true, if successful
	 */
	public static boolean handleObjects(Player player, int objectId) {
		switch (objectId) {
		case 42030:
			passRed(player);
			return true;

		case 42029:
			passBlue(player);
			return true;

		case 42031:
			balanceportal(player);
			return true;
		}
		return false;
	}

	/*
	 * if (allWaiting.size() >= 2 && minutes == 1 || allWaiting.size() >= 2)//
	 * just for testing passPlayersToGame();
	 */

}
