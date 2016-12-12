package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.content.PartyRoom;
import net.kagani.game.player.dialogues.Dialogue;

public class PartyRoomLever extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
				"Balloon Bonanza (1000 coins).", "Nightly Dance (500 coins).",
				"No action.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 2) {
			PartyRoom.purchase(player, true);
		} else if (componentId == 3) {
			PartyRoom.purchase(player, false);
		}
		end();
	}

	@Override
	public void finish() {

	}
}
