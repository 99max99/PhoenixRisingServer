package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.DragonfireShield;
import net.kagani.utils.Utils;

public class LeatherDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Green dragon", "Blue dragon", "Red dragon",
				"Black dragon", 742, 14548, 5362, 10770, 10815, 10219 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(),
				target.getX(), target.getY(), target.getSize(), 0)
				&& Utils.random(3) != 0) {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		} else {
			if (Utils.random(5) == 1) {// Dragonfire
				npc.setNextAnimation(new Animation(12259));
				npc.setNextGraphics(new Graphics(1, 0, 100));
				int damage = Utils.random(5000);// getMaxHit(npc,
												// NPCCombatDefinitions.MAGE,
												// target);
				if (/* damage > 200 && */target instanceof Player) {
					Player player = (Player) target;
					// Rest should all be types of dragon-fire.

					boolean hasSuperPot = player.getEffectsManager()
							.hasActiveEffect(EffectType.SUPER_FIRE_IMMUNITY);
					boolean hasRegularPot = player.getEffectsManager()
							.hasActiveEffect(EffectType.FIRE_IMMUNITY);
					boolean hasShield = Combat.hasAntiDragProtection(target);
					if (hasSuperPot) {
						damage = 0;
						player.getPackets()
								.sendGameMessage(
										"Your potion fully protects you from the dragon's fiery breath.");
					} else {
						if (hasRegularPot)
							damage *= 0.5;
						if (hasShield) {
							player.getPackets()
									.sendGameMessage(
											"Your shield absorbs some of the dragon's fiery breath!");
							damage = hasRegularPot ? 0 : damage / 2;
						}
					}
					DragonfireShield.chargeDFS(player, false);
				}
				delayHit(npc, 1, target, getMagicHit(npc, damage));
			} else {// Magical attack
				npc.setNextAnimation(new Animation(12259));
				Projectile projectile = World.sendProjectileNew(npc, target,
						2731, 28, 16, 35, 4, 0, 0);
				delayHit(
						npc,
						Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
						target,
						getMagicHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MAGE,
										target)));
				target.setNextGraphics(new Graphics(2738, projectile
						.getEndTime(), 80));
			}
		}
		return npc.getAttackSpeed();
	}
}