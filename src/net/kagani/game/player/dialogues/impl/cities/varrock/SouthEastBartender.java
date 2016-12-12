package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class SouthEastBartender extends Dialogue {

	public static final int BEER = 1917;

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(NORMAL, "Hello.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, NORMAL,
					"Good day to you, brave adventurer. Can I get you a",
					"refreshing beer?");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes please!",
					"No thanks.", "How much?");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Yes please!");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No thanks.");
				stage = 4;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "How much?");
				stage = 5;
				break;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, NORMAL,
					"Ok then, that's two gold coins please.");
			stage = 3;
			break;
		case 3:
			if (player.getInventory().containsItem(995, 2)) {
				sendNPCDialogue(npcId, NORMAL, "Cheers!");
				player.getInventory().deleteItem(995, 2);
				player.getInventory().addItem(BEER, 1);
				stage = 50;
			} else {
				end();
				player.getPackets().sendGameMessage(
						"You don't have enough gold!");
			}
			break;
		case 4:
			sendNPCDialogue(npcId, CONFUSED,
					"Let me know if you change your mind.");
			stage = 50;
			break;
		case 5:
			sendNPCDialogue(npcId, NORMAL,
					"Two gold pieces a pint. So, what do you say?");
			stage = 6;
			break;
		case 6:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes please!",
					"No thanks.");
			stage = 7;
			break;
		case 7:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Yes please!");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No thanks.");
				stage = 4;
				break;
			}
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
