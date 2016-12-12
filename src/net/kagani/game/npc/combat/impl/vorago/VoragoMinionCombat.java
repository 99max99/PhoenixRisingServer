package net.kagani.game.npc.combat.impl.vorago;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.vorago.Vorago;
import net.kagani.game.npc.vorago.VoragoHandler;
import net.kagani.game.npc.vorago.VoragoMinion;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class VoragoMinionCombat extends CombatScript {

	public static boolean EnrageMessage;
	private Vorago rago = VoragoHandler.vorago;
	static VoragoMinion S1 = VoragoHandler.scop1;
	static VoragoMinion S2 = VoragoHandler.scop2;
	private int abilityId;
	private int adren = 0;
	private int multiplier;

	@Override
	public Object[] getKeys() {
		return new Object[] { 17185, 17159, 17159, 17160 };

	}

	private int getMultiplier() {// Multiplier of damage to player based on
		// closeness of scopuli
		if (rago.scopDead == 0) {
			multiplier = (10 - Utils.getDistance(S1, S2));
			if (multiplier < 1) {
				multiplier = 0;
			}
		} else {
			multiplier = 0;
		}
		return multiplier;
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final int id = npc.getId();
		if (id == 17158 || id == 17159 || id == 17160) {// Stone clone
			if (npc.isBound()
					|| npc.isStunned()
					|| npc.getEffectsManager()
							.hasActiveEffect(EffectType.BLEED)) {
				npc.getEffectsManager().startEffect(
						new Effect(EffectType.FREEDOM, 10));
			}
			switch (id) {
			case 17158:// Melee TODO currently is a mage clone
				if (adren >= 100) {// Ult
					adren = 0;
					abilityId = 6;// Utils.random(6,7);
				} else if (adren >= 50 && adren < 100 && Utils.random(3) == 0) {// Thresh
					adren = adren - 15;
					abilityId = Utils.random(4, 5);
				} else {// Basic
					adren = adren + 8;
					abilityId = Utils.random(4);
				}
				break;
			case 17159:// Range
				if (adren >= 100) {// Ult
					adren = 0;
					abilityId = Utils.random(15, 16);
				} else if (adren >= 50 && adren < 100 && Utils.random(3) == 0) {// Thresh
					adren = adren - 15;
					abilityId = Utils.random(13, 14);
				} else {// Basic
					adren = adren + 8;
					abilityId = Utils.random(8, 12);
				}
				break;
			case 17160:// Mage
				if (adren >= 100) {// Ult
					adren = 0;
					abilityId = 6;// Utils.random(6,7);
				} else if (adren >= 50 && adren < 100 && Utils.random(3) == 0) {// Thresh
					adren = adren - 15;
					abilityId = Utils.random(4, 5);
				} else {// Basic
					adren = adren + 8;
					abilityId = Utils.random(4);
				}
				break;

			}
			sendAbility(npc, target, abilityId);
		} else {// Scopulus
			int a = (int) Math.floor(getMultiplier() * Math.exp(6));
			final int damage = (getMaxHit(npc, 1653 + a,
					NPCCombatDefinitions.MELEE, target));// Has
			// a
			// max
			// of
			// ~5600
			npc.setForceFollowClose(true);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
			if (rago.scopDead == 1 && EnrageMessage == false) {
				npc.heal(15000);
				EnrageMessage = true;
				for (final Player p : VoragoHandler.getPlayers()) {
					p.getPackets()
							.sendGameMessage(
									"The remaining Scopulus becomes enraged as its twin dies.");
				}

			}
		}
		if (id == 17185) {// Scopulus
			return npc.getAttackSpeed() / (rago.scopDead + 1);
		} else {// Stone clone
			return npc.getAttackSpeed();
		}
	}

	/*** Abilities for clones TODO Melee clone ***/
	private void sendAbility(NPC npc, Entity target, int abilityId) {
		switch (abilityId) {
		case 0:// WRACK
			npc.setNextAnimation(new Animation(18355));
			npc.setNextGraphics(new Graphics(3530));
			int m = (int) (target.getEffectsManager().hasActiveEffect(
					EffectType.STUNNED) ? 1.25 : 0.94);
			Hit hit = getMagicHit(npc,
					getMaxHit(npc, 2560, NPCCombatDefinitions.MAGE, target) * m);
			hit.setAbilityMark();
			delayHit(npc, 0, target, hit);
			target.setNextGraphics(new Graphics(3536, 0, 130));
			break;
		case 1:// IMPACT
			npc.setNextAnimation(new Animation(18426));
			npc.setNextGraphics(new Graphics(3553));
			Projectile projectile = World.sendProjectileNew(npc, target, 3576,
					32, 37, 15, 5, 0, 10);
			hit = getMagicHit(npc,
					getMaxHit(npc, 2560, NPCCombatDefinitions.MAGE, target));
			hit.setAbilityMark();
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit);
			target.setStunDelay(5);
			int angle = (int) Math.round(Math.toDegrees(Math.atan2(
					(npc.getX() * 2 + npc.getSize())
							- (target.getX() * 2 + target.getSize()),
					(npc.getY() * 2 + npc.getSize())
							- (target.getY() * 2 + target.getSize()))) / 45d) & 0x7;
			target.setNextGraphics(new Graphics(3575,
					projectile.getEndTime() / 2, 130, angle, true));
			target.setNextGraphics(new Graphics(3488, projectile.getEndTime(),
					92));
			break;
		case 2:// CHAIN
			npc.setNextAnimation(new Animation(18426));
			npc.setNextGraphics(new Graphics(3553));
			projectile = World.sendProjectileNew(npc, target, 3551, 22, 39, 15,
					5, 0, 10);
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getMagicHit(
							npc,
							getMaxHit(npc, 2560, NPCCombatDefinitions.MAGE,
									target)));
			target.setNextGraphics(new Graphics(3559, projectile.getEndTime(),
					130));
			break;
		case 3:// COMBUST
			npc.setNextAnimation(new Animation(18449));
			npc.setNextGraphics(new Graphics(3568));
			int damage = getMaxHit(npc, 2560, NPCCombatDefinitions.MAGE, target) / 5;
			target.setNextGraphics(new Graphics(3574, 0, 130));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.getEffectsManager().startEffect(
							new Effect(EffectType.COMBUST, 10,
									HitLook.MAGIC_DAMAGE, new Graphics(3574),
									damage, 2, npc, new WorldTile(target)));
				}
			});
			break;

		case 4:// WILD MAGIC
			npc.setNextAnimation(new Animation(18410));
			Hit hit2 = getMagicHit(npc,
					getMaxHit(npc, 3500, NPCCombatDefinitions.MAGE, target));
			Hit hit3 = getMagicHit(npc,
					getMaxHit(npc, 3500, NPCCombatDefinitions.MAGE, target));
			hit2.setAbilityMark();
			hit3.setAbilityMark();
			projectile = World.sendProjectileNew(npc, target, 2729, 32, 42, 40,
					5, 0, 10);
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit2);
			projectile = World.sendProjectileNew(npc, target, 2729, 32, 39, 45,
					3, 0, 10);
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit3);
			break;
		case 5:// ASPHYXIATE
			npc.setNextAnimation(new Animation(18392));
			npc.setNextGraphics(new Graphics(3541));
			WorldTasksManager.schedule(new WorldTask() {
				int cycle = 9;

				@Override
				public void run() {
					if (cycle == 9 || cycle == 7 || cycle == 5 || cycle == 3) {
						Hit hit = getMagicHit(
								npc,
								getMaxHit(npc, 3500, NPCCombatDefinitions.MAGE,
										target));
						if (cycle == 9 && hit.getDamage() > 0)
							target.setBoundDelay(10);// Six seconds
						delayHit(npc, 0, target, hit);
					}
					if (cycle == 0) {
						stop();
					}
					cycle--;
				}
			}, 1);

			break;
		case 6:// OMNIPOWER
			hit = getMagicHit(npc,
					getMaxHit(npc, 6000, NPCCombatDefinitions.MAGE, target));
			hit.setAbilityMark();
			npc.setNextAnimation(new Animation(18364));
			npc.setNextGraphics(new Graphics(3564));
			projectile = World.sendProjectileNew(npc, target, 3565, 32, 40, 80,
					5, 0, 10);
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit);
			target.setNextGraphics(new Graphics(3566, projectile.getEndTime(),
					130));
			break;
		case 7:// Meta TODO
			sendAbility(npc, target, 6);
			break;
		case 8:// PIERCING SHOT
			npc.setNextAnimation(new Animation(18522));
			projectile = World.sendProjectileNew(npc, target, 0, 41, 38, 0, 5,
					0, 95);
			m = (int) (target.getEffectsManager().hasActiveEffect(
					EffectType.STUNNED) ? 1.25 : 0.94);
			hit = getMagicHit(npc,
					getMaxHit(npc, 2560, NPCCombatDefinitions.RANGE, target)
							* m);
			hit.setAbilityMark();
			npc.setNextGraphics(new Graphics(4590));
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit);
			break;
		case 9:// BINDING SHOT
			npc.setNextAnimation(new Animation(18461));
			projectile = World.sendProjectileNew(npc, target, 0, 12, 39, 29, 5,
					0, 5);
			hit = getRangeHit(npc,
					getMaxHit(npc, 2560, NPCCombatDefinitions.RANGE, target));
			hit.setAbilityMark();
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit);
			target.getEffectsManager().startEffect(
					new Effect(EffectType.BINDING_SHOT,
							target instanceof Player ? 16 : 33));
			target.setStunDelay(5);// Three seconds
			target.setNextGraphics(new Graphics(3488,
					projectile.getEndTime() / 2, 92));
			break;
		case 10:// RICOCHET
			npc.setNextAnimation(new Animation(18522));
			npc.setNextGraphics(new Graphics(4590));
			projectile = World.sendProjectileNew(npc, target, 3496, 41, 42, 0,
					5, 0, 10);
			hit = getRangeHit(npc,
					getMaxHit(npc, 2560, NPCCombatDefinitions.RANGE, target));
			hit.setAbilityMark();
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit);
			target.setNextGraphics(new Graphics(3502,
					projectile.getEndTime() / 2, 92));
		case 11:// SNIPE TODO
			sendAbility(npc, target, 8);
			// npc.getEffectsManager().startEffect(new Effect(EffectType.SNIPE,
			// 5, projectileId, targetGFX, usingChin)); TODO
			break;
		case 12:// FRAGMENTATION SHOT
			npc.setNextAnimation(new Animation(18522));
			npc.setNextGraphics(new Graphics(4590));
			damage = getMaxHit(npc, 2560, NPCCombatDefinitions.RANGE, target) / 5;
			target.setNextGraphics(new Graphics(3574, 0, 130));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.getEffectsManager().startEffect(
							new Effect(EffectType.FRAGMENTATION, 10,
									HitLook.MAGIC_DAMAGE, new Graphics(3574),
									damage, 2, npc, new WorldTile(target)));
				}
			});

			break;
		case 13:// SNAPSHOT
			npc.setNextAnimation(new Animation(18486));
			hit2 = getRangeHit(npc,
					getMaxHit(npc, 3500, NPCCombatDefinitions.RANGE, target));
			hit3 = getRangeHit(npc,
					getMaxHit(npc, 3500, NPCCombatDefinitions.RANGE, target));
			hit2.setAbilityMark();
			hit3.setAbilityMark();
			projectile = World.sendProjectileNew(npc, target, 0, 32, 42, 40, 5,
					0, 10);
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit2);
			projectile = World.sendProjectileNew(npc, target, 0, 32, 39, 45, 3,
					0, 10);
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit3);
			break;
		case 14:// BOMBARDMENT
			npc.setNextAnimation(new Animation(18506));
			npc.setNextGraphics(new Graphics(3524));
			projectile = World.sendProjectileNew(npc, target, 0, 41, 40, 60, 2,
					0, 5);
			hit = getRangeHit(npc,
					getMaxHit(npc, 4500, NPCCombatDefinitions.RANGE, target));
			hit.setAbilityMark();
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, hit);
			target.setNextGraphics(new Graphics(3525, projectile.getEndTime(),
					130));
			break;
		case 15:// INCENRARY SHOT
			npc.setNextAnimation(new Animation(184510));
			projectile = World.sendProjectileNew(npc, target, 0, 41, 40, 60, 2,
					0, 5);
			hit = getRangeHit(npc,
					getMaxHit(npc, 6000, NPCCombatDefinitions.RANGE, target));
			hit.setAbilityMark();
			delayHit(npc, 200, target, hit);
			target.setNextGraphics(new Graphics(3521, projectile.getEndTime(),
					130));
			target.setNextGraphics(new Graphics(3522, 200, 0, 0, true));
			break;
		case 16:// DEADSHOT TODO
			sendAbility(npc, target, 15);
			/*
			 * npc.setNextGraphics(new Graphics(3519)); targets = usingChin ?
			 * getMultiAttackTargets(npc) : new Entity[] { target }; for (Entity
			 * t : targets) { projectile = World.sendProjectileNew(t == target ?
			 * npc : target, t, projectileId, 41, 39, 70, 5, 0, 5); Hit hit =
			 * getHit(npc, t, true, Combat.RANGE_TYPE, 1.0, 1.88, false, true);
			 * delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime
			 * ()) + 1, hit); if (hit.getDamage() > 0) { int minDamage =
			 * getMaxHit(npc, t, true, Combat.RANGE_TYPE, 1.0, true); int
			 * maxDamage = getMaxHit(npc, t, true, Combat.RANGE_TYPE, 3.13,
			 * true); final int constantDmg = Utils.random(minDamage, maxDamage)
			 * / 5; WorldTasksManager.schedule(new WorldTask() {
			 * 
			 * @Override public void run() {
			 * t.getEffectsManager().startEffect(new Effect(EffectType.DEADSHOT,
			 * 10, HitLook.RANGE_DAMAGE, new Graphics(3527), constantDmg, 2,
			 * npc)); } }, Utils.projectileTimeToCycles(projectile.getEndTime
			 * ()) - 1); } }
			 */
			break;
		}
	}
}