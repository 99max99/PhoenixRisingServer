package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class GemTrader extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL,
				"Good day to you, traveller. Would you be interested in",
				"buying some gems?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Yes, please.", "No, thank you.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 35);
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No, thank you.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, NORMAL, "Eh, suit yourself.");
			stage = -2;
			break;
		case -2:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
