package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Magestix extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"If you want to know how to bring forth magical creatures",
				"from the ether, you've come to the right place!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "I need summoning supplies.",
					"I want to train summoning.",
					"Tell me more about summoning.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 162);
				end();
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"You need to create pouches, by combining reagents with",
						"spirit shards.");
				stage = 50;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"There is another world, besides this one. That world is",
						"filled with wondrous creatures, great and small.",
						"Summoning is the art of drawing those creatures forth to",
						"server you.");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"By combining rare reagents with our spirit shards, you can",
					"create pouches to perform this act.");
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
