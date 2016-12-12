package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.familiar.Familiar;

public class BloodragerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 11106, 11108, 11110, 11112, 11114, 11116, 11118,
				11120, 11122, 11124, 11126 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int tier = (npc.getId() - 11106) / 2;

		int damage = 0;
		if (usingSpecial) {
			npc.setNextGraphics(new Graphics(2444));
			damage = getMaxHit(
					npc,
					(int) (npc.getMaxHit(NPCCombatDefinitions.MELEE) * (1.05 * tier)),
					NPCCombatDefinitions.MELEE, target);
		} else
			damage = getMaxHit(npc, NPCCombatDefinitions.MELEE, target);
		delayHit(npc, usingSpecial ? 1 : 0, target, getMeleeHit(npc, damage));
		npc.setNextAnimation(new Animation(13617));
		return npc.getAttackSpeed();
	}
}
