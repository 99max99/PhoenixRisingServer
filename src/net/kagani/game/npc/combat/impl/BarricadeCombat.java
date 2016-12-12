package net.kagani.game.npc.combat.impl;

import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;

public class BarricadeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Barricade" };
	}

	/**
	 * empty
	 */
	@Override
	public int attack(NPC npc, Entity target) {
		return 0;
	}
}
