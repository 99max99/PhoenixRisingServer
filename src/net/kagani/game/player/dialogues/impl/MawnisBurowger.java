package net.kagani.game.player.dialogues.impl;

import net.kagani.game.item.Item;
import net.kagani.game.player.dialogues.Dialogue;

public class MawnisBurowger extends Dialogue {

	private static final int NPCID = 5530;

	@Override
	public void start() {
		sendNPCDialogue(
				NPCID,
				NORMAL,
				"It makes me proud to know that the helm of my ancestors will be worn in battle.");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendNPCDialogue(
					NPCID,
					NORMAL,
					"Thank you on behalf of all my kinsmen "
							+ player.getDisplayName() + ".");
			stage = 0;
		} else if (stage == 0) {
			sendPlayerDialogue(NORMAL, "Well, it is a beatiful helmet.");
			stage = (byte) (player.containsItem(10828) ? 7 : 1);
		} else if (stage == 1) {
			sendNPCDialogue(
					NPCID,
					NORMAL,
					"Do you have the priceless heirloom that I gave to you as a sign of my trust and gratitude?");
			stage = 2;
		} else if (stage == 2) {
			sendPlayerDialogue(NORMAL, "Maybe. I may have misplaced it.");
			stage = 3;
		} else if (stage == 3) {
			sendNPCDialogue(
					NPCID,
					NORMAL,
					"Good job I have alert and loyal men who notice when something like this is left lying around the around and picks it up.");
			stage = 4;
		} else if (stage == 4) {
			sendNPCDialogue(NPCID, NORMAL,
					"But I'm afraid I'm going to have to charge you 50,000 coins.");
			stage = 5;
		} else if (stage == 5) {
			sendOptionsDialogue("Would you like to pay 50,000 coins?", "Yes",
					"No");
			stage = 6;
		} else if (stage == 6) {
			if (componentId == OPTION_1) {
				if (player.getInventory().getCoinsAmount() >= 50000) {
					sendNPCDialogue(NPCID, NORMAL,
							"Please be more careful with it in the future.");
					player.getInventory().addItem(new Item(10828, 1));
					player.getInventory().removeItemMoneyPouch(
							new Item(995, 50000));
				} else
					sendNPCDialogue(NPCID, NORMAL,
							"You don't have enough to cover the cost, come again when you do.");
				stage = 7;
			} else
				end();
		} else if (stage == 7) {
			end();
		}
	}

	@Override
	public void finish() {

	}
}
