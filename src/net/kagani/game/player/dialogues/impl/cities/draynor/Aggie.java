package net.kagani.game.player.dialogues.impl.cities.draynor;

import java.io.IOException;

import net.kagani.game.player.dialogues.Dialogue;
//
///**
// * 
// * @author Frostbite
// *<email@frostbitersps@gmail.com><skype:frostbiterps>
// */
//
public class Aggie extends Dialogue {

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(int interfaceId, int componentId) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
//
//
//	/**
//	 * Represents the animaton used to make the dye with aggie.
//	 */
//	private static final Animation ANIMATION = new Animation(4352);
//
//	/**
//	 * Represents the ingredients needed to make paste.
//	 */
//	private static final Item[] PASTE_INGREDIENTS = new Item[] {new Item(592), new Item(1951), new Item(1929), new Item(1933)};
//
//	/**
//	 * Represents the cauldron location
//	 */
//	private final static int CAULDRON = -1;
//
//	/**
//	 * Represents the woad leaves item.
//	 */
//	private static final Item WOAD_LEAVES = new Item(1793, 2);
//
//	/**
//	 * Represents the onions item.
//	 */
//	private static final Item ONIONS = new Item(1957, 2);
//
//	/**
//	 * Represents the redberries item.
//	 */
//	private static final Item REDBERRIES = new Item(1951, 3);
//
//
//
//	@Override
//	public void start() {
//		stage = (Integer) parameters[0];
//		QuestState state = player.getQuestManager().get(Quests.PRINCE_ALI_RESUCE).getState();
//		switch(state) {
//		case COMPLETED:
//		case NOT_STARTED:
//			if(stage != -3) {
//				sendNPCChat(ASKING, "What can I help you with?");
//				stage = 1;
//			} else {
//				sendOptionsDialogue("WHAT DYE DO YOU WANT AGGIE TO MAKE FOR YOU?", "Red dye (requires 5 coins and 3 lots of redberries).", 
//						"Blue dye (requires 5 coins and 2 woad leaves).", "Yellow dye (requires 5 coins and 2 onions).");
//				stage = 32;
//			}
//			break;
//
//		case STARTED:
//			if(stage != -3) {
//				sendOptionsDialogue(DEFAULT, "Could you think of a way to make skin paste?", "What could you make for me?", "Cool, do you turn people into frogs?", 
//						"You mad old witch, you can't help me.", "Can you make dyes for me please?");
//				stage = 20;
//			} else {
//				sendOptionsDialogue("WHAT DYE DO YOU WANT AGGIE TO MAKE FOR YOU?", "Red dye (requires 5 coins and 3 lots of redberries).", 
//						"Blue dye (requires 5 coins and 2 woad leaves).", "Yellow dye (requires 5 coins and 2 onions).");
//				stage = 32;
//			}
//			break;
//
//		}
//	}
//
//	@Override
//	public void run(int interfaceId, int componentId) {
//		switch(stage) {
//		case 1:
//			sendOptionsDialogue(DEFAULT, "Hey, you're a witch aren't you?", "So what is actually in that cauldron?", "What's new in Draynor Village?", "More...");
//			stage = 2;
//			break;
//
//		case 2:
//			switch(componentId) {
//			case OPTION_1:
//				sendNPCChat(NORMAL, "My, you're observant!");
//				stage = 3;
//				break;
//			case OPTION_2:
//				sendNPCChat(ASKING, "You don't really expect me to give away trade secrets, do " + 
//						"you?");
//				stage = -2;
//				break;
//			case OPTION_3:
//				sendNPCChat(ANGRY, "The blood sucker has returend to Draynor Manor. I hate "
//						+ "his kind.");
//				stage = 5;
//				break;
//			case OPTION_4:
//				options(DEFAULT, "What could you make for me?", "Can you make dyes for me please?", "More...");
//				stage = 8;
//				break;
//			}
//			break;
//
//		case 3:
//			sendPlayerChat(ASKING, "Cool, do you turn people into frogs?");
//			stage = 4;
//			break;
//
//		case 4:
//			sendNPCChat(NORMAL, "Oh, not for years. But if you meet a talking chicken, you " + 
//					"have probably met the professor in the maynor north of " + 
//					"here. A few years ago it was flying fish. That machine is a " + 
//					"menace.");
//			stage = 5;
//			break;
//
//		case 5:
//			sendPlayerChat(ASKING, "The blood sucker?");
//			stage = 6;
//			break;
//
//		case 6:
//			sendPlayerChat(SAD, "Yes, Court Draynor, the vampyre. He's preying on the " +
//					"innocent people of this village. I do my best to protect " + 
//					"them with what magic I have, but he is powerful.");
//			stage = 7;
//			break;
//
//		case 7:
//			sendPlayerChat(SAD, "Wow, that's sad.");
//			stage = -2;
//			break;
//
//		case 8:
//			switch(componentId) {
//			case OPTION_1:
//				sendNPCChat(NORMAL, "I mostly just make what I find pretty. I sometimes make " + 
//						"dye for the women's clothes to brighten the place up. I " + 
//						"can make red, yellow and blue dyes. If you'd like some, " +
//						"just bring me the appropriate ingredients.");
//				stage = 9;
//				break;
//			case OPTION_2:
//				sendNPCChat(ASKING, "What sort of dye would you like? Red, yellow or blue?");
//				stage = 13;
//				break;
//			case OPTION_3:
//				sendOptionsDialogue(DEFAULT, "Hey, you're a witch aren't you?", "So what is actually in that cauldron?", "What's new in Draynor Village?", "More...");
//				stage = 2;
//				break;
//			}
//			break;
//
//		case 9:
//			sendOptionsDialogue(DEFAULT, "What do you need to make red dye?", "What do you need to make yellow dye?", "What do you need to make blue dye?", "No thanks, I am happy the colour I am.");
//			stage = 10;
//			break;
//
//		case 10:
//			switch(componentId) {
//			case OPTION_1:
//				sendNPCChat(NORMAL, "3 lots of redberries and 5 coins to you.");
//				stage = 11;
//				break;
//			case OPTION_2:
//				sendNPCChat(NORMAL, "Yellow is a strange colour to get, comes from onion skins.",
//						"I need 2 onions and 5 coins to make yellow dye.");
//				stage = 15;
//				break;
//			case OPTION_3:
//				sendNPCChat(NORMAL, "2 Woad leaves and 5 coins to you.");
//				stage = 17;
//				break;
//			case OPTION_4:
//				sendNPCChat(NORMAL, "You are easily pleased with yourself then.",
//						"When you need dyes, come to me.");
//				stage = -2;
//				break;
//			}
//			break;
//
//		case 11:
//			sendOptionsDialogue(DEFAULT, "Could you make me some red dye, please?", "I don't think I have all the ingredients yet.", "I can do without dye at that price.", "Where can I get redberries?", "What other colours can you make?");
//			stage = 12;
//			break;
//
//		case 12:
//			switch(componentId) {
//			case OPTION_1:
//				if(!player.getInventory().contains(REDBERRIES)) {
//					sendDialogue("You don't have enough berries to make the red dye.");
//					stage = -2;
//					return;
//				}
//				if(!player.getInventory().containsItem(995,  5)) {
//					sendDialogue("You don't have enough coins.");
//					stage = -2;
//					return;
//				}
//				if(player.getInventory().contains(REDBERRIES) && player.getInventory().containsItemMoneyPouch(995, 5)) {
//					player.getInventory().deleteItem(REDBERRIES);
//					player.getInventory().removeItemMoneyPouch(995, 5);
//					make(1763);
//					sendItemDialogue(1763, "You hand the berries and payment to Aggie. Aggie prodcues a red",
//							"bottle and hands it to you.");
//					stage = -2;
//				}
//				break;
//			case OPTION_2:
//				sendNPCChat(NORMAL, "You know what you need to get, now come back when you",
//						"have them. Goodbye for now.");
//				stage = -2;
//				break;
//			case OPTION_3:
//				sendNPCChat(NORMAL, "That's your choice, but I would think you have killed for",
//						"less. I can see it in your eyes.");
//				stage = -2;
//				break;
//			case OPTION_4:
//				sendNPCChat(NORMAL, "I pick mine from the woods south of Varrock. The food",
//						"shop port in Port Sarim sometimes has some as well.");
//				stage = 13;
//				break;
//			case OPTION_5:
//				sendNPCChat(NORMAL, "Red, yellow, and blue. Which one would you like?");
//				stage = 9;
//				break;
//			}
//			break;
//
//		case 13:
//			sendOptionsDialogue(DEFAULT, "What other colours can you make?", "Thanks");
//			stage = 14;
//			break;
//
//		case 14:
//			switch(componentId) {
//			case OPTION_1:
//				sendNPCChat(ASKING, "Red, yellow and blue. Which one would you like?");
//				stage = 9;
//				break;
//			case OPTION_2:
//				sendNPCChat(HAPPY, "You're Welcome!");
//				stage = -2;
//				break;
//			}
//			break;
//
//		case 15:
//			sendOptionsDialogue(DEFAULT, DEFAULT, "Could you make me some yellow dye, please?", "I don't think I have all the ingredients yet.", "I can do without dye at the price.", "Where can I get onions?", "What other colours can you make?");
//			stage = 16;
//			break;
//
//		case 16:
//			switch(componentId) {
//			case OPTION_1:
//				if(!player.getInventory().contains(ONIONS)) {
//					sendDialogue("You don't have enough onions to make the yellow dye.");
//					stage = -2;
//					return;
//				}
//				if(!player.getInventory().containsItemMoneyPouch(995, 5)) {
//					sendDialogue("You don't have enough coins.");
//					stage = -2;
//					return;
//				}
//				if(player.getInventory().contains(ONIONS) && player.getInventory().containsItemMoneyPouch(995,  5)) {
//					player.getInventory().deleteItem(ONIONS);
//					player.getInventory().removeItemMoneyPouch(995, 5);
//					make(1765);
//					sendItemDialogue(1765, "You hand the onions and payment to Aggie. Aggie produces a yellow", 
//							"bottle and hands it to you.");
//					stage = -2;
//				}
//				break;
//			case OPTION_2:
//				sendNPCChat(NORMAL, "You know what you need to get, now come back when you",
//						"have them. Goodbye for now.");
//				stage = -2;
//				break;
//			case OPTION_3:
//				sendNPCChat(NORMAL, "That's your choice, but I would think you have killed for",
//						"less. I can see it in your eyes.");
//				stage = -2;
//				break;
//			case OPTION_4:
//				sendNPCChat(NORMAL, "There are some onions growing on a farm to East of",
//						"here, next to the sheep field.");
//				stage = 13;
//				break;
//			case OPTION_5:
//				options(DEFAULT, "What do you need to make red dye?", "What do you need to make yellow dye?", "What do you need to make blue dye?");
//				stage = 9;
//				break;
//			}
//			break;
//
//		case 17:
//			sendOptionsDialogue(DEFAULT, "Could you make me some blue dye? please?", "I don't think I have all the ingredients yet.", "I can do without dye at that price.", "Where can I get woad leaves?", "What other colours can you make?");
//			stage = 18;
//			break;
//
//		case 18:
//			switch(componentId) {
//			case OPTION_1:
//				if(!player.getInventory().contains(WOAD_LEAVES)) {
//					sendDialogue("You don't have enough woad leafs to make the blue dye.");
//					stage = -2;
//					return;
//				}
//				if(!player.getInventory().containsItemMoneyPouch(995,  5)) {
//					sendDialogue("You don't have enough coins.");
//					stage = -2;
//					return;
//				}
//				if(player.getInventory().contains(WOAD_LEAVES) && player.getInventory().containsItem(995,  5)) {
//					player.getInventory().deleteItem(WOAD_LEAVES);
//					player.getInventory().removeItemMoneyPouch(995, 5);
//					make(1767);
//					sendItemDialogue(1767, "You hand the woad leafs and payment to Aggie. Aggie produces a blue",
//							"bottle and hands it to you.");
//					stage = -2;
//				}
//				break;
//			case OPTION_2:
//				sendNPCChat(NORMAL, "You know what you need to get, now come back when you",
//						"have them. Goodbye for now.");
//				stage = -2;
//				break;
//			case OPTION_3:
//				sendNPCChat(NORMAL, "That's your choice, but I would think you have killed for",
//						"less. I can see it in your eyes.");
//				stage = -2;
//				break;
//			case OPTION_4:
//				sendNPCChat(NORMAL, "Woad leaves are fairly hard to find. My other customers",
//						"tell me the chief gardener in Falador grows them.");
//				stage = 13;
//				break;
//			case OPTION_5:
//				sendOptionsDialogue(DEFAULT, "What do you need to make red dye?", "What do you need to make yellow dye?", "What do you need to make blue dye?");
//				stage = 9;
//				break;
//			}
//			break;
//
//		case 19:
//			sendOptionsDialogue(DEFAULT, "Could you think of a way to make skin paste?", "What could you make for me?", "Cool, do you turn people into frogs?", 
//					"You mad old witch, you can't help me.", "Can you make dyes for me please?");
//			stage = 20;
//			break;
//
//		case 20:
//			switch(componentId) {
//			case OPTION_1:
//				if(!hasIngredients(player)) {
//					sendNPCChat(NORMAL, "Why it's one of my most popular potions. The women", "here, they like to have smooth looking skin. And I must", "admit, some of the men buy it as well.");
//					stage = 21;
//				} else {
//					sendNPCChat(HAPPY, "Yes I can, I see you already have the ingredients.", "Would you like me to mix some for you now?");
//					stage = 24;
//				}
//				break;
//			case OPTION_2:
//				sendNPCChat(NORMAL, "I mostly just make what I find pretty. I sometimes make " + 
//						"dye for the women's clothes to brighten the place up. I " + 
//						"can make red, yellow and blue dyes. If you'd like some, " +
//						"just bring me the appropriate ingredients.");
//				stage = 9;
//				break;
//			case OPTION_3:
//				sendNPCChat(NORMAL, "Oh, not for years. But if you meet a talking chicken, you " + 
//						"have probably met the professor in the maynor north of " + 
//						"here. A few years ago it was flying fish. That machine is a " + 
//						"menace.");
//				stage = 5;
//				break;
//			case OPTION_4:
//				sendNPCChat(ANGRY, "Oh, you like to call a witch names do you?");
//				stage = 31;
//				break;
//			case OPTION_5:
//				sendNPCChat(ASKING, "What sort of dye would you like? Red, yellow or blue?");
//				stage = 13;
//				break;
//			}
//			break;
//
//		case 21:
//			sendNPCChat(NORMAL, "I can make it for you, just get me what's needed.");
//			stage = 22;
//			break;
//
//		case 22:
//			sendPlayerChat(ASKING, "What do you need to make it?");
//			stage = 23;
//			break;
//
//		case 23:
//			sendNPCChat(NORMAL, "Well dearie, you need a base of the paste. That's a",
//					"mix of ash, four and water. Then you need redberries", "to colour it as you want. bring me those four items",
//					"and I will make you some.");
//			stage = -2;
//			break;
//
//		case 24:
//			sendOptionsDialogue(DEFAULT, "Yes please. Mix me some skin paste.", "No thank you, I don't need any skin paste right now.");
//			stage = 25;
//			break;
//
//		case 25:
//			switch(componentId) {
//			case OPTION_1:
//				sendPlayerChat(NORMAL, "Yes please. Mix me some skin paste.");
//				stage = 26;
//				break;
//			case OPTION_2:
//				sendNPCChat(NORMAL, "Okay dearie, that's always your choice.");
//				stage = -2;
//				break;
//			}
//			break;
//
//		case 26:
//			sendNPCChat(NORMAL, "That should be simple. hand the things to Aggie then.");
//			stage = 27;
//			break;
//
//		case 27:
//			removeIngredients(player);
//			sendDialogue("You hand the ash, flour, water and redberries to Aggie.", "Aggie tips the ingredients into a cauldron", "and mutters some words.");
//			stage = 28;
//			break;
//
//		case 28:
//			sendNPCChat(NORMAL, "Tourniquet, Fenderbaum, Tottenham, marshmaallow,", "MarbleArch.");
//			make(2424);
//			stage = 29;
//			break;
//
//		case 29:
//			sendItemDialogue(2424, "Aggie hands you the skin paste.");
//			stage = 30;
//			break;
//
//		case 30:
//			sendNPCChat(NORMAL, "There you go dearie, your skin potion. That will make", "you look good at the Varrock dances.");
//			stage = -2;
//			break;
//
//		case 31:
//			if(!player.getInventory().containsItemMoneyPouch(995, 20)) {
//				sendNPCChat(ANGRY, "You should be careful about insulting a witch. You", "never know what shape you could wake up in.");
//				stage = -2;
//				return;
//			} else {
//				sendItemDialogue(1001, "Aggie waves her hands about, and you seem to be 20", "coins poorer.");
//				player.getInventory().removeItemMoneyPouch(995, 20);
//				stage = -2;
//			}
//			break;
//
//			/**
//			 * Option 2
//			 */
//		case 32:
//			switch(componentId) {
//			case OPTION_1:
//				if(!player.getInventory().contains(REDBERRIES)) {
//					sendDialogue("You don't have enough berries to make the red dye.");
//					stage = -2;
//					return;
//				}
//				if(!player.getInventory().containsItem(995,  5)) {
//					sendDialogue("You don't have enough coins.");
//					stage = -2;
//					return;
//				}
//				if(player.getInventory().contains(REDBERRIES) && player.getInventory().containsItemMoneyPouch(995, 5)) {
//					player.getInventory().deleteItem(REDBERRIES);
//					player.getInventory().removeItemMoneyPouch(995, 5);
//					make(1763);
//					sendItemDialogue(1763, "You hand the berries and payment to Aggie. Aggie prodcues a red",
//							"bottle and hands it to you.");
//					stage = -2;
//				}
//				break;
//			case OPTION_2:
//				if(player.getInventory().contains(WOAD_LEAVES) && player.getInventory().containsItem(995,  5)) {
//					player.getInventory().deleteItem(WOAD_LEAVES);
//					player.getInventory().removeItemMoneyPouch(995, 5);
//					make(1767);
//					sendItemDialogue(1767, "You hand the woad leafs and payment to Aggie. Aggie produces a blue",
//							"bottle and hands it to you.");
//					stage = -2;
//				}
//				break;
//			case OPTION_3:
//				if(!player.getInventory().contains(ONIONS)) {
//					sendDialogue("You don't have enough onions to make the yellow dye.");
//					stage = -2;
//					return;
//				}
//				if(!player.getInventory().containsItemMoneyPouch(995, 5)) {
//					sendDialogue("You don't have enough coins.");
//					stage = -2;
//					return;
//				}
//				if(player.getInventory().contains(ONIONS) && player.getInventory().containsItemMoneyPouch(995,  5)) {
//					player.getInventory().deleteItem(ONIONS);
//					player.getInventory().removeItemMoneyPouch(995, 5);
//					make(1765);
//					sendItemDialogue(1765, "You hand the onions and payment to Aggie. Aggie produces a yellow", 
//							"bottle and hands it to you.");
//					stage = -2;
//				}
//				break;
//			}
//			break;
//
//		case -2:
//			end();
//			break;
//		}
//	}
//
//	/**
//	 * Method used to check if the player has the ingredients for the paste.
//	 * @param player the player.
//	 * @return {@code True} if so.
//	 */
//	private final boolean hasIngredients(final Player player) {
//		for (Item i : PASTE_INGREDIENTS) {
//			if (!player.getInventory().contains(i)) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	public final void removeIngredients(final Player player) {
//		for(Item i : PASTE_INGREDIENTS) {
//			if(player.getInventory().contains(i)) {
//				player.getInventory().deleteItem(i);
//			}
//		}
//	}
//
//	public void make(int dye) {
//		for (final NPC n : World.getNPCs()) {
//			if (n == null || n.getId() != 922)
//				continue;
//			npc.setNextAnimation(ANIMATION);
//			player.getInventory().addItem(dye, 1);
//			//npc.faceObject(CAULDRON);
//		}
//	}
//
//
//	@Override
//	public void finish() {
//		// TODO Auto-generated method stub
//
//	}
//
}
