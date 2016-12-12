package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

public class Lactopher extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(9843, "Hello there.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, NORMAL,
					"Hello, I suppose. I'm Lactopher. Could you lend me some",
					"money?");
			stage = 0;
			break;
		case 0:
			sendPlayerDialogue(CONFUSED,
					"Lend you money? I really don't think so. Don't you have",
					"any of your own?");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, NORMAL,
					"I spent it all and I can't be bothered to earn any more.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(CONFUSED,
					"Right and you want my hard-earned money instead? No",
					"chance!");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"You're just like my sister, Victoria. She won't give me any",
					"money.");
			stage = 4;
			break;
		case 4:
			sendPlayerDialogue(HAPPY,
					"Your sister sounds like she has the right idea.");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Yeah, I've heared it all before. 'Oh,' she says, 'It's easy to",
					"make money: just complete Tasks for cash.'");
			stage = 6;
			break;
		case 6:
			sendPlayerDialogue(HAPPY, "Well, if you want to make money...");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"That's just it. I don't want to make money. I just want to",
					"have money.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(
					ANGRY,
					"I've had it with you! I don't think I've come across a less",
					"worth while person.");
			stage = 9;
			break;
		case 9:
			sendPlayerDialogue(ANGRY,
					"I think I'll call you Lazy Lactopher, from now on.");
			stage = 10;
			break;
		case 10:
			end();
			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
