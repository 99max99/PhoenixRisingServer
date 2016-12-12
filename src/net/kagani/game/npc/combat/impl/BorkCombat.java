package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.others.Bork;

public class BorkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bork" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions cdef = npc.getCombatDefinitions();
		Bork bork = (Bork) npc;
		if (npc.getHitpoints() <= (cdef.getHitpoints() * 0.6)
				&& !bork.isSpawnedMinions()) {
			bork.spawnMinions();
			return 0;
		}
		npc.setNextAnimation(new Animation(cdef.getAttackEmote()));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		return npc.getAttackSpeed();
	}
}