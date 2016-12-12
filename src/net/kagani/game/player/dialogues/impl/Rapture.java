package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Rapture extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	@Override
	public void start() {
		npcId = (int) parameters[0];
		stage = 1;
		sendNPCDialogue(npcId, BLANK, getText());
	}

	private String getText() {
		if (player.getCombatLevel() < 10)
			return "You're weak " + player.getDisplayName()
					+ ". Get some meat on yourself. What do you want?";
		else if (player.getCombatLevel() >= 10 && player.getCombatLevel() <= 50)
			return "Ewwww... Get some meat on yourself. What do you want?";
		else if (player.getCombatLevel() >= 51
				&& player.getCombatLevel() <= 100)
			return "You're getting stronger, what do you want?";
		else if (player.getCombatLevel() >= 101
				&& player.getCombatLevel() <= 120)
			return "I like you... What can I do for you?";
		else if (player.getCombatLevel() >= 121)
			return "Tough guy! What do you want?";
		return null;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			stage = 2;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"What do you have in store?",
					"Tell me about the boss system.", "Nothing.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				ShopsHandler.openShop(player, 202);
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Next page.",
						"Exit.");
				break;
			case OPTION_2:
				stage = 1;
				sendNPCDialogue(
						npcId,
						HAPPY,
						"You gain a specific amount of points for killing a boss, which can later on be used to buy in my store.");
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				stage = 4;
				ShopsHandler.openShop(player, 203);
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Previous page.",
						"Exit.");
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				ShopsHandler.openShop(player, 202);
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Next page.",
						"Exit.");
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}