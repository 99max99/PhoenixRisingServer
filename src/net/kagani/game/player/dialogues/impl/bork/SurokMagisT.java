package net.kagani.game.player.dialogues.impl.bork;

import net.kagani.game.player.controllers.BorkController;
import net.kagani.game.player.dialogues.Dialogue;

public class SurokMagisT extends Dialogue {

	private BorkController bork;

	@Override
	public void start() {
		bork = (BorkController) parameters[0];
		sendPlayerDialogue(NORMAL, "That monk - he called to Zam");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = -2;
			sendPlayerDialogue(
					NORMAL,
					"What the? This power again! It must be Zamorak! I can't fight something this strong! I better loot what I can and get out of here!");
			break;
		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		bork.startEarthquake();
	}

}
