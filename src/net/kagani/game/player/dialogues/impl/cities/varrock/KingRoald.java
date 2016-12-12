package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class KingRoald extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Ask about the kingdom.",
				"Greet the king.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY,
						"Greetings, your majesty. How fares the kingdom?");
				stage = 0;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "Greetings, your majesty.");
				stage = 3;
				break;
			}
			break;
		case 0:
			sendNPCDialogue(npcId, HAPPY,
					"The kingdom is at peace again after the battle of",
					"Lumbridge! I'm glad to see Saradomin and Zamorak are",
					"both gone from there now.");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"I really hope that sort of thing doesn't happen again in my",
					"kingdom! There is a lot of tidy up!");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, NORMAL,
					"So as you can see I'm very busy right now. Please don't",
					"waste my time.");
			stage = 50;
			break;
		case 3:
			sendNPCDialogue(npcId, NORMAL,
					"Do you have anything of importance to say?");
			stage = 4;
			break;
		case 4:
			sendPlayerDialogue(NORMAL, "...Not really.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, NORMAL,
					"You will have to excuse me, I am very busy.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, NORMAL,
					"I must keep a fixed eye on Morytania to the east.");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(HAPPY, "Or both eyes, perhaps?");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Indeed, we cannot all be heroes, wandering from one crisis",
					"to another.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, UPSET,
					"Ever since Kara-Meir and her friends became famous,",
					"adventuring has become a career of choice amongst our",
					"youth. Yet, people forget that her tale is a cautionary",
					"one.");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(WORRIED, "Why? What happend to her?");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"She withheld important information about one of her",
					"associates, and it very nearly led to a disaster for all of us:",
					"every man, woman and child.");
			stage = 12;
			break;
		case 12:
			sendPlayerDialogue(WORRIED, "And?");
			stage = 13;
			break;
		case 13:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"And what? I have spent enough time on you, adventurer.",
					"You will have to excuse me, I have a kingdom to run.");
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
