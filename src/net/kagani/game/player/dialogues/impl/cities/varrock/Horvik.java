package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Horvik extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY, "Hello, do you need any help?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Do you want to trade?",
					"No, thanks. I'm just looking around.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 1);
				end();
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY,
						"No, thanks. I'm just looking around.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, HAPPY,
					"Well, come and see me if you're ever in need of armour!");
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
