package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.DragonfireShield;
import net.kagani.utils.Utils;

public class KingBlackDragonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 50 };
	}

	private static final String[] ADVERBS = { "poisonous", "freezing",
			"shocking" };
	private static final int[][] ATTACKS = { { 17786, 3441, 3442, 3443 },
			{ 17785, 3435, 3436, 3437 }, { 17783, 3438, 3439, 3440 },
			{ 17784, 3432, 3433, 3434 }, };

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		boolean isDistanced = !Utils.isOnRange(npc.getX(), npc.getY(),
				npc.getSize(), target.getX(), target.getY(), target.getSize(),
				0);
		int style = Utils.random(isDistanced ? 4 : 5);
		if (style == 4) {// MELEE
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			return npc.getAttackSpeed();
		} else {
			int damage = getMaxHit(npc, 4500, NPCCombatDefinitions.MAGE, target);
			boolean negateDamage = target instanceof Familiar;
			if (damage > 200 && target instanceof Player) {
				Player player = (Player) target;
				// Rest should all be types of dragon-fire.

				boolean hasSuperPot = player.getEffectsManager()
						.hasActiveEffect(EffectType.SUPER_FIRE_IMMUNITY);
				boolean hasRegularPot = player.getEffectsManager()
						.hasActiveEffect(EffectType.FIRE_IMMUNITY);
				boolean hasShield = Combat.hasAntiDragProtection(target);
				if (style == 0) {
					negateDamage = hasRegularPot && hasShield || hasSuperPot;
					if (hasSuperPot)
						player.getPackets()
								.sendGameMessage(
										"Your potion fully protects you from the dragon's fiery breath.");
				} else {
					boolean hasPrayer = player.getPrayer().isMageProtecting();
					negateDamage = hasShield || hasRegularPot && hasPrayer;
					if (hasShield)
						player.getPackets().sendGameMessage(
								"Your shield absorbs some of the dragon's "
										+ ADVERBS[style - 1] + " breath!");
				}
				DragonfireShield.chargeDFS(player, false);
			}
			if (negateDamage)
				damage *= 0.30915576694411414982164090368609;// Just leave it
																// like this,
																// works fine
																// for KBD.
			if (style == 1 && Utils.random(5) == 0)
				EffectsManager.makePoisoned(target, 100);
			else if (style == 2 && damage > 200)
				target.setBoundDelay(5, true);
			else if (style == 3 && damage > 200 && target instanceof Player) {
				Player player = ((Player) target);
				int drain = Utils.random(1, 2);
				for (int skill = 0; skill < Skills.SKILL_NAME.length; skill++)
					player.getSkills().set(skill,
							player.getSkills().getLevelForXp(skill) - drain);
				player.getPackets().sendGameMessage(
						"You feel drained from the dragon's shocking breath.");
			}
			final int[] ATTACK_DATA = ATTACKS[style];
			final Animation ATTACK_ANIM = new Animation(ATTACK_DATA[0]);
			npc.setNextAnimation(ATTACK_ANIM);
			Projectile projectile = World.sendProjectile(npc, target, false,
					true, -1, ATTACK_DATA[2], 60, 41,
					(style == 1 || style == 2) ? 30 : 40, 2, 0, 0);
			npc.setNextGraphics(new Graphics(ATTACK_DATA[1], 0, projectile
					.getStartHeight()));
			delayHit(npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target, getMagicHit(npc, damage));
			target.setNextGraphics(new Graphics(ATTACK_DATA[3], projectile
					.getEndTime(), 0));
		}
		return npc.getAttackSpeed();
	}
}
