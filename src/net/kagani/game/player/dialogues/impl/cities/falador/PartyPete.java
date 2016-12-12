package net.kagani.game.player.dialogues.impl.cities.falador;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

/**
 * The class that represents the dialogue for the NPC - Party Pete
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class PartyPete extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Hi! I'm Party Pete. Welcome to the Party Room!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "So, what's this room for?",
					"What's the big lever over there for?",
					"What's the gold chest for?", "I wanna party!", "More.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "So, what's this room for?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(CONFUSED,
						"What's the big lever over there for?");
				stage = 6;
				break;
			case OPTION_3:
				sendPlayerDialogue(CONFUSED, "What's the gold chest for?");
				stage = 12;
				break;
			case OPTION_4:
				sendPlayerDialogue(HAPPY, "I wanna party!");
				stage = 16;
				break;
			case OPTION_5:
				sendOptionsDialogue(DEFAULT, "I love your hair!",
						"Why's there a chameleon in here?", "Back");
				stage = 18;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"This room is for partying the night away!");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(LISTENS_THEN_LAUGHS,
					"How do you have a party in MaxScape830?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Get a few mates round, get the beers in and have fun!");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Some players organise parties so keep an eye open!");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(HAPPY, "Woop! Thanks Pete!");
			stage = 50;
			break;
		case 6:
			sendNPCDialogue(npcId, HAPPY,
					"Simple. With the lever you can do some fun stuff.");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(CONFUSED, "What kind of stuff?");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(npcId, HAPPY,
					"A balloon drop costs 1000 gold. For this, you get 200",
					"balloons dropped across the whole party room. You",
					"can then have fun popping the balloons!");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Any items in the Party Drop Chest will be put into balloons",
					"as soon as you pull the lever.");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"When the balloons are released, you can burst them to",
					"get at the items!");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(npcId, HAPPY,
					"For 500 gold, you can summon the Party Room Knights,",
					"who will dance for your delight. Their singing isn't a",
					"delight, though!");
			stage = 50;
			break;
		case 12:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Any items in the chest will be dropped inside the balloons",
					"when you pull the lever.");
			stage = 13;
			break;
		case 13:
			sendPlayerDialogue(HAPPY,
					"Cool! Sounds like a fun way to do a drop party.");
			stage = 14;
			break;
		case 14:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS, "Exactly!");
			stage = 15;
			break;
		case 15:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"A word of warning, though. Any items that you put into",
					"the chest can't be taken out again, and it costs 1000 gold",
					"pieces for each drop party.");
			stage = 50;
			break;
		case 16:
			sendNPCDialogue(npcId, HAPPY,
					"I've won the Dance Trophy at the Kandarin Ball three",
					"years in a trot!");
			stage = 17;
			break;
		case 17:
			sendPlayerDialogue(LISTENS_THEN_LAUGHS, "Show me your moves Pete!");
			stage = 50;
			break;
		case 18:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "I love your hair!");
				stage = 19;
				break;
			case OPTION_2:
				sendPlayerDialogue(CONFUSED, "Why's there a chameleon in here?");
				stage = 22;
				break;
			case OPTION_3:
				stage = -1;
				break;
			}
			break;
		case 19:
			sendNPCDialogue(
					npcId,
					LISTENS_THEN_LAUGHS,
					"Isn't it groovy? I liked it so much, I had extras made for",
					"my party goers. Would you like to buy one?");
			stage = 20;
			break;
		case 20:
			sendOptionsDialogue(DEFAULT, "Yes.", "No.");
			stage = 21;
			break;
		case 21:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 160);
				end();
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 22:
			sendNPCDialogue(
					npcId,
					LISTENS_THEN_LAUGHS,
					"Karma's my pet. I got him for Christmas one year. He",
					"keeps the Party Room free of flies, and he loves watching",
					"me dance. Karma karma karma cha...");
			stage = 23;
			break;
		case 23:
			sendOptionsDialogue(DEFAULT, "Can you talk to him?",
					"Christmas is over.", "Aww, that's nice.");
			stage = 24;
			break;
		case 24:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "Can you talk to him?");
				stage = 25;
				break;
			case OPTION_2:
				sendPlayerDialogue(CONFUSED,
						"Christmas is over. Why've you still got him hanging",
						"around?");
				stage = 28;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Aww, that's nice.");
				stage = 50;
				break;
			}
			break;
		case 25:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Sure, I talk to the little fellow all the time. My Summoning",
					"level's not high enough to understand what he says back",
					"but he's still great company.");
			stage = 26;
			break;
		case 26:
			sendOptionsDialogue(DEFAULT, "Christmas is over.",
					"Aww, that's nice.");
			stage = 27;
			break;
		case 27:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED,
						"Christmas is over. Why've you still got him hanging",
						"around?");
				stage = 28;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Aww, that's nice.");
				stage = 50;
				break;
			}
			break;
		case 28:
			sendNPCDialogue(npcId, SCARED,
					"I couldn't chuck the little chapp out! A pet is for life!");
			stage = 29;
			break;
		case 29:
			sendOptionsDialogue(DEFAULT, "Can you talk to him?",
					"Aww, that's nice.");
			stage = 30;
			break;
		case 30:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Sure, I talk to the little fellow all the time. My Summoning",
						"level's not high enough to understand what he says back",
						"but he's still great company.");
				stage = 26;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Aww, that's nice.");
				stage = 50;
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
