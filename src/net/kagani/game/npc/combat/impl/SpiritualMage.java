package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class SpiritualMage extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6257, 6221, 6278 };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getMaxHit(npc, NPCCombatDefinitions.MAGE, target);
		npc.setNextGraphics(new Graphics(defs.getAttackGfx()));
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		if (damage > 0) {
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextGraphics(new Graphics(defs
							.getAttackProjectile()));
				}
			}, 2);
		}
		return npc.getAttackSpeed();
	}
}
