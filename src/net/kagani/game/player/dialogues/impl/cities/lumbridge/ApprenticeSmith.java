package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class ApprenticeSmith extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(NORMAL,
				"Can you teach me the basics of smelting please?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"You'll need to have mined some ore to smelt first. Go"
							+ " see the mining tutor to the south if you're not sure how to do this.");
			stage = 0;
			break;
		case 0:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
