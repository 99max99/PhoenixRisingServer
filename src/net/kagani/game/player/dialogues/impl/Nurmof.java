package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Nurmof extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Greetings and welcome to my pickaxe shop. Do you want",
				"to buy my premium quality pickaxes?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, please.",
					"No, thank you.",
					"Are your pickaxes better than other pickaxes, then?");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 95);
				end();
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No, thank you.");
				stage = 50;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL,
						"Are your pickaxes better than other pickaxes, then?");
				stage = 2;
				break;
			}
		case 3:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Of course they are! My pickaxes are made of higher grade",
					"metal than your ordinary bronze pickaxes, allowing you to",
					"mine ore just that little bit faster.");
			stage = 50;
			break;
		case 50:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
