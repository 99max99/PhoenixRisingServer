package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.WorldTile;
import net.kagani.game.player.dialogues.Dialogue;

public class LumbridgeSage extends Dialogue {

	private int npcId;

	public static boolean locked = false;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Greetings, adventurer. I am Phileas, the Lumbridge",
				"Guide. I am here to give information and directions to",
				"new players. Do you require any help?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (player.getRights() >= 1) {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes please.",
						"No, I can find things myself thank you.",
						"Take me to the P-Mod Room.");
			} else {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes please.",
						"No, I can find things myself thank you.");
			}
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "Yes please.");
				stage = 1;
				break;
			case OPTION_2:
				npc(PLAIN_TALKING, "No, I can find things myself thank you.");
				stage = 50;
				break;
			case OPTION_3:
				if (player.getRights() >= 1) {
					player(PLAIN_TALKING, "Can you take me to the P-Mod Room?");
					stage = 144;
				}
				break;
			}
			break;
		case 144:
			if (locked) {
				npc(PLAIN_TALKING,
						"The P-Mod Room is locked, only Administrators can access it.");
				if (player.getRights() >= 2) {
					player.setNextWorldTile(new WorldTile(2847, 5148, 0));
				}
				stage = -2;
			} else {
				player.setNextWorldTile(new WorldTile(2847, 5148, 0));
				end();
			}
			break;
		case 1:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"First I must warn you to take ever precaution to",
					"keep your arrow password and PIN secure. The",
					"most important thing to remember is to never give your",
					"password to, or share your account with, anyone.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I have much more information to impart; what would",
					"you like to know about?");
			stage = 3;
			break;
		case 3:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Where can I find a quest to go on?",
					"What monsters should I fight?", "Where can I make money?",
					"I'd like to know more about security.",
					"Where can I find a bank?");
			stage = 4;
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Where can I find a quest to go on?");
				stage = 5;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "What monsters should I fight?");
				stage = 9;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Where can I make money?");
				stage = 50;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL,
						"I'd like to know more about security.");
				stage = 54;
				break;
			case OPTION_5:
				sendPlayerDialogue(NORMAL, "Where can I find a bank?");
				stage = 100;
				break;
			}
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Well, I heard my friend the cook was in need of a spot",
					"of help. He'll be in the kitchen of this here castle. Just",
					"talk to him and he'll set you off.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Is there anything else you need help with?");
			stage = 7;
			break;
		case 7:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "No thank you.",
					"Yes please.");
			stage = 8;
			break;
		case 8:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "No thank you.");
				stage = -2;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "Yes please.");
				stage = 3;
				break;
			}
			break;
		case 9:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"There's lots of beasts to fight in the woods around here,",
					"especially to the west. There are certainly some goblins",
					"and spiders that are pests and could do with being",
					"cleared out. There's also a chicken farm or two up the");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"road for some fairly easy pickings. Non-player",
					"characters usually appear as yellow dots on your mini-",
					"map, although there are some that you won't be able to",
					"fight, such as myself. A monster's combat level is shown");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"next to their 'Attack' option. If that level is coloured",
					"green it means the monster is weaker than you. If it is",
					"red, it means that the monster is toucher than you.");
			stage = 12;
			break;
		case 12:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Remember, you will do better if you have better",
					"armour and weapons and it's always worth carrying a",
					"bit of food to heal yourself.");
			stage = 13;
			break;
		case 13:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Where can I get food to heal myself?",
					"Where can I get better armour and weapons?",
					"Okay, thanks, I will go and kill things.",
					"Can I kill other players?",
					"I'd like to know about something else.");
			stage = 14;
			break;
		case 14:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING,
						"Where can I get food to heal myself?");
				stage = 15;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"Where can I get better armour and weapons?");
				stage = 34;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"Okay, thanks, I will go and kill things.");
				stage = -2;
				break;
			case OPTION_4:
				sendPlayerDialogue(PLAIN_TALKING, "Can I kill other players?");
				stage = 46;
				break;
			case OPTION_5:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know about something else.");
				stage = 3;
				break;
			}
			break;
		case 15:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"There are many different foods in the game such as",
					"cabbage, fish, meat and many more. Which do you wish to hear",
					"about?");
			stage = 16;
			break;
		case 16:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"How do I get cabbages?", "How do I fish?",
					"Where can I find meat?");
			stage = 17;
			break;
		case 17:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "How do I get cabbages?");
				stage = 18;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "How do I fish?");
				stage = 22;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING, "Where can I find meat?");
				stage = 30;
				break;
			}
			break;
		case 18:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"There is a field a little distance to the north of here",
					"packed full of cabbages which are there for the picking.");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Is there anything else you need help with?");
			stage = 20;
			break;
		case 20:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "No thank you.",
					"I'd like to know about other food.", "Yes please.");
			stage = 21;
			break;
		case 21:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "No thank you.");
				stage = -2;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know about other food.");
				stage = 16;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING, "Yes please.");
				stage = 3;
				break;
			}
			break;
		case 22:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Fishing spots require different levels and equipment to",
					"use. To start Fishing, you'll want to buy a Fishing",
					"net.");
			stage = 23;
			break;
		case 23:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"If you don't own one already.");
			stage = 24;
			break;
		case 24:
			sendDialogue(
					"The mini-map in the top right corner of the game",
					"screen has various icons showing the locations of things.",
					"The icon with the fish symbol indicates where Fishing spots are.");
			stage = 25;
			break;
		case 25:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You will need some Fishing equipment. At the Fishing",
					"spots to the south you can only use a small fishing net.");
			stage = 26;
			break;
		case 26:
			sendPlayerDialogue(PLAIN_TALKING,
					"Where could I find one of those?");
			stage = 27;
			break;
		case 27:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You can get them from a Fishing shop",
					"There is a Fishing",
					"shop in Port Sarim; you can find it on the world map.",
					"Port Sarim is some way to the west of here, beyond");
			stage = 28;
			break;
		case 28:
			sendNPCDialogue(npcId, PLAIN_TALKING, "the village of Draynor.");
			stage = 29;
			break;
		case 29:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Is there anything else you need help with?");
			stage = 20;
			break;
		case 30:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I suggest you go and kill some chickens. The roads on",
					"either side of this river eventually go past a chicken",
					"farm. When you have killed some chickens, cook them.",
					"You could either make a fire or use a range.");
			stage = 31;
			break;
		case 31:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"There is a range at the southern end in this town.");
			stage = 32;
			break;
		case 32:
			sendDialogue(
					"The mini-map in the top right corner of the game",
					"screen has various icons showing the locations of things.",
					"The icon with the pot symbol indicates where Cooking ranges are.");
			stage = 33;
			break;
		case 33:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Is there anything else you need help with?");
			stage = 20;
			break;
		case 34:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well, you can make them, buy them or talk to the",
					"combat tutors just west of here.");
			stage = 35;
			break;
		case 35:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"How do I make a weapon?", "Where can I buy a weapon?",
					"Could I get a staff like yours?");
			stage = 36;
			break;
		case 36:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "How do I make a weapon?");
				stage = 37;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "Where can I buy a weapon?");
				stage = 40;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"Could I get a staff like yours?");
				stage = 45;
				break;
			}
			break;
		case 37:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"The Smithing skill allows you to make armour and",
					"weapons. Talk to the boy who smelts metal in the",
					"furnace, I'm sure he can help.");
			stage = 38;
			break;
		case 38:
			sendDialogue(
					"The mini-map in the top right corne of the game",
					"screen has various icons showing the locations of things.",
					"The pickaxe icon indicates where Mining sites are.");
			stage = 39;
			break;
		case 39:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I suggest you go and mine some ore; find the Mining",
					"symbol - you can mine in the swamp south of here.");
			stage = 43;
			break;
		case 40:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You can get a weapon free from the combat tutors if",
					"they think you need it. Failing that, the nearest shop",
					"that would sell you something of that nature is Bob's",
					"Brilliant Axes in this very town. If you want a bigger");
			stage = 41;
			break;
		case 41:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"choice I suggest you head to one of the big cities. If",
					"you follow the road east over the bridge and then head",
					"north you will eventually reach Varrock, where you can",
					"buy all manner of things. You can use the world map");
			stage = 42;
			break;
		case 42:
			sendNPCDialogue(npcId, PLAIN_TALKING, "to help you find your way.");
			stage = 43;
			break;
		case 43:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "No thank you.",
					"I'd like to know more about getting weapons.",
					"I'd like to know more about something else.");
			stage = 44;
			break;
		case 44:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "No thank you.");
				stage = -2;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know more about getting weapons.");
				stage = 35;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know more about something else.");
				stage = 13;
				break;
			}
			break;
		case 45:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"There is no other staff like this in all the land. It's a",
					"very important staff. It shows who holds the job as the",
					"Lumbridge Guide, and that's me.");
			stage = 43;
			break;
		case 46:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well, you can go to the Wilderness. That is the area",
					"for killing other players. Just keep heading north and",
					"eventually you will be bound to reach it. Be very",
					"careful, though. Player-killing is not for the unprepared.");
			stage = 47;
			break;
		case 47:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I'd suggest that you leave any items you don't want to",
					"lose in the bank and take a good supply of food with",
					"you.");
			stage = 48;
			break;
		case 48:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "No thank you.",
					"I'd like to know more about combat.",
					"I'd like to know more about something else.");
			stage = 49;
			break;
		case 49:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "No thank you.");
				stage = -2;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know more about combat.");
				stage = 13;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know more about something else.");
				stage = 3;
				break;
			}
			break;
		case 50:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"There are many ways to make money in the game. I",
					"would suggest either killing monsters or doing a trade",
					"skill such as Smithing or Fishing");
			stage = 51;
			break;
		case 51:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Please don't try to get money by begging off other",
					"players. It will make you unpopular. Nobody likes a",
					"beggar. It is very irritating to have other players asking",
					"for your hard-earned cash.");
			stage = 52;
			break;
		case 52:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Where can I smith?",
					"How do I fish?", "What monsters should I fight?");
			stage = 53;
			break;
		case 53:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "Where can I smith?");
				stage = 37;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "How do I fish?");
				stage = 22;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"What monsters should I fight?");
				stage = 9;
				break;
			}
			break;
		case 54:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I can tell you about password security, avoiding item",
					"scamming and in-game moderation. I can also tell you",
					"about a place called the Stronghold of Security, where",
					"you can learn more about account security and have a");
			stage = 55;
			break;
		case 55:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"bit of an adventure at the same time.");
			stage = 56;
			break;
		case 56:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"I'd like to know about password security.",
					"I'd like to know more about avoiding item scamming.",
					"I'd like to know more about in-game moderation.",
					"I'd like to know about the Stronghold of Security.");
			stage = 57;
			break;
		case 57:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know about password security.");
				stage = 58;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know more about avoiding item scamming.");
				stage = 75;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know more about in-game moderation.");
				stage = 85;
				break;
			case OPTION_4:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know about the Stronghold of Security.");
				stage = 92;
				break;
			}
			break;
		case 58:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well, the first thing to remember with password",
					"security, which I can't stress enough, is to never tell",
					"your password, bank PIN or recovery questions to",
					"anyone, not even if they claim to be arrow staff. Real");
			stage = 59;
			break;
		case 59:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"arrow staff will NEVER ask for your password.",
					"Sharing your account is a very bad idea, even if you",
					"think that you know and trust the person you are",
					"sharing with. Players have lost items and even their");
			stage = 60;
			break;
		case 60:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"account this way. Sharing accounts is also against the",
					"rules.");
			stage = 61;
			break;
		case 61:
			sendPlayerDialogue(PLAIN_TALKING,
					"Is there anything else to be aware of?");
			stage = 62;
			break;
		case 62:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes, also be careful where you enter your password.",
					"Be aware that fake arrow websites exist. These are",
					"sites that claim to give things such as item/stat",
					"upgrades, free items, beta testing and moderator");
			stage = 63;
			break;
		case 63:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"applications. arrow staff do not offer any of these services.");
			stage = 64;
			break;
		case 64:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Also be aware of trojans and keyloggers. These nasty",
					"programs can send everything you type into your",
					"computer back to the computers of malicious people, so",
					"you can lose more than just your arrow password");
			stage = 65;
			break;
		case 65:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"in this way. To avoid getting these on your computer,",
					"be very wary of what you download, especially when it",
					"comes to downloading third party arrow-related",
					"software. Also be very wary of email attachments and");
			stage = 66;
			break;
		case 66:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"instant messenger file transfers, which can contain such",
					"things.");
			stage = 67;
			break;
		case 67:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Finally, remember the game doesn't censor your",
					"password or bank PIN when you type them; anyone",
					"who tells you otherwise is probably trying to steal your",
					"character or items. There are many more arrow");
			stage = 68;
			break;
		case 68:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"security tips on the arrow website.");
			stage = 69;
			break;
		case 69:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Is there anything else you need help with?");
			stage = 70;
			break;
		case 70:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "No thanks.",
					"Yes please.", "Tell me more about security.");
			stage = 71;
			break;
		case 71:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING, "No thank you.");
				stage = -2;
				break;
			case OPTION_2:
				sendPlayerDialogue(PLAIN_TALKING, "Yes please.");
				stage = 72;
				break;
			case OPTION_3:
				sendPlayerDialogue(PLAIN_TALKING,
						"I'd like to know more about security.");
				stage = 54;
				break;
			}
			break;
		case 72:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Where can I find a quest to go on?",
					"What monsters should I fight?", "Where can I make money?",
					"How can I heal myself?", "Where can I find a bank?");
			stage = 73;
			break;
		case 73:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING,
						"Where can I find a quest to go on?");
				stage = 5;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "What monsters should I fight?");
				stage = 9;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Where can I make money?");
				stage = 50;
				break;
			case OPTION_4:
				sendPlayerDialogue(PLAIN_TALKING, "How can I heal myself?");
				stage = 74;
				break;
			case OPTION_5:
				sendPlayerDialogue(NORMAL, "Where can I find a bank?");
				stage = 100;
				break;
			}
			break;
		case 74:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You will always heal slowly over time, but people",
					"normally choose to heal themselves faster by eating food.");
			stage = 15;
			break;
		case 75:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"There are many nice and helpful players in",
					"arrow; unfortunately, as in real life, there are",
					"some who aren't so honest. Some people may try to",
					"trick you out of your items. Try not to fall for this as");
			stage = 76;
			break;
		case 76:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"arrow policy is to never return lost items to players",
					"that have been scammed. Doing so just encourages",
					"users to claim they lost items they never had to gain an",
					"unfair advantage. However, people carrying out such");
			stage = 77;
			break;
		case 77:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"scams will get banned as it is against the rules.");
			stage = 78;
			break;
		case 78:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"When trading, you have a second trade confirmation",
					"screen. Always double check that you are getting the",
					"items you expect on this page as some people may try",
					"and change what they are trading at the last minute on");
			stage = 79;
			break;
		case 79:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"the first trade screen. Remember when you have",
					"clicked accept on the second trade screen, the trade is",
					"not reversible.");
			stage = 80;
			break;
		case 80:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Try and find out from someone else what an item is",
					"before spending a lot of money on it. Not all merchants",
					"are honest about rarity or the uses of their wares.",
					"For example, some people try to pass off spinach rolls as");
			stage = 81;
			break;
		case 81:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"rare when in fact they're as common as cabbages!");
			stage = 82;
			break;
		case 82:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Be wary of people offering to improve your items. For",
					"example, some people will claim they can add a trim to",
					"your armour or upgrade your sword to a more",
					"powerful one. This is not possible and they are trying");
			stage = 83;
			break;
		case 83:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"to steal your hard-earned equipment. There are other",
					"ways in which people might try to trick you out of your",
					"items. You can find out more things to be aware of by",
					"reading the 'Avoid Scamming' guidelines on the");
			stage = 84;
			break;
		case 84:
			sendNPCDialogue(npcId, PLAIN_TALKING, "arrow website.");
			stage = 69;
			break;
		case 85:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You will, from time to time, see moderator characters in",
					"game. Remember: a real staff member will never ask",
					"for your password or any other personal information.",
					"There are two types of moderator. You may see arrow");
			stage = 86;
			break;
		case 86:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"staff characters in game - these can be identified by a",
					"gold crown next to their name and are the people who",
					"develop or manage the game. They might be from",
					"Customer Support, or they might be a tester or a");
			stage = 87;
			break;
		case 87:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"programmer. You may also see player moderators -",
					"these can be identified by a silver crown next to their",
					"name. Player moderators are trusted players of",
					"arrow that help keep in-game behaviour in line with");
			stage = 88;
			break;
		case 88:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"the arrow rules. The answers to many frequently",
					"asked questions about player moderators can be found",
					"on the arrow website.");
			stage = 89;
			break;
		case 89:
			sendPlayerDialogue(PLAIN_TALKING,
					"How do I become a player moderator?");
			stage = 90;
			break;
		case 90:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"The only way to become a player moderator is to know",
					"and play by the rules laid down by arrow on the",
					"arrow website. Report those who break the rules,",
					"help players who need it and one day you might find");
			stage = 91;
			break;
		case 91:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"an invite to become a moderator in your arrow",
					"inbox. Note that arrow will never contact you in game",
					"or by email to become a player moderator.");
			stage = 69;
			break;
		case 92:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"An exciting new discovery has been made in the",
					"Barbarian Village. Soon after they started mining in the",
					"middle of the village, one of the miners got caught in a cave.");
			stage = 93;
			break;
		case 93:
			sendPlayerDialogue(PLAIN_TALKING,
					"Sounds painful, did he fall on his head?");
			stage = 94;
			break;
		case 94:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"No, but it turned out that he had discovered a place",
					"unlike any other.");
			stage = 95;
			break;
		case 95:
			sendPlayerDialogue(PLAIN_TALKING, "How so?");
			stage = 96;
			break;
		case 96:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Well an explorer went to look down there, it's full of",
					"new and exciting creatures and other surprises on top",
					"of that. It turns out this place can also help you to",
					"secure your account more effectively.");
			stage = 97;
			break;
		case 97:
			sendPlayerDialogue(PLAIN_TALKING,
					"Wow, that sounds good. Where can I find the explorer",
					"to learn more?");
			stage = 98;
			break;
		case 98:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Sadly the explorer went missing and has not been",
					"found. However, here is some information to get you",
					"started on securing your account. You can find the",
					"Barbarian Village north of Falador and west of Varrock.");
			stage = 99;
			break;
		case 99:
			sendHandedItem(9003, "The guide hands you a book.");
			player.getInventory().addItem(9003, 1);
			stage = -2;
			break;
		case 100:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You'll find a bank upstairs in Lumbridge Castle - go",
					"right to the top.");
			stage = 101;
			break;
		case 101:
			sendDialogue(
					"The mini-map in the top right corner of the game",
					"screen has various icons showing the locations of things.",
					"The icon with the money symbol indicates where banks are.");
			stage = 6;
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