package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class FightKilnDialogue extends Dialogue {

	@Override
	public void start() {
		player.lock();
		sendDialogue("You journey directly to the Kiln.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {
		player.getControlerManager().startControler("FightKilnControler", 0);
	}

}
