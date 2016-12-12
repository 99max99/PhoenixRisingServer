package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class LavaTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7342, 7341 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(7883));
			npc.setNextGraphics(new Graphics(1491));
			delayHit(
					npc,
					1,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
			if (damage <= 4 && target instanceof Player) {
				Player player = (Player) target;
				player.getCombatDefinitions().desecreaseSpecialAttack(
						(player.getCombatDefinitions()
								.getSpecialAttackPercentage() / 10));
			}
		} else {
			damage = (int) (getMaxHit(npc, NPCCombatDefinitions.MELEE, target) * 0.85);
			npc.setNextAnimation(new Animation(7980));
			npc.setNextGraphics(new Graphics(1490));
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		if (Utils.random(10) == 0)// 1/10 chance of happening
			delayHit(npc, 1, target, getMeleeHit(npc, Utils.random(500)));
		return npc.getAttackSpeed();
	}
}
