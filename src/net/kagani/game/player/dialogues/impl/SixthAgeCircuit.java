package net.kagani.game.player.dialogues.impl;

import net.kagani.game.WorldTile;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.dialogues.Dialogue;

public class SixthAgeCircuit extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		sendOptionsDialogue("Choose your destination", "Guthix's Shine.",
				"Cancel.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1923,
					5987, 0));
			FadingScreen.unfade(player,
					FadingScreen.fade(player, FadingScreen.TICK / 2),
					new Runnable() {
						@Override
						public void run() {

						}
					});
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