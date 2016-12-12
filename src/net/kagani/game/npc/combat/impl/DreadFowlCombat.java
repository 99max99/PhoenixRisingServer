package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.utils.Utils;

public class DreadFowlCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6825, 6824 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(7810));
			npc.setNextGraphics(new Graphics(1318));
			Projectile projectile = World.sendProjectileNew(npc, target, 1376,
					34, 16, 35, 2, 10, 0);
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getMagicHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
		} else {
			if (Utils.random(10) == 0) {// 1/10 chance of random special
				// (weaker)
				npc.setNextAnimation(new Animation(7810));
				npc.setNextGraphics(new Graphics(1318));
				Projectile projectile = World.sendProjectileNew(npc, target,
						1376, 34, 16, 35, 2, 10, 0);
				delayHit(
						npc,
						Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
						target,
						getMagicHit(
								npc,
								(int) (getMaxHit(npc, 30,
										NPCCombatDefinitions.MAGE, target) * 0.85)));
			} else {
				npc.setNextAnimation(new Animation(7810));
				delayHit(
						npc,
						0,
						target,
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)));
			}
		}
		return npc.getAttackSpeed();
	}
}
