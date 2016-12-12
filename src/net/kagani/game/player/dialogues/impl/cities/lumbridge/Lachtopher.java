package net.kagani.game.player.dialogues.impl.cities.lumbridge;

import net.kagani.game.player.dialogues.Dialogue;

/**
 * The Dialogue for Lachtopher in Lumbridge city.
 * 
 * @author Ethan Kyle Millard
 * @version 1.0 2/26/2015
 */
public class Lachtopher extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(HAPPY_FACE, "Hello there.");
		stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			sendNPCDialogue(npcId, THINKING_THEN_TALKING_FACE,
					"Hello, I suppose. I'm Lachtopher. Could you lend me some",
					"money?");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(ASKING_FACE,
					"Lend you money? I really don't think so. Don't you have",
					"any of your own?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, PLAIN_TALKING_FACE,
					"I spent it all and can't be bothered to earn any more.");
			stage = 4;
			break;
		case 4:
			sendPlayerDialogue(ASKING_FACE,
					"Right, and you want my hard-earned money instead? No",
					"chance!");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING_FACE,
					"You're just like my sister, Victoria. She won't give me any",
					"money.");
			stage = 6;
			break;
		case 6:
			sendPlayerDialogue(PLAIN_TALKING_FACE,
					"Your sister sounds like she has the right idea.");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(
					npcId,
					THINKING_THEN_TALKING_FACE,
					"Yeah, I've heard it all before. 'Oh', she says, 'It's easy to",
					"mak money: just complete Tasks for cash.'");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(HAPPY_FACE, "Well if you want to make money...");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(
					npcId,
					THINKING_THEN_TALKING_FACE,
					"That's just it. I don't want to make money. I just want to",
					"have money.");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(
					ANGRY_FACE,
					"I've had it with you! I don't think I've come across a less",
					"worthwhile person.");
			stage = 11;
			break;
		case 11:
			sendPlayerDialogue(ANGRY_FACE,
					"I think I'll call you Lazy Lachtopher, from now on.");
			stage = 12;
			break;
		case 12:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}
