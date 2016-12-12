package net.kagani.game.player.dialogues.impl.cities.varrock;

import net.kagani.game.player.dialogues.Dialogue;

public class Apothecary extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL, "I am the Apothecary. I brew potions.",
				"Do you need anything specific?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Can you make a strength potion?",
					"Do you know a potion to make hair fall out?",
					"Have you got any good potions to give away?",
					"Can you make a potion that makes it seem like I'm dead?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(PLAIN_TALKING,
						"Can you make a strength potion?");
				stage = 1;
				break;
			case OPTION_2:
				sendPlayerDialogue(HAPPY,
						"Do you know a potion to make hair fall out?");
				stage = 6;
				break;
			case OPTION_3:
				sendPlayerDialogue(HAPPY,
						"Have you got any good potions to give away?");
				stage = 7;
				break;
			case OPTION_4:
				sendPlayerDialogue(CONFUSED,
						"Can you make a potion that makes it seem like I'm dead?");
				stage = 8;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Yes, but the ingredients are a little hard to find. If you",
					"ever get them I will make it for you, for a fee.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(CONFUSED, "So what are the ingredients?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					NORMAL,
					"You'll need to find the eggs of the deadly red spider and a",
					"limpwurt root.");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, HAPPY,
					"Oh and you'll have to pay me 5 coins.");
			stage = 5;
			break;
		case 5:
			sendPlayerDialogue(PLAIN_TALKING, "Ok, I'll look out for them.");
			stage = 50;
			break;
		case 6:
			sendNPCDialogue(
					npcId,
					HAPPY,
					"I do indeed. I gave it to my mother. That's why I now live",
					"alone.");
			stage = 50;
			break;
		case 7:
			sendNPCDialogue(npcId, UPSET,
					"Sorry, charity is not my strong point.");
			stage = 50;
			break;
		case 8:
			sendNPCDialogue(
					npcId,
					UPSET,
					"What a strange and morbid request! I can as it happens.",
					"The berry of the cadava bush, prepared properly, will",
					"induce a coma so deep that you will seem to be dead. It's",
					"very dangerous.");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(
					npcId,
					UPSET,
					"I have the othre ingredients, but I'll need you to bring me",
					"one bunch of cadava berries.");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(PLAIN_TALKING, "I'll bear that in mind.");
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
