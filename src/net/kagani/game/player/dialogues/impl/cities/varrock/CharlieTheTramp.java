package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class CharlieTheTramp extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, UPSET, "Spare some change guv?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Who are you?",
					"Sorry, I haven't got any.", "Go get a job!",
					"Ok. Here you go.", "Is there anything down this alleyway?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Who are you?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(UPSET, "Sorry, I haven't got any.");
				stage = 4;
				break;
			case OPTION_3:
				sendPlayerDialogue(MILDLY_ANGRY, "Go get a job!");
				stage = 5;
				break;
			case OPTION_4:
				if (player.getInventory().containsItem(995, 1)) {
					sendPlayerDialogue(HAPPY, "Ok. Here you go.");
					player.getInventory().deleteItem(995, 1);
					stage = 6;
				} else {
					sendPlayerDialogue(UPSET,
							"Sorry, I don't have any money to spare.");
					stage = 4;
				}
				break;
			case OPTION_5:
				sendPlayerDialogue(NORMAL,
						"Is there anything down this alleyway?");
				stage = 11;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Charles. Charles E. Trampin' at your service. Now, about",
					"that change you were going to give me...");
			stage = 2;
			break;
		case 2:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Sorry, I haven't got any.", "Go get a job!",
					"Ok. Here you go.", "Is there anything down this alleyway?");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(UPSET, "Sorry, I haven't got any.");
				stage = 4;
				break;
			case OPTION_2:
				sendPlayerDialogue(MILDLY_ANGRY, "Go get a job!");
				stage = 5;
				break;
			case OPTION_3:
				if (player.getInventory().containsItem(995, 1)) {
					sendPlayerDialogue(HAPPY, "Ok. Here you go.");
					player.getInventory().deleteItem(995, 1);
					stage = 6;
				} else {
					sendPlayerDialogue(UPSET,
							"Sorry, I don't have any money to spare.");
					stage = 4;
				}
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL,
						"Is there anything down this alleyway?");
				stage = 11;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, UPSET, "Thanks anyway.");
			stage = 50;
			break;
		case 5:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"You startin? I hope your nose falls off!");
			stage = 50;
			break;
		case 6:
			sendNPCDialogue(npcId, HAPPY, "Hey, thanks a lot!");
			stage = 7;
			break;
		case 7:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "No problem.",
					"Don't I get some sort of quest hint or something now?");
			stage = 8;
			break;
		case 8:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "No problem.");
				stage = 50;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL,
						"Don't I get some sort of quest hint or something now?");
				stage = 9;
				break;
			}
			break;
		case 9:
			sendNPCDialogue(npcId, CONFUSED,
					"Huh? What do you mean? That wasn't why I asked you for",
					"money.");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(npcId, UPSET, "I just need to eat...");
			stage = 50;
			break;
		case 11:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Funny you should mention that...there is actually.");
			stage = 12;
			break;
		case 12:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"The ruthless and notorious criminal gang known as the",
					"Black Arm Gang have their headquarters down there.");
			stage = 13;
			break;
		case 13:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Thanks for the warning!",
					"Do you think they would let me join?");
			stage = 14;
			break;
		case 14:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Thanks for the warning!");
				stage = 15;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL,
						"Do you think they would let me join?");
				stage = 17;
				break;
			}
			break;
		case 15:
			sendNPCDialogue(npcId, HAPPY, "Don't worry about it.");
			stage = 16;
			break;
		case 16:
			sendPlayerDialogue(NORMAL, "Do you think they would let me join?");
			stage = 17;
			break;
		case 17:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You never know. You'll find a lady down there called",
					"Katrine. Speak to her.");
			stage = 18;
			break;
		case 18:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"But don't upset her, she's pretty dangerous.");
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
