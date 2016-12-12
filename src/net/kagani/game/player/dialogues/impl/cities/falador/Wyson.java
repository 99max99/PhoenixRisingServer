package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * @Author arrow
 * @Contact<arrowrsps@gmail.com;skype:arrowrsps>
 */
public class Wyson extends Dialogue {

	public int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCChat(NORMAL, "I'm the head gardener around here. "
				+ "If you're looking for woad leaves, or if you need help"
				+ " with owt, I'm yer man.");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Yes please, I need woad leaves.",
					"Sorry, but I'm not interested.");
			stage = 1;
			break;

		case 1:
			switch (componentId) {
			case OPTION_1:
				sendNPCChat(ASKING, "How much are you willing to pay?");
				stage = 2;
				break;
			case OPTION_2:
				sendNPCChat(NORMAL, "Fair enough.");
				stage = -2;
				break;
			}
			break;

		case 2:
			sendOptionsDialogue(DEFAULT, "How about 5 coins?",
					"How about 10 coins?", "How about 15 coins?",
					"How about 20 coins?");
			stage = 3;
			break;

		case 3:
			switch (componentId) {
			case OPTION_1:
				sendNPCChat(
						ANGRY,
						"No no, that's far too little. Woad leaves are hard to get."
								+ " I used to have plenty but someone kept stealing them"
								+ " off me.");
				stage = -2;
				break;
			case OPTION_2:
				sendNPCChat(
						ANGRY,
						"No no, that's far too little. Woad leaves are hard to get."
								+ " I used to have plenty but someone kept stealing them"
								+ " off me.");
				stage = -2;
				break;
			case OPTION_3:
				sendNPCChat(NORMAL, "Mmmm... okay, that sounds fair.");
				stage = 4;
				break;
			case OPTION_4:
				sendNPCChat(NORMAL, "Okay, that's more than fair.");
				stage = 5;
				break;
			}
			break;

		case 4:
			sendPlayerChat(HAPPY, "Thanks!");
			player.getInventory().addItem(1793, 1);
			stage = -2;
			break;

		case 5:
			sendNPCChat(HAPPY,
					"Here, have two since you are a generous person.");
			stage = 6;
			break;

		case 6:
			sendPlayerChat(HAPPY, "Thanks!");
			player.getInventory().addItem(1793, 2);
			stage = 7;
			break;

		case 7:
			sendNPCChat(HAPPY, "Come back if you have anymore gardening needs.");
			stage = -2;
			break;

		case -2:
			end();
			break;

		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}
