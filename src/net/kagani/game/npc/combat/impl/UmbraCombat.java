package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.minigames.ZarosGodwars;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class UmbraCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13452 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		npc.setNextGraphics(new Graphics(3357));
		for (final Entity t : ZarosGodwars.nex.getPossibleTargets()) {
			World.sendProjectile(npc, t, 380, 20, 20, 20, 1, 0, 0);
			int damage = getMaxHit(npc, NPCCombatDefinitions.MAGE, t);
			delayHit(npc, 1, t, getMagicHit(npc, damage));
			if (damage > 0 && Utils.random(5) == 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (t instanceof Player)
							((Player) t).getSkills().drainLevel(
									Skills.ATTACK,
									(int) (((Player) t).getSkills().getLevel(
											Skills.ATTACK) * 0.1));
						t.setNextGraphics(new Graphics(381));
					}
				}, 2);
			}
		}
		return npc.getAttackSpeed();
	}
}