package net.kagani.game.player.dialogues.impl.dungeoneering;

import net.kagani.game.player.content.dungeoneering.rooms.puzzles.UnhappyGhostRoom;
import net.kagani.game.player.dialogues.Dialogue;

public class UnhappyGhostD extends Dialogue {

	@Override
	public void start() {
		UnhappyGhostRoom puzzle = (UnhappyGhostRoom) parameters[0];
		sendNPCDialogue(UnhappyGhostRoom.GHOST, puzzle.isComplete() ? NORMAL
				: SAD, "Woooo wooooo woooooooo wooo");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}

}
