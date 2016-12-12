package net.kagani.game.player.dialogues.impl.construction;

import net.kagani.game.player.dialogues.Dialogue;

public class BuildD extends Dialogue {

	@Override
	public void start() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 55) { // close
			player.closeInterfaces();
			return;
		}
		player.getHouse().build((componentId - 8) / 7); // this one calls close
														// interface
	}

	@Override
	public void finish() {
	}

}
