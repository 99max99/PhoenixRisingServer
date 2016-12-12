package net.kagani.game.player.dialogues.impl.home;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Arianwyn extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Hello, " + player.getDisplayName() + "! May I interest you in some stock to assist you in your journey?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Show me your First skilling store.", "Show me your Second skilling store.",
					"Show me your first herb store.", "Show me your second herb store.", "No thanks.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 204);
				end();
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, 205);
				end();
				break;
		case OPTION_3:
				ShopsHandler.openShop(player, 206);
				end();
				break;
			case OPTION_4:
				ShopsHandler.openShop(player, 207);
				end();
				break;
			case OPTION_5:
				stage = 2;
				sendPlayerDialogue(NORMAL, "No thanks.");
				break;
			}
			break;
		case 2:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}