package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class PortSarimBrian extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue(DEFAULT, "So, are you selling something?", "'Ello.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING,
						"So, are you selling something?");
				stage = 0;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "'Ello.");
				stage = 2;
				break;
			}
			break;
		case 0:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yep, take a look at these great axes.");
			stage = 1;
			break;
		case 1:
			end();
			ShopsHandler.openShop(player, 33);
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING, "'Ello.");
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
