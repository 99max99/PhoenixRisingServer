package net.kagani.game.player.controllers.trollinvasion;

import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;

public class Summoner extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Summoner" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int hit = 0;
		hit = getMaxHit(npc, 250, NPCCombatDefinitions.MAGE, target);

		delayHit(npc, 2, target, getRangeHit(npc, hit));

		return defs.getAttackGfx();
	}

}
