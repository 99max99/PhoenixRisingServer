package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.content.Drinkables;
import net.kagani.game.player.dialogues.Dialogue;

public class BobBarterD extends Dialogue {

	private static final int BOB = 6524;

	@Override
	public void start() {
		sendPlayerDialogue(NORMAL, "Hi.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendNPCDialogue(
					BOB,
					HAPPY,
					"Hello, chum, fancy buyin' some designer jewllery? They've come all the way from Ardougne! Most pukka!");
			stage = 0;
		} else if (stage == 0) {
			sendPlayerDialogue(CONFUSED, "Erm, no. I'm all set, thanks.");
			stage = 1;
		} else if (stage == 1) {
			sendNPCDialogue(
					BOB,
					HAPPY,
					"Okay, chum, so what can I do for you? I can tell you the very latest herb prices, or perhaps I could help you decant your potions.");
			stage = 2;
		} else if (stage == 2) {
			sendOptionsDialogue("Select an Option", "Who are you?",
					"Can you decant things for me?", "Sorry I've got to split.");
			stage = 3;
		} else if (stage == 3) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "Who are you?");
				stage = 4;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL, "Can you decant things for me?");
				stage = 11;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(NORMAL, "Sorry I've got to split.");
				stage = 20;
			}
		} else if (stage == 4) {
			sendNPCDialogue(BOB, NORMAL,
					"Why, I'm Bob! Your friendly seller of smashin goods!");
			stage = 5;
		} else if (stage == 5) {
			sendPlayerDialogue(NORMAL, "So what do you have to sell?");
			stage = 6;
		} else if (stage == 6) {
			sendNPCDialogue(BOB, NORMAL,
					"Oh, not much at the moment. Cuz, ya know. Business being so well and cushie.");
			stage = 7;
		} else if (stage == 7) {
			sendPlayerDialogue(NORMAL,
					"You don't really look like you're being so successful.");
			stage = 8;
		} else if (stage == 8) {
			sendNPCDialogue(
					BOB,
					MAD,
					"You plonka! It's all a show, innit! If I let people knows I'm in good business they'll want a share of the moolah!");
			stage = 9;
		} else if (stage == 9) {
			sendPlayerDialogue(MOCK,
					"You conviently have a responce for everything.");
			stage = 10;
		} else if (stage == 10) {
			sendNPCDialogue(BOB, HAPPY, "That's the Ardougne way, my son.");
			stage = 20;
		} else if (stage == 11) {
			sendNPCDialogue(BOB, HAPPY, "Why of course my son.");
			stage = 12;
		} else if (stage == 12) {
			Drinkables.decantPotsInv(player);
			sendNPCDialogue(BOB, LAUGHING, "Tis the work of a herblore master.");
			stage = 20;
		} else if (stage == 20) {
			end();
		}
	}

	@Override
	public void finish() {

	}
}
