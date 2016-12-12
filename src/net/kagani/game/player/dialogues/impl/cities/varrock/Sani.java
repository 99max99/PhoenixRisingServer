package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class Sani extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(HAPPY, "Hello.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Hello! Welcome to my workshop. My name is Sani. Feel free",
					"to use any of my anvils, I can't use them all at once!");
			stage = 0;
			break;
		case 0:
			sendPlayerDialogue(HAPPY, "Thanks!");
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
