package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dragons.FrostDragon;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.DragonfireShield;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class FrostDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Frost dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final FrostDragon dragon = (FrostDragon) npc;

		if (dragon.getAttackStage() > 9 && Utils.random(2) == 0) {
			dragon.setAttackStage(0);
			dragon.setOrb(true);
			WorldTasksManager.schedule(new WorldTask() {

				int ticks = -1;

				@Override
				public void run() {
					ticks++;
					World.sendGraphics(dragon, new Graphics(2875, 0, 750),
							new WorldTile(dragon.getMiddleWorldTile()));
					// World.sendProjectile(dragon, dragon, new
					// WorldTile(dragon), 2875, 50, 28, 15, ticks * 15, 0, 4);
					if (ticks == 8) {
						dragon.setOrb(false);
						stop();
						return;
					}
				}
			}, 0, 0);
		}
		dragon.setAttackStage(dragon.getAttackStage() + 1);

		if (Utils.random(5) == 0) { // regular dragonfire
			npc.setNextAnimation(new Animation(13155));
			World.sendProjectile(npc, target, 2464, 28, 28, 50, 41, 16, 0);
			delayHit(npc, 2, target,
					getRegularHit(npc, getBreathDamage(npc, target)));
		} else {
			boolean meleeAttack = Utils.random(2) == 0
					&& Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(),
							target.getX(), target.getY(), target.getSize(), 0);
			if (meleeAttack) {
				if (Utils.random(5) == 0) {// Fire breath swipe
					npc.setNextAnimation(new Animation(13152));
					npc.setNextGraphics(new Graphics(2465));
					delayHit(npc, 1, target,
							getRegularHit(npc, getBreathDamage(npc, target)));
				} else {
					npc.setNextAnimation(new Animation(13151));
					delayHit(
							npc,
							0,
							target,
							getMeleeHit(
									npc,
									getMaxHit(npc, NPCCombatDefinitions.MELEE,
											target)));
				}
			} else {
				boolean isMagicOnly = dragon.isMagicOnly();
				npc.setNextAnimation(new Animation(13155));
				World.sendProjectile(npc, target, isMagicOnly ? 2705 : 16, 20,
						28, 35, 50, 0, 1);
				if (isMagicOnly)
					delayHit(
							npc,
							2,
							target,
							getMagicHit(
									npc,
									getMaxHit(npc, NPCCombatDefinitions.MAGE,
											target)));
				else
					delayHit(
							npc,
							2,
							target,
							getRangeHit(
									npc,
									getMaxHit(npc, NPCCombatDefinitions.RANGE,
											target)));
			}
		}
		return npc.getAttackSpeed();
	}

	private int getBreathDamage(NPC npc, Entity target) {
		int damage = getMaxHit(npc, NPCCombatDefinitions.MAGE, target);
		if (damage > 200 && target instanceof Player) {
			Player player = (Player) target;
			// Rest should all be types of dragon-fire.

			boolean hasSuperPot = player.getEffectsManager().hasActiveEffect(
					EffectType.SUPER_FIRE_IMMUNITY);
			boolean hasRegularPot = player.getEffectsManager().hasActiveEffect(
					EffectType.FIRE_IMMUNITY);
			boolean hasShield = Combat.hasAntiDragProtection(target);
			if (hasSuperPot) {
				damage = 0;
				player.getPackets()
						.sendGameMessage(
								"Your potion fully protects you from the dragon's breath.");
			} else {
				if (hasRegularPot)
					damage *= 0.5;
				if (hasShield) {
					player.getPackets().sendGameMessage(
							"Your shield absorbs some of the dragon's breath!");
					damage = hasRegularPot ? 0 : damage / 2;
				}
			}
			DragonfireShield.chargeDFS(player, false);
		}
		return damage;
	}

}
