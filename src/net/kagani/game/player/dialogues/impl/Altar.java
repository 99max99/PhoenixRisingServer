package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class Altar extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Spellbook.",
				"Prayer Book.", "Nevermind.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				stage = 0;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Normal Spellbook.",
						"Ancient Spellbook.", "Lunar Spellbook.", "Nevermind.");
			} else if (componentId == OPTION_2) {
				stage = 1;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Normal.",
						"Curses.", "Nevermind.");
			} else {
				end();
			}
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				stage = 2;
				sendDialogue("Your mind clears and you switch",
						"back to the normal spellbook.");
				player.getCombatDefinitions().setSpellBook(0);
			} else if (componentId == OPTION_2) {
				stage = 2;
				sendDialogue("Your mind clears and you switch",
						"back to the ancient spellbook.");
				player.getCombatDefinitions().setSpellBook(1);
			} else if (componentId == OPTION_3) {
				stage = 2;
				sendDialogue("Your mind clears and you switch",
						"back to the lunar spellbook.");
				player.getCombatDefinitions().setSpellBook(2);
			} else {
				end();
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				stage = 2;
				sendDialogue("Your mind clears and you switch",
						"back to the normal prayer book.");
				player.getPrayer().setPrayerBook(false);
			} else if (componentId == OPTION_2) {
				stage = 2;
				sendDialogue("Your mind clears and you switch",
						"back to the curses prayer book.");
				player.getPrayer().setPrayerBook(true);
			} else {
				end();
			}
		} else if (stage == 2) {
			end();
		}
	}

	@Override
	public void finish() {

	}
}