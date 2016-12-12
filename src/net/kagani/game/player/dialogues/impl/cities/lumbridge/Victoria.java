package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class Victoria extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(HAPPY, "Good day.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, HAPPY,
					"To you too, traveller. I am Victoria. Tell me, have you",
					"seen my brother, Lachtopher, around the town?");
			stage = 0;
			break;
		case 0:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Yes, I've seen Lachtopher", "No, I haven't seen him.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						SAD,
						"Ah, he'll have asked you for money, no doubt. I hope you",
						"didn't give him any.");
				stage = 2;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						HAPPY,
						"Well, if you do meet him, he'll ask you for money, no",
						"doubt. Please don't give him any.");
				stage = 18;
				break;
			}
			break;
		case 2:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"No, I didn't give him a single coin.",
					"Yes, I loaned him money, just like he asked.");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						NORMAL,
						"Oh, good! If you had, then you would never have got it",
						"back. My brother is such a waste of space. I've been",
						"lending him things for years and he never gives them",
						"back.");
				stage = 4;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						SAD,
						"Oh dear. I'm sorry to tell you this, but that's the last",
						"you'll see of that money. My brother is such a waste of",
						"space. I've been lending him things for years, and he never",
						"gives them back.");
				stage = 4;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, SAD,
					"Yes, but it never used to be this bad. You see...");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Lachtopher used to live on the east side of the river,",
					"before it was overrun with goblins. Although he didn't have",
					"a steady job, he used to help out around farms when he",
					"needed cash.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(
					npcId,
					SAD,
					"Then, one day, the Duke told us it was no longer safe to",
					"live on the east riverbank, so some villagers had to move",
					"across here.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					SAD,
					"With no money for lodgings, and nowhere else to go,",
					"Lachtopher came to live with me. I've only a small house,",
					"so he sleeps downstairs on the floor.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(SAD,
					"Goodness. That sounds quite uncomfortable.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, ANGRY, "Not uncomfortable enough, it seems.");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(
					npcId,
					SAD,
					"I thought he'd only be staying for a couple of weeks, just",
					"until he'd got some money together, but he's been here",
					"for ages now.");
			stage = 11;
			break;
		case 11:
			sendPlayerDialogue(CONFUSED,
					"So, why not just throw him out on to the streets?");
			stage = 12;
			break;
		case 12:
			sendNPCDialogue(npcId, SAD,
					"Oh, no! I couldn't do that to my brother.");
			stage = 13;
			break;
		case 13:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Besides, my parents taught me to support and care for",
					"those in need. I'm sure that, if I try hard enough, I can",
					"change my brother's ways.");
			stage = 14;
			break;
		case 14:
			sendNPCDialogue(
					npcId,
					ANGRY,
					"That doesn't mean he's having any more money out of",
					"me, however. He can have a roof over his head, but that's",
					"all.");
			stage = 15;
			break;
		case 15:
			sendPlayerDialogue(HAPPY,
					"Good luck with that. I don't think Lachtopher deserves a",
					"sister like you.");
			stage = 16;
			break;
		case 16:
			sendNPCDialogue(npcId, HAPPY,
					"Such kind words. Thank you. Remember: don't give him",
					"any money - tell him to get a job instead.");
			stage = 17;
			break;
		case 17:
			sendPlayerDialogue(HAPPY, "Okay, I'll try to remember that.");
			stage = 30;
			break;
		case 18:
			sendPlayerDialogue(CONFUSED, "Why not?");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(
					npcId,
					SAD,
					"Sorry to tell you this, but if you lend him money you'll",
					"never see it again. My brother is such a waste of space.",
					"I've been lending him things for years and he never gives",
					"them back.");
			stage = 4;
			break;
		case 30:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
	}

}
