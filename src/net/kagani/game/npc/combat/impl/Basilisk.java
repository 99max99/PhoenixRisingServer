package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Slayer;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Basilisk extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Basilisk" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (!Slayer.hasReflectiveEquipment(target)) {
			Player targetPlayer = (Player) target;
			int randomSkill = Utils.random(0, 6);
			int currentLevel = targetPlayer.getSkills().getLevel(randomSkill);
			targetPlayer.getSkills().set(randomSkill, currentLevel / 4);
			delayHit(npc, 0, target,
					getMeleeHit(npc, targetPlayer.getMaxHitpoints() / 10));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					target.setNextGraphics(new Graphics(747));
				}
			});
			// TODO player emote hands on ears
		} else
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, npc.getAttackStyle(), target)));
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return npc.getAttackSpeed();
	}
}
