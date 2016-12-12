package net.kagani.game.npc.combat.impl;

import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.utils.Utils;

public class SwarmCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 411 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		delayHit(npc, 0, target, getRegularHit(npc, Utils.random(100)));
		return 6;
	}
}
