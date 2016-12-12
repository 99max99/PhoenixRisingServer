package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The dialogue for the Market Guard in Varrock.
 * 
 * @author Ethan Kyle Millard
 * @version 1.0 2/27/2015
 */
public class MarketGuard extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY_FACE, "Greetings, citizen.");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			sendPlayerDialogue(HAPPY_FACE, "Good day to you.");
			stage = 2;
			break;
		case 2:
			sendOptionsDialogue("Select and Option",
					"How's everything going today?",
					"I'll let you get on with it, then.");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(ASKING_FACE, "How's everything going today?");
				stage = 5;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY_FACE,
						"I'll let you get on with it, then.");
				stage = 4;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, HAPPY_FACE, "Stay safe, citizen.");
			stage = 100;
			break;
		case 5:
			sendNPCDialogue(npcId, HAPPY_FACE,
					"Fairly uneventful so far. Varrock's not the hotbed of",
					"crime that Ardougne is, after all.");
			stage = 100;
			break;
		case 6:
			sendPlayerDialogue(HAPPY_FACE, "I'll let you get on with it, then.");
			stage = 4;
			break;
		case 100:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}
