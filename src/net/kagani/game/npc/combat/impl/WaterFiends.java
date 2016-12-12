package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class WaterFiends extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Waterfiend" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));

		Projectile projectile = World.sendProjectileNew(npc, target,
				Utils.random(2) == 0 ? 16 : defs.getAttackProjectile(), 20, 30,
				25, 2, 16, 5);
		delayHit(
				npc,
				Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
				target,
				getMagicHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
		return npc.getAttackSpeed();
	}

}
