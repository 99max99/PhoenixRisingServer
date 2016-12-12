package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class GuidorsWife extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(PLAIN_TALKING, "Hello.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, UPSET,
					"Oh hello, I can't chat now. I have to keep an eye on my",
					"husband. He's very ill!");
			stage = 0;
			break;
		case 0:
			sendPlayerDialogue(UPSET, "I'm sorry to hear that!");
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
