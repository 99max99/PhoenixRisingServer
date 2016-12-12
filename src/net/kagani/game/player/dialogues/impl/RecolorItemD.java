package net.kagani.game.player.dialogues.impl;

import net.kagani.game.item.Item;
import net.kagani.game.player.dialogues.Dialogue;

public class RecolorItemD extends Dialogue {

	/**
	 * @auhtor: Dylan Page
	 */

	private Item item;;

	@Override
	public void start() {
		stage = 1;
		item = (Item) this.parameters[0];
		switch (item.getId()) {
		case 2581:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Recolor to red - 2500 loyalty points.",
					"Recolor to blue - 2500 loyalty points.",
					"Recolor to white - 2500 loyalty points.",
					"Recolor to yellow - 2500 loyalty points.", "Nevermind.");
			break;
		case 9470:
		case 15486:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Recolor to red - 2500 loyalty points.",
					"Recolor to yellow - 2500 loyalty points.",
					"Recolor to blue - 2500 loyalty points.",
					"Recolor to green - 2500 loyalty points.", "Nevermind.");
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
			case OPTION_2:
			case OPTION_3:
			case OPTION_4:
				if (player.getLoyaltyPoints() >= 2500) {
					stage = -1;
					player.setLoyaltyPoints(player.getLoyaltyPoints() - 2500);
					sendNPCDialogue(13727, HAPPY, "There you go.");
					transformItem(componentId);
				} else {
					stage = -1;
					sendDialogue("You need 2500 loyalty points to recolor this item.");
				}
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		}
	}

	private void transformItem(int colorId) {
		switch (item.getId()) {
		case 2581:
			if (player.getInventory().containsItem(2581, 1)) {
				player.getInventory().deleteItem(2581, 1);
				switch (colorId) {
				case 3:
					player.getInventory().addItem(20949, 1);
					break;
				case 4:
					player.getInventory().addItem(20951, 1);
					break;
				case 5:
					player.getInventory().addItem(20952, 1);
					break;
				case 6:
					player.getInventory().addItem(20950, 1);
					break;
				}
			} else {
				stage = -1;
				sendDialogue("You don't have that item in your inventory.");
			}
			break;
		case 9470:
			if (player.getInventory().containsItem(9470, 1)) {
				player.getInventory().deleteItem(9470, 1);
				switch (colorId) {
				case 3:
					player.getInventory().addItem(22215, 1);
					break;
				case 4:
					player.getInventory().addItem(22216, 1);
					break;
				case 5:
					player.getInventory().addItem(22217, 1);
					break;
				case 6:
					player.getInventory().addItem(22218, 1);
					break;
				}
			} else {
				stage = -1;
				sendDialogue("You don't have that item in your inventory.");
			}
			break;
		case 15486:
			if (player.getInventory().containsItem(15486, 1)) {
				player.getInventory().deleteItem(15486, 1);
				switch (colorId) {
				case 3:
					player.getInventory().addItem(22207, 1);
					break;
				case 4:
					player.getInventory().addItem(22209, 1);
					break;
				case 5:
					player.getInventory().addItem(22211, 1);
					break;
				case 6:
					player.getInventory().addItem(22213, 1);
					break;
				}
			} else {
				stage = -1;
				sendDialogue("You don't have that item in your inventory.");
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}
