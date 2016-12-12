package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Slayer;
import net.kagani.utils.Utils;

public class BansheeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Banshee", "Mighty banshee" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (!Slayer.hasEarmuffs(target)) {
			Player targetPlayer = (Player) target;
			if (!targetPlayer.getPrayer().isMeleeProtecting()) {
				int randomSkill = Utils.random(0, 6);
				int currentLevel = targetPlayer.getSkills().getLevel(
						randomSkill);
				targetPlayer.getSkills().set(randomSkill,
						currentLevel < 5 ? 0 : currentLevel - 5);
				targetPlayer
						.getPackets()
						.sendGameMessage(
								"The screams of the banshee make you feel slightly weaker.");
				npc.setNextForceTalk(new ForceTalk("*EEEEHHHAHHH*"));
			}
			delayHit(npc, 0, target,
					getMeleeHit(npc, targetPlayer.getMaxHitpoints() / 10));
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
