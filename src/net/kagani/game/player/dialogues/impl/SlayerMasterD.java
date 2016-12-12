package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.Skills;
import net.kagani.game.player.content.Slayer.SlayerMaster;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;
import net.kagani.utils.Utils;

public class SlayerMasterD extends Dialogue {

	private int npcId;
	private SlayerMaster master;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		master = (SlayerMaster) parameters[1];
		sendNPCDialogue(npcId, 9827, "'Ello and what are you after then?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (player.getSlayerManager().getCurrentMaster() != master
					|| player.getSlayerManager().getCurrentTask() == null) {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"Please give me a task.",
						"What do you have in your shop?", "Nothing, Nevermind.");
				stage = 1;
			} else {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"How many monsters do I have left?",
						"What do you have in your shop?", "Give me a tip.",
						"Reset my task.", "Nothing, Nevermind.");
				stage = 0;
			}
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.getSlayerManager().checkKillsLeft();
				end();
			} else if (componentId == OPTION_2) {
				sendNPCDialogue(npcId, 9827,
						"Only the best slayer equipment money could buy. Come check it out.");
				stage = 5;
			} else if (componentId == OPTION_3) {
				stage = 6;
				if (player.getSlayerManager().getCurrentTask() == null) {
					sendNPCDialogue(npcId, 9827,
							"You currently don't have a task.");
					return;
				}
				String[] tipDialouges = player.getSlayerManager()
						.getCurrentTask().getTips();
				if (tipDialouges != null && tipDialouges.length != 0) {
					String chosenDialouge = tipDialouges[Utils
							.random(tipDialouges.length)];
					if (chosenDialouge == null || chosenDialouge.equals(""))
						sendNPCDialogue(npcId, 9827,
								"I don't have any tips for you currently.");
					else
						sendNPCDialogue(npcId, 9827, chosenDialouge);
				} else {
					sendNPCDialogue(npcId, 9827,
							"I don't have any tips for you currently.");
				}
			} else if (componentId == OPTION_4) {
				sendNPCDialogue(npcId, 9827, "Your Slayer task has been reset.");
				player.getSlayerManager().skipCurrentTask(false);
				stage = 6;
			} else {
				end();
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				if (player.getSlayerManager().getCurrentMaster() != master
						&& master != SlayerMaster.TURAEL
						&& player.getSlayerManager().getCurrentTask() != null) {
					sendNPCDialogue(npcId, NORMAL, "You're still hunting "
							+ player.getSlayerManager().getCurrentTask()
									.getName()
							+ "; come back when you've finished your task.");
				} else if (player.getSkills().getCombatLevelWithSummoning() < master
						.getRequiredCombatLevel())
					sendNPCDialogue(npcId, 9827,
							"Your too weak overall, come back when you've become stronger.");
				else if (player.getSkills().getLevel(Skills.SLAYER) < master
						.getRequiredSlayerLevel()) {
					sendNPCDialogue(
							npcId,
							9827,
							"Your Slayer level is too low to take on my challenges, come back when you have a level of at least "
									+ master.getRequiredSlayerLevel()
									+ " slayer.");
				} else {
					if (master == SlayerMaster.TURAEL
							&& player.getSlayerManager().getCurrentTask() != null)
						player.getSlayerManager().skipCurrentTask(true);
					player.getSlayerManager().setCurrentTask(true, master);
					sendNPCDialogue(npcId, 9827, "Your new assignment is: "
							+ player.getSlayerManager().getCurrentTask()
									.getName() + "; only "
							+ player.getSlayerManager().getCount()
							+ " more to go.");
					stage = 6;
				}
			} else if (componentId == OPTION_2) {
				sendNPCDialogue(npcId, 9827,
						"Only the best slayer equipment money could buy. Come check it out.");
				stage = 5;
			} else
				end();
		} else if (stage == 5) {
			ShopsHandler.openShop(player, 29);
			end();
		} else if (stage == 8) {
			if (player.getSlayerManager().getCurrentTask() != null) {
				sendNPCDialogue(npcId, NORMAL, "You're still hunting "
						+ player.getSlayerManager().getCurrentTask().getName()
						+ "; come back when you've finished your task.");
			}
		} else if (stage == 6)
			end();
	}

	@Override
	public void finish() {

	}
}