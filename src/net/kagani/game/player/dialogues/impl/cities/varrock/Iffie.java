package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Iffie extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Hello, dearie! Were you wanting to collect a costume, or is",
				"there something else I can do for you today?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"I've come for a costume.", "Aren't you selling anything?",
					"I just came for a chat.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, HAPPY,
						"Some of these costumes even come with a free emote!");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Aren't you selling anything?");
				stage = 3;
				break;
			case OPTION_3:
				sendPlayerDialogue(HAPPY, "I just came for a chat.");
				stage = 4;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, HAPPY,
					"Just buy one piece of the mime or zombie costumes and",
					"I'll show you all the relevant moves.");
			stage = 2;
			break;
		case 2:
			ShopsHandler.openShop(player, 161);
			end();
			break;
		case 3:
			sendNPCDialogue(npcId, LISTENS_THEN_LAUGHS,
					"Oh, yes, but only costumes.",
					"Thessalia sells some other clothes and runs the makeover",
					"service.");
			stage = 50;
			break;
		case 4:
			sendNPCDialogue(npcId, UPSET,
					"Oh, I'm sorry, but I'll never get my knitting",
					"done if I stop for a chit-chat with every young lad",
					"who wanders through the shop!");
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
