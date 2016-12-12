package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Poletax extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Burthorpe needs every advantage it can get and the",
				"power of herblore is quite an advantage. Would you like to",
				"learn its secrets?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "I need herblore supplies.",
					"I want to train herblore.", "Tell me more about herblore.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 165);
				end();
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Every stage in the brewing of a potion will give you a little",
						"practise. You can also start out by cleaning herbs for use.");
				stage = 50;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Every natural plant has powers locked within. Herblore is",
						"the art of unlocking those powers through potion-making.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"You'll be able to brew potions to do just about anything.",
					"Most significantly, you can restore yourself when you feel",
					"depleted.");
			stage = -1;
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
