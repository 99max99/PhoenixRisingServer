package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class HeadFarmerJones extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"The war effort in Burthorpe takes a lot of food. With the",
				"farming skill you'll be able to grow crops to help out.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "I want to train farming.",
					"I need farming supplies.", "Tell me more about farming.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Plant as many crops as ya can, as often as ya can. It's",
						"only through practice that you'll improve.");
				stage = -2;
				break;
			case OPTION_2:
				end();
				ShopsHandler.openShop(player, 167);
				break;
			case OPTION_3:
				break;
			}
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
