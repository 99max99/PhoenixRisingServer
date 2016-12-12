package net.kagani.game.player.dialogues.impl.home;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Ozan extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY, "Hello, " + player.getDisplayName()
				+ "! Which skillcape accessory would you like to purchase?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Show me the skillcape hoods.",
					"Show me the un-trimmed skillcapes.",
					"Show me the trimmed skillcapes.",
					"Show me the expert hoods & capes.",
					"Show me the master capes.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 177);
				end();
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, 178);
				end();
				break;
			case OPTION_3:
				ShopsHandler.openShop(player, 179);
				end();
				break;
			case OPTION_4:
				ShopsHandler.openShop(player, 197);
				end();
				break;
			case OPTION_5:
				ShopsHandler.openShop(player, 180);
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
