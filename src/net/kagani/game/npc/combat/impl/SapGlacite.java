package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;

public class SapGlacite extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14303 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (target instanceof Player)
			((Player) target).getPrayer().drainPrayer(25);
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target,
				getMeleeHit(npc, getMaxHit(npc, npc.getAttackStyle(), target)));
		return npc.getAttackSpeed();
	}
}
