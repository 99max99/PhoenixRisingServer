package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class AnimatedBookCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 10744 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		boolean meleeAttack = Utils.random(2) == 0;
		if (meleeAttack) { // melee
			if (!Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(),
					target.getX(), target.getY(), target.getSize(), 0)) {
				magicAttack(npc, target);
				return npc.getAttackSpeed();
			} else {
				meleeAttack(npc, target);
				return npc.getAttackSpeed();
			}
		} else {
			magicAttack(npc, target);
			return npc.getAttackSpeed();
		}
	}

	private void meleeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(13479));
		delayHit(
				npc,
				0,
				target,
				getMeleeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
	}

	private void magicAttack(NPC npc, final Entity target) {
		npc.setNextAnimation(new Animation(13480));
		npc.setNextGraphics(new Graphics(2728));
		delayHit(
				npc,
				1,
				target,
				getMagicHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.MAGE, target)));
		World.sendProjectile(npc, target, 2731, 34, 16, 30, 35, 16, 0);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.setNextGraphics(new Graphics(2738, 0, 80));
			}
		}, 2);
	}
}
