package net.kagani.game.player.controllers.trollinvasion;

import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;

public class TrollFather extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Troll father" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int hit = 0;
		hit = getMaxHit(npc, 250, NPCCombatDefinitions.MELEE, target);
		delayHit(npc, 2, target, getMeleeHit(npc, hit));
		return defs.getAttackGfx();
	}

}
