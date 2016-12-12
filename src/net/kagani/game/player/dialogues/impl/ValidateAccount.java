package net.kagani.game.player.dialogues.impl;

import net.kagani.Settings;
import net.kagani.game.player.dialogues.Dialogue;

public class ValidateAccount extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		stage = 0;
		sendItemDialogues(
				15000,
				"Validate your account!",
				"Your account does not appear to have a forums account, please register one to keep your account secure.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 0:
			stage = 1;
			sendOptionsDialogue(
					"Open registration page to create this account?",
					"Yes, please.", "No thanks.");
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				player.stopAll();
				player.getPackets().sendOpenURL(Settings.REGISTER_LINK);
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