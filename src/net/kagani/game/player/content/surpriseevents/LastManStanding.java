package net.kagani.game.player.content.surpriseevents;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import net.kagani.Settings;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class LastManStanding implements SurpriseEvent {

	/**
	 * 3 minutes before the event starts.
	 */
	public static final int PREP_MINS = 5;
	/**
	 * Maximum game time.
	 */
	public static final int GAME_MINS = 15;

	/**
	 * Cash for event winner.
	 */
	public static final int REWARD_CASH_FINAL = 5000000;
	/**
	 * Spins for event winner.
	 */
	public static final int REWARD_SPINS_FINAL = 10;

	/**
	 * Drop rate modifier.
	 */
	public static final double REWARD_DROP_MOD_PER_KILL = 0.75;
	/**
	 * Cash reward for single kill.
	 */
	public static final int REWARD_CASH_PER_KILL = 300000;

	/**
	 * Phases... 0 - Pre-start 1 - Initializing area 2 - Accepting
	 * players/waiting till game 3 - Game 4 - Shutdown
	 */
	private int phase = 0;

	/**
	 * Our arena.
	 */
	public EventArena arena;
	/**
	 * Our task.
	 */
	private TimerTask task;

	/**
	 * When we will start game and end game.
	 */
	private long startTime, endTime;

	/**
	 * Our lock
	 */
	private Object lock = new Object();

	/**
	 * List of all players.
	 */
	private List<Player> players = new ArrayList<Player>();

	/**
	 * Alive players.
	 */
	private List<Player> alive = new ArrayList<Player>();

	@Override
	public void start() {
		if (phase != 0)
			return;
		phase = 1;
		GameExecutorManager.fastExecutor.schedule(task = new TimerTask() {
			@Override
			public void run() {
				try {
					if (phase == 2 || phase == 3)
						LastManStanding.this.run();
					else if (phase == 4) {
						task.cancel();
						task = null;

						GameExecutorManager.slowExecutor
								.execute(new Runnable() {
									@Override
									public void run() {
										arena.destroy();
										arena = null;
									}
								});
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0L, 1000);

		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				arena = ArenaFactory.randomEventArena(true);
				arena.create();
				phase = 2;
				timerinit();
			}
		});
	}

	private void timerinit() {
		startTime = Utils.currentTimeMillis() + (1000 * 60 * PREP_MINS);
		endTime = startTime + (1000 * 60 * GAME_MINS);
		World.sendNews(null, "Last man standing event will start in "
				+ PREP_MINS + " minute! Talk to oracle to get there.",
				World.WORLD_NEWS);
	}

	private void run() {
		if (phase == 2 && canBegin())
			begin();
		else if (phase == 3 && canEnd())
			end();
	}

	private boolean canBegin() {
		synchronized (lock) {
			if (players.size() < 2)
				return false;
		}
		return Utils.currentTimeMillis() >= startTime;
	}

	private void begin() {
		phase = 3;
		synchronized (lock) {
			for (Player player : players) {
				player.setCanPvp(true);
				alive.add(player);
			}
		}
		World.sendNews(null, "Last man standing event has started!",
				World.WORLD_NEWS);
	}

	private boolean canEnd() {
		synchronized (lock) {
			return Utils.currentTimeMillis() >= endTime || players.size() < 1
					|| alive.size() < 2;
		}
	}

	private void end() {
		phase = 4;

		synchronized (lock) {
			if (alive.size() == 1) {
				Player winner = alive.iterator().next();
				World.sendNews(
						null,
						"Last man standing event has ended, winner: "
								+ winner.getDisplayName(), World.WORLD_NEWS);
				winner.getInventory().addItemDrop(995, REWARD_CASH_FINAL);
			} else {
				World.sendNews(null,
						"Last man standing event has ended without winner.",
						World.WORLD_NEWS);
			}

			List<Player> ps = new ArrayList<Player>(players);
			for (Player player : ps)
				player.getControlerManager().forceStop();
		}

	}

	public void forceleave(final Player player) {
		synchronized (lock) {
			player.setCanPvp(false);
			player.stopAll();
			player.reset();
			player.getAppearence().setHidden(false);
			player.getAppearence().setIdentityHide(false);
			player.useStairs(-1, Settings.HOME_LOCATION, 0, 1);

			players.remove(player);
			alive.remove(player);
		}
	}

	@Override
	public void tryJoin(final Player player) {
		if (phase != 2)
			return;

		synchronized (lock) {
			players.add(player);
		}

		player.stopAll();
		player.reset();
		player.getAppearence().setHidden(false);
		player.getAppearence().setIdentityHide(true);
		player.useStairs(-1, arena.randomSpawn(), 0, 1);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (player.getFamiliar() != null)
					player.getFamiliar().sendDeath(player);
				player.getControlerManager().startControler(
						"LastManStandingController", LastManStanding.this);
			}
		}, 1);
	}

	public int getPhase() {
		return phase;
	}

	public long startTime() {
		return startTime;
	}

	public long endTime() {
		return endTime;
	}

	public Object getLock() {
		return lock;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Player> getAlive() {
		return alive;
	}

	public EventArena getArena() {
		return arena;
	}

}
