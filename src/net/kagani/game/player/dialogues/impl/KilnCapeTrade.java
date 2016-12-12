package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class KilnCapeTrade extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		npcId = (int) parameters[0];
		stage = 1;
		sendNPCDialogue(npcId, NORMAL, "Hello " + player.getUsername()
				+ ", what would you like to swap your melee kiln too?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Ranged.", "Magic.",
					"Nevermind.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				stage = -1;
				if (player.getInventory().containsItem(23659, 1)) {
					player.getInventory().deleteItem(23659, 1);
					player.getInventory().addItem(31610, 1);
					sendNPCDialogue(npcId, NORMAL, "Thanks.");
				} else
					sendNPCDialogue(npcId, NORMAL,
							"You don't have a melee kiln cape.");
				break;
			case OPTION_2:
				stage = -1;
				if (player.getInventory().containsItem(23659, 1)) {
					player.getInventory().deleteItem(23659, 1);
					player.getInventory().addItem(31611, 1);
					sendNPCDialogue(npcId, NORMAL, "Thanks.");
				} else
					sendNPCDialogue(npcId, NORMAL,
							"You don't have a melee kiln cape.");
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}