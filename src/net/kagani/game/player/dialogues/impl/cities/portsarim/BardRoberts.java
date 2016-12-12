package net.kagani.game.player.dialogues.impl.cities.portsarim;

import net.kagani.game.player.dialogues.Dialogue;

public class BardRoberts extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue(DEFAULT, "Who are you?",
				"Can I ask you some questions about resting?",
				"Can I ask you sone questions about running?",
				"That's all for now.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Who are you?");
				stage = 0;
				break;
			case OPTION_2:
				sendOptionsDialogue(DEFAULT, "How does resting work?",
						"What's special about resting by a musician?",
						"Can you summarise the effects for me?",
						"That's alll for now.");
				stage = 5;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL,
						"Can I ask you some questions about running?");
				stage = 17;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "That's all for now.");
				stage = 15;
				break;
			}
			break;
		case 0:
			sendNPCDialogue(npcId, HAPPY,
					"Arr, the name be Bard Roberts, an' me companion here be",
					"Stanky Morgan. Pleased to meet ye!");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(6667, BLANK, "Ecstatic.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(NORMAL, "So, what do you do?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"I play music an' sing, mostly. Ye can sit down and rest a",
					"while, if ye like.");
			stage = 4;
			break;
		case 4:
			end();
			player.getDialogueManager().startDialogue("BardRoberts",
					parameters[0]);
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "So, how does resting work?");
				stage = 6;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY,
						"What's special about resting by a musician?");
				stage = 13;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL,
						"Can you summarise the effects for me?");
				stage = 14;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "That's all for now.");
				stage = 15;
				break;
			}
			break;
		case 6:
			sendNPCDialogue(
					6667,
					MILDLY_ANGRY,
					"What do ye mean, 'how does resting work'? Rest an' work",
					"are opposing concepts: restin' don't work. Ye get tired, ye",
					"sit down, ye regain yer energy. No work involved.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, MILDLY_ANGRY,
					"What Stanky means is, choose the rest option on yer run",
					"button to start restin'.");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"When ye rest, ye'll recharge run energy an' life points",
					"faster than normal.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Restin' by a musician such as mesel-");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(6667, BLANK, "Huh! Musician? That be charitable.");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(
					npcId,
					MILDLY_ANGRY,
					"Restin' by a musician such as meself will have a similar but",
					"more potent effect.");
			stage = 12;
			break;
		case 12:
			sendOptionsDialogue(DEFAULT, "How does resting work?",
					"What's special about resting by a musician?",
					"Can you summarise the effects for me?",
					"That's alll for now.");
			stage = -1;
			break;
		case 13:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Music soothes the mind an' relaxes ye, "
							+ player.getDisplayName() + ".",
					"Restin' by a musician will restore yer energy an' life points",
					"even faster than restin' anywhere else.");
			stage = 12;
			break;
		case 14:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Choose rest on yer run button te sit down. Yer life points",
					"an' energy will come back faster. If yer near enough te a",
					"musician, such as me'self, they'll come back even faster",
					"still.");
			stage = 12;
			break;
		case 15:
			sendNPCDialogue(npcId, HAPPY,
					"Come back soon, " + player.getDisplayName() + "!");
			stage = 16;
			break;
		case 16:
			sendNPCDialogue(6667, MILDLY_ANGRY, "Or don't, preferably.");
			stage = 50;
			break;
		case 17:
			sendNPCDialogue(6667, BLANK, "No.");
			stage = 18;
			break;
		case 18:
			sendPlayerDialogue(NORMAL,
					"Can I ask YOU some questions about running, Bard?");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(npcId, HAPPY, "Aye, " + player.getDisplayName()
					+ ". Course ye can.");
			stage = 20;
			break;
		case 20:
			sendPlayerDialogue(NORMAL, "Why do I need to run anyway?");
			stage = 21;
			break;
		case 21:
			sendNPCDialogue(npcId, HAPPY,
					"Te get where ye goin' twice as fast as walkin', fer a",
					"start! And te escape from a fight faster if ye happen ye",
					"be losin'.");
			stage = 22;
			break;
		case 22:
			sendPlayerDialogue(NORMAL, "Can I keep running forever?");
			stage = 23;
			break;
		case 23:
			sendNPCDialogue(6667, MILDLY_ANGRY,
					"What kind of question be that? Ye can't do anythin'",
					"forever, " + player.getDisplayName()
							+ ". Ye'd be dead afore then.");
			stage = 24;
			break;
		case 24:
			sendNPCDialogue(
					npcId,
					MILDLY_ANGRY,
					"Stanky! No, ye can't. Ye'll run outta breath after runnin'",
					"for a time. Ye'll need to catch it before ye can run again.",
					"If yer walkin' or standin' aroud, yer energy'll come back",
					"slowly. If yer restin', it'll come back quicker.");
			stage = 25;
			break;
		case 25:
			sendDialogue(
					"You may start running by a clicking once on a run button attached to your",
					"minimap. It has an icon of a boot. Clicking the run button a second time will",
					"switch you back to walking. The button will tell you how much run energy",
					"you currently have.");
			stage = 4;
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
