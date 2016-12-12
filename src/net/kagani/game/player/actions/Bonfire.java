package net.kagani.game.player.actions;

import java.util.ArrayList;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.item.Item;
import net.kagani.game.npc.others.FireSpirit;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.skillertasks.SkillTasks;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Bonfire extends Action {

	public static enum Log {

		LOG(1511, 3098, 1, 40, 6), OAK(1521, 3099, 15, 60, 12), WILLOW(1519,
				3101, 30, 90, 18), TEAK(6333, 3103, 35, 105, 24), MAPLE(1517,
				3100, 45, 135, 36), MAHOGANY(6332, 3102, 50, 157.5, 42), EUCALYPTUS(
				12581, 3112, 58, 193, 48), YEWS(1515, 3111, 60, 202.5, 54), MAGIC(
				1513, 3135, 75, 303.8, 60), ELDER(29556, 3135, 91, 403.5, 75), BLISTERWOOD(
				21600, 3113, 76, 434, 60), CURSED_MAGIC(13567, 3116, 82, 434,
				60);
		private int logId, gfxId, level, boostTime;
		private double xp;

		private Log(int logId, int gfxId, int level, double xp, int boostTime) {
			this.logId = logId;
			this.gfxId = gfxId;
			this.level = level;
			this.xp = xp;
			this.boostTime = boostTime;
		}

		public int getLogId() {
			return logId;
		}

	}

	private Log log;
	private WorldObject object;
	private int count;

	public Bonfire(Log log, WorldObject object) {
		this.log = log;
		this.object = object;
	}

	private boolean checkAll(Player player) {
		if (!World.containsObjectWithId(object, object.getId()))
			return false;
		if (!player.getInventory().containsItem(log.logId, 1))
			return false;
		if (player.getSkills().getLevel(Skills.FIREMAKING) < log.level) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need level " + log.level
							+ " Firemaking to add these logs to a bonfire.");
			return false;
		}
		return true;
	}

	public static Log getLog(int logId) {
		for (Log log : Log.values()) {
			if (log.getLogId() == logId)
				return log;
		}
		return null;
	}

	public static boolean addLog(Player player, WorldObject object, Item item) {
		for (Log log : Log.values())
			if (log.logId == item.getId()) {
				player.getActionManager().setAction(new Bonfire(log, object));
				return true;
			}
		return false;
	}

	public static void addLogs(Player player, WorldObject object) {
		ArrayList<Log> possiblities = new ArrayList<Log>();
		for (Log log : Log.values())
			if (player.getInventory().containsItem(log.logId, 1))
				possiblities.add(log);
		Log[] logs = possiblities.toArray(new Log[possiblities.size()]);
		if (logs.length == 0)
			player.getPackets().sendGameMessage(
					"You do not have any logs to add to this fire.");
		else if (logs.length == 1)
			player.getActionManager().setAction(new Bonfire(logs[0], object));
		else
			player.getDialogueManager().startDialogue("BonfireD", logs, object);
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			player.getAppearence().setRenderEmote(2498);
			return true;
		}
		return false;

	}

	@Override
	public boolean process(Player player) {
		if (checkAll(player)) {
			if (Utils.random(750) == 0) {
				new FireSpirit(new WorldTile(object, 1), player);
				player.getPackets().sendGameMessage(
						"<col=ff0000>A fire spirit emerges from the bonfire.");
			}
			return true;
		}
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		player.getInventory().deleteItem(log.logId, 1);
		if (log == Log.LOG) {
			player.getSkillTasks().decreaseTask(SkillTasks.FNORMAL1);
			player.getSkillTasks().decreaseTask(SkillTasks.FNORMAL2);
		} else if (log == Log.OAK) {
			player.getSkillTasks().decreaseTask(SkillTasks.FOAK1);
			player.getSkillTasks().decreaseTask(SkillTasks.FOAK2);
		} else if (log == Log.WILLOW) {
			player.getSkillTasks().decreaseTask(SkillTasks.FWILLOW1);
			player.getSkillTasks().decreaseTask(SkillTasks.FWILLOW2);
			player.getSkillTasks().decreaseTask(SkillTasks.FWILLOW3);
		} else if (log == Log.MAPLE) {
			player.getSkillTasks().decreaseTask(SkillTasks.FMAPLE1);
			player.getSkillTasks().decreaseTask(SkillTasks.FMAPLE2);
		} else if (log == Log.YEWS) {
			player.getSkillTasks().decreaseTask(SkillTasks.FYEW1);
			player.getSkillTasks().decreaseTask(SkillTasks.FYEW2);
			player.getSkillTasks().decreaseTask(SkillTasks.FYEW3);
		} else if (log == Log.MAGIC) {
			player.getSkillTasks().decreaseTask(SkillTasks.FMAGIC1);
			player.getSkillTasks().decreaseTask(SkillTasks.FMAGIC2);
		}
		player.getSkillTasks().decreaseTask(SkillTasks.FBON1);
		player.getSkillTasks().decreaseTask(SkillTasks.FBON2);
		player.getSkillTasks().decreaseTask(SkillTasks.FBON3);

		player.getSkills().addXp(Skills.FIREMAKING,
				Firemaking.increasedExperience(player, log.xp));
		player.setNextAnimation(new Animation(16703));
		player.setNextGraphics(new Graphics(log.gfxId));
		player.getPackets().sendGameMessage("You add a log to the fire.", true);
		if (player.getDailyTask() != null)
			player.getDailyTask().incrementTask(player, 3, log.logId,
					Skills.FIREMAKING);
		if (count++ == 4
				&& player.getEffectsManager().hasActiveEffect(
						EffectType.BONFIRE)) {
			player.getEffectsManager().startEffect(
					new Effect(EffectType.BONFIRE, log.boostTime * 100));
			int percentage = (int) (getBonfireBoostMultiplier(player) * 100 - 100);
			player.getPackets().sendGameMessage(
					"<col=00ff00>The bonfire's warmth increases your maximum health by "
							+ percentage + "%. This will last " + log.boostTime
							+ " minutes.");
		}
		return 6;
	}

	public static double getBonfireBoostMultiplier(Player player) {
		int fmLvl = player.getSkills().getLevel(Skills.FIREMAKING);
		if (fmLvl >= 90)
			return 1.1;
		if (fmLvl >= 80)
			return 1.09;
		if (fmLvl >= 70)
			return 1.08;
		if (fmLvl >= 60)
			return 1.07;
		if (fmLvl >= 50)
			return 1.06;
		if (fmLvl >= 40)
			return 1.05;
		if (fmLvl >= 30)
			return 1.04;
		if (fmLvl >= 20)
			return 1.03;
		if (fmLvl >= 10)
			return 1.02;
		return 1.01;

	}

	@Override
	public void stop(final Player player) {
		player.getEmotesManager().setNextEmoteEnd(2400);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(16702));
				player.getAppearence().setRenderEmote(-1);
			}
		}, 3);
	}
}