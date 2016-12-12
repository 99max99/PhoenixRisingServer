package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;

public class JellyCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Jelly" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(npc.getCombatDefinitions()
				.getAttackEmote()));
		delayHit(
				npc,
				2,
				target,
				getRangeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		return npc.getAttackSpeed();
	}
}
