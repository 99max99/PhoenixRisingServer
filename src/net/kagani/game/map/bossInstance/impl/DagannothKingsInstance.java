package net.kagani.game.map.bossInstance.impl;

import net.kagani.game.World;
import net.kagani.game.map.bossInstance.BossInstance;
import net.kagani.game.map.bossInstance.InstanceSettings;
import net.kagani.game.player.Player;

public class DagannothKingsInstance extends BossInstance {

	public DagannothKingsInstance(Player owner, InstanceSettings settings) {
		super(owner, settings);
	}

	@Override
	public int[] getMapPos() {
		return new int[] { 360, 552 };
	}

	@Override
	public int[] getMapSize() {
		return new int[] { 1, 1 };
	}

	@Override
	public void loadMapInstance() {
		// kings
		World.spawnNPC(2881, getTile(2911, 4454, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(2882, getTile(2920, 4448, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(2883, getTile(2909, 4441, 0), -1, true).setBossInstance(
				this);
		// Spinolyp
		World.spawnNPC(2892, getTile(2912, 4465, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(2892, getTile(2923, 4462, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(2892, getTile(2902, 4462, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(2892, getTile(2913, 4433, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(2892, getTile(2928, 4438, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(2892, getTile(2903, 4435, 0), -1, true).setBossInstance(
				this);
		World.spawnNPC(2892, getTile(2930, 4450, 0), -1, true).setBossInstance(
				this);

	}

}
