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

public class CaveHorrerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Cave horror" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		if (!Slayer.hasWitchWoodIcon(target)) {
			Player targetPlayer = (Player) target;
			int randomSkill = Utils.random(0, 6);
			int currentLevel = targetPlayer.getSkills().getLevel(randomSkill);
			targetPlayer.getSkills().set(randomSkill,
					currentLevel < 5 ? 0 : currentLevel - 5);
			targetPlayer
					.getPackets()
					.sendGameMessage(
							"The screams of the cave horrer make you feel slightly weaker.");
			npc.setNextForceTalk(new ForceTalk("*OOOoooAHHHH*"));
			delayHit(npc, 0, target,
					getMeleeHit(npc, targetPlayer.getMaxHitpoints() / 3));
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
