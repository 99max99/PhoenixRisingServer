package net.kagani.game.player.dialogues.impl.cities.karamja;

import net.kagani.game.WorldTile;
import net.kagani.game.player.dialogues.Dialogue;

public class HardwoodDoors extends Dialogue {
	@Override
	public void start() {
		sendDialogue("You will need to pay 100 coins in order to enter.",
				"Are you sure?");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue("Are you sure?", "Yes.", "No.");
			break;
		case 0:
			if (componentId == OPTION_1) {
				if (player.getMoneyPouch().getCoinsAmount() > 100) {
					player.getMoneyPouch().sendDynamicInteraction(100, true);
					player.setNextWorldTile(new WorldTile(2817, 3083, 0));
					end();
				} else {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You need 100 coins to enter the Hardwood Grove.");
				}
			} else if (componentId == OPTION_2)
				end();
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void finish() {
	}

}