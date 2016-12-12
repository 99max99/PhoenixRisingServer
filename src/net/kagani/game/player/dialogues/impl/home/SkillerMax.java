package net.kagani.game.player.dialogues.impl.home;

import net.kagani.game.item.Item;
import net.kagani.game.player.content.skillertasks.SkillerTasks;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class SkillerMax extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL, "Hello, " + player.getDisplayName()
				+ ".");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -2:
			end();
			break;
		case -1:
			if (player.getSkillTasks().hasTask()) {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"What is my current task?",
						"What do you have in your shop?",
						"What are the task rewards?",
						"I would like to cancel my task.", "Nevermind.");
				stage = 0;
			} else {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
						"Please give me a task.",
						"What do you have in your shop?",
						"What are the task rewards?", "Nevermind.");
				stage = 1;
			}
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, 9827, "Your current task is "
						+ player.getSkillTasks().getCurrentTask()
								.getAssignment() + ".", player.getSkillTasks()
						.getCurrentTask().getDescription(), "You have "
						+ player.getSkillTasks().getTaskAmount()
						+ " more to go!");
				stage = -2;
				break;
			case OPTION_2:
				player.getPackets().sendGameMessage(
						"You have <col=FF0000>" + player.getTaskPoints()
								+ "</col> skill task points.");
				ShopsHandler.openShop(player, 196);
				end();
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, 9827,
						"The amount of points and cash vary",
						"depending on the task tier that you", "chose.");
				stage = 2;
				break;
			case OPTION_4:
				sendNPCDialogue(npcId, 9827,
						"That will cost either one point or 10,000 gp to cancel your task.");
				stage = 4;
				break;
			}
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendOptionsDialogue("Select task type",
						"An Easy task -  50k gp and 1 point.",
						"A Medium task -  75k gp and 3 points.",
						"A Hard task -  125k gp and 7 points.",
						"An Elite task 200k gp and 10 points.", "Nevermind.");
				stage = 6;
				break;
			case OPTION_2:
				player.getPackets().sendGameMessage(
						"You have <col=FF0000>" + player.getTaskPoints()
								+ "</col> skill task points.");
				ShopsHandler.openShop(player, 196);
				end();
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, 9827,
						"The amount of points and cash vary",
						"depending on the task tier that you", "chose.");
				stage = 2;
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, 9827,
					"The harder the task, the more you will",
					"receive upon completion.");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(
					npcId,
					9827,
					"An easy task gives you  50k gp and 1 point. A medium task gives you 75k gp and 3 points. A hard task gives you 125k and 7 points. An elite task gives you 200k gp and 10 points. Also all tasks gives xp in the skill.");
			stage = -2;
			break;
		case 4:
			sendOptionsDialogue("Are you sure you want to cancel your task?",
					"Yes, cancel with 1 point.", "Yes, cancel with 10k gp.",
					"No");
			stage = 5;
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				if (player.getTaskPoints() > 0) {
					sendNPCDialogue(npcId, 9827,
							"You successfully reset your task.");
					player.setTaskPoints(player.getTaskPoints() - 1);
					player.getSkillTasks().setCurrentTask(null);
				} else {
					sendNPCDialogue(npcId, 9827,
							"You do not have enough task points to reset your task.");
				}
				stage = -2;
				break;
			case OPTION_2:
				if (player.getInventory().getCoinsAmount() >= 10000) {
					sendNPCDialogue(npcId, 9827,
							"You successfully reset your task.");
					player.getInventory().removeItemMoneyPouch(
							new Item(995, 10000));
					player.getSkillTasks().setCurrentTask(null);
				} else {
					sendNPCDialogue(npcId, 9827,
							"You do not have enough money to reset your task.");
				}
				stage = -2;
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				player.getSkillTasks().getNewTask(SkillerTasks.EASY);
				sendNPCDialogue(npcId, 9827, player.getSkillTasks()
						.getCurrentTask().getDescription(),
						"To view the progress of your task, use the command ::task.");
				stage = -2;
				break;
			case OPTION_2:
				player.getSkillTasks().getNewTask(SkillerTasks.MEDIUM);
				sendNPCDialogue(npcId, 9827, player.getSkillTasks()
						.getCurrentTask().getDescription(),
						"To view the progress of your task, use the command ::task.");
				stage = -2;
				break;
			case OPTION_3:
				player.getSkillTasks().getNewTask(SkillerTasks.HARD);
				sendNPCDialogue(npcId, 9827, player.getSkillTasks()
						.getCurrentTask().getDescription(),
						"To view the progress of your task, use the command ::task.");
				stage = -2;
				break;
			case OPTION_4:
				player.getSkillTasks().getNewTask(SkillerTasks.ELITE);
				sendNPCDialogue(npcId, 9827, player.getSkillTasks()
						.getCurrentTask().getDescription(),
						"To view the progress of your task, use the command ::task.");
				stage = -2;
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {

	}
}