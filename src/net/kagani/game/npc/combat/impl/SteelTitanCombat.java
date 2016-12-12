package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.utils.Utils;

public class SteelTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7344, 7343 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		boolean isDistanced = !Utils.isOnRange(npc, target, 0);
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(8190));
			target.setNextGraphics(new Graphics(1449));
			if (isDistanced) {// range hit
				delayHit(
						npc,
						2,
						target,
						getRangeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE,
										target)),
						getRangeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE,
										target)),
						getRangeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE,
										target)),
						getRangeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE,
										target)));
			} else {// melee hit
				delayHit(
						npc,
						1,
						target,
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)),
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)),
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)),
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)));
			}
		} else {
			int style = Utils.random(isDistanced ? 1 : 0, 3);
			switch (style) {
			case 0:// MELEE
				npc.setNextAnimation(new Animation(8183));
				delayHit(
						npc,
						0,
						target,
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)));
				break;
			case 1:// RANGE
				npc.setNextAnimation(new Animation(8190));
				Projectile projectile = World.sendProjectileNew(npc, target,
						1445, 34, 16, 35, 2, 10, 0);
				delayHit(
						npc,
						Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
						target,
						getRangeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE,
										target)));
				break;
			case 2:// MAGE
				npc.setNextAnimation(new Animation(7694));
				projectile = World.sendProjectileNew(npc, target, 1451, 34, 16,
						35, 2, 10, 0);
				delayHit(
						npc,
						Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
						target,
						getMagicHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MAGE,
										target)));
				break;
			}
		}
		return npc.getAttackSpeed();
	}
}
