package net.kagani.game.player.dialogues.impl.bork;

import net.kagani.game.player.controllers.BorkController;
import net.kagani.game.player.dialogues.Dialogue;

public class SurokMagis extends Dialogue {

	private int npcId;
	private BorkController bork;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		bork = (BorkController) parameters[1];
		if (player.getLastBork() == 0) {
			sendPlayerDialogue(NORMAL, "Oh boy...");
			stage = -2;
		} else
			sendNPCDialogue(
					npcId,
					MAD,
					"Our Lord Zamorak has power over life and death, "
							+ player.getDisplayName()
							+ "! He has seen fit to resurrect Bork to continue his great work...and now you will fall before him!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = -2;
			sendPlayerDialogue(NORMAL, "Uh-oh! Here we go again.");
			break;
		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		bork.startFight();
	}
}