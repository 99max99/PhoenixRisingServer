package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class LouieLegs extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCChat(ASKING, "Hey, wanna buy some armour?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "What have you got?", "No, thank you.");
			stage = 0;
			break;

		case 1:
			switch (componentId) {
			case OPTION_1:
				sendNPCChat(NORMAL,
						"I provide items to help you keep your legs!");
				stage = -3;
				break;
			case OPTION_2:
				sendPlayerChat(NORMAL, "No, thank you.");
				stage = -2;
				break;
			}
			break;

		case -3:
			end();
			ShopsHandler.openShop(player, 37);
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
