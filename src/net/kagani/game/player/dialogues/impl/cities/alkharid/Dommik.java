package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Dommik extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCChat(ASKING, "Would you like to buy some crafting equipment?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT,
					"No thanks; I've got all the Crafting equipment I need.",
					"Let's see what you've got, then.");
			stage = 1;
			break;

		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerChat(NORMAL,
						"No thanks; I've got all the Crafting equipment I need.");
				stage = 0;
				break;
			case OPTION_2:
				sendPlayerChat(NORMAL, "Let's see what you've got, then.");
				stage = -3;
				break;
			}
			break;

		case 0:
			sendNPCChat(NORMAL, "Okay. Fare well on your travels.");
			stage = -2;
			break;

		case -2:
			end();
			break;

		case -3:
			end();
			ShopsHandler.openShop(player, 13);
			break;

		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
