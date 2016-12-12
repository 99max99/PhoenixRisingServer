package net.kagani.game.map.bossInstance.impl;

import net.kagani.game.World;
import net.kagani.game.map.bossInstance.BossInstance;
import net.kagani.game.map.bossInstance.InstanceSettings;
import net.kagani.game.player.Player;

public class KalphiteQueenInstance extends BossInstance {

	public KalphiteQueenInstance(Player owner, InstanceSettings settings) {
		super(owner, settings);
	}

	@Override
	public int[] getMapPos() {
		return new int[] { 433, 1185 };
	}

	@Override
	public int[] getMapSize() {
		return new int[] { 1, 1 };
	}

	@Override
	public void loadMapInstance() {
		// guardian
		World.spawnNPC(1157, getTile(3493, 9493, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(1157, getTile(3487, 9487, 0), -1, true).setBossInstance(
				this);
		// larva
		World.spawnNPC(1161, getTile(3483, 9505, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(1161, getTile(3479, 9489, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(1161, getTile(3496, 9515, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(1161, getTile(3485, 9516, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(1161, getTile(3470, 9489, 0), -1, true).setBossInstance(
				this);
		// queen
		World.spawnNPC(1158, getTile(3484, 9491, 0), -1, true).setBossInstance(
				this);
	}

}
