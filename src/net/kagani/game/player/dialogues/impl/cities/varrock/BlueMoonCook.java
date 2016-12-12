package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class BlueMoonCook extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, MILDLY_ANGRY, "What do you want? I'm busy!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Can you sell me any food?",
					"Can you give me any free food?",
					"I don't want anything from this horrible kitchen.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Can you sell me any food?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Can you give me any free food?");
				stage = 7;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL,
						"I don't want anything from this horrible kitchen.");
				stage = 11;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"I suppose I could sell you some cabbage, if you're willing to",
					"pay for it. Cabbage is good for you.");
			stage = 2;
			break;
		case 2:
			if (player.getInventory().containsItem(995, 1)) {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"Alright, I'll buy a cabbage.",
						"No thanks, I don't like cabbage.");
				stage = 3;
			} else {
				sendPlayerDialogue(UPSET, "Oh, I haven't got any money.");
				stage = 5;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Alright, I'll buy a cabbage.");
				stage = 4;
				break;
			case OPTION_2:
				sendPlayerDialogue(UPSET, "No thanks, I don't like cabbage.");
				stage = 6;
				break;
			}
			break;
		case 4:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"It's a deal. Now, make sure you eat is all up. Cabbage is",
					"good for you.");
			player.getInventory().addItem(1965, 1);
			player.getInventory().deleteItem(995, 1);
			stage = 50;
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					MILDLY_ANGRY,
					"Why are you asking me to sell you food if you haven't got",
					"any money? Go away!");
			stage = 50;
			break;
		case 6:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"Bah! People these days only appreciate junk food.");
			stage = 50;
			break;
		case 7:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"Can you give me any free money?");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(CONFUSED, "Why should I give you free money?");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"Why should I give you free food?");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(PLAIN_TALKING, "Oh, forget it.");
			stage = 50;
			break;
		case 11:
			sendNPCDialogue(
					npcId,
					MILDLY_ANGRY,
					"How dare you? I put alot of effort into cleaning this",
					"kitchen. My daily sweat and elbow-grease keep this kitchen",
					"clean!");
			stage = 12;
			break;
		case 12:
			sendPlayerDialogue(WORRIED, "Ewww!");
			stage = 13;
			break;
		case 13:
			sendNPCDialogue(npcId, MILDLY_ANGRY, "Oh, just leave me alone.");
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
