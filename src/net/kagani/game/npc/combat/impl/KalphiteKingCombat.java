package net.kagani.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Colour;
import net.kagani.game.Entity;
import net.kagani.game.ForceMovement;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.NewForceMovement;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.kalphite.KalphiteKing;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class KalphiteKingCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 16697, 16698, 16699 };
	}

	private void rangeBasic(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(19450));
		for (Entity e : npc.getPossibleTargets()) {
			Projectile projectile = World.sendProjectile(npc, e, false, true,
					0, 3747, 30, 30, 25, 2, 0, 0);
			int delay = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
			Hit hit = getRangeHit(npc,
					getMaxHit(npc, 1460, NPCCombatDefinitions.RANGE, e));
			hit.setAbilityMark();
			delayHit(npc, delay, e, hit);

		}
	}

	private void rangeFrag(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(19450));
		List<Entity> list = npc.getPossibleTargets();
		Collections.shuffle(list);
		int c = 0;
		for (Entity e : list) {
			if (c < 3) {
				Projectile projectile = World.sendProjectile(npc, e, false,
						true, 0, 3747, 30, 30, 25, 2, 0, 0);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						e.getEffectsManager().startEffect(
								new Effect(EffectType.FRAGMENTATION, 10,
										HitLook.RANGE_DAMAGE,
										new Graphics(3574),
										Utils.random(1280) + 1, 2, npc,
										new WorldTile(e)));
					}
				}, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1);
				c++;
			}
		}
	}

	private void rangeIncendiaryShot(NPC npc, Entity target) {

		npc.setNextAnimation(new Animation(19450));

		List<Entity> list = ((KalphiteKing) npc).getPossibleTargets();
		Collections.shuffle(list);
		int c = 0;
		while (c < 3) {
			for (Entity t : list) {
				Projectile projectile = World.sendProjectile(npc, t, false,
						true, 0, 3747, 30, 30, 25, 2, 0, 0);
				Hit hit = new Hit(npc, 520 + Utils.random(2500),
						HitLook.RANGE_DAMAGE);
				hit.setAbilityMark();
				delayHit(
						npc,
						Utils.projectileTimeToCycles(projectile.getEndTime()) + 2,
						t, hit);
				t.getEffectsManager().startEffect(
						new Effect(EffectType.INCENDIARY_SHOT, 5));

				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						t.setNextGraphics(new Graphics(3522));
					}
				}, 3);

				c++;
			}
		}
	}

	private void green(NPC npc, Entity target) {
		KalphiteKing king = (KalphiteKing) npc;
		king.setForceFollowClose(true);
		king.setNextAnimation(new Animation(19464));
		king.setNextGraphics(new Graphics(3738));
		Projectile projectile = World.sendProjectile(npc, target, 3739, 58, 30,
				25, 75, 10, 0);
		target.setNextGraphics(new Graphics(3740, projectile.getEndTime(), 0));
		if (target instanceof Player) {
			((Player) target).lock(11);
			((Player) target).stopAll();
			((Player) target).setNextAnimation(new Animation(-1)); // to stop
			// abilities
			// emotes
			((Player) target).setNextColour(new Colour(projectile.getEndTime(),
					580 - projectile.getEndTime(), 100, 40, 40, 100));
			((Player) target)
					.getPackets()
					.sendGameMessage(
							"The Kalphite King has imobilised you while preparing for a powerful attack. You are unable to move.");
			((Player) target).getEffectsManager().removeEffect(
					EffectType.DEVOTION);
			((Player) target).getEffectsManager().removeEffect(
					EffectType.BARRICADE);
			((Player) target).getEffectsManager().removeEffect(
					EffectType.IMMORTALITY);
			((Player) target).getEffectsManager().removeEffect(
					EffectType.RESONANCE);
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				king.setForceFollowClose(true);
			}
		}, 8);
	}

	private boolean excecuteGreen(NPC npc, Entity target) {
		if (Utils.isOnRange(npc, target, 2)) {
			npc.setNextAnimation(new Animation(19449));
			target.applyHit(new Hit(npc, 24000, HitLook.MELEE_DAMAGE));
			npc.setForceFollowClose(false);
			((KalphiteKing) npc).nextPhase();
			return true;
		}
		return false;
	}

	private void dig(NPC npc, Entity target) {
		((KalphiteKing) npc).dig(target);
	}

	private void rangeStun(NPC npc, Entity target) {
		for (Entity t : npc.getPossibleTargets()) {
			if (t instanceof Player && Utils.random(10) < 6)
				t.getEffectsManager().startEffect(
						new Effect(EffectType.STUNNED, 8));
		}
	}

	// MELEE incomplete

	private void meleeStomp(NPC npc, Entity target) // Quake
	{
		KalphiteKing king = (KalphiteKing) npc;
		king.setNextAnimation(new Animation(19435));
		king.setNextGraphics(new Graphics(3734));
		for (Entity t : npc.getPossibleTargets()) {
			if (Utils.isOnRange(king, t, 3)) {
				Hit hit = getMeleeHit(npc,
						getMaxHit(npc, 2560, NPCCombatDefinitions.MELEE, t));
				hit.setAbilityMark();
				delayHit(npc, 1, t, hit);
				if (t instanceof Player)
					((Player) t).getSkills().drainLevel(Skills.DEFENCE,
							hit.getDamage() / 200);
			}
		}
	}

	private void meleePush(NPC npc, Entity target) {
		KalphiteKing king = (KalphiteKing) npc;
		king.setTarget(null);
		final byte[] dirs = Utils.getOrthogonalDirection(
				npc.getLastWorldTile(), target.getLastWorldTile());

		if (dirs[0] == 1) // To make it face the correct way
			king.setNextFaceWorldTile(new WorldTile(king.getX() + 10, king
					.getY() + 2, king.getPlane()));
		else if (dirs[0] == -1)
			king.setNextFaceWorldTile(new WorldTile(king.getX() - 10, king
					.getY() + 2, king.getPlane()));
		else if (dirs[1] == 1)
			king.setNextFaceWorldTile(new WorldTile(king.getX() + 2, king
					.getY() + 10, king.getPlane()));
		else if (dirs[1] == -1)
			king.setNextFaceWorldTile(new WorldTile(king.getX() + 2, king
					.getY() - 10, king.getPlane()));

		king.setNextAnimation(new Animation(19449));
		List<Entity> targets = king.getPossibleTargets();

		for (Entity t : targets) {
			if (t instanceof Player) // Ugly but simple code for checking if
			// player should be pushed back
			{
				Hit hit = getMeleeHit(npc, Utils.random(800, 3600));
				hit.setAbilityMark();
				if (dirs[0] == 1) {
					if (t.getY() - king.getY() >= -1
							&& t.getY() - king.getY() <= 4
							&& t.getX() - king.getX() >= 2
							&& t.getX() - king.getX() <= 6) {
						t.setNextAnimation(new Animation(10070));
						t.setNextForceMovement(new ForceMovement(t, 0,
								new WorldTile(t.getX() + dirs[0], t.getY()
										+ dirs[1], t.getPlane()), 1,
								ForceMovement.EAST));
						delayHit(npc, 0, t, hit);
					}
				}
				if (dirs[0] == -1) {
					if (t.getY() - king.getY() >= -1
							&& t.getY() - king.getY() <= 4
							&& t.getX() - king.getX() <= 0
							&& t.getX() - king.getX() <= -6) {
						t.setNextForceMovement(new ForceMovement(t, 0,
								new WorldTile(t.getX() + dirs[0], t.getY()
										+ dirs[1], t.getPlane()), 1,
								ForceMovement.WEST));
						t.setNextAnimation(new Animation(10070));
						delayHit(npc, 0, t, hit);
					}
				}
				if (dirs[1] == 1) {
					if (t.getX() - king.getX() >= -2
							&& t.getX() - king.getX() <= 5
							&& t.getY() - king.getY() >= 2
							&& t.getY() - king.getY() <= 6) {
						t.setNextForceMovement(new ForceMovement(t, 0,
								new WorldTile(t.getX() + dirs[0], t.getY()
										+ dirs[1], t.getPlane()), 1,
								ForceMovement.NORTH));
						t.setNextAnimation(new Animation(10070));
						delayHit(npc, 0, t, hit);
					}
				}
				if (dirs[1] == -1) {
					if (t.getX() - king.getX() >= -2
							&& t.getX() - king.getX() <= 5
							&& t.getY() - king.getY() <= -2
							&& t.getY() - king.getY() >= -6) {
						t.setNextForceMovement(new ForceMovement(t, 0,
								new WorldTile(t.getX() + dirs[0], t.getY()
										+ dirs[1], t.getPlane()), 1,
								ForceMovement.SOUTH));
						t.setNextAnimation(new Animation(10070));
						delayHit(npc, 0, t, hit);
					}
				}
			}
		}
	}

	private void meleeBleed(NPC npc, Entity target) {
		Entity t = null;
		try {
			List<Entity> targets = npc.getPossibleTargets(); // To select random
			// target and not
			// tank
			Collections.shuffle(targets);
			t = targets.get(0);
		} catch (Exception e) {

		}
		if (t != null) {
			npc.setTarget(t);
			npc.setNextFaceEntity(t);
			npc.setNextAnimation(new Animation(19449));
			t.getEffectsManager().startEffect(
					new Effect(EffectType.SLAUGHTER, 10, HitLook.MELEE_DAMAGE,
							new Graphics(3464), Utils.random(1280) + 1, 2, npc,
							new WorldTile(target)));
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				npc.setTarget(target);
			}
		}, 1);
	}

	private void rush(NPC npc, Entity target) // Needs some improvement, if king
	// starts the bullrush too close
	// to the wall it won't do any
	// damage to players, credits for
	// 90% of this method goes to
	// matrix developers
	{
		KalphiteKing king = (KalphiteKing) npc;
		king.setTarget(null);

		final byte[] dirs = Utils.getOrthogonalDirection(
				npc.getLastWorldTile(), target.getLastWorldTile());

		if (dirs[0] == 1) // To make it face the correct way
			king.setNextFaceWorldTile(new WorldTile(king.getX() + 10, king
					.getY() + 2, king.getPlane()));
		else if (dirs[0] == -1)
			king.setNextFaceWorldTile(new WorldTile(king.getX() - 10, king
					.getY() + 2, king.getPlane()));
		else if (dirs[1] == 1)
			king.setNextFaceWorldTile(new WorldTile(king.getX() + 2, king
					.getY() + 10, king.getPlane()));
		else if (dirs[1] == -1)
			king.setNextFaceWorldTile(new WorldTile(king.getX() + 2, king
					.getY() - 10, king.getPlane()));

		WorldTile lastTile = null;
		int distance;
		for (distance = 1; distance < 10; distance++) {
			WorldTile nextTile = new WorldTile(new WorldTile(king.getX()
					+ (dirs[0] * distance), king.getY() + (dirs[1] * distance),
					king.getPlane()));
			if (!World.isFloorFree(nextTile.getPlane(), nextTile.getX(),
					nextTile.getY(), king.getSize()))
				break;
			lastTile = nextTile;
		}
		if (lastTile == null || distance <= 2) {
			king.setNextAnimation(new Animation(19447));
			king.setNextGraphics(new Graphics(3735));
			for (Entity t : king.getPossibleTargets()) {
				if (!Utils.isOnRange(king, t, 1))
					continue;
				delayHit(npc, 0, t,
						getRegularHit(npc, Utils.random(2600) + 2600));
			}
		} else {
			king.setNextAnimation(new Animation(19457));
			final int maxStep = distance / 2;
			king.setCantInteract(true);
			king.setNextAnimation(new Animation(maxStep + 19456));
			int totalTime = distance / 2;
			final WorldTile firstTile = new WorldTile(king);
			int dir = king.getDirection();
			king.setNextForceMovement(new NewForceMovement(firstTile, 5,
					lastTile, totalTime + 5, dir));
			WorldTile tpTile = lastTile;
			final ArrayList<Entity> targets = king.getPossibleTargets();
			WorldTasksManager.schedule(new WorldTask() {
				int step = 0;

				@Override
				public void run() {
					if (step == maxStep - 1) {
						king.setCantInteract(false);
						king.setTarget(target);
						stop();
						return;
					}
					if (step == 1)
						king.setNextWorldTile(tpTile);
					WorldTile kingTile = new WorldTile(firstTile.getX()
							+ (dirs[0] * step * 2), firstTile.getY()
							+ (dirs[1] * step * 2), king.getPlane());
					int leftSteps = (maxStep - step) + 1;
					for (Entity t : targets) {
						if (!(t instanceof Player))
							continue;
						Player player = (Player) t;
						if (player.isLocked())
							continue;
						if (Utils.colides(kingTile, t, king.getSize(), 1)) {

							WorldTile lastTileForP = null;
							int stepCount = 0;
							for (int thisStep = 1; thisStep <= leftSteps; thisStep++) {
								WorldTile nextTile = new WorldTile(
										new WorldTile(
												player.getX()
														+ (dirs[0] * thisStep * 2),
												player.getY()
														+ (dirs[1] * thisStep * 2),
												player.getPlane()));
								if (!World.isFloorFree(nextTile.getPlane(),
										nextTile.getX(), nextTile.getY()))
									break;
								lastTileForP = nextTile;
								stepCount = thisStep;
							}
							if (lastTileForP == null)
								continue;
							player.setNextForceMovement(new NewForceMovement(
									player, 0, lastTileForP, stepCount, Utils
											.getAngle(
													firstTile.getX()
															- player.getX(),
													firstTile.getY()
															- player.getY())));
							player.setNextAnimation(new Animation(10070));
							player.lock(stepCount + 1);
							delayHit(
									npc,
									0,
									t,
									getRegularHit(npc, Utils.random(1800, 3600)));
							final WorldTile lastTileForP_T = lastTileForP;

							WorldTasksManager.schedule(new WorldTask() {
								@Override
								public void run() {
									player.setNextWorldTile(lastTileForP_T);
									player.faceEntity(king);
								}
							}, 0);
						}
					}
					step++;
				}
			}, 3, 0);
		}
	}

	private void mageBall(NPC npc, Entity target, int n, boolean bleed) // Single
	// ball
	{
		npc.setNextAnimation(new Animation(19448));
		npc.setNextGraphics(new Graphics(3742));

		for (Entity t : npc.getPossibleTargets()) {
			WorldTile tile = new WorldTile(t).transform(n, 0, 0);
			Projectile projectile = World.sendProjectileNew(npc, tile, 3743,
					100, 30, 80, 1, 16, 0);
			// if(twoOrbs)
			// World.sendProjectileNew(npc, tile.transform(0, -1, 0), 3743, 100,
			// 30, 80, 1, 16, 0);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					mageDoBallGraphics(npc, tile);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							World.sendGraphics(npc, new Graphics(3752), tile);
							for (Entity t : npc.getPossibleTargets()) {
								if (t.withinDistance(tile, 2)) {
									if (!bleed) {
										Hit hit = new Hit(npc, Utils
												.random(4350) + 100,
												HitLook.MAGIC_DAMAGE);
										hit.setAbilityMark();
										t.applyHit(hit);
									} else
										t.getEffectsManager().startEffect(
												new Effect(EffectType.COMBUST,
														10,
														HitLook.MAGIC_DAMAGE,
														new Graphics(3574),
														Utils.random(1280) + 1,
														4, npc,
														new WorldTile(t)));
								}
							}
						}
					}, 2);
				}
			}, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1);
		}
	}

	private void mageBallDouble(NPC npc, Entity target) // Double ball
	{
		mageBall(npc, target, 1, false);
		mageBall(npc, target, -1, false);
	}

	private void mageBallBlue(NPC npc, Entity target) // Blue ball
	{
		npc.setNextAnimation(new Animation(19448));
		npc.setNextGraphics(new Graphics(3757));
		for (Entity t : npc.getPossibleTargets()) {
			Projectile projectile = World.sendProjectileNew(npc, t, 3758, 100,
					30, 80, 2, 16, 0);
			int delay = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
			Hit hit = getMagicHit(
					npc,
					getMaxHit(npc, Utils.random(2200),
							NPCCombatDefinitions.MAGE, t));
			if (!(Utils.random(100)
					- ((Player) target).getSkills().getLevel(Skills.MAGIC) / 2 < 10)
					&& hit.getDamage() > 1) {
				t.setStunDelay(8);
				hit.setAbilityMark();
				delayHit(npc, delay, t, hit);
			}
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (t instanceof Player)
						((Player) t).getPackets().sendGameMessage(
								"You've been prevented from moving.");
				}
			}, delay);
		}
	}

	private void mageBallBleed(NPC npc, Entity target) // Combustion ball
	{
		mageBall(npc, target, 0, true);
	}

	private void mageDoBallGraphics(NPC npc, WorldTile tile) {
		// Projectile projectile = World.sendProjectileNew(tile, tile, 3758, 0,
		// 100, 80, 2, 16, 0);
		World.sendGraphics(npc, new Graphics(3743), tile);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				World.sendGraphics(npc, new Graphics(3743), tile);
			}
		}, 1);
	}

	@Override
	public int attack(NPC npc, Entity target) {
		KalphiteKing king = (KalphiteKing) npc;
		if ((king.isStunned() || king.isBound())
				&& (king.getId() == 16699 || king.getId() == 16698)) // Only
		// on
		// range
		// and
		// mage
		// phase
		{
			king.getEffectsManager().removeEffect(EffectType.STUNNED);
			king.getEffectsManager().removeEffect(EffectType.BOUND);
			king.getEffectsManager().removeEffect(EffectType.BINDING_SHOT);
			if (king.getPhase() != 5 && king.getPhase() != 6) // Can't skip
				// green after he
				// started it
				king.nextPhase();
			else if (king.getPhase() == 5)
				king.setPhase(7);
			if (king.getPhase() < 0 || king.getPhase() > 9)
				king.setPhase(0);
			return king.getAttackSpeed() + 5;
		}

		if (Utils.random(20) == 1 && !king.isShieldActive) {
			king.activateShield();
		}

		// When he spawns minions, he skips an attack only if it's not melee
		// phase and he hasnt thrown the green ball before the 1 hit
		if ((king.getHPPercentage() < 75 && king.getSpawnCount() < 1)
				&& ((king.getPhase() != 6 && king.getId() == 16699)
						|| (king.getPhase() != 9 && king.getId() == 16697) || king
						.getId() == 16698)) {
			king.battleCry();
			king.spawnCount++;
			if (!(king.getPhase() == 5 && king.getId() == 16697)
					&& !(king.getId() == 16697)) {
				king.nextPhase();
			} else if (king.getPhase() == 5 && king.getId() == 16699) {
				king.setPhase(7);
			}
			return 17;
		} else if ((king.getHPPercentage() < 25 && king.getSpawnCount() < 2)
				&& ((king.getPhase() != 6 && king.getId() == 16699)
						|| (king.getPhase() != 9 && king.getId() == 16697) || king
						.getId() == 16698)) { // Same here
			king.battleCry();
			king.battleCry();
			king.spawnCount++;
			if (!(king.getPhase() == 5 && king.getId() == 16697)
					&& !(king.getId() == 16697)) {
				king.nextPhase();
			} else if (king.getPhase() == 5 && king.getId() == 16699) {
				king.setPhase(7);
			}
			return 17;
		}

		if (Utils.random(30) == 15 && king.getPhase() != 5
				&& king.getPhase() != 9) {
			king.switchPhase();
		}

		if (npc.getId() == 16699) // Range DONE
		{
			switch (king.getPhase()) {
			case 0:
				rangeBasic(npc, target);
				break;
			case 1:
				rangeFrag(npc, target);
				break;
			case 2:
				rangeStun(npc, target);
				break;
			case 3:
				rangeBasic(npc, target);
				break;
			case 4:
				rangeBasic(npc, target);
				break;
			case 5:
				green(npc, target);
				king.nextPhase();
				return 8;
			case 6:
				if (excecuteGreen(npc, target))
					return npc.getAttackSpeed();
				else
					return 1;
			case 7:
				king.setForceFollowClose(false);
				rangeIncendiaryShot(npc, target);
				break;
			case 8:
				rangeBasic(npc, target);
				break;
			case 9:
				dig(npc, target);
				king.setPhase(0);
				break;
			}
			king.nextPhase();
			if (king.getPhase() < 0 || king.getPhase() > 9)
				king.setPhase(0);
			return npc.getAttackSpeed();
		} else if (npc.getId() == 16697) // Melee DONE
		{
			switch (king.getPhase()) {
			case 0:
				meleeBleed(npc, target);
				break;
			case 1:
				meleeStomp(npc, target);
				break;
			case 2:
				meleePush(npc, target);
				break;
			case 3:
				meleeBleed(npc, target);
				break;
			case 4:
				meleeBleed(npc, target);
				break;
			case 5:
				rush(npc, target);
				king.nextPhase();
				return 17;
			case 6:
				meleeStomp(npc, target);
				break;
			case 7:
				meleeBleed(npc, target);
				break;
			case 8:
				green(npc, target);
				king.nextPhase();
				return 8;
			case 9:
				if (excecuteGreen(npc, target))
					return npc.getAttackSpeed();
				else
					return 1;
			}
			king.nextPhase();
			if (king.getPhase() < 0 || king.getPhase() > 9)
				king.setPhase(0);
			return npc.getAttackSpeed();
		} else if (npc.getId() == 16698) // Mage DONE
		{
			switch (king.getPhase()) {
			case 0:
				mageBall(npc, target, 0, false);
				break;
			case 1:
				mageBallBlue(npc, target);
				break;
			case 2:
				mageBall(npc, target, 0, false);
				break;
			case 3:
				mageBall(npc, target, 0, false);
				break;
			case 4:
				dig(npc, target);
				break;
			case 5:
				mageBallDouble(npc, target);
				break;
			case 6:
				mageBall(npc, target, 0, false);
				break;
			case 7:
				rush(npc, target);
				king.nextPhase();
				return 17;
			case 8:
				mageBallBlue(npc, target);
				break;
			case 9:
				mageBallBleed(npc, target);
				break;
			}
			king.nextPhase();
			if (king.getPhase() < 0 || king.getPhase() > 9)
				king.setPhase(0);
			return npc.getAttackSpeed() + 3;
		}
		return npc.getAttackSpeed();
	}
}