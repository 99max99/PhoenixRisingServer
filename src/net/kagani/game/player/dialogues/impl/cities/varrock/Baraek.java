package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class Baraek extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (player.getInventory().containsItem(948, 1)) {
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Can you sell me some furs?",
					"Would you like to buy my fur?",
					"Hello. I am in search of a quest.");
			stage = 16;
		} else {
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Can you sell me some furs?",
					"Hello. I am in search of a quest.");
			stage = -1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Can you sell me some furs?");
				stage = 0;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"Hello! I am in search of a quest.");
				stage = 15;
				break;
			}
			break;
		case 0:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yeah, sure. They're 20 gold coins each.");
			stage = 1;
			break;
		case 1:
			if (player.getInventory().containsItem(995, 20)) {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"Yeah, okay, here you go.",
						"20 gold coins? That's an outrage!");
				stage = 2;
			} else {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"I can't afford that.",
						"20 gold coins? That's an outrage!");
				stage = 9;
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "Yeah, OK, here you go.");
				stage = 3;
				break;
			case OPTION_2:
				sendPlayerDialogue(MILDLY_ANGRY,
						"20 gold coins? That's an outrage!");
				stage = 4;
				break;
			}
			break;
		case 3:
			sendItemDialogue(948, "Baraek sells you a fur.");
			player.getInventory().addItem(948, 1);
			player.getInventory().deleteItem(995, 20);
			stage = 50;
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well, my best price is 18 coins.");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "OK, here you go.",
					"No thanks, I'll leave it.");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "OK, here you go.");
				stage = 7;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "No thanks, I'll leave it.");
				stage = 14;
				break;
			}
			break;
		case 7:
			sendItemDialogue(948, "Baraek sells you a fur.");
			player.getInventory().addItem(948, 1);
			player.getInventory().deleteItem(995, 18);
			stage = 50;
			break;
		case 9:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(UPSET, "I can't afford that.");
				stage = 10;
				break;
			case OPTION_2:
				sendPlayerDialogue(MILDLY_ANGRY,
						"20 gold coins? That's an outrage!");
				stage = 10;
				break;
			}
			break;
		case 10:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well, my best price is 18 coins.");
			stage = 11;
			break;
		case 11:
			sendPlayerDialogue(UPSET, "I haven't actually got that much.");
			stage = 12;
			break;
		case 12:
			sendNPCDialogue(npcId, UPSET,
					"Well, I can't go any cheaper than that mate. I've got a",
					"family to feed.");
			stage = 13;
			break;
		case 13:
			sendPlayerDialogue(UPSET, "Oh well, never mind.");
			stage = 50;
			break;
		case 14:
			sendNPCDialogue(npcId, PLAIN_TALKING, "It's your loss mate.");
			stage = 50;
			break;
		case 15:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Sorry kiddo, I'm a fur trader not a damsel in distress.");
			stage = 50;
			break;
		case 16:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"Yeah, sure. They're 20 gold coins each.");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"Would you like to buy my fur?");
				stage = 17;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"Hello! I am in search of a quest.");
				stage = 15;
				break;
			}
			break;
		case 17:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Let's have a look at it.");
			stage = 18;
			break;
		case 18:
			sendItemDialogue(948, "You hand Baraek your fur to look at.");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"It's not in the best condition. I guess I could give you 12",
					"coins for each one.");
			stage = 20;
			break;
		case 20:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yeah, that'll do.",
					"I think I'll keep hold of it actually.");
			stage = 21;
			break;
		case 21:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "Yeah, that'll do.");
				stage = 22;
				break;
			case OPTION_2:
				sendPlayerDialogue(MILDLY_ANGRY,
						"I think I'll keep hold of it actually!");
				stage = 23;
				break;
			}
			break;
		case 22:
			sendPlayerDialogue(HAPPY, "Thanks!");
			player.getInventory().deleteItem(948, 1);
			player.getInventory().addItem(995, 12);
			stage = 50;
			break;
		case 23:
			sendNPCDialogue(npcId, UPSET, "Oh ok. Didn't want it anyway!");
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
