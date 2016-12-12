package net.kagani.game.npc.dungeonnering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class Dreadnaut extends DungeonBoss {

	private List<GassPuddle> puddles;

	private int ticks;
	private boolean reduceMagicLevel;

	public Dreadnaut(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
		setForceFollowClose(true);
		setRun(true);
		setLureDelay(6000);// 6 seconds
		puddles = new CopyOnWriteArrayList<>();
	}

	@Override
	public void processNPC() {
		if (puddles == null) // still loading
			return;
		super.processNPC();
		if (!reduceMagicLevel) {
			if (isUnderCombat()) {
				for (Entity t : getPossibleTargets()) {
					if (!t.withinDistance(this, 1)) {
						ticks++;
						break;
					}
				}
			}
			if (ticks == 25) {
				reduceMagicLevel = true;
				setNextForceTalk(new ForceTalk(
						"You cannot run from me forever!"));
			}
		}

		for (GassPuddle puddle : puddles) {
			puddle.cycles++;
			if (puddle.canDestroyPuddle()) {
				puddles.remove(puddle);
				continue;
			} else if (puddle.cycles % 2 != 0)
				continue;
			if (puddle.cycles % 2 == 0)
				puddle.refreshGraphics();
			List<Entity> targets = getPossibleTargets(true, true);
			for (Entity t : targets) {
				if (!t.matches(puddle.tile))
					continue;
				t.applyHit(new Hit(this,
						Utils.random((int) (t.getHitpoints() * 0.25)) + 1,
						HitLook.REGULAR_DAMAGE));
			}
		}
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.60;
	}

	public boolean canReduceMagicLevel() {
		return reduceMagicLevel;
	}

	public void setReduceMagicLevel(boolean reduceMagicLevel) {
		this.reduceMagicLevel = reduceMagicLevel;
	}

	public void addSpot(WorldTile tile) {
		GassPuddle puddle = new GassPuddle(this, tile);
		puddle.refreshGraphics();
		puddles.add(puddle);
	}

	private static class GassPuddle {
		final Dreadnaut boss;
		final WorldTile tile;
		int cycles;

		public GassPuddle(Dreadnaut boss, WorldTile tile) {
			this.tile = tile;
			this.boss = boss;
		}

		public void refreshGraphics() {
			World.sendGraphics(boss, new Graphics(2859, 0, 10), tile);
		}

		public boolean canDestroyPuddle() {
			return cycles == 50;
		}
	}
}
