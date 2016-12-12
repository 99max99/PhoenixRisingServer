package net.kagani.game.npc.combat.impl;

import java.util.ArrayList;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.corp.CorporealBeast;
import net.kagani.game.npc.corp.DarkEnergyCore;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class CorporealBeastCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 8133 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		boolean isDistanced = !Utils.isOnRange(npc.getX(), npc.getY(),
				npc.getSize(), target.getX(), target.getY(), target.getSize(),
				0);
		final ArrayList<Entity> targets = npc.getPossibleTargets(true, true);
		CorporealBeast beast = (CorporealBeast) npc;
		if (beast.canSpawnCore(targets.size()) && Utils.random(5) == 0)
			beast.spawnDarkEnergyCore();
		boolean stomp = false;
		for (Entity t : targets) {
			if (t instanceof DarkEnergyCore)
				continue;
			if (t instanceof Familiar) {
				t.heal(npc.getHitpoints());
				t.sendDeath(npc);
				continue;
			}
			if (Utils.colides(t.getX(), t.getY(), t.getSize(), npc.getX(),
					npc.getY(), npc.getSize())) {
				stomp = true;
				delayHit(
						npc,
						0,
						t,
						getRegularHit(
								npc,
								getMaxHit(npc, Utils.random(5804) + 2000,
										NPCCombatDefinitions.MELEE, target)));
			}
		}
		if (stomp) {
			npc.setNextAnimation(new Animation(10496));
			npc.setNextGraphics(new Graphics(1834));
			return npc.getAttackSpeed();
		}
		int style = Utils.random(isDistanced ? 3 : 5);
		if (style == 3 || style == 4) {
			npc.setNextAnimation(new Animation(10058));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
			return npc.getAttackSpeed();
		} else {
			npc.setNextAnimation(new Animation(10410));
			final WorldTile tile = new WorldTile(target);
			Projectile projectile = World.sendProjectileNew(npc,
					style == 1 ? tile : target, 1823 + style, 41,
					style == 1 ? 0 : 16, 10, 1.5d, 16, 0);
			int projectileCycles = Utils.projectileTimeToCycles(projectile
					.getEndTime()) - 1, damage = getMaxHit(
					npc,
					style == 2 ? 5000 : npc
							.getMaxHit(NPCCombatDefinitions.MAGE),
					NPCCombatDefinitions.MAGE, target);
			if (style == 0 && damage > 0 && target instanceof Player) {
				final Player player = (Player) target;
				int skillSelect = Utils.random(3);
				final int skill = skillSelect == 0 ? Skills.MAGIC
						: (skillSelect == 1 ? Skills.SUMMONING : Skills.PRAYER);
				if ((skill == Skills.PRAYER && player.getPrayer()
						.getPrayerpoints() == 0)
						|| (skill != Skills.PRAYER && player.getSkills()
								.getLevel(skill) == 0)) {
					damage += 150 + Utils.random(100); // extra dmg as cant
														// drain more
				} else {
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							if (skill == Skills.PRAYER)
								player.getPrayer().drainPrayer(
										10 + Utils.random(41));
							else {
								int lvl = player.getSkills().getLevel(skill);
								lvl -= 1 + Utils.random(5);
								player.getSkills()
										.set(skill, lvl < 0 ? 0 : lvl);
							}
							player.getPackets().sendGameMessage(
									"Your " + Skills.SKILL_NAME[skill]
											+ " has been slighly drained!",
									true);
						}
					}, projectileCycles);
				}

			} else if (style == 1) {
				for (int i = 0; i < 6; i++) {
					final WorldTile newTile = new WorldTile(tile, 3);
					if (!World.isTileFree(newTile.getPlane(), newTile.getX(),
							newTile.getY(), 1))
						continue;
					final Projectile subProjectile = World.sendProjectileNew(
							tile, newTile, 1824, 0, 0,
							projectile.getEndTime() + 5, 1, 30, 0);
					WorldTasksManager.schedule(
							new WorldTask() {

								@Override
								public void run() {
									for (Entity t : targets) {
										if (t instanceof DarkEnergyCore)
											continue;
										if (t.getX() >= newTile.getX() - 1
												&& t.getX() <= newTile.getX() + 1
												&& t.getY() >= newTile.getY() - 1
												&& t.getY() <= newTile.getY() + 1)
											t.applyHit(new Hit(npc, Utils
													.random(150, 400),
													HitLook.MAGIC_DAMAGE));
									}
								}
							},
							Utils.projectileTimeToCycles(subProjectile
									.getEndTime()) - 1);
					World.sendGraphics(npc,
							new Graphics(1806, subProjectile.getEndTime(), 0,
									0, true), newTile);
				}
				return npc.getAttackSpeed();
			}
			delayHit(npc, projectileCycles, target, getMagicHit(npc, damage));
		}
		return npc.getAttackSpeed();
	}
}
