package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Lowe extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL,
				"Welcome to Lowe's Archery Emporium. Do you want to see",
				"my wares?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, please.",
					"No, I prefer to bash things close up.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 550);
				end();
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL,
						"No, I prefer to bash things close up.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, NORMAL, "Humph, philistine.");
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
