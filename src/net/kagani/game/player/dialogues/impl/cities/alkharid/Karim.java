package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * 
 * @author Frostbite
 *
 */

public class Karim extends Dialogue {

	/*
	 * Represents Kebab itemId
	 */
	public int KEBAB = 1971;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCChat(ASKING,
				"Would you like to buy a nice kebab? Only one gold.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "I think I'll give it a miss.",
					"Yes please.");
			stage = 0;
			break;

		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerChat(NORMAL, "I think I'll give it a miss.");
				break;
			case OPTION_2:
				sendPlayerChat(NORMAL, "Yes please.");
				stage = 1;
				break;
			}
			break;

		case 1:
			if (player.getInventory().getCoinsAmount() > 0) {
				if (player.getInventory().getFreeSlots() > 0) {
					player.getInventory().addItem(KEBAB, 1);
					end();
				}
			} else {
				sendPlayerChat(SAD,
						"Oops, I forgot to bring any money with me.");
				stage = -2;
			}
			break;

		case -2:
			end();
			break;

		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
