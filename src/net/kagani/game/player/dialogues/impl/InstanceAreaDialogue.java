package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class InstanceAreaDialogue extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Select an option", "Enter boss room",
				"Start a new session", "Start a new session (Hard mode)",
				"Join an existing fight", "Rejoin my previous battle");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {

	}

	@Override
	public void finish() {

	}
}
