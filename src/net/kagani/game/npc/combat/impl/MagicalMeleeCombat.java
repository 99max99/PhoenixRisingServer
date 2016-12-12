package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;

public class MagicalMeleeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Jelly", "Bloodveld" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return npc.getAttackSpeed();
	}
}
