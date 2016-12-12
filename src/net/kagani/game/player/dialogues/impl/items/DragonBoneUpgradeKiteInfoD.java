package net.kagani.game.player.dialogues.impl.items;

import net.kagani.game.player.dialogues.Dialogue;

public class DragonBoneUpgradeKiteInfoD extends Dialogue {

	@Override
	public void start() {
		sendDialogue(
				"The dragonbone upgrade kit is compatible with the following objects(1/2)",
				"Infinity hat", "Infinity top", "Infinity bottoms",
				"Infinity gloves", "Infinity boots");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendDialogue("(2/2)", "Dragon full helm", "Dragon platebody",
					"Dragon plateskirt/platelegs", "Culinaromancer's gloves 9",
					"Dragon boots");
			break;
		case 0:
			end();
			break;
		}

	}

	@Override
	public void finish() {

	}

}
