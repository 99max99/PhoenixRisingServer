package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class AlfredStonemason extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Hello there. Can I help you with something?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "I need construction supplies.",
					"Do you have any construction recommendations?",
					"How can I get my own house?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 166);
				end();
				break;
			case OPTION_2:
				end();
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
