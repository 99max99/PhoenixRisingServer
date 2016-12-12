package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class GurdianMummy extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private int option;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		option = (int) parameters[1];
		if (player.getInventory().containsItem(9050, 1)) {
			stage = 18;
			sendPlayerDialogue(NORMAL,
					"This sceptre seems to have run out of charges.");
			return;
		}
		switch (option) {
		case 1:
			stage = 1;
			sendNPCDialogue(npcId, NORMAL, "*sigh* Not another one.");
			break;
		case 2:
			if (!player.getInventory().containsItem(9050, 1)) {
				stage = -1;
				sendNPCDialogue(npcId, ANGRY,
						"You don't have any sceptres to rechage.");
				return;
			}
			stage = 18;
			sendPlayerDialogue(NORMAL,
					"This sceptre seems to have run out of charges.");
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendPlayerDialogue(QUESTIONS, "Another what?");
			break;
		case 2:
			stage = 3;
			sendNPCDialogue(npcId, NORMAL, "Another 'archeaologist'.");
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(npcId, NORMAL,
					"I'm not going to let you plunder my master's tomb you know.");
			break;
		case 4:
			stage = 5;
			sendPlayerDialogue(QUESTIONS,
					"That's a shame. Have you got anything else I could do while I'm here?");
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"If it will keep you out of mischief I suppose I could set something up for you...");
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"I have a few rooms full of some things you humans might consider valueable, do you want to give it a go?");
			break;
		case 7:
			stage = 8;
			sendOptionsDialogue("Hmm, Pyramid Plunder or Questions?",
					"I'd rather talk about artefacts a bit more.",
					"The game sounds like fun; what do I do?",
					"Not right now.",
					"I know what I'm doing, so let's get on with it.");
		case 8:
			switch (componentId) {
			case OPTION_1:
				stage = 9;
				sendPlayerDialogue(NORMAL,
						"I'd rather talk about artefacts a bit more, on behalf of a...friend.");
				break;
			case OPTION_2:
				stage = 10;
				sendPlayerDialogue(HAPPY,
						"The game sounds like fun; what do I do?");
				break;
			case OPTION_3:
				stage = 9;
				sendPlayerDialogue(NORMAL, "Not right now.");
				break;
			case OPTION_4:
				stage = -1;
				sendNPCDialogue(npcId, NORMAL,
						"Search the anonymous looking door behind you.");
				break;
			}
			break;
		case 9:
			stage = -1;
			sendNPCDialogue(npcId, ANGRY, "Well, get out of then.");
			break;
		case 10:
			stage = 11;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"You have five minutes to explore the treasure rooms and collect as many artefacts as you can.");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(npcId, NORMAL,
					"The artefacts are in the urns, chests and sarcophagi found in each room.");
			break;
		case 12:
			stage = 13;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"There are eight treasure rooms, each subsequent room requires higher thieving skills to both enter the room and thieve from the urns and other containers.");
			break;
		case 13:
			stage = 14;
			sendNPCDialogue(npcId, NORMAL,
					"The rewards also become more lucrative the further into the tomb you go.");
			break;
		case 14:
			stage = 15;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"You will also have to deactivate a trap in order to enter the main part of each room.");
			break;
		case 15:
			stage = 16;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"When you want to move onto the next room you need to find the correct door first.");
			break;
		case 16:
			stage = 17;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"There are four possibile exists... you must open the door before finding out whetever it is the exit or not.");
			break;
		case 17:
			stage = 7;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Opening the doors require picking their locks. Having a lockpick will make this easier.");
			break;
		case 18:
			stage = 19;
			sendNPCDialogue(npcId, ANGRY,
					"You shouldn't have that thing in the first place, thief!");
			break;
		case 19:
			stage = 20;
			sendPlayerDialogue(
					NORMAL,
					"If I gave you back some of the artefacts I've taken from the tomb, would you recharge the sceptre for me?.");
			break;
		case 20:
			stage = 21;
			sendNPCDialogue(
					npcId,
					ANGRY,
					"*sigh* Oh, alright. But only if the sceptre is fully empty, I'm not wasting the King's magic...");
			break;
		case 21:
			stage = 22;
			sendOptionsDialogue("Recharge this sceptre with:", "100,000gp.",
					"Actually I'm more interested in plundering the tombs.");
			break;
		case 22:
			switch (componentId) {
			case OPTION_1:
				if (player.getInventory().hasCoinAmount(100000)) {
					stage = -1;
					sendItemDialogue(9044,
							"You recharge your sceptre with 100,000gp.");
					player.getInventory().deleteCoinAmount(100000);
					player.getInventory().deleteItem(9050, 1);
					player.getInventory().addItem(9044, 1);
				} else {
					stage = -1;
					sendNPCDialogue(npcId, ANGRY,
							"You need to have 100,000gp to recharge your sceptre.");
				}
				break;
			case OPTION_2:
				stage = 7;
				sendPlayerDialogue(NORMAL,
						"Actually I'm more interested in plundering the tombs.");
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}