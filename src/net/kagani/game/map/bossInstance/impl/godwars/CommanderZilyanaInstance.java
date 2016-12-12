package net.kagani.game.map.bossInstance.impl.godwars;

import net.kagani.game.World;
import net.kagani.game.map.bossInstance.BossInstance;
import net.kagani.game.map.bossInstance.InstanceSettings;
import net.kagani.game.player.Player;

public class CommanderZilyanaInstance extends BossInstance {

	/**
	 * @author: Dylan Page
	 */

	public CommanderZilyanaInstance(Player owner, InstanceSettings settings) {
		super(owner, settings);
	}

	@Override
	public int[] getMapPos() {
		return new int[] { 363, 654, 0 };
	}

	@Override
	public int[] getMapSize() {
		return new int[] { 1, 1 };
	}

	@Override
	public void loadMapInstance() {
		World.spawnNPC(6247, getTile(2924, 5250, 0), -1, true, false)
				.setBossInstance(this);
		World.spawnNPC(6252, getTile(2926, 5250, 0), -1, true, false)
				.setBossInstance(this);
		World.spawnNPC(6250, getTile(2916, 5253, 0), -1, true, false)
				.setBossInstance(this);
		World.spawnNPC(6248, getTile(2928, 5252, 0), -1, true, false)
				.setBossInstance(this);
	}
}