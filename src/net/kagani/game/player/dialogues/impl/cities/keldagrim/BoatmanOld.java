package net.kagani.game.player.dialogues.impl.cities.keldagrim;

import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;

public class BoatmanOld extends Dialogue {
	Player player;
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		player = (Player) parameters[1];
		sendNPCDialogue(npcId, 9827, "Welcome back, " + player.getDisplayName()
				+ ".");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendNPCDialogue(npcId, 9827,
					"Would you like to cross the River Kelda?");
			stage++;
		} else if (stage == 0) {
			sendOptionsDialogue("Choose an option", "Yes.", "No.");
			stage++;
		} else if (stage == 1) {
			switch (componentId) {
			case (OPTION_2):
				end();
				break;
			case (OPTION_1):
				// Teleport the player to the other side of the river (boat)
				if (player.getRegionId() == 11679) {
					player.setNextWorldTile(new WorldTile(2843, 10128, 0));
				}
				// Teleport the player to the other side of the river (cave
				// entrance)
				else {
					player.setNextWorldTile(new WorldTile(2888, 10224, 0));
				}
				end();
				break;
			}
		}
	}

	@Override
	public void finish() {
	}
}
