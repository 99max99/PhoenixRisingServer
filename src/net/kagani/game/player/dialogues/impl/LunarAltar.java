package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class LunarAltar extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Change spellbooks?", "Yes, replace my spellbook.",
				"Never mind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				stage = 0;
				if (player.getCombatDefinitions().getSpellBook() != 2) {
					sendDialogue("Your mind clears and you switch",
							"back to the lunar spellbook.");
					player.getCombatDefinitions().setSpellBook(2);
				} else {
					sendDialogue("Your mind clears and you switch",
							"back to the normal spellbook.");
					player.getCombatDefinitions().setSpellBook(0);
				}
			} else
				end();
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
