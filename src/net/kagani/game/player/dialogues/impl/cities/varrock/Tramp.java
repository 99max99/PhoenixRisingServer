package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class Tramp extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, UPSET, "Got any spare change, mate?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Yes, I can spare a little money.",
					"Sorry, you'll have to earn it yourself.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Yes, I can spare a little money.");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"Sorry, you'll have to earn it yourself, just like I did.");
				stage = 2;
				break;
			}
			break;
		case 1:
			if (player.getInventory().containsItem(995, 1)) {
				sendNPCDialogue(npcId, HAPPY, "Thanks, mate!");
				player.getInventory().deleteItem(995, 1);
				stage = 50;
			} else {
				sendPlayerDialogue(UPSET,
						"Sorry, I don't seem to have any coins right now.");
				stage = 50;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, MILDLY_ANGRY, "Please yourself!");
			stage = 50;
			break;
		case 3:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"Yeah, right, they all say that.");
			stage = 50;
			break;
		case 50:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
