package net.kagani.game.player.dialogues.impl.dungeoneering;

import net.kagani.game.player.controllers.DungeonController;
import net.kagani.game.player.dialogues.Dialogue;

public class DungeonLeave extends Dialogue {

	private DungeonController dungeon;

	@Override
	public void start() {
		dungeon = (DungeonController) parameters[0];
		sendOptionsDialogue("Leave the dungeon permanently?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1)
			dungeon.leaveDungeonPermanently();
		end();
	}

	@Override
	public void finish() {

	}

}
