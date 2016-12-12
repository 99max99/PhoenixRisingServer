package net.kagani.game.player.dialogues.impl;

import net.kagani.Settings;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class OpenURLPrompt extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private String sub;

	@Override
	public void start() {
		sub = (String) parameters[0];
		sendOptionsDialogue("Open '" + Utils.fixChatMessage(sub) + "' page?", "Yes, please.", "No thanks.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			player.getPackets().sendOpenURL(Settings.WEBSITE_LINK + "/" + sub);
			end();
			break;
		case OPTION_2:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}