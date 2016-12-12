package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
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

public class FumusCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13451 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		npc.setNextGraphics(new Graphics(3355));
		for (final Entity t : ZarosGodwars.nex.getPossibleTargets()) {
			World.sendProjectile(npc, t, 386, 41, 20, 20, 1, 25, 10);
			int damage = getMaxHit(npc, NPCCombatDefinitions.MAGE, t);
			delayHit(npc, 1, t, getMagicHit(npc, damage));
			if (damage > 0 && Utils.random(5) == 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						EffectsManager.makePoisoned(t, 80);
						t.setNextGraphics(new Graphics(388));
					}
				}, 2);

			}
		}
		return npc.getAttackSpeed();
	}
}