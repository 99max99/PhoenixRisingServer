package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;

public class FoppishPierre extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(NORMAL, "Hello.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Oh, are you addressing...me?");
			stage = 0;
			break;
		case 0:
			sendPlayerDialogue(NORMAL, "Well, yes?");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, UNSURE, "Oh dear...This will never do.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING, "What are you talking about?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I was right. This will never do. I mean...look at yourself.");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"How, by any stretch of the imagination, am I supposed",
					"to...converse...with one such as yourself?");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(NORMAL,
					"Well, you could flap your mouth about while making words",
					"into a sentence.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, UNSURE,
					"Oh...you are so lower class. I've become used to dealing",
					"with better people than you since I became fabulously",
					"wealthy.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Thank goodness I have my pomander of expensive spices,",
					"your...odour was beginning to make me feel light-headed.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(MILDLY_ANGRY, "Hey!");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I'm sorry, I can't hear it when poor people are upset.",
					"Toodle-pip.");
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
