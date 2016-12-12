package net.kagani.game.npc.vorago.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.vorago.VoragoHandler;
import net.kagani.game.npc.vorago.VoragoMinion;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

import javafx.scene.effect.Effect;

/***
 * 
 * @author AlexRSPS
 *
 */

public class VoragoCombat extends CombatScript {
	int RBDmg;
	static int x;
	static int y;
	static int z;

	@Override
	public Object[] getKeys() {
		return new Object[] { 17182, 17183, 17184 };
	}

	private void sendGField() {
		World.spawnObject(new WorldObject(84959, 10, 0, VoragoHandler.vorago.getRandomJump()));
		VoragoHandler.vorago.fieldCount++;
	}

	public static void sendWaterfall() {// TODO make this better
		VoragoHandler.vorago.setCantInteract(true);
		VoragoHandler.vorago.setNextWorldTile(VoragoHandler.vorago.getCentre());
		VoragoHandler.vorago.getCombat().removeTarget();
		// for each place it sets the safe tiles i.e those behind the waterfall
		switch (Utils.random(3)) {
		case 0:// North-West
			x = VoragoHandler.vorago.Centre[3].getX() - 12;
			y = VoragoHandler.vorago.Centre[3].getY() + 8;
			z = VoragoHandler.vorago.Centre[3].getPlane();
			VoragoHandler.vorago.safeTiles[0] = new WorldTile(x, y + 1, z);
			VoragoHandler.vorago.safeTiles[1] = new WorldTile(x, y + 2, z);
			VoragoHandler.vorago.safeTiles[2] = new WorldTile(x, y + 3, z);
			VoragoHandler.vorago.safeTiles[3] = new WorldTile(x + 1, y + 2, z);
			VoragoHandler.vorago.safeTiles[4] = new WorldTile(x + 1, y + 3, z);
			VoragoHandler.vorago.safeTiles[5] = new WorldTile(x + 2, y + 3, z);
			VoragoHandler.vorago.rotation = 0;
			VoragoHandler.vorago.wfTile = new WorldTile(x, y, z);
			break;
		case 1:// North-East
			x = VoragoHandler.vorago.Centre[3].getX() + 8;// 3048
			y = VoragoHandler.vorago.Centre[3].getY() + 8;// 5992
			z = VoragoHandler.vorago.Centre[3].getPlane();
			VoragoHandler.vorago.safeTiles[0] = new WorldTile(x + 1, y + 3, z);
			VoragoHandler.vorago.safeTiles[1] = new WorldTile(x + 2, y + 2, z);
			VoragoHandler.vorago.safeTiles[2] = new WorldTile(x + 2, y + 3, z);
			VoragoHandler.vorago.safeTiles[3] = new WorldTile(x + 3, y + 1, z);
			VoragoHandler.vorago.safeTiles[4] = new WorldTile(x + 3, y + 2, z);
			VoragoHandler.vorago.safeTiles[5] = new WorldTile(x + 3, y + 3, z);
			VoragoHandler.vorago.rotation = 1;
			VoragoHandler.vorago.wfTile = new WorldTile(x, y, z);
			break;
		case 2:// South-East
			x = VoragoHandler.vorago.Centre[3].getX() + 8;
			y = VoragoHandler.vorago.Centre[3].getY() - 12;
			z = VoragoHandler.vorago.Centre[3].getPlane();
			VoragoHandler.vorago.safeTiles[0] = new WorldTile(x + 1, y, z);
			VoragoHandler.vorago.safeTiles[1] = new WorldTile(x + 2, y, z);
			VoragoHandler.vorago.safeTiles[2] = new WorldTile(x + 3, y, z);
			VoragoHandler.vorago.safeTiles[3] = new WorldTile(x + 2, y + 1, z);
			VoragoHandler.vorago.safeTiles[4] = new WorldTile(x + 3, y + 1, z);
			VoragoHandler.vorago.safeTiles[5] = new WorldTile(x + 3, y + 2, z);
			VoragoHandler.vorago.wfTile = new WorldTile(x, y, z);
			VoragoHandler.vorago.rotation = 0;
			break;
		case 3:// South-West
			x = VoragoHandler.vorago.Centre[3].getX() - 12;
			y = VoragoHandler.vorago.Centre[3].getY() - 12;
			z = VoragoHandler.vorago.Centre[3].getPlane();
			VoragoHandler.vorago.safeTiles[0] = new WorldTile(x, y, z);
			VoragoHandler.vorago.safeTiles[1] = new WorldTile(x + 1, y, z);
			VoragoHandler.vorago.safeTiles[2] = new WorldTile(x + 2, y, z);
			VoragoHandler.vorago.safeTiles[3] = new WorldTile(x, y + 1, z);
			VoragoHandler.vorago.safeTiles[4] = new WorldTile(x, y + 2, z);
			VoragoHandler.vorago.safeTiles[5] = new WorldTile(x + 1, y + 1, z);
			VoragoHandler.vorago.rotation = 1;
			VoragoHandler.vorago.wfTile = new WorldTile(x, y, z);
			break;
		}
		for (Player p : VoragoHandler.getPlayers()) {
			(p).getPackets().sendGameMessage("<col=ff0000>Vorago starts to charge all his power into a massive fire attack!</col");
		}
		VoragoHandler.vorago.setNextAnimation(new Animation(20322));
		VoragoHandler.vorago.setNextGraphics(new Graphics(4014));
		VoragoHandler.vorago.wfCount++;
		VoragoHandler.vorago.safePlayers.removeAll(VoragoHandler.vorago.safePlayers);
		World.spawnObjectTemporary(new WorldObject(84967, 11, VoragoHandler.vorago.rotation, VoragoHandler.vorago.wfTile), 10200);// Spawns
																									// the
																									// waterfalls
																									// for
																									// 10.2s

		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				VoragoHandler.vorago.setNextAnimation(new Animation(20383));// TODO find actual
															// emote
				VoragoHandler.vorago.setNextGraphics(new Graphics(4013));
				for (Player p : VoragoHandler.getPlayers()) {

					VoragoHandler.vorago.setCantInteract(false);
					if (VoragoHandler.vorago.wfCount == 3) {
						(p).getPackets().sendGameMessage("<col=15ff00>The weapon part has come loose and hits the floor.</col");
					} else {
						(p).getPackets().sendGameMessage("<col=eaa605>Part of the weapon loosens as Vorago unleashes the attack.</col");
					}
					for (int i = 0; i < VoragoHandler.vorago.safeTiles.length; i++) {
						if (p.getX() == VoragoHandler.vorago.safeTiles[i].getX() && p.getY() == VoragoHandler.vorago.safeTiles[i].getY()) {
							VoragoHandler.vorago.safePlayers.add(p);
							break;
						}
					}
					if (!VoragoHandler.vorago.safePlayers.contains(p)) {
						p.applyHit(new Hit(VoragoHandler.vorago, Utils.random(900, 1000), HitLook.REGULAR_DAMAGE));
					}
				}
				if (VoragoHandler.vorago.wfCount == 3) {
					World.addGroundItem(new Item(28604), VoragoHandler.vorago.getRandomJump());
					VoragoHandler.vorago.canDie = true;
				}
				stop();
				return;
			}
		}, 17);

	}

	public void sendClones(Entity target) {
		Entity targets[] = VoragoHandler.getPlayers().toArray(new Player[VoragoHandler.getPlayers().size()]);
		VoragoHandler.vorago.Clonee = (Entity) targets[Utils.random(targets.length)];// picks a
																		// random
																		// target
		if (VoragoHandler.getPlayersCount() > 1) {
			for (int i = 0; i < targets.length; i++) {// Can't be the current
														// target
				if (VoragoHandler.vorago.Clonee == target) {
					if (i == 0) {
						VoragoHandler.vorago.Clonee = targets[i + 1];
						break;

					} else {
						VoragoHandler.vorago.Clonee = targets[i - 1];
						break;
					}
				}
			}
		}

		int combatStyle = ((Player) VoragoHandler.vorago.Clonee).getCombatDefinitions().getAttackStyle();
		if (combatStyle == Combat.MAGIC_TYPE) {
			VoragoHandler.vorago.stoneId = 17160;
		} else if (combatStyle == Combat.RANGE_TYPE) {
			VoragoHandler.vorago.stoneId = 17159;
		} else {// Melee clone
			VoragoHandler.vorago.stoneId = 17158;
		}
		VoragoMinion Stone = new VoragoMinion(VoragoHandler.vorago.stoneId, VoragoHandler.vorago.Clonee, -1, true, true);
		Stone.setName("Stone " + VoragoHandler.vorago.Clonee.getName());
		Stone.setTarget(VoragoHandler.vorago.Clonee);
		((Player) VoragoHandler.vorago.Clonee).getHintIconsManager().addHintIcon(Stone, 1, -1, false);
		VoragoHandler.vorago.cloneOut = true;
	}

	public void sendReflect(NPC npc) {
		if (VoragoHandler.vorago.getId() == 17182) {
			VoragoHandler.vorago.transformRago();
		}
		VoragoHandler.vorago.isReflecting = true;
		if (VoragoHandler.vorago.getPhase() == 2) {
			sendGField();
		}
		npc.setNextAnimation(new Animation(20319));
		npc.setNextGraphics(new Graphics(4011));
		Entity targets[] = VoragoHandler.getPlayers().toArray(new Player[VoragoHandler.getPlayers().size()]);
		VoragoHandler.vorago.Reflectee = (Entity) targets[Utils.random(targets.length)];// Picks
																		// random
																		// target
		VoragoHandler.vorago.Reflectee.setNextGraphics(new Graphics(4012));
		((Player) VoragoHandler.vorago.Reflectee).getPackets().sendGameMessage("<col=ff0000>Vorago channels incoming damage to you. Beware!</col>");
		for (Player p : VoragoHandler.getPlayers()) {
			if (p == VoragoHandler.vorago.Reflectee) {
				continue;
			} else {
				p.getPackets().sendGameMessage("<col=f0ff00>Vorago reflects incoming damage to surrounding foes!</col");
			}
		}
		WorldTasksManager.schedule(new WorldTask() {
			private int count = 0;

			@Override
			public void run() {
				if (count == 16) {
					if(VoragoHandler.vorago == null)
						return;
					VoragoHandler.vorago.Reflectee.setNextGraphics(new Graphics(2670));// TODO
					((Player) VoragoHandler.vorago.Reflectee).getPackets().sendGameMessage("<col=15ff00>Vorago releases his mental link with you.</col");
					VoragoHandler.vorago.isReflecting = false;
					stop();
					return;
				}
				count++;
			}
		}, 0, 1);
	}

	public void sendSmash(NPC npc, Entity target) {
		target.applyHit(new Hit(npc, 600, HitLook.REGULAR_DAMAGE));
		if (VoragoHandler.vorago.getPhase() == 5) {
			VoragoHandler.vorago.pushBackDamage += 600 * VoragoHandler.getPlayersCount();
		}
		npc.setNextAnimation(new Animation(20363));
		npc.setNextGraphics(new Graphics(4018));
		int dmg = Utils.random(1280) + 1;
		if (VoragoHandler.vorago.getPhase() == 5) {
			VoragoHandler.vorago.pushBackDamage += dmg * VoragoHandler.getPlayersCount();
		}
		if (VoragoHandler.vorago.smashCount == 2 && VoragoHandler.vorago.getPhase() < 5) {
			sendBlueBomb(VoragoHandler.vorago, false);
		}
	}

	public static void spawnScop() { // Spawns the scopulus
		VoragoHandler.scop1.setNextWorldTile(new WorldTile(VoragoHandler.vorago.Centre[2].getX() - 5, VoragoHandler.vorago.Centre[2].getY() - 8, VoragoHandler.vorago.Centre[2].getPlane()));
		VoragoHandler.scop2.setNextWorldTile(new WorldTile(VoragoHandler.vorago.Centre[2].getX() + 5, VoragoHandler.vorago.Centre[2].getY() - 8, VoragoHandler.vorago.Centre[2].getPlane()));
	}

	public static void sendBringHimDown(NPC npc) {
		if (!VoragoHandler.vorago.isDown) {
			VoragoHandler.vorago.bringDownTransform();
		}
	}

	public Entity getFarthest(NPC npc) {// Returns the farthest away player
		Entity targets[] = VoragoHandler.getPlayers().toArray(new Entity[VoragoHandler.getPlayersCount()]);
		int highest = Utils.getDistance(npc, targets[0]);
		int index = 0;
		for (int i = 1; i < targets.length; i++) {
			if (Utils.getDistance(npc, targets[i]) > highest) {
				highest = Utils.getDistance(npc, targets[i]);
				index = i;
			}
		}

		/*
		 * if (Utils.getDistance(rago, targets[index]) > 30) {//This removes the player from the handler if they're too far a controller will replace this VoragoHandler.removePlayer((Player) targets[index]);//Causes some issues return
		 * getFarthest(npc);//Recurses to find the new farthest player } else {
		 */
		return targets[index];
		// }
	}

	private void sendMeleeAttack(NPC npc, Entity target) {
		if (!Utils.isOnRange(npc, target, 2)) {// If its not in MD then send a
												// blue bomb
			sendBlueBomb(npc, true);
		} else {
			npc.setNextAnimation(new Animation(20355));
			for (Entity t : VoragoHandler.getPlayers()) {// Hits all players in
															// MD of rago
				if (Utils.isOnRange(npc, t, 2)) {
					Hit hit = getMeleeHit(npc, getMaxHit(npc, 5000, NPCCombatDefinitions.MELEE, t));
					delayHit(npc, 0, t, hit);
					if (VoragoHandler.vorago.getPhase() == 5) {
						VoragoHandler.vorago.pushBackDamage += (hit.getDamage());// Adds to P5
																	// push back
																	// count
					}
				}
			}
		}
	}

	private void sendBlueBomb(NPC npc, Boolean anim) {// Blue bombs are
														// sometimes sent
														// without animation
		Entity farthest = (Entity) getFarthest(npc);
		if (anim) {
			npc.setNextAnimation(new Animation(20356));
			npc.setNextGraphics(new Graphics(4015));
		}
		Projectile projectile = World.sendProjectile(npc, farthest, 4016, 90, 20, 10, 2, 0, 0);
		int cycleTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
				players.add((Player) farthest);
				Entity targets[] = VoragoHandler.getPlayers().toArray(new Entity[VoragoHandler.getPlayersCount()]);
				for (int i = 0; i < targets.length; i++) {
					if (targets[i] != farthest) {
						if (Utils.getDistance(farthest, targets[i]) <= 2) {
							players.add((Player) targets[i]);// Adds to players
																// that will get
																// hit
						}
					}
				}
				for (Entity t : players) {// Hits all players close to the
											// farthest player
					int damage = getMaxHit(npc, 4500, NPCCombatDefinitions.MAGE, t);
					delayHit(npc, 0, t, getMagicHit(npc, damage));
					t.setNextGraphics(new Graphics(4017));
					if (VoragoHandler.vorago.getPhase() == 5) {
						VoragoHandler.vorago.pushBackDamage += (damage * VoragoHandler.getPlayersCount());// Adds
																							// to
																							// the
																							// P5
																							// push
																							// back
																							// count
					}
				}
			}
		}, cycleTime);
	}

	private void sendRedBomb(NPC npc) {
		Entity farthest = (Entity) getFarthest(npc);
		npc.setNextAnimation(new Animation(20371));
		npc.setNextGraphics(new Graphics(4022));
		RBDmg = 2000;
		((Player) farthest).getPackets().sendGameMessage("<col=ff0000>Vorago has sent a bomb after you. Run!</col>");
		sendBlueBomb(npc, false);// Sends a blue bomb at the same time
		Projectile redBomb = World.sendProjectileNew(npc, farthest, 4023, 90, 20, 10, 1, 0, 0);
		int cycleTime = Utils.projectileTimeToCycles(redBomb.getEndTime()) - 1;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
				players.add((Player) farthest);
				Entity targets[] = VoragoHandler.getPlayers().toArray(new Entity[VoragoHandler.getPlayersCount()]);
				for (int i = 0; i < targets.length; i++) {
					if (targets[i] != farthest) {
						if (Utils.getDistance(farthest, targets[i]) <= 4) {
							RBDmg = RBDmg + 1000;// Adds 1k damage for all
													// players close to farthest
							if (Utils.getDistance(farthest, targets[i]) <= 2) {
								players.add((Player) targets[i]);// Adds to
																	// players
																	// that will
																	// get hit
							}
						}
					}
				}
				for (Entity t : players) {// Hits all players close to the
											// farthest
					delayHit(npc, 0, t, getRegularHit(npc, RBDmg));
					t.setNextGraphics(new Graphics(4024));
					if (VoragoHandler.vorago.getPhase() == 5) {
						VoragoHandler.vorago.pushBackDamage += (RBDmg * VoragoHandler.getPlayersCount());
					}
				}

			}
		}, cycleTime);

	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		if (VoragoHandler.vorago.special) {// Handles all the special attacks
			switch (VoragoHandler.vorago.getPhase()) {
			case 1:// No special in P1
				return 7;
			case 2:
				switch (VoragoHandler.vorago.specialType) {
				case 0:// Smash
					if (VoragoHandler.vorago.getId() == 17183) {
						VoragoHandler.vorago.transformRago();
					}
					sendSmash(npc, target);
					VoragoHandler.vorago.smashCount++;
					if (VoragoHandler.vorago.smashCount == 5) {
						VoragoHandler.vorago.special = false;
						VoragoHandler.vorago.smashCount = 0;
						VoragoHandler.vorago.specialType = 1;
						VoragoHandler.vorago.moves = 0;
					}
					return 7;
				case 1:// Reflect
					VoragoHandler.vorago.special = false;
					VoragoHandler.vorago.specialType = 3;
					sendReflect(npc);
					break;
				case 2:// Bring Him Down
					sendBringHimDown(npc);
					VoragoHandler.vorago.special = false;
					return 0;
				}
				return 7;
			case 3:
				return 7;
			case 4:
				if (VoragoHandler.vorago.wfCount < 3) {
					switch (VoragoHandler.vorago.specialType) {
					case 0:// Waterfalls
						if (npc.getId() == 17182) {
							VoragoHandler.vorago.transformRago();
						}
						sendWaterfall();
						VoragoHandler.vorago.special = false;
						if (VoragoHandler.vorago.wfCount < 3) {
							VoragoHandler.vorago.specialType = 1;
						} else {
							VoragoHandler.vorago.specialType = 0;
						}
						VoragoHandler.vorago.moves = 0;
						break;
					case 1:// Clones
						VoragoHandler.vorago.special = false;
						VoragoHandler.vorago.specialType = 4;
						sendClones(target);
						break;
					case 2:// Smashes
						if (VoragoHandler.vorago.getId() == 17183) {
							VoragoHandler.vorago.transformRago();
						}
						sendSmash(npc, target);
						VoragoHandler.vorago.smashCount++;

						if (VoragoHandler.vorago.smashCount == 5) {
							VoragoHandler.vorago.special = false;
							VoragoHandler.vorago.smashCount = 0;
							VoragoHandler.vorago.specialType = 3;
							VoragoHandler.vorago.moves = 0;
						}
						return 7;
					case 3:// Reflect
						VoragoHandler.vorago.special = false;
						VoragoHandler.vorago.specialType = 0;
						sendReflect(npc);
						break;
					}
				} else {
					switch (VoragoHandler.vorago.specialType) {
					case 0:// Clones
						VoragoHandler.vorago.special = false;
						VoragoHandler.vorago.specialType = 3;
						sendClones(target);
						break;
					case 1:// Smashes
						if (VoragoHandler.vorago.getId() == 17183) {
							VoragoHandler.vorago.transformRago();
						}
						sendSmash(npc, target);
						VoragoHandler.vorago.smashCount++;

						if (VoragoHandler.vorago.smashCount == 5) {
							VoragoHandler.vorago.special = false;
							VoragoHandler.vorago.smashCount = 0;
							VoragoHandler.vorago.specialType = 2;
							VoragoHandler.vorago.moves = 0;
						}
						return 7;
					case 2:// Reflect
						VoragoHandler.vorago.special = false;
						VoragoHandler.vorago.specialType = 0;
						sendReflect(npc);
						break;
					}
				}
				return 7;
			case 5:
				switch (VoragoHandler.vorago.specialType) {// NOTE: In current Vorago, phase 5
											// of Scopulus has purple bomb
				case 0:// Reflect
					VoragoHandler.vorago.special = false;
					VoragoHandler.vorago.specialType = 1;
					sendReflect(npc);
					break;
				case 1:// Smash
					sendSmash(npc, target);
					VoragoHandler.vorago.smashCount++;
					if (VoragoHandler.vorago.smashCount == 5) {
						VoragoHandler.vorago.special = false;
						VoragoHandler.vorago.smashCount = 0;
						VoragoHandler.vorago.specialType = 2;
						VoragoHandler.vorago.moves = 0;
					}
					return 7;

				}
				return 7;
			}
		} else {
			if (npc.getId() == 17184) {

			} else {
				VoragoHandler.vorago.moves++;
				if (VoragoHandler.vorago.week == 2 && VoragoHandler.vorago.getPhase() == 3) {
					sendBlueBomb(npc, true);
				} else {

					if (VoragoHandler.vorago.redBomb) {// Red Bombs

						VoragoHandler.vorago.redBomb = false;
						VoragoHandler.vorago.moves = 0;
						sendRedBomb(npc);
					} else {
						if (Utils.random(2) == 0) { // Blue Bombs
							sendBlueBomb(npc, true);
						} else { // melee attack
							sendMeleeAttack(npc, target);
						}
					}
				}

				switch (VoragoHandler.vorago.getPhase()) {// Move counting
				case 1:
					if (VoragoHandler.vorago.moves == 5) {
						VoragoHandler.vorago.redBomb = true;
					}
					break;
				case 2:
					if (VoragoHandler.vorago.fieldCount < 4) {
						switch (VoragoHandler.vorago.specialType) {
						case 0:// Smashes
							if (VoragoHandler.vorago.moves == 4) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
						case 1:// Reflect
							if (VoragoHandler.vorago.moves == 3) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
						case 2:// Not handled in here
							break;
						case 3:// Red Bombs
							if (VoragoHandler.vorago.moves == 3) {
								VoragoHandler.vorago.redBomb = true;
								VoragoHandler.vorago.moves = 0;
								VoragoHandler.vorago.specialType = 4;
								break;
							}
						case 4:
							if (VoragoHandler.vorago.moves == 4) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.specialType = 0;
								VoragoHandler.vorago.moves = 0;
								break;
							}
						}
					} else {
						if (VoragoHandler.vorago.moves == 4) {
							VoragoHandler.vorago.redBomb = true;
							VoragoHandler.vorago.moves = 0;
						}
					}
					break;
				case 3:// Only done scop so no specials in p2
					break;
				case 4:
					if (VoragoHandler.vorago.wfCount < 3) {
						switch (VoragoHandler.vorago.specialType) {
						case 0:// Waterfalls
							if (VoragoHandler.vorago.moves == 3) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
							break;
						case 1:// Stone Clones
							if (VoragoHandler.vorago.moves == 7) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
							break;
						case 2:// Smashes
							if (VoragoHandler.vorago.moves == 4) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
							break;
						case 3:// Reflect
							if (VoragoHandler.vorago.moves == 3) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
							break;
						case 4:// Red Bomb
							if (VoragoHandler.vorago.moves == 3) {
								VoragoHandler.vorago.redBomb = true;
								VoragoHandler.vorago.moves = 0;
								VoragoHandler.vorago.specialType = 2;
								break;
							}
							break;
						}
					} else {// Skips waterfalls
						switch (VoragoHandler.vorago.specialType) {
						case 0:// Stone Clones
							if (VoragoHandler.vorago.moves == 7) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
							break;
						case 1:// Smashes
							if (VoragoHandler.vorago.moves == 4) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
							break;
						case 2:// Reflect
							if (VoragoHandler.vorago.moves == 3) {
								VoragoHandler.vorago.special = true;
								VoragoHandler.vorago.moves = 0;
								break;
							}
							break;
						case 3:// Red Bomb
							if (VoragoHandler.vorago.moves == 3) {
								VoragoHandler.vorago.redBomb = true;
								VoragoHandler.vorago.moves = 0;
								VoragoHandler.vorago.specialType = 1;
								break;
							}
							break;
						}
					}
					break;
				case 5:
					switch (VoragoHandler.vorago.specialType) {
					case 0:// Reflects
						if (VoragoHandler.vorago.moves == 5) {
							VoragoHandler.vorago.special = true;
							VoragoHandler.vorago.moves = 0;
							break;
						}
					case 1:// Smashes
						if (VoragoHandler.vorago.moves == 3) {
							VoragoHandler.vorago.special = true;
							VoragoHandler.vorago.moves = 0;
							break;
						}
					case 2:// Red Bombs
						if (VoragoHandler.vorago.moves == 3) {
							VoragoHandler.vorago.redBomb = true;
							VoragoHandler.vorago.moves = 0;
							VoragoHandler.vorago.specialType = 0;
							break;
						}
						break;
					}
				}

			}
		}
		return 7;// 7 ticks between each attack
	}

}