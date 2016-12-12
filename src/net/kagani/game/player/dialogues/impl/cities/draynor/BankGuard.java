package net.kagani.game.player.dialogues.impl.cities.draynor;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The class that represents the dialogue for the NPC - Bank Guard
 * 
 * @author Mod Austin
 * @version 1.0 3/5/2015
 * @contact@deviouscoding@gmail.com
 */

public class BankGuard extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING, "Yes?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT, "Can I deposit my stuff here?",
					"That wall doesn't look very good.",
					"Sorry, I don't want anything.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(HAPPY, "Hello. Can I deposit my stuff here?");
				stage = 1;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, PLAIN_TALKING, "No, it doesn't.");
				stage = 5;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Sorry, I don't want anthing.");
				stage = 27;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"No. I'm a security guard, not a bank clerk.");
			stage = 2;
			break;
		case 2:
			sendOptionsDialogue(DEFAULT, "That wall doesn't look very good.",
					"Alright, I'll stop bothering you now.");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED,
						"That wall doesn't look very good.");
				stage = 4;
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING, "No, it doesn't.");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue(DEFAULT,
					"Are you going to tell me what happend?",
					"Alright, I'll stop bothering you now.");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, PLAIN_TALKING, "I could do.");
				stage = 7;
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 7:
			sendPlayerDialogue(HAPPY, "Okay, go on!");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Someone smashed the wall when",
					"they were robbing the bank.");
			stage = 9;
			break;
		case 9:
			sendPlayerDialogue(WORRIED, "Someone's robbed the bank?");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Yes.");
			stage = 11;
			break;
		case 11:
			sendPlayerDialogue(WORRIED, "But... was anyone hurt?",
					"Did they get anything valuable?");
			stage = 12;
			break;
		case 12:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes, but we were able to get more staff and mend the",
					"wall easily enough.");
			stage = 13;
			break;
		case 13:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"The Bank has already replaced all the stolen items that",
					"belonged to customers.");
			stage = 14;
			break;
		case 14:
			sendPlayerDialogue(WORRIED,
					"Oh, good... but the bank staff got hurt?");
			stage = 15;
			break;
		case 15:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes, but the new ones are just as good.");
			stage = 16;
			break;
		case 16:
			sendPlayerDialogue(NORMAL, "You're not very nice, are you?");
			stage = 17;
			break;
		case 17:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"No-one's expecting me to be nice.");
			stage = 18;
			break;
		case 18:
			sendPlayerDialogue(CONFUSED,
					"Anyway... So, someone's robbed the bank?");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Yes.");
			stage = 20;
			break;
		case 20:
			sendPlayerDialogue(CONFUSED, "Do you know who did it?");
			stage = 21;
			break;
		case 21:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"We are fairly sure we know who the robber was. The",
					"security recording was damaged in the attack, but it still",
					"shows his face clearly enough.");
			stage = 22;
			break;
		case 22:
			sendPlayerDialogue(CONFUSED, "You've got a security recording?");
			stage = 23;
			break;
		case 23:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes. Our insurers insisted that we",
					"install a magical scrying orb.");
			stage = 24;
			break;
		case 24:
			sendOptionsDialogue(DEFAULT, "So do you know who the robber was?",
					"Nevermind.");
			stage = 25;
			break;
		case 25:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, PLAIN_TALKING,
						"I can't disclose that information.");
				stage = 26;
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 26:
			sendPlayerDialogue(NORMAL, "Alright, I'll stop bothering you now.");
			stage = 50;
			break;
		case 27:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Ok.");
			stage = 50;
			break;
		case 50:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}
