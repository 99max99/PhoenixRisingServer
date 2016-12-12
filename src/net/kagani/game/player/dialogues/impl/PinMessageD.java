package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class PinMessageD extends Dialogue {

	@Override
	public void start() {
		sendDialogue(new String[] { (String) this.parameters[0] });
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			end();
			player.closeInterfaces();
		}
	}

	@Override
	public void finish() {

	}
}
