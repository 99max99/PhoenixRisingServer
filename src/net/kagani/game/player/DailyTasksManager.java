package net.kagani.game.player;

import java.util.HashMap;
import java.util.Map;

import net.kagani.game.player.QuestManager.Quests;
import net.kagani.utils.Utils;

public class DailyTasksManager {

	private static Map<Byte, TaskData[]> TASKS_THRESHHOLD;

	/**
	 * The level of difficulty assigned with the task.
	 */
	private static final byte EASY = 0, MEDIUM = 1, HARD = 2, EXTREME = 3,
			FIXED = 4;

	static {
		/**
		 * Populate the list with enabled tasks.
		 */
		TASKS_THRESHHOLD = new HashMap<Byte, TaskData[]>();

		/**
		 * Separate tasks by type here.
		 */
		TASKS_THRESHHOLD.put(EASY, new TaskData[] { TaskData.TEST_TASK });

	}

	/**
	 * The type of task we are assigning.
	 */
	private static final byte EXPERIENCE = 0, SKILL = 1, PVP = 2, PVM = 3,
			MINIGAME = 4;

	private class SpecialTask {
		private final TaskData task;

		private boolean completed;
		private int currentCount;
		private double[] experienceTracker;

		public SpecialTask(TaskData task) {
			this.task = task;
		}

		public int getCurrentCount() {
			return currentCount;
		}

		public void setCurrentCount(int currentCount) {
			if (currentCount > getMaximumCount())
				currentCount = getMaximumCount();
			this.currentCount = currentCount;
		}

		public double[] getExperienceTracker() {
			return experienceTracker;
		}

		public void setExperienceTracker(double[] experienceTracker) {
			this.experienceTracker = experienceTracker;
		}

		public int getMaximumCount() {
			return task.maximumCount;
		}

		public byte getTaskType() {
			return task.taskType;
		}

		public Object[] getParamaters() {
			return task.paramaters;
		}

		public void setCompleted(boolean completed) {
			this.completed = completed;
		}

		public boolean isCompleted() {
			return completed;
		}

		public String getName() {
			return task.toString();
		}
	}

	/**
	 * Parameters change based off type.
	 * 
	 * @author Khaled
	 * 
	 */
	public enum TaskData {
		/**
		 * Experience format: new int[] skills[], new double experience[]
		 */
		TEST_TASK(10, EXPERIENCE);

		private int maximumCount;
		private byte taskType;
		private Object[] paramaters;

		private TaskData(int maximumCount, byte taskType, Object... paramaters) {
			this.maximumCount = maximumCount;
			this.taskType = taskType;
			this.paramaters = paramaters;
		}

		@Override
		public String toString() {// TODO task names that are funny, separate
			// with $ as sentinel
			return name().replace("_", " ");
		}
	}

	private transient Player player;
	private SpecialTask dailyTask, sharedTask;
	private int completedTasks, consecutiveTasks;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void checkForProgression(SpecialTask task, int type,
			Object... paramaters) {
		if (task.isCompleted() || task.getTaskType() != type)
			return;
		if (type == EXPERIENCE) {
			int skill = (int) paramaters[0];
			double experience = (double) paramaters[1];
			double[] experienceCap = (double[]) task.getParamaters()[1];
			if (task.getExperienceTracker() == null)
				task.setExperienceTracker(new double[experienceCap.length]);
			for (int idx = 0; idx < task.getExperienceTracker().length; idx++) {
				if (((int[]) task.getParamaters()[0])[idx] == skill) {
					task.getExperienceTracker()[idx] += experience;
					if (task.getExperienceTracker()[idx] > experienceCap[idx])
						task.getExperienceTracker()[idx] = experienceCap[idx];
					player.getPackets().sendGameMessage(
							"<col=EAC117> Daily Task Update: "
									+ task.getExperienceTracker()[skill]
									+ " / " + experienceCap[skill]
									+ " for skill: " + Skills.SKILL_NAME[skill]
									+ ".", true);
					break;
				}
			}
			for (int idx = 0; idx < task.getExperienceTracker().length; idx++) {// We
				// only
				// check
				// this
				// here
				// cuz
				// of
				// exp
				// tracker
				if (task.getExperienceTracker()[idx] != experienceCap[idx])
					return;
			}
			setCompletedTask(task);
		} else {
			if (type == SKILL) {
				int skill = (int) paramaters[0];// which skill are we using
				Object skillExtention = paramaters[1];
				int increment = (int) paramaters[2];
				if (skill != (int) task.getParamaters()[0]
						|| skillExtention != task.getParamaters()[1])// cuz
					// peas.
					return;
				sendTaskUpdate(type, increment, task);
			} else if (type == PVP) {
				sendTaskUpdate(type, 1, task);
			} else if (type == PVM) {
				Object identifier = task.getParamaters()[0];
				if (identifier instanceof String) {
					String actualName = (String) identifier;
					String name = (String) paramaters[0];
					if (actualName.contains(name))
						sendTaskUpdate(type, 1, task);
				} else {
					Class<?> clazz = (Class<?>) identifier;
					Class<?> returnedClazz = (Class<?>) paramaters[0];
					if (clazz == returnedClazz)
						sendTaskUpdate(type, 1, task);
				}
			} else if (type == MINIGAME) {
				byte minigame = (byte) paramaters[0];
				if (minigame == (int) task.getParamaters()[0])
					sendTaskUpdate(type, 1, task);
			}
			if (task.getCurrentCount() == task.getMaximumCount())
				setCompletedTask(task);
		}
	}

