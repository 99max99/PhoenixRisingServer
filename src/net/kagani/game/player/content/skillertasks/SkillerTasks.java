package net.kagani.game.player.content.skillertasks;

import java.io.Serializable;
import java.util.ArrayList;

import net.kagani.Settings;
import net.kagani.game.Graphics;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class SkillerTasks implements Serializable {

	/**
	 * @author: King Fox
	 * @author: Miles
	 * @author: Dylan Page
	 */

	private static final long serialVersionUID = 3535856484888897359L;

	public final static int EASY = 0;
	public final static int MEDIUM = 1;
	public final static int HARD = 2;
	public final static int ELITE = 3;

	public SkillTasks task;
	public int taskAmount;
	private transient Player player;

	/**
	 * Decreases the amount remaining for the task, if it hits 0 player will
	 * receive a message only one time.
	 */
	public void decreaseTask(SkillTasks stask) {
		if (!hasTask()
				|| taskAmount == 0
				|| !task.getAssignment()
						.equalsIgnoreCase(stask.getAssignment())) {
			return;
		}
		int cash = 0, points = 0, xp = 0;
		if (Settings.DEBUG)
			taskAmount -= 50;
		else
			taskAmount--;
		if (taskAmount <= 0) {
			if (player.getSkillTasks().getCurrentTask().getDifficulty() == SkillerTasks.EASY) {
				xp = 500;
				player.setTaskPoints(player.getTaskPoints() + 1);
				cash = 50000;
				points = 1;
				player.getSkills().addXp(
						player.getSkillTasks().getCurrentTask().getSkill(), xp);
				player.getMoneyPouch().setAmount(cash, false);
			} else if (player.getSkillTasks().getCurrentTask().getDifficulty() == SkillerTasks.MEDIUM) {
				xp = 1500;
				player.setTaskPoints(player.getTaskPoints() + 3);
				cash = 75000;
				points = 3;
				player.getSkills().addXp(
						player.getSkillTasks().getCurrentTask().getSkill(), xp);
				player.getMoneyPouch().setAmount(cash, false);
			} else if (player.getSkillTasks().getCurrentTask().getDifficulty() == SkillerTasks.HARD) {
				xp = 2500;
				player.setTaskPoints(player.getTaskPoints() + 7);
				cash = 125000;
				points = 7;
				player.getSkills().addXp(
						player.getSkillTasks().getCurrentTask().getSkill(), xp);
				player.getMoneyPouch().setAmount(cash, false);
			} else if (player.getSkillTasks().getCurrentTask().getDifficulty() == SkillerTasks.ELITE) {
				xp = 5000;
				player.setTaskPoints(player.getTaskPoints() + 10);
				cash = 200000;
				points = 10;
				player.getSkills().addXp(
						player.getSkillTasks().getCurrentTask().getSkill(), xp);
				player.getMoneyPouch().setAmount(cash, false);
			}
			player.setNextGraphics(new Graphics(1765));
			player.getPackets().sendGameMessage(
					"<col=00FFFF>You have been awarded with "
							+ Utils.format(xp * Settings.XP_RATE) + " xp, "
							+ Utils.format(cash) + " gp and " + points
							+ " points for completing your task!");
			player.getSkillTasks().setCurrentTask(null);
			xp = 0;
			cash = 0;
			return;
		}
	}

	/**
	 * Returns the current assigned task
	 *
	 * @return task
	 */
	public SkillTasks getCurrentTask() {
		return task;
	}

	/**
	 * Adds all tasks to an array that they meet the requirements then picks a
	 * random task from that list.
	 *
	 * @return
	 */
	public SkillTasks getNewTask(int tier) {
		final ArrayList<SkillTasks> tasks = new ArrayList<SkillTasks>();
		for (final SkillTasks t : SkillTasks.values()) {
			if (player.getSkills().getLevelForXp(t.getSkill()) < t.getLevel()) {
				continue;
			}
			if (t.getDifficulty() == tier)
				tasks.add(t);
		}
		setCurrentTask(tasks.get(Utils.random(tasks.size() - 1)));
		setTaskAmount(task.getAmount());
		return task;
	}

	/**
	 * Returns the current task amount.
	 *
	 * @return taskAmount
	 */
	public int getTaskAmount() {
		return taskAmount;
	}

	/**
	 * Checks if player has a task, if returns null, means there is no task.
	 *
	 * @return
	 */
	public boolean hasTask() {
		return task != null;
	}

	/**
	 * Checks if their task is completed.
	 *
	 * @return true if they have a task, and the amount remaining is 0,
	 *         otherwise false
	 */
	public boolean isCompleted() {
		return hasTask() && taskAmount == 0;
	}

	/**
	 * Set's the current task to be completed. if null, no task
	 *
	 * @param task
	 */
	public void setCurrentTask(SkillTasks task) {
		this.task = task;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * Sets the amount of the task to be completed. Decreases by 1 until ask is
	 * 0, at which points its completed.
	 *
	 * @param amount
	 */
	public void setTaskAmount(int amount) {
		this.taskAmount = amount;
	}
}