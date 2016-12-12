package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class Clive extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(HAPPY, "Hello, I'm " + player.getDisplayName()
				+ ". Who are you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"One does not normally talk to peasants.");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"But I'm a famous adventurer.", "Oh, okay. Goodbye.",
					"Then one is a snob.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "But I'm a famous adventurer.");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Oh, okay. Goodbye.");
				stage = 50;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Then one is a snob.");
				stage = 3;
				break;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Nope, never heard of you. Please leave.");
			stage = 50;
			break;
		case 3:
			sendNPCDialogue(npcId, MILDLY_ANGRY, "Pah! Such insolence.");
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
