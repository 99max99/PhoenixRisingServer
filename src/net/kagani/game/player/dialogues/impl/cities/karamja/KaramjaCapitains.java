package net.kagani.game.player.dialogues.impl.cities.karamja;

import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.dialogues.Dialogue;

public class KaramjaCapitains extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(
				npcId,
				9827,
				"Do you want to go on a trip to Port Sarim?</br>The trip will cost you 30 coins.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Yes, please.", "No, thank you.");
			stage = 0;
			break;
		case 0:
			if (componentId == OPTION_1) {
				if (!(player.getMoneyPouch().getCoinsAmount() < 30)) {
					sendItemDialogue(995,
							"You need 30 coins to travel to Port Sarim.");
					stage = 6;
				} else {
					player.getInventory().removeItemMoneyPouch(
							new Item(995, 30));
					player.useStairs(-1, new WorldTile(3029, 3217, 0), 0, 1);
					end();
				}
			} else if (componentId == OPTION_2) {
				end();
			}
			break;
		case 6:
			end();
			break;
		}
	}

	@Override
	public void finish() {
	}
}