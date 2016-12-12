package net.kagani.game.player.content;

import net.kagani.Settings;
import net.kagani.game.minigames.stealingcreation.StealingCreationLobbyController;
import net.kagani.game.player.Player;

public class Bonds {

	/**
	 * @author: Dylan Page
	 */

	public static void redeem(Player player) {
		if (player.getInventory().containsItem(29492, 1)) {
			player.getInterfaceManager().sendCentralInterface(229);
			player.getPackets().sendIComponentText(229, 33, "You have");
			player.getPackets().sendIComponentText(229, 40, "You have");
			player.getPackets().sendIComponentText(229, 34,
					player.getInventory().getAmountOf(29492) + " bonds");
			player.getPackets().sendIComponentText(229, 41,
					player.getInventory().getAmountOf(29492) + " bonds");
			player.getVarsManager().sendVar(4563, 95);
		} else {
			player.getPackets().sendGameMessage(
					"You don't have a bond in your inventory.");
		}
	}

	public static void handleButtonClick(Player player, int interfaceId,
			int componentId) {
		if (interfaceId == 229) {
			switch (componentId) {
			case 58:
				player.getDialogueManager().startDialogue("BondsD");
				break;
			case 18:
				player.getDialogueManager().startDialogue("OpenURLPrompt", "store");
				break;
			case 25:
				player.getInterfaceManager().sendCentralInterface(230);
				break;
			}
		}
		if (interfaceId == 230) {
			switch (componentId) {
			case 52:
				player.getInterfaceManager().sendCentralInterface(229);
				break;
			}
		}
	}

	public static void useOnPlayer(Player player, Player usedOn) {
		if (player.getInventory().containsItem(29492, 1)) {
			if (usedOn.isLocked()) {
				player.getDialogueManager()
						.startDialogue(
								"SimpleMessage",
								usedOn.getDisplayName()
										+ " is too busy at the moment.");
				usedOn.getPackets()
						.sendGameMessage(
								player.getDisplayName()
										+ " is offering you a bond. But you are too busy at the moment.",
								true);
				return;
			}
			if (usedOn.isBeginningAccount()) {
				player.getDialogueManager()
						.startDialogue("SimpleMessage",
								"Starter accounts cannot take bonds for the first hour of playing time.");
				usedOn.getPackets()
						.sendGameMessage(
								"Starter accounts cannot take bonds for the first hour of playing time.");
				return;
			}
			if (usedOn.getControlerManager().getControler() != null
					&& usedOn.getControlerManager().getControler() instanceof StealingCreationLobbyController) {
				player.getDialogueManager()
						.startDialogue(
								"SimpleMessage",
								usedOn.getDisplayName()
										+ " is too busy at the moment.");
				usedOn.getPackets()
						.sendGameMessage(
								player.getDisplayName()
										+ " is offering you a bond. But you are too busy at the moment.",
								true);
				return;
			}
			if (!usedOn.withinDistance(player, 14)) {
				player.getDialogueManager().startDialogue(
						"SimpleMessage",
						"Unable to find target " + usedOn.getDisplayName()
								+ ".");
				return;
			}
			usedOn.stopAll();
			player.stopAll();
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Offering " + usedOn.getDisplayName() + " a bond...");
			usedOn.getDialogueManager().startDialogue("UseBond", player);
		} else {
			player.getPackets().sendGameMessage(
					"You don't have a bond in your inventory.");
		}

	}

	public static int getValue() {
		return 500000000;
	}
}