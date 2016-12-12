package net.kagani.game.player.dialogues.impl;

import net.kagani.game.minigames.fistofguthix.MinigameManager;
import net.kagani.game.player.controllers.FistOfGuthixControler;
import net.kagani.game.player.dialogues.Dialogue;

public class LeaveFistOfGuthix extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Really leave?", "Yes", "No");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			switch (componentId) {
			case OPTION_1:
				if ((FistOfGuthixControler) player.getControlerManager()
						.getControler() != null)
					((FistOfGuthixControler) player.getControlerManager()
							.getControler()).exit(true);
				if (MinigameManager.INSTANCE().fistOfGuthix().team(player) != null) {
					MinigameManager.INSTANCE().fistOfGuthix().team(player)
							.forfeit(player);
				}
				end();
				break;
			case OPTION_2:
				end();
				break;
			}
		}
	}

	@Override
	public void finish() {
	}

}
