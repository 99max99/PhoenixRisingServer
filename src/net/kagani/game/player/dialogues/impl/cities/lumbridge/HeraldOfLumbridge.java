package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.item.Item;
import net.kagani.game.player.dialogues.Dialogue;

public class HeraldOfLumbridge extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Hello there, deary! How can I make your Lumbridge day",
				"even more wonderful?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(HAPPY, "Hello back at you! What do you do here?");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I'm the Lumbridge herald, my dear! It's my job to make",
					"Lumbridge a lovely place to be in.");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, HAPPY,
					"You need a parade? I'll arrange it! You need a fancy",
					"banner? I can do that too! You need a herald cape...");
			stage = 2;
			break;
		case 2:
			if (player.hasItem(21435, 1)) {
				sendPlayerDialogue(NORMAL, "Oh, I already have one of those.");
				stage = 11;
			} else {
				sendPlayerDialogue(CONFUSED, "Herald capes? What are those?");
				stage = 3;
			}
			break;
		case 3:
			sendNPCDialogue(npcId, HAPPY,
					"Why, it's the latest and greatest thing in Lumbridge",
					"attire. We provide you with a lovely Lumbridge cape to",
					"wear around, for nothing!");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, UNSURE,
					"And if you've been particularly handy around the area,",
					"we'll show you how to customise it into something more",
					"fabulous.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"In fact, I hear that other cities, like Varrock and Falador,",
					"are also offering herald capes with their own special",
					"designs.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, UNSURE,
					"Naturally, our Lumbridge capes are much nicer.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Can I interest you in taking a cape?");
			stage = 8;
			break;
		case 8:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes", "No");
			stage = 9;
			break;
		case 9:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Sure, I'll take it!");
				player.getInventory().addItem(new Item(21435));
				stage = 10;
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 10:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Wonderful! Let's take a look at the customisations we can",
					"do...");
			stage = 50;
			break;
		case 11:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Yes, I noticed! It looks very dashing on you. Would you like",
					"to customise it some more, perhaps?");
			stage = 12;
			break;
		case 12:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes", "No");
			stage = 13;
			break;
		case 13:
			switch (componentId) {
			case OPTION_1:
				/*
				 * Open Cape Customisation
				 */
				end();
				break;
			case OPTION_2:
				end();
				break;
			}
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
