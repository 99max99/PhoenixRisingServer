package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;

public class LadyHefin extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		npcId = (int) parameters[0];
		stage = 1;
		sendNPCDialogue(npcId, NORMAL, "Praise Seren for sending you, human.",
				"<br>You are the savior of our city.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Ask about Lady Hefin...", "Ask about Clan Hefin...",
					"Ask about Hefin skills...", "Nothing, thanks.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				sendPlayerDialogue(QUESTIONS, "Can you tell me about yourself?");
				break;
			case OPTION_2:
				stage = 5;
				sendPlayerDialogue(QUESTIONS,
						"Can you tell me about Clan Herifin?");
				break;
			case OPTION_3:
				stage = 8;
				sendPlayerDialogue(QUESTIONS,
						"Can you tell me about Clan Helfin's skills?");
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"I am marely another traveller on the journey to enlightenment. It is a road that few dare walk, but the light of Seren shall show us the way.");
			break;
		case 4:
			stage = 1;
			sendNPCDialogue(npcId, NORMAL,
					"Who I am is not important. Nor indeed my origin.",
					"To focus on what we have inhibits our potential to become something more.");
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(npcId, NORMAL,
					"Where other clans build or fight or destroy, clan Hefin looks inward.");
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"We contemtplate the physical and spiritual world, and meditate upon the wisdom of Seren.");
			break;
		case 7:
			stage = 1;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"We strengthen our bodies and our minds, that we may be enlightened by her grace and the beauty of her spirit.");
			break;
		case 8:
			stage = 9;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"We seek to cleanse our minds in prayer, and hone our bodies through agility exercises.");
			break;
		case 9:
			stage = 1;
			sendNPCDialogue(
					npcId,
					HAPPY,
					"We welcome all who crave enlightenment, and provide for their physical and spiritual needs.");
			break;
		}
	}

	@Override
	public void finish() {

	}
}