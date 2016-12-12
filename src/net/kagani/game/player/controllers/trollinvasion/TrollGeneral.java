package net.kagani.game.player.controllers.trollinvasion;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class TrollGeneral extends CombatScript {

	@Override
	public Object[] getKeys() {

		return new Object[] { "Troll general", 12291 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int hit = 0;
		npc.setNextAnimation(new Animation(13788));

		hit = getMaxHit(npc, Utils.random(300, 400),
				NPCCombatDefinitions.MELEE, target);
		delayHit(npc, 1, target, getMeleeHit(npc, hit));
		return defs.getAttackGfx();
	}

}
