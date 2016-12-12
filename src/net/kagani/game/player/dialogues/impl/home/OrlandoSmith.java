package net.kagani.game.player.dialogues.impl.home;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class OrlandoSmith extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL, "Hello, " + player.getDisplayName()
				+ ". May I interest you in some starting supplies?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes please.",
					"No thanks.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 175);
				end();
				break;
			case OPTION_2:
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
