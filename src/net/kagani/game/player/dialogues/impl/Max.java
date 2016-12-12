package net.kagani.game.player.dialogues.impl;

import net.kagani.game.item.Item;
import net.kagani.game.player.Skills;
import net.kagani.game.player.dialogues.Dialogue;

public class Max extends Dialogue {

	private int npcId = 3705;

	@Override
	public void start() {
		sendNPCDialogue(npcId, NORMAL, "How can I help you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("What would you like to say?", "Who are you?",
					"That's a nice cape you have there.", "Nothing, nevermind.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "Who are you?");
				stage = 1;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL, "That's a nice cape you have there.");
				stage = 5;
			} else {
				sendPlayerDialogue(NORMAL, "It's nothing, nevermind.");
				stage = 20;
			}
		} else if (stage == 1) {
			sendNPCDialogue(npcId, NORMAL,
					"Oh sorry - I'm Max. Some say I'm a bit obsessed with skilling.");
			stage = 2;
		} else if (stage == 2) {
			sendPlayerDialogue(NORMAL, "And I'm " + player.getDisplayName()
					+ ", nice to meet you.");
			stage = 3;
		} else if (stage == 3) {
			sendNPCDialogue(npcId, NORMAL,
					"Indeed. So can I help you with anything?");
			stage = 4;
		} else if (stage == 4) {
			sendOptionsDialogue("What would you like to say?",
					"That's a nice cape you have there.", "Nothing, nevermind.");
			stage = 13;
		} else if (stage == 13) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL,
						"That's a nice cape you have there...");
				stage = 5;
			} else {
				sendPlayerDialogue(NORMAL, "Nothing, nevermind.");
				stage = 20;
			}
		} else if (stage == 5) {
			sendNPCDialogue(npcId, NORMAL,
					"This? Thanks! It's a symbol that I've trained all my skill to level 99.");
			stage = 6;
		} else if (stage == 6) {
			boolean canPurchase = true;
			for (int skill = 0; skill < Skills.SKILL_NAME.length; skill++) {
				if (skill == Skills.CONSTRUCTION
						|| skill == Skills.DUNGEONEERING)
					continue;
				if (player.getSkills().getLevelForXp(skill) < 99)
					canPurchase = false;
			}
			if (canPurchase) {
				sendPlayerDialogue(NORMAL, "So have I!");
				stage = 7;
			} else {
				sendPlayerDialogue(NORMAL, "Wow, that's quite impressive.");
				stage = 8;
			}
		} else if (stage == 7) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Indeed so. Would you like a cape like mine to show that fact off? 2,475,000 coins - 99,000 for each skill.");
			stage = 10;
		} else if (stage == 8) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Thanks. I have faith in you "
							+ player.getDisplayName()
							+ " - one day you'll be here and I'll sell you one for yourself!");
			stage = 9;
		} else if (stage == 9) {
			sendPlayerDialogue(NORMAL, "We'll see about that, thanks.");
			stage = 20;
		} else if (stage == 10) {
			sendOptionsDialogue("Pay 2,475,000 coins for a max cape?",
					"I'll take one!", "No thanks.");
			stage = 11;
		} else if (stage == 11) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "I'll take one.");
				stage = 12;
			} else {
				sendPlayerDialogue(NORMAL, "No thanks.");
				stage = 20;
			}
		} else if (stage == 12) {
			if (player.getInventory().getCoinsAmount() >= 2475000) {
				player.getInventory().removeItemMoneyPouch(
						new Item(995, 2475000));
				player.getInventory().addItemDrop(20767, 1);
				player.getInventory().addItemDrop(20768, 1);
				sendNPCDialogue(npcId, NORMAL, "Thanks! Enjoy.");
			} else
				sendNPCDialogue(npcId, NORMAL,
						"You don't have enough to cover the costs, come back when you do.");
			stage = 20;
		} else if (stage == 20) {
			end();
		}
	}

	@Override
	public void finish() {

	}
}