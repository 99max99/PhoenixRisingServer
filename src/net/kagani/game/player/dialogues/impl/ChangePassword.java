package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class ChangePassword extends Dialogue {

	/**
	 * @author: 99max99 M
	 */

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue("Open cpanel page to edit password?", "Yes, please.", "No thanks.");
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
				player.stopAll();
				player.getPackets()
						.sendOpenURL("https://MaxScape830.net/forums/index.php?app=core&module=usercp&tab=core&area=email");
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