package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.Settings;
import net.kagani.game.player.dialogues.Dialogue;

public class BlueMoonBartender extends Dialogue {

	public static final int BEER = 1917;

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL, "What can I do yer for?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(
					DEFAULT_OPTIONS_TITLE,
					"A glass of your finest ale please.",
					"Can you recommend where an adventurer might make his fortune?",
					"Do you know where I can get some good equipment?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "A glass of your finest ale please.");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL,
						"Can you recommend where an adventurer might make his",
						"fortune?");
				stage = 3;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL,
						"Do you know where I can get some good equipment?");
				stage = 11;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, HAPPY, "No problemo. That'll be 2 coins.");
			stage = 2;
			break;
		case 2:
			if (player.getInventory().containsItem(995, 2)) {
				player.getInventory().addItem(BEER, 1);
				end();
			} else {
				sendPlayerDialogue(UPSET,
						"Oh dear. I don't seem to have enough money.");
				stage = 50;
			}
			break;
		case 3:
			sendNPCDialogue(npcId, UNSURE,
					"Ooh I don't know if I should be giving away information,",
					"makes the computer game too easy.");
			stage = 4;
			break;
		case 4:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Oh ah well...",
					"Computer game? What are you talking about?",
					"Just a small clue?");
			stage = 5;
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "Oh ah well...");
				stage = 50;
				break;
			case OPTION_2:
				sendPlayerDialogue(CONFUSED,
						"Computer game? What are you talking about?");
				stage = 6;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Just a small clue?");
				stage = 10;
				break;
			}
			break;
		case 6:
			sendNPCDialogue(npcId, UNSURE,
					"This world around us... is a computer game... called",
					Settings.SERVER_NAME + ".");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(CONFUSED,
					"Nope, still don't understand what you are talking about.",
					"What's a computer?");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(
					npcId,
					UNSURE,
					"It's a sort of magic box thing, which can do all sorts of",
					"stuff.");
			stage = 9;
			break;
		case 9:
			sendPlayerDialogue(MILDLY_ANGRY,
					"I give up. You're obviously completely mad!");
			stage = 50;
			break;
		case 10:
			sendNPCDialogue(npcId, UNSURE,
					"Go and talk to the bartender at the Jolly Boar Inn, he",
					"doesn't seem to mind giving away clues.");
			stage = 50;
			break;
		case 11:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well, there's the sword shop across the road, or there's",
					"also all sorts of shops up around the market.");
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
