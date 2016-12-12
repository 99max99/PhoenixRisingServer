package net.kagani.game.player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.NewForceMovement;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.duel.DuelArena;
import net.kagani.game.minigames.stealingcreation.StealingCreationController;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.npc.others.GuthixBlessing;
import net.kagani.game.player.actions.PlayerCombatNew;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.controllers.BorkController;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.player.controllers.DTControler;
import net.kagani.game.player.controllers.FightCaves;
import net.kagani.game.player.controllers.FightKiln;
import net.kagani.game.player.controllers.HouseControler;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.network.decoders.WorldPacketsDecoder;
import net.kagani.network.decoders.handlers.InventoryOptionsHandler;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class ActionBar implements Serializable {

	private static final long serialVersionUID = 2370857782475511964L;
	public static final int ITEM_SHORTCUT = 0, MELEE_ABILITY_SHORTCUT = 1,
			STRENGTH_ABILITY_SHORTCUT = 2, RANGED_ABILITY_SHORTCUT = 5,
			DEFENCE_ABILITY_SHORTCUT = 3, HEAL_ABILITY_SHORTCUT = 4,
			MAGIC_ABILITY_SHORTCUT = 6, PRAYER_SHORTCUT = 7;

	public static final int REGENERATION_VARBIT_ID = 24939;

	public static final int[] CS_DATA_ID = { 6734, 6735, 6736, 6737, 6738,
			6740, 6739 };

	public static GeneralRequirementMap getAbilityData(int type, int abilityId) {
		int id = ClientScriptMap.getMap(CS_DATA_ID[type - 1]).getIntValue(
				abilityId);
		return id == -1 ? null : GeneralRequirementMap.getMap(id);
	}

	public static class PrayerShortcut extends Shortcut {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7336111838898227173L;

		private int prayerId;
		private boolean curse;

		public PrayerShortcut(int prayerId, boolean curse) {
			this.prayerId = prayerId;
			this.curse = curse;
		}

		@Override
		public int getType() {
			return PRAYER_SHORTCUT;

		}

		@Override
		public int getId(Player player) {
			if (!curse) {
				switch (prayerId) {
				case 0:
					int level = player.getSkills().getLevelForXp(Skills.PRAYER);
					return level >= 28 ? 10 : level >= 10 ? 4 : 1;
				case 1:
					level = player.getSkills().getLevelForXp(Skills.PRAYER);
					return level >= 31 ? 11 : level >= 13 ? 5 : 2;
				case 2:
					level = player.getSkills().getLevelForXp(Skills.PRAYER);
					return level >= 34 ? 12 : level >= 16 ? 6 : 3;
				case 7:
					return 7;
				case 8:
					return 8;
				case 9:
					return 9;
				case 11:
					return 13;
				case 12:
					return 14;
				case 13:
					return 15;
				case 14:
					return 16;
				case 15:
					return 17;
				case 16:
					return 18;
				case 3:
					level = player.getSkills().getLevelForXp(Skills.PRAYER);
					return level >= 44 ? 23 : level >= 26 ? 21 : 19;
				case 5:
					level = player.getSkills().getLevelForXp(Skills.PRAYER);
					return level >= 45 ? 24 : level >= 27 ? 22 : 20;
				case 10:
					return 25;
				case 17:
					return 26;
				case 19:
					return 27;
				case 18:
					return 28;
				case 21:
					return 29;
				case 20:
					return 30;
				case 4:
					level = player.getSkills().getLevelForXp(Skills.PRAYER);
					return level >= 44 ? 53 : level >= 26 ? 52 : 51;
				case 6:
					level = player.getSkills().getLevelForXp(Skills.PRAYER);
					return level >= 45 ? 56 : level >= 27 ? 55 : 54;
				}
			} else {
				switch (prayerId) {
				case 0:
					return 31;
				case 1:
					return 32;
				case 2:
					return 33;
				case 4:
					return 34;
				case 6:
					return 35;
				case 9:
					return 36;
				case 10:
					return 37;
				case 11:
					return 38;
				case 12:
					return 39;
				case 13:
					return 40;
				case 14:
					return 41;
				case 15:
					return 42;
				case 17:
					return 43;
				case 19:
					return 44;
				case 20:
					return 45;
				case 21:
					return 46;
				case 22:
					return 47;
				case 23:
					return 48;
				case 24:
					return 49;
				case 25:
					return 50;
				case 3:
					return 57;
				case 16:
					return 58;
				case 5:
					return 59;
				case 18:
					return 60;
				case 27:
					return 61;
				case 26:
					return 62;
				case 8:
					return 63;
				case 7:
					return 64;
				}

			}
			return 0;
		}

		@Override
		public boolean queue(Player player) {
			return false;
		}
	}

	public static class ItemShortcut extends Shortcut {

		private static final long serialVersionUID = 4248509971373414335L;

		private int itemId;

		public ItemShortcut(int itemId) {
			this.itemId = itemId;
		}

		@Override
		public int getType() {
			return ITEM_SHORTCUT;

		}

		@Override
		public int getId(Player player) {
			return itemId;
		}

		@Override
		public boolean queue(Player player) {
			return false;
		}
	}

	public static class RangeAbilityShortcut extends Shortcut {

		private static final long serialVersionUID = 2631054284301142532L;

		private int abilityId;

		public RangeAbilityShortcut(int abilityId) {
			this.abilityId = abilityId;
		}

		@Override
		public int getType() {
			return RANGED_ABILITY_SHORTCUT;
		}

		@Override
		public int getId(Player player) {
			return abilityId;
		}

		@Override
		public boolean queue(Player player) {
			GeneralRequirementMap data = GeneralRequirementMap
					.getMap(ClientScriptMap.getMap(CS_DATA_ID[getType() - 1])
							.getIntValue(abilityId));
			return !data.getValues().containsKey((long) 3394);
		}
	}

	public static class MeleeAbilityShortcut extends Shortcut {

		private static final long serialVersionUID = 4307311904537253515L;

		private int abilityId;

		public MeleeAbilityShortcut(int abilityId) {
			this.abilityId = abilityId;
		}

		@Override
		public int getType() {
			return MELEE_ABILITY_SHORTCUT;

		}

		@Override
		public int getId(Player player) {
			return abilityId;
		}

		@Override
		public boolean queue(Player player) {
			GeneralRequirementMap data = GeneralRequirementMap
					.getMap(ClientScriptMap.getMap(CS_DATA_ID[getType() - 1])
							.getIntValue(abilityId));
			return !data.getValues().containsKey((long) 3394);
		}
	}

	public static class StrengthAbilityShortcut extends Shortcut {

		private static final long serialVersionUID = -4563409545893188469L;

		private int abilityId;

		public StrengthAbilityShortcut(int abilityId) {
			this.abilityId = abilityId;
		}

		@Override
		public int getType() {
			return STRENGTH_ABILITY_SHORTCUT;

		}

		@Override
		public int getId(Player player) {
			return abilityId;
		}

		@Override
		public boolean queue(Player player) {
			GeneralRequirementMap data = GeneralRequirementMap
					.getMap(ClientScriptMap.getMap(CS_DATA_ID[getType() - 1])
							.getIntValue(abilityId));
			return !data.getValues().containsKey((long) 3394);
		}
	}

	public static class DefenceAbilityShortcut extends Shortcut {

		private static final long serialVersionUID = 2285557519314659732L;

		private int abilityId;

		public DefenceAbilityShortcut(int abilityId) {
			this.abilityId = abilityId;
		}

		@Override
		public int getType() {
			return DEFENCE_ABILITY_SHORTCUT;

		}

		@Override
		public int getId(Player player) {
			return abilityId;
		}

		@Override
		public boolean queue(Player player) {
			GeneralRequirementMap data = GeneralRequirementMap
					.getMap(ClientScriptMap.getMap(CS_DATA_ID[getType() - 1])
							.getIntValue(abilityId));
			return !data.getValues().containsKey((long) 3394);
		}
	}

	public static class HealAbilityShortcut extends Shortcut {

		private static final long serialVersionUID = 55418066282083274L;

		private int abilityId;

		public HealAbilityShortcut(int abilityId) {
			this.abilityId = abilityId;
		}

		@Override
		public int getType() {
			return HEAL_ABILITY_SHORTCUT;

		}

		@Override
		public int getId(Player player) {
			return abilityId;
		}

		@Override
		public boolean queue(Player player) {
			if (abilityId == 16) {
				Item weapon = player.getEquipment().getItem(
						Equipment.SLOT_WEAPON);
				if (weapon == null)
					return true;
				return !player.hasInstantSpecial(weapon);
			} else if (abilityId == 1)
				return player.getCombatDefinitions().isCombatStance();
			else if (abilityId == 10)
				return true;
			GeneralRequirementMap data = GeneralRequirementMap
					.getMap(ClientScriptMap.getMap(CS_DATA_ID[getType() - 1])
							.getIntValue(abilityId));
			return !data.getValues().containsKey((long) 3394);
		}
	}

	public static class MagicAbilityShortcut extends Shortcut {

		private static final long serialVersionUID = -1677214505228254289L;

		private int abilityId;

		public MagicAbilityShortcut(int abilityId) {
			this.abilityId = abilityId;
		}

		@Override
		public int getType() {
			return MAGIC_ABILITY_SHORTCUT;

		}

		@Override
		public int getId(Player player) {
			return abilityId;
		}

		@Override
		public boolean queue(Player player) {
			GeneralRequirementMap data = GeneralRequirementMap
					.getMap(ClientScriptMap.getMap(CS_DATA_ID[getType() - 1])
							.getIntValue(abilityId));
			return !data.getValues().containsKey((long) 3394);
			// Magic.isCombatSpell(data);
		}
	}

	public static abstract class Shortcut implements Serializable {

		private static final long serialVersionUID = 585026886173513110L;

		public abstract int getId(Player player);

		public abstract int getType();

		public abstract boolean queue(Player player);
	}

	private int currentBar;
	private Shortcut[][] shortcuts;
	private boolean lockedBar;
	private boolean blockIncomingShareOffers;

	private transient Player player;
	private transient Map<Integer, Long> cooldowns;
	private transient long globalCooldown;

	public ActionBar() {
		shortcuts = new Shortcut[5][14];
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void pushShortcut(int index) {
		pushShortcut(index, WorldPacketsDecoder.ACTION_BUTTON1_PACKET);
	}

	public void setShortcut(int index, Shortcut shortcut) {
		if (shortcuts[currentBar][index] != null
				&& shortcuts[currentBar][index].getType() == shortcut.getType()
				&& shortcuts[currentBar][index].getId(player) == shortcut
						.getId(player)) {
			return;
		}
		if (getQueueShortcutIndex() == index)
			removeQueueShortcut();
		shortcuts[currentBar][index] = shortcut;
		refresh(index, true);
		refreshButtons();
	}

	public void clearShortcut(int index) {
		if (shortcuts[currentBar][index] == null)
			return;
		if (getQueueShortcutIndex() == index)
			removeQueueShortcut();
		shortcuts[currentBar][index] = null;
		refresh(index, true);
		refreshButtons();
	}

	// public static final int ITEM_SHORTCUT = 0, MELEE_ABILITY_SHORTCUT = 1,
	// STRENGTH_ABILITY_SHORTCUT = 2, RANGED_ABILITY_SHORTCUT = 5,
	// DEFENCE_ABILITY_SHORTCUT = 3, HEAL_ABILITY_SHORTCUT = 4,
	// MAGIC_ABILITY_SHORTCUT = 6, PRAYER_SHORTCUT = 7;

	// public static final int[] CS_DATA_ID = {6734, 6735, 6736, 6737, 6738,
	// 6740, 6739};

	/*
	 * recommended setup - adds spec ability
	 */
	public void setupBar() {
		shortcuts[currentBar] = new Shortcut[14];

		int type = player.getCombatDefinitions().getType(Equipment.SLOT_WEAPON);

		int mainAbilityType = type == Combat.MELEE_TYPE ? MELEE_ABILITY_SHORTCUT
				: type == Combat.RANGE_TYPE ? RANGED_ABILITY_SHORTCUT
						: type == Combat.MAGIC_TYPE ? MAGIC_ABILITY_SHORTCUT
								: -1;

		int secondAbilityType = type == Combat.MELEE_TYPE ? STRENGTH_ABILITY_SHORTCUT
				: -1;

		int shortcutCount = 0;

		if (mainAbilityType != -1) {
			for (Long abilityId : ClientScriptMap
					.getMap(CS_DATA_ID[mainAbilityType - 1]).getValues()
					.keySet()) {

				if (mainAbilityType == MELEE_ABILITY_SHORTCUT
						&& abilityId == 13
						|| (mainAbilityType == MAGIC_ABILITY_SHORTCUT && !isMagicAbility(abilityId
								.intValue())))
					continue;

				if (!hasRequiriments(abilityId.intValue(),
						getAbilityData(mainAbilityType, abilityId.intValue()),
						false, true))
					continue;

				shortcuts[currentBar][shortcutCount++] = mainAbilityType == MELEE_ABILITY_SHORTCUT ? new MeleeAbilityShortcut(
						abilityId.intValue())
						:

						mainAbilityType == RANGED_ABILITY_SHORTCUT ? new RangeAbilityShortcut(
								abilityId.intValue())
								: mainAbilityType == MAGIC_ABILITY_SHORTCUT ? new MagicAbilityShortcut(
										abilityId.intValue()) : null;
				if (shortcutCount == 12)
					break;
			}
		}

		if (secondAbilityType != -1 && shortcutCount < 12) {
			for (Long abilityId : ClientScriptMap
					.getMap(CS_DATA_ID[secondAbilityType - 1]).getValues()
					.keySet()) {
				if (!hasRequiriments(
						abilityId.intValue(),
						getAbilityData(secondAbilityType, abilityId.intValue()),
						false, true))
					continue;

				shortcuts[currentBar][shortcutCount++] = secondAbilityType == STRENGTH_ABILITY_SHORTCUT ? new StrengthAbilityShortcut(
						abilityId.intValue()) : null;
				if (shortcutCount == 12)
					break;
			}
		}

		shortcuts[currentBar][12] = new DefenceAbilityShortcut(1);
		shortcuts[currentBar][13] = new DefenceAbilityShortcut(2);
		refreshActionBar();
		refreshButtons();
	}

	public void clearBar() {
		shortcuts[currentBar] = new Shortcut[14];
		refreshActionBar();
		refreshButtons();
	}

	public void switchShortcut(int fromIndex, int toIndex) {
		if (shortcuts[currentBar][fromIndex] == null)
			return;
		Shortcut fromShortcut = shortcuts[currentBar][fromIndex];
		Shortcut toShortcut = shortcuts[currentBar][toIndex];
		shortcuts[currentBar][fromIndex] = toShortcut;
		shortcuts[currentBar][toIndex] = fromShortcut;
		refresh(fromIndex, true);
		refresh(toIndex, true);
		refreshButtons();
	}

	public void pushShortcut(int index, int packetId) {
		// examine replaced with customize keybind
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
			player.getInterfaceManager().openMenu(8, 3);
			return;
		}
		Shortcut shortcut = shortcuts[currentBar][index];
		if (shortcut == null)
			return;
		if (player.getInterfaceManager().containsScreenInterface()
				|| player.getInterfaceManager().containsBankInterface()) {
			player.getPackets().sendGameMessage(
					"You're currently busy, and can't do that right now.");
			return;
		}
		processShortcut(shortcut, packetId, index, false);
	}

	public void processShortcut(Shortcut shortcut, int packetId,
			int shortcutIndex, boolean queue) {
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
			// huge exeption for activation of detonate
			if (shortcut.getType() == MAGIC_ABILITY_SHORTCUT
					&& shortcut.getId(player) == 8
					&& player.getEffectsManager().hasActiveEffect(
							EffectType.DETONATE)) {
				if (queue)
					removeQueueShortcut();
				// here
				if (!(player.getActionManager().getAction() instanceof PlayerCombatNew))
					return;
				Entity target = ((PlayerCombatNew) player.getActionManager()
						.getAction()).getTarget();
				// activate effect dmg here. get time effect been active
				Effect effect = player.getEffectsManager().getEffectForType(
						EffectType.DETONATE);
				int ticks = effect.getArguments() == null ? 10 : ((int) effect
						.getArguments()[0] / 5);
				for (Entity e : PlayerCombatNew.getMultiAttackTargets(player,
						target, 3, 9))
					PlayerCombatNew.delayHit(e, 0, PlayerCombatNew.getHit(
							player, e, true, Combat.MAGIC_TYPE, 1.0,
							1.5 + (ticks / 5), false, true, false));
				player.getEffectsManager().removeEffect(EffectType.DETONATE);
				return;
			} else if (shortcut.getType() == HEAL_ABILITY_SHORTCUT
					&& shortcut.getId(player) == 15) {
				// here
				if (!queue) {
					Entity target = player.getCombatDefinitions()
							.getCurrentTarget();// ((PlayerCombatNew)
					// player.getActionManager().getAction()).getTarget();
					if (target != null) {
						boolean canSiphon = !(target instanceof NPC);
						if (target instanceof Player) {
							Player playerTarget = (Player) target;
							canSiphon = playerTarget.getCombatDefinitions()
									.getSpecialAttackPercentage() > player
									.getCombatDefinitions()
									.getSpecialAttackPercentage();
						}
						if (!canSiphon) {
							player.getPackets()
									.sendGameMessage(
											"You cannot use this ability, as you have at least as much adrenaline as your target.");
							removeQueueShortcut();
							return;
						}
						player.faceEntity(target);
					}
					setQueueShortcut(shortcut, shortcutIndex);
					return;
				}
			}
			if (shortcut.getType() == HEAL_ABILITY_SHORTCUT
					&& shortcut.getId(player) == 1) {
				if (!queue) {
					if (player.getCombatDefinitions()
							.getSpecialAttackPercentage() == 0) {
						player.getPackets()
								.sendGameMessage(
										"This ability requires adrenaline in order to be used.");
						return;
					}
					if (player.getCombatDefinitions().isCombatStance()) {
						if (player.getVarsManager().getBitValue(
								REGENERATION_VARBIT_ID) == 1) {
							player.getPackets()
									.sendGameMessage(
											"Regenerate will no longer be activated when you have left combat.");
							removeQueueShortcut();
							return;
						}
						player.getPackets()
								.sendGameMessage(
										"Regenerate will be activated when you have left combat.");
						setQueueShortcut(shortcut, shortcutIndex);
						return;
					}
				} else {
					if (player.isUnderCombat()) // cuz stance changes 1 tick
						// later
						return;
					removeQueueShortcut();
				}
				// turn it on here
				player.getEffectsManager().startEffect(
						new Effect(EffectType.REGENERATE, 11));
				return;
			} else {
				if (queue) {
					Entity target = player.getCombatDefinitions()
							.getCurrentTarget();
					if (!(player.getActionManager().getAction() instanceof PlayerCombatNew)) {
						if (target == null || target.isDead()
								|| target.hasFinished()) {
							if (player.getCombatDefinitions()
									.isAllowAbilityQueueing()) {
								refreshQueueShortcut();
								return;
							}
							removeQueueShortcut();
							player.getPackets().sendGameMessage(
									"You don't have a target.");
							return;
						}
						if (!player.withinDistance(target)) {
							if (player.getCombatDefinitions()
									.isAllowAbilityQueueing()) {
								refreshQueueShortcut();
								return;
							}
							removeQueueShortcut();
							player.getPackets().sendGameMessage(
									"Your target is too far away.");
							return;
						}
						player.faceEntity(target);
						if (!player.getControlerManager().canAttack(target)) {
							removeQueueShortcut();
							return;
						}
						if (target instanceof Player) {
							Player p2 = (Player) target;
							if (!player.isCanPvp() || !p2.isCanPvp()) {
								player.getPackets()
										.sendGameMessage(
												"You can only attack players in a player-vs-player area.");
								removeQueueShortcut();
								return;
							}
						} else if (target instanceof Familiar) {
							Familiar familiar = (Familiar) target;
							if (familiar == player.getFamiliar()) {
								player.getPackets().sendGameMessage(
										"You can't attack your own familiar.");
								removeQueueShortcut();
								return;
							}
							if (!familiar.canAttack(player)) {
								player.getPackets().sendGameMessage(
										"You can't attack this npc.");
								removeQueueShortcut();
								return;
							}
						} else if (target instanceof NPC) {
							if (!((NPC) target).getDefinitions()
									.hasAttackOption()) {
								player.getPackets().sendGameMessage(
										"You can't attack this npc.");
								removeQueueShortcut();
								return;
							}
						}
						PlayerCombatNew combat = new PlayerCombatNew(target);
						player.getActionManager().setAction(combat);
					} else if (target == null)
						target = ((PlayerCombatNew) player.getActionManager()
								.getAction()).getTarget();
					if (hasCooldown(shortcut)) {
						if (player.getCombatDefinitions()
								.isAllowAbilityQueueing()) {
							refreshQueueShortcut();
							return;
						}
						removeQueueShortcut();
						player.getPackets().sendGameMessage(
								"Ability not ready yet.");
						return;
					}

					if (target == null
							|| !PlayerCombatNew
									.isWithinDistance(player, target)
							|| player.isStunned())
						return;

					if (player.getCombatDefinitions().getCombatMode() == CombatDefinitions.LEGACY_COMBAT_MODE)
						if ((int) (player.getCombatDefinitions()
								.getMainHandDelay() - Utils.currentWorldCycle()) > 0)
							return;

					// else use ability 4 cd wait
					removeQueueShortcut();
				} else if (shortcut.queue(player)) {
					setQueueShortcut(shortcut, shortcutIndex);
					return;
				} else if ((shortcut.getType() == MELEE_ABILITY_SHORTCUT
						|| shortcut.getType() == DEFENCE_ABILITY_SHORTCUT
						|| shortcut.getType() == STRENGTH_ABILITY_SHORTCUT
						|| shortcut.getType() == RANGED_ABILITY_SHORTCUT
						|| shortcut.getType() == HEAL_ABILITY_SHORTCUT || shortcut
						.getType() == MAGIC_ABILITY_SHORTCUT)
						&& hasCooldown(shortcut)) {
					if (player.getCombatDefinitions().isAllowAbilityQueueing()) {
						setQueueShortcut(shortcut, shortcutIndex);
						return;
					}
					player.getPackets().sendGameMessage(
							"Ability not ready yet.");
					return;
				}
			}
		}

		switch (shortcut.getType()) {
		case ITEM_SHORTCUT:
			if (player.getInterfaceManager().containsInventoryInter())
				return;
			ItemDefinitions defs = ItemDefinitions.getItemDefinitions(shortcut
					.getId(player));
			// remove option if wearing item
			if (player.getCombatDefinitions().isAllowAbilityQueueing()) {
				refreshQueueShortcut();
				return;
			}
			if (defs.isWearItem()
					&& packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
				Item item = player.getEquipment().getItem(defs.getEquipSlot());
				if (item != null && item.getId() == shortcut.getId(player)) {
					player.getEquipment().handleEquipment(defs.getEquipSlot(),
							shortcut.getId(player),
							WorldPacketsDecoder.ACTION_BUTTON1_PACKET);
					return;
				}
			}
			int slotId = player.getInventory().getItems()
					.lookupSlot(shortcut.getId(player));
			if (slotId == -1) {
				player.getPackets().sendGameMessage("You don't have any left!");
				return;
			}
			Item item = player.getInventory().getItem(slotId);
			if (item == null || item.getId() != shortcut.getId(player)) {
				player.getPackets().sendGameMessage("You don't have any left!");
				return;
			}
			switch (packetId) {
			case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
				InventoryOptionsHandler.handleItemOption1(player, slotId,
						shortcut.getId(player), item);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
				InventoryOptionsHandler.handleItemOption2(player, slotId,
						shortcut.getId(player), item);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
				InventoryOptionsHandler.handleItemOption3(player, slotId,
						shortcut.getId(player), item);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
				InventoryOptionsHandler.handleItemOption4(player, slotId,
						shortcut.getId(player), item);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON5_PACKET:
				InventoryOptionsHandler.handleItemOption5(player, slotId,
						shortcut.getId(player), item);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON6_PACKET:
				InventoryOptionsHandler.handleItemOption6(player, slotId,
						shortcut.getId(player), item);
				break;
			case WorldPacketsDecoder.ACTION_BUTTON7_PACKET:
				InventoryOptionsHandler.handleItemOption7(player, slotId,
						shortcut.getId(player), item);
				break;
			}
			break;
		case MAGIC_ABILITY_SHORTCUT:
			int abilityId = shortcut.getId(player);
			// abilty ids here to distinguish from normal spells
			if (isMagicAbility(abilityId))
				triggerAbility(shortcut);
			else
				Magic.handleSpell(player, abilityId, packetId);
			break;
		case MELEE_ABILITY_SHORTCUT:
		case DEFENCE_ABILITY_SHORTCUT:
		case RANGED_ABILITY_SHORTCUT:
		case HEAL_ABILITY_SHORTCUT:
		case STRENGTH_ABILITY_SHORTCUT:
			triggerAbility(shortcut);
			break;
		case PRAYER_SHORTCUT:
			if (((PrayerShortcut) shortcut).curse != player.getPrayer()
					.isAncientCurses())
				return;
			player.getPrayer().delayUsePrayer(
					((PrayerShortcut) shortcut).prayerId, false);
			break;
		}
	}

	private static final int[][] SHARED_CD_MAPS = {
			{ 14682, 14699, 14664, 14664 },// Binding
			// abilities
			{ 14726, 14665, 14678 },// Barge, Surge, and Escape
			{ 14706, 14731 },// Asphxyiate, Destroy
	};

	// 2069 - Revenge

	public void setCooldownShared(int abilityMap, int cooldown) {
		for (int primaryIndex = 0; primaryIndex < SHARED_CD_MAPS.length; primaryIndex++) {
			int[] sharedAbilityMaps = SHARED_CD_MAPS[primaryIndex];
			for (int secondaryIndex = 0; secondaryIndex < sharedAbilityMaps.length; secondaryIndex++) {
				int sharedAbilityId = sharedAbilityMaps[secondaryIndex];
				if (sharedAbilityId == abilityMap) {
					for (int index = 0; index < sharedAbilityMaps.length; index++)
						setCooldown(sharedAbilityMaps[index], cooldown);
					break;
				}
			}
		}
		setCooldown(abilityMap, cooldown);
	}

	public void setCooldown(int abilityMap, int cooldown) {
		long currentCycle = Utils.currentWorldCycle();
		long cd = BAR_CYCLE + cooldown * 3 + 1;// currentCycle + cooldown;
		if (abilityMap == 14881)
			globalCooldown = cd;
		else
			cooldowns.put(abilityMap, cd);
		player.getPackets().sendExecuteScript(6570, abilityMap,
				(int) currentCycle, ((int) currentCycle) + cooldown, 1, 1);
	}

	public void reduceCooldown(int abilityMap, int reduction) {
		Long cd = cooldowns.get(abilityMap);
		if (cd == null)
			return;
		// int nextCD = (int) ((cd - Utils.currentWorldCycle()) - reduction);
		int nextCD = (int) ((cd - BAR_CYCLE) - reduction * 3);
		setCooldown(abilityMap, nextCD < 0 ? 0 : nextCD);
	}

	// suposed to be = but rs lets uu se 1 tick earlier
	private boolean hasCooldown(Shortcut shortcut) {
		// long currentTime = Utils.currentWorldCycle();
		if (globalCooldown > BAR_CYCLE)
			return true;
		int mapId = ClientScriptMap.getMap(CS_DATA_ID[shortcut.getType() - 1])
				.getIntValue(shortcut.getId(player));
		Long cooldown = cooldowns.get(mapId);
		return cooldown != null && cooldown > /* = */BAR_CYCLE;
	}

	private static final int THRESHOLD_ABILITY = 1;

	private static final String[] ABILITY_STYLES = { "melee", "melee",
			"ranged", "magic" };

	private boolean hasRequiriments(int abilityId, GeneralRequirementMap data,
			boolean warn, boolean setup) {
		int abilityStyle = data.getIntValue(2806);
		if (abilityStyle > 0) {
			int level = data.getIntValue(2807);
			if (level > 0) {
				int skill = abilityStyle == 1 ? Skills.ATTACK
						: abilityStyle == 2 ? Skills.STRENGTH
								: abilityStyle == 3 ? Skills.RANGE
										: abilityStyle == 4 ? Skills.MAGIC
												: abilityStyle == 5 ? Skills.DEFENCE
														: abilityStyle == 6 ? Skills.HITPOINTS
																: -1;
				if (skill != -1) {
					if (player.getSkills().getLevel(skill) < level) {
						if (warn)
							player.getPackets().sendGameMessage(
									"You require level " + level + " "
											+ Skills.SKILL_NAME[skill]
											+ " in order to use this ability.");
						return false;
					}
				}
			}
			if ((abilityStyle == 3 && abilityId == 3)
					|| (abilityStyle == 4 && abilityId == 2)
					|| (abilityStyle == 2 && abilityId == 2)
					|| (abilityStyle == 1 && abilityId == 6))
				return true;// The 'jump' abilities and backhand/ kick
			if (abilityStyle == 5) {
				if (!player.getEquipment().hasShield()
						&& data.getIntValue(2813) != 0) {
					if (warn)
						player.getPackets().sendGameMessage(
								"This ability requires a shield.");
					return false;
				}
			} else {
				if (!hasAbilityStyle(player, abilityStyle, true)) {
					if (warn)
						player.getPackets().sendGameMessage(
								"This ability requires a "
										+ ABILITY_STYLES[abilityStyle - 1]
										+ " weapon in your main hand.");
					return false;
				}
			}
			boolean offHandRequired = data.getIntValue(2811) == 1;
			if (offHandRequired) {
				boolean hasOffHand = player.getEquipment().hasOffHand();
				if (!hasOffHand
						|| !hasAbilityStyle(player, abilityStyle, false)) {
					if (warn)
						player.getPackets().sendGameMessage(
								"You need to be dual wielding "
										+ ABILITY_STYLES[abilityStyle - 1]
										+ " weapons to use this ability.");
					return false;
				}
			}
			boolean twoHandRequired = data.getIntValue(2812) == 1;
			if (twoHandRequired && !player.getEquipment().hasTwoHandedWeapon()) {
				if (warn)
					player.getPackets()
							.sendGameMessage(
									"You need a two-handed weapon to use this ability.");
				return false;
			}

			int abilityType = data.getIntValue(2799);
			// 0 - normal - 1 thresold - 2 - ultimate
			if (!setup
					&& abilityType != 0
					&& player.getCombatDefinitions()
							.getSpecialAttackPercentage() < (abilityType == THRESHOLD_ABILITY ? 50
							: 100)) {
				if (warn)
					player.getPackets().sendGameMessage(
							(abilityType == THRESHOLD_ABILITY ? "Threeshold"
									: "Ultimate")
									+ " abilities require you to have "
									+ (abilityType == THRESHOLD_ABILITY ? 50
											: 100)
									+ "% adrenaline before they can be used.");
				return false;
			}
		}
		return true;
	}

	private static boolean hasAbilityStyle(Player player, int abilityStyle,
			boolean mainHand) {
		int handStyle = player.getCombatDefinitions().getType(
				mainHand ? Equipment.SLOT_WEAPON : Equipment.SLOT_SHIELD);
		return ((abilityStyle == 1 || abilityStyle == 2) && handStyle == Combat.MELEE_TYPE)
				|| (abilityStyle == 3 && handStyle == Combat.RANGE_TYPE)
				|| (abilityStyle == 4 && handStyle == Combat.MAGIC_TYPE)
				|| abilityStyle == 6;
	}

	private void triggerAbility(Shortcut shortcut) {

		// already done somewhere else
		/*
		 * if(hasCooldown(shortcut)) {
		 * player.getPackets().sendGameMessage("Ability not ready yet.");
		 * return; }
		 */

		// cant use eoc abilities in legacy :L. at bar even if we enable nis
		if (player.getCombatDefinitions().getCombatMode() == CombatDefinitions.LEGACY_COMBAT_MODE)
			return;
		if (player.getCombatDefinitions().getCombatMode() == CombatDefinitions.MOMENTUM_COMBAT_MODE)
			player.getCombatDefinitions().setCombatMode(
					CombatDefinitions.MANUAL_COMBAT_MODE);

		removeBuffs();

		int bookId = shortcut.getType();
		int abilityId = shortcut.getId(player);

		int mapId = ClientScriptMap.getMap(CS_DATA_ID[bookId - 1]).getIntValue(
				abilityId);

		if (bookId == HEAL_ABILITY_SHORTCUT && abilityId == 16) { // special
			// attack
			// exeption
			Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
			if (weapon == null)
				return;
			if (!weapon.getDefinitions().hasSpecialAttack()) {
				player.getPackets()
						.sendGameMessage(
								"You can only do that with a weapon that can perform a special attack.");
				return;
			}
			setCooldown(14881, 3);
			if (player.hasInstantSpecial(weapon)) {
				player.performInstantSpecial(weapon);
				return;
			}
			player.getCombatDefinitions().setUsingSpecialAttack();
			return;
		}

		// another exeption here is incite and regenerate
		// (6570, 1, 1, b.ticks, 0, b.mapid)

		GeneralRequirementMap data = GeneralRequirementMap.getMap(mapId);

		if (!hasRequiriments(abilityId, data, true, false))
			return;

		int abilityType = data.getIntValue(2799);

		// adrenaline drain done auto too

		if (!shortcut.queue(player)) { // code one click abilities here
			switch (bookId) {
			case MAGIC_ABILITY_SHORTCUT:
				switch (abilityId) {
				case 2:// SURGE
					if (!canSurge(player))
						return;
					final WorldTile tile = getSurgeTile(player, 0, 10, true);
					if (tile == null || tile.matches(player)) {
						player.getPackets().sendGameMessage(
								"Destination unreachable.");
						return;
					}
					player.lock(2);
					player.setNextAnimation(new Animation(18358));
					player.setNextGraphics(new Graphics(3537, 5, 0));
					player.setNextForceMovement(new NewForceMovement(player, 0,
							tile, 1, Utils.getAngle(
									tile.getX() - player.getX(), tile.getY()
											- player.getY())));
					player.setAttackingDelay(Utils.currentTimeMillis() + 4000);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.setNextWorldTile(tile);
						}
					}, 1);
					break;
				case 8:// DETONATE
					if (!(player.getActionManager().getAction() instanceof PlayerCombatNew)
							|| player.getEffectsManager().hasActiveEffect(
									EffectType.DETONATE)) {
						// message rs says when no target
						return;
					}
					// turn detonate on. make it do emote(check how emote is
					// being gathered at combat)
					player.getEffectsManager().startEffect(
							new Effect(EffectType.DETONATE, 10, 0));// Six
					// seconds
					break;
				case 10:// METAMORPHISIS
					GeneralRequirementMap spellData = Magic.getSpellData(player
							.getCombatDefinitions().getSpellId());
					player.getEffectsManager().startEffect(
							new Effect(EffectType.METAMORPHISIS, 35, Magic
									.getSpellType(spellData)));
					break;
				}
				break;
			case RANGED_ABILITY_SHORTCUT:
				switch (abilityId) {
				case 3:// ESCAPE
					if (!canSurge(player))
						return;
					player.lock(2);
					final WorldTile tile = getSurgeTile(player, 0, -10, false);
					if (tile == null || tile.matches(player)) {
						player.getPackets().sendGameMessage(
								"Destination unreachable.");
						return;
					}
					player.setNextAnimation(new Animation(18527));
					player.setAttackingDelay(Utils.currentTimeMillis() + 4000);
					player.setNextGraphics(new Graphics(3526, 0, 0));
					player.setNextForceMovement(new NewForceMovement(player, 0,
							tile, 1, Utils.getAngle(
									player.getX() - tile.getX(), player.getY()
											- tile.getY())));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.setNextWorldTile(tile);
						}
					}, 1);
					break;
				}
				break;
			case MELEE_ABILITY_SHORTCUT:
				switch (abilityId) {
				case 2:// BARGE
					if (!canSurge(player))
						return;
					if (player.isBound())
						player.getEffectsManager().removeEffect(
								EffectType.BOUND);
					WorldTile tile = getSurgeTile(player, 0, 10, true);
					if (tile == null || tile.matches(player)) {
						player.getPackets().sendGameMessage(
								"Destination unreachable.");
						return;
					}
					Entity target = player.getCombatDefinitions()
							.getCurrentTarget();
					if (target != null) {
						if (!player.withinDistance(target, 10)) {
							player.getPackets()
									.sendGameMessage(
											"Too far from target to perform this ability!");
							return;
						}
						PlayerCombatNew combat;
						if (player.getActionManager().getAction() instanceof PlayerCombatNew)
							combat = (PlayerCombatNew) player
									.getActionManager().getAction();
						else {
							combat = new PlayerCombatNew(target);
							PlayerCombatNew.setWeaponAbilityDelay(player);
							player.getActionManager().setAction(combat);
						}
						player.faceEntity(target);
						tile = getSurgeTile(player, 0, 10, true, target);
						if (tile == null) {
							player.getPackets().sendGameMessage(
									"Destination unreachable.");
							return;
						}
						combat.setNextAbility(new StrengthAbilityShortcut(200));// Kick
						// is
						// the
						// next
						// ability
						// overide
					}
					player.setAttackingDelay(Utils.currentTimeMillis() + 4000);
					player.lock(2);
					player.setNextAnimation(new Animation(18147));
					player.setNextGraphics(new Graphics(3580));
					player.setNextForceMovement(new NewForceMovement(player, 0,
							tile, 1, player.getDirection()));
					final WorldTile lastTile = tile;
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.setNextWorldTile(lastTile);
						}
					}, 1);
					break;
				}
				break;
			case STRENGTH_ABILITY_SHORTCUT:
				if (abilityId == 10)
					player.getEffectsManager().startEffect(
							new Effect(EffectType.BERSERK, 33));
				break;
			case DEFENCE_ABILITY_SHORTCUT:
				Effect effect = null;
				switch (abilityId) {
				case 1:// ANTICIPATION
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.ANTICIPATION, 16));
					break;
				case 2:// FREEDOM
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.FREEDOM, 10));
					break;
				case 7:// REFLECT
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.REFLECT, 17));
					break;
				case 4:// RESONANCE
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.RESONANCE, 10));
					break;
				case 5:// PREPERATION
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.PREPARATION, 10));
					break;
				case 9:// REVENGE
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.REVENGE, 33, 1.0));
					break;
				case 10:
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.BARRICADE, 17));
					break;
				case 11:// REJUVINATE
					effect = new Effect(EffectType.REJUVINATE, 17,
							HitLook.HEALED_DAMAGE, null,
							(int) ((player.getMaxHitpoints() * 0.4) * 0.05), 2,
							player);
					player.getEffectsManager().startEffect(effect);
					break;
				case 12:// IMMORTALITY
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.IMMORTALITY, 50));
					break;
				case 14:// DEVOTION
					player.getEffectsManager().startEffect(
							effect = new Effect(EffectType.DEVOTION, 16));
					break;
				}
				player.setAttackingDelay(((effect.getCycle() * 600) - 6000)
						+ Utils.currentTimeMillis());
				break;
			case HEAL_ABILITY_SHORTCUT:
				switch (abilityId) {
				case 2:// INCITE
					if (player.getEffectsManager().hasActiveEffect(
							EffectType.INCITE)) {
						player.getEffectsManager().removeEffect(
								EffectType.INCITE);
						break;
					}
					player.getEffectsManager().startEffect(
							new Effect(EffectType.INCITE, -1));
					player.getPackets()
							.sendGameMessage("Incite is now active.");
					break;
				case 9:// GUTHIX_BLESSING
					player.getEffectsManager().startEffect(
							new Effect(EffectType.GUTHIX_BLESSING, 17,
									new GuthixBlessing(player, new WorldTile(
											player))));
					break;
				case 14:// ICE ASYLUM
					player.getEffectsManager().startEffect(
							new Effect(EffectType.ICE_ASYLUM, 33,
									new WorldTile(player), player
											.getMaxHitpoints() * 3));
					break;

				}
				break;
			}
		} else if (player.getActionManager().getAction() instanceof PlayerCombatNew) {
			PlayerCombatNew combat = (PlayerCombatNew) player
					.getActionManager().getAction();
			Entity target = combat.getTarget();
			if (target == null)
				return;
			switch (bookId) {
			case MAGIC_ABILITY_SHORTCUT:
				switch (abilityId) {
				case 11:// TSUNAMI
					if (!player.withinDistance(target, 5)) {
						player.getPackets()
								.sendGameMessage(
										"There are no valid targets in that direction.");
						return;
					}
					break;
				}
				break;
			case MELEE_ABILITY_SHORTCUT:
				switch (abilityId) {
				case 6:// BACKHAND
					if (!Utils.isOnRange(player, target, 1)) {
						player.getPackets().sendGameMessage(
								"Target is too far away.");
						return;
					}
					break;
				case 12:// METEOR STRIKE
					int maxHP = target.getMaxHitpoints(),
					currentHP = target.getHitpoints();
					if (currentHP <= maxHP / 2) {
						player.getPackets()
								.sendGameMessage(
										"This ability may only be used on targets with over 50% life points remaining.");
						return;
					}
					break;
				}
				break;
			case STRENGTH_ABILITY_SHORTCUT:
				switch (abilityId) {
				case 2:// KICK
					if (!Utils.isOnRange(player, target, 1)) {
						player.getPackets().sendGameMessage(
								"Target is too far away.");
						return;
					}
					break;
				}
				break;
			case DEFENCE_ABILITY_SHORTCUT:
				switch (abilityId) {
				case 6:// BASH
					if (!Utils.isOnRange(player, target, 1)) {
						player.getPackets().sendGameMessage(
								"Target is too far away.");
						return;
					}
					break;
				}
				break;
			}

			combat.setNextAbility(shortcut);
		}

		// adrenaline drain
		if (!(abilityType == 0 && player.getCombatDefinitions()
				.getCurrentTarget() == null))
			player.getCombatDefinitions().increaseSpecialAttack(
					abilityType == 0 ? player.getEffectsManager()
							.hasActiveEffect(EffectType.NATURAL_INSTINCT) ? 16
							: 8 : abilityType == 1 ? -15 : -100);

		setCooldownShared(mapId, data.getIntValue(2796));
		setCooldown(14881, 3);

		if (bookId == DEFENCE_ABILITY_SHORTCUT && abilityId == 14)// Manditory
			// exception
			player.getEffectsManager().processBuffTimer(
					player.getEffectsManager().getEffectForType(
							EffectType.DEVOTION), true);
	}

	public void removeBuffs() {
		player.getEffectsManager().removeEffectsWithAction(
				EffectsManager.COMBO_BUFFS);
	}

	public boolean isComboOn() {
		return player.getEffectsManager().hasActiveEffect(
				EffectsManager.COMBO_BUFFS);
	}

	private transient Shortcut queueShortcut;

	/*
	 * var1: 4164, 0 var1: 4513, 0 var1: 4164, 4
	 */

	private void setQueueShortcut(Shortcut shortcut, int shortcutIndex) {
		if (shortcutIndex != -1 && getQueueShortcutIndex() == shortcutIndex) { // -1
			// is
			// outside
			// book
			removeQueueShortcut();
			return;
		}
		player.getVarsManager().setVar(4164, shortcutIndex + 1);
		if (shortcut.getType() == HEAL_ABILITY_SHORTCUT
				&& shortcut.getId(player) == 1)
			player.getVarsManager().sendVarBit(REGENERATION_VARBIT_ID, 1);
		else if (queueShortcut != null
				&& queueShortcut.getType() == HEAL_ABILITY_SHORTCUT
				&& queueShortcut.getId(player) == 1)
			player.getVarsManager().sendVarBit(REGENERATION_VARBIT_ID, 0);
		queueShortcut = shortcut;
	}

	private void refreshQueueShortcut() {
		player.getVarsManager().forceSendVar(4164,
				player.getVarsManager().getValue(4164));
	}

	private void removeQueueShortcut() {
		player.getVarsManager().sendVar(4164, 0);
		if (queueShortcut != null
				&& queueShortcut.getType() == HEAL_ABILITY_SHORTCUT
				&& queueShortcut.getId(player) == 1)
			player.getVarsManager().sendVarBit(REGENERATION_VARBIT_ID, 0);
		queueShortcut = null;
	}

	public int getQueueShortcutIndex() {
		return player.getVarsManager().getValue(4164) - 1;
	}

	public boolean hasQueuedShortcut() {
		return queueShortcut != null;
	}

	private static boolean isMagicAbility(int abilityId) {
		// abilty ids here to distinguish from normal spells
		return (abilityId == 7 || abilityId == 1 || abilityId == 12
				|| abilityId == 6 || abilityId == 165 || abilityId == 3
				|| abilityId == 166 || abilityId == 171 || abilityId == 5
				|| abilityId == 2 || abilityId == 8 || abilityId == 4
				|| abilityId == 9 || abilityId == 10 || abilityId == 11
				|| abilityId == 164 || abilityId == 169);
	}

	private static long BAR_CYCLE;

	public static void addActionBarTask() {
		GameExecutorManager.fastExecutor.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				try {
					for (Player player : World.getPlayers()) {
						if (player == null || !player.isRunning())
							continue;
						player.getActionbar().processQueuedShortcut();
					}
					BAR_CYCLE++;
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, 0, 200);
	}

	public void processQueuedShortcut() {
		if (player.isLocked())
			return;
		if (queueShortcut != null)
			processShortcut(queueShortcut,
					WorldPacketsDecoder.ACTION_BUTTON1_PACKET,
					getQueueShortcutIndex(), true);
		else if (player.getCombatDefinitions().getCombatMode() == CombatDefinitions.REVOLUTION_COMBAT_MODE) {
			// revolution autopress
			if (!(player.getActionManager().getAction() instanceof PlayerCombatNew))
				return;
			Entity target = player.getCombatDefinitions().getCurrentTarget();
			if (target == null)
				return;
			/*
			 * long currentTime = Utils.currentWorldCycle(); if(globalCooldown >
			 * currentTime) return;
			 */
			if (globalCooldown > BAR_CYCLE)
				return;
			for (int i = 0; i < 9; i++) {
				Shortcut shortcut = shortcuts[currentBar][i];
				if (shortcut != null
						&& (shortcut.getType() == MELEE_ABILITY_SHORTCUT
								|| shortcut.getType() == DEFENCE_ABILITY_SHORTCUT
								|| shortcut.getType() == STRENGTH_ABILITY_SHORTCUT
								|| shortcut.getType() == RANGED_ABILITY_SHORTCUT
								|| shortcut.getType() == HEAL_ABILITY_SHORTCUT || (shortcut
								.getType() == MAGIC_ABILITY_SHORTCUT && isMagicAbility(shortcut
								.getId(player)))) && !hasCooldown(shortcut)) {

					GeneralRequirementMap data = GeneralRequirementMap
							.getMap(ClientScriptMap.getMap(
									CS_DATA_ID[shortcut.getType() - 1])
									.getIntValue(shortcut.getId(player)));
					int abilityType = data.getIntValue(2799);
					if (abilityType != 0
							|| !hasRequiriments(shortcut.getId(player), data,
									false, false))
						continue;
					pushShortcut(i, WorldPacketsDecoder.ACTION_BUTTON1_PACKET);
					player.getPackets().sendCSVarInteger(4098, data.getId());
					break;
				}
			}
		}
	}

	public void pushShortcutOnSomething(int index, Object target) {
		Shortcut shortcut = shortcuts[currentBar][index];
		if (shortcut == null)
			return;
		pushShortcutOnSomething(shortcut, target);
	}

	private void pushShortcutOnSomething(Shortcut shortcut, Object target) {
		if (player.getInterfaceManager().containsScreenInterface()
				|| player.getInterfaceManager().containsBankInterface()) {
			player.getPackets().sendGameMessage(
					"You're currently busy, and can't do that right now.");
			return;
		}
		switch (shortcut.getType()) {
		case MELEE_ABILITY_SHORTCUT:
		case DEFENCE_ABILITY_SHORTCUT:
		case RANGED_ABILITY_SHORTCUT:
		case HEAL_ABILITY_SHORTCUT:
		case STRENGTH_ABILITY_SHORTCUT:
			if (target instanceof Entity) {
				if (shortcut.queue(player)) {
					queueCombatAbility(shortcut, (Entity) target);
					return;
				}
			}
			break;
		case MAGIC_ABILITY_SHORTCUT:
			if (target instanceof Item) {
				int slotId = player.getInventory().getItems()
						.lookupSlot(((Item) target).getId());
				if (slotId == -1)
					return;
				Magic.handleSpellOnItem(player, shortcut.getId(player),
						(byte) slotId);
			} else if (target instanceof Entity) {
				if (shortcut.queue(player)) { // magic abilities
					queueCombatAbility(shortcut, (Entity) target);
					return;
				}
				Magic.handleSpellOnEntity(player, shortcut.getId(player),
						(Entity) target);
			}
			break;
		}

	}

	private void queueCombatAbility(Shortcut shortcut, Entity target) {
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (!player.isCanPvp() || !p2.isCanPvp()) {
				player.getPackets()
						.sendGameMessage(
								"You can only attack players in a player-vs-player area.");
				removeQueueShortcut();
				return;
			}
		} else if (target instanceof Familiar) {
			Familiar familiar = (Familiar) target;
			if (familiar == player.getFamiliar()) {
				player.getPackets().sendGameMessage(
						"You can't attack your own familiar.");
				removeQueueShortcut();
				return;
			}
			if (!familiar.canAttack(player)) {
				player.getPackets().sendGameMessage(
						"You can't attack this npc.");
				removeQueueShortcut();
				return;
			}
		} else if (target instanceof NPC) {
			if (!((NPC) target).getDefinitions().hasAttackOption()) {
				player.getPackets().sendGameMessage(
						"You can't attack this npc.");
				removeQueueShortcut();
				return;
			}
		}
		player.getActionManager().setAction(new PlayerCombatNew(target));
		useAbility(shortcut, WorldPacketsDecoder.ACTION_BUTTON1_PACKET);
	}

	public void useAbility(Shortcut shortcut, int packetId) {
		processShortcut(shortcut, packetId, -1, false);
	}

	public void useAbility(Shortcut shortcut, Object target) {
		pushShortcutOnSomething(shortcut, target);
	}

	public void setCurrentBar(int id) {
		currentBar = id;
		refreshActionBar();
	}

	public void shareBar() {

	}

	public void helpTrashCan() {
		player.getPackets()
				.sendGameMessage(
						"To remove an icon from the action bar, drag it onto the trashcan or the game world. Alternatively, you can select 'clear all' from the trashcan to remove all icons on your action bar.");
	}

	public void switchBlockIncomingShareOffers() {
		blockIncomingShareOffers = !blockIncomingShareOffers;
		refreshBlockIncomingShareOffers();
	}

	public void switchLockBar() {
		lockedBar = !lockedBar;
		refreshLockBar();
		unlockShortcuts(false);
		player.getPackets().sendGameMessage(
				"The action bar is now " + (lockedBar ? "" : "un") + "locked.");
	}

	public void refreshLockBar() {
		player.getVarsManager().sendVarBit(1892, lockedBar ? 1 : 0);
	}

	public void refreshBlockIncomingShareOffers() {
		player.getVarsManager().sendVarBit(22085,
				blockIncomingShareOffers ? 1 : 0);
	}

	public void increaseCurrentBar() {
		setCurrentBar(currentBar == shortcuts.length - 1 ? 0 : currentBar + 1);
	}

	public void desecreaseCurrentBar() {
		setCurrentBar(currentBar == 0 ? shortcuts.length - 1 : currentBar - 1);
	}

	public void refreshActionBar() {
		player.getVarsManager().sendVarBit(1893, currentBar + 1);
		for (int i = 0; i < shortcuts[currentBar].length; i++)
			refresh(i, false);
	}

	public void init() {
		cooldowns = new HashMap<Integer, Long>();
		refreshActionBar();
		refreshLockBar();
		refreshBlockIncomingShareOffers();
	}

	private void refresh(int index, boolean force) {
		Shortcut shortcut = shortcuts[currentBar][index];
		if (shortcut == null) {
			sendShortcutVar(index, 0, -1, force);
			return;
		}
		sendShortcutVar(index, shortcut.getType(), shortcut.getId(player),
				force);
	}

	public void sendShortcutVar(int barIndex, int type, int id, boolean force) {
		if (force) { // to save data from being sent every click lol
			player.getVarsManager().forceSendVar(
					barIndex >= 12 ? 4427 + (barIndex - 12) : 811 + barIndex,
					type != 0 ? -1 : id);
			player.getVarsManager().forceSendVar(
					barIndex >= 12 ? 4413 + (barIndex - 12) : 727 + barIndex,
					type != 0 ? (id << 4 | type) : 0);
		} else {
			player.getVarsManager().sendVar(
					barIndex >= 12 ? 4427 + (barIndex - 12) : 811 + barIndex,
					type != 0 ? -1 : id);
			player.getVarsManager().sendVar(
					barIndex >= 12 ? 4413 + (barIndex - 12) : 727 + barIndex,
					type != 0 ? (id << 4 | type) : 0);
		}
	}

	public void unlockActionBar(boolean menu) {
		unlockShortcuts(menu);
		if (!menu) {
			player.getPackets().sendIComponentSettings(1430, 1, -1, -1, 0);
			player.getPackets().sendIComponentSettings(1430, 0, -1, -1, 262150);
			player.getPackets().sendIComponentSettings(1430, 2, -1, -1, 0);
		}
		refreshButtons();
	}

	private void unlockShortcuts(boolean menu) {
		for (int i = 0; i < shortcuts[currentBar].length; i++) {
			player.getPackets().sendIComponentSettings(menu ? 1436 : 1430,
					(menu ? 25 : 55) + i * 13, -1, 1,
					!lockedBar || menu ? 15302654 : 2195454);
		}
	}

	public void refreshButtons() {
		player.getPackets().sendExecuteScript(6992); // unlocks action bar
		// buttons properly
	}

	private static boolean canSurge(Player player) {
		Controller c = player.getControlerManager().getControler();
		boolean surgeDisabled = c != null
				&& (c instanceof BorkController || c instanceof DuelArena
						|| c instanceof DTControler || c instanceof FightCaves
						|| c instanceof FightKiln
						|| c instanceof HouseControler
						|| c instanceof StealingCreationController || c instanceof DuelArena);
		if (surgeDisabled)
			player.getPackets()
					.sendGameMessage(
							"You are in an area which is preventing you from performing that ability.");
		return !surgeDisabled;
	}

	private static WorldTile getSurgeTile(Player player, int start, int end,
			boolean increment) {
		return getSurgeTile(player, start, end, increment, null);
	}

	private static WorldTile getSurgeTile(Player player, int start, int end,
			boolean increment, Entity target) {
		byte[] dirs = Utils.getDirection(player.getDirection());
		WorldTile lastStep = null;
		for (int steps = start; increment ? steps < end : steps > end; steps += (increment ? 1
				: -1)) {
			WorldTile step = new WorldTile(player.getX() + (dirs[0] * steps),
					player.getY() + (dirs[1] * steps), player.getPlane());
			if (target != null
					&& Utils.colides(target.getX(), target.getY(),
							target.getSize(), step.getX(), step.getY(),
							player.getSize())
					|| !player.clipedProjectile(step, true)
					|| !World.isTileFree(step.getPlane(), step.getX(),
							step.getY(), player.getSize()))
				break;
			lastStep = step;
		}
		return lastStep;
	}

	public static void main(String[] args) {
	}
}