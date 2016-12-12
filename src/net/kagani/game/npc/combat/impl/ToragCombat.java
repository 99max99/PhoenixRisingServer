package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class ToragCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2029 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getMaxHit(npc, NPCCombatDefinitions.MELEE, target);
		if (damage != 0 && target instanceof Player && Utils.random(3) == 0) {
			target.setNextGraphics(new Graphics(399));
			Player targetPlayer = (Player) target;
			targetPlayer
					.setRunEnergy(targetPlayer.getRunEnergy() > 4 ? targetPlayer
							.getRunEnergy() - 4 : 0);
		}
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
