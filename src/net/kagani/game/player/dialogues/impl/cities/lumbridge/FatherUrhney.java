package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class FatherUrhney extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, ANGRY, "Go away! I'm meditating!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Well, that's friendly.",
					"I've come to reposses your house.",
					"Can you tell me about the crater?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, ANGRY, "I said go away!");
				stage = 1;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, SCARED, "On what grounds?");
				stage = 2;
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, SAD,
						"There were enough people clattering around my house",
						"before, and then Zamorak decides he wants a war camp",
						"right next to my hut.");
				stage = 8;
				break;
			}
			break;
		case 1:
			sendPlayerDialogue(NORMAL, "Okay, okay. Sheesh, what a grouch.");
			stage = 25;
			break;
		case 2:
			sendOptionsDialogue(DEFAULT,
					"Repeated failure to make mortgage repayments.",
					"I don't know, I just wanted this house.");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, ANGRY, "What?");
				stage = 4;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, ANGRY,
						"Oh, go away and stop wasting my time.");
				stage = 25;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, ANGRY,
					"But I don't have a mortgage - I built this house myself!");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(NORMAL,
					"Sorry, I must have got the wrong address. All the houses",
					"look the same around here.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, ANGRY,
					"What? What houses? This is the only one. What are you",
					"talking about?");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(NORMAL, "Never mind.");
			stage = 25;
			break;
		case 8:
			sendNPCDialogue(npcId, ANGRY,
					"At least Zamorak has gone now, but he's left his camp",
					"littering my swamp.");
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
