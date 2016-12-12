package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;

public class PortSarimBartender extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(HAPPY, "Good day to you!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, HAPPY, "Hello there!");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT, "Could I buy a beer, please?",
					"Bye, then.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Could I buy a beer, please?");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(BLANK, "Bye, then.");
				stage = 5;
				break;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, HAPPY,
					"Sure, that will be two gold coins, please.");
			stage = 3;
			break;
		case 3:
			if (player.getInventory().containsItem(995, 2)) {
				sendPlayerDialogue(HAPPY, "Okay, here you go.");
				stage = 4;
			} else {
				sendPlayerDialogue(SAD, "I don't have enough coins.");
				stage = 50;
			}
			break;
		case 4:
			sendDialogue("You buy a pint of beer.");
			player.getInventory().deleteItem(995, 2);
			player.getInventory().addItem(1917, 1);
			stage = 50;
			break;
		case 5:
			sendNPCDialogue(npcId, HAPPY, "Come back soon!");
			stage = 50;
			break;
		case 50:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
