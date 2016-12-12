package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Zeke extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCChat(HAPPY, "A thousand greetings, sir.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Do you want to trade?",
					"Nice cloak.", "Could you sell me a dragon scimitar?");
			stage = 0;
			break;

		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCChat(HAPPY, "Yes, certainly. I deal in scimitars.");
				stage = -3;
				break;
			case OPTION_2:
				sendPlayerChat(NORMAL, "Nice Cloak.");
				stage = 1;
				break;
			case OPTION_3:
				sendNPCChat(UNSURE, "A dragon scimitar? A DRAGON scimitar?");
				stage = 2;
				break;
			}
			break;

		case 1:
			sendNPCChat(HAPPY, "Thank you.");
			stage = -2;
			break;

		case 2:
			sendNPCChat(ANGRY, "No way, man!");
			stage = 3;
			break;

		case 3:
			sendNPCChat(NORMAL,
					"The banana-brained nitwits who make them would never "
							+ "dream of selling any to me.");
			stage = 4;
			break;

		case 4:
			sendNPCChat(NORMAL,
					"Seriously, you'll be a monkey's uncle before you'll ever "
							+ "hold a dragon scimitar.");
			stage = 5;
			break;

		case 5:
			sendPlayerChat(SAD, "Oh well, thanks anyways.");
			stage = 6;
			break;

		case 6:
			sendNPCChat(ASKING,
					"Perhaps you'd like to take a look at my stock?");
			stage = 7;
			break;

		case 7:
			sendOptionsDialogue(DEFAULT, "Yes please, Zeke.",
					"Not today, thank you.");
			stage = 8;
			break;

		case 8:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 36);
				break;
			case OPTION_2:
				sendPlayerChat(NORMAL, "Not today, thank you.");
				stage = -2;
				break;
			}
			break;

		case -2:
			end();
			break;

		case -3:
			end();
			ShopsHandler.openShop(player, 36);
			break;

		}
	}

	@Override
	public void finish() {

	}

}
