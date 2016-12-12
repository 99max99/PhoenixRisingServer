package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;

public class CaptainDalbur extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue(DEFAULT, "Can you take me on the glider?",
				"What do you think of Ali Morrisane?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, HAPPY, "Certainly.");
				stage = 0;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL,
						"What do you think of Ali Morrisane?");
				stage = 1;
				break;
			}
			break;
		case 0:
			/*
			 * Opens Glider Interface
			 */
			end();
			break;
		case 1:
			sendNPCDialogue(npcId, NORMAL,
					"Oh, he's always up to something - like that magic carpet",
					"business.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(CONFUSED, "What magic carpet business?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, NORMAL,
					"He has set up a magic carpet network, like our gliders.");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(
					npcId,
					MILDLY_ANGRY,
					"If he ever gets it set up outside of the desert, we gnomes",
					"won't be happy with him. Especially not King Narnode.");
			stage = -2;
			break;
		case -2:
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
