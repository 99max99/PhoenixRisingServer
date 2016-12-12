package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;

public class ClayFamiliarCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8241, 8243, 8245, 8247, 8249 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (target instanceof Player) {
			Player player = (Player) target;
			if (player.getAppearence().isNPC()) {
				npc.getCombat().removeTarget();
				return npc.getAttackSpeed();
			}
		}
		delayHit(npc, 0, target,
				getMeleeHit(npc, getMaxHit(npc, npc.getAttackStyle(), target)));
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return npc.getAttackSpeed();
	}
}
