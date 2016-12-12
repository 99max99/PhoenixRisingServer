package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;

public class Scalectrix extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, SAD, "Oh dear...oh dear...");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(NORMAL, "Are you alright?");
			stage = 0;
			break;
		case 0:
			sendDialogue(
					"The young druidess seems intent on staring into the well and wringing her",
					"hands, and does not reply.");
			stage = 1;
			break;
		case 1:
			sendPlayerDialogue(NORMAL, "Uh...ok then! Have a nice day!");
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
