package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The dialogue for the Gypsy Aris in Varrock.
 * 
 * @author Ethan Kyle Millard
 * @version 1.0 2/27/2015
 */
public class GypsyAris extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(
				npcId,
				HAPPY_FACE,
				"Our paths are destined to cross one day, "
						+ player.getDisplayName() + ", but",
				"today is not that day.");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			sendPlayerDialogue(ASKING_FACE, "How do you know my name?");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING_FACE,
					"I have the power to know, just as I have the power to see the future.");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(PLAIN_TALKING_FACE,
					"Well, that's in no way terrifying. I'll be going now!");
			stage = 100;
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
