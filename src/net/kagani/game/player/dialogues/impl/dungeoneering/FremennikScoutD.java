package net.kagani.game.player.dialogues.impl.dungeoneering;

import net.kagani.game.player.actions.Fletching;
import net.kagani.game.player.content.dungeoneering.rooms.PuzzleRoom;
import net.kagani.game.player.content.dungeoneering.rooms.puzzles.FremennikCampRoom;
import net.kagani.game.player.dialogues.Dialogue;

public class FremennikScoutD extends Dialogue {

	@Override
	public void start() {
		PuzzleRoom room = (PuzzleRoom) parameters[0];
		if (room.isComplete()) {
			sendNPCDialogue(
					FremennikCampRoom.FREMENNIK_SCOUT,
					NORMAL,
					"Wonderful! That was the last of them. As promised, I'll unlock the door for you.");
			stage = 100;
		} else {
			sendNPCDialogue(FremennikCampRoom.FREMENNIK_SCOUT, NORMAL,
					"Need some tools?");
			stage = 1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (!player.getInventory().containsItemToolBelt(
					Fletching.DUNGEONEERING_KNIFE)) {
				player.getInventory().addItem(Fletching.DUNGEONEERING_KNIFE, 1);
			}
			stage = 100;
		}
		if (stage == 100) {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
