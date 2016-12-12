package net.kagani.game.npc.dungeonnering;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public final class IcyBones extends DungeonBoss {

	public IcyBones(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
		spikes = new ArrayList<WorldObject>();
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	private List<WorldObject> spikes;

	public void removeSpikes() {
		if (spikes.isEmpty())
			return;
		for (WorldObject object : spikes)
			World.removeObject(object);
		spikes.clear();
	}

	public boolean sendSpikes() {
		if (!spikes.isEmpty())
			return false;
		int size = getSize();
		for (int x = -1; x < 7; x++) {
			for (int y = -1; y < 7; y++) {
				if (((x != -1 && x != 6) && (y != -1 && y != 6))
						|| Utils.random(2) != 0)
					continue;
				WorldTile tile = transform(x - size, y - size, 0);
				RoomReference current = getManager().getCurrentRoomReference(
						tile);
				if (current.getX() != getReference().getX()
						|| current.getY() != getReference().getY()
						|| !World.isFloorFree(tile.getPlane(), tile.getX(),
								tile.getY()))
					continue;
				WorldObject object = new WorldObject(52285 + Utils.random(3),
						10, Utils.random(4), tile.getX(), tile.getY(),
						tile.getPlane());
				spikes.add(object);
				World.spawnObject(object);
				for (Player player : getManager().getParty().getTeam()) {
					if (player.getX() == object.getX()
							&& player.getY() == object.getY())
						player.applyHit(new Hit(this, 1 + Utils
								.random(getMaxHit()), HitLook.REGULAR_DAMAGE));
				}
			}
		}
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				removeSpikes();
			}

		}, 10);
		return true;
	}

}
