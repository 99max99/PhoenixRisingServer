package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class HammerCrate extends Dialogue {

	@Override
	public void start() {
		sendHandedItem(2347, "You take the hammer from the crate.");
		player.getInventory().addItem(2347, 1);
		stage = -1;

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;

		default:
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
