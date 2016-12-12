package net.kagani.game.player.dialogues.impl.cities.keldagrim;

import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;

public class BoatmanNew extends Dialogue {
	Player player;
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		player = (Player) parameters[1];
		sendNPCDialogue(npcId, 9827, "Hello, welcome to Keldagrim!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendNPCDialogue(npcId, 9827, "How may I help you?");
			stage++;
		} else if (stage == 0) {
			sendOptionsDialogue("Choose an option", "Who are you?",
					"May I cross the river?");
			stage++;
		} else if (stage == 1) {
			switch (componentId) {
			case (OPTION_1):
				sendNPCDialogue(
						npcId,
						9827,
						"I am the dwarven boatman of Keldagrim.  I can take players across the River Kelda and back.");
				stage++;
				break;
			case (OPTION_2):
				// Teleport the player to the other side of the river (boat)
				if (player.getRegionId() == 11679)
					player.setNextWorldTile(new WorldTile(2843, 10128, 0));
				// Teleport the player to the other side of the river (cave
				// entrance)
				else
					player.setNextWorldTile(new WorldTile(2888, 10224, 0));
				end();
				break;
			}
		} else
			end();
	}

	@Override
	public void finish() {
	}
}
