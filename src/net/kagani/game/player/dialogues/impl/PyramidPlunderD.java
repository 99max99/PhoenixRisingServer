package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.controllers.PyramidPlunder;
import net.kagani.game.player.dialogues.Dialogue;

public class PyramidPlunderD extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private PyramidPlunder pyramidPlunder;

	@Override
	public void start() {
		pyramidPlunder = (PyramidPlunder) parameters[0];
		stage = 1;
		sendOptionsDialogue("Really leave the pyramid?",
				"Yes, I'm out of here.",
				"Ah, I think I'll stay a little longer.");
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
				pyramidPlunder.reset();
				player.setNextWorldTile(pyramidPlunder.OUTSIDE);
				player.closeInterfaces();
				pyramidPlunder.removeControler();
				end();
				break;
			case OPTION_2:
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