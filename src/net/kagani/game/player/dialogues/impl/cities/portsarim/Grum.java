package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Grum extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Would you like to buy or sell some gold jewellery?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Yes, please.",
					"No, I'm not that rich.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 32);
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "No, I'm not that rich.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"Get out, then! We don't want any riff-raff in here.");
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
