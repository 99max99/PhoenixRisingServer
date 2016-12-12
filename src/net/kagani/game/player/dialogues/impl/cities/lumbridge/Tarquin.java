package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Tarquin
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class Tarquin extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL,
				"Hello old bean. Is there something I can help you with?");
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
						"My name is Tarquin Marjoribanks.");
				stage = 1;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"It's really quite simple. Just walk down to that tree on the",
						"bank and chop it down.");
				stage = 12;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, NORMAL,
					"Id be suprised if you haven't already heard of me?");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(CONFUSED,
					"Why would I have heard of you Mr. Marjoribanks?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, ANGRY, "It's pronounced 'Marchbanks'!");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, NORMAL,
					"You should know of me because I am a member of the",
					"royal family of Misthalin!");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(CONFUSED, "Are you related to King Roald?");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, HAPPY, "Oh yes! Quite closely actually.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, NORMAL,
					"I'm his 4th cousin, once removed on his mothers side.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(CONFUSED,
					"Er... Okay. What are you doing here then?");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, NORMAL,
					"I'm canoeing on the river! It's enormous fun! Would you",
					"like to know how?");
			stage = 10;
			break;
		case 10:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes", "No");
			stage = 11;
			break;
		case 11:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"It's really quite simple. Just walk down to that tree on the",
						"bank and chop it down.");
				stage = 12;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No thanks.");
				stage = 25;
				break;
			}
			break;
		case 12:
			sendNPCDialogue(npcId, HAPPY,
					"When you have done that you can shape the log further",
					"with your hatchet to make a canoe.");
			stage = 13;
			break;
		case 13:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"My personal favourite is the Stable Dugout canoe. A finer",
					"craft you'll never see old bean!");
			stage = 14;
			break;
		case 14:
			sendNPCDialogue(npcId, HAPPY,
					"A Stable Dugout canoe will take you pretty much the",
					"length of the Lum river.");
			stage = 15;
			break;
		case 15:
			sendNPCDialogue(npcId, HAPPY, "Of course there are other canoes.");
			stage = 16;
			break;
		case 16:
			sendNPCDialogue(npcId, NORMAL,
					"Well ... erm. You seem to be able to make a Waka!");
			stage = 17;
			break;
		case 17:
			sendPlayerDialogue(HAPPY, "Sounds fun, what's a Waka.");
			stage = 18;
			break;
		case 18:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"I've only ever seen one man on the river who uses a Waka.",
					"A big, fearsome looking fellow up near Edgeville.");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(npcId, CONFUSED,
					"People say he was born in the Wilderness and that he is",
					"looking for a route back.");
			stage = 20;
			break;
		case 20:
			sendPlayerDialogue(CONFUSED, "Is that true!");
			stage = 21;
			break;
		case 21:
			sendNPCDialogue(npcId, NORMAL,
					"How should I know? I would not consort with such a base",
					"fellow!");
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
