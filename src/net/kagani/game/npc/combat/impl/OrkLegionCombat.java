package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class OrkLegionCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Ork legion" };
	}

	public String[] messages = { "For Bork!", "Die Human!", "To the attack!",
			"All together now!" };

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		if (Utils.random(3) == 0)
			npc.setNextForceTalk(new ForceTalk(messages[Utils
					.random(messages.length > 3 ? 3 : 0)]));
		delayHit(npc, 0, target, getMeleeHit(npc, defs.getMaxHit()));
		return defs.getAttackGfx();
	}

}