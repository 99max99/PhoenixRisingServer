package net.kagani.game.npc.combat.impl.vorago;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.CombatScript;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.vorago.Vorago;
import net.kagani.game.npc.vorago.VoragoHandler;
import net.kagani.game.npc.vorago.VoragoMinion;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class VoragoCombat extends CombatScript {
	static Vorago rago = VoragoHandler.vorago;
	static VoragoMinion S1 = VoragoHandler.scop1;
	static VoragoMinion S2 = VoragoHandler.scop2;
	int RBDmg;
	static int x;
	static int y;
	static int z;

	@Override
	public Object[] getKeys() {
		return new Object[] { 17182, 17183, 17184 };
	}

	private void sendGField() {
		World.spawnObject(new WorldObject(84959, 10, 0, VoragoHandler.vorago
				.getRandomJump()));
		rago.fieldCount++;
	}

	public static void sendWaterfall() {// TODO make this better
		rago.setCantInteract(true);
		rago.setNextWorldTile(rago.getCentre());
		rago.getCombat().removeTarget();
		// for each place it sets the safe tiles i.e those behind the waterfall
		switch (Utils.random(3)) {
		case 0:// North-West
			x = rago.Centre[3].getX() - 12;
			y = rago.Centre[3].getY() + 8;
			z = rago.Centre[3].getPlane();
			rago.safeTiles[0] = new WorldTile(x, y + 1, z);
			rago.safeTiles[1] = new WorldTile(x, y + 2, z);
			rago.safeTiles[2] = new WorldTile(x, y + 3, z);
			rago.safeTiles[3] = new WorldTile(x + 1, y + 2, z);
			rago.safeTiles[4] = new WorldTile(x + 1, y + 3, z);
			rago.safeTiles[5] = new WorldTile(x + 2, y + 3, z);
			rago.rotation = 0;
			rago.wfTile = new WorldTile(x, y, z);
			break;
		case 1:// North-East
			x = rago.Centre[3].getX() + 8;// 3048
			y = rago.Centre[3].getY() + 8;// 5992
			z = rago.Centre[3].getPlane();
			rago.safeTiles[0] = new WorldTile(x + 1, y + 3, z);
			rago.safeTiles[1] = new WorldTile(x + 2, y + 2, z);
			rago.safeTiles[2] = new WorldTile(x + 2, y + 3, z);
			rago.safeTiles[3] = new WorldTile(x + 3, y + 1, z);
			rago.safeTiles[4] = new WorldTile(x + 3, y + 2, z);
			rago.safeTiles[5] = new WorldTile(x + 3, y + 3, z);
			rago.rotation = 1;
			rago.wfTile = new WorldTile(x, y, z);
			break;
		case 2:// South-East
			x = rago.Centre[3].getX() + 8;
			y = rago.Centre[3].getY() - 12;
			z = rago.Centre[3].getPlane();
			rago.safeTiles[0] = new WorldTile(x + 1, y, z);
			rago.safeTiles[1] = new WorldTile(x + 2, y, z);
			rago.safeTiles[2] = new WorldTile(x + 3, y, z);
			rago.safeTiles[3] = new WorldTile(x + 2, y + 1, z);
			rago.safeTiles[4] = new WorldTile(x + 3, y + 1, z);
			rago.safeTiles[5] = new WorldTile(x + 3, y + 2, z);
			rago.wfTile = new WorldTile(x, y, z);
			rago.rotation = 0;
			break;
		case 3:// South-West
			x = rago.Centre[3].getX() - 12;
			y = rago.Centre[3].getY() - 12;
			z = rago.Centre[3].getPlane();
			rago.safeTiles[0] = new WorldTile(x, y, z);
			rago.safeTiles[1] = new WorldTile(x + 1, y, z);
			rago.safeTiles[2] = new WorldTile(x + 2, y, z);
			rago.safeTiles[3] = new WorldTile(x, y + 1, z);
			rago.safeTiles[4] = new WorldTile(x, y + 2, z);
			rago.safeTiles[5] = new WorldTile(x + 1, y + 1, z);
			rago.rotation = 1;
			rago.wfTile = new WorldTile(x, y, z);
			break;
		}
		for (Player p : VoragoHandler.getPlayers()) {
			(p).getPackets()
					.sendGameMessage(
							"<col=ff0000>Vorago starts to charge all his power into a massive fire attack!</col");
		}
		rago.setNextAnimation(new Animation(20322));
		rago.setNextGraphics(new Graphics(4014));
		rago.wfCount++;
		rago.safePlayers.removeAll(rago.safePlayers);
		World.spawnObjectTemporary(new WorldObject(84967, 11, rago.rotation,
				rago.wfTile), 10200);// Spawns
		// the
		// waterfalls
		// for
		// 10.2s

		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				rago.setNextAnimation(new Animation(20383));// TODO find actual
				// emote
				rago.setNextGraphics(new Graphics(4013));
				for (Player p : VoragoHandler.getPlayers()) {

					rago.setCantInteract(false);
					if (rago.wfCount == 3) {
						(p).getPackets()
								.sendGameMessage(
										"<col=15ff00>The weapon part has come loose and hits the floor.</col");
					} else {
						(p).getPackets()
								.sendGameMessage(
										"<col=eaa605>Part of the weapon loosens as Vorago unleashes the attack.</col");
					}
					for (int i = 0; i < rago.safeTiles.length; i++) {
						if (p.getX() == rago.safeTiles[i].getX()
								&& p.getY() == rago.safeTiles[i].getY()) {
							rago.safePlayers.add(p);
							break;
						}
					}
					if (!rago.safePlayers.contains(p)) {// If the player isn't
						// behind the waterfall
						// they get hit between
						// 9 and 10k
						p.applyHit(new Hit(rago, Utils.random(9000, 10000),
								HitLook.REGULAR_DAMAGE));
					}
				}
				if (rago.wfCount == 3) {
					World.addGroundItem(new Item(28604), rago.getRandomJump());// Adds
					// the
					// final
					// weapon
					// piece
					rago.canDie = true;
				}
				stop();
				return;
			}
		}, 17);

	}

	public void sendClones(Entity target) {
		Entity targets[] = VoragoHandler.getPlayers().toArray(
				new Player[VoragoHandler.getPlayers().size()]);
		rago.Clonee = targets[Utils.random(targets.length)];// picks a random
															// target
		if (VoragoHandler.getPlayersCount() > 1) {
			for (int i = 0; i < targets.length; i++) {// Can't be the current
														// target
				if (rago.Clonee == target) {
					if (i == 0) {
						rago.Clonee = targets[i + 1];
						break;

					} else {
						rago.Clonee = targets[i - 1];
						break;
					}
				}
			}
		}

		int combatStyle = ((Player) rago.Clonee).getCombatDefinitions()
				.getType(Equipment.SLOT_WEAPON);// Spawns
												// clone
												// based
												// on
												// what
												// combat
												// style
												// player
												// is
												// using
		if (combatStyle == Combat.MAGIC_TYPE) {
			rago.stoneId = 17160;
		} else if (combatStyle == Combat.RANGE_TYPE) {
			rago.stoneId = 17159;
		} else {// Melee clone
			rago.stoneId = 17158;
		}
		VoragoMinion Stone = new VoragoMinion(rago.stoneId, rago.Clonee, -1,
				true, true);
		Stone.setName("Stone " + rago.Clonee.getName());
		Stone.setTarget(rago.Clonee);
		((Player) rago.Clonee).getHintIconsManager().addHintIcon(Stone, 1, -1,
				false);
		rago.cloneOut = true;
	}

	public void sendReflect(NPC npc) {

		if (rago.getId() == 17182) {
			rago.transformRago();
		}
		rago.isReflecting = true;
		if (rago.getPhase() == 2) {
			sendGField();
		}
		npc.setNextAnimation(new Animation(20319));
		npc.setNextGraphics(new Graphics(4011));
		Entity targets[] = VoragoHandler.getPlayers().toArray(
				new Player[VoragoHandler.getPlayers().size()]);
		rago.Reflectee = targets[Utils.random(targets.length)];// Picks random
																// target
		rago.Reflectee.setNextGraphics(new Graphics(4012));
		((Player) rago.Reflectee)
				.getPackets()
				.sendGameMessage(
						"<col=ff0000>Vorago channels incoming damage to you. Beware!</col>");
		for (Player p : VoragoHandler.getPlayers()) {
			if (p == rago.Reflectee) {
				continue;
			} else {
				p.getPackets()
						.sendGameMessage(
								"<col=f0ff00>Vorago reflects incoming damage to surrounding foes!</col");
			}
		}
		WorldTasksManager.schedule(new WorldTask() {
			private int count = 0;

			@Override
			public void run() {
				if (count == 16) {
					rago.Reflectee.setNextGraphics(new Graphics(2670));// TODO
																		// find
																		// correct
																		// gfx
					((Player) rago.Reflectee)
							.getPackets()
							.sendGameMessage(
									"<col=15ff00>Vorago releases his mental link with you.</col");
					rago.isReflecting = false;
					stop();
					return;
				}
				count++;
			}
		}, 0, 1);
	}

	public void sendSmash(NPC npc, Entity target) {
		if (target.getEffectsManager().hasActiveEffect(EffectType.DISMEMBER)) {// If
																				// the
																				// player
																				// is
																				// currently
																				// bleeding
																				// they
																				// get
																				// hit
																				// 6k
			target.applyHit(new Hit(npc, 6000, HitLook.REGULAR_DAMAGE));
			if (rago.getPhase() == 5) {
				rago.pushBackDamage += 6000 * VoragoHandler.getPlayersCount();// Adds
																				// to
																				// the
																				// P5
																				// pushback
																				// count
			}
		}
		npc.setNextAnimation(new Animation(20363));
		npc.setNextGraphics(new Graphics(4018));
		int dmg = Utils.random(1280) + 1;
		target.getEffectsManager()
				.startEffect(
						new Effect(EffectType.DISMEMBER, 10,
								HitLook.MELEE_DAMAGE, new Graphics(3464), dmg,
								2, npc, new WorldTile(target)));
		if (rago.getPhase() == 5) {
			rago.pushBackDamage += dmg * VoragoHandler.getPlayersCount();// Adds
																			// to
																			// the
																			// P5
																			// pushback
																			// count
		}
		if (rago.smashCount == 2 && rago.getPhase() < 5) {
			sendBlueBomb(rago, false);
		}
	}

	public static void spawnScop() { // Spawns the scopulus
		S1.setNextWorldTile(new WorldTile(rago.Centre[2].getX() - 5,
				rago.Centre[2].getY() - 8, rago.Centre[2].getPlane()));
		S2.setNextWorldTile(new WorldTile(rago.Centre[2].getX() + 5,
				rago.Centre[2].getY() - 8, rago.Centre[2].getPlane()));
	}

	public static void sendBringHimDown(NPC npc) {
		if (!rago.isDown) {
			rago.bringDownTransform();
		}
	}

	public Entity getFarthest(NPC npc) {// Returns the farthest away player
		Entity targets[] = VoragoHandler.getPlayers().toArray(
				new Entity[VoragoHandler.getPlayersCount()]);
		int highest = Utils.getDistance(npc, targets[0]);
		int index = 0;
		for (int i = 1; i < targets.length; i++) {
			if (Utils.getDistance(npc, targets[i]) > highest) {
				highest = Utils.getDistance(npc, targets[i]);
				index = i;
			}
		}

		/*
		 * if (Utils.getDistance(rago, targets[index]) > 30) {//This removes the
		 * player from the handler if they're too far a controller will replace
		 * this VoragoHandler.removePlayer((Player) targets[index]);//Causes
		 * some issues return getFarthest(npc);//Recurses to find the new
		 * farthest player } else {
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
					Hit hit = getMeleeHit(npc,
							getMaxHit(npc, 5000, NPCCombatDefinitions.MELEE, t));
					delayHit(npc, 0, t, hit);
					if (rago.getPhase() == 5) {
						rago.pushBackDamage += (hit.getDamage());// Adds to P5
						// push back
						// count
					}
				}
			}
		}
	}

	private void sendBlueBomb(NPC npc, Boolean anim) {// Blue bombs are
		// sometimes sent without
		// animation
		Entity farthest = getFarthest(npc);
		if (anim) {
			npc.setNextAnimation(new Animation(20356));
			npc.setNextGraphics(new Graphics(4015));
		}
		Projectile projectile = World.sendProjectile(npc, farthest, 4016, 90,
				20, 10, 2, 0, 0);
		int cycleTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				List<Player> players = Collections
						.synchronizedList(new ArrayList<Player>());
				players.add((Player) farthest);
				Entity targets[] = VoragoHandler.getPlayers().toArray(
						new Entity[VoragoHandler.getPlayersCount()]);
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
					int damage = getMaxHit(npc, 4500,
							NPCCombatDefinitions.MAGE, t);
					delayHit(npc, 0, t, getMagicHit(npc, damage));
					t.setNextGraphics(new Graphics(4017));
					if (rago.getPhase() == 5) {
						rago.pushBackDamage += (damage * VoragoHandler
								.getPlayersCount());// Adds
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
		Entity farthest = getFarthest(npc);
		npc.setNextAnimation(new Animation(20371));
		npc.setNextGraphics(new Graphics(4022));
		RBDmg = 2000;
		((Player) farthest).getPackets().sendGameMessage(
				"<col=ff0000>Vorago has sent a bomb after you. Run!</col>");
		sendBlueBomb(npc, false);// Sends a blue bomb at the same time
		Projectile redBomb = World.sendProjectileNew(npc, farthest, 4023, 90,
				20, 10, 1, 0, 0);
		int cycleTime = Utils.projectileTimeToCycles(redBomb.getEndTime()) - 1;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				List<Player> players = Collections
						.synchronizedList(new ArrayList<Player>());
				players.add((Player) farthest);
				Entity targets[] = VoragoHandler.getPlayers().toArray(
						new Entity[VoragoHandler.getPlayersCount()]);
				for (int i = 0; i < targets.length; i++) {
					if (targets[i] != farthest) {
						if (Utils.getDistance(farthest, targets[i]) <= 4) {
							RBDmg = RBDmg + 1000;// Adds 1k damage for all
							// players close to farthest
							if (Utils.getDistance(farthest, targets[i]) <= 2) {
								players.add((Player) targets[i]);// Adds to
								// players that
								// will get hit
							}
						}
					}
				}
				for (Entity t : players) {// Hits all players close to the
					// farthest
					delayHit(npc, 0, t, getRegularHit(npc, RBDmg));
					t.setNextGraphics(new Graphics(4024));
					if (rago.getPhase() == 5) {
						rago.pushBackDamage += (RBDmg * VoragoHandler
								.getPlayersCount());
					}
				}

			}
		}, cycleTime);

	}

	@Override
	public int attack(final NPC npc, final Entity target) {

		if (rago.special) {// Handles all the special attacks
			switch (rago.getPhase()) {
			case 1:// No special in P1
				return 7;
			case 2:
				switch (rago.specialType) {
				case 0:// Smash
					if (rago.getId() == 17183) {
						rago.transformRago();
					}
					sendSmash(npc, target);
					rago.smashCount++;
					if (rago.smashCount == 5) {
						rago.special = false;
						rago.smashCount = 0;
						rago.specialType = 1;
						rago.moves = 0;
					}
					return 7;
				case 1:// Reflect
					rago.special = false;
					rago.specialType = 3;
					sendReflect(npc);
					break;
				case 2:// Bring Him Down
					sendBringHimDown(npc);
					rago.special = false;
					return 0;
				}
				return 7;
			case 3:
				return 7;
			case 4:
				if (rago.wfCount < 3) {
					switch (rago.specialType) {
					case 0:// Waterfalls
						if (npc.getId() == 17182) {
							rago.transformRago();
						}
						sendWaterfall();
						rago.special = false;
						if (rago.wfCount < 3) {
							rago.specialType = 1;
						} else {
							rago.specialType = 0;
						}
						rago.moves = 0;
						break;
					case 1:// Clones
						rago.special = false;
						rago.specialType = 4;
						sendClones(target);
						break;
					case 2:// Smashes
						if (rago.getId() == 17183) {
							rago.transformRago();
						}
						sendSmash(npc, target);
						rago.smashCount++;

						if (rago.smashCount == 5) {
							rago.special = false;
							rago.smashCount = 0;
							rago.specialType = 3;
							rago.moves = 0;
						}
						return 7;
					case 3:// Reflect
						rago.special = false;
						rago.specialType = 0;
						sendReflect(npc);
						break;
					}
				} else {
					switch (rago.specialType) {
					case 0:// Clones
						rago.special = false;
						rago.specialType = 3;
						sendClones(target);
						break;
					case 1:// Smashes
						if (rago.getId() == 17183) {
							rago.transformRago();
						}
						sendSmash(npc, target);
						rago.smashCount++;

						if (rago.smashCount == 5) {
							rago.special = false;
							rago.smashCount = 0;
							rago.specialType = 2;
							rago.moves = 0;
						}
						return 7;
					case 2:// Reflect
						rago.special = false;
						rago.specialType = 0;
						sendReflect(npc);
						break;
					}
				}
				return 7;
			case 5:
				switch (rago.specialType) {// NOTE: In current Vorago, phase 5
				// of Scopulus has purple bomb
				case 0:// Reflect
					rago.special = false;
					rago.specialType = 1;
					sendReflect(npc);
					break;
				case 1:// Smash
					sendSmash(npc, target);
					rago.smashCount++;
					if (rago.smashCount == 5) {
						rago.special = false;
						rago.smashCount = 0;
						rago.specialType = 2;
						rago.moves = 0;
					}
					return 7;

				}
				return 7;
			}
		} else {
			if (npc.getId() == 17184) {

			} else {
				rago.moves++;
				if (rago.week == 2 && rago.getPhase() == 3) {
					sendBlueBomb(npc, true);
				} else {

					if (rago.redBomb) {// Red Bombs

						rago.redBomb = false;
						rago.moves = 0;
						sendRedBomb(npc);
					} else {
						if (Utils.random(2) == 0) { // Blue Bombs
							sendBlueBomb(npc, true);
						} else { // melee attack
							sendMeleeAttack(npc, target);
						}
					}
				}

				switch (rago.getPhase()) {// Move counting
				case 1:
					if (rago.moves == 5) {
						rago.redBomb = true;
					}
					break;
				case 2:
					if (rago.fieldCount < 4) {
						switch (rago.specialType) {
						case 0:// Smashes
							if (rago.moves == 4) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
						case 1:// Reflect
							if (rago.moves == 3) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
						case 2:// Not handled in here
							break;
						case 3:// Red Bombs
							if (rago.moves == 3) {
								rago.redBomb = true;
								rago.moves = 0;
								rago.specialType = 4;
								break;
							}
						case 4:
							if (rago.moves == 4) {
								rago.special = true;
								rago.specialType = 0;
								rago.moves = 0;
								break;
							}
						}
					} else {
						if (rago.moves == 4) {
							rago.redBomb = true;
							rago.moves = 0;
						}
					}
					break;
				case 3:// Only done scop so no specials in p2
					break;
				case 4:
					if (rago.wfCount < 3) {
						switch (rago.specialType) {
						case 0:// Waterfalls
							if (rago.moves == 3) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
							break;
						case 1:// Stone Clones
							if (rago.moves == 7) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
							break;
						case 2:// Smashes
							if (rago.moves == 4) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
							break;
						case 3:// Reflect
							if (rago.moves == 3) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
							break;
						case 4:// Red Bomb
							if (rago.moves == 3) {
								rago.redBomb = true;
								rago.moves = 0;
								rago.specialType = 2;
								break;
							}
							break;
						}
					} else {// Skips waterfalls
						switch (rago.specialType) {
						case 0:// Stone Clones
							if (rago.moves == 7) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
							break;
						case 1:// Smashes
							if (rago.moves == 4) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
							break;
						case 2:// Reflect
							if (rago.moves == 3) {
								rago.special = true;
								rago.moves = 0;
								break;
							}
							break;
						case 3:// Red Bomb
							if (rago.moves == 3) {
								rago.redBomb = true;
								rago.moves = 0;
								rago.specialType = 1;
								break;
							}
							break;
						}
					}
					break;
				case 5:
					switch (rago.specialType) {
					case 0:// Reflects
						if (rago.moves == 5) {
							rago.special = true;
							rago.moves = 0;
							break;
						}
					case 1:// Smashes
						if (rago.moves == 3) {
							rago.special = true;
							rago.moves = 0;
							break;
						}
					case 2:// Red Bombs
						if (rago.moves == 3) {
							rago.redBomb = true;
							rago.moves = 0;
							rago.specialType = 0;
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