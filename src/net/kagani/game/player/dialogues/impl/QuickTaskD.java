package net.kagani.game.player.dialogues.impl;

import net.kagani.game.player.Skills;
import net.kagani.game.player.content.Slayer.SlayerMaster;
import net.kagani.game.player.dialogues.Dialogue;

public class QuickTaskD extends Dialogue {

	@Override
	public void start() {
		SlayerMaster master = (SlayerMaster) parameters[0];
		int npcId = master.getNPCId();
		if (player.getSlayerManager().getCurrentTask() != null) {
			if (master == SlayerMaster.TURAEL
					&& player.getSlayerManager().getCurrentMaster() != SlayerMaster.TURAEL) {
				// skip
			} else {
				sendNPCDialogue(npcId, NORMAL, "You're still hunting "
						+ player.getSlayerManager().getCurrentTask().getName()
						+ "; come back when you've finished your task.");
				return;
			}
		}
		if (player.getSkills().getCombatLevelWithSummoning() < master
				.getRequiredCombatLevel())
			sendNPCDialogue(npcId, 9827,
					"Your too weak overall, come back when you've become stronger.");
		else if (player.getSkills().getLevel(Skills.SLAYER) < master
				.getRequiredSlayerLevel()) {
			sendNPCDialogue(
					npcId,
					9827,
					"Your Slayer level is too low to take on my challenges, come back when you have a level of at least "
							+ master.getRequiredSlayerLevel() + " slayer.");
		} else {
			if (master == SlayerMaster.TURAEL
					&& player.getSlayerManager().getCurrentTask() != null)
				player.getSlayerManager().skipCurrentTask(true);
			player.getSlayerManager().setCurrentTask(true, master);
			sendNPCDialogue(npcId, 9827, "Your new assignment is: "
					+ player.getSlayerManager().getCurrentTask().getName()
					+ "; only " + player.getSlayerManager().getCount()
					+ " more to go.");
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {

	}
}