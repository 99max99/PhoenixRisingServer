package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class KrilTsutsaroth extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 6203 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (!Utils.isOnRange(npc, target, 0))
			return 0;
		if (Utils.random(4) == 0) {
			switch (Utils.random(8)) {
			case 0:
				npc.setNextForceTalk(new ForceTalk("Attack them, you dogs!"));
				npc.playSoundEffect(3278);
				break;
			case 1:
				npc.setNextForceTalk(new ForceTalk("Forward!"));
				npc.playSoundEffect(3276);
				break;
			case 2:
				npc.setNextForceTalk(new ForceTalk("Death to Saradomin's dogs!"));
				npc.playSoundEffect(3277);
				break;
			case 3:
				npc.setNextForceTalk(new ForceTalk("Kill them, you cowards!"));
				npc.playSoundEffect(3290);
				break;
			case 4:
				npc.setNextForceTalk(new ForceTalk(
						"The Dark One will have their souls!"));
				npc.playSoundEffect(3280);
				break;
			case 5:
				npc.setNextForceTalk(new ForceTalk("Zamorak curse them!"));
				npc.playSoundEffect(3270);
				break;
			case 6:
				npc.setNextForceTalk(new ForceTalk("Rend them limb from limb!"));
				npc.playSoundEffect(3273);
				break;
			case 7:
				npc.setNextForceTalk(new ForceTalk("No retreat!"));
				npc.playSoundEffect(3276);
				break;
			case 8:
				npc.setNextForceTalk(new ForceTalk("Flay them all!"));
				npc.playSoundEffect(3286);
				break;
			}
		}
		boolean specialAttack = Utils.random(5) == 0; // 1/5 chance
		int style = Utils.random(3);
		if (specialAttack || style == 0) {// Spec takes priority ofc
			npc.setNextAnimation(new Animation(14384));
			for (Entity e : npc.getPossibleTargets()) {
				if (specialAttack) {
					if (e.withinDistance(target, 1)) {
						if (e instanceof Player) {
							Player p = (Player) e;
							p.getPackets()
									.sendGameMessage(
											"K'ril Tsutsaroth slams through "
													+ (e == target ? "your" : p
															.getDisplayName()
															+ "'s")
													+ " "
													+ (p.getPrayer()
															.isAncientCurses() ? "curses"
															: "protection prayer")
													+ ", leaving you feeling drained.");
						}
						npc.playSoundEffect(3274);
						npc.setNextForceTalk(new ForceTalk("YARRRRRRR!"));// TODO
																			// find
																			// GFX
						delayHit(
								npc,
								0,
								e,
								getMagicHit(
										npc,
										getMaxHit(npc,
												NPCCombatDefinitions.MAGE, e)));
					}
				} else {
					Projectile projectile = World.sendProjectileNew(npc, e,
							1211, 25, 16, 30, 2, 16, 0);
					delayHit(
							npc,
							Utils.projectileTimeToCycles(projectile
									.getEndTime()),
							e,
							getMagicHit(
									npc,
									getMaxHit(
											npc,
											specialAttack ? 5050
													: npc.getMaxHit(NPCCombatDefinitions.MAGE),
											NPCCombatDefinitions.MAGE, e)));
				}
			}
		} else {
			npc.setNextAnimation(new Animation(14963));
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		}
		return npc.getAttackSpeed();
	}
}
