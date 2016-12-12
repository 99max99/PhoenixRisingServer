package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Zaff extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Would you like to buy or sell some staves?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, please.",
					"No, thank you.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 546);
				end();
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No, thank you.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, HAPPY,
					"Well, 'stick' your head in again if you change your mind.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(HAPPY,
					"Huh, terrible pun. You just can't get the 'staff' these",
					"days!");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Actually, I have an assistant now, but he's a bit thin. You",
					"might even call him a bit of a beanpole!");
			stage = 4;
			break;
		case 4:
			sendPlayerDialogue(HAPPY, "*groans* Okay, I'm leaving now!");
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
