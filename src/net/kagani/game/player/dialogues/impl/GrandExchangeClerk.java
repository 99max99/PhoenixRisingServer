package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.dialogues.Dialogue;

public class GrandExchangeClerk extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (player.isAnIronMan()) {
			stage = 17;
			sendNPCDialogue(npcId, 9827,
					"You are an " + player.getIronmanTitle(true)
							+ ", you stand alone.");
			return;
		}
		sendNPCDialogue(npcId, 9827, "Good day, How may I help you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("What would you like to say?",
					"I'd like to acess the grand exchange, please.",
					"I'd like to see my collection box.", "Show all offers.",
					"What is my net worth?", "Nothing.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.getGeManager().openGrandExchange();
				end();
			} else if (componentId == OPTION_2) {
				player.getGeManager().openCollectionBox();
				end();
			} else if (componentId == OPTION_3) {
				GrandExchange.showOffers(player);
				end();
			} else if (componentId == OPTION_4) {
				stage = 15;
				sendPlayerDialogue(9827, "What is my net worth?");
			} else
				end();
		} else if (stage == 15) {
			player.getWealth();
			end();
		} else if (stage == 17) {
			end();
		} else
			end();
	}

	@Override
	public void finish() {

	}
}