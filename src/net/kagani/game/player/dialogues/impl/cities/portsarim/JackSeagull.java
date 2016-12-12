package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;

public class JackSeagull extends Dialogue {

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
			sendOptionsDialogue(DEFAULT, "What are you doing here?",
					"Have you got any quests I could do?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "What are you doing here?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "Have you got any quests I could do?");
				stage = 3;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, DRUNK, "Drinking.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(BLANK, "Fair enough.");
			stage = 50;
			break;
		case 3:
			sendNPCDialogue(npcId, HAPPY,
					"Nay, but the barkeep hears most of the news around here.",
					"Perhaps ye should be asking him for a quest.");
			stage = 4;
			break;
		case 4:
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
