package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Gerrant extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Welcome! You can buy fishing equipment at my store.",
				"We'll also buy anything you catch off you.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Let's see what you've got then.",
					"Sorry, I'm not interested.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(BLANK, "Let's see what you've got then.");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(BLANK, "Sorry, I'm not interested.");
				stage = 50;
				break;
			}
			break;
		case 1:
			ShopsHandler.openShop(player, 31);
			end();
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
