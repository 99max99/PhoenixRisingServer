package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class JackOval extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"You'd be suprised how much the people of Burthorpe rely",
				"on crafting goods. Want to give it a try?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "I need crafting supplies.",
					"I need you to tan some leather for me.",
					"I want to improve my crafting skill.",
					"Tell me more about crafting.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 168);
				break;
			case OPTION_2:
				/*
				 * Tan's All Cowhide in Inventory into Leather
				 */
				sendHandedItem(1741, "Jack tans your cow hides into leather.");
				stage = -2;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"You need to make things. Leather. Pottery. Every piece",
						"you make will increase your skill.");
				stage = -2;
				break;
			case OPTION_4:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Crafting is the most versatile skill by far. You can make",
						"leather armour, pottery, jewellery and even glasswork.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"If you ever get as good as me, you'll be able to perform",
					"extraordinary feats like making armour from the hides of",
					"dragons!");
			stage = -1;
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
