package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;

public class LongBowBen extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, DRUNK, "Arrr, matey!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Why are you called Longbow Ben?",
					"Have you got any quests I could do?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Why are you called Longbow Ben?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "Have you got any quests I could do?");
				stage = 7;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, SAD, "Arrr, that's a strange yarn.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I was to be marooned, ye see. A scurvy troublemaker had",
					"taken my ship, and he put me ashore on a little island.");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(NORMAL, "Gosh, how did you escape?");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Arrr, ye see, he made one mistake! Before he sailed away,",
					"he gave me a bow and one arrow so that I wouldn't have",
					"to die slowly.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"So I shot him and took my ship back.");
			stage = 6;
			break;
		case 6:
			sendPlayerDialogue(CONFUSED, "Right...");
			stage = 50;
			break;
		case 7:
			sendNPCDialogue(npcId, HAPPY,
					"Nay, but the barkeep hears most of the news around here.",
					"Perhaps ye should be asking him for a quest.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(HAPPY, "Thanks.");
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
