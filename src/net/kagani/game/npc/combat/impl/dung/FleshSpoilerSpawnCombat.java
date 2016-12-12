package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class FleshSpoilerSpawnCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 11910 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(Utils.random(3) == 0 ? 14474 : 14475));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		return 3;
	}
}
