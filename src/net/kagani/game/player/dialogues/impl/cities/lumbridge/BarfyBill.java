package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - BarfyBill
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class BarfyBill extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL, "Oh! Hello there.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Who are you?",
					"Can you teach me about canoeing?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, NORMAL,
						"My name is Ex Sea Captain Barfy Bill.");
				stage = 1;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						NORMAL,
						"It's really quite simple. Just walk down to that tree on the",
						"bank and chop it down.");
				stage = 13;
				break;
			}
			break;
		case 1:
			sendPlayerDialogue(CONFUSED, "Ex sea captain?");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, UPSET,
					"Yeah, I bought a lovely ship and was planning to make a",
					"fortune running her as a merchant vessel.");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(CONFUSED, "Why are you not still sailing?");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, UPSET,
					"Chronic sea sickness. My first, and only, voyage was",
					"spent dry heaving over the rails.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, NORMAL,
					"If I had known about the sea sickness I could have saved",
					"myself a lot of money.");
			stage = 6;
			break;
		case 6:
			sendPlayerDialogue(CONFUSED, "What are you up to now then?");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Well my ship had a little fire related problem. Fortunately",
					"it was well insured.");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(npcId, NORMAL,
					"Anyway, I don't have to work anymore so I've taken to",
					"canoeing on the river.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, HAPPY, "I don't get river sick!");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(npcId, CONFUSED,
					"Would you like to know how to make a canoe?");
			stage = 11;
			break;
		case 11:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes", "No");
			stage = 12;
			break;
		case 12:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						NORMAL,
						"It's really quite simple. Just walk down to that tree on the",
						"bank and chop it down.");
				stage = 13;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No thanks");
				stage = 25;
				break;
			}
			break;
		case 13:
			sendNPCDialogue(npcId, NORMAL,
					"When you have done that you can shape the log further",
					"with your hatchet to make a canoe.");
			stage = 14;
			break;
		case 14:
			sendNPCDialogue(npcId, HAPPY,
					"Ho! You look like you know which end of a hatchet is",
					"which!");
			stage = 15;
			break;
		case 15:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"You can easily build one of those Wakas. Be careful if you",
					"travel into the Wilderness though.");
			stage = 16;
			break;
		case 16:
			sendNPCDialogue(npcId, SCARED,
					"I've heard tell of great evil in that blasted wasteland.");
			stage = 17;
			break;
		case 17:
			sendPlayerDialogue(NORMAL, "Thanks for the warning Bill.");
			stage = 25;
			break;
		case 25:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
