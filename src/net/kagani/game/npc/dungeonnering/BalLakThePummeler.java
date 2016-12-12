package net.kagani.game.npc.dungeonnering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class BalLakThePummeler extends DungeonBoss {

	private boolean skip;
	private int barPercentage;

	private List<PoisionPuddle> puddles = new CopyOnWriteArrayList<PoisionPuddle>();

	public BalLakThePummeler(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
		setLureDelay(6000); // this way you can lure him hehe, still not as much
		// as outside dung npcs
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		super.processNPC();
		skip = !skip;
		if (!skip) {
			boolean reduced = false;
			for (PoisionPuddle puddle : puddles) {
				puddle.cycles++;
				if (puddle.canDestroyPoision()) {
					puddles.remove(puddle);
					continue;
				}
				List<Entity> targets = getPossibleTargets(true, true);
				if (Utils.colides(getX(), getY(), getSize(),
						puddle.tile.getX(), puddle.tile.getY(), 1)) {
					barPercentage = barPercentage > 1 ? barPercentage - 2 : 0;
					sendDefenceBar();
					reduced = true;
				}
				for (Entity t : targets) {
					if (!t.withinDistance(puddle.tile, 2))
						continue;
					t.applyHit(new Hit(this, Utils.random((int) (t
							.getHitpoints() * 0.25)) + 1,
							HitLook.REGULAR_DAMAGE));
				}
			}
			if (!reduced) {
				if (!isUnderCombat()) {
					if (barPercentage > 0) {
						barPercentage--;
						sendDefenceBar();
					}
				} else {
					if (barPercentage < 100) {
						barPercentage++;
						sendDefenceBar();
					}
				}
			}
		}
	}

	@Override
	public void processHit(Hit hit) {
		int damage = hit.getDamage();
		HitLook look = hit.getLook();
		if (damage > 0) {
			if (look == HitLook.MELEE_DAMAGE || look == HitLook.RANGE_DAMAGE
					|| look == HitLook.MAGIC_DAMAGE) {
				double multiplier = (100D - (barPercentage)) / 100D;
				hit.setDamage((int) (damage * multiplier));
			}
		}
		super.processHit(hit);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		puddles.clear();
		sendDefenceBar();
	}

	private void sendDefenceBar() {
		if (isDead())
			getManager().hideBar(getReference());
		else
			getManager().showBar(getReference(), "Demon's Defence",
					barPercentage);
	}

	private static class PoisionPuddle {
		final WorldTile tile;
		int cycles;

		public PoisionPuddle(WorldTile tile, int barPercentage) {
			this.tile = tile;
		}

		public boolean canDestroyPoision() {
			return cycles == 15;
		}
	}

	public void addPoisionBubble(WorldTile tile) {
		puddles.add(new PoisionPuddle(tile, barPercentage));
		World.sendGraphics(this, new Graphics(2588), tile);
	}

	public List<PoisionPuddle> getPoisionPuddles() {
		return puddles;
	}
}