package net.kagani.game.npc.combat.impl.dung;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.utils.Utils;

public class PlaneFreezerLakhrahnazCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Plane-freezer Lakhrahnaz" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(8) == 0) {
			npc.resetWalkSteps();
			npc.addWalkSteps(npc.getX() + Utils.random(3) - 2, npc.getY()
					+ Utils.random(3) - 2);
		}
		if (Utils.random(3) == 0) {
			int attackStyle = Utils.random(2);
			if (attackStyle == 1
					&& !Utils.isOnRange(target.getX(), target.getY(),
							target.getSize(), npc.getX(), npc.getY(),
							npc.getSize(), 0))
				attackStyle = 0;
			switch (attackStyle) {
			case 0:
				npc.setNextAnimation(new Animation(13775));
				for (Entity t : npc.getPossibleTargets()) {
					World.sendProjectile(npc, t, 2577, 16, 16, 41, 30, 0, 0);
					t.setNextGraphics(new Graphics(2578, 70, 0));
					delayHit(
							npc,
							1,
							t,
							getMagicHit(
									npc,
									getMaxHit(npc, NPCCombatDefinitions.MAGE,
											target)));
				}
				break;
			case 1:
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				/**
				 * Bugged
				 */
				// int dir = Utils.random(Utils.DIRECTION_DELTA_X.length);
				// target.addWalkSteps(target.getX() +
				// Utils.DIRECTION_DELTA_X[dir], target.getY() +
				// Utils.DIRECTION_DELTA_Y[dir], 1);
				delayHit(
						npc,
						0,
						target,
						getMeleeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MELEE,
										target)));
				break;
			}
			return npc.getAttackSpeed();
		}
		npc.setNextAnimation(new Animation(13775));
		npc.setNextGraphics(new Graphics(2574));
		World.sendProjectile(npc, target, 2595, 16, 16, 41, 30, 0, 0);
		target.setNextGraphics(new Graphics(2576, 70, 0));
		delayHit(
				npc,
				1,
				target,
				getRangeHit(npc,
						getMaxHit(npc, NPCCombatDefinitions.RANGE, target)));
		return npc.getAttackSpeed();
	}
}
