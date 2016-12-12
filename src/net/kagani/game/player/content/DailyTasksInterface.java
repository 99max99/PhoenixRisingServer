package net.kagani.game.player.content;

import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Utils;

public class DailyTasksInterface {

	/**
	 * @author: Dylan Page
	 */

	private static int selectedSkill = -1;

	public static void openTaskDialogue(Player player) {
		player.getInterfaceManager().sendCentralInterface(631);
		for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(631); i++)
			player.getPackets().sendUnlockIComponentOptionSlots(631, i, -1, 0,
					0);
		/*
		 * player.getPackets().sendHideIComponent(631, 7, true);
		 * player.getPackets().sendHideIComponent(631, 27, true);
		 * player.getPackets().sendHideIComponent(631, 26, true);
		 * player.getPackets().sendHideIComponent(631, 25, true);
		 * player.getPackets().sendHideIComponent(631, 8, true);
		 * player.getPackets().sendHideIComponent(631, 21, true);
		 * player.getPackets().sendHideIComponent(631, 13, true);
		 * player.getPackets().sendHideIComponent(631, 23, true);
		 */
	}

	public static void handleButton(Player player, int interfaceId,
			int componentId) {
		switch (interfaceId) {
		case 631:
			switch (componentId) {
			case 42:
				finish(player);
				break;
			case 34:
				if (selectedSkill == -1)
					finish(player);
				confirm(player, true);
				break;
			default:
				if (componentId == 7)
					selectedSkill = 0;
				else if (componentId == 9)
					selectedSkill = 16;
				else if (componentId == 16)
					selectedSkill = 10;
				else if (componentId == 23)
					selectedSkill = 22;
				else if (componentId == 10)
					selectedSkill = 15;
				else if (componentId == 17)
					selectedSkill = 7;
				else if (componentId == 24)
					selectedSkill = 21;
				else if (componentId == 11)
					selectedSkill = 17;
				else if (componentId == 18)
					selectedSkill = 11;
				else if (componentId == 25)
					selectedSkill = 23;
				else if (componentId == 12)
					selectedSkill = 12;
				else if (componentId == 19)
					selectedSkill = 8;
				else if (componentId == 26)
					selectedSkill = 24;
				else if (componentId == 13)
					selectedSkill = 20;
				else if (componentId == 20)
					selectedSkill = 9;
				else if (componentId == 22)
					selectedSkill = 19;
				else if (componentId == 14)
					selectedSkill = 14;
				else if (componentId == 21)
					selectedSkill = 18;
				else if (componentId == 8)
					selectedSkill = 5;
				else if (componentId == 15)
					selectedSkill = 13;
				else if (componentId == 27)
					selectedSkill = 25;
				openTaskDialogue(player);
				confirm(player, false);
				break;
			}
			break;
		}
	}

	private static void confirm(Player player, boolean message) {
		if (selectedSkill != -1) {
			if (selectedSkill == Skills.CONSTRUCTION
					|| selectedSkill == Skills.RUNECRAFTING
					|| selectedSkill == Skills.SLAYER
					|| selectedSkill == Skills.SUMMONING
					|| selectedSkill == Skills.ATTACK
					|| selectedSkill == Skills.DEFENCE
					|| selectedSkill == Skills.STRENGTH
					|| selectedSkill == Skills.RANGE
					|| selectedSkill == Skills.MAGIC
					|| selectedSkill == Skills.PRAYER
					|| selectedSkill == Skills.HITPOINTS
					|| selectedSkill == Skills.DUNGEONEERING
					|| selectedSkill == Skills.DIVINATION) {
				player.getPackets().sendGameMessage(
						"We currently do not offer skill tasks in "
								+ player.getSkills()
										.getSkillName(selectedSkill) + ".");
			}
			if (selectedSkill == Skills.DIVINATION) // so we dont print out
													// errors
				return;
			if (player.toggledAmount >= 10 && player.settings[selectedSkill]) {
				player.getPackets().sendGameMessage(
						"You may only filter a maximum of 10 skills.");
				player.getPackets().sendGameMessage(
						"The skills that you currently have blocked are: "
								+ getBlockedSkills(player), true);
				openTaskDialogue(player);
				return;
			}
			player.settings[selectedSkill] = !player.settings[selectedSkill];
			if (getBlockedSkills(player) != "" && message)
				player.getPackets().sendGameMessage(
						"You are blocking challenges for the following skills: "
								+ getBlockedSkills(player));
			if (!player.settings[selectedSkill])
				player.toggledAmount++;
			else
				player.toggledAmount--;
			if (message)
				finish(player);
		}
	}

	private static void finish(Player player) {
		if (player.getInterfaceManager().containsScreenInterface())
			player.getInterfaceManager().removeCentralInterface();
	}

	private static String getBlockedSkills(Player player) {
		String xyz = "";
		int x = 0;
		for (int i = 0; i < 25; i++) {
			if (player.settings[i])
				continue;
			x++;
			if (x < 9)
				Utils.fixChatMessage(xyz += Skills.SKILL_NAME[i] + ", ");
			else if (x == 9)
				Utils.fixChatMessage(xyz += Skills.SKILL_NAME[i] + ", ");
			else if (x == 10)
				Utils.fixChatMessage(xyz += Skills.SKILL_NAME[i] + ".");
		}
		return Utils.fixChatMessage(xyz.toLowerCase());
	}
}