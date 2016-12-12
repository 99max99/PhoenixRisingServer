package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;

public class GorakCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Gorak" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int damage = getMaxHit(npc, NPCCombatDefinitions.MELEE, target);
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, new Hit(npc, damage, HitLook.REGULAR_DAMAGE));
		return npc.getAttackSpeed();
	}
}
