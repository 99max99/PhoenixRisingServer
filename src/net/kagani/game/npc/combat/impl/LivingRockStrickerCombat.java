package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class LivingRockStrickerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { 8833 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (!Utils.isOnRange(target, npc, 0)) {
			// TODO add projectile
			npc.setNextAnimation(new Animation(12196));
			delayHit(
					npc,
					1,
					target,
					getRangeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.RANGE, target)));
		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							getMaxHit(npc, 84, NPCCombatDefinitions.MELEE,
									target)));
			return npc.getAttackSpeed();
		}
		return npc.getAttackSpeed();
	}
}
