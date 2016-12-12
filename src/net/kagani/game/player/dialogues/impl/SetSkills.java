package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.Skills;
import net.kagani.game.player.dialogues.Dialogue;

/**
 * Setting a skill level.
 * 
 * @author Raghav
 * 
 */
public class SetSkills extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (player.getEquipment().wearingArmour()) {
			sendDialogue("Please remove your armour first.");
			stage = -2;
		} else
			sendOptionsDialogue("Choose a skill", "" + Skills.SKILL_NAME[0], ""
					+ Skills.SKILL_NAME[1], "" + Skills.SKILL_NAME[2], ""
					+ Skills.SKILL_NAME[3], "More options.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getPrayer().closeAllPrayers();
		end();
		if (stage == -2)
			end();
		else if (stage == -1) {
			if (componentId == OPTION_1) {
				player.getTemporaryAttributtes().put("skillId", Skills.ATTACK);
				player.getPackets()
						.sendInputIntegerScript("Enter skill level:");
			} else if (componentId == OPTION_2) {
				player.getTemporaryAttributtes().put("skillId", Skills.DEFENCE);
				player.getPackets()
						.sendInputIntegerScript("Enter skill level:");
			} else if (componentId == OPTION_3) {
				player.getTemporaryAttributtes()
						.put("skillId", Skills.STRENGTH);
				player.getPackets()
						.sendInputIntegerScript("Enter skill level:");
			} else if (componentId == OPTION_4) {
				player.getTemporaryAttributtes().put("skillId",
						Skills.HITPOINTS);
				player.getPackets()
						.sendInputIntegerScript("Enter skill level:");
			} else {
				stage = 0;
				sendOptionsDialogue("Choose a skill",
						"" + Skills.SKILL_NAME[4], "" + Skills.SKILL_NAME[5],
						"" + Skills.SKILL_NAME[6], "Back.", "Never mind.");
			}
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.getTemporaryAttributtes().put("skillId", Skills.RANGE);
				player.getPackets()
						.sendInputIntegerScript("Enter skill level:");
			} else if (componentId == OPTION_2) {
				player.getTemporaryAttributtes().put("skillId", Skills.PRAYER);
				player.getPackets()
						.sendInputIntegerScript("Enter skill level:");
			} else if (componentId == OPTION_3) {
				player.getTemporaryAttributtes().put("skillId", Skills.MAGIC);
				player.getPackets()
						.sendInputIntegerScript("Enter skill level:");
			} else if (componentId == OPTION_4) {
				stage = -1;
				sendOptionsDialogue("Choose a skill",
						"" + Skills.SKILL_NAME[0], "" + Skills.SKILL_NAME[1],
						"" + Skills.SKILL_NAME[2], "" + Skills.SKILL_NAME[3],
						"More options.");
			} else {
				end();
			}
		}
	}

	@Override
	public void finish() {

	}
}
