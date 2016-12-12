
package net.kagani.game;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.godwars.zaros.Nex;
import net.kagani.game.npc.kalphite.KalphiteKing;
import net.kagani.game.npc.others.MirrorBackSpider;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.actions.PlayerCombatNew;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.Drinkables;
import net.kagani.utils.Utils;

public class EffectsManager implements Serializable {

	private static final long serialVersionUID = -5884310017906704149L;

	private transient Entity entity;
	private transient boolean isPlayer;

	private List<Effect> effects = new CopyOnWriteArrayList<Effect>();

	public EffectsManager() {}

	public void setEntity(Entity entity) {
		this.entity = entity;
		this.isPlayer = entity instanceof Player;
	}

	public Entity getEntity() {
		return entity;
	}

	public void startEffect(Effect effect) {
		EffectType type = effect.type;
		if (!type.canStartEffect(effect, entity))
			return;
		Effect currentEffect = getEffectForType(type);
		if (currentEffect != null)
			effects.set(effects.indexOf(currentEffect), effect);
		else
			effects.add(effect);
		refreshBuffEffect(effect);
	}

	public void processEffects() {
		if (entity.isDead() || entity.hasFinished() || effects.isEmpty())
			return;
		for (Effect effect : effects) {
			int action = effect.type.getAction();
			if (effect.cycle != -1) {
				effect.cycle--; 
				if (effect.cycle == 0) {
					removeEffect(effect.type);
					continue;
				}
			}
			//already refreshes at both remove and add
			if (isPlayer)
				processBuffTimer(effect, false);
			if (action == BUFF) {
				if (isPlayer) {
					Player player = (Player) entity;
					if (effect.type == EffectType.OVERLOAD) {
						if (effect.cycle % 25 == 0)
							Drinkables.applyOverLoadEffect(player);
					}
					else if (effect.type == EffectType.PRAYER_RENEWAL) {
						if (effect.cycle == 50)
							player.getPackets().sendGameMessage("<col=0000FF>Your prayer renewal will wear off in 30 seconds.");
						if (!player.getPrayer().hasFullPrayerpoints()) {
							if (effect.cycle % getRenewalTime(player) == 0)
								player.getPrayer().restorePrayer(1);
							if (effect.cycle % 25 == 0)
								player.setNextGraphics(new Graphics(1295));
						}
					}else if (effect.type == EffectType.REGENERATE &&(player.isUnderCombat() || player.getCombatDefinitions().getSpecialAttackPercentage() <= 5)) {
						removeEffect(effect.type);
					}else if (effect.type == EffectType.BONFIRE) {
						if (effect.cycle == 500)
							player.getPackets().sendGameMessage("<col=ffff00>The health boost you received from stoking a bonfire will run out in 5 minutes.");
					}
					else if (effect.type == EffectType.FIRE_IMMUNITY || effect.type == EffectType.SUPER_FIRE_IMMUNITY) {
						if (effect.cycle == (effect.type == EffectType.FIRE_IMMUNITY ? 10 : 20))
							player.getPackets().sendGameMessage("<col=480000>Your resistance to dragonfire is about to run out.</col>");
					}
					else if (effect.type == EffectType.WEAPON_POISON) {
						if (player.isUnderCombat() && player.getEquipment().hasOffHand())
							effect.cycle--;//Duel wielding makes it go twice as fast.
					}
					else if (effect.type == EffectType.METAMORPHISIS
							|| effect.type == EffectType.DEATHS_SWIFTNESS || effect.type == EffectType.SUNSHINE) {
						if (player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON) != (effect.type == EffectType.DEATHS_SWIFTNESS ? Combat.RANGE_TYPE : Combat.MAGIC_TYPE))
							removeEffect(effect.type);
						if (effect.type == EffectType.METAMORPHISIS) {
							if (effect.cycle == 33) {
								int type = (int) effect.args[0];
								player.getAppearence().transformIntoNPC(type == Combat.TYPE_EARTH ? 15983 : type == Combat.TYPE_AIR ? 15984 : type == Combat.TYPE_WATER ? 15982 : 15981);
							}
						} else if (effect.type == EffectType.DEATHS_SWIFTNESS || effect.type == EffectType.SUNSHINE) {
							if (effect.cycle < 46 && effect.cycle % 4 == 0) {
								int mainHandDamage = player.getCombatDefinitions().getHandDamage(false) / 10, damage = (int) Utils.random(mainHandDamage * 0.10, mainHandDamage * 0.20);
								Entity target = player.getCombatDefinitions().getCurrentTarget();
								if (target == null || !Utils.isOnRange((WorldTile) effect.args[0], target, 0, 3, target.getSize()))
									return;
								Hit hit = new Hit(entity, damage, effect.type == EffectType.DEATHS_SWIFTNESS ? HitLook.RANGE_DAMAGE : HitLook.MAGIC_DAMAGE);
								hit.setAbilityMark();
								target.applyHit(hit);
								PlayerCombatNew.autoRelatie(player, target);
							}
						}
					} else if (effect.type == EffectType.ICE_ASYLUM) {
						if (effect.cycle % 7 == 0) {
							List<Integer> playerIndexes = World.getRegion(player.getRegionId()).getPlayerIndexes();
							WorldTile tile = (WorldTile) effect.args[0];
							for (int idx : playerIndexes) {
								Player p2 = World.getPlayers().get(idx);
								int distance = Utils.getDistance(p2, tile);
								if (p2 == null || !p2.isRunning() || p2 != player && !p2.isAcceptingAid() || distance >= 8)
									continue;
								double healPercentage = (8 - distance) * 0.01;
								healPercentage = healPercentage >= 0.07 ? 0.07 : healPercentage;
								int healedAmount = (int) (healPercentage * p2.getMaxHitpoints()), healCapRemaining = (int) effect.args[1];
								if (healCapRemaining < healedAmount)
									healedAmount = healCapRemaining;
								effect.args[1] = healCapRemaining - healedAmount;
								p2.heal(healedAmount, 0, 0, true);
							}
						}
					}
				}
			}
			else if (action == DEBUFF) {

			}
			else if (action == COMBO_BUFFS) {
				Player player = (Player) entity;
				Action a = player.getActionManager().getAction();
				if (!(a instanceof PlayerCombatNew)) {
					effect.cycle = 0;
					return;
				}
				Entity target = ((PlayerCombatNew) a).getTarget();
				if (effect.type == EffectType.CONCENTRATED_BLAST) {
					if (effect.cycle == 9 || effect.cycle == 7 || effect.cycle == 5) {
						Projectile projectile = World.sendProjectileNew(player, target, (int) effect.args[0], 32, 42, effect.cycle == 9 ? 15 : 0, 5, 0, 10);
						PlayerCombatNew.delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, PlayerCombatNew.getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, effect.cycle == 8 ? 0.75 : effect.cycle == 4 ? 0.82 : 0.87, false, true, false));
						target.setNextGraphics(new Graphics((int) effect.args[1], projectile.getEndTime(), 0));
					}
				}
				else if (effect.type == EffectType.ASPHYXIATE) {
					if (effect.cycle == 9 || effect.cycle == 7 || effect.cycle == 5 || effect.cycle == 3) {
						Projectile projectile = World.sendProjectileNew(player, target, (int) effect.args[0], 32, 42, 15, 5, 0, 10);
						Hit hit = PlayerCombatNew.getHit(player, target, true, Combat.MAGIC_TYPE, 1.0, 1.88, false, true, false);
						if (effect.cycle == 9 && hit.getDamage() > 0)
							target.setBoundDelay(10);//Six seconds
						PlayerCombatNew.delayHit(target, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
					}
				}
				else if (effect.type == EffectType.SNIPE) {
					if (effect.cycle == 1) {
						Entity[] targets = (boolean) effect.args[2] ? PlayerCombatNew.getMultiAttackTargets(player, target) : new Entity[] { target };
						for (Entity t : targets) {
							Projectile projectile = World.sendProjectileNew(t == target ? player : target, t, (int) effect.args[0], 41, 39, 10, 5, 0, 95);
							Hit hit = PlayerCombatNew.getHit(player, t, true, Combat.RANGE_TYPE, 1.15, 1.25 + Utils.random(0.94), false, true, false);
							PlayerCombatNew.delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
							t.setNextGraphics(new Graphics((int) effect.args[1], projectile.getEndTime(), 0));
							if (hit.getDamage() > 0)
								t.getEffectsManager().startEffect(new Effect(EffectType.PROTECTION_DISABLED, 8));
						}
					}
				}
				else if (effect.type == EffectType.RAPID_FIRE) {
					if (effect.cycle > 1) {
						Entity[] targets = (boolean) effect.args[3] ? PlayerCombatNew.getMultiAttackTargets(player, target) : new Entity[] { target };
						entity.setNextAnimation(new Animation((int) effect.args[0]));
						for (Entity t : targets) {
							Projectile projectile = World.sendProjectileNew(t == target ? player : target, t, (int) effect.args[1], 41, 39, (int) effect.args[2], 8, 0, 0);
							Hit hit = PlayerCombatNew.getHit(player, t, true, Combat.RANGE_TYPE, 1.0, 0.94, false, true, false);
							if (effect.cycle == 9 && hit.getDamage() > 0)
								t.setBoundDelay(10);//Six seconds
							PlayerCombatNew.delayHit(t, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
						}
					}
				}
				else if (effect.type == EffectType.UNLOAD) {
					if (effect.cycle == 8 || effect.cycle == 6 || effect.cycle == 4 || effect.cycle == 2) {
						Entity[] targets = (boolean) effect.args[2] ? PlayerCombatNew.getMultiAttackTargets(player, target) : new Entity[] { target };
						for (Entity t : targets) {
							Projectile projectile = World.sendProjectileNew(t == target ? player : target, t, (int) effect.args[0], 41, 39, 10, 5, 0, 95);
							for (Entity t2 : PlayerCombatNew.getMultiAttackTargets(player, t, 0, 9, true)) {
								Hit hit = PlayerCombatNew.getHit(player, t2, true, Combat.RANGE_TYPE, 1.0, 1.50 + (double) effect.args[1], false, true, false);
								if (t2 == target && hit.getDamage() > 0)//10% dmg increase each successful hit
									effect.args[1] = (double) effect.args[1] + 0.10;
								PlayerCombatNew.delayHit(t2, Utils.projectileTimeToCycles(projectile.getEndTime()) - 1, hit);
							}
						}
					}
				}
				else if (effect.type == EffectType.DETONATE) {
					if (effect.cycle > 5)
						effect.args[0] = new Integer(((Integer) effect.args[0]) + 1);
					else if (effect.cycle == 5)
						player.getHintIconsManager().addHintIcon(target, 3, -1, false);
					else if (effect.cycle == 1)
						effect.args = null;
				}
				else if (effect.type == EffectType.FLURRY) {
					if (effect.cycle == 8 || effect.cycle == 6 || effect.cycle == 4 || effect.cycle == 2) {
						for (Entity t : PlayerCombatNew.getMultiAttackTargets(player, target, 1, 9, true)) {
							if (!((boolean) effect.args[0]) && t != target)
								continue;
							Hit hit = PlayerCombatNew.getHit(player, t, true, Combat.MELEE_TYPE, 1.0, 0.94, false, true, false);
							if (t == target)
								effect.args[0] = hit.getDamage() > 0;
								PlayerCombatNew.delayHit(t, 0, hit);
						}
					}
				}
				else if (effect.type == EffectType.FURY) {
					if (effect.cycle == 6 || effect.cycle == 4 || effect.cycle == 1) {
						Hit hit = PlayerCombatNew.getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 0.75 + (double) effect.args[0], false, true, false);
						if (hit.getDamage() > 0)//10% dmg increase each successful hit
							effect.args[0] = (double) effect.args[0] + 0.10;
						PlayerCombatNew.delayHit(target, 0, hit);
					}
				}
				else if (effect.type == EffectType.ASSAULT) {
					if (effect.cycle == 7 || effect.cycle == 5 || effect.cycle == 3 || effect.cycle == 1) {
						Hit hit = PlayerCombatNew.getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 2.19, false, true, false);
						PlayerCombatNew.delayHit(target, 0, hit);
					}
				}
				else if (effect.type == EffectType.DESTROY) {
					if (effect.cycle == 9 || effect.cycle == 8 || effect.cycle == 5 || effect.cycle == 4) {
						if (effect.cycle == 9 || effect.cycle == 5) {
							player.setNextAnimationNoPriority((Animation) effect.args[0]);
							player.setNextGraphics(new Graphics((int) effect.args[1]));
						}
						Hit hit = PlayerCombatNew.getHit(player, target, true, Combat.MELEE_TYPE, 1.0, 1.88, false, true, false);
						PlayerCombatNew.delayHit(target, 0, hit);
					}
				}
				else if (effect.type == EffectType.FRENZY) {
					if (effect.cycle == 8 || effect.cycle == 6 || effect.cycle == 4 || effect.cycle == 2) {
						int angle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (target.getX() * 2 + target.getSize()), (player.getY() * 2 + player.getSize()) - (target.getY() * 2 + target.getSize()))) / 45d) & 0x7;
						for (Entity t : PlayerCombatNew.getMultiAttackTargets(player, target, 1, 9, true)) {
							int nextAngle = (int) Math.round(Math.toDegrees(Math.atan2((player.getX() * 2 + player.getSize()) - (t.getX() * 2 + t.getSize()), (player.getY() * 2 + player.getSize()) - (t.getY() * 2 + t.getSize()))) / 45d) & 0x7;
							if (nextAngle != angle)
								continue;
							Hit hit = PlayerCombatNew.getHit(player, t, true, Combat.MELEE_TYPE, 1.0, 1.10 + (double) effect.args[0], false, true, false);
							if (t == target && hit.getDamage() > 0)//10% dmg increase each successful hit
								effect.args[0] = (double) effect.args[0] + 0.10;
							PlayerCombatNew.delayHit(t, 0, hit);
						}
					}
				}
			}
			else if (action == HIT_MARK) {
				HitLook look = (HitLook) effect.args[0];
				Graphics graphics = (Graphics) effect.args[1];
				int damage = (int) effect.args[2], effectDelay = (int) effect.args[3];
				if (effect.cycle % effectDelay == 0) {
					if (effect.type == EffectType.POISON) {
						if (isPlayer) {
							Player player = (Player) entity;
							if (player.getInterfaceManager().containsScreenInterface()) {
								effect.cycle++;
								return;
							}
							if (player.getAuraManager().hasPoisonPurge())
								look = HitLook.HEALED_DAMAGE;
						}
					}
					else if (effect.type == EffectType.COMBUST || effect.type == EffectType.FRAGMENTATION || effect.type == EffectType.SLAUGHTER) {
						WorldTile tile = (WorldTile) effect.args[5];
						if (tile.getX() != entity.getX() || tile.getY() != entity.getY() || tile.getPlane() != entity.getPlane())
							damage *= (effect.type == EffectType.SLAUGHTER ? 3 : 2);
					} else if (effect.type == EffectType.REJUVINATE) {
						if (isPlayer) {
							Player player = (Player) entity;
							if (!player.getEquipment().hasShield())
								removeEffect(effect.type);
						}
					}
					if (look == HitLook.HEALED_DAMAGE)
						entity.heal(damage, 0, 0, true);
					else {
						Hit hit = new Hit(entity, damage, look);
						if (effect.args.length >= 5) {
							hit.setAbilityMark();
							hit.setSource((Entity) effect.args[4]);
						}
						entity.applyHit(hit);
						if(hit.getSource() instanceof Player && effect.args.length >= 5)
							PlayerCombatNew.autoRelatie((Player) effect.args[4], entity);
					}
					if (graphics != null)
						entity.setNextGraphics(graphics);
				}
			} else if (action == SHIELD_BUFF) {
				if (isPlayer) {
					Player player = (Player) entity;
					if (!player.getEquipment().hasShield())
						removeEffect(effect.type);
				}
			}
		}
	}

	public void refreshAllBuffs() {
		for (Effect effect : effects)
			refreshBuffEffect(effect);
	}

	public void refreshBuffEffect(Effect effect) {
		if (!isPlayer)
			return;
		Player player = (Player) entity;
		EffectType type = effect.type;
		if (type == EffectType.ADRENALINE_GAIN_DECREASE) {
			if (player.getVarsManager().sendVarBit(2794, effect.cycle > 0 ? 1 : 0))
				player.updateBuffs();
		}
		else if (type == EffectType.DRAGON_BATTLEAXE) {
			player.refreshMeleeAttackRating();
			player.refreshMeleeStrengthRating();
			player.refreshDefenceRating();
		}
		else if (type == EffectType.BONFIRE)
			player.getEquipment().refreshConfigs(false);
		else {
			if (type.var != -1 || type.varbit != -1) {
				boolean isVar = type.var != -1 && player.getVarsManager().sendVar(type.var, type == EffectType.ANTIPOISON ? -effect.cycle : effect.cycle);
				boolean isVarbit = type.varbit != -1 && player.getVarsManager().sendVarBit(type.varbit, effects.contains(effect) ? 1 : 0);
				if (type == EffectType.OVERLOAD) // Required to display.
					player.getVarsManager().sendVar(4910, 2048);
				if (isVar || isVarbit)
					processBuffTimer(effect, true);
			}
		}
	}

	public void processBuffTimer(Effect effect, boolean refresh) {
		if (!isPlayer)
			return;
		Player player = (Player) entity;
		EffectType type = effect.type;
		if (type.var == -1 && type.varbit == -1)
			return;
		if (refresh || effect.cycle % 25 == 0) {
			if (type.grMap != -1) {
				if (effect.type == EffectType.DEVOTION) {
					player.getPackets().sendCSVarInteger(4098, type.grMap);
					player.getPackets().sendExecuteScript(9379, type.grMap, 0, effect.cycle);
				} else 
					player.getPackets().sendExecuteScript(4252, type.grMap, effect.cycle);
			}
			player.updateBuffs();
		}
	}

	public void resetEffects() {
		Effect[] e = effects.toArray(new Effect[effects.size()]);
		effects.clear();
		for (Effect effect : e) {
			effect.setCycle(0);
			refreshBuffEffect(effect);
		}
	}

	public boolean removeEffect(EffectType type) {
		Effect effect = getEffectForType(type);
		if (effect == null)
			return false;
		if (effect.getCycle() > 0)
			effect.setCycle(0);
		type.onRemoval(entity);
		if (effect.type.action == COMBO_BUFFS && entity instanceof Player)
			PlayerCombatNew.setWeaponAbilityDelay((Player) entity);
		boolean removedEffect = effects.remove(effect);
		refreshBuffEffect(effect);
		return removedEffect;
	}

	public void removeEffectsWithAction(int action) {
		for (Effect effect : effects) {
			EffectType type = effect.getType();
			if (type.getAction() == action)
				removeEffect(type);
		}
	}

	public boolean hasActiveEffect(EffectType type) {
		Effect effect = getEffectForType(type);
		if (effect == null)
			return false;
		return effects.contains(effect);
	}

	public boolean hasActiveEffect(int action) {
		return getEffectForAction(action) != null;
	}

	public Effect getEffectForType(EffectType type) {
		for (Effect effect : effects) {
			if (effect.type == type)
				return effect;
		}
		return null;
	}

	public Effect getEffectForAction(int action) {
		for (Effect effect : effects) {
			EffectType type = effect.getType();
			if (type.getAction() == action)
				return effect;
		}
		return null;
	}

	public static byte BUFF = 0, DEBUFF = 1, HIT_MARK = 2, COMBO_BUFFS = 3, SHIELD_BUFF = 4;

	public static enum EffectType {
		POISON(HIT_MARK, 722, -1, 14900) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				Effect currentEffect = e.getEffectsManager().getEffectForType(this);
				if (currentEffect != null) {
					if ((int) currentEffect.args[2] > (int) effect.args[2])
						return false;
				}
				if (e instanceof Player) {
					Player p = (Player) e;
					if (p.getEquipment().getShieldId() == 18340)
						return false;
					if (currentEffect == null)
						p.getPackets().sendGameMessage("<col=00ff00>You are poisoned.");
				}
				return !e.isPoisonImmune();
			}
		},

		DISMEMBER(HIT_MARK, -1, 2073, -1),

		DEADSHOT(HIT_MARK, -1, 2092, -1),

		MASSACRE(HIT_MARK, -1, 2060, -1),

		COMBUST(HIT_MARK, -1, 2079, -1),

		FRAGMENTATION(HIT_MARK, -1, 2089, -1),

		SLAUGHTER(HIT_MARK, -1, 2059, -1),

		ANTIPOISON(BUFF, 722, -1, 14900) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.getEffectsManager().removeEffect(EffectType.POISON);
				return true;
			}
		},

		MORRIGAN_AXE(DEBUFF),

		STAFF_OF_LIGHT(BUFF) {
			@Override
			public void onRemoval(Entity e) {
				if (e instanceof Player)
					((Player) e).getPackets().sendGameMessage("The power of the light fades. Your resistance to melee attacks return to normal.");
			}
		},

		VESTA_IMMUNITY(BUFF) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				if (e instanceof Player)
					((Player) e).getPackets().sendEntityMessage(1, 0xFFFFFF, ((Player) e), "You are now immune to attacks from players for 10 seconds.");
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				if (e instanceof Player)
					((Player) e).getPackets().sendGameMessage("Your resistance to attacks return to normal.");
			}
		},

		MIRRORBACK_SPIDER(BUFF) {

			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				MirrorBackSpider spider = (MirrorBackSpider) effect.args[0];
				spider.setOwner(e);
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				MirrorBackSpider spider = (MirrorBackSpider) e.getEffectsManager().getEffectForType(this).args[0];
				if (!spider.isDead())
					spider.sendDeath(spider);
			}
		},

		SHADOW_EFFECT(DEBUFF),

		SMOKE_EFFECT(DEBUFF),

		CONFUSE_EFFECT(DEBUFF, -1, 2082, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				return !e.getEffectsManager().hasActiveEffect(STAGGER_EFFECT);
			}
		},

		WEAKEN_EFFECT(DEBUFF, -1, 2083, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				return !e.getEffectsManager().hasActiveEffect(ENFEEBLE_EFFECT);
			}
		},

		CURSE_EFFECT(DEBUFF, -1, 2084, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				return !e.getEffectsManager().hasActiveEffect(VULNERABILITY_EFFECT);
			}
		},

		VULNERABILITY_EFFECT(DEBUFF, -1, 2085, -1){
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.getEffectsManager().removeEffect(CURSE_EFFECT);
				return true;
			}
		},

		ENFEEBLE_EFFECT(DEBUFF, -1, 2086, -1){
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.getEffectsManager().removeEffect(WEAKEN_EFFECT);
				return true;
			}
		},

		STAGGER_EFFECT(DEBUFF, -1, 2087, -1){
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.getEffectsManager().removeEffect(CONFUSE_EFFECT);
				return true;
			}
		},

		ADRENALINE_GAIN_DECREASE(DEBUFF),

		INCREASE_CRIT_CHANCE(BUFF),

		MODIFY_ACCURACY(BUFF),

		DRAGON_BATTLEAXE(BUFF),

		BONFIRE(BUFF) {
			@Override
			public void onRemoval(Entity e) {
				if (e instanceof Player)
					((Player) e).getPackets().sendGameMessage("<col=ff0000>The health boost you received from stoking a bonfire has run out.");
			}
		},

		OVERLOAD(BUFF, -1, 500, 23129) {
			@Override
			public void onRemoval(Entity e) {
				if (e instanceof Player)
					Drinkables.resetOverLoadEffect((Player) e);
			}
		},

		PRAYER_RENEWAL(BUFF, -1, 2100, 14905) {
			@Override
			public void onRemoval(Entity e) {
				if (e instanceof Player)
					((Player) e).getPackets().sendGameMessage("<col=0000FF>Your prayer renewal has ended.");
			}
		},

		HEAL(HIT_MARK),

		BOUND(DEBUFF, -1, 2057, 14884) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				if (e.isBoundImmune())
					return false;
				Effect currentEffect = e.getEffectsManager().getEffectForType(this);
				boolean freeze = (boolean) effect.args[0];
				if (freeze && e instanceof Player && currentEffect == null)
					((Player) e).getPackets().sendGameMessage("You have been frozen.");
				if (e.getSize() == 1)
					e.setNextGraphics(new Graphics(4531, 0, 0));
				e.resetWalkSteps();
				e.getEffectsManager().startEffect(new Effect(EffectType.BOUND_IMMUNITY, effect.cycle + (int) effect.args[1]));
				return true;
			}
		},

		BOUND_IMMUNITY(DEBUFF),

		STUNNED(DEBUFF, -1, 2056, 14883) {

			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				//if (e.getSize() == 1)//TODO not sure if this is used (old stun gfx)
				//	e.setNextGraphics(new Graphics(254, 0, 92));
				return !e.isStunImmune();
			}
		},

		PROTECTION_DISABLED(DEBUFF) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				if (e instanceof NPC)
					return true;
				((Player) e).getPrayer().closeProtectionPrayers();
				return true;
			}
		},

		TELEPORT_BLOCK(DEBUFF, -1, 2097, -1),

		SIPHIONING(BUFF) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				Effect currentEffect = e.getEffectsManager().getEffectForType(SIPHIONING);
				if (currentEffect != null)
					return false;
				Nex nex = (Nex) effect.args[0];
				nex.killBloodReavers();
				nex.setNextForceTalk(new ForceTalk("A siphon will solve this!"));
				nex.playSoundEffect(3317);
				nex.setNextAnimation(new Animation(17409));
				nex.setNextGraphics(new Graphics(3370));
				return true;
			}
		},

		VIRUS(DEBUFF) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextForceTalk(new ForceTalk("*Cough*"));
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				if (e instanceof NPC)
					return;
				if (e instanceof Player)
					((Player) e).getPackets().sendGameMessage("The smoke clouds around you dissapate");
			}
		},

		BLOOD_SACRIFICE(DEBUFF) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				if (e instanceof NPC)
					return true;
				Player player = (Player) e;
				player.getPackets().sendGameMessage("<col=480000>Nex has marked you as a sacrifice, RUN!");
				player.getAppearence().setGlowRed(true);
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				if (e instanceof NPC)
					return;
				Effect currentEffect = e.getEffectsManager().getEffectForType(BLOOD_SACRIFICE);
				if (currentEffect != null) {
					Player player = (Player) e;
					Nex nex = (Nex) currentEffect.args[0];
					if (Utils.isOnRange(nex, player, 3)) {
						nex.setNextAnimation(new Animation(17414));
						nex.setNextGraphics(new Graphics(3375));
						nex.heal(player.getHitpoints());

						player.getPackets().sendGameMessage("You didn't make it far enough in time - Nex fires a punishing attack!");
						player.applyHit(new Hit(nex, (int) (player.getMaxHitpoints() * 0.1), HitLook.REGULAR_DAMAGE));
					}
				}
			}
		},

		UNLOAD(COMBO_BUFFS, -1, 2091, -1),

		CONCENTRATED_BLAST(COMBO_BUFFS),

		SNIPE(COMBO_BUFFS, -1, 2095, -1),

		RAPID_FIRE(COMBO_BUFFS, -1, 2096, -1),

		FLURRY(COMBO_BUFFS, -1, 2094, -1),

		FURY(COMBO_BUFFS, -1, 2074, -1),

		ASSAULT(COMBO_BUFFS, -1, 2093, -1),

		DESTROY(COMBO_BUFFS, -1, 2075, -1),

		FRENZY(COMBO_BUFFS, -1, 2077, -1),

		DETONATE(COMBO_BUFFS, -1, 2080, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18362));
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				Effect currentEffect = e.getEffectsManager().getEffectForType(this);
				if (currentEffect != null && currentEffect.args == null || currentEffect == null) //Just reset the animation.
					e.setNextAnimation(new Animation(-1));
				else {
					e.setNextAnimation(new Animation(18360));
					e.setNextGraphics(new Graphics(3539));
				}

				if (e instanceof Player)
					((Player) e).getHintIconsManager().removeUnsavedHintIcon();
			}
		},

		ASPHYXIATE(COMBO_BUFFS, -1, 2099, -1) {
			@Override
			public void onRemoval(Entity e) {
				e.setNextGraphics(new Graphics(-1));
				e.setNextAnimation(new Animation(-1));
			}
		},

		BINDING_SHOT(DEBUFF, -1, 2088, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				if (e.isBoundImmune())
					return false;
				e.getEffectsManager().startEffect(new Effect(EffectType.BOUND_IMMUNITY, effect.cycle + 5));
				return true;
			}
		},

		INCENDIARY_SHOT(DEBUFF, -1, 2090, -1),

		PULVERISE(DEBUFF, -1, 2078, -1),

		BERSERK(BUFF, -1, 2076, -1) {

			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18597));
				e.setNextGraphics(new Graphics(3475));
				e.setNextGraphics(new Graphics(3476));

				//TODO if someone atk you first you lose :)
				return true;
			}
		},

		FIRE_IMMUNITY(BUFF, -1, 497, 14903) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				Effect currentEffect = e.getEffectsManager().getEffectForType(SUPER_FIRE_IMMUNITY);
				if (currentEffect != null)
					return false;
				return true;
			}
		},

		SUPER_FIRE_IMMUNITY(BUFF, -1, 498, 14904) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				Effect currentEffect = e.getEffectsManager().getEffectForType(FIRE_IMMUNITY);
				if (currentEffect != null)
					e.getEffectsManager().removeEffect(EffectType.FIRE_IMMUNITY);
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				if (e instanceof Player)
					((Player) e).getPackets().sendGameMessage("<col=480000>Your resistance to dragonfire has run out.</col>");
			}
		},

		WEAPON_POISON(BUFF, -1, 2101, 14901) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				Effect currentEffect = e.getEffectsManager().getEffectForType(WEAPON_POISON);
				if (currentEffect != null && currentEffect.cycle > effect.cycle)
					return false;
				return true;
			}
		},

		ADRENALINE_GAIN_FOR_CRIT(BUFF, -1, 20685, 19194),

		MODIFY_DAMAGE(BUFF),

		SEVER(DEBUFF, -1, 2056, -1),

		//TODO CODE ADRENALINE POTION PREVENTION

		METAMORPHISIS(BUFF, -1, 2081, -1) {

			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				Player player = (Player) e;
				int type = (int) effect.getArguments()[0];
				if (type == Combat.TYPE_AIR || type == Combat.TYPE_WATER)
					player.setNextGraphics(new Graphics(type == Combat.TYPE_WATER ? 3547 : 3563));
				else if (type == Combat.TYPE_EARTH || type == Combat.TYPE_FIRE) {
					player.setNextGraphics(new Graphics(type == Combat.TYPE_FIRE ? 3550 : 3548));
					player.setNextAnimation(new Animation(type == Combat.TYPE_EARTH ? 18406 : 18418));
				}
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				((Player) e).getAppearence().transformIntoNPC(-1);
			}
		},

		SUNSHINE(BUFF, -1, 18139, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(19866));
				World.sendGraphics(e, new Graphics(3856, 0, 10, 0, true), (WorldTile) effect.args[0]);
				return true;
			}
		},

		DEATHS_SWIFTNESS(BUFF, -1, 18140, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(19879));
				World.sendGraphics(e, new Graphics(3869, 0, 10), (WorldTile) effect.args[0]);
				return true;
			}
		},

		TRANSFIGURE(BUFF, -1, -1, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				World.sendGraphics(e, new Graphics(3535), e);
				e.setNextGraphics(new Graphics(3536));
				return true;
			}
		},

		BLEED(HIT_MARK),

		ANTICIPATION(BUFF, -1, 2062, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18069));
				return true;
			}
		},

		PROVOKE(BUFF, -1, 2064, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				if (e instanceof NPC)//TODO exception for KK
					return false;
				return true;
			}
		},

		FREEDOM(BUFF, -1, 2063, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18070));
				e.getEffectsManager().removeEffect(EffectType.STUNNED);
				e.getEffectsManager().removeEffect(EffectType.BOUND);
				e.getEffectsManager().removeEffect(EffectType.BINDING_SHOT);
				e.getEffectsManager().removeEffect(EffectType.COMBUST);
				e.getEffectsManager().removeEffect(EffectType.DISMEMBER);
				e.getEffectsManager().removeEffect(EffectType.FRAGMENTATION);
				e.getEffectsManager().removeEffect(EffectType.SLAUGHTER);
				//TODO find out the rest of these.
				return true;
			}
		},

		REFLECT(SHIELD_BUFF, -1, 2067, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18345));
				e.setNextGraphics(new Graphics(3615));
				return true;
			}
		},

		RESONANCE(SHIELD_BUFF, -1, 2065, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18081));
				return true;
			}
		},

		PREPARATION(SHIELD_BUFF, -1, 2066, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18106));
				e.setNextGraphics(new Graphics(3614));
				return true;
			}
		},

		REJUVINATE(HIT_MARK, -1, 2071, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18087));
				e.setNextGraphics(new Graphics(3617));

				//Restores all skills
				if (e instanceof Player) {
					Player player = (Player) e;
					for (int skill = 0; skill < 25; skill++) {
						if (skill == Skills.PRAYER || skill == Skills.SUMMONING)
							continue;
						player.getSkills().set(skill, player.getSkills().getLevelForXp(skill));
					}
				}
				return true;
			}
		},

		BARRICADE(SHIELD_BUFF, -1, 2070, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				if(e instanceof KalphiteKing) {
					e.setNextAnimation(new Animation(19455));
					e.setNextGraphics(new Graphics(3741));
				}else{
					e.setNextAnimation(new Animation(18091));
					e.setNextGraphics(new Graphics(3632));
				}
				return true;
			}
		},

		IMMORTALITY(SHIELD_BUFF, -1, 2072, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18081));
				e.setNextGraphics(new Graphics(3623));
				e.setNextGraphics(new Graphics(3624));
				return true;
			}
		},

		DEBILITATE(SHIELD_BUFF, -1, 2068, -1),

		REVENGE(SHIELD_BUFF, -1, 2069, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18099));
				e.setNextGraphics(new Graphics(3616));
				return true;
			}
		},

		DEVOTION(BUFF, -1, 21023, 25028) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextGraphics(new Graphics(4503));
				return true;
			}
		},

		REGENERATE(BUFF, -1, 2061, -1),

		INCITE(BUFF, -1, 2098, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(12584));
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				if (e instanceof Player)
					((Player) e).getPackets().sendGameMessage("Incite is no longer active.");
			}
		},

		NATURAL_INSTINCT(BUFF, -1, 18141, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(19858));
				e.setNextGraphics(new Graphics(3855));
				Entity target = (Entity) effect.getArguments()[0];
				if (target instanceof Player) {
					Player playerTarget = (Player) target;
					int adrenaline = playerTarget.getCombatDefinitions().getSpecialAttackPercentage();
					playerTarget.getCombatDefinitions().desecreaseSpecialAttack(adrenaline);
					if (e instanceof Player)
						((Player) e).getCombatDefinitions().increaseSpecialAttack(adrenaline);
				}
				return target instanceof NPC;
			}
		},

		ICE_ASYLUM(BUFF, -1, 22462, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(23207));
				e.setNextGraphics(new Graphics(4667));
				World.sendGraphics(e, new Graphics(4668, 0, 10), (WorldTile) effect.args[0]);
				return true;
			}
		},

		GUTHIX_BLESSING(BUFF, -1, 18142, -1) {
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(19831));
				e.setNextGraphics(new Graphics(3850));
				return true;
			}

			@Override
			public void onRemoval(Entity e) {
				Effect effect = e.getEffectsManager().getEffectForType(this);
				if (effect == null)
					return;
				((NPC) effect.getArguments()[0]).finish();
			}
		},

		SIPHON_IMMUNITY(BUFF, -1, -1, -1) {//TODO find this var when im not tired as fuck
			@Override
			public boolean canStartEffect(Effect effect, Entity e) {
				e.setNextAnimation(new Animation(18205));
				e.setNextGraphics(new Graphics(3594));
				return true;
			}
		},

		//22461 - Shadow Tendrils
		//22460 - Blood Tendrils
		//22459 - Smoke Tendrils
		//21454 - Adrenaline Prevention
		//21024 - Transmutation
		//21022 - Sacrifice


		;

		public boolean canStartEffect(Effect effect, Entity e) {
			return true;
		}

		public void onRemoval(Entity e) {

		}

		private byte action;
		private int var, varbit, grMap;

		private EffectType(byte action, int var, int varbit, int grMap) {
			this.action = action;
			this.var = var;
			this.varbit = varbit;
			this.grMap = grMap;
		}

		private EffectType(byte action) {
			this(action, -1, -1, -1);
		}

		public byte getAction() {
			return action;
		}

		public int getVar() {
			return var;
		}

		public int getGrMap() {
			return grMap;
		}
	}

	public static class Effect implements Serializable {

		private static final long serialVersionUID = 9217587656136559938L;

		private EffectType type;
		private int cycle;
		private Object[] args;

		public Effect(EffectType type, int count, Object... args) {
			this.type = type;
			this.cycle = count;
			this.args = args;
		}

		public EffectType getType() {
			return type;
		}

		public int getCycle() {
			return cycle;
		}

		public void setCycle(int cycle) {
			this.cycle = cycle;
		}

		public Object[] getArguments() {
			return args;
		}
	}

	public static final int getRenewalTime(Player player) {
		int lvl = player.getSkills().getLevel(Skills.PRAYER);
		if (lvl < 20)
			return 0;
		else if (lvl < 50)
			return 1;
		return 2;
	}

	public static void makePoisoned(Entity e, int damage) {
		makePoisoned(e, damage, -1);
	}

	public static void makePoisoned(Entity e, int damage, int cycles) {
		e.getEffectsManager().startEffect(new Effect(EffectType.POISON, cycles == -1 ? ((damage * 5) - 40) / 10 : cycles, HitLook.POISON_DAMAGE, null, damage, 17));
	}

	public static void startHealEffect(Entity e, int damage) {
		e.getEffectsManager().startEffect(new Effect(EffectType.HEAL, 60, HitLook.HEALED_DAMAGE, new Graphics(93, 0, 0, 0, true), damage, 6));
	}

	public static void startBleedEffect(Entity e, int cycles, int delay, int damage) {
		e.getEffectsManager().startEffect(new Effect(EffectType.BLEED, cycles, HitLook.REGULAR_DAMAGE, null, damage, delay));
	}

	public static boolean healPoison(Entity entity) {
		if (!(entity instanceof Player))
			return false;
		Player player = (Player) entity;
		if (!player.getEffectsManager().hasActiveEffect(EffectType.POISON)) {
			player.getPackets().sendGameMessage("You're not currently in need of curing.");
			return false;
		}
		for (int i = 0; i < 28; i++) {
			Item item = player.getInventory().getItem(i);
			if (item == null || (!Drinkables.Drink.ANTIPOISON_POTION.contains(item.getId()) && !Drinkables.Drink.SUPER_ANTIPOISON.contains(item.getId()) && !Drinkables.Drink.ANTIPOISON_FLASK.contains(item.getId()) && !Drinkables.Drink.SUPER_ANTIPOISON_FLASK.contains(item.getId())))
				continue;
			Drinkables.drink(player, item, i);
			return true;
		}
		player.getPackets().sendGameMessage("You don't have anything to cure the poison.");
		return false;
	}

	public static void addBoundEffect(Entity entity, int delay, boolean freeze, int immunityDelay) {
		entity.getEffectsManager().startEffect(new Effect(EffectType.BOUND, delay, freeze, immunityDelay));
	}

	public boolean isEmpty() {
		return effects.isEmpty();
	}
}
