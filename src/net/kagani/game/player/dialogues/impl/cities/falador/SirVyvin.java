package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;

public class SirVyvin extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(BLANK, "Hello.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Greetings traveller.");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT, "Do you have anything to trade?",
					"Why are there so many knights in this city?",
					"Can I just distract you for a minute?");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Do you have anything to trade?");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL,
						"Why are there so many knights in this city?");
				stage = 3;
				break;
			case OPTION_3:
				sendPlayerDialogue(
						BLANK,
						"Can I just talk to you very slowly for a few minutes, while",
						"I distract you, so that my friend over there can do",
						"something while you're busy being distracted by me.");
				stage = 4;
				break;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING, "No, I'm sorry.");
			stage = 50;
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"We are the White Knights of Falador. We are the most",
					"powerful order of knights in the land. We are helping the",
					"king Vllance rule the kingdom as he is getting old and",
					"tired.");
			stage = 50;
			break;
		case 4:
			sendNPCDialogue(npcId, CONFUSED, "... ...what?");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					CONFUSED,
					"I'm... not sure what you're asking me... you want to join",
					"the White Knights?");
			stage = 6;
			break;
		case 6:
			sendPlayerDialogue(HAPPY, "Nope. I'm just trying to distract you.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, CONFUSED, "... ... you are very odd.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(HAPPY, "So can I distract you some more?");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, CONFUSED,
					"... ...I don't think I want to talk to you anymore.");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(HAPPY, "Ok. My work here is done. 'Bye!");
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
