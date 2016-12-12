package net.kagani.game.minigames.clanwars;

import java.util.TimerTask;

import net.kagani.game.player.Player;
import net.kagani.utils.Logger;

/**
 * The timer task subclass used for clan wars updating.
 * 
 * @author Emperor
 * 
 */
public final class ClanWarsTimer extends TimerTask {// this is the class for
	// updating the timer

	/**
	 * The clan wars object.
	 */
	private final ClanWars clanWars;

	/**
	 * The start ticks (before the wall goes down and the war commences).
	 */
	private int startTicks = 200;

	/**
	 * If the clan war has started.
	 */
	private boolean started;

	/**
	 * The amount of time left.
	 */
	private int timeLeft;

	/**
	 * The last amount of minutes.
	 */
	private int lastMinutes = -1;

	/**
	 * The time-out counter, we use this to check if the war has expired due to
	 * inactivity.
	 */
	private int timeOut = 0;

	/**
	 * Constructs a new {@code ClanWarsTimer} {@code Object}.
	 * 
	 * @param clanWars
	 *            The clan wars object.
	 */
	public ClanWarsTimer(ClanWars clanWars) {
		this.clanWars = clanWars;
		this.timeLeft = clanWars.getTimeLeft();
	}

	@Override
	public void run() {
		try {
			if (!started) {
				if (startTicks-- == 8) {
					WallHandler.dropWall(clanWars);
					for (Player player : clanWars.getFirstPlayers())
						player.getPackets()
								.sendMusicEffectOld(getMusicEffect());
					for (Player player : clanWars.getSecondPlayers())
						player.getPackets()
								.sendMusicEffectOld(getMusicEffect());
				} else if (startTicks == 100) {
					for (Player player : clanWars.getFirstPlayers())
						player.getPackets()
								.sendMusicEffectOld(getMusicEffect());
					for (Player player : clanWars.getSecondPlayers())
						player.getPackets()
								.sendMusicEffectOld(getMusicEffect());
				} else if (startTicks == 0) {
					started = true;
					for (Player player : clanWars.getFirstPlayers()) {
						player.getPackets().sendCSVarInteger(270, 0);
						player.getPackets().sendCSVarInteger(260, 1);
					}
					for (Player player : clanWars.getSecondPlayers()) {
						player.getPackets().sendCSVarInteger(270, 0);
						player.getPackets().sendCSVarInteger(260, 1);
					}
					WallHandler.removeWall(clanWars);
					clanWars.updateWar();
				}
				return;
			}
			if (clanWars.getPlayersInside(false) == 0
					|| clanWars.getPlayersInside(true) == 0) {
				if (++timeOut == 1_000) { // 10 minutes until the war is
					// time-out.
					clanWars.endWar();
					cancel();
					return;
				}
			} else {
				timeOut = 0; // Reset time-out.
			}
			if ((timeLeft * 0.6) / 60 != lastMinutes) {
				lastMinutes = (int) Math.ceil((timeLeft * 0.6) / 60);
				for (Player player : clanWars.getFirstPlayers()) {
					player.getPackets().sendCSVarInteger(270, lastMinutes);
				}
				for (Player player : clanWars.getSecondPlayers()) {
					player.getPackets().sendCSVarInteger(270, lastMinutes);
				}
			}
			if (timeLeft-- == 0) {
				clanWars.endWar();
				cancel();
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public int getMusicEffect() {
		if (startTicks > 100)
			return 290;
		if (startTicks > 8)
			return 291;
		return 289;
	}

	/**
	 * Joins the war.
	 * 
	 * @param p
	 *            The player.
	 * @param firstTeam
	 *            If the player is part of the first team/viewers.
	 */
	public void refresh(Player p, boolean firstTeam) {
		int count1 = clanWars.getPlayersInside(false);
		int count2 = clanWars.getPlayersInside(true);
		p.getPackets().sendCSVarInteger(261, firstTeam ? count1 : count2);
		p.getPackets().sendCSVarInteger(262, firstTeam ? count2 : count1);
		p.getPackets().sendCSVarInteger(263,
				clanWars.getKills() >> (firstTeam ? 0 : 24) & 0xFFFF);
		p.getPackets().sendCSVarInteger(264,
				clanWars.getKills() >> (firstTeam ? 24 : 0) & 0xFFFF);
		p.getPackets().sendCSVarInteger(260, started ? 1 : 0);
		p.getPackets()
				.sendCSVarInteger(270, started ? lastMinutes : startTicks);
		p.getMusicsManager().forcePlayMusic(442);
	}

	/**
	 * If the war has started.
	 * 
	 * @return {@code True} if so.
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * Gets the time left.
	 * 
	 * @return The time left.
	 */
	public int getTimeLeft() {
		return timeLeft;
	}

	/**
	 * Checks if the clan ended due to a timeout.
	 * 
	 * @return {@code True} if so.
	 */
	public boolean isTimeOut() {
		return timeOut > 499;
	}

}