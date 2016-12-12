package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Kaylee
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class Kaylee extends Dialogue {

	private static final int DWARVEN_STOUT = 1903;
	private static final int ASGARNIAN_ALE = 1905;
	private static final int WIZARDS_MIND_BOMB = 1907;

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY, "Hi! What can I get you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(NORMAL, "What ales are you serving?");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, HAPPY,
					"Well, we've got Asgarnian Ale, Wizard's Mind Bomb",
					"and Dwarven Stout, all for only 3 coins.");
			stage = 1;
			break;
		case 1:
			sendOptionsDialogue(DEFAULT, "One Asgarnian Ale, please.",
					"I'll try the Mind Bomb.", "Can I have a Dwarven Stout?",
					"I don't feel like any of those.");
			stage = 2;
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "One Asgarnian Ale, please.");
				stage = 3;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "I'll try the Mind Bomb.");
				stage = 4;
				break;
			case OPTION_3:
				sendPlayerDialogue(CONFUSED, "Can I have a Dwarven Stout?");
				stage = 5;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "I don't feel like any of those.");
				stage = 25;
				break;
			}
			break;
		case 3:
			if (player.getInventory().containsItem(995, 3)) {
				player.getInventory().addItem(ASGARNIAN_ALE, 1);
				player.getInventory().deleteItem(995, 3);
				sendPlayerDialogue(HAPPY, "Thanks, Kaylee.");
				stage = 25;
			} else {
				sendNPCDialogue(npcId, SAD,
						"Sorry, you don't have enough coins to buy this.");
				stage = 25;
			}
			break;
		case 4:
			if (player.getInventory().containsItem(995, 3)) {
				player.getInventory().addItem(WIZARDS_MIND_BOMB, 1);
				player.getInventory().deleteItem(995, 3);
				sendPlayerDialogue(HAPPY, "Thanks, Kaylee.");
				stage = 25;
			} else {
				sendNPCDialogue(npcId, SAD,
						"Sorry, you don't have enough coins to buy this.");
				stage = 25;
			}
			break;
		case 5:
			if (player.getInventory().containsItem(995, 3)) {
				player.getInventory().addItem(DWARVEN_STOUT, 1);
				player.getInventory().deleteItem(995, 3);
				sendPlayerDialogue(HAPPY, "Thanks, Kaylee.");
				stage = 25;
			} else {
				sendNPCDialogue(npcId, SAD,
						"Sorry, you don't have enough coins to buy this.");
				stage = 25;
			}
			break;
		case 25:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
