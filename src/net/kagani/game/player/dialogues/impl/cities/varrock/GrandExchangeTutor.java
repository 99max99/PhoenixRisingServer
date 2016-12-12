package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * 
 * @author Ethan
 *
 */

public class GrandExchangeTutor extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "How can I help?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendPlayerDialogue(9827,
					"Can you teach me about the Grand Exchange again?");
			break;
		case 0:
			stage = 1;
			sendNPCDialogue(npcId, 9827, "Of course I can.");
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(
					npcId,
					9827,
					"The building you see here is the Grand Exchange. You can simply tell us what you want to buy or sell and for how much, and we'll pair you up with another player and make the trade for you!");
			break;
		case 2:
			stage = 3;
			sendNPCDialogue(
					npcId,
					9827,
					"Buying and selling is done in a very similar way. Let me describe it in five steps.");
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(
					npcId,
					9827,
					"Step 1: You decide what to buy or sell and come here with the items to sell or the money to buy with");
			break;
		case 4:
			stage = 5;
			sendNPCDialogue(
					npcId,
					9827,
					" Step 2: Speak with one of the clerks, behind the desk in the middle of the building and they will guide you through placing the bid and the finer details of what you are looking for.");
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(
					npcId,
					9827,
					"Step 3: The clerks will take the items or money off you and look for someone to complete the trade.");
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(
					npcId,
					9827,
					"Step 4: You then need to wait perhaps a matter of moments or maybe even days until someone is looking for what you have offered.");
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(
					npcId,
					9827,
					"Step 5: When the trade is complete, we will let you know with a message and you can pick up your winnings by talking to the clerks or by visiting any bank in RuneScape.");
			break;
		case 8:
			stage = 9;
			sendOptionsDialogue(DEFAULT,
					"Where can I find out more information?", "Okay, thanks.");
			break;
		case 9:
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827, "Where can I find more information?");
				stage = 10;
				break;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(9827, "Okay, thanks.");
				stage = 100;
				break;
			}
		case 10:
			stage = 11;
			sendNPCDialogue(
					npcId,
					9827,
					"Go and speak to Brugsen who's standing over there, closer to the building. He'll help you out.");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(npcId, 9827, "Anything else I can help with?");
			break;
		case 12:
			stage = 13;
			sendPlayerDialogue(9827, "I'm fine thanks.");
			break;
		case 13:
			stage = 100;
			sendNPCDialogue(npcId, 9827, "Fair enough.");
			break;
		case 100:
			end();
			break;
		}
	}

	@Override
	public void finish() {
	}
}
