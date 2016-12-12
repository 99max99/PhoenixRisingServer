package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class KreearraCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6222 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (!npc.isUnderCombat()) {
			npc.setNextAnimation(new Animation(17396));
			delayHit(
					npc,
					1,
					target,
					getMeleeHit(
							npc,
							getMaxHit(npc, 260, NPCCombatDefinitions.MELEE,
									target)));
			return npc.getAttackSpeed();
		}
		npc.setNextAnimation(new Animation(17397));
		for (final Entity t : npc.getPossibleTargets()) {
			if (Utils.random(2) == 0) {
				delayHit(
						npc,
						1,
						t,
						getMagicHit(
								npc,
								getMaxHit(npc, 210, NPCCombatDefinitions.MAGE,
										t)));
				World.sendProjectile(npc, t, 3388, 65, 41, 65, 35, 0, 5);
			} else {
				delayHit(
						npc,
						1,
						t,
						getRangeHit(
								npc,
								getMaxHit(npc, 720, NPCCombatDefinitions.RANGE,
										t)));
				World.sendProjectile(npc, t, 3349, 65, 41, 65, 35, 0, 5);
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						for (int c = 0; c < 10; c++) {
							int dir = Utils
									.random(Utils.DIRECTION_DELTA_X.length);
							if (World.checkWalkStep(t.getPlane(), t.getX(),
									t.getY(), dir, 1)) {
								t.resetWalkSteps();
								t.setNextWorldTile(new WorldTile(t.getX()
										+ Utils.DIRECTION_DELTA_X[dir], t
										.getY() + Utils.DIRECTION_DELTA_Y[dir],
										t.getPlane()));
								break;
							}
						}
					}
				}, 1);
			}
		}
		return npc.getAttackSpeed();
	}
}
