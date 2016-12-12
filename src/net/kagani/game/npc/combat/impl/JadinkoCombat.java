package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class JadinkoCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13820, 13821, 13822 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		boolean isDistanced = !Utils.isOnRange(npc, target, 0);
		if (target instanceof Player
				&& ((Player) target).getPrayer().isMeleeProtecting())
			isDistanced = true;
		if (isDistanced)
			magicAttack(npc, target);
		else
			meleeAttack(npc, target);
		return npc.getAttackSpeed();
	}

	private void magicAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(npc.getId() == 13820 ? 3031 : 3215));
		delayHit(
				npc,
				2,
				target,
				getMagicHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
	}

	private void meleeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(npc.getId() == 13820 ? 3009 : 3214));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
	}
}