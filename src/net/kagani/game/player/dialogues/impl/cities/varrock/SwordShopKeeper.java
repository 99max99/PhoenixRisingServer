package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class SwordShopKeeper extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Hello, bold adventurer. Can I interest you in some swords?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, please.",
					"No, I'm okay for swords right now.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 554);
				end();
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"No, I'm okay for swords right now.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Come back if you need any.");
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
