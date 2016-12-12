package net.kagani.game.player.content;

import net.kagani.game.player.Player;

public class FarmingStore {

	/**
	 * @author: Dylan Page
	 */

	public static int INTER = 761;

	public static void handleButtons(Player player, int componentId) {
		switch (componentId) {
		case 13:
			if (player.getRakeStored() > 0) {
				player.getInventory().addItem(5341, 1);
				player.setRakeStored(player.getRakeStored() - 1);
				player.getPackets().sendGameMessage(
						"You have withdrawn your rake.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item stored.");
			}
			break;
		case 15:
			if (player.getSeedDibberStored() > 0) {
				player.getInventory().addItem(5343, 1);
				player.setSeedDibberStored(player.getSeedDibberStored() - 1);
				player.getPackets().sendGameMessage(
						"You have withdrawn your rake.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item stored.");
			}
			break;
		case 17:
			if (player.getSpadeStored() > 0) {
				player.getInventory().addItem(952, 1);
				player.setSpadeStored(player.getSpadeStored() - 1);
				player.getPackets().sendGameMessage(
						"You have withdrawn your spade.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item stored.");
			}
			break;
		case 19:
			if (player.getTrowelStored() > 0) {
				player.getInventory().addItem(5325, 1);
				player.setTrowelStored(player.getTrowelStored() - 1);
				player.getPackets().sendGameMessage(
						"You have withdrawn your gardening trowel.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item stored.");
			}
			break;
		case 21:
			if (player.getSecateursStored() > 0) {
				player.getInventory().addItem(5329, 1);
				player.setSecateursStored(player.getSecateursStored() - 1);
				player.getPackets().sendGameMessage(
						"You have withdrawn your secateurs.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item stored.");
			}
			break;
		case 23:
			if (player.getWateringCanStored() > 0) {
				player.getInventory().addItem(5331, 1);
				player.setWateringCanStored(player.getWateringCanStored() - 1);
				player.getPackets().sendGameMessage(
						"You have withdrawn your watering can.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item stored.");
			}
			break;

		case 41:
			if (player.getInventory().containsItem(5341, 1)) {
				player.getInventory().deleteItem(5341, 1);
				player.setRakeStored(player.getRakeStored() + 1);
				player.getPackets().sendGameMessage(
						"You have stored your rake.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item in your inventory.");
			}
			break;
		case 43:
			if (player.getInventory().containsItem(5343, 1)) {
				player.getInventory().deleteItem(5343, 1);
				player.setSeedDibberStored(player.getSeedDibberStored() + 1);
				player.getPackets().sendGameMessage(
						"You have stored your seed dibber.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item in your inventory.");
			}
			break;
		case 45:
			if (player.getInventory().containsItem(952, 1)) {
				player.getInventory().deleteItem(952, 1);
				player.setSpadeStored(player.getSpadeStored() + 1);
				player.getPackets().sendGameMessage(
						"You have stored your spade.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item in your inventory.");
			}
			break;
		case 47:
			if (player.getInventory().containsItem(5325, 1)) {
				player.getInventory().deleteItem(5325, 1);
				player.setTrowelStored(player.getTrowelStored() + 1);
				player.getPackets().sendGameMessage(
						"You have stored your gardening trowel.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item in your inventory.");
			}
			break;
		case 49:
			if (player.getInventory().containsItem(5329, 1)) {
				player.getInventory().deleteItem(5329, 1);
				player.setSecateursStored(player.getSecateursStored() + 1);
				player.getPackets().sendGameMessage(
						"You have stored your secateurs.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item in your inventory.");
			}
			break;
		case 51:
			if (player.getInventory().containsItem(5331, 1)) {
				player.getInventory().deleteItem(5331, 1);
				player.setWateringCanStored(player.getWateringCanStored() + 1);
				player.getPackets().sendGameMessage(
						"You have stored your watering can.");
			} else {
				player.getPackets().sendGameMessage(
						"You don't have that item in your inventory.");
			}
			break;
		default:
			player.getPackets().sendGameMessage(
					"This feature has not been added.");
			break;
		}
	}

	public static void sendInterface(final Player player) {
		player.getInterfaceManager().sendCentralInterface(INTER);
	}
}