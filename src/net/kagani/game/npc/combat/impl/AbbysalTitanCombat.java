package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;

public class AbbysalTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7350, 7349 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		int damage = 0;
		damage = getMaxHit(npc, NPCCombatDefinitions.MELEE, target);
		npc.setNextAnimation(new Animation(7980));
		npc.setNextGraphics(new Graphics(1490));

		if (target instanceof Player) { // cjay failed dragonkk saved the day
			Player player = (Player) target;
			if (damage > 0 && player.getPrayer().getPrayerpoints() > 0)
				player.getPrayer().drainPrayer(damage / 2);
		}
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
