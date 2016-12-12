package net.kagani.game.map.bossInstance;

import java.io.Serializable;

import net.kagani.game.map.bossInstance.BossInstanceHandler.Boss;
import net.kagani.utils.Utils;

public class InstanceSettings implements Serializable {

	private static final long serialVersionUID = 3082211665373930652L;

	private Boss boss;
	private int minCombat, maxPlayers, spawnSpeed, protection;
	private boolean practiseMode, hardMode;

	private long creationTime;

	public InstanceSettings(Boss boss, int maxPlayers, int minCombat,
			int spawnSpeed, int protection, boolean practiseMode,
			boolean hardMode) {
		this.boss = boss;
		this.minCombat = minCombat;
		this.spawnSpeed = spawnSpeed;
		this.protection = protection;
		this.practiseMode = practiseMode;
		this.hardMode = hardMode;
	}

	public InstanceSettings(Boss boss) {
		this.boss = boss;
	}

	public void setPractiseMode(boolean practiseMode) {
		this.practiseMode = practiseMode;
	}

	public void setHardMode(boolean hardMode) {
		this.hardMode = hardMode;
	}

	public void setMinCombat(int minCombat) {
		this.minCombat = minCombat;
	}

	public void setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
	}

	public void setSpawnSpeed(int spawnSpeed) {
		this.spawnSpeed = spawnSpeed;
	}

	public void setProtection(int protection) {
		this.protection = protection;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public boolean isHardMode() {
		return hardMode;
	}

	public boolean isPractiseMode() {
		return practiseMode;
	}

	public int getMinCombat() {
		return minCombat;
	}

	public int getProtection() {
		return protection;
	}

	public int getSpawnSpeed() {
		return spawnSpeed;
	}

	public Boss getBoss() {
		return boss;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	/*
	 * in milliseconds
	 */
	public long getTimeRemaining() {
		long diff = (Utils.currentTimeMillis() - creationTime);
		long timeRemaining = 60 * 60 * 1000 - diff;
		return timeRemaining < 0 ? 0 : timeRemaining;
	}

	public boolean hasTimeRemaining() {
		return getTimeRemaining() > 0;
	}
}
