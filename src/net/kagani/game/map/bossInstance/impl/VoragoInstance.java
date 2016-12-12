package net.kagani.game.map.bossInstance.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.game.map.bossInstance.BossInstance;
import net.kagani.game.map.bossInstance.BossInstanceHandler;
import net.kagani.game.map.bossInstance.InstanceSettings;
import net.kagani.game.player.Player;

public class VoragoInstance extends BossInstance {

	private List<Player> playersOnBattle;

	public VoragoInstance(Player owner, InstanceSettings settings) {
		super(owner, settings);
		playersOnBattle = new CopyOnWriteArrayList<Player>();
	}

	public List<Player> getPlayersOnBattle() {
		return playersOnBattle;
	}

	public boolean isBattleOn() {
		synchronized (BossInstanceHandler.LOCK) {
			return !playersOnBattle.isEmpty();
		}
	}

	public boolean isPlayerOnBattle(Player player) {
		synchronized (BossInstanceHandler.LOCK) {
			return playersOnBattle.contains(player);
		}
	}

	@Override
	public int[] getMapSize() {
		return new int[] { 2, 3 };
	}

	@Override
	public int[] getMapPos() {
		return new int[] { 376, 744 };
	}

	@Override
	public void leaveInstance(Player player, int type) {
		leaveBattle(player);
		super.leaveInstance(player, type);
	}

	public void leaveBattle(Player player) {
		synchronized (BossInstanceHandler.LOCK) {
			player.getTimersManager().removeTimer(); // removes timer without
														// saving, wont do
														// anything if it wasnt
														// enabled yet
			playersOnBattle.remove(player);
			if (playersOnBattle.isEmpty())
				finishBattle();
		}
	}

	// accept challenge
	public void enterBattle(Player player) {
		playersOnBattle.add(player);
		if (playersOnBattle.size() == 1) {
			// vorago.startBattle(makes vorago head go into first stage)
		}
	}

	// clears the floor and deletes vorago
	public void finishBattle() {
		// vorago.endBattle(makes vorago head go into head)
	}

	@Override
	public void playMusic(Player player) {
		if (!isPlayerOnBattle(player)) { // forces no music
			player.getMusicsManager().forcePlayMusic(-2);
			return;
		}
		super.playMusic(player);
	}

	@Override
	public void loadMapInstance() {

	}

}
