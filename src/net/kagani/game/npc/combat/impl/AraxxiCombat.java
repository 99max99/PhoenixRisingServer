package net.kagani.game.npc.combat.impl;

import java.util.ArrayList;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.araxxi.Araxxi;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class AraxxiCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 19464 };
	}

	private void araxxiSweep(ArrayList<Entity> possibleTargets,
			final Araxxi araxxi) {
		araxxi.addFreezeDelay(10);
		araxxi.setLocked(true);
		if (possibleTargets.size() == 0)
			return;
		araxxi.setNextAnimation(new Animation(24096));
		araxxi.setNextGraphics(new Graphics(4986));
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				for (Entity t : araxxi.getPossibleTargets()) {
					if (t instanceof Player) {
						Player player = (Player) t;
						if (player.withinDistance(araxxi, 5)) {
							delayHit(
									araxxi,
									0,
									t,
									new Hit(araxxi, (int) (Utils.random(2000,
											3250) * araxxi.damageMulti),
											HitLook.REGULAR_DAMAGE));

						}
					}
				}
			}
		}, 2);
	}

	public void WebShield(NPC npc, Player victim) {
		// npc.addFreezeDelay(15);
		npc.setLocked(true);
		npc.setTarget(null);
		victim.araxxorHeal = true;
		npc.setNextAnimation(new Animation(24075));
		npc.setNextGraphics(new Graphics(4987));
		WorldTasksManager.schedule(new WorldTask() {

			private int time = 0;

			@Override
			public void run() {
				if (time % 2 == 0) {
					Hit healHit = new Hit(npc,
							(int) (200 * araxxi.healingMulti),
							HitLook.HEALED_DAMAGE);
					healHit.setHealHit();
					npc.applyHit(healHit);
					npc.heal((int) (200 * araxxi.healingMulti));
				}
				if (time == 7) {
					victim.araxxorHeal = false;
					npc.setLocked(false);
					stop();
				}
				time++;
			}
		}, 3, 0);
	}

	Araxxi araxxi;

	@Override
	public int attack(NPC npc, Entity target) {
		final ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
		@SuppressWarnings("unused")
		Entity targets[] = possibleTargets.toArray(new Entity[possibleTargets
				.size()]);
		araxxi = (Araxxi) npc;
		araxxi.attackNumber++;
		if (araxxi.attackNumber >= 10) {
			araxxi.canSpecial = true;
		}
		int size = npc.getSize();

		if (Utils.random(5) == 0) {
			Araxxi araxxi = (Araxxi) npc;
			araxxi.spawnSpiders();
		}

		if (Utils.random(5) == 0 && araxxi.canSpecial) {// sweeping attack
			if (npc.getPossibleTargets().size() != 0) {
				araxxi.setForceFollowClose(false);
				araxxiSweep(npc.getPossibleTargets(), araxxi);
				final NPC npcTemp = npc;
				final Player currentTarget = (Player) target;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						npcTemp.setLocked(false);
						npcTemp.setTarget(currentTarget);
					}
				}, 7);
			}
			araxxi.attackNumber = 0;
			araxxi.canSpecial = false;
		} else if (Utils.random(5) == 0 && araxxi.canSpecial) {
			araxxi.setForceFollowClose(false);
			WebShield(npc, (Player) target);
			araxxi.attackNumber = 0;
			araxxi.canSpecial = false;
			return 12;
		} else if (araxxi.canSpecial) {
			araxxi.setForceFollowClose(true);
			araxxi.attackNumber = 0;
			araxxi.canSpecial = false;
		} else {
			int attackStyle = Utils.random(4);
			if (attackStyle == 0 || attackStyle == 1) {// melee
				int distanceX = target.getX() - npc.getX();
				int distanceY = target.getY() - npc.getY();
				if (distanceX > size || distanceX < -1 || distanceY > size
						|| distanceY < -1) {
					int rand = Utils.random(2);
					if (rand == 0)
						attackStyle = 2;
					else
						attackStyle = 3;
				} else {

					delayHit(
							npc,
							0,
							target,
							getMeleeHit(
									npc,
									getMaxHit(npc,
											(int) (7000 * araxxi.damageMulti),
											NPCCombatDefinitions.MELEE, target)));
					npc.setNextAnimation(new Animation(24046));
					return npc.getAttackSpeed();
				}
			}
			if (attackStyle == 2) {
				delayHit(
						npc,
						1,
						target,
						getRangeHit(
								npc,
								getMaxHit(npc,
										(int) (2500 * araxxi.damageMulti),
										NPCCombatDefinitions.RANGE, target)));
				npc.setNextAnimation(new Animation(24095));
				World.sendProjectile(npc, target, 4997, 41, 16, 41, 0, 16, -20);
				return npc.getAttackSpeed();
			}
			if (attackStyle == 3) {
				delayHit(
						npc,
						1,
						target,
						getMagicHit(
								npc,
								getMaxHit(npc,
										(int) (2500 * araxxi.damageMulti),
										NPCCombatDefinitions.MAGE, target)));
				npc.setNextAnimation(new Animation(24095));
				World.sendProjectile(npc, target, 4979, 41, 16, 41, 0, 16, -20);
				return npc.getAttackSpeed();
			}
		}

		return npc.getAttackSpeed();
	}
}
