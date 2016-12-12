package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class Xuan extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private int option;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		option = (Integer) parameters[1];
		switch (option) {
		case 1:
			sendNPCDialogue(npcId, HAPPY, "Good day, my friend! Good day!");
			break;
		case 2:
			stage = 20;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "todo.",
					"Recolor an item.", "Nevermind.");
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, HAPPY,
					"It is my privilege to offer you access to an exclusive",
					"stock of the finest and most exotic wares.");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Show me what you have.", "Can I ask about your stock?",
					"Learn about Loyalty Points.", "Who are you?",
					"How many loyalty points do I have?");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				stage = 20;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "todo.",
						"Recolor an item.", "Nevermind.");
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, HAPPY,
						"Of course. Nothing would bring me greater joy!");
				stage = 2;
				break;
			case OPTION_3:
				sendDialogue(
						"Loyalty Points are earned with time played. The",
						"number of points you earn increases depending on your membership",
						"bronze members get 175 points, silver members gets 200, gold member gets 225,",
						"platinum members get 250 points, diamond members get 300 points and normal players get 150 an hour.");
				stage = 0;
				break;
			case OPTION_4:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"One hundred thousand apologies! I am Xuan. The land of",
						"my birth lies far from here, but my home now is the trade",
						"routes of this world.");
				stage = 0;
				break;
			case OPTION_5:
				sendNPCDialogue(npcId, NORMAL,
						"You have " + player.getLoyaltyPoints()
								+ " loyalty points.");
				stage = 50;
				break;
			}
			break;
		case 2:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Tell me about auras.",
					"Tell me about the outfits.",
					"Tell me about recolouring items.",
					"Tell me about emotes.", "Tell me about titles.");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Age-worn mystics in lands far beyond this carved the",
						"bones of fantastical beasts with such precision and skill",
						"that they can channel the power of magic. Each is a",
						"treasure beyond value.");
				stage = 4;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, NORMAL,
						"I have travelled in many lands and seen ten thousand",
						"wonders, and all those which could be worn, I have",
						"brought here for your perusal.");
				stage = 10;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"I can recolour some of your items. Have a look around the",
						"shop and you'll see which ones.");
				stage = 12;
				break;
			case OPTION_4:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"The things I have seen! Firebreathers! Stormcallers! The",
						"transcendence of gravity! I can teach you such",
						"techniques, or at least ha passable imitation.");
				stage = 14;
				break;
			case OPTION_5:
				sendNPCDialogue(
						npcId,
						NORMAL,
						"In many lands, titles of worth can be purchased. I have",
						"documentation here to claim such a title for yourself.",
						"Once owned, you may present yourself as any of your",
						"titles.");
				stage = 15;
				break;
			}
			break;
		case 4:
			sendPlayerDialogue(NORMAL, "How do I use them?");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Why, you wear them. When you activate one, the magical",
					"energy will create a wondrous and very visible glow.");
			stage = 6;
			break;
		case 6:
			sendPlayerDialogue(NORMAL, "What powers do they have?");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Each is unique! Some aid you with combat, some with",
					"training. I even heard one of which turned cannonballs into",
					"squid!");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"The power will not last forever. After using it for a time,",
					"you need to let it replenish itself. How long depends on the",
					"aura.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, SAD,
					"But sadly Aura's are only sold in the vote shop.",
					"But I still got lots of items for sale.");
			stage = 0;
			break;
		case 10:
			sendNPCDialogue(npcId, GOOFY_LAUGH,
					"As if that wasn't enough, each outfit comes in several",
					"colours. One purchase gives you every colour!");
			stage = 11;
			break;
		case 11:
			sendDialogue("Outfits can be activated from the Customisation interface.");
			stage = 0;
			break;
		case 12:
			sendDialogue(
					"Purchasing the ability to recolour a specific item allows you to recolour",
					"that item as often as you like.");
			stage = 13;
			break;
		case 13:
			sendDialogue(
					"You must own the specific item and have it equipped or in your inventory",
					"for Xuan to recolour it. If you loose or sell the item, you will need to",
					"recover the item in order to recolour it.");
			stage = 0;
			break;
		case 14:
			sendDialogue("Purchased emotes are permanently unlocked for your character.");
			stage = 0;
			break;
		case 15:
			sendDialogue(
					"Titles appear before your name. For example, you could be called Sir",
					""
							+ player.getDisplayName()
							+ ". You can switch between any titles you have purchased.");
			stage = 16;
			break;
		case 16:
			sendDialogue("Titles can be activated from the Customisation interface.");
			stage = 0;
			break;
		case 20:
			switch (componentId) {
			case OPTION_1:
				end();
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, NORMAL,
						"Use the item you wish to recolor on me.");
				stage = 50;
				break;
			case OPTION_3:
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