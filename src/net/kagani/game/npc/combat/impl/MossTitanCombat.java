package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.utils.Utils;

public class MossTitanCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 7330, 7329 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Familiar familiar = (Familiar) npc;
		boolean usingSpecial = familiar.hasSpecialOn();
		int damage = 0;
		if (usingSpecial) {// priority over regular attack
			npc.setNextAnimation(new Animation(8223));
			npc.setNextGraphics(new Graphics(1460));
			for (Entity targets : npc.getPossibleTargets()) {
				sendSpecialAttack(targets, npc);
			}
			sendSpecialAttack(target, npc);
		} else {
			damage = getMaxHit(npc, NPCCombatDefinitions.MELEE, target);
			npc.setNextAnimation(new Animation(8222));
			delayHit(npc, 1, target, getMeleeHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}

	public void sendSpecialAttack(Entity target, NPC npc) {
		Projectile projectile = World.sendProjectileNew(npc, target, 1462, 34,
				16, 35, 2, 10, 0);
		delayHit(
				npc,
				Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
				target,
				getMagicHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
		if (Utils.random(3) == 0)// 1/3 chance of being poisioned
			EffectsManager.makePoisoned(target, 58);
	}
}
