package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;

public class SilkTrader extends Dialogue {

	private int npcId;

	public static final int GOLD = 995;
	public static final int SILK = 950;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL, "Do you want to buy any fine silks?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "How much are they?",
					"No, silk doesn't suit me.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "How much are they?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No, silk doesn't suit me.");
				stage = -2;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, NORMAL, "3 gp.");
			stage = 2;
			break;
		case 2:
			sendOptionsDialogue(DEFAULT, "No, that's too much for me.",
					"Okay, that sounds good.");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(SCARED, "No, that's too much for me.");
				stage = 4;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Okay, that sounds good.");
				stage = 9;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, NORMAL, "2gp and that's as low as I'll go.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					SAD,
					"I'm not selling it for any less. You'll only go and sell it in Varrock for a profit.");
			stage = 6;
			break;
		case 6:
			sendOptionsDialogue(DEFAULT, "2gp sounds good.",
					"No, really, I don't want it.");
			stage = 7;
			break;
		case 7:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "2gp sounds good.");
				stage = 8;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No, really, I don't want it.");
				stage = 10;
				break;
			}
			break;
		case 8:
			if (player.getInventory().containsItem(GOLD, 2)) {
				sendItemDialogue(SILK, "You buy some silk for 2gp.");
				player.getInventory().deleteItem(GOLD, 2);
				player.getInventory().addItem(SILK, 1);
				stage = -2;
			} else {
				sendPlayerDialogue(SAD, "Oh dear. I don't have enough money.");
				stage = 11;
			}
			break;
		case 9:
			if (player.getInventory().containsItem(GOLD, 3)) {
				sendItemDialogue(SILK, "You buy some silk for 3 gp.");
				player.getInventory().deleteItem(GOLD, 3);
				player.getInventory().addItem(SILK, 1);
				stage = -2;
			} else {
				sendPlayerDialogue(SAD, "Oh dear. I don't have enough money.");
				stage = 11;
			}
			break;
		case 10:
			sendNPCDialogue(npcId, NORMAL,
					"Okay, but that's the best price you're going to get.");
			stage = -2;
			break;
		case 11:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"Well, come back when you do have some money.");
			stage = -2;
			break;
		case -2:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
