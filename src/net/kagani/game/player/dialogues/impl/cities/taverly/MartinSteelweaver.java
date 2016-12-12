package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class MartinSteelweaver extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING, "Do you need smithing help?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "I need smithing supplies.",
					"What smithing recommendations do you have for me?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 170);
				break;
			case OPTION_2:
				/*
				 * Opens Challenge System
				 */
				stage = -2;
				break;
			}
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
