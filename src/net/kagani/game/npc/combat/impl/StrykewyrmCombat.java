package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class StrykewyrmCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 9463, 9465, 9467 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(5) == 0) {// Digging
			final WorldTile tile = new WorldTile(target);
			tile.moveLocation(-1, -1, 0);
			npc.setNextAnimation(new Animation(12796));
			npc.setCantInteract(true);

			WorldTasksManager.schedule(new WorldTask() {
				int ticks;

				@Override
				public void run() {
					ticks++;

					if (ticks == 1) {
						npc.setNextNPCTransformation(npc.getId() - 1);
						npc.setForceWalk(tile);
					} else if (ticks == 0) {
						npc.setCantInteract(false);
						npc.setTarget(target);
						stop();
						return;
					} else if (!npc.hasForceWalk()) {
						npc.setNextNPCTransformation(npc.getId() + 1);
						npc.setNextAnimation(new Animation(12795));
						if (Utils.colides(target, npc)) {
							delayHit(npc, 0, target,
									new Hit(npc, Utils.random(500, 2000),
											HitLook.REGULAR_DAMAGE));
							if (npc.getId() == 9467)
								EffectsManager.makePoisoned(target, 88);
							else if (npc.getId() == 9465) {
								delayHit(npc, 0, target,
										new Hit(npc, Utils.random(500, 2000),
												HitLook.REGULAR_DAMAGE));
								target.setNextGraphics(new Graphics(2311));
							}
						}
						ticks = -1;
					}
				}
			}, 1, 1);
		} else if (Utils.random(3) == 0 || !Utils.isOnRange(npc, target, 0)) {// Magical
																				// attack
			npc.setNextAnimation(new Animation(12794));
			final Hit hit = getMagicHit(npc,
					getMaxHit(npc, NPCCombatDefinitions.MAGE, target));
			Projectile projectile = World.sendProjectileNew(npc, target,
					defs.getAttackProjectile(), 41, 16, 30, 2, 16, 0);
			int endTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
			delayHit(npc, endTime, target, hit);
			if (npc.getId() == 9463) {
				if (Utils.random(5) == 0) {
					target.setBoundDelay(5, true);
					target.setNextGraphics(new Graphics(369, projectile
							.getEndTime(), 0));
				} else
					target.setNextGraphics(new Graphics(2315, projectile
							.getEndTime(), 0));
			} else if (npc.getId() == 9467) {
				target.setNextGraphics(new Graphics(2313, projectile
						.getEndTime(), 0));
				if (Utils.random(3) == 0)
					EffectsManager.makePoisoned(target, 88);
			}
		} else {// Melee Attack
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			if (npc.getId() == 9467 && Utils.random(5) == 0) {
				target.setNextGraphics(new Graphics(2309));
				EffectsManager.makePoisoned(target, 44);
			}
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		}
		return npc.getAttackSpeed();
	}
}
