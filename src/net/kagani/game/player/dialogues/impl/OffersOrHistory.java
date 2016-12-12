package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.dialogues.Dialogue;

public class OffersOrHistory extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Show all offers.",
				"Show my history.", "Items that buys/sells instantly.",
				"Nevermind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				GrandExchange.showOffers(player);
				end();
				break;
			case OPTION_2:
				player.getGeManager().openHistory();
				end();
				break;
			case OPTION_3:
				GrandExchange.showInstantOffers(player);
				end();
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}