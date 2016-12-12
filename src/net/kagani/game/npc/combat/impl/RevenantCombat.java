package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class RevenantCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 13465, 13466, 13467, 13468, 13469, 13470, 13471,
				13472, 13473, 13474, 13475, 13476, 13477, 13478, 13479, 13480,
				13481 };
	}

	public int getMagicAnimation(NPC npc) {
		switch (npc.getId()) {
		case 13465:
			return 7500;
		case 13466:
		case 13467:
		case 13468:
		case 13469:
			return 7499;
		case 13470:
		case 13471:
			return 7506;
		case 13472:
			return 7503;
		case 13473:
			return 7507;
		case 13474:
			return 7496;
		case 13475:
			return 7497;
		case 13476:
			return 7515;
		case 13477:
			return 7498;
		case 13478:
			return 7505;
		case 13479:
			return 7515;
		case 13480:
			return 7508;
		case 13481:
		default:
			// melee emote, better than 0
			return npc.getCombatDefinitions().getAttackEmote();
		}
	}

	public int getRangeAnimation(NPC npc) {
		switch (npc.getId()) {
		case 13465:
			return 7501;
		case 13466:
		case 13467:
		case 13468:
		case 13469:
			return 7513;
		case 13470:
		case 13471:
			return 7519;
		case 13472:
			return 7516;
		case 13473:
			return 7520;
		case 13474:
			return 7521;
		case 13475:
			return 7510;
		case 13476:
			return 7501;
		case 13477:
			return 7512;
		case 13478:
			return 7518;
		case 13479:
			return 7514;
		case 13480:
			return 7522;
		case 13481:
		default:
			// melee emote, better than 0
			return npc.getCombatDefinitions().getAttackEmote();
		}
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (npc.getHitpoints() < npc.getMaxHitpoints() / 2
				&& Utils.random(5) == 0) // if
			// lower
			// than
			// 50%
			// hp,
			// 1/5
			// prob
			// of
			// healing
			// 10%
			npc.heal(100);

		int attackStyle = Utils.random(3);
		if (attackStyle == 2) { // checks if can melee
			if (!Utils.isOnRange(npc, target, 0))
				attackStyle = Utils.random(2);
		}
		boolean noDamage = false;
		if (target instanceof Player) {
			Player targetPlayer = (Player) target;
			Long ivulnerability = (Long) targetPlayer.getTemporaryAttributtes()
					.get(Key.REVENEANT_IVULNERABILITY);
			if (ivulnerability != null
					&& ivulnerability + 100 > Utils.currentWorldCycle())
				noDamage = true;
		}
		switch (attackStyle) {
		case 0: // magic
			int damage = noDamage ? 0 : getMaxHit(npc,
					NPCCombatDefinitions.MAGE, target);
			Projectile projectile = World.sendProjectileNew(npc, target, 1276,
					34, 16, 35, 2, 10, 0);
			int endTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
			delayHit(npc, endTime, target, getMagicHit(npc, damage));
			if (damage > 0) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						target.setNextGraphics(new Graphics(1277, 0, 100));
						if (Utils.random(5) == 0) { // 1/5 prob freezing
							// while maging
							target.setNextGraphics(new Graphics(363));
							target.setBoundDelay(8);
						}
					}

				}, endTime);
			}
			npc.setNextAnimation(new Animation(getMagicAnimation(npc)));
			break;
		case 1: // range
			npc.setNextAnimation(new Animation(getRangeAnimation(npc)));
			projectile = World.sendProjectileNew(npc, target, 1278, 34, 16, 35,
					2, 10, 0);
			delayHit(
					npc,
					Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
					target,
					getRangeHit(
							npc,
							noDamage ? 0 : getMaxHit(npc,
									NPCCombatDefinitions.RANGE, target)));
			break;
		case 2: // melee
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(
							npc,
							noDamage ? 0 : getMaxHit(npc,
									NPCCombatDefinitions.MELEE, target)));
			break;
		}
		return npc.getAttackSpeed();
	}
}
