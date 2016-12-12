package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class Doomsayer extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, PLAIN_TALKING, "Dooooom!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(WORRIED, "Where?");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"All around us! I can feel it in the air, hear it on the",
					"wind, smell it...also in the air!");
			stage = 1;
			break;
		case 1:
			sendPlayerDialogue(PLAIN_TALKING,
					"Is there anything we can do about this doom?");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"There is nothing you need to do my friend! I am the",
					"Doomsayer, although my real title could be something",
					"like the Danger Tutor?");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(NORMAL, "Danger Tutor?");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Yes! I roam the world sensing danger.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"If I find a dangerous area, then I put up warning",
					"signs that will tell you what is so dangerous about that",
					"area.");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"If you see the signs often enough, then you can turn",
					"them off; by that time you likely know what the area",
					"has in store for you.");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(NORMAL,
					"But what if I want to see the warnings again?");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"That's why I'm waiting here!");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"If you want to see the warning messages again, I can",
					"turn them back on for you.");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(NORMAL,
					"Thanks, I'll remember that if I see any warning",
					"messages.");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Your welcome!");
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
