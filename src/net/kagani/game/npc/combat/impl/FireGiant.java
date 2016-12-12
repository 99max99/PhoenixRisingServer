package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class FireGiant extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Fire giant", "Moss giant" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		boolean isDistance = !Utils.isOnRange(npc, target, 0);
		int style = Utils.random(isDistance ? 1 : 0, 2);
		switch (style) {
		case 0:// MELEE
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
			break;
		case 1:// RANGED
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			Projectile projectile = World.sendProjectileNew(npc, target, 276,
					40, 30, 30, 3, 0, 5);
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getRangeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.RANGE, target)));
			break;
		}
		return npc.getAttackSpeed();
	}

}
