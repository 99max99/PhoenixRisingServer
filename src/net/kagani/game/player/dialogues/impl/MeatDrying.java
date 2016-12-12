package net.kagani.game.player.dialogues.impl;

import net.kagani.game.WorldObject;
import net.kagani.game.item.Item;
import net.kagani.game.player.actions.Cooking;
import net.kagani.game.player.actions.Cooking.Cookables;
import net.kagani.game.player.dialogues.Dialogue;

public class MeatDrying extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("What would you like to do?", "Cook the meat.",
				"Dry the meat.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			player.getActionManager().setAction(
					new Cooking((WorldObject) this.parameters[0], new Item(
							2132, 1), 28,
							componentId == OPTION_1 ? Cookables.RAW_MEAT
									: Cookables.SINEW));
			end();
		}
	}

	@Override
	public void finish() {

	}
}
