package net.kagani.game.npc.combat.impl;

import java.util.LinkedList;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.godwars.zaros.Nex;
import net.kagani.game.npc.godwars.zaros.Nex.NexPhase;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class NewNexCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] {};
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Nex nex = (Nex) npc;
		nex.setForceFollowClose(Utils.random(2) == 0);
		nex.resetLastAttack();
		boolean isDistanced = !Utils.isOnRange(npc.getX(), npc.getY(),
				npc.getSize(), target.getX(), target.getY(), target.getSize(),
				0);
		if (nex.isSiphioning() || nex.isFlying())
			return 0;
		if (Utils.random(3) == 0 && !isDistanced) {
			sendMeleeAttack(nex, target);
		} else {
			switch (nex.getCurrentPhase().getPhaseValue()) {
			case 1: // Smoke
				if (nex.isFirstStageAttack()) {
					sendVirusAttack(nex);
					nex.setFirstStageAttack(false);
					return nex.getAttackSpeed();
				}
				if (Utils.random(8) == 0) {
					// No escape
				} else if (Utils.random(5) == 0) {
					// Virus attack
					sendVirusAttack(nex);
				} else if (Utils.random(3) == 0) {
					// Drag check
					List<Entity> distanceTargets = new LinkedList<Entity>();
					for (Entity t : nex.getPossibleTargets()) {
						if (!Utils.isOnRange(npc.getX(), npc.getY(),
								npc.getSize(), t.getX(), t.getY(), t.getSize(),
								6)) {
							distanceTargets.add(t);
							break;
						}
					}
					if (distanceTargets.size() == 0) {
						sendMagicAttack(nex);
					} else {
						Entity t = distanceTargets.get(Utils
								.random(distanceTargets.size()));
						sendPullAttack(nex, t);
					}
				} else
					sendMagicAttack(nex);
				break;
			case 2:// Shadow
				break;
			case 3:// Blood
				break;
			case 4:// Ice
				break;
			case 5:// Zaros phase
				break;
			}
		}
		return nex.getAttackSpeed();
	}

	private void sendPullAttack(Nex nex, Entity target) {
		nex.setTarget(target);

	}

	private void sendMeleeAttack(Nex nex, Entity target) {
		nex.setNextAnimation(new Animation(17453));
		Hit hit = getMeleeHit(nex,
				getMaxHit(nex, NPCCombatDefinitions.MELEE, target));
		delayHit(nex, 0, target, hit);
		if (nex.getCurrentPhase() == NexPhase.ZAROS)
			nex.sendSoulSplit(hit, target);
	}

	private void sendMagicAttack(final Nex nex) {
		for (final Entity t : nex.getPossibleTargets()) {
			nex.setNextAnimation(new Animation(17413));
			nex.setNextGraphics(new Graphics(1214));
			Projectile projectile = World.sendProjectileNew(nex, t, 3371, 25,
					25, 50, 2, 0, 0);

			Hit hit = getMagicHit(nex,
					getMaxHit(nex, NPCCombatDefinitions.MAGE, t));
			delayHit(nex,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					t, hit);

			if (hit.getDamage() > 0) {
				if (nex.getCurrentPhase() == NexPhase.ZAROS
						&& nex.getId() == 13448)
					nex.sendSoulSplit(hit, t);
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						t.setNextGraphics(new Graphics(3373));
						if (nex.getCurrentPhase() == NexPhase.SMOKE) {
							if (Utils.random(5) == 0)
								EffectsManager.makePoisoned(t, 360);
						}
					}
				}, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1);
			}
		}
	}

	private void sendVirusAttack(Nex nex) {
		nex.addVirusAttackDelay(45 + Utils.random(15));
		nex.setNextForceTalk(new ForceTalk("Let the virus flow through you!"));
		nex.playSoundEffect(3296);
		nex.setNextAnimation(new Animation(17414));
		nex.setNextGraphics(new Graphics(3375));
		List<Entity> targets = nex.getPossibleTargets();
		Entity target = targets.get(Utils.random(targets.size()));
		nex.setTarget(target);
		for (Entity t : targets) {
			if (!t.withinDistance(target, 3))
				continue;
			t.setNextForceTalk(new ForceTalk("*Cough*"));

			t.getTemporaryAttributtes().put("nex_infected", true);
		}
		nex.playSoundEffect(3296);
	}
}
