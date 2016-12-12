package net.kagani.game.map.bossInstance.impl.godwars;

import net.kagani.game.World;
import net.kagani.game.map.bossInstance.BossInstance;
import net.kagani.game.map.bossInstance.InstanceSettings;
import net.kagani.game.player.Player;

public class KrilTsutsarothInstance extends BossInstance {

	/**
	 * @author: Dylan Page
	 */

	public KrilTsutsarothInstance(Player owner, InstanceSettings settings) {
		super(owner, settings);
	}

	@Override
	public int[] getMapPos() {
		return new int[] { 364, 664, 0 };
	}

	@Override
	public int[] getMapSize() {
		return new int[] { 1, 1 };
	}

	@Override
	public void loadMapInstance() {
		World.spawnNPC(6203, getTile(2926, 5324, 0), -1, true, false)
				.setBossInstance(this);
		World.spawnNPC(6204, getTile(2919, 5327, 0), -1, true, false)
				.setBossInstance(this);
		World.spawnNPC(6206, getTile(2930, 5326, 0), -1, true, false)
				.setBossInstance(this);
		World.spawnNPC(6208, getTile(2927, 5320, 0), -1, true, false)
				.setBossInstance(this);
	}
}