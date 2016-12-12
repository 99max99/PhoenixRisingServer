package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class GuthanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2027 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getMaxHit(npc, NPCCombatDefinitions.MELEE, target);
		if (damage != 0 && Utils.random(3) == 0) {
			target.setNextGraphics(new Graphics(398));
			npc.heal(damage);
		}
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
