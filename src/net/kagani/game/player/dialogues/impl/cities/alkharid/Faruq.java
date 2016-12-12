package net.kagani.game.player.dialogues.impl.cities.alkharid;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Faruq extends Dialogue {

	private int npcId;

	public static final int TREASURE_CHEST = 24507;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Hello! Have you come to sample my marvellous wares?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(
					DEFAULT,
					"Yes, I'd like to see what you have.",
					"Perhaps. Your stall has some odd-looking stuff; what are they for?",
					"Can I have a treasure chest?", "No, thanks.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 171);
				break;
			case OPTION_2:
				sendPlayerDialogue(
						CONFUSED,
						"Perhaps. Your stall has some odd-looking stuff; what are",
						"they for?");
				stage = 1;
				break;
			case OPTION_3:
				if (!player.getInventory().containsItem(TREASURE_CHEST, 1)
						&& (!player.hasItem(TREASURE_CHEST, 1))) {
					sendItemDialogue(24507, "Faruq hands you a treasure chest.");
					player.getInventory().addItem(TREASURE_CHEST, 1);
					stage = -2;
				} else {
					sendNPCDialogue(npcId, SAD,
							"No, you may only have one chest at a time.",
							"Can I interest you in my other wares?");
					stage = 12;
				}
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "No, thanks.");
				stage = -2;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"I have the finest of items for those who would find their",
					"own things to do. Would you like to see them?");
			stage = 2;
			break;
		case 2:
			sendOptionsDialogue(DEFAULT, "Yes, please.",
					"But what are they for?");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 171);
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "But what are they for?");
				stage = 4;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, NORMAL,
					"Although the world has many things to do, some people",
					"like to play games of their own.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, NORMAL,
					"I sell them the tools to keep track of time, mark out",
					"places and routes, decide things randomly, even to hold",
					"great ballots of their group.");
			stage = 6;
			break;
		case 6:
			sendOptionsDialogue(DEFAULT, "Let me see, then.",
					"These tools are they complicated?",
					"I don't think this is for me.");
			stage = 7;
			break;
		case 7:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 171);
				break;
			case OPTION_2:
				sendPlayerDialogue(CONFUSED,
						"These tools, are they complicated?");
				stage = 8;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "I don't think this is for me.");
				stage = 11;
				break;
			}
			break;
		case 8:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"No, sir, they are not complicated.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, NORMAL,
					"I have a book that explains them, should you need.");
			stage = 10;
			break;
		case 10:
			end();
			ShopsHandler.openShop(player, 171);
			break;
		case 11:
			sendNPCDialogue(npcId, SAD,
					"That is a shame. I shall be here if you change your mind.");
			stage = -2;
			break;
		case 12:
			sendOptionsDialogue(
					DEFAULT,
					"Yes, I'd like to see what you have.",
					"Perhaps. Your stall has some odd-looking stuff; what are they for?",
					"No, thanks.");
			stage = 13;
			break;
		case 13:
			switch (componentId) {
			case OPTION_1:
				end();
				ShopsHandler.openShop(player, 171);
				break;
			case OPTION_2:
				sendPlayerDialogue(
						CONFUSED,
						"Perhaps. Your stall has some odd-looking stuff; what are",
						"they for?");
				stage = 1;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "No, thanks.");
				stage = -2;
				break;
			}
			break;
		case -2:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		
	}
}