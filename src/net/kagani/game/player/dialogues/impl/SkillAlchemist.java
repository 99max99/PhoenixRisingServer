package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.dialogues.Dialogue;

public class SkillAlchemist extends Dialogue {

	private int npcId = 5585, selectedSkill;

	@Override
	public void start() {
		sendNPCDialogue(npcId, NORMAL,
				"Hello there traveler, what are thoust searching for?");
		selectedSkill = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			sendOptionsDialogue("Select an Option", "Who are you?",
					"What are you doing in Gelinor?",
					"Can I purchase some negative experience?",
					"Nothing, nevermind.");
			stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(NORMAL, "Who are you?");
				stage = 1;
			} else if (componentId == OPTION_2) {
				sendPlayerDialogue(NORMAL, "What are you doing in Gelinor?");
				stage = 13;
			} else if (componentId == OPTION_3) {
				sendPlayerDialogue(NORMAL,
						"Can I purchase some negative experience?");
				stage = 11;
			} else if (componentId == OPTION_4) {
				sendNPCDialogue(npcId, NORMAL,
						"Oh... well if you ever want anything else come back again!");
				stage = 30;
			}
		} else if (stage == 1) {
			sendNPCDialogue(npcId, NORMAL, "I am the skill alchemist.");
			stage = 2;
		} else if (stage == 2) {
			sendPlayerDialogue(NORMAL, "Erm... that's pretty vague.");
			stage = 3;
		} else if (stage == 3) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Well. If you would like to know more, my friends call me Shirley... *GAH* I mean Savions Sw.");
			stage = 4;
		} else if (stage == 4) {
			sendPlayerDialogue(
					NORMAL,
					"HA, your name is Shirely.... but besides that, what do you do on a daily basis?");
			stage = 5;
		} else if (stage == 5) {
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Erm.. well I transmute coins into negative experience. What this does is reduce unwanted experience within a certain skill of your choice.");
			stage = 6;
		} else if (stage == 6) {
			sendNPCDialogue(npcId, NORMAL,
					"Are you in need of any negative experience?");
			stage = 7;
		} else if (stage == 7) {
			sendOptionsDialogue(
					"Would you like to transmute some coins into negative experience?",
					"Yes", "No");
			stage = 8;
		} else if (stage == 8) {
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Please choose a skill", "Attack",
						"Strength", "Defence", "Prayer", "Next selection");
				stage = 9;
			} else {
				sendNPCDialogue(npcId, NORMAL,
						"Oh... well if you ever want anything else come back again!");
				stage = 30;
			}
		} else if (stage == 9) {
			if (componentId == OPTION_1) {
				selectedSkill = Skills.ATTACK;
			} else if (componentId == OPTION_2) {
				selectedSkill = Skills.STRENGTH;
			} else if (componentId == OPTION_3) {
				selectedSkill = Skills.DEFENCE;
			} else if (componentId == OPTION_4) {
				selectedSkill = Skills.PRAYER;
			} else if (componentId == OPTION_5) {
				sendOptionsDialogue("Please choose a skill", "Range", "Magic",
						"Summoning", "Previous Selection",
						"Nothing interests you");
				stage = 10;
			}
			if (selectedSkill != -1) {
				player.getPackets().sendInputIntegerScript(
						"Select the requested level you would like.");
				player.getTemporaryAttributtes().put("selected_neg",
						selectedSkill);
			}
		} else if (stage == 10) {
			if (componentId == OPTION_1) {
				selectedSkill = Skills.RANGE;
			} else if (componentId == OPTION_2) {
				selectedSkill = Skills.MAGIC;
			} else if (componentId == OPTION_3) {
				selectedSkill = Skills.SUMMONING;
			} else if (componentId == OPTION_4) {
				sendOptionsDialogue("Please choose a skill", "Attack",
						"Strength", "Defence", "Prayer", "Next selection");
				stage = 9;
			} else if (componentId == OPTION_4) {
				sendNPCDialogue(npcId, NORMAL,
						"Oh... well if you ever want anything else come back again!");
				stage = 30;
			}
			if (selectedSkill != -1) {
				player.getPackets().sendInputIntegerScript(
						"Select the requested level you would like.");
				player.getTemporaryAttributtes().put("selected_neg",
						selectedSkill);
			}
		} else if (stage == 11) {
			sendNPCDialogue(npcId, NORMAL,
					"Why of course! You should be warned that you will be charged a fee.");
			stage = 12;
		} else if (stage == 12) {
			sendOptionsDialogue("Please choose a skill", "Attack", "Strength",
					"Defence", "Prayer", "Next selection");
			stage = 9;
		} else if (stage == 13) {
			sendNPCDialogue(npcId, NORMAL, "Oh, the government sent me here.");
			stage = 14;
		} else if (stage == 14) {
			sendPlayerDialogue(NORMAL,
					"The government? Uhh are you alright there.");
			stage = 15;
		} else if (stage == 15) {
			sendNPCDialogue(npcId, 9835, "HUEFDKJDSF IEFDS JKDKFSHURURRRR");
			stage = 16;
		} else if (stage == 16) {
			sendPlayerDialogue(NORMAL, "Okay..... I'll be going now.");
			stage = 30;
		} else if (stage == 30) {
			end();
		}
	}

	public static int calculatePrice(Player player, int skillProduct) {
		int price = skillProduct * 100000;
		if (player.isAMemberGreaterThanGold())
			price = 0;
		else if (player.isSilverMember())
			price *= .5;
		return price;
	}

	@Override
	public void finish() {

	}
}
