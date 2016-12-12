package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.minigames.ZarosGodwars;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class GlaciesCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13454 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		npc.setNextGraphics(new Graphics(3356));
		for (final Entity t : ZarosGodwars.nex.getPossibleTargets()) {
			World.sendProjectile(npc, t, 362, 20, 20, 20, 1, 10, 0);
			int damage = getMaxHit(npc, NPCCombatDefinitions.MAGE, t);
			delayHit(npc, 1, t, getMagicHit(npc, damage));
			if (damage > 0 && Utils.random(5) == 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						t.setBoundDelay(25);
						t.setNextGraphics(new Graphics(369));
					}
				}, 2);
			}
		}
		return npc.getAttackSpeed();
	}
}