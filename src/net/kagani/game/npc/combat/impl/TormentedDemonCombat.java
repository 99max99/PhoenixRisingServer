package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.others.TormentedDemon;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class TormentedDemonCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tormented demon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		TormentedDemon demon = (TormentedDemon) npc;
		boolean isDistanced = !Utils.isOnRange(npc, target, 0);
		int style = demon.getFixedCombatType();

		if (isDistanced && demon.getFixedCombatType() == 0)
			demon.setFixedAmount(0);
		if (demon.getFixedAmount() == 0) {
			style = Utils.random(!isDistanced ? 0 : 1, 3);
			demon.setFixedCombatType(style);
			demon.setFixedAmount(Utils.random(3, 7));
		}
		if (style == 0) {
			npc.setNextAnimation(new Animation(10922));
			npc.setNextGraphics(new Graphics(1886));
			delayHit(npc, target, NPCCombatDefinitions.MELEE, 0);
		} else if (style == 1) {
			npc.setNextAnimation(new Animation(10918));
			Projectile projectile = World.sendProjectileNew(npc, target, 1884,
					70, 25, 30, 1, 16, 30);
			if (delayHit(npc, target, NPCCombatDefinitions.MAGE,
					projectile.getEndTime()))
				target.setNextGraphics(new Graphics(1883, projectile
						.getEndTime(), 100));
		} else if (style == 2) {
			npc.setNextAnimation(new Animation(10919));
			npc.setNextGraphics(new Graphics(1888));
			Projectile projectile = World.sendProjectileNew(npc, target, 1887,
					70, 25, 65, 1, 16, 0);
			delayHit(npc, target, NPCCombatDefinitions.RANGE,
					projectile.getEndTime());
		}
		demon.setFixedAmount(demon.getFixedAmount() - 1);
		return npc.getAttackSpeed();
	}

	private boolean delayHit(NPC npc, Entity target, int style, int endTime) {
		int damage = Utils.random(2100);
		if (target instanceof Player) {
			boolean negateDamage = false;
			Player player = (Player) target;
			negateDamage |= player.getPrayer().isMeleeProtecting()
					&& style == 0;
			negateDamage |= player.getPrayer().isRangeProtecting()
					&& style == 1;
			negateDamage |= player.getPrayer().isMageProtecting() && style == 2;
			if (negateDamage)
				damage = 0;
		}
		target.applyHit(new Hit(npc, damage, style == 0 ? HitLook.MELEE_DAMAGE
				: style == 1 ? HitLook.RANGE_DAMAGE : HitLook.MAGIC_DAMAGE,
				endTime));
		return damage != 0;
	}
}
