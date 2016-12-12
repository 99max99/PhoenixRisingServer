package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class RemovePIND extends Dialogue {

	private boolean removing;

	@Override
	public void start() {
		removing = (boolean) this.parameters[0];
		sendOptionsDialogue("Would you like to "
				+ (removing ? "remove" : "keep") + " your current PIN?",
				"Yes.", "No.");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				if (removing) {
					player.getBank().setRecoveryTime(
							System.currentTimeMillis() + (3 * 86400000));
					sendDialogue("Your bank PIN will be reset in three days. Use your current PIN until then.");
				} else {
					sendDialogue("Your bank PIN will no longer reset.");
					player.getBank().setRecoveryTime(-1);
					player.closeInterfaces();
				}
				stage = 0;
			} else {
				end();
			}
		} else if (stage == 0) {
			end();
			player.closeInterfaces();
		}
	}

	@Override
	public void finish() {

	}
}
