package net.kagani.game.player.content.reaper;

import java.io.Serializable;
import java.util.ArrayList;

import net.kagani.Settings;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class Reaper implements Serializable {

	/**
	 * @author: Dylan Page
	 */

	private static final long serialVersionUID = -8124545900972130417L;

	private transient Player player;
	public ReaperTasks task;
	public int taskAmount, points, xp;

	public ReaperTasks generateTask() {
		final ArrayList<ReaperTasks> tasks = new ArrayList<ReaperTasks>();
		for (final ReaperTasks t : ReaperTasks.values()) {
			if (player.getSkills().getLevelForXp(18) < t.getRequirement())
				continue;
			tasks.add(t);
		}
		player.reaperTask++;
		if (player.isAMember()) {
			if (player.reaperTask >= 3) {
				player.lastReaperTask = Utils.currentTimeMillis();
			} else if (player.reaperTask >= 4) {
				player.reaperTask = 0;
			}
		} else {
			if (player.reaperTask >= 2) {
				player.lastReaperTask = Utils.currentTimeMillis();
			} else if (player.reaperTask >= 3) {
				player.reaperTask = 0;
			}
		}
		setCurrentTask(tasks.get(Utils.random(tasks.size() - 1)));
		setTaskAmount(giveAmount());
		setXp();
		setPoints();
		return task;
	}

	private int getPoints() {
		return points;
	}

	private int getXp() {
		return xp;
	}

	private int setPoints() {
		switch (task.getNPCId()) {
		case 19457:
			return this.points = 20;
		case 2025:
			return this.points = 7;
		case 3200:
			return this.points = 10;
		case 17161:
			return this.points = 25;
		case 13447:
			return this.points = 17;
		case 8133:
			return this.points = 12;
		case 16697:
			return this.points = 15;
		case 1158:
			return this.points = 10;
		case 50:
			return this.points = 7;
		case 15454:
		case 15507:
		case 15506:
		case 15509:
			return this.points = 10;
		case 6247:
		case 6203:
		case 6260:
		case 6222:
			return this.points = 12;
		case 2883:
			return this.points = 10;
		case 3340:
			return this.points = 7;
		case 2745:
			return this.points = 10;
		}
		return this.points = 7;
	}

	private int setXp() {
		switch (task.getNPCId()) {
		case 19457:
			return this.xp = 20000;
		case 2025:
			return this.xp = 7000;
		case 3200:
			return this.xp = 10000;
		case 17161:
			return this.xp = 20000;
		case 13447:
			return this.xp = 17000;
		case 8133:
			return this.xp = 12000;
		case 16697:
			return this.xp = 15000;
		case 1158:
			return this.xp = 10000;
		case 50:
			return this.xp = 7000;
		case 15454:
		case 15507:
		case 15506:
		case 15509:
			return this.xp = 10000;
		case 6247:
		case 6203:
		case 6260:
		case 6222:
			return this.xp = 12000;
		case 2883:
			return this.xp = 1000;
		case 3340:
			return this.xp = 7000;
		case 2745:
			return this.xp = 7000;
		}
		return this.xp = 7000;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int giveAmount() {
		switch (Utils.random(5)) {
		case 0:
			return 14;
		case 1:
			return 17;
		case 2:
			return 19;
		case 3:
			return 22;
		case 4:
			return 34;
		}
		return 20;
	}

	public int getAmount() {
		return taskAmount;
	}

	public ReaperTasks getCurrentTask() {
		return task;
	}

	public boolean hasTask() {
		return task != null;
	}

	public void setCurrentTask(ReaperTasks task) {
		this.task = task;
	}

	public void setTaskAmount(int amount) {
		this.taskAmount = amount;
	}

	public void checkKillsLeft() {
		if (task == null) {
			player.getPackets().sendGameMessage(
					"You have no reaper assignment.");
			return;
		}
		player.getPackets().sendGameMessage(
				"You are currently assigned to collect souls from: "
						+ task.getName() + ".");
		player.getPackets().sendGameMessage(
				"You must retrieve " + getAmount()
						+ " to complete your assignment.");
	}

	public void updateTask() {
		if (getAmount() < 2) {
			player.getPackets().sendGameMessage(
					"<col=009a00>You have completed Death's assignment, gaining "
							+ Utils.format(getXp() * Settings.XP_RATE / 2)
							+ " Slayer XP and " + getPoints()
							+ " reaper points.");
			player.getSkills().addSkillXpRefresh(18,
					getXp() * Settings.XP_RATE / 2);
			player.setReaperPoints(player.getReaperPoints() + getPoints());
			setCurrentTask(null);
			setTaskAmount(0);
			xp = 0;
			points = 0;
			return;
		}
		taskAmount--;
	}
}