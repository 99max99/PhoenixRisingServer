package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;

public class StankyMorgan extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(HAPPY, "Good day.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Is it, indeed.");
			stage = 0;
			break;
		case 0:
			sendPlayerDialogue(NORMAL, "Erm...");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, PLAIN_TALKING, "What's so good about it?");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(NORMAL, "Uh...");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(3509, BLANK, "Over here, lad.");
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
