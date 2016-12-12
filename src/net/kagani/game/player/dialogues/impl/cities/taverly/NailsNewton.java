package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.game.player.dialogues.Dialogue;

public class NailsNewton extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendPlayerDialogue(HAPPY, "Hello!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Hey, you looking for work?");
			stage = 0;
			break;
		case 0:
			sendPlayerDialogue(NORMAL, "What sort of work?");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, UNSURE,
					"Just a little job I need doing. See, there's a merchant",
					"upstairs I need something lifting from.");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, UNSURE,
					"I'd do it myself, but I'm more into a 'legitimate'",
					"document business these days.");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(WORRIED,
					"You want me to rob him? That doesn't sound very heroic!");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, PLAIN_TALKING, "Keep your voice down!");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, PLAIN_TALKING,
					"Look, he's not a very nice bloke, right?");
			stage = 6;
			break;
		case 6:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"He's Rolo the Stout, and he's currently buying up all the",
					"food in the area on the cheap and selling it to the",
					"regugees at a huge mark-up.");
			stage = 7;
			break;
		case 7:
			sendPlayerDialogue(CONFUSED,
					"Well, that isn't very nice...but two wrongs don't make a",
					"right.");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"Just listen will ya? All I need is his seal. I've a forged letter",
					"right here that says he's releasing all the food to the",
					"refugee for free.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(
					npcId,
					PLAIN_TALKING,
					"If I can get his personal seal, hen I can get his associates",
					"to open the stockpiles to the people of the town.");
			stage = 10;
			break;
		case 10:
			sendNPCDialogue(npcId, PLAIN_TALKING, "You in, or not?");
			stage = 11;
			break;
		case 11:
			sendOptionsDialogue(DEFAULT, "Sure. I'll help.", "No, thanks.");
			stage = 12;
			break;
		case 12:
			switch (componentId) {
			case OPTION_1:
				/*
				 * Start of Quest - Let Them Eat Pie
				 */
				end();
				break;
			case OPTION_2:
				sendPlayerDialogue(BLANK, "No, thanks.");
				stage = 50;
				break;
			}
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
