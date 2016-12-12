package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class SpiritualGaurdianCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 10700 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(Utils.random(2) == 0 ? 13036 : 13035));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		return npc.getAttackSpeed();
	}
}
