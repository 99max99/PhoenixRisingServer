package net.kagani.game.player.dialogues.impl.actionBar;

import net.kagani.game.player.dialogues.Dialogue;

public class SetupActionBar extends Dialogue {

	@Override
	public void start() {
		sendDialogue("This will overwrite your current action bar with a set of suggested abilities based on the weapons you're wielding.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("Overwrite your current action bar?", "Yes.",
					"No.");

		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				if (player.getEquipment().getWeaponId() == -1) {
					stage = 1;
					sendDialogue("You do not have a weapon equipped. Please equip a weapon in your main hand and try again.");
					return;
				}
				player.getActionbar().setupBar();
				end();
			}
		} else if (stage == 1)
			end();
	}

	@Override
	public void finish() {

	}

}
