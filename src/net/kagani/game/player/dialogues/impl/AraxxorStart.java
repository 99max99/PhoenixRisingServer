package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class AraxxorStart extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		stage = 1;
		sendDialogue("<col=800000>Beyond this point is the Araxyte hive.<br><col=800000>There is no way out other than death or Victory.<br><col=800000>Only those who can endure dangerious encounters should proceed.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendOptionsDialogue("What would you like to do?", "Start a fight.",
					"Join an existing fight.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				sendDialogue("Starting a fight against this foe cost 100,000 coins.");
				break;
			case OPTION_2:
				stage = -1;
				sendDialogue("Unable to find an existing fight. Please start a fight.");
				break;
			}
			break;
		case 3:
			stage = 4;
			sendOptionsDialogue("Pay 100,000 coins for a fight?", "Yes.", "No.");
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				if (player.hasMoney(100000)) {
					player.getControlerManager().startControler(
							"AraxxorControler", true, player);
					player.takeMoney(100000);
					end();
				} else {
					stage = -1;
					sendDialogue("You do not have enough coins.");
				}
				break;
			case OPTION_2:
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