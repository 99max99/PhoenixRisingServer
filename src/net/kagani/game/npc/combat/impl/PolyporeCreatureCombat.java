package net.kagani.game.npc.combat.impl;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.NewForceMovement;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.others.PolyporeCreature;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class PolyporeCreatureCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 14688, 14689, 14690, 14691, 14692, 14693, 14694,
				14695, 14696, 14697, 14698, 14699, 14700, 14701 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		NPCCombatDefinitions def = npc.getCombatDefinitions();
		final PolyporeCreature creature = (PolyporeCreature) npc;
		if (Utils.isOnRange(target, npc, 0) && creature.canInfect()) {
			int infectEmote = creature.getInfectEmote();
			if (infectEmote != -1) {
				npc.setNextAnimation(new Animation(infectEmote));
				if (target instanceof Player) {
					Player player = (Player) target;
					if (Utils.random(5) == 0) {
						player.getPrayer().drainPrayer(1);
						npc.setNextForceTalk(new ForceTalk("Krrr!"));
					}
					player.getPackets().sendGameMessage(
							"The creature infests you with its toxic fungus.");
				}
				int base = npc.getId() >= 14696 && npc.getId() <= 14699 ? 10
						: 2;
				delayHit(npc, 0, target, new Hit(npc, base + Utils.random(2),
						HitLook.POISON_DAMAGE));
				return 1;
			}
		}
		if ((npc.getId() == 14700 || npc.getId() == 14701)
				&& Utils.random(5) == 0) {
			int size = npc.getSize();
			if (Utils.isOnRange(npc, target, 6)) {
				int[][] dirs = Utils.getCoordOffsetsNear(size);
				for (int dir = 0; dir < dirs[0].length; dir++) {
					final WorldTile tile = new WorldTile(new WorldTile(
							target.getX() + dirs[0][dir], target.getY()
									+ dirs[1][dir], target.getPlane()));
					if (World.isTileFree(tile.getPlane(), tile.getX(),
							tile.getY(), size)) {
						npc.setNextForceTalk(new ForceTalk("Hup!"));
						WorldTile middle = npc.getMiddleWorldTile();
						npc.setNextForceMovement(new NewForceMovement(
								new WorldTile(npc), 0, tile, 2, Utils.getAngle(
										tile.getX() - middle.getX(),
										tile.getY() - middle.getY())));
						npc.setNextWorldTile(tile);
						npc.setNextAnimation(new Animation(15491));
						return 8;
					}
				}
			}
		} else if ((npc.getId() == 14688 || npc.getId() == 14689)
				&& Utils.random(5) == 0) {
			npc.resetWalkSteps();
			npc.calcFollow(target, false);
			npc.setForceFollowClose(true);
			npc.setNextForceTalk(new ForceTalk("Raargh!"));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					if (!Utils.isOnRange(target, npc, 0))
						npc.setNextForceTalk(new ForceTalk("*Sigh*"));
					npc.setForceFollowClose(false);
				}
			}, 5);
			return 5;
		}
		if (npc.getAttackStyle() != NPCCombatDefinitions.MELEE) {
			if (npc.getId() == 14701 || npc.getId() == 14689) {
				delayHit(
						npc,
						2,
						target,
						getRangeHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.RANGE,
										target)));
				if (npc.getId() == 14689) {
					npc.setNextAnimation(new Animation(15487));
					return npc.getAttackSpeed();
				}
			} else {
				Projectile projectile = World.sendProjectileNew(npc, target,
						2035, 41, 16, 20, 2, 10, 0);
				delayHit(
						npc,
						Utils.projectileTimeToCycles(projectile.getEndTime()) - 1,
						target,
						getMagicHit(
								npc,
								getMaxHit(npc, NPCCombatDefinitions.MAGE,
										target)));
			}
		} else
			delayHit(
					npc,
					0,
					target,
					getMeleeHit(npc,
							getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		if (def.getAttackGfx() != -1)
			npc.setNextGraphics(new Graphics(def.getAttackGfx()));
		npc.setNextAnimation(new Animation(def.getAttackEmote()));
		return npc.getAttackSpeed();
	}
}
