package net.kagani.game.npc.dungeonnering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class LakkTheRiftSplitter extends DungeonBoss {

	private static final int[] RAIN_GRAPHICS = { 2581, 2583, 2585 };

	private List<PortalCluster> clusters;

	public LakkTheRiftSplitter(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
		clusters = new CopyOnWriteArrayList<PortalCluster>();
	}

	@Override
	public void processNPC() {
		if (isDead() || clusters == null)
			return;
		super.processNPC();
		for (PortalCluster cluster : clusters) {
			cluster.incrementCycle();
			if (cluster.getCycle() == 35) {
				clusters.remove(cluster);
				continue;
			}
			for (Entity t : getPossibleTargets()) {
				Player player = (Player) t;
				if (cluster.getCycle() < 1)
					continue;
				if (cluster.getCycle() % 2 == 0) {
					for (WorldTile tile : cluster.getBoundary()) {
						if (player.getX() == tile.getX()
								&& player.getY() == tile.getY()) {
							cluster.increaseEffectMultipier();
							int type = cluster.getType();
							double effectMultiplier = cluster
									.getEffectMultiplier();
							int maxHit = getMaxHit();

							if (type == 0)
								player.applyHit(new Hit(
										this,
										(int) (Utils.random(maxHit * .35,
												maxHit * .55) * effectMultiplier),
										HitLook.REGULAR_DAMAGE));
							else if (type == 1)
								EffectsManager
										.makePoisoned(
												player,
												(int) (Utils.random(
														maxHit * .10,
														maxHit * .30) * effectMultiplier));
							else {
								int skill = Utils.random(6);
								player.getSkills()
										.drainLevel(
												skill == 3 ? Skills.MAGIC
														: skill,
												(int) (Utils.random(2, 3) * effectMultiplier));
							}
						}
					}
				}
			}
			if (cluster.getCycle() % 15 == 0)
				submitGraphics(cluster, this);
		}
	}

	@Override
	public void sendDeath(Entity killer) {
		super.sendDeath(killer);
		clusters.clear();
	}

	public void addPortalCluster(int type, WorldTile[] boundary) {
		PortalCluster cluster = new PortalCluster(type, boundary);
		submitGraphics(cluster, this);
		clusters.add(cluster);
	}

	public static void submitGraphics(PortalCluster cluster, NPC creator) {
		for (WorldTile tile : cluster.getBoundary())
			World.sendGraphics(creator, new Graphics((Utils.random(3) == 0 ? 1
					: 0) + RAIN_GRAPHICS[cluster.getType()]), tile);
	}

	private static class PortalCluster {

		private final int type;
		private final WorldTile[] boundary;
		private int cycle;
		private double effectMultiplier;

		public PortalCluster(int type, WorldTile[] boundary) {
			this.type = type;
			this.boundary = boundary;
			effectMultiplier = 0.5;
		}

		public WorldTile[] getBoundary() {
			return boundary;
		}

		public int getType() {
			return type;
		}

		public void incrementCycle() {
			cycle++;
		}

		public int getCycle() {
			return cycle;
		}

		public double getEffectMultiplier() {
			return effectMultiplier;
		}

		public void increaseEffectMultipier() {
			effectMultiplier += 0.5;
		}
	}

	public boolean doesBoundaryOverlap(List<WorldTile> boundaries) {
		for (PortalCluster cluster : clusters) {
			for (WorldTile tile : cluster.getBoundary()) {
				for (WorldTile boundary : boundaries) {
					if (tile.getX() == boundary.getX()
							&& tile.getY() == boundary.getY())
						return true;
				}
			}
		}
		return false;
	}
}
