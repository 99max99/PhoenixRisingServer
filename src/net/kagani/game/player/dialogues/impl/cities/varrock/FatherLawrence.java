package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class FatherLawrence extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Oh to be a father in the times of whiskey.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, HAPPY,
					"I sing and I drink and I wake up in gutters.");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, UPSET,
					"To err is human, to forgive, quite difficult.");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, UPSET, "I need a think I drink.");
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
