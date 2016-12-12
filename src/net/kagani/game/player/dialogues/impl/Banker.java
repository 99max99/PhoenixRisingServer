package net.kagani.game.player.dialogues.impl;

import net.kagani.Settings;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class Banker extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, 9827, "Good day, How may I help you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue("What would you like to say?",
					"I'd like to acess my bank account, please.",
					"I'd like to check my PIN settings.",
					"I'd like to see my collection box.",
					"What is my net worth?", "What is this place?");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.getBank().openBank();
				end();
			} else if (componentId == OPTION_2) {
				player.getBank().openPinSettings(false);
				end();
				// stage = 10;
			} else if (componentId == OPTION_3) {
				player.getGeManager().openCollectionBox();
				end();
			} else if (componentId == OPTION_4) {
				stage = 15;
				sendPlayerDialogue(9827, "What is my net worth?");
			} else if (componentId == OPTION_5) {
				stage = 1;
				sendPlayerDialogue(9827, "What is this place?");
			} else
				end();
		} else if (stage == 15) {
			player.getWealth();
			end();
		} else if (stage == 1) {
			stage = 2;
			sendNPCDialogue(npcId, 9827, "This is a branch of the Bank of "
					+ Settings.SERVER_NAME + ". We have",
					"branches in many towns.");
		} else if (stage == 2) {
			stage = 3;
			sendOptionsDialogue("What would you like to say?",
					"And what do you do?",
					"Didnt you used to be called the Bank of Varrock?");
		} else if (stage == 3) {
			if (componentId == OPTION_1) {
				stage = 4;
				sendPlayerDialogue(9827, "And what do you do?");
			} else if (componentId == OPTION_2) {
				stage = 5;
				sendPlayerDialogue(9827,
						"Didnt you used to be called the Bank of Varrock?");
			} else
				end();
		} else if (stage == 4) {
			stage = -2;
			sendNPCDialogue(npcId, 9827,
					"We will look after your items and money for you.",
					"Leave your valuables with us if you want to keep them",
					"safe.");
		} else if (stage == 5) {
			stage = -2;
			sendNPCDialogue(npcId, 9827,
					"Yes we did, but people kept on coming into our",
					"signs were wrong. They acted as if we didn't know",
					"what town we were in or something.");
		} else if (stage == 10) {
			if (player.getBank().getPin() == -1) {
				player.closeInterfaces();
				player.getBank().openPin(0);
			} else {
				if (componentId == 18 || componentId == 19) {
					player.getDialogueManager().startDialogue(
							"RemovePIND",
							!(player.getBank().getRecoveryTime() >= Utils
									.currentTimeMillis()));
				} else {
					System.out.println("This action is currently disabled.");
					player.closeInterfaces();
				}
			}
		} else
			end();
	}

	@Override
	public void finish() {

	}
}