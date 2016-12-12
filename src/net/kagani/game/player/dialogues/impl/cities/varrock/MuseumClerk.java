package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.Animation;
import net.kagani.game.player.dialogues.Dialogue;

public class MuseumClerk extends Dialogue {

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Welcome to Varrock Museum. How can I help you today?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Take a map of the Museum.",
					"Find out about the Dig Site exhibit.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendItemDialogue(11184,
						"You reach and take a map of the Museum.");
				player.setNextAnimation(new Animation(832));
				player.getInventory().addItem(11184, 1);
				stage = 50;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL,
						"Could you tell me about the Dig Site exhibit please?");
				stage = 1;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Of course. The Dig Site exhibit has several display cases",
					"of finds discovered on the Dig Site to the east of Varrock.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(
					npcId,
					UPSET,
					"As you are on a free world, you can't help us with cleaning",
					"finds and putting them in their display cases, but do feel",
					"free to take a look around at the displays available. You'll",
					"find out a little about the things we've found on the Dig");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, UPSET,
					"Site from the 3rd and 4th Ages - all really quite",
					"fascinating.");
			stage = 4;
			break;
		case 4:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"I'd like to talk about something else.", "Bye");
			stage = 5;
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL,
						"I'd like to talk about something else.");
				stage = -1;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY, "Bye.");
				stage = 6;
				break;
			}
			break;
		case 6:
			sendNPCDialogue(npcId, HAPPY, "Have a good day!");
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
