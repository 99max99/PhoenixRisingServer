package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Utils;

public class KarilCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 2028 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		int damage = getMaxHit(npc, NPCCombatDefinitions.RANGE, target);
		if (damage != 0 && target instanceof Player && Utils.random(3) == 0) {
			target.setNextGraphics(new Graphics(401, 0, 100));
			Player targetPlayer = (Player) target;
			int drain = (int) (targetPlayer.getSkills().getLevelForXp(
					Skills.AGILITY) * 0.2);
			int currentLevel = targetPlayer.getSkills()
					.getLevel(Skills.AGILITY);
			targetPlayer.getSkills().set(Skills.AGILITY,
					currentLevel < drain ? 0 : currentLevel - drain);
		}
		Projectile projectile = World.sendProjectileNew(npc, target,
				defs.getAttackProjectile(), 41, 16, 35, 3, Utils.random(5), 5);
		delayHit(npc,
				Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
				target, getRangeHit(npc, damage));
		return npc.getAttackSpeed();
	}
}
