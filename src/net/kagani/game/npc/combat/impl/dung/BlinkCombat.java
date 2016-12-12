package net.kagani.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.dungeonnering.Blink;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class BlinkCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 12865 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final Blink boss = (Blink) npc;
		DungeonManager manager = boss.getManager();

		if (Utils.random(10) == 0 || boss.isSpecialRequired()) {
			boss.setSpecialRequired(false);
			boss.setNextForceTalk(new ForceTalk("H...Here it comes..."));
			boss.playSoundEffect(2989);
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					boss.setNextAnimation(new Animation(14956));
					boss.setNextForceTalk(new ForceTalk("Kapow!!"));
					boss.playSoundEffect(3002);
					for (Entity t : boss.getPossibleTargets()) {
						if (t instanceof Player)
							((Player) t).getPackets().sendGameMessage(
									"You are hit by a powerful magical blast.");
						t.setNextGraphics(new Graphics(2855, 0, 50));
						delayHit(
								boss,
								0,
								t,
								new Hit(boss, (int) Utils.random(
										boss.getMaxHit() * .6D,
										boss.getMaxHit()), HitLook.MAGIC_DAMAGE));
					}
				}
			}, 5);
			return 8;
		}

		boolean atDistance = !Utils.isOnRange(npc.getX(), npc.getY(),
				npc.getSize(), target.getX(), target.getY(), target.getSize(),
				0);
		if (atDistance || Utils.random(3) == 0) {
			boolean rangeAttack = Utils.random(3) == 0;

			if (rangeAttack) {
				if (manager.getParty().getTeam().size() > 1
						|| Utils.random(3) == 0) {
					WorldTile beginningTile = boss.getNextPath();
					boss.setNextAnimation(new Animation(14949));
					boss.resetCombat();
					boss.setNextFaceEntity(null);
					boss.setNextFaceWorldTile(beginningTile);// Faces the
																// direction it
																// throws into
					World.sendProjectile(boss, beginningTile, 2853, 18, 18, 50,
							50, 0, 0);
					WorldTasksManager.schedule(new WorldTask() {

						private List<WorldTile> knifeTargets;
						private int cycles;

						@Override
						public void run() {
							cycles++;
							if (cycles == 1) {
								knifeTargets = new LinkedList<WorldTile>();
								for (Entity t : boss.getPossibleTargets()) {
									WorldTile center = new WorldTile(t);
									for (int i = 0; i < 3; i++)
										knifeTargets.add(i == 0 ? center
												: Utils.getFreeTile(center, 1));
								}
							} else if (cycles == 2) {
								for (WorldTile tile : knifeTargets) {
									// outdated method projectile
									int delay = Utils
											.projectileTimeToCycles(World
													.sendProjectile(boss,
															boss.getNextPath(),
															tile, 2853, 18, 18,
															50, 50, 0, 0)
													.getEndTime());
									entityLoop: for (Entity t : boss
											.getPossibleTargets()) {
										if (!t.matches(tile))
											continue entityLoop;
										delayHit(
												boss,
												delay,
												t,
												getRangeHit(
														boss,
														getMaxHit(
																boss,
																boss.getMaxHit(),
																NPCCombatDefinitions.RANGE,
																t)));
									}
								}
								stop();
								return;
							}
						}
					}, 0, 0);
				} else {
					boss.setNextAnimation(new Animation(14949));
					World.sendProjectile(boss, target, 2853, 18, 18, 50, 50, 0,
							0);
					delayHit(
							boss,
							1,
							target,
							getRangeHit(
									boss,
									getMaxHit(boss, boss.getMaxHit(),
											NPCCombatDefinitions.RANGE, target)));
				}
			} else {
				if (Utils.random(7) == 0) {
					boss.setNextForceTalk(new ForceTalk("Magicinyaface!"));
					boss.playSoundEffect(3022);// MAGIC IN YA FACE
				}
				boss.setNextAnimation(new Animation(14956));
				boss.setNextGraphics(new Graphics(2854));
				target.setNextGraphics(new Graphics(2854, 5, 0));
				int damage = getMaxHit(boss, boss.getMaxHit(),
						NPCCombatDefinitions.MAGE, target);
				if (target instanceof Player) {
					if (((Player) target).getPrayer().isMageProtecting())
						damage *= .5D;
				}
				delayHit(boss, 1, target, getMagicHit(boss, damage));
			}
			return 5;
		} else {
			boss.setNextAnimation(new Animation(12310));
			delayHit(
					boss,
					0,
					target,
					getMeleeHit(
							boss,
							getMaxHit(boss, boss.getMaxHit(),
									NPCCombatDefinitions.MELEE, target)));
			return 4;
		}
	}
}
