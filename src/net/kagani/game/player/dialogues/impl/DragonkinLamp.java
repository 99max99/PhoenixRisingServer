package net.kagani.game.player.dialogues.impl;

import java.io.IOException;

import net.kagani.game.player.Skills;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class DragonkinLamp extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private long last = 0;

	@Override
	public void start() {
		stage = 1;
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Prayer XP.", "Herblore XP.", "Construction XP.", "Nevermind.");
	}

	@Override
	public void run(int interfaceId, int componentId) throws ClassNotFoundException, IOException {
		switch (stage) {
		case -1:
			end();
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				stage = -1;
				addXp(5);
				break;
			case OPTION_2:
				stage = -1;
				addXp(15);
				break;
			case OPTION_3:
				stage = -1;
				addXp(22);
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}
	}

	public void addXp(int skillId) {
		if (System.currentTimeMillis() - last > 500 && player.getInventory().containsItem(18782, 1)) {
			last = System.currentTimeMillis();
			int xp = (int) Math.floor(player.getSkills().getLevel((skillId)) * 3
					- 2 * player.getSkills().getLevel((skillId)) * 2 + 2500 * player.getSkills().getLevel((skillId)));
			player.getInventory().deleteItem(18782, 1);
			player.getSkills().addXp(skillId, xp, true);
			player.getDialogueManager().startDialogue("SimpleMessage", "<col=0000ff>Your wish has been granted!</col>",
					"You have been awarded " + Utils.getFormattedNumber(xp, ',') + " XP in "
							+ Skills.SKILL_NAME[skillId] + "!");
		} else
			player.getPackets().sendGameMessage("You don't have a Dragonkin Lamp in your inventory.");
	}

	@Override
	public void finish() {
		
	}
}