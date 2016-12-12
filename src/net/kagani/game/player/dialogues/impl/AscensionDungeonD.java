package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.controllers.AscensionDungeon;
import net.kagani.game.player.controllers.AscensionDungeon.AscDoors;
import net.kagani.game.player.dialogues.Dialogue;

public class AscensionDungeonD extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private AscDoors door;

	private String name;

	private boolean leave, quick;

	@Override
	public void start() {
		door = (AscDoors) parameters[0];
		name = (String) parameters[1];
		leave = (boolean) parameters[2];
		quick = (boolean) parameters[3];
		if (leave && quick == false) {
			if (player.getInventory().containsItem(
					AscensionDungeon.getKey(door.ordinal()), 1)) {
				stage = 1;
				sendOptionsDialogue(
						"You have another keystone for this room.<br>Do you wish to restart?",
						"Yes.", "No, I want to leave.");
			} else {
				stage = 1;
				sendOptionsDialogue("Exit the laboratory?", "Yes.", "Not yet.");
			}
			return;
		} else if (quick) {
			AscensionDungeon.exit(player, door.ordinal(), false, false);
			return;
		}
		stage = 0;
		sendDialogue("The legiones are significantly more dangerious than the other Ascended. Make sure you are prepared before enterting the labs.");
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
					"Are you sure you want to enter?<br>This will use up your keystone!",
					"Yes, I am ready.", "No, I need to prepare.");
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				AscensionDungeon.enter(player, door.ordinal());
				end();
				break;
			case OPTION_2:
				if (leave && quick == false)
					AscensionDungeon.exit(player, door.ordinal(), false, false);
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