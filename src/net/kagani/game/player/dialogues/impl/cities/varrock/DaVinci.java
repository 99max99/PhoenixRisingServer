package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class DaVinci extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, MILDLY_ANGRY,
				"Bah! A great artist such as myself should not have to",
				"suffer the HUMILIATION of spending time on these",
				"dreadful worlds where non-members wander everywhere.");
		stage = 50;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
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
