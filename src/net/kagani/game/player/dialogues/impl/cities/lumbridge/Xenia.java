package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class Xenia extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING,
				"Im glad you've come by. I need some help.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"What help do you need?", "Who are you?",
					"How did you know who I am?", "Sorry, I've got to go.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						ANGRY,
						"Some cultists of Zamorak have gone into the catacombs",
						"with a prisoner. I don't know what they're planning, but",
						"I'm pretty sure it's not a tea party.");
				stage = 1;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"My name's Xenia. I'm an adventurer.");
				stage = 7;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Oh, I have my ways. I get the feeling you're one to watch;",
						"you could be quite to hero some day.");
				stage = 2;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "Sorry, I've got to go.");
				stage = 50;
				break;

			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					SAD,
					"There are three of them, and I'm not as young as the last",
					"time I was here. I don't want to go down there without",
					"backup.");
			stage = 2;
			break;
		case 2:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "I'll help you.",
					"I need to know more before I help you.", "Who are you?",
					"How did you know who I am?", "Sorry, I've got to go.");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "I'll help you.");
				stage = 50;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Very wise. I got into a lot of trouble in my youth by",
						"rushing in without knowing a situation.");
				stage = 4;
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"My name's Xenia. I'm an adventurer.");
				stage = 7;
				break;
			case OPTION_4:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Oh, I have my ways. I get the feeling you're one to watch;",
						"you could be quite to hero some day.");
				stage = 2;
				break;
			case OPTION_5:
				sendPlayerDialogue(NORMAL, "Sorry, I've got to go.");
				stage = 50;
				break;
			}
			break;
		case 4:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Tell me more about these cultists.",
					"Who did they kidnap?", "What's down there?",
					"Is there a reward if I help you?", "Enough questions.");
			stage = 5;
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Lumbridge is a Saradominist town, but there will always be",
						"some people drawn to worship Zamorak. They must have",
						"found some ritual that they think will give them power",
						"over other people.");
				stage = 4;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						SAD,
						"A young woman named llona. She had just left Lumbridge",
						"to apprentice at the Wizards' Tower.");
				stage = 6;
				break;
			case OPTION_3:
				sendNPCDialogue(
						npcId,
						SAD,
						"The catacombs of Lumbridge Church. The dead of",
						"Lumbridge have been buried there since...well, for about",
						"forty years now.");
				stage = 4;
				break;
			case OPTION_4:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"The cultists all have weapons, and you'll be able to keep",
						"them if we succeed. This adventure will also help to train",
						"your combat skills.");
				stage = 4;
				break;
			case OPTION_5:
				sendNPCDialogue(npcId, CONFUSED,
						"So, will you help me, adventurer?");
				stage = 2;
				break;
			}
			break;
		case 6:
			sendNPCDialogue(
					npcId,
					ANGRY,
					"They grabbed her on the road. Without training she didn't",
					"have a chance.");
			stage = 4;
			break;
		case 7:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"I'm one of the old guard, I suppose. I helped found the",
					"Champions' Guild, and I've done a fair few quests in my",
					"time.");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Now I'm starting to get a bit old for action, which is why I",
					"need your help.");
			stage = 9;
			break;
		case 9:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"What help do you need?", "How did you know who I am?",
					"Sorry, I've got to go.");
			stage = 10;
			break;
		case 10:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(
						npcId,
						ANGRY,
						"Some cultists of Zamorak have gone into the catacombs",
						"with a prisoner. I don't know what they're planning, but",
						"I'm pretty sure it's not a tea party.");
				stage = 1;
				break;
			case OPTION_2:
				sendNPCDialogue(
						npcId,
						PLAIN_TALKING,
						"Oh, I have my ways. I get the feeling you're one to watch;",
						"you could be quite to hero some day.");
				stage = 2;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Sorry, I've got to go.");
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
