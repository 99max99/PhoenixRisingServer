package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class VarrockTanner extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, HAPPY,
				"Greetings friend. I am a manufacturer of leather.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Can I buy some leather then?",
					"Leather is rather weak stuff.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "Can I buy some leather then?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Leather is rather weak stuff.");
				stage = 2;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, NORMAL,
					"I make leather from animal hides. Bring me some cowhides",
					"and one gold coin per hide, and I'll tan them into soft",
					"leather for you.");
			stage = 50;
			break;
		case 2:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Normal leather may be quite weak, but it's very cheap - I",
					"make it from cowhides for only 1 gp per hide - and it's so",
					"easy to craft that anyone can work with it.");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"Alternatively you could try hard leather. It's not so easy",
					"to craft, but I only charge 3 gp per cowhide to prepare it,",
					"and it makes much sturdier armour.");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, HAPPY,
					"I can also tan snake hides and dragonhides, suitable for",
					"crafting into the highest quality armour for rangers.");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(PLAIN_TALKING, "Thanks, I'll bear it in mind.");
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
