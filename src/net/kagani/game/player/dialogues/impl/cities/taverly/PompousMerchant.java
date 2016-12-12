package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;

public class PompousMerchant extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, MILDLY_ANGRY, "Step aside, you low-born oaf!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(MILDLY_ANGRY, "Hey! Who are you calling names?");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I have no idea. Some 'hero' I would guess.");
			stage = 1;
			break;
		case 1:
			sendPlayerDialogue(MILDLY_ANGRY, "Look you...");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Hush. I have far to much money to listen to the idle",
					"prattle of menials. Begone!");
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
