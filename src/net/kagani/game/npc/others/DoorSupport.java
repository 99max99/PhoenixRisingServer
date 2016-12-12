package net.kagani.game.npc.others;

import java.util.concurrent.TimeUnit;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.utils.Logger;

@SuppressWarnings("serial")
public class DoorSupport extends NPC {

	public DoorSupport(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setCantFollowUnderCombat(true);
	}

	@Override
	public void processNPC() {
		cancelFaceEntityNoCheck();
	}

	public boolean canDestroy(Player player) {
		if (getId() == 2446)
			return player.getY() < getY();
		if (getId() == 2440)
			return player.getY() > getY();
		return player.getX() > getX();
	}

	@Override
	public void sendDeath(Entity killer) {
		setNextNPCTransformation(getId() + 1);
		final WorldObject door = World.getObjectWithId(this, 8967);
		if (door != null)
			World.removeObject(door);
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					setNextNPCTransformation(getId() - 1);
					reset();
					if (door != null)
						World.spawnObject(door);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, 60, TimeUnit.SECONDS);
	}

}
