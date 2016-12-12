package net.kagani.game.player.content;

import net.kagani.game.player.Player;

public class ArtisanWorkshop {
	public static int BRONZEINGOTS = 20502;
	public static int IRONINGOT = 20503;

	public static int INGOTWITH = 1072;
	public static int IRONINGOTS = 20632;
	public static int STEELINGOTS = 20504;
	public static int MITHRILINGOTS = 20634;
	public static int ADAMANTINGOTS = 20635;
	public static int RUNEINGOTS = 20636;
	public static int FULLINVENTORY = 28;

	public static void GiveBronzeIngots(Player player) {
		player.getInventory().addItem(BRONZEINGOTS, 28);
	}

	public static void TakeBronzeIngots(Player player) {
		player.getInventory().deleteItem(BRONZEINGOTS, 28);
	}

	public static void TakeIronIngots(Player player) {
		player.getInventory().addItem(IRONINGOT, 28);
	}

	public static void GiveIronIngots(Player player) {
		player.getInventory().deleteItem(IRONINGOT, 28);
	}

	public static void TakeSteelIngots(Player player) {
		player.getInventory().addItem(STEELINGOTS, 28);
	}

	public static void GiveSteelIngots(Player player) {
		player.getInventory().deleteItem(STEELINGOTS, 28);
	}

	public static void SendInformationBoard(Player player) {
		player.getInterfaceManager().sendCentralInterface(891);
	}

	public static void DepositIngots(Player player) {
		if (player.getInventory().containsItem(IRONINGOTS, FULLINVENTORY)) {
			player.getInventory().deleteItem(IRONINGOTS, FULLINVENTORY);
			player.getInventory().refresh();
		} else {
			player.getPackets().sendGameMessage(
					"You don't have any ingots to return.");
		}
		if (player.getInventory().containsItem(STEELINGOTS, FULLINVENTORY)) {
			player.getInventory().deleteItem(STEELINGOTS, FULLINVENTORY);
			player.getInventory().refresh();
		} else {
			player.getPackets().sendGameMessage(
					"You don't have any ingots to return.");
		}
		if (player.getInventory().containsItem(MITHRILINGOTS, FULLINVENTORY)) {
			player.getInventory().deleteItem(MITHRILINGOTS, FULLINVENTORY);
			player.getInventory().refresh();
		} else {
			player.getPackets().sendGameMessage(
					"You don't have any ingots to return.");
		}
		if (player.getInventory().containsItem(ADAMANTINGOTS, FULLINVENTORY)) {
			player.getInventory().deleteItem(ADAMANTINGOTS, FULLINVENTORY);
			player.getInventory().refresh();
		} else {
			player.getPackets().sendGameMessage(
					"You don't have any ingots to return.");
		}
		if (player.getInventory().containsItem(RUNEINGOTS, FULLINVENTORY)) {
			player.getInventory().deleteItem(RUNEINGOTS, FULLINVENTORY);
			player.getInventory().refresh();
		} else {
			player.getPackets().sendGameMessage(
					"You don't have any ingots to return.");
		}
	}

	public static void DepositArmour(Player player) {
		player.getInventory().deleteItem(20572, 28);
		player.getInventory().deleteItem(20577, 28);
		player.getInventory().deleteItem(20582, 28);
		player.getInventory().deleteItem(20587, 28);
		player.getInventory().deleteItem(20597, 28);
		player.getInventory().deleteItem(20602, 28);
		player.getInventory().deleteItem(20607, 28);
		player.getInventory().deleteItem(20612, 28);
		player.getInventory().deleteItem(20617, 28);
		player.getInventory().deleteItem(20622, 28);
		player.getInventory().deleteItem(20627, 28);
		player.getInventory().deleteItem(20573, 28);
		player.getInventory().deleteItem(20578, 28);
		player.getInventory().deleteItem(20583, 28);
		player.getInventory().deleteItem(20588, 28);
		player.getInventory().deleteItem(20593, 28);
		player.getInventory().deleteItem(20598, 28);
		player.getInventory().deleteItem(20603, 28);
		player.getInventory().deleteItem(20608, 28);
		player.getInventory().deleteItem(20613, 28);
		player.getInventory().deleteItem(20618, 28);
		player.getInventory().deleteItem(20623, 28);
		player.getInventory().deleteItem(20628, 28);
		player.getInventory().deleteItem(20574, 28);
		player.getInventory().deleteItem(20579, 28);
		player.getInventory().deleteItem(20584, 28);
		player.getInventory().deleteItem(20589, 28);
		player.getInventory().deleteItem(20594, 28);
		player.getInventory().deleteItem(20599, 28);
		player.getInventory().deleteItem(20604, 28);
		player.getInventory().deleteItem(20609, 28);
		player.getInventory().deleteItem(20614, 28);
		player.getInventory().deleteItem(20619, 28);
		player.getInventory().deleteItem(20624, 28);
		player.getInventory().deleteItem(20629, 28);
		player.getInventory().deleteItem(20575, 28);
		player.getInventory().deleteItem(20580, 28);
		player.getInventory().deleteItem(20585, 28);
		player.getInventory().deleteItem(20590, 28);
		player.getInventory().deleteItem(20595, 28);
		player.getInventory().deleteItem(20600, 28);
		player.getInventory().deleteItem(20605, 28);
		player.getInventory().deleteItem(20610, 28);
		player.getInventory().deleteItem(20615, 28);
		player.getInventory().deleteItem(20620, 28);
		player.getInventory().deleteItem(20625, 28);
		player.getInventory().deleteItem(20630, 28);
		player.getInventory().deleteItem(20576, 28);
		player.getInventory().deleteItem(20581, 28);
		player.getInventory().deleteItem(20586, 28);
		player.getInventory().deleteItem(20591, 28);
		player.getInventory().deleteItem(20596, 28);
		player.getInventory().deleteItem(20601, 28);
		player.getInventory().deleteItem(20606, 28);
		player.getInventory().deleteItem(20611, 28);
		player.getInventory().deleteItem(20616, 28);
		player.getInventory().deleteItem(20621, 28);
		player.getInventory().deleteItem(20626, 28);
		player.getInventory().deleteItem(20631, 28);
		player.getInventory().refresh();
		player.getPackets().sendGameMessage("You deposit armour in the chute.",
				true);
	}

	public static void WithdrawIngots(Player player) {
		player.getInventory().refresh();
	}

	public static void handleButtons(Player player, int componentId) {
		if (componentId == 201) {
			player.getInventory().addItem(IRONINGOTS, FULLINVENTORY);
			player.getPackets()
					.sendGameMessage(
							"You have taken full inventory of iron ingots from the machine.",
							true);
			player.getInventory().refresh();
			player.closeInterfaces();
		}
		if (componentId == 213) {
			player.getInventory().addItem(STEELINGOTS, FULLINVENTORY);
			player.getPackets()
					.sendGameMessage(
							"You have taken full inventory of steel ingots from the machine.",
							true);
			player.getInventory().refresh();
			player.closeInterfaces();
		}
		if (componentId == 225) {
			player.getInventory().addItem(MITHRILINGOTS, FULLINVENTORY);
			player.getPackets()
					.sendGameMessage(
							"You have taken full inventory of mithril ingots from the machine.",
							true);
			player.getInventory().refresh();
			player.closeInterfaces();
		}
		if (componentId == 237) {
			player.getInventory().addItem(ADAMANTINGOTS, FULLINVENTORY);
			player.getPackets()
					.sendGameMessage(
							"You have taken full inventory of adamant ingots from the machine.",
							true);
			player.getInventory().refresh();
			player.closeInterfaces();
		}
		if (componentId == 249) {
			player.getInventory().addItem(RUNEINGOTS, FULLINVENTORY);
			player.getPackets()
					.sendGameMessage(
							"You have taken full inventory of rune ingots from the machine.",
							true);
			player.getInventory().refresh();
			player.closeInterfaces();
		}
	}
}