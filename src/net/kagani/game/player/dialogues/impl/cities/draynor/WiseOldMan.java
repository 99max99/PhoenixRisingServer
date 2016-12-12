package net.kagani.game.player.dialogues.impl.cities.draynor;

import net.kagani.Settings;
import net.kagani.game.player.dialogues.Dialogue;

/**
 * 
 * @author Mod Austin
 *
 */

public class WiseOldMan extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY, "Greetings, " + player.getDisplayName()
				+ ".");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(CONFUSED, "So you're a wise old man, huh?");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, UNSURE,
					"Less of the 'old' man, if you please!");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"But yes, I suppose you could say that. I prefer to think of",
					"myself as a sage.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(CONFUSED, "So what's a sage doing here?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"I've spent most of my life studying this world in which we",
					"live. I've strode through the depths of the deadliest",
					"dungeons, roamed the murky jungles of Karamja,",
					"meditated on the glories of Saradomin on Entrana,");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"and read dusty tomes in the Library of Varrock.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, HAPPY,
					"Now I'm not as young as I used to be, I'm settling here",
					"where it's peaceful.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, SAD,
					"It's a pity about that vampyre that keeps attacking the",
					"village. At least Saradomin protects me.");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(BLANK,
					"That's quite an exciting life you've had.");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"Exciting? Yes, I suppose so.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Now I'm here, perhaps I could offer you the benefit of my",
					"experience and wisdom?");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(HAPPY, "Thanks! So how can you help me?");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Well, I imagine you've gathered up quite a lot of stuff on",
					"your travels. Things you used for quests a long time ago",
					"that you don't need now.");
			stage = 12;
			break;
		case 12:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"If you like, I can look through your bank and see if there's",
					"anything you can chuck away.");
			stage = 13;
			break;
		case 13:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Alternatively, you can bring items here and show them to",
					"me. If I see that it's something you don't need, I'll let you",
					"know. I might even be willing to buy it.");
			stage = 14;
			break;
		case 14:
			sendPlayerDialogue(BLANK,
					"So you'll help me clear junk out of my bank?");
			stage = 15;
			break;
		case 15:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Yes, that's right. Or I'd be happy to chat with you about",
					"the wonders of this world!");
			stage = 16;
			break;
		case 16:
			sendOptionsDialogue(DEFAULT,
					"Could I have some free stuff, please?",
					"I'd just like to ask you something.",
					"Is there anything I can do for you?",
					"Could you check my things for junk, please?",
					"Thanks, maybe some other time.");
			stage = 17;
			break;
		case 17:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(UNSURE,
						"Could I have some free stuff, please?");
				stage = 18;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "I'd just like to ask you something.");
				stage = 20;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Thanks, but I don't have anything I need doing on non-",
						"members' worlds.");
				stage = -2;
				break;
			case OPTION_4:
				end();
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		case 18:
			sendNPCDialogue(npcId, SAD, "Deary deary me...");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I'm not giving out free money, but if you log into a",
					"members' world I'd be glad to reward you if you'd do a",
					"little job for me.");
			stage = -2;
			break;
		case 20:
			sendNPCDialogue(npcId, HAPPY, "Please do!");
			stage = 21;
			break;
		case 21:
			sendOptionsDialogue(DEFAULT, "Distant lands", "Strange beasts",
					"Days gone by", "Gods and demons", "Your hat!");
			stage = 22;
			break;
		case 22:
			switch (componentId) {
			case OPTION_1:
				sendOptionsDialogue(DEFAULT, "The Wilderness", "Misty jungles",
						"Underground domains", "Mystical realms");
				stage = 23;
				break;
			case OPTION_2:
				sendOptionsDialogue(DEFAULT, "Biggest & Baddest",
						"Poison and how to survive it",
						"Wealth through slaughter");
				stage = 48;
				break;
			case OPTION_3:
				sendOptionsDialogue(DEFAULT, "Heroic figures",
						"The origin of magic", "Settlements");
				stage = 68;
				break;
			case OPTION_4:
				sendOptionsDialogue(DEFAULT, "Three gods?",
						"The wars of the gods", "The Mahjarrat",
						"Wielding the power of the gods");
				stage = 86;
				break;
			case OPTION_5:
				sendPlayerDialogue(BLANK, "I want to ask you about your hat.");
				stage = 101;
				break;
			}
			break;
		case 23:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY,
						"Could you tell me about the Wilderness, please?");
				stage = 24;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "What can you tell me about jungles?");
				stage = 34;
				break;
			case OPTION_3:
				sendPlayerDialogue(HAPPY, "Tell me about what's underground.");
				stage = 40;
				break;
			case OPTION_4:
				sendPlayerDialogue(HAPPY, "What mystical realms can I visit?");
				stage = 46;
				break;
			}
			break;
		case 24:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Many years ago, when the foul Zamorak was doing battle",
					"with the forces of Saradomin, the northern reaches of",
					"this continent were blasted by his evil magic,",
					"transforming a vast area into a barren wasteland of rocks");
			stage = 25;
			break;
		case 25:
			sendNPCDialogue(npcId, PLAIN_TALKING, "and lava.");
			stage = 26;
			break;
		case 26:
			sendNPCDialogue(
					npcId,
					SCARED,
					"The spirits of the monsters that perished in this",
					"cataclysm are still roaming the Wilderness, even today,",
					"hunting for people to slaughter in their endless desire for",
					"revenge. They are incredibly dangerous, far more deadly");
			stage = 27;
			break;
		case 27:
			sendNPCDialogue(npcId, SCARED, "than other monsters.");
			stage = 28;
			break;
		case 28:
			sendNPCDialogue(npcId, HAPPY,
					"However, it's still often worth the risk of entering the",
					"Wilderness. There are treasures in the Wilderness that",
					"are scarce elsewhere, and some of the other monsters",
					"are popular for combat training. There are even arenas");
			stage = 29;
			break;
		case 29:
			sendNPCDialogue(npcId, HAPPY,
					"where you can fight other people like yourself!");
			stage = 30;
			break;
		case 30:
			sendNPCDialogue(
					npcId,
					LISTENS_THEN_LAUGHS,
					"If you dare to go to the far north-west of the Wilderness,",
					"there's a building called the Mage Arena where you can",
					"learn to summon the power of Saradomin himself!");
			stage = 31;
			break;
		case 31:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Is there anything else you'd like to ask?");
			stage = 32;
			break;
		case 32:
			sendOptionsDialogue(DEFAULT, "Yes please.",
					"Thanks, maybe some other time.");
			stage = 33;
			break;
		case 33:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Yes please.");
				stage = 21;
				break;
			case OPTION_2:
				sendPlayerDialogue(BLANK, "Thanks, maybe some other time.");
				stage = -2;
				break;
			}
			break;
		case 34:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"If it's jungle you want, look no further then the southern",
					"regions of Karamja.");
			stage = 35;
			break;
		case 35:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Once you get south of Brimhaven, the whole island is",
					"pretty much covered in exotic trees, creepers and shrubs.");
			stage = 36;
			break;
		case 36:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"There's a small settlement called Tai Bwo Wannai Village",
					"in the middle of the island. It's a funny place; the",
					"chieftain's an unfriendly chap and his sons are barking",
					"mad.");
			stage = 37;
			break;
		case 37:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"Honestly, one of them asked me to stuff a dead monkey",
					"with seaweed so he could EAT it!");
			stage = 38;
			break;
		case 38:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Further south you'll find Shilo Village. It's been under",
					"attack by terrifying zombies in recent months, if my",
					"sources are correct.");
			stage = 39;
			break;
		case 39:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"The jungle's filled with nasty creatures. There are vicious",
					"spiders that you can hardly see before they try to bite",
					"your legs off, and great big jungle ogres.");
			stage = 31;
			break;

		case 40:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"Oh, the dwarven realms?");
			stage = 41;
			break;
		case 41:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes, there was a time, back in the Fourth Age, when we",
					"humands wouldn't have been able to venture underground.",
					"That was before we had magic; the dwarves were quite a",
					"threat.");
			stage = 42;
			break;
		case 42:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Still, it's much more friendly now. You can visit the vast",
					"dwarven mine if you like; the entrance is on the mountain",
					"north of Falador.");
			stage = 43;
			break;
		case 43:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"If you go further west you may be able to visit the",
					"dwarven city of Keldagrim. But they were a bit cautious",
					"about letting humans in, last time I asked.");
			stage = 44;
			break;
		case 44:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"On the other hand, if you go west of Brimhaven, you'll find",
					"a huge underground labyrinth full of giants, demons, dogs",
					"and dragons to fight. It's even bigger than the caves",
					"under Taverly, although the Taverly dungeon's pretty");
			stage = 45;
			break;
		case 45:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"good for training your combat skills.");
			stage = 31;
			break;

		case 46:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"The fabled Lost City of Zanaris has an entrance",
					"somewhere near here. Perhaps some day you'll go there.");
			stage = 47;
			break;
		case 47:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Also, in my research I came across ancient references to",
					"some kind of Abyss. Demons from the Abyss have already",
					"escaped into this land; Saradomin be thanked that they",
					"are very rare!");
			stage = 31;
			break;

		case 48:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Tell me about mighty monsters.");
				stage = 49;
				break;
			case OPTION_2:
				sendPlayerDialogue(CONFUSED, "What does poison do?");
				stage = 61;
				break;
			case OPTION_3:
				sendPlayerDialogue(UNSURE, "What monsters drop good items?");
				stage = 65;
				break;
			}
			break;
		case 49:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"There's a mighty fire-breathing dragon living underground",
					"in the deep Wilderness, known as the King Black Dragon.",
					"It's a fearsome beast, with a breath that can poison you,",
					"freeze you to the ground or incinerate you where you");
			stage = 50;
			break;
		case 50:
			sendNPCDialogue(npcId, PLAIN_TALKING, "stand.");
			stage = 51;
			break;
		case 51:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"But even more deadly is the Queen of the Kalphites. As if",
					"her giant mandibles of death were not enough, she also",
					"throws her spines at her foes with deadly force. She can",
					"even cast rudimentary spells.");
			stage = 52;
			break;
		case 52:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Some dark power must be protecting her, for she can",
					"block attacks using prayer just as humans do.");
			stage = 53;
			break;
		case 53:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Another beast that's worthy of a special mention is the",
					"Shaikahan. It dwells in the eastern reaches of Karamja",
					"and is almost impossible to kill except with specially",
					"prepared weapons.");
			stage = 54;
			break;
		case 54:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"In the north, beyond the troll lands, there is a cavern",
					"where an endless battle rages between the forces of",
					"Saradomin and those of Zamorak.");
			stage = 55;
			break;
		case 55:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Saradomin's forces are led by the icyene warrior",
					"Commander Zilyana, Keeeper of the Faith. According to",
					"legend, she takes the form of a collossal woman with",
					"wings.");
			stage = 56;
			break;
		case 56:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Zamorak's forces are led by K'ril Tsutsaroth, a demon of",
					"devastating power. Around their encampment is a",
					"mystical darkness that no lantern can alleviate.");
			stage = 57;
			break;
		case 57:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Two minor armies are loosely allied with the Saradominist",
					"force. One consists of a bird-like creatures, the Aviansie.",
					"Another consists of mindless monsters such as ogres and",
					"orks. The leaders of those armies are terrifying creatures");
			stage = 58;
			break;
		case 58:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"in their own right, although inferior by far to Zilyana.");
			stage = 59;
			break;
		case 59:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Beyond the Fremennik lands dwell the fearsome",
					"Dagannoth Kings, reptillian creatures of the deep. There",
					"are three of them, fighting with melee, magic and ranged",
					"attacks respectively, on a small island at the bottom of a");
			stage = 60;
			break;
		case 60:
			sendNPCDialogue(npcId, PLAIN_TALKING, "labyrinthine cavern.");
			stage = 31;
			break;

		case 61:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Many monsters use poison against their foes. If you get",
					"poisoned, you will not feel it at the same time, but later you will",
					"begin to suffer its effect, and your life will drain slowly",
					"from you.");
			stage = 62;
			break;
		case 62:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Over time the effects dwindle to nothing, but if you had",
					"already been wounded you might di before they wear off",
					"completely.");
			stage = 63;
			break;
		case 63:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Fortunately, followers of Guthix have devised potions that",
					"can cure the poison or even give immunity to it.");
			stage = 64;
			break;
		case 64:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Should you wish to use poison against your own enemies",
					"and those of our Lord Saradomin, there is a potion that",
					"you can smear on your daggers, arrows, spears, javelins",
					"and throwing knives.");
			stage = 31;
			break;

		case 65:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"As a general rule, tougher monsters drop more valuable",
					"items. But even a lowly hobgoblin can drop valuable gems;",
					"it just does this extremely rarely.");
			stage = 66;
			break;
		case 66:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"If you can persuade the Slayer Masters to train you as a",
					"Slayer, you will be able to fight certain monsters that",
					"drop valuable items far more often.");
			stage = 67;
			break;
		case 67:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"You might care to invest in an enchanted dragonstone",
					"ring. These are said to make a monster drop its most",
					"valuable items a little more often.");
			stage = 31;
			break;

		case 68:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Tell me about valiant heroes!");
				stage = 69;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY,
						"Where did humans learn to use magic?");
				stage = 77;
				break;
			case OPTION_3:
				sendPlayerDialogue(BLANK,
						"I suppose you'd know about the history of today's cities?");
				stage = 81;
				break;
			}
			break;
		case 69:
			sendNPCDialogue(
					npcId,
					LISTENS_THEN_LAUGHS,
					"Ha ha ha... There are plenty of heroes. Always have been,",
					"always will be, until the fall of the world.");
			stage = 70;
			break;
		case 70:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"If you'd do a few more quests, you'd soon become a fairly",
					"noted adventurer yourself.");
			stage = 71;
			break;
		case 71:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"But I suppose I could tell you of a couple...");
			stage = 72;
			break;
		case 72:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes, there was a man called Arrav. No-one knew where he",
					"came from, but he was a fearsome fighter, a skilful",
					"hunter and remarkable farmer. He lived in the ancient",
					"settlement of Avarrocka, defending it from goblins, until");
			stage = 73;
			break;
		case 73:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"he went forth in search of some strange artefact long",
					"desired by the dreaded Mahjarrat.");
			stage = 74;
			break;
		case 74:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Perhaps some day I shall be able to tell you what became",
					"of him.");
			stage = 75;
			break;
		case 75:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"But do not let your head be turned by heroics. Randas was",
					"another great man, but he let himself be beguiled into",
					"turning to serve Zamorak, and they say he is now a",
					"mindless creature deep in the Underground Pass that");
			stage = 76;
			break;
		case 76:
			sendNPCDialogue(npcId, PLAIN_TALKING, "leads to Isafdar.");
			stage = 31;
			break;

		case 77:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Ah, that was quite a discovery! It revolutionised our way",
					"of life and jolted us into this Fifth Age of the world.");
			stage = 78;
			break;
		case 78:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"They say a traveller in the north discovered the key,",
					"although no records state exactly what he found. From",
					"this he was able to summon the magic of the four",
					"elements, using magic as a tool and a weapon. He and his");
			stage = 79;
			break;
		case 79:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"followers learnt how to bind the power into little stones so",
					"that others could use it.");
			stage = 80;
			break;
		case 80:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"in the land south of here they constructed an immense",
					"tower where the power could be studied, but followers of",
					"Zamorak destroyed it with fire many years ago, and most",
					"of the knowledge was lost.");
			stage = 31;
			break;

		case 81:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes, there are fairly good records of the formation of",
					"the cities from primitive settlements.");
			stage = 82;
			break;
		case 82:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"In the early part of the Fourth Age, of course, there were",
					"no permanent settlements. Tribes wandered the lands,",
					"staying where they could until the resources were",
					"exhausted.");
			stage = 83;
			break;
		case 83:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"This changed as people learnt to grow crops and breed",
					"animals, and now there are very few of the old nomadic",
					"tribes. There's at least one tribe roaming between the",
					"Troll Stronghold and Rellekka, though.");
			stage = 84;
			break;
		case 84:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"One settlement was Avarrocka, a popular trading centre.");
			stage = 85;
			break;
		case 85:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"In the west, Ardougne gradually formed under the",
					"leadershop of the Carnillean family, despite the threat of",
					"the Mahjarrat warlord Hazeel who dwelt in that area until",
					"his downfall.");
			stage = 31;
			break;

		case 86:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(UNSURE, "I heard that "
						+ Settings.SERVER_NAME + " used to have three gods.");
				stage = 87;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY,
						"I wanna know about the wars of the gods!");
				stage = 92;
				break;
			case OPTION_3:
				sendPlayerDialogue(UNSURE, "What are the Mahjarrat?");
				stage = 97;
				break;
			case OPTION_4:
				sendPlayerDialogue(HAPPY,
						"Can I wield the power of Saradomin myself?");
				stage = 100;
				break;
			}
			break;
		case 87:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Indeed. That was correct:",
					"Saradomin, Guthix and Zamorak.");
			stage = 88;
			break;
		case 88:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Saradomin, the great and glorious,",
					"gives life to this world.");
			stage = 89;
			break;
		case 89:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Zamorak craves only death and destruction.");
			stage = 90;
			break;
		case 90:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Guthix, calling itself a god of 'balance', held no allegiance,",
					"but simply aided whatever cause suits its shifting purpose.");
			stage = 91;
			break;
		case 91:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Guthix, however, died not so long ago. Zamorak, too,",
					"after being defeated by Saradomin, was banished from",
					"this world.");
			stage = 31;
			break;

		case 92:
			sendNPCDialogue(npcId, SAD,
					"Ah, the first war was a terrible time. The armies of",
					"Saradomin fought gloriously against the minions of",
					"Zamorak, but many brave warriors and noble cities were",
					"overthrown and destroyed utterly.");
			stage = 93;
			break;
		case 93:
			sendPlayerDialogue(CONFUSED, "How did it end?");
			stage = 94;
			break;
		case 94:
			sendNPCDialogue(npcId, UNSURE,
					"Before the Zamorakian forces could be utterly routed,",
					"Lord Saradomin took pitty on them and the battle-scarred",
					"world, and allowed a truce.");
			stage = 95;
			break;
		case 95:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"All was quiet for thousands of years, but not so long ago",
					"Zamorak and Saradomin both returned to Geilinor!");
			stage = 96;
			break;
		case 96:
			sendNPCDialogue(npcId, HAPPY,
					"Fortunately, the power of Lord Saradomin was sufficient",
					"to send Zamorak back to where he came from.");
			stage = 31;
			break;

		case 97:
			sendNPCDialogue(
					npcId,
					SAD,
					"Verry little is written about the tribe of the Mahjarrat.",
					"They are believed to be from the realm of Freneskae, or",
					"Frenaskrae - the spelling in this tongue is only",
					"approximate.");
			stage = 98;
			break;
		case 98:
			sendNPCDialogue(npcId, SAD,
					"One of them, the foul Zamorak, has achieved godhood,",
					"although none know how this came about.");
			stage = 99;
			break;
		case 99:
			sendNPCDialogue(npcId, SAD,
					"Other Mahjarrat who have been particularly active upon",
					"this plane are Hazeel, Lucien, Azzanadra, and Zamouregal.");
			stage = 31;
			break;

		case 100:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"If you travel to the Mage Arena in the north-west reaches",
					"of the Wilderness, the battle mage Kolodion may be willing",
					"to let you learn to summon the power of Saradomin,",
					"should you be able to pass his test.");
			stage = 31;
			break;

		case 101:
			sendNPCDialogue(npcId, HAPPY,
					"Why, thank you! I rather like it myself.");
			stage = 102;
			break;
		case 102:
			sendOptionsDialogue(DEFAULT,
					"You stole it off that woman in the bank!",
					"How can I get a hat like that?");
			stage = 103;
			break;
		case 103:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(ANGRY,
						"You stole it off that woman in the bank!",
						"You stole all this valuable stuff to!");
				stage = 104;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Ahh, sadly these hats are now very rare. Maybe some",
						"other player would be willing to sell you theirs, but it would",
						"be incredibly expensive.");
				break;
			}
			break;
		case 104:
			sendNPCDialogue(npcId, WORRIED, "Stole it? How could you possibly",
					"think I did such a thing?");
			stage = 105;
			break;
		case 105:
			sendPlayerDialogue(ANGRY, "I saw you robbing the bank!",
					"You killed all those people!");
			stage = 106;
			break;
		case 106:
			sendNPCDialogue(npcId, SAD, "Deary me, " + player.getDisplayName()
					+ ", your imagination is running wild! What",
					"could make you think you saw me do that?");
			stage = 107;
			break;
		case 107:
			sendPlayerDialogue(
					ANGRY,
					"I've seen a security recording that shows you robbing the",
					"bank. The bank's guard showed it to me.");
			stage = 108;
			break;
		case 108:
			sendNPCDialogue(npcId, UNSURE,
					"You've seen the bank's security recording?");
			stage = 109;
			break;
		case 109:
			sendNPCDialogue(
					npcId,
					SAD,
					"Tut tut tut... Oh well, at least you'll never be able to tell",
					"the bank about me. They'll never listen.");
			stage = 110;
			break;
		case 110:
			sendPlayerDialogue(ANGRY,
					"So you're just going to get away with it?");
			stage = 111;
			break;
		case 111:
			sendNPCDialogue(npcId, PLAIN_TALKING, "That's my plan, yes.");
			stage = 112;
			break;
		case 112:
			sendPlayerDialogue(SAD, "But that's... well, it's WRONG!");
			stage = 113;
			break;
		case 113:
			sendNPCDialogue(npcId, ANGRY,
					"Wrong? WRONG? I'll tell you what's wrong!");
			stage = 114;
			break;
		case 114:
			sendNPCDialogue(
					npcId,
					ANGRY,
					"I've spent my whole life travelling the world, doing quests",
					"for people, saving lives, saving villages from terrifying",
					"monsters and all that sort of thing.");
			stage = 115;
			break;
		case 115:
			sendNPCDialogue(
					npcId,
					ANGRY,
					"Now I'm old, and where do I have to live? In this freezing",
					"old house next to a pig-sty, with a bunch of yobs outside",
					"who can't keep their hands off the market stall! What",
					"sort of reward is that?");
			stage = 116;
			break;
		case 116:
			sendNPCDialogue(npcId, ANGRY,
					"So don't talk to me about right and wrong!");
			stage = 117;
			break;
		case 117:
			sendPlayerDialogue(ANGRY, "Maybe someone SHOULD talk to you",
					"about right and wrong...");
			stage = 118;
			break;
		case 118:
			sendNPCDialogue(npcId, ANGRY, "Bah!");
			stage = 119;
			break;
		case 119:
			sendPlayerDialogue(ANGRY, "Hmmph!");
			stage = 120;
			break;
		case 120:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Now you've got that off your chest, would you like to ask",
					"me about anything else?");
			stage = 121;
			break;
		case 121:
			sendOptionsDialogue(DEFAULT, "How can I get a hat like that?",
					"Yes please.", "Thanks, maybe some other time.");
			stage = 122;
			break;
		case 122:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Ahh, sadly these hats are now very rare. Maybe some",
						"other player would be willing to sell you theirs, but it would",
						"be incredibly expensive.");
				stage = 123;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Yes please.");
				stage = 21;
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 123:
			sendPlayerDialogue(CONFUSED, "Can I buy your hat?");
			stage = 124;
			break;
		case 124:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"Ohhh no, I don't intend to part with this.",
					"Would you like to ask me about something else?");
			stage = 125;
			break;
		case 125:
			sendOptionsDialogue(DEFAULT, "You should give it back, you know.",
					"Yes please.", "Thanks, maybe some other time.");
			stage = 126;
			break;
		case 126:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"No, I think I'll keep it.");
				stage = 127;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Yes please.");
				stage = 21;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Thanks, maybe some other time.");
				stage = -2;
				break;
			}
			break;
		case 127:
			sendPlayerDialogue(CONFUSED, "But...");
			stage = 120;
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