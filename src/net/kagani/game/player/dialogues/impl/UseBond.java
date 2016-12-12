package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;

public class UseBond extends Dialogue {

	public Player player2;

	@Override
	public void start() {
		stage = 1;
		player2 = (Player) parameters[0];
		sendOptionsDialogue(player.getDisplayName() + " offering you a bond",
				"Accept bond.", "Decline bond.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (player2.getInventory().containsItem(29492, 1)) {
					if (player.getInventory().getFreeSlots() < 2) {
						player.getDialogueManager().startDialogue(
								"SimpleMessage",
								"Not enough space in your inventory.");
						player2.getDialogueManager()
								.startDialogue(
										"SimpleMessage",
										player.getDisplayName()
												+ " does not have enough space in their inventory.");
						return;
					}
					player.getDialogueManager()
							.startDialogue(
									"SimpleMessage",
									"You have accepted the offer for a free bond. It has been placed in your inventory.");
					player2.getDialogueManager()
							.startDialogue(
									"SimpleMessage",
									player.getDisplayName()
											+ " has accepted your offer for a free bond.");
					player.getInventory().addItem(29492, 1);
					player2.getInventory().deleteItem(29492, 1);
				} else {
					player.getDialogueManager()
							.startDialogue(
									"SimpleMessage",
									player2.getDisplayName()
											+ " doesn't seem to have a bond in his inventory.");
					player2.getDialogueManager().startDialogue("SimpleMessage",
							"You don't have a bond in your inventory.");
				}
				break;
			case OPTION_2:
				player2.getDialogueManager().startDialogue(
						"SimpleMessage",
						player.getDisplayName()
								+ " has declined your offer of a free bond.");
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You have declined the offer of a free bond.");
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}