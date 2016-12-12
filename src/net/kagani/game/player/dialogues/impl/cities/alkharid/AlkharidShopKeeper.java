package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class AlkharidShopKeeper extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCChat(ASKING, "Can I help you at all?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Yes please. What are you selling?",
					"No thanks.");
			stage = 0;
			break;

		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerChat(ASKING, "Yes please. What are you selling?");
				stage = -3;
				break;
			case OPTION_2:
				sendPlayerChat(NORMAL, "No thanks.");
				stage = -2;
				break;
			}
			break;

		case -3:
			end();
			ShopsHandler.openShop(player, 173);
			break;

		case -2:
			end();
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
