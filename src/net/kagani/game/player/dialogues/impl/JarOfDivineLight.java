package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class JarOfDivineLight extends Dialogue {

	@Override
	public void start() {
		stage = 1;
		sendDialogue("When you use this you will be downgrading from the Hardcore Ironman mode to standard Ironman mode. You will <col=FF0000>never</col> be able to return back as an Hardcore Ironman.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendOptionsDialogue("Downgrade to Ironman mode?",
					"Yes, downgrade to Ironman mode.", "No, I don't want to.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				if (player.isHardcoreIronman()) {
					stage = -1;
					player.getInventory().deleteItem(32335, 1);
					player.setIronman(true);
					player.setHardcoreIronMan(false);
					player.getAppearence().generateAppearenceData();
					sendDialogue("You have downgraded to an "
							+ player.getIronmanTitle(false) + ".");
				} else {
					player.getPackets()
							.sendGameMessage(
									"You need to be an Hardcore Ironman to use this item.");
					end();
				}
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}