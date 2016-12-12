package net.kagani.game.player.dialogues.impl.home;

import net.kagani.game.player.content.Drinkables;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Jatix extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY, "Hello, " + player.getDisplayName()
				+ "! May I interest you in some herblore supplies?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, please.",
					"Decant my potions.", "No thanks.");
			stage = 1;
			break;
		case 0:
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 186);
				end();
				break;
			case OPTION_2:
				stage = 0;
				sendNPCDialogue(npcId, HAPPY, "There you go.");
				Drinkables.decantPotsInv(player);
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