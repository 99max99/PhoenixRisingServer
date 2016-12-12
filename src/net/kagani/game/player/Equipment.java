package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.cache.loaders.BodyDefinitions;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.item.ItemsContainer;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.actions.Bonfire;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.ItemTransportation;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.content.SkillCapeCustomizer;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.controllers.SorceressGarden;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.network.decoders.WorldPacketsDecoder;
import net.kagani.network.decoders.handlers.ButtonHandler;
import net.kagani.utils.ItemExamines;
import net.kagani.utils.ItemWeights;
import net.kagani.utils.Utils;

public final class Equipment implements Serializable {

	private static final long serialVersionUID = -4147163237095647617L;

	public static final byte SLOT_HAT = 0, SLOT_CAPE = 1, SLOT_AMULET = 2,
			SLOT_WEAPON = 3, SLOT_CHEST = 4, SLOT_SHIELD = 5, SLOT_LEGS = 7,
			SLOT_HANDS = 9, SLOT_FEET = 10, SLOT_RING = 12, SLOT_ARROWS = 13,
			SLOT_AURA = 14, SLOT_POCKET = 15, SLOT_WINGS = 17;

	private ItemsContainer<Item> items;
	private ItemsContainer<Item> cosmeticItems;

	private transient Player player;
	private transient int equipmentHpIncrease;
	private transient double equipmentWeight;
	private int costumeColor;

	public Equipment() {
		items = new ItemsContainer<Item>(
				BodyDefinitions.getEquipmentContainerSize(), false);
		cosmeticItems = new ItemsContainer<Item>(
				BodyDefinitions.getEquipmentContainerSize(), false);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		player.getPackets().sendItems(94, items);
		refresh(null);
	}

	public boolean hasItem(int item) {
		for (int i = 0; i < 15; i++)
			if (getItem(i) != null)
				if (getItem(i).getId() == item)
					return true;
		return false;
	}

	public void checkItems() {
		int size = items.getSize();
		int newSize = BodyDefinitions.getEquipmentContainerSize();
		if (size != newSize) {
			Item[] copy = items.getItemsCopy();
			items = new ItemsContainer<Item>(newSize, false);
			cosmeticItems = new ItemsContainer<Item>(newSize, false);
			items.addAll(copy);
		} else if (cosmeticItems == null) // temporary
			cosmeticItems = new ItemsContainer<Item>(newSize, false);

		for (int i = 0; i < size; i++) {
			Item item = items.get(i);
			if (item == null)
				continue;
			if (!ItemConstants.canWear(item, player)) {
				items.set(i, null);
				player.getInventory().addItemDrop(item.getId(),
						item.getAmount());
			}
		}
	}

	public void refresh(int... slots) {
		if (slots != null)
			player.getPackets().sendUpdateItems(94, items, slots);
		player.getCombatDefinitions().refreshBonuses();
		refreshConfigs(slots == null);
	}

	public void reset() {
		items.reset();
		init();
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public void sendExamine(int slotId) {
		Item item = items.get(slotId);
		if (item == null)
			return;
		player.getPackets()
				.sendInterfaceMessage(
						1464,
						15,
						0,
						slotId,
						ItemExamines.getExamine(item)
								+ (ItemConstants.isTradeable(item) ? "<br>GE guide price: "
										+ Utils.format(GrandExchange
												.getPrice(item.getId()))
										+ " gp" : ""));
	}

	public void handleEquipment(int slotId, int itemId, int packetId) {
		Item item = getItem(slotId);
		if (item == null || item.getId() != itemId)
			return;
		if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET
				|| (slotId == Equipment.SLOT_AURA && packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET))
			ButtonHandler.sendRemove(player, slotId, false);
		else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
			sendExamine(slotId);
		else {

			int option = packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET ? 0
					: packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET ? 1
							: packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET ? 2
									: packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET ? 3
											: -1;

			if (item.getDefinitions()
					.containsEquipmentOption(option, "Inspect")
					|| item.getDefinitions().containsEquipmentOption(option,
							"Check")
					|| item.getDefinitions().containsEquipmentOption(option,
							"Check state")
					|| item.getDefinitions().containsEquipmentOption(option,
							"Check-charges")
					|| item.getDefinitions().containsEquipmentOption(option,
							"Check charges")) {
				if (player.getCharges().checkCharges(item))
					return;
			}

			switch (slotId) {
			case Equipment.SLOT_HAT:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == 22332) {
						player.stopAll();
						player.getInterfaceManager().sendCentralInterface(1153);
						player.getPackets().sendIComponentText(1153, 139, "-1");
						player.getPackets().sendIComponentText(1153, 143, "-1");
						player.getPackets().sendIComponentText(1153, 134, "-1");
					}
					if (hatId == 24437 || hatId == 24439 || hatId == 24440
							|| hatId == 24441)
						player.getDialogueManager().startDialogue(
								"FlamingSkull",
								player.getEquipment().getItem(
										Equipment.SLOT_HAT), -1);
				}
				break;
			case Equipment.SLOT_CAPE:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20769 || capeId == 20771 || capeId == 32152
							|| capeId == 32153)
						SkillCapeCustomizer.startCustomizing(player, capeId);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					if (item.getDefinitions().containsEquipmentOption(0,
							"Boost")) {
						player.getPackets()
								.sendGameMessage(
										"You have been given a slight boost by your skillcape.",
										true);
						return;
					}
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20767 || capeId == 32151)
						SkillCapeCustomizer.startCustomizing(player, capeId);
					else if (capeId == 20763 || capeId == 24709)
						player.getEmotesManager().useBookEmote(39);
					else if (capeId == 20769 || capeId == 20771
							|| capeId == 32152 || capeId == 32153
							|| capeId == 19748 || capeId == 15349
							|| capeId == 15347 || capeId == 15345) {
						if (player.isLocked()
								|| player.getControlerManager().getControler() != null) {
							player.getPackets().sendGameMessage(
									"You cannot tele anywhere from here.");
							return;
						}

						WorldTasksManager.schedule(new WorldTask() {
							int timer;

							@Override
							public void run() {
								if (timer == 0) {
									player.lock(2);
									player.setNextGraphics(new Graphics(2670));
									player.setNextAnimation(new Animation(3254));
								}
								if (timer == 1) {
									player.setNextWorldTile(new WorldTile(2606,
											3222, 0));
								}
								if (timer == 2) {
									player.setNextGraphics(new Graphics(2671));
									player.setNextAnimation(new Animation(3255));
								}
								timer++;
							}
						}, 0, 1);
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					int capeId = player.getEquipment().getCapeId();

					if (capeId == 20767 || capeId == 32151)
						Magic.sendNormalTeleportSpell(player, 0, 0,
								new WorldTile(2276, 3314, 1));
					else if (capeId == 20769 || capeId == 20771
							|| capeId == 32152 || capeId == 32153
							|| capeId == 19748 || capeId == 15349
							|| capeId == 15347 || capeId == 15345) {
						if (player.isLocked()
								|| player.getControlerManager().getControler() != null) {
							player.getPackets().sendGameMessage(
									"You cannot tele anywhere from here.");
							return;
						}

						WorldTasksManager.schedule(new WorldTask() {
							int timer;

							@Override
							public void run() {
								if (timer == 0) {
									player.lock(2);
									player.setNextGraphics(new Graphics(2670));
									player.setNextAnimation(new Animation(3254));
								}
								if (timer == 1) {
									player.setNextWorldTile(new WorldTile(2674,
											3375, 0));
								}
								if (timer == 2) {
									player.setNextGraphics(new Graphics(2671));
									player.setNextAnimation(new Animation(3255));
								}
								timer++;
							}
						}, 0, 1);
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					int capeId = player.getEquipment().getCapeId();
					if (capeId == 20769 || capeId == 20771 || capeId == 32152
							|| capeId == 32153)
						Magic.sendNormalTeleportSpell(player, 0, 0,
								new WorldTile(2276, 3314, 1));
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					int capeId = player.getEquipment().getCapeId();
					int summonLevel = player.getSkills().getLevelForXp(
							Skills.SUMMONING);
					if (capeId == 20769 || capeId == 20771 || capeId == 19748
							|| capeId == 28301 || capeId == 28302
							|| capeId == 32152 || capeId == 32153) {
						if (player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
							player.lock(3);
							player.setNextAnimation(new Animation(8502));
							player.setNextGraphics(new Graphics(1308));
							player.getSkills().set(Skills.SUMMONING,
									summonLevel);
							player.getPackets()
									.sendGameMessage(
											"You have recharged your Summoning points.",
											true);
						} else
							player.getPackets().sendGameMessage(
									"You already have full Summoning points.");
					}
				}
				break;
			case Equipment.SLOT_AMULET:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					if (item.getDefinitions().containsEquipmentOption(0, "Rub")) {
						ItemTransportation.transportationDialogue(player, item,
								true);
						return;
					}
					ItemTransportation.sendTeleport(player, item, 0, true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					ItemTransportation.sendTeleport(player, player
							.getEquipment().getItem(Equipment.SLOT_AMULET), 1,
							true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					ItemTransportation.sendTeleport(player, player
							.getEquipment().getItem(Equipment.SLOT_AMULET), 2,
							true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					ItemTransportation.sendTeleport(player, player
							.getEquipment().getItem(Equipment.SLOT_AMULET), 3,
							true);
				}
				break;
			case Equipment.SLOT_WEAPON:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int weaponId = player.getEquipment().getWeaponId();
					if (weaponId == 15484)
						player.getInterfaceManager().gazeOrbOfOculus();
					if (weaponId == 9044 || weaponId == 9046
							|| weaponId == 9048)
						player.getDialogueManager().startDialogue(
								"PharaohSceptre", weaponId);
					else if (weaponId == 14057) // broomstick
						player.setNextAnimation(new Animation(10532));
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					int weaponId = player.getEquipment().getWeaponId();
					if (weaponId == 14057) // broomstick
						SorceressGarden.teleportToSocreressGarden(player, true);
					if (weaponId == 9044 || weaponId == 9046
							|| weaponId == 9048)
						player.getDialogueManager().startDialogue(
								"PharaohSceptre", weaponId);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					int weaponId = player.getEquipment().getWeaponId();
					if (weaponId == 9044 || weaponId == 9046
							|| weaponId == 9048)
						player.getDialogueManager().startDialogue(
								"PharaohSceptre", weaponId);
				}
				break;
			case Equipment.SLOT_SHIELD:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int shieldId = player.getEquipment().getShieldId();
					if (shieldId == 11283) {
						if (player.getDFSDelay() >= Utils.currentTimeMillis()) {
							player.getPackets()
									.sendGameMessage(
											"You must wait two minutes before performing this attack once more.");
							return;
						}
						player.getTemporaryAttributtes().put(
								"dfs_shield_active", true);
					} else if (shieldId == 11284)
						player.getPackets().sendGameMessage(
								"You don't have any charges in your shield.");
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					if (item.getDefinitions().containsEquipmentOption(1,
							"Provoke")) {
						Entity target = player.getCombatDefinitions()
								.getCurrentTarget();
						if (target == null || !(target instanceof NPC)) {
							player.getPackets().sendGameMessage(
									"You can't use provoke without a target.");
							return;
						}
						Long cd = (Long) player.getTemporaryAttributtes().get(
								Key.PROVOKE);
						if (cd != null && cd > Utils.currentWorldCycle()) {
							player.getPackets()
									.sendGameMessage(
											"You can't use provoke while it is on cooldown.");
							return;
						}
						player.getTemporaryAttributtes().put(Key.PROVOKE,
								Utils.currentWorldCycle() + 20);
						player.setNextAnimation(new Animation(18130));
						if (target instanceof NPC)
							((NPC) target).setTarget(player);
					}
				}
				break;
			case Equipment.SLOT_HANDS:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int glovesId = player.getEquipment().getGlovesId();
					if ((glovesId >= 24450 && glovesId <= 24454)
							|| (glovesId >= 22358 && glovesId <= 22369))
						player.getCharges().checkPercentage(
								"The gloves are " + ChargesManager.REPLACE
										+ "% degraded.", glovesId, true);
				}
				break;
			case Equipment.SLOT_RING:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int ringId = player.getEquipment().getRingId();
					if (ringId == 15707) {
						player.getDungManager().openPartyInterface();
						return;
					}
					if (itemId == 27477) {
						player.getDialogueManager().startDialogue(
								"SixthAgeCircuit");
						return;
					}
					if (item.getDefinitions().containsEquipmentOption(0, "Rub")) {
						ItemTransportation.transportationDialogue(player, item,
								true);
						return;
					}
					ItemTransportation.sendTeleport(player, player
							.getEquipment().getItem(Equipment.SLOT_RING), 0,
							true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					int ringId = player.getEquipment().getRingId();
					if (ringId == 15707) {
						Magic.sendTeleportSpell(player, 13652, 13654, 2602,
								2603, 1, 0, new WorldTile(3447, 3694, 0), 10,
								true, Magic.ITEM_TELEPORT);
						return;
					}
					ItemTransportation.sendTeleport(player, player
							.getEquipment().getItem(Equipment.SLOT_RING), 1,
							true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					ItemTransportation.sendTeleport(player, player
							.getEquipment().getItem(Equipment.SLOT_RING), 2,
							true);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					ItemTransportation.sendTeleport(player, player
							.getEquipment().getItem(Equipment.SLOT_RING), 3,
							true);
				}
				break;
			case Equipment.SLOT_AURA:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getAuraManager().activate();
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getAuraManager().sendAuraRemainingTime();
				break;
			}
		}
	}

	public void unlockEquipment(boolean menu) {
		player.getPackets().sendIComponentSettings(menu ? 1462 : 1464,
				menu ? 14 : 15, 0, 15, 15302654);
		player.getPackets().sendIComponentSettings(menu ? 1462 : 1464,
				menu ? 20 : 13, 2, 12, 2);
	}

	public void refreshConfigs(boolean init) {
		double hpIncrease = 0;
		if (player.getEffectsManager().hasActiveEffect(EffectType.BONFIRE)) {
			int maxhp = player.getSkills().getLevel(Skills.HITPOINTS) * 10;
			hpIncrease += (maxhp * Bonfire.getBonfireBoostMultiplier(player))
					- maxhp;
		}
		if (player.getHpBoostMultiplier() != 0) {
			int maxhp = player.getSkills().getLevel(Skills.HITPOINTS) * 10;
			hpIncrease += maxhp * player.getHpBoostMultiplier();
		}

		// skip gear health as that doesnt appear as buff, its auto

		if (player.getVarsManager().sendVarBit(16463, (int) hpIncrease))
			player.updateBuffs();
		hpIncrease += player.getCombatDefinitions().getStats()[CombatDefinitions.HEALTH];

		if ((int) hpIncrease != equipmentHpIncrease) {
			equipmentHpIncrease = (int) hpIncrease;
			int maxHP = player.getMaxHitpoints();
			if (player.getHitpoints() > maxHP) {
				player.setHitpoints(maxHP);
				player.refreshHitPoints();
			}
		}
		double w = 0;
		for (Item item : items.getItems()) {
			if (item == null)
				continue;
			w += ItemWeights.getWeight(item, true);
		}
		equipmentWeight = w;
		player.getPackets().refreshWeight();
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1)))
				return true;
		}
		return false;
	}

	public static boolean hideArms(Item item) {
		return item.getDefinitions().getEquipLookHideSlot() == 6;
	}

	public static boolean hideHair(Item item) {
		return item.getDefinitions().getEquipLookHideSlot() == 8;
	}

	public static boolean showBear(Item item) {
		String name = item.getName().toLowerCase();
		return !hideHair(item) || name.contains("horns")
				|| name.contains("hat") || name.contains("afro")
				|| name.contains("cowl") || name.contains("tattoo")
				|| name.contains("headdress") || name.contains("hood")
				|| (name.contains("mask") && !name.contains("h'ween"))
				|| (name.contains("helm")/* && !name.contains("full") */);
	}

	public static int getItemSlot(int itemId) {
		return ItemDefinitions.getItemDefinitions(itemId).getEquipSlot();
	}

	public static boolean isTwoHandedWeapon(Item item) {
		return item.getDefinitions().getEquipLookHideSlot() == 5;
	}

	public int getWeaponStance() {
		boolean combatStance = player.getCombatDefinitions().isCombatStance();
		boolean legacy = player.getCombatDefinitions().getCombatMode() == CombatDefinitions.LEGACY_COMBAT_MODE;
		Item weapon = items.get(3);
		if (weapon == null) {
			Item offhand = items.get(SLOT_SHIELD);
			if (offhand == null)
				return combatStance ? 2688 : 2699;
			int emote = offhand.getDefinitions().getCombatOpcode(
					combatStance ? (legacy ? 4384 : 2955) : (legacy ? 4383
							: 2954)/*
									 * 4368 : 4367
									 */);
			return emote == 0 ? combatStance ? 2688 : 2699 : emote;
		}
		int emote = weapon.getDefinitions()
				.getCombatOpcode(
						combatStance ? (legacy ? 4384 : 2955) : (legacy ? 4383
								: 2954)/*
										 * 4368 : 4367
										 */);
		return emote == 0 ? combatStance ? 2688 : 2699 : emote;

		// return weapon.getDefinitions().getRenderAnimId();
	}

	/*
	 * none for legacy
	 */
	public int getWeaponEndCombatEmote() {
		Item weapon = items.get(3);
		if (weapon == null) {
			Item offhand = items.get(SLOT_SHIELD);
			if (offhand == null)
				return -1;
			int emote = offhand.getDefinitions().getCombatOpcode(2918);
			return emote == 0 ? 18025 : emote;
		}
		int emote = weapon.getDefinitions().getCombatOpcode(2918);
		return emote == 0 ? 18025 : emote;
	}

	public boolean hasShield() {
		Item item = items.get(5);
		return items.get(5) != null && item.getDefinitions().isShield();
	}

	public boolean hasOffHand() {
		Item item = items.get(SLOT_SHIELD);
		return items.get(5) != null
				&& item.getDefinitions().getCombatMap() != null;
	}

	public int getWeaponId() {
		Item item = items.get(SLOT_WEAPON);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getChestId() {
		Item item = items.get(SLOT_CHEST);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getId(int slot) {
		Item item = items.get(slot);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getHatId() {
		Item item = items.get(SLOT_HAT);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getShieldId() {
		Item item = items.get(SLOT_SHIELD);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getLegsId() {
		Item item = items.get(SLOT_LEGS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getPocketId() {
		Item item = items.get(SLOT_POCKET);
		if (item == null)
			return -1;
		return item.getId();
	}

	public void removeAmmo(int ammoId, int amount) {
		if (amount == -1) {
			items.remove(SLOT_WEAPON, new Item(ammoId, 1));
			refresh(SLOT_WEAPON);
			if (items.get(SLOT_WEAPON) == null)
				player.getAppearence().generateAppearenceData();
		} else if (amount == -2) {
			items.remove(SLOT_SHIELD, new Item(ammoId, 1));
			refresh(SLOT_SHIELD);
			if (items.get(SLOT_SHIELD) == null)
				player.getAppearence().generateAppearenceData();
		} else {
			items.remove(SLOT_ARROWS, new Item(ammoId, amount));
			refresh(SLOT_ARROWS);
		}
	}

	public int getAuraId() {
		Item item = items.get(SLOT_AURA);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getCapeId() {
		Item item = items.get(SLOT_CAPE);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getRingId() {
		Item item = items.get(SLOT_RING);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getAmmoId() {
		Item item = items.get(SLOT_ARROWS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public void deleteItem(int itemId, int amount) {
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void addItem(int itemId, int amount) {
		Item[] itemsBefore = items.getItemsCopy();
		items.add(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getItems()[index])
				changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public int getBootsId() {
		Item item = items.get(SLOT_FEET);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getGlovesId() {
		Item item = items.get(SLOT_HANDS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public int getEquipmentHpIncrease() {
		return equipmentHpIncrease;
	}

	public void setEquipmentHpIncrease(int hp) {
		this.equipmentHpIncrease = hp;
	}

	public boolean wearingArmour() {
		return getItem(SLOT_HAT) != null || getItem(SLOT_CAPE) != null
				|| getItem(SLOT_AMULET) != null || getItem(SLOT_WEAPON) != null
				|| getItem(SLOT_CHEST) != null || getItem(SLOT_SHIELD) != null
				|| getItem(SLOT_LEGS) != null || getItem(SLOT_HANDS) != null
				|| getItem(SLOT_FEET) != null || getItem(SLOT_AURA) != null;
	}

	public int getAmuletId() {
		Item item = items.get(SLOT_AMULET);
		if (item == null)
			return -1;
		return item.getId();
	}

	public boolean hasTwoHandedWeapon() {
		Item weapon = items.get(SLOT_WEAPON);
		return weapon != null && isTwoHandedWeapon(weapon);
	}

	// player.isCanPvp() || costume == null || (costume.getType() != 0 &&
	// (player.getAppearence().isMale() ? 1 : 2) != costume.getType())
	public ItemsContainer<Item> getCosmeticItems() {
		return cosmeticItems;
	}

	public boolean isCanDisplayCosmetic() {
		return !player.isCanPvp();
	}

	public int getCostumeColor() {
		return costumeColor;
	}

	public void setCostumeColor(int costumeColor) {
		this.costumeColor = costumeColor;
		player.getAppearence().generateAppearenceData();
	}

	public double getEquipmentWeight() {
		return equipmentWeight;
	}

	public void refreshEquipmentInterfaceBonuses() {
		player.getVarsManager()
				.sendVar(
						1037,
						player.getCombatDefinitions().getStats()[CombatDefinitions.MELEE_ACCURACY_PENALTY] / 10);
		player.getVarsManager()
				.sendVar(
						1038,
						player.getCombatDefinitions().getStats()[CombatDefinitions.RANGE_ACCURACY_PENALTY] / 10);
		player.getVarsManager()
				.sendVar(
						1039,
						player.getCombatDefinitions().getStats()[CombatDefinitions.MAGE_ACCURACY_PENALTY] / 10);
		// main hand
		player.getVarsManager().sendVar(715,
				player.getCombatDefinitions().getHandDamage(false) / 10);
		player.getVarsManager().sendVar(3531,
				player.getCombatDefinitions().getAbilitiesDamage() / 10);
		player.getVarsManager().sendVar(3561,
				player.getCombatDefinitions().getSkillAccuracy(false) / 10);
		player.getVarsManager().sendVar(717,
				player.getCombatDefinitions().getStyle(false));
		// offhand
		player.getVarsManager().sendVar(716,
				player.getCombatDefinitions().getHandDamage(true) / 10);
		player.getVarsManager().sendVar(3562,
				player.getCombatDefinitions().getSkillAccuracy(true) / 10);
		player.getVarsManager().sendVar(718,
				player.getCombatDefinitions().getStyle(true));

		// armor
		player.getVarsManager()
				.sendVar(
						711,
						player.getCombatDefinitions().getStats()[CombatDefinitions.ARMOR] / 10);
		player.getVarsManager().sendVar(3563,
				player.getCombatDefinitions().getDefenceArmor() / 10);
		player.getVarsManager()
				.sendVar(
						712,
						player.getCombatDefinitions().getStats()[CombatDefinitions.ARMOR_MELEE_WEAKNESS]);
		player.getVarsManager()
				.sendVar(
						713,
						player.getCombatDefinitions().getStats()[CombatDefinitions.ARMOR_RANGE_WEAKNESS]);
		player.getVarsManager()
				.sendVar(
						714,
						player.getCombatDefinitions().getStats()[CombatDefinitions.ARMOR_MAGIC_WEAKNESS]);

		player.getVarsManager().sendVarBit(21707, 0); // armor pve def
		player.getVarsManager().sendVar(4379, 0); // armor pvp def

		player.getPackets().sendCSVarInteger(779, 2702); // sets equip inter
		// render anim
		player.getPackets().sendExecuteScript(6992);
	}

	public static boolean isCape(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

}