	private void sendTaskUpdate(int type, int increment, SpecialTask task) {
		String message = "<col=EAC117>Daily Task Update: ";// Default color
		if (type == SKILL)
			message += "Completed " + task.getCurrentCount() + " "
					+ task.getName() + " out of " + task.getMaximumCount()
					+ ".";
		else if (type == PVP)
			message += "Completed " + task.getCurrentCount() + " out of "
					+ task.getMaximumCount() + " PVP kills.";
		else if (type == PVM)
			message += "Completed " + task.getCurrentCount() + " out of "
					+ task.getMaximumCount() + " " + task.getName()
					+ " PVM kills";
		else if (type == MINIGAME)
			message += "Completed " + task.getCurrentCount() + " out of "
					+ task.getMaximumCount() + " " + task.getName()
					+ " minigame wins.";
		player.getPackets().sendGameMessage(message);
		task.setCurrentCount(task.getCurrentCount() + increment);
	}

	private void setCompletedTask(SpecialTask task) {
		consecutiveTasks++;
		completedTasks++;
		task.setCompleted(true);
		// player.getInterfaceManager().setOverlay(199, false);
		// player.getPackets().sendIComponentText(199, "Your task '");
		player.getPackets()
				.sendGameMessage(
						"<col=EAC117> Daily Task Update: Your task has been completed, go speak to the task manager for your reward!");
	}

	public int getConsecutiveTasks() {
		return consecutiveTasks;
	}

	public int getCompletedTasks() {
		return completedTasks;
	}

	public void setConsecutiveTasks(int consecutiveTasks) {
		this.consecutiveTasks = consecutiveTasks;
	}

	public static TaskData generateTask(Player player, boolean isFixed) {
		final TaskData[] POSSIBLE_TASKS = TASKS_THRESHHOLD.get(isFixed ? FIXED
				: Utils.random(player.isAMember() ? EASY : MEDIUM, EXTREME));
		do {
			TaskData task = POSSIBLE_TASKS[Utils.random(POSSIBLE_TASKS.length)];
			if (task != null && hasRequirements(player, task))
				return task;
		} while (true);
	}

	public static boolean hasRequirements(Player player, TaskData task) {
		if (task.taskType != SKILL)
			return true;
		final int[] REQUIREMENTS = (int[]) task.paramaters[2];
		for (int idx = 0; idx < REQUIREMENTS.length; idx += 2) {
			int skill = REQUIREMENTS[idx], level = REQUIREMENTS[idx + 1];
			if (player.getSkills().getLevelForXp(skill) < level)
				return false;
		}
		if (task.paramaters.length == 3) {
			Quests[] quests = (Quests[]) task.paramaters[3];// Does our skill
			// need any quests?
			for (Quests quest : quests) {
				if (!player.getQuestManager().completedQuest(quest))
					return false;
			}
		}
		return true;
	}
}