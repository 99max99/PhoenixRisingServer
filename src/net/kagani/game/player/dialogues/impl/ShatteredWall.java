package net.kagani.game.player.dialogues.impl;

import net.kagani.game.WorldTile;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.dialogues.Dialogue;

public class ShatteredWall extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Leave the chambers.",
				"Go to Guthix's shrine.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			FadingScreen.unfade(player,
					FadingScreen.fade(player, FadingScreen.TICK / 2),
					new Runnable() {
						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(2702, 3372, 0));
						}
					});
			end();
			break;
		case OPTION_2:
			FadingScreen.unfade(player,
					FadingScreen.fade(player, FadingScreen.TICK / 2),
					new Runnable() {
						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(1923, 5987, 0));
						}
					});
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}