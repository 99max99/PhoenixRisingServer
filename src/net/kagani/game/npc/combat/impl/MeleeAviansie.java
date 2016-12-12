package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;

public class MeleeAviansie extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6232, 6233, 6235, 6237, 6240, 6243, 6244 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(npc, 1, target,
				getMeleeHit(npc, getMaxHit(npc, npc.getAttackStyle(), target)));
		return npc.getAttackSpeed();
	}
}
