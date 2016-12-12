package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class JadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 2745, 15208 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		int attackStyle = Utils.random(
				!Utils.isOnRange(npc, target, 0) ? 1 : 0, 3);
		if (attackStyle == 0) { // melee
			npc.setNextAnimation(new Animation(npc.getCombatDefinitions()
					.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getMaxHit(npc, 8500, NPCCombatDefinitions.MELEE,
									target)));
			return npc.getAttackSpeed();
		}
		if (attackStyle == 1) { // range
			npc.setNextAnimation(new Animation(16202));
			npc.setNextGraphics(new Graphics(2994));
			target.setNextGraphics(new Graphics(3000, 100, 0));
			delayHit(
					npc,
					3,
					target,
					getRangeHit(
							npc,
							getMaxHit(npc, 8500, NPCCombatDefinitions.RANGE,
									target)));
		} else {
			npc.setNextAnimation(new Animation(16195));
			npc.setNextGraphics(new Graphics(2995));
			Projectile projectile = World.sendProjectileNew(npc, target, 2996,
					30, 30, 120, 2, 20, 5);// TODO
											// check
											// this
											// out.
			target.setNextGraphics(new Graphics(2741, projectile.getEndTime(),
					100));
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getMagicHit(
							npc,
							getMaxHit(npc, 8500, NPCCombatDefinitions.MAGE,
									target)));
		}
		return npc.getAttackSpeed() + 2;
	}

}
