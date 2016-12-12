package net.kagani.game.map.bossInstance.impl;

import net.kagani.game.World;
import net.kagani.game.map.bossInstance.BossInstance;
import net.kagani.game.map.bossInstance.InstanceSettings;
import net.kagani.game.player.Player;

public class EvilChickenInstance extends BossInstance {

	/**
	 * @author: Dylan Page
	 */

	public EvilChickenInstance(Player owner, InstanceSettings settings) {
		super(owner, settings);
	}

	@Override
	public int[] getMapPos() {
		return new int[] { 0, 0 }; // TODO
	}

	@Override
	public int[] getMapSize() {
		return new int[] { 1, 1 };
	}

	@Override
	public void loadMapInstance() {
		World.spawnNPC(3375, getTile(2646, 10424, 0), -1, true, false).setBossInstance(this);
	}
}