package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

/**
 * The class that represents the dialogue for the NPC - Cassie
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class Cassie extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"I buy and sell shields; do you want to trade?");
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
				ShopsHandler.openShop(player, 45);
				end();
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 25:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
