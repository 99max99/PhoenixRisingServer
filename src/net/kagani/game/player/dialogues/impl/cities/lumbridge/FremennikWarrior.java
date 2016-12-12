package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class FremennikWarrior extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"You! You look like a capable adventurer. We need your",
				"help.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "What do you need?",
					"Why do you need me?", "Sounds good.",
					"Sorry, I'm too busy.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"My Fremennik clansmen and I have embarked on a test of",
						"skill and combat in the far north.");
				stage = 1;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"You seem like a man with fire in his heart.");
				stage = 5;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Excellent! Just look for the jetty nearby. There, a boat",
						"will take you to your destination.");
				stage = 25;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "Sorry, I'm to busy.");
				stage = 9;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, SAD,
					"Unfortunately, it's proving more of a challenge than we",
					"had anticipated.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"They need reinforcements to explore the dungeons there:",
					"to fight, or, if not, to provide support for those who do.");
			stage = 3;
			break;
		case 3:
			sendOptionsDialogue(DEFAULT, "Why do you need me?",
					"How dangerous is this?", "Sounds good.",
					"Sorry, I'm too busy.");
			stage = 4;
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"You seem like a man with fire in his heart.");
				stage = 5;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"There are challenges for adventurers of all skill-levels.");
				stage = 6;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Excellent! Just look for the jetty nearby. There, a boat",
						"will take you to your destination.");
				stage = 25;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "Sorry, I'm to busy.");
				stage = 9;
				break;
			}
			break;
		case 5:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"A thirst for advnture, am I wrong?");
			stage = 3;
			break;
		case 6:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"My clansmen can explain more when you arrive.");
			stage = 7;
			break;
		case 7:
			sendOptionsDialogue(DEFAULT, "Sounds good!", "Why do you need me?",
					"Sorry, I'm to busy.");
			stage = 8;
			break;
		case 8:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Excellent! Just look for the jetty nearby. There, a boat",
						"will take you to your destination.");
				stage = 25;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"You seem like a man with fire in his heart.");
				stage = 5;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Sorry, I'm to busy.");
				stage = 9;
				break;
			}
			break;
		case 9:
			sendNPCDialogue(npcId, SAD,
					"Suit yourself. We'll be here if you change your mind.");
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
