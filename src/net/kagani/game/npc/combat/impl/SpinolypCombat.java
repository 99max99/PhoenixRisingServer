package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class SpinolypCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Spinolyp" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		switch (Utils.random(2)) {
		case 0:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			Projectile projectile = World.sendProjectileNew(npc, target, 2705,
					34, 16, 35, 2, 10, 0);
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getMagicHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
			break;
		case 1:
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			projectile = World.sendProjectileNew(npc, target, 473, 34, 16, 35,
					2, 10, 0);
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getRangeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.RANGE, target)));
			break;
		}
		if (Utils.random(10) == 0)
			EffectsManager.makePoisoned(target, 68);
		return npc.getAttackSpeed();
	}
}
