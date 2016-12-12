package net.kagani.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.dungeonnering.FamishedEye;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class FamishedEyeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 12436, 12451, 12466 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final FamishedEye eye = (FamishedEye) npc;

		if (eye.isInactive())
			return 0;
		else if (!eye.isFirstHit()) {
			eye.setFirstHit(true);
			return Utils.random(5, 15);
		}

		npc.setNextAnimation(new Animation(14916));
		WorldTasksManager.schedule(new WorldTask() {

			private List<WorldTile> tiles;
			private WorldTile targetTile;

			int cycles;

			@Override
			public void run() {
				cycles++;
				if (cycles == 1) {
					tiles = new LinkedList<WorldTile>();
					targetTile = new WorldTile(target);
					World.sendProjectile(eye, targetTile, 2849, 35, 30, 41, 0,
							15, 0);
				} else if (cycles == 2) {
					for (int x = -1; x < 2; x++) {
						for (int y = -1; y < 2; y++) {
							WorldTile attackedTile = targetTile.transform(x, y,
									0);
							if (x != y)
								World.sendProjectile(eye, targetTile,
										attackedTile, 2851, 35, 0, 26, 40, 16,
										0);
							tiles.add(attackedTile);
						}
					}
				} else if (cycles == 3) {
					for (WorldTile tile : tiles) {
						if (!tile.matches(targetTile))
							World.sendGraphics(eye, new Graphics(2852, 35, 5),
									tile);
						for (Entity t : eye.getPossibleTargets()) {
							if (t.matches(tile))
								t.applyHit(new Hit(eye,
										(int) Utils.random(
												eye.getMaxHit() * .25,
												eye.getMaxHit()),
										HitLook.REGULAR_DAMAGE));
						}
					}
					tiles.clear();
					stop();
					return;
				}
			}
		}, 0, 0);
		return Utils.random(5, 35);
	}
}
