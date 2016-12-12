package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Betty extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY, "Welcome to the magic emporium.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Can I see your wares?",
					"Sorry, I'm not into Magic.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 34);
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "Sorry, I'm not into Magic.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, HAPPY,
					"Well, if you see anyone who is into Magic, please send",
					"them my way.");
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
