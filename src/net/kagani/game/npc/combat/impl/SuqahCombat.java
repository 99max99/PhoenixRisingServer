package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class SuqahCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Suqah" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(3) == 0) {// barrage
			boolean hit = Utils.random(2) == 0;
			delayHit(npc, 2, target, getMagicHit(npc, hit ? 100 : 0));
			if (hit) {
				target.setNextGraphics(new Graphics(369, 40, 0));
				target.setBoundDelay(8, true);
			}
		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		}
		return npc.getAttackSpeed();
	}
}
