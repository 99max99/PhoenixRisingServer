package net.kagani.game.player.content;

import java.util.ArrayList;
import java.util.List;

import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.Sawmill;
import net.kagani.game.minigames.Sawmill.Plank;
import net.kagani.game.minigames.clanwars.ClanWarRequestController;
import net.kagani.game.minigames.duel.DuelArena;
import net.kagani.game.minigames.stealingcreation.StealingCreationController;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.InterfaceManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.DreamSpellAction;
import net.kagani.game.player.actions.HomeTeleport;
import net.kagani.game.player.actions.PlayerCombatNew;
import net.kagani.game.player.actions.Cooking.Cookables;
import net.kagani.game.player.actions.Smelting.SmeltingBar;
import net.kagani.game.player.actions.WaterFilling.Fill;
import net.kagani.game.player.content.ectofuntus.Ectofuntus;
import net.kagani.game.player.content.prayer.Burying.Bone;
import net.kagani.game.player.controllers.Kalaboss;
import net.kagani.game.player.controllers.Wilderness;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.network.decoders.WorldPacketsDecoder;
import net.kagani.utils.Utils;

/*
 * content package used for static stuff
 */
public class Magic {

	public static final int MAGIC_TELEPORT = 0, ITEM_TELEPORT = 1,
			OBJECT_TELEPORT = 2;

	// Normal Runes
	private static int AIR_RUNE = 556;
	private static int WATER_RUNE = 555;
	public static int EARTH_RUNE = 557;
	private static int FIRE_RUNE = 554;
	private static int MIND_RUNE = 558;
	private static int CHAOS_RUNE = 562;
	private static int DEATH_RUNE = 560;
	private static int BLOOD_RUNE = 565;
	private static int BODY_RUNE = 559;
	private static int COSMIC_RUNE = 564;
	public static int ASTRAL_RUNE = 9075;
	private static int NATURE_RUNE = 561;
	private static int LAW_RUNE = 563;
	private static int SOUL_RUNE = 566;

	// Special Runes
	private static int ELEMENTAL_RUNE = 12850;
	private static int CATALYTIC_RUNE = 12851;
	private static int ARMADYL_RUNE = 21773;

	public static final boolean hasInfiniteRunes(int runeId, int... itemIds) {
		for (int id : itemIds) {
			ItemDefinitions defs = ItemDefinitions.getItemDefinitions(id);
			if (defs.getCSOpcode(972) == 1 && runeId == AIR_RUNE
					|| defs.getCSOpcode(973) == 1 && runeId == WATER_RUNE
					|| defs.getCSOpcode(974) == 1 && runeId == EARTH_RUNE
					|| defs.getCSOpcode(975) == 1 && runeId == FIRE_RUNE)
				return true;
		}
		return false;
	}

	public static boolean hasSpecialRunes(Player player, int runeId,
			int amountRequired) {
		if (player.getInventory().containsItem(ELEMENTAL_RUNE, amountRequired)) {
			if (runeId == AIR_RUNE || runeId == WATER_RUNE
					|| runeId == EARTH_RUNE || runeId == FIRE_RUNE)
				return true;
		}
		if (player.getInventory().containsItem(CATALYTIC_RUNE, amountRequired)) {
			if (runeId == ARMADYL_RUNE || runeId == MIND_RUNE
					|| runeId == CHAOS_RUNE || runeId == DEATH_RUNE
					|| runeId == BLOOD_RUNE || runeId == BODY_RUNE
					|| runeId == NATURE_RUNE || runeId == ASTRAL_RUNE
					|| runeId == SOUL_RUNE || runeId == LAW_RUNE)
				return true;
		}
		return false;
	}

	public static int getRuneForId(int runeId) {
		if (runeId == AIR_RUNE || runeId == WATER_RUNE || runeId == EARTH_RUNE
				|| runeId == FIRE_RUNE)
			return ELEMENTAL_RUNE;
		else if (runeId == ARMADYL_RUNE || runeId == DEATH_RUNE
				|| runeId == MIND_RUNE || runeId == CHAOS_RUNE
				|| runeId == BLOOD_RUNE || runeId == BODY_RUNE
				|| runeId == NATURE_RUNE || runeId == ASTRAL_RUNE
				|| runeId == SOUL_RUNE || runeId == LAW_RUNE)
			return CATALYTIC_RUNE;
		return -1;
	}

	public static final boolean handleCombatSpell(Player player, int spellId,
			int set, boolean delete) {
		GeneralRequirementMap data = getSpellData(spellId);
		if (data == null)
			return false;
		int spellBook = getSpellBook(data);// spellbook 3 means shared by all
		// books
		if (spellBook != 3
				&& spellBook != player.getCombatDefinitions().getSpellBook())
			return false;
		if (data == null || !hasLevel(player, data))
			return false;
		// 0 - temp cast click target
		// 2 - temp cast spell select(new)
		// 1 - autocast
		// if set -1, dont set spell
		// runes are no longer checked when autocasting/temp casting setting
		if (set >= 0) {
			if (set == 0 || set == 2) {
				if (player.getCombatDefinitions()
						.getType(Equipment.SLOT_WEAPON) != Combat.MAGIC_TYPE) {
					player.getPackets()
							.sendGameMessage(
									"This ability requires a magic weapon in your main hand.");
					return false;
				}
				if (set == 2
						&& !(player.getActionManager().getAction() instanceof PlayerCombatNew)) {
					Entity target = player.getCombatDefinitions()
							.getCurrentTarget();
					if (target == null || target.isDead()
							|| target.hasFinished()) {
						player.getPackets().sendGameMessage(
								"You don't have a target.");
						return false;
					}
					if (!player.withinDistance(target)) {
						player.getPackets().sendGameMessage(
								"Your target is too far away.");
						return false;
					}
					setCombat(player, target);
				}
				player.getTemporaryAttributtes().put("tempCastSpell", spellId);
			} else if (isAutoCastSpell(data))
				player.getCombatDefinitions().setAutoCastSpell(spellId);
		} else {
			return checkRunes(player, data, delete);
		}
		return true;

	}

	public static GeneralRequirementMap getSpellData(int spellId) {
		int id = ClientScriptMap.getMap(6740).getIntValue(spellId);
		return id == -1 ? null : GeneralRequirementMap.getMap(id);
	}

	public static String getSpellName(GeneralRequirementMap data) {
		return data.getStringValue(2794);
	}

	public static int getSpellDamage(GeneralRequirementMap data) {
		return data.getIntValue(2877);
	}

	public static int getSpellLevel(GeneralRequirementMap data) {
		return data.getIntValue(2807);
	}

	private static boolean hasLevel(Player player, GeneralRequirementMap data) {
		return Magic.checkSpellLevel(player, getSpellLevel(data));
	}

	private static WorldTile getSpellTeleport(GeneralRequirementMap data) {
		return new WorldTile(data.getIntValue(2941));
	}

	private static double getSpellXP(GeneralRequirementMap data) {
		return data.getIntValue(2891) / 10;
	}

	public static boolean isCombatSpell(GeneralRequirementMap data) {
		return getSpellType(data) != 0;
	}

	/*
	 * 1 - air, 2 - water, 3 - earth, 4 - fire, others no type
	 */
	public static int getSpellType(GeneralRequirementMap data) {
		return data.getIntValue(2873);
	}

	public static int getSpellBook(GeneralRequirementMap data) {
		return data.getIntValue(2871);
	}

	public static boolean isAutoCastSpell(GeneralRequirementMap data) {
		return data.getIntValue(2874) == 1;
	}

	public static boolean checkRunes(Player player, GeneralRequirementMap data,
			boolean delete) {
		List<Integer> reqs = new ArrayList<Integer>();
		int airRunes = data.getIntValue(2898);
		if (airRunes > 0) {
			reqs.add(AIR_RUNE);
			reqs.add(airRunes);
		}
		int mindRunes = data.getIntValue(2902);
		if (mindRunes > 0) {
			reqs.add(MIND_RUNE);
			reqs.add(mindRunes);
		}
		int waterRunes = data.getIntValue(2900);
		if (waterRunes > 0) {
			reqs.add(WATER_RUNE);
			reqs.add(waterRunes);
		}
		int earthRunes = data.getIntValue(2899);
		if (earthRunes > 0) {
			reqs.add(EARTH_RUNE);
			reqs.add(earthRunes);
		}
		int fireRunes = data.getIntValue(2901);
		if (fireRunes > 0) {
			reqs.add(FIRE_RUNE);
			reqs.add(fireRunes);
		}
		int bodyRunes = data.getIntValue(2903);
		if (bodyRunes > 0) {
			reqs.add(BODY_RUNE);
			reqs.add(bodyRunes);
		}
		int cosmicRunes = data.getIntValue(2910);
		if (cosmicRunes > 0) {
			reqs.add(COSMIC_RUNE);
			reqs.add(cosmicRunes);
		}
		int chaosRunes = data.getIntValue(2904);
		if (chaosRunes > 0) {
			reqs.add(CHAOS_RUNE);
			reqs.add(chaosRunes);
		}
		int astralRunes = data.getIntValue(2908);
		if (astralRunes > 0) {
			reqs.add(ASTRAL_RUNE);
			reqs.add(astralRunes);
		}
		int natureRunes = data.getIntValue(2909);
		if (natureRunes > 0) {
			reqs.add(NATURE_RUNE);
			reqs.add(natureRunes);
		}
		int lawRunes = data.getIntValue(2911);
		if (lawRunes > 0) {
			reqs.add(LAW_RUNE);
			reqs.add(lawRunes);
		}
		int deathRunes = data.getIntValue(2905);
		if (deathRunes > 0) {
			reqs.add(DEATH_RUNE);
			reqs.add(deathRunes);
		}
		int bloodRunes = data.getIntValue(2906);
		if (bloodRunes > 0) {
			reqs.add(BLOOD_RUNE);
			reqs.add(bloodRunes);
		}
		int soulRunes = data.getIntValue(2907);
		if (soulRunes > 0) {
			reqs.add(SOUL_RUNE);
			reqs.add(soulRunes);
		}
		int armadylRunes = data.getIntValue(2912);
		if (armadylRunes > 0) {
			reqs.add(ARMADYL_RUNE);
			reqs.add(armadylRunes);
		}

		int[] values = new int[reqs.size()];
		for (int i = 0; i < reqs.size(); i++)
			values[i] = reqs.get(i);

		return checkRunes(player, delete, player.getCombatDefinitions()
				.isDungeonneringSpellBook(), values);
	}

	/*
	 * spell on item
	 */
	public static final void handleSpellOnItem(Player player, int spellId,
			byte slot) {
		GeneralRequirementMap data = getSpellData(spellId);
		if (data == null)
			return;
		int spellBook = getSpellBook(data);// spellbook 3 means shared by all
		// books
		if (spellBook != 3
				&& spellBook != player.getCombatDefinitions().getSpellBook())
			return;
		if (data == null || !hasLevel(player, data))
			return;
		final Item target = player.getInventory().getItem(slot);
		player.stopAll();
		switch (spellId) {
		// normal
		case 17: // enchant
		case 29:
		case 41:
		case 40:
		case 64:
		case 77:
			Enchanting.processMagicEnchantSpell(player, slot,
					Enchanting.getJewleryIndex(spellId));
			break;
		case 38:// superheat item
			for (int index = 0; index < 9; index++) {
				SmeltingBar bar = SmeltingBar.values()[index];
				Item[] required = bar.getItemsRequired();
				if (target.getId() == required[0].getId()) {
					if (player.getSkills().getLevel(Skills.SMITHING) >= bar
							.getLevelRequired()) {
						if (player.getInventory().containsItems(required)) {
							if (!Magic.checkRunes(player, true, NATURE_RUNE, 1,
									FIRE_RUNE, 4))
								return;
							player.lock(3);
							player.setNextAnimation(new Animation(725));
							player.setNextGraphics(new Graphics(148, 0, 100));
							player.getSkills().addXp(Skills.SMITHING,
									bar.getExperience());
							player.getSkills().addXp(Skills.MAGIC, 53);
							player.getInventory().removeItems(required);
							player.getInventory().addItem(bar.getProducedBar());
							player.getInterfaceManager().openGameTab(
									InterfaceManager.MAGIC_ABILITIES_TAB);
						} else
							player.getDialogueManager()
									.startDialogue("SimpleMessage",
											"You are missing required ingredients to the spell.");
						return;
					} else {
						player.getPackets().sendGameMessage(
								"You need a Smithing level of at least "
										+ bar.getLevelRequired()
										+ " to smelt "
										+ bar.getProducedBar().getDefinitions()
												.getName());
					}
				}
			}
			player.setNextGraphics(new Graphics(85, 0, 96));
			player.getPackets().sendGameMessage(
					"You cannot cast superheat on this item.");
			break;
		case 26: // low alch
		case 47: // high alch
			boolean highAlch = spellId == 47;

			if (player.getDungManager().isInside()) {
				player.getPackets().sendGameMessage(
						"You are not allowed to alch whilst in a dungeon.");
				return;
			}

			if (target.getId() == 995) {
				player.getPackets().sendGameMessage(
						"You can't cast " + (highAlch ? "high" : "low")
								+ " alchemy on gold.");
				return;
			}
			if (target.getDefinitions().isDestroyItem() /*
														 * || ItemConstants.
														 * getItemDefaultCharges
														 * ( target.getId()) !=
														 * -1
														 */) {
				player.getPackets().sendGameMessage(
						"You can't convert this item..");
				return;
			}
			if (target.getAmount() != 1
					&& !player.getInventory().hasFreeSlots()) {
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				return;
			}
			if (player.getControlerManager().getControler() instanceof StealingCreationController) {
				player.getPackets().sendGameMessage("You can't alch here.");
				return;
			}
			if (!checkRunes(player, true, FIRE_RUNE, highAlch ? 5 : 3,
					NATURE_RUNE, 1))
				return;
			player.lock(4);
			player.getInterfaceManager().openGameTab(
					InterfaceManager.MAGIC_ABILITIES_TAB);
			player.getInventory().deleteItem(target.getId(), 1);
			player.getSkills().addXp(Skills.MAGIC, highAlch ? 65 : 31);
			player.getInventory()
					.addItemMoneyPouch(
							new Item(995, (int) (target.getDefinitions()
									.getValue() * (highAlch ? 0.6D : 0.3D))));
			Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
			if (weapon != null
					&& weapon.getName().toLowerCase().contains("staff")) {
				player.setNextAnimation(new Animation(highAlch ? 9633 : 9625));
				player.setNextGraphics(new Graphics(highAlch ? 1693 : 1692));
			} else {
				player.setNextAnimation(new Animation(713));
				player.setNextGraphics(new Graphics(highAlch ? 113 : 112));
			}
			break;

		// lunar
		case 139: // plank make
			Plank plank = Sawmill.getPlankForLog(target.getId());
			if (plank == null) {
				player.getPackets()
						.sendGameMessage(
								"You can only convert plain, oak, teak and mahogany logs into planks.");
				return;
			}
			int cost = (int) (plank.getCost() * 0.7);
			if (player.getInventory().getCoinsAmount() < cost) {
				player.getPackets().sendGameMessage(
						" You do not have enough coins to cast this spell.");
				return;
			}
			if (!checkRunes(player, true, ASTRAL_RUNE, 2, EARTH_RUNE, 15,
					NATURE_RUNE, 1))
				return;
			player.lock(4);
			player.getInterfaceManager().openGameTab(
					InterfaceManager.MAGIC_ABILITIES_TAB);
			target.setId(plank.getId());
			player.getInventory().refresh(slot);
			player.getInventory().removeItemMoneyPouch(new Item(995, cost));
			player.getSkills().addXp(Skills.MAGIC, 90);
			player.setNextAnimation(new Animation(4413));
			player.setNextGraphics(new Graphics(1063, 0, 100));
			break;
		}
	}

	private static void setCombat(Player player, Entity target) {
		player.setNextFaceWorldTile(target.getMiddleWorldTile());
		if (!player.getControlerManager().canAttack(target))
			return;
		if (target instanceof Player) {
			Player p2 = (Player) target;
			if (!player.isCanPvp() || !p2.isCanPvp()) {
				player.getPackets()
						.sendGameMessage(
								"You can only attack players in a player-vs-player area.");
				return;
			}
		} else if (target instanceof Familiar) {
			Familiar familiar = (Familiar) target;
			if (familiar == player.getFamiliar()) {
				player.getPackets().sendGameMessage(
						"You can't attack your own familiar.");
				return;
			}
			if (!familiar.canAttack(player)) {
				player.getPackets().sendGameMessage(
						"You can't attack this npc.");
				return;
			}
		} else if (target instanceof NPC) {
			if (!((NPC) target).getDefinitions().hasAttackOption()) {
				player.getPackets().sendGameMessage(
						"You can't attack this npc.");
				return;
			}
		}
		player.getActionManager().setAction(new PlayerCombatNew(target));
	}

	/*
	 * spell on entity
	 */
	public static final void handleSpellOnEntity(final Player player,
			int spellId, Entity target) {
		player.setNextFaceWorldTile(new WorldTile(target.getCoordFaceX(target
				.getSize()), target.getCoordFaceY(target.getSize()), target
				.getPlane()));
		// doesnt stop what u doing on rs so no stopall call
		GeneralRequirementMap data = getSpellData(spellId);
		if (data == null)
			return;
		int spellBook = getSpellBook(data);// spellbook 3 means shared by all
		// books
		if (spellBook != 3
				&& spellBook != player.getCombatDefinitions().getSpellBook())
			return;
		if (data == null || !hasLevel(player, data))
			return;
		if (isCombatSpell(data)) {
			if (handleCombatSpell(player, spellId, 0, false))
				setCombat(player, target);
			return;
		}
		// lunar
		switch (spellId) {
		case 150:// venge other
			if (!(target instanceof Player))
				return;
			Long lastVeng = (Long) player.getTemporaryAttributtes().get(
					"LAST_VENG");
			if (lastVeng != null
					&& lastVeng + 30000 > Utils.currentTimeMillis()) {
				long lastVeng1 = ((lastVeng - Utils.currentTimeMillis()) / 1000) + 30;
				player.getPackets().sendGameMessage(
						"You must wait " + lastVeng1
								+ " seconds until you can cast vengeance.");
				return;
			}
			if (!((Player) target).isAcceptingAid()) {
				player.getPackets().sendGameMessage(
						((Player) target).getDisplayName()
								+ " is not accepting aid");
				return;
			}
			if (((Player) target).getControlerManager().getControler() != null
					&& ((Player) target).getControlerManager().getControler() instanceof DuelArena) {
				return;
			}
			if (!checkRunes(player, true, ASTRAL_RUNE, 3, DEATH_RUNE, 2,
					EARTH_RUNE, 10))
				return;
			player.setNextAnimation(new Animation(4411));
			player.getTemporaryAttributtes().put("LAST_VENG",
					Utils.currentTimeMillis());
			player.getPackets().sendGameMessage("You cast a vengeance.");
			((Player) target).setNextGraphics(new Graphics(725, 0, 100));
			((Player) target).setCastVeng(true);
			((Player) target).getPackets().sendGameMessage(
					"You have the power of vengeance!");
			break;
		case 109: // cure other
			if (!(target instanceof Player))
				return;
			if (!((Player) target).isAcceptingAid()) {
				player.getPackets().sendGameMessage(
						((Player) target).getDisplayName()
								+ " is not accepting aid");
				return;
			}
			if (((Player) target).getControlerManager().getControler() != null
					&& ((Player) target).getControlerManager().getControler() instanceof DuelArena) {
				return;
			}
			if (!checkRunes(player, true, ASTRAL_RUNE, 1, EARTH_RUNE, 10))
				return;
			player.setNextAnimation(new Animation(4411));
			Player p2 = (Player) target;
			p2.setNextGraphics(new Graphics(736, 0, 150));
			p2.getEffectsManager().removeEffect(EffectType.POISON);
			p2.getPackets().sendGameMessage(
					"You have been healed by " + player.getDisplayName() + "!");
			break;
		case 107:// stat spy npc
			if (!(target instanceof NPC))
				return;
			NPC npc = (NPC) target;
			if (!npc.getDefinitions().hasAttackOption()) {
				player.getPackets().sendGameMessage(
						"That NPC cannot be examined.");
				return;
			}
			if (!checkRunes(player, true, ASTRAL_RUNE, 1, 564, 1, MIND_RUNE, 1))
				return;
			player.getInterfaceManager().sendMagicAbilities(522);
			player.getPackets().sendIComponentText(522, 0,
					"Monster Name: " + npc.getName());
			player.getPackets().sendIComponentText(522, 1,
					"Combat Level: " + npc.getCombatLevel());
			player.getPackets().sendIComponentText(522, 2,
					"Life Points: " + npc.getHitpoints());
			player.getPackets().sendIComponentText(
					522,
					3,
					"Creature's Max Hit: "
							+ npc.getMaxHit(npc.getAttackStyle()));
			player.getPackets()
					.sendIComponentText(
							522,
							4,
							(player.getSlayerManager().isValidTask(
									npc.getName()) ? "Valid Slayer Task" : ""));
			player.setNextAnimation(new Animation(4413));
			player.setNextGraphics(new Graphics(1061));
			break;
		case 122:// stat spy
			if (!(target instanceof Player))
				return;
			if (!checkRunes(player, data, true))
				return;
			p2 = (Player) target;
			player.getInterfaceManager().sendMagicAbilities(523);

			player.getPackets().sendIComponentText(523, 105,
					p2.getDisplayName());
			player.getPackets().sendIComponentText(523, 107,
					"" + p2.getHitpoints());
			for (int i = 0; i < stat_spy_skills.length; i++) {
				player.getPackets().sendIComponentText(
						523,
						stat_spy_skills[i] == Skills.DIVINATION ? 110
								: (1 + i * 4),
						"" + p2.getSkills().getLevel(stat_spy_skills[i]));
				player.getPackets().sendIComponentText(
						523,
						stat_spy_skills[i] == Skills.DIVINATION ? 111
								: (2 + i * 4),
						"" + p2.getSkills().getLevelForXp(stat_spy_skills[i]));
			}
			player.setNextAnimation(new Animation(4412));
			player.setNextGraphics(new Graphics(1060));
			break;
		}

	}

	private static final int[] stat_spy_skills = { Skills.ATTACK,
			Skills.HITPOINTS, Skills.MINING, Skills.STRENGTH, Skills.AGILITY,
			Skills.SMITHING, Skills.DEFENCE, Skills.HERBLORE, Skills.FISHING,
			Skills.RANGE, Skills.THIEVING, Skills.COOKING, Skills.PRAYER,
			Skills.CRAFTING, Skills.FIREMAKING, Skills.MAGIC, Skills.FLETCHING,
			Skills.WOODCUTTING, Skills.RUNECRAFTING, Skills.SLAYER,
			Skills.FARMING, Skills.CONSTRUCTION, Skills.HUNTER,
			Skills.SUMMONING, Skills.DUNGEONEERING, Skills.DIVINATION };

	/*
	 * normal click spells
	 */
	public static final void handleSpell(Player player, int spellId,
			int packetId) {
		GeneralRequirementMap data = getSpellData(spellId);
		if (data == null)
			return;
		int spellBook = getSpellBook(data);// spellbook 3 means shared by all
		// books
		if (spellBook != 3
				&& spellBook != player.getCombatDefinitions().getSpellBook())
			return;
		if (data == null || !hasLevel(player, data))
			return;
		if (isCombatSpell(data)) {
			handleCombatSpell(player, spellId,
					packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 2
							: 1, false);
			return;
		}
		switch (spellId) {
		// lunar spells
		case 151: // vengeance
			if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
				player.getPackets().sendGameMessage(
						"You need a Defence level of 40 for this spell.");
				return;
			}
			Long lastVeng = (Long) player.getTemporaryAttributtes().get(
					"LAST_VENG");
			if (lastVeng != null
					&& lastVeng + 30000 > Utils.currentTimeMillis()) {
				long lastVeng1 = ((lastVeng - Utils.currentTimeMillis()) / 1000) + 30;
				player.getPackets().sendGameMessage(
						"You must wait " + lastVeng1
								+ " seconds until you can cast vengeance.");
				return;
			}
			if (!checkRunes(player, true, ASTRAL_RUNE, 4, DEATH_RUNE, 2,
					EARTH_RUNE, 10))
				return;
			player.getSkills().addXp(Skills.MAGIC, 112);
			player.setNextGraphics(new Graphics(726, 0, 100));
			player.setNextAnimation(new Animation(4410));
			player.setCastVeng(true);
			player.getTemporaryAttributtes().put("LAST_VENG",
					Utils.currentTimeMillis());
			player.getPackets().sendGameMessage("You cast a vengeance.");
			break;
		case 155: // home teleport
			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
				useHomeTele(player);
			else {
				player.stopAll();
				HomeTeleport
						.useLodestone(player, player.getPreviousLodestone());
			}
			break;
		case 129: // dream spell
			if (player.getEffectsManager().hasActiveEffect(EffectType.POISON)) {
				player.getPackets().sendGameMessage(
						"You can't dream while you're poisoned.");
				return;
			} else if (player.isUnderCombat()) {
				player.getPackets()
						.sendGameMessage(
								"You can't cast dream until 10 seconds after the end of combat.");
				return;
			} else if (player.getHitpoints() == player.getMaxHitpoints()) {
				player.getPackets()
						.sendGameMessage(
								"You have no need to cast this spell since your life points are already full.");
				return;
			} else if (!checkRunes(player, true, ASTRAL_RUNE, 2, COSMIC_RUNE,
					1, BODY_RUNE, 5))
				return;
			player.getActionManager().setAction(new DreamSpellAction());
			break;
		case 152: // vegeance group
			lastVeng = (Long) player.getTemporaryAttributtes().get("LAST_VENG");
			if (lastVeng != null
					&& lastVeng + 30000 > Utils.currentTimeMillis()) {
				long lastVeng1 = ((lastVeng - Utils.currentTimeMillis()) / 1000) + 30;
				player.getPackets().sendGameMessage(
						"You must wait " + lastVeng1
								+ " seconds until you can cast vengeance.");
				return;
			}
			if (!checkRunes(player, true, ASTRAL_RUNE, 4, DEATH_RUNE, 3,
					EARTH_RUNE, 11))
				return;
			int affectedPeopleCount = 0;
			for (int regionId : player.getMapRegionsIds()) {
				List<Integer> playerIndexes = World.getRegion(regionId)
						.getPlayerIndexes();
				if (playerIndexes == null)
					continue;
				for (int playerIndex : playerIndexes) {
					Player p2 = World.getPlayers().get(playerIndex);
					if (p2 == null || p2 == player || p2.isDead()
							|| !p2.hasStarted() || p2.hasFinished()
							|| !p2.withinDistance(player, 4)
							|| !player.getControlerManager().canHit(p2))
						continue;
					if (!p2.isAcceptingAid()) {
						player.getPackets().sendGameMessage(
								p2.getDisplayName() + " is not accepting aid");
						continue;
					} else if (p2.getControlerManager().getControler() != null
							&& p2.getControlerManager().getControler() instanceof DuelArena) {
						continue;
					}
					p2.setNextGraphics(new Graphics(725, 0, 100));
					p2.setCastVeng(true);
					p2.getPackets().sendGameMessage(
							"You have the power of vengeance!");
					affectedPeopleCount++;
				}
			}
			player.getSkills().addXp(Skills.MAGIC, 120);
			player.setNextAnimation(new Animation(4411));
			player.getTemporaryAttributtes().put("LAST_VENG",
					Utils.currentTimeMillis());
			player.getPackets().sendGameMessage(
					"The spell affected " + affectedPeopleCount
							+ " nearby people.");
			break;
		case 111: // moonclan teleport
			sendLunarTeleportSpell(player, 69, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					EARTH_RUNE, 2);
			break;
		case 113: // ourania teleport
			sendLunarTeleportSpell(player, 71, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					EARTH_RUNE, 6);
			break;
		case 116: // south falador teleport
			sendLunarTeleportSpell(player, 72, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					AIR_RUNE, 2);
			break;
		case 117: // waterbirth teleport
			sendLunarTeleportSpell(player, 72, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					WATER_RUNE, 1);
			break;
		case 121: // barbarian teleport
			sendLunarTeleportSpell(player, 75, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					FIRE_RUNE, 3);
			break;
		case 123: // North Ardroudge teleport
			sendLunarTeleportSpell(player, 76, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					WATER_RUNE, 5);
			break;
		case 127: // Khazard teleport
			sendLunarTeleportSpell(player, 78, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 2, LAW_RUNE, 2,
					WATER_RUNE, 4);
			break;
		case 137: // Fishing guild teleport
			sendLunarTeleportSpell(player, 85, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 3, LAW_RUNE, 3,
					WATER_RUNE, 8);
			break;
		case 140: // Catherbay teleport
			sendLunarTeleportSpell(player, 87, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 3, LAW_RUNE, 3,
					WATER_RUNE, 10);
			break;
		case 143: // Ice Plateau teleport
			sendLunarTeleportSpell(player, 89, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 3, LAW_RUNE, 3,
					WATER_RUNE, 8);
			break;
		case 148: // Throheim teleport
			sendLunarTeleportSpell(player, 92, getSpellXP(data),
					getSpellTeleport(data), ASTRAL_RUNE, 3, LAW_RUNE, 3,
					WATER_RUNE, 10);
			break;
		case 105: // bake pie
			for (Cookables food : Cookables.values()) {
				if (food.toString().toLowerCase().contains("_pie")) {
					if (player.getSkills().getLevel(Skills.COOKING) < food
							.getLvl())
						continue;
					Item item = food.getRawItem();
					if (player.getInventory().containsItem(item.getId(), 1)) {
						for (int i = 0; i < player.getInventory().getAmountOf(
								item.getId()); i++) {
							if (!checkRunes(player, true, ASTRAL_RUNE, 1,
									FIRE_RUNE, 5, WATER_RUNE, 4))
								return;
							player.lock(2);
							player.getInventory().replaceItem(
									food.getProduct().getId(),
									item.getAmount(),
									player.getInventory().getItems()
											.getThisItemSlot(item.getId()));
							player.getInterfaceManager().openGameTab(
									InterfaceManager.MAGIC_ABILITIES_TAB);
							player.getSkills().addXp(Skills.MAGIC, 60);
							player.getSkills().addXp(Skills.COOKING,
									food.getXp());
							player.setNextAnimation(new Animation(4413));
							player.setNextGraphics(new Graphics(746));
							return;
						}
					}
				}
			}
			player.getPackets().sendGameMessage("You do not have any pie.");
			break;
		case 110: // humidify
			if (!checkRunes(player, true, ASTRAL_RUNE, 1, FIRE_RUNE, 1,
					WATER_RUNE, 3))
				return;
			player.lock(2);
			for (Item item : player.getInventory().getItems().getItems()) {
				if (item == null)
					continue;
				for (Fill fill : Fill.values()) {
					if (fill.getEmpty() == item.getId())
						item.setId(fill.getFull());
				}
			}
			player.getInventory().refresh();
			player.getSkills().addXp(Skills.MAGIC, 65);
			player.getInterfaceManager().openGameTab(
					InterfaceManager.MAGIC_ABILITIES_TAB);
			player.setNextAnimation(new Animation(4413));
			player.setNextGraphics(new Graphics(1061, 0, 150));
			break;
		case 112: // group teleport
		case 118:
		case 124:
		case 128:
		case 138:
		case 142:
		case 144:
		case 149:
			int index = spellId == 112 ? 0 : spellId == 118 ? 1
					: spellId == 124 ? 2 : spellId == 128 ? 3
							: spellId == 138 ? 4 : spellId == 142 ? 5
									: spellId == 144 ? 6 : 7;
			if (!checkRunes(player, data, true))
				return;
			player.getSkills().addXp(Skills.MAGIC, getSpellXP(data));
			String name = player.getDisplayName();
			for (int regionId : player.getMapRegionsIds()) {
				List<Integer> playersIndexes = World.getRegion(regionId)
						.getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player p2 = World.getPlayers().get(playerIndex);
					if (p2 == null
							|| p2.isDead()
							|| p2.hasFinished()
							|| !p2.isRunning()
							|| !p2.isAcceptingAid()
							&& player.getIndex() != p2.getIndex()
							|| p2.getInterfaceManager()
									.containsScreenInterface()
							|| !p2.withinDistance(player, 5))
						continue;
					ManiFoldTeleport.openInterface(p2, name, index, true);
				}
			}
			break;
		case 114: // cure me
			if (!checkRunes(player, true, ASTRAL_RUNE, 2, COSMIC_RUNE, 2))
				return;
			player.setNextAnimation(new Animation(4411));
			player.setNextGraphics(new Graphics(736, 0, 150));
			player.getEffectsManager().removeEffect(EffectType.POISON);
			break;
		case 119:// TODO cure group
			if (!Magic.checkSpellLevel(player, 74))
				return;
			else if (!checkRunes(player, true, ASTRAL_RUNE, 2, COSMIC_RUNE, 2))
				return;
			affectedPeopleCount = 0;
			for (int regionId : player.getMapRegionsIds()) {
				List<Integer> playerIndexes = World.getRegion(regionId)
						.getPlayerIndexes();
				if (playerIndexes == null)
					continue;
				for (int playerIndex : playerIndexes) {
					Player p2 = World.getPlayers().get(playerIndex);
					if (p2 == null || p2 == player || p2.isDead()
							|| !p2.hasStarted() || p2.hasFinished()
							|| !p2.withinDistance(player, 4))
						continue;
					if (!p2.isAcceptingAid())
						continue;
					player.setNextGraphics(new Graphics(736, 0, 150));
					p2.getPackets().sendGameMessage(
							"You have been cured of all illnesses!");
					affectedPeopleCount++;
				}
			}
			player.setNextAnimation(new Animation(4411));
			player.getPackets().sendGameMessage(
					"The spell affected " + affectedPeopleCount
							+ " nearby people.");
			break;
		case 83: // paddewa
			sendAncientTeleportSpell(player, 54, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 2, FIRE_RUNE, 1,
					AIR_RUNE, 1);
			break;
		case 86: // senntisten
			sendAncientTeleportSpell(player, 60, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 2, SOUL_RUNE, 1);
			break;
		case 89: // k
			sendAncientTeleportSpell(player, 66, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 2, BLOOD_RUNE, 1);
			break;
		case 92: // lassar
			sendAncientTeleportSpell(player, 72, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 2, WATER_RUNE, 4);
			break;
		case 95: // dare
			sendAncientTeleportSpell(player, 78, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 2, FIRE_RUNE, 3,
					AIR_RUNE, 2);
			break;
		case 98: // care
			sendAncientTeleportSpell(player, 84, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 2, SOUL_RUNE, 2);
			break;
		case 101: // ana
			sendAncientTeleportSpell(player, 90, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 2, BLOOD_RUNE, 2);
			break;
		case 104: // ghorok
			sendAncientTeleportSpell(player, 96, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 2, WATER_RUNE, 8);
			break;
		case 22:// bones to banana
		case 53:// bones to peaches
			boolean bones_to_peaches = spellId == 53;
			if (!checkRunes(player, true, NATURE_RUNE,
					bones_to_peaches ? 2 : 1, EARTH_RUNE, bones_to_peaches ? 4
							: 2, WATER_RUNE, bones_to_peaches ? 4 : 2))
				return;
			int bones = 0;
			for (int i = 0; i < 28; i++) {
				Item item = player.getInventory().getItem(i);
				if (item == null || Bone.forId(item.getId()) == null)
					continue;
				item.setId(bones_to_peaches ? 6883 : 1963);
				bones++;
			}
			if (bones != 0) {
				player.getSkills().addXp(Skills.MAGIC,
						bones_to_peaches ? 35.5 : 25);
				player.getInventory().refresh();
			}
			break;
		case 156: // crossbow bolt enchant
			if (player.getSkills().getLevel(Skills.MAGIC) < 4) {
				player.getPackets().sendGameMessage(
						"Your Magic level is not high enough for this spell.");
				return;
			}
			player.stopAll();
			player.getInterfaceManager().sendCentralInterface(432);
			break;
		case 19: // mobi
			sendNormalTeleportSpell(player, 10, getSpellXP(data),
					getSpellTeleport(data), LAW_RUNE, 1, WATER_RUNE, 1,
					AIR_RUNE, 1);
			break;
		case 28: // varrock
			sendNormalTeleportSpell(player, 25, getSpellXP(data),
					getSpellTeleport(data), FIRE_RUNE, 1, AIR_RUNE, 3,
					LAW_RUNE, 1);
			break;
		case 31: // lumby
			sendNormalTeleportSpell(player, 31, getSpellXP(data),
					getSpellTeleport(data), EARTH_RUNE, 1, AIR_RUNE, 3,
					LAW_RUNE, 1);
			break;
		case 34: // fally
			sendNormalTeleportSpell(player, 37, getSpellXP(data),
					getSpellTeleport(data), WATER_RUNE, 1, AIR_RUNE, 3,
					LAW_RUNE, 1);
			break;
		case 36: // house
			sendNormalTeleportSpell(player, 40, getSpellXP(data), null,
					LAW_RUNE, 1, AIR_RUNE, 1, EARTH_RUNE, 1);
			break;
		case 39: // camelot
			sendNormalTeleportSpell(player, 45, getSpellXP(data),
					getSpellTeleport(data), AIR_RUNE, 5, LAW_RUNE, 1);
			break;
		case 45: // ardy
			sendNormalTeleportSpell(player, 51, getSpellXP(data),
					getSpellTeleport(data), WATER_RUNE, 2, LAW_RUNE, 2);
			break;
		case 50: // watch
			sendNormalTeleportSpell(player, 58, getSpellXP(data),
					new WorldTile(2549, 3114, 2), EARTH_RUNE, 2, LAW_RUNE, 2);
			break;
		case 57: // troll
			sendNormalTeleportSpell(player, 61, getSpellXP(data),
					getSpellTeleport(data), FIRE_RUNE, 2, LAW_RUNE, 2);
			break;
		case 170: // godswars
			sendNormalTeleportSpell(player, 61, getSpellXP(data),
					getSpellTeleport(data), FIRE_RUNE, 2, LAW_RUNE, 2);
			break;
		case 64: // ape
			sendNormalTeleportSpell(player, 64, getSpellXP(data),
					getSpellTeleport(data), FIRE_RUNE, 2, WATER_RUNE, 2,
					LAW_RUNE, 2, 1963, 1);
			break;
		}

	}

	public static final void processDungSpell(Player player, int spellId,
			int slot, int packetId) {
		/*
		 * final Item target = player.getInventory().getItem(slot); if (target
		 * == null && slot != -1) return; switch (spellId) { case 25: case 27:
		 * case 28: case 30: case 32: // air bolt case 36: // water bolt case
		 * 37: // earth bolt case 41: // fire bolt case 42: // air blast case
		 * 43: // water blast case 45: // earth blast case 47: // fire blast
		 * case 48: // air wave case 49: // water wave case 54: // earth wave
		 * case 58: // fire wave case 61:// air surge case 62:// water surge
		 * case 63:// earth surge case 67:// fire surge case 34:// bind case
		 * 44:// snare case 59:// entangle setCombatSpell(player, spellId);
		 * break; case 65: if (player.getSkills().getLevel(Skills.MAGIC) < 94) {
		 * player.getPackets().sendGameMessage(
		 * "Your Magic level is not high enough for this spell."); return; }
		 * else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
		 * player.getPackets().sendGameMessage(
		 * "You need a Defence level of 40 for this spell"); return; } Long
		 * lastVeng = (Long) player.getTemporaryAttributtes().get("LAST_VENG");
		 * if (lastVeng != null && lastVeng + 30000 > Utils.currentTimeMillis())
		 * { player.getPackets().sendGameMessage(
		 * "Players may only cast vengeance once every 30 seconds."); return; }
		 * if (!checkRunes(player, true, true, 17790, 4, 17786, 2, 17782, 10))
		 * return; player.getSkills().addXp(Skills.MAGIC, 112);
		 * player.setNextGraphics(new Graphics(726, 0, 100));
		 * player.setNextAnimation(new Animation(4410));
		 * player.setCastVeng(true);
		 * player.getTemporaryAttributtes().put("LAST_VENG",
		 * Utils.currentTimeMillis()); player.getPackets().sendGameMessage(
		 * "You cast a vengeance."); break; case 66: // vegeance group if
		 * (player.getSkills().getLevel(Skills.MAGIC) < 95) {
		 * player.getPackets().sendGameMessage(
		 * "Your Magic level is not high enough for this spell."); return; }
		 * lastVeng = (Long) player.getTemporaryAttributtes().get("LAST_VENG");
		 * if (lastVeng != null && lastVeng + 30000 > Utils.currentTimeMillis())
		 * { player.getPackets().sendGameMessage(
		 * "Players may only cast vengeance once every 30 seconds."); return; }
		 * if (!checkRunes(player, true, true, 17790, 4, 17786, 3, 17782, 11))
		 * return; int affectedPeopleCount = 0; for (int regionId :
		 * player.getMapRegionsIds()) { List<Integer> playerIndexes =
		 * World.getRegion(regionId).getPlayerIndexes(); if (playerIndexes ==
		 * null) continue; for (int playerIndex : playerIndexes) { Player p2 =
		 * World.getPlayers().get(playerIndex); if (p2 == null || p2 == player
		 * || p2.isDead() || !p2.hasStarted() || p2.hasFinished() ||
		 * !p2.withinDistance(player, 4) ||
		 * !player.getControlerManager().canHit(p2)) continue; if
		 * (!p2.isAcceptingAid()) {
		 * player.getPackets().sendGameMessage(p2.getDisplayName() +
		 * " is not accepting aid"); continue; } else if
		 * (p2.getControlerManager().getControler() != null &&
		 * p2.getControlerManager().getControler() instanceof DuelArena) {
		 * continue; } p2.setNextGraphics(new Graphics(725, 0, 100));
		 * p2.setCastVeng(true); p2.getPackets().sendGameMessage(
		 * "You have the power of vengeance!"); affectedPeopleCount++; } }
		 * player.getSkills().addXp(Skills.MAGIC, 120);
		 * player.setNextAnimation(new Animation(4411));
		 * player.getTemporaryAttributtes().put("LAST_VENG",
		 * Utils.currentTimeMillis()); player.getPackets().sendGameMessage(
		 * "The spell affected " + affectedPeopleCount + " nearby people.");
		 * break; case 53: if (player.getSkills().getLevel(Skills.MAGIC) < 68) {
		 * player.getPackets().sendGameMessage(
		 * "Your Magic level is not high enough for this spell."); return; } if
		 * (!checkRunes(player, true, true, 17790, 1, 17783, 1, 17781, 3))
		 * return; player.lock(2); Item[] itemsBefore =
		 * player.getInventory().getItems().getItemsCopy(); for (Item item :
		 * player.getInventory().getItems().getItems()) { if (item == null)
		 * continue; for (Fill fill : Fill.values()) { if (fill.getEmpty() ==
		 * item.getId()) item.setId(fill.getFull()); } }
		 * player.getInventory().refreshItems(itemsBefore);
		 * player.getSkills().addXp(Skills.MAGIC, 65);
		 * player.getInterfaceManager().openGameTab(4);
		 * player.setNextAnimation(new Animation(4413));
		 * player.setNextGraphics(new Graphics(1061, 0, 150)); break; case 35:
		 * // low alch case 46: // high alch boolean highAlch = spellId == 46;
		 * if (!Magic.checkSpellLevel(player, (highAlch ? 55 : 21))) return; if
		 * (target.getId() == DungeonConstants.RUSTY_COINS) {
		 * player.getPackets().sendGameMessage("You can't cast " + (highAlch ?
		 * "high" : "low") + " alchemy on gold."); return; } if
		 * (target.getDefinitions().isDestroyItem() ||
		 * ItemConstants.getItemDefaultCharges(target.getId()) != -1 ||
		 * !ItemConstants.isTradeable(target)) {
		 * player.getPackets().sendGameMessage("You can't convert this item..");
		 * return; } if (target.getAmount() != 1 &&
		 * !player.getInventory().hasFreeSlots()) {
		 * player.getPackets().sendGameMessage(
		 * "Not enough space in your inventory."); return; } if
		 * (!checkRunes(player, true, true, 17783, highAlch ? 5 : 3, 17791, 1))
		 * return; player.lock(4); player.getInterfaceManager().openGameTab(7);
		 * player.getInventory().deleteItem(target.getId(), 1);
		 * player.getSkills().addXp(Skills.MAGIC, highAlch ? 25 : 15);
		 * player.getInventory().addItemMoneyPouch(new
		 * Item(DungeonConstants.RUSTY_COINS, (int)
		 * (target.getDefinitions().getValue() * (highAlch ? 0.6D : 0.3D))));
		 * Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
		 * if (weapon != null &&
		 * weapon.getName().toLowerCase().contains("staff")) {
		 * player.setNextAnimation(new Animation(highAlch ? 9633 : 9625));
		 * player.setNextGraphics(new Graphics(highAlch ? 1693 : 1692)); } else
		 * { player.setNextAnimation(new Animation(713));
		 * player.setNextGraphics(new Graphics(highAlch ? 113 : 112)); } break;
		 * case 31:// bones to bananas if (!Magic.checkSpellLevel(player, 15))
		 * return; else if (!checkRunes(player, true, true, 17791, 1, 17781, 2,
		 * 17782, 2)) return; int bones = 0; for (int i = 0; i < 28; i++) { Item
		 * item = player.getInventory().getItem(i); if (item == null ||
		 * Bone.forId(item.getId()) == null) continue; item.setId(18199);
		 * bones++; } if (bones != 0) { player.getSkills().addXp(Skills.MAGIC,
		 * 25); player.getInventory().refresh(); } break; case 55: if
		 * (player.getSkills().getLevel(Skills.MAGIC) < 71) {
		 * player.getPackets().sendGameMessage(
		 * "Your Magic level is not high enough for this spell."); return; } if
		 * (!checkRunes(player, true, true, 17790, 2, 17789, 2)) return;
		 * player.setNextAnimation(new Animation(4411));
		 * player.setNextGraphics(new Graphics(736, 0, 150));
		 * EffectsManager.reset(); break; case 57: if
		 * (!Magic.checkSpellLevel(player, 74)) return; else if
		 * (!checkRunes(player, true, true, 17790, 2, 17789, 2)) return;
		 * affectedPeopleCount = 0; for (int regionId :
		 * player.getMapRegionsIds()) { List<Integer> playerIndexes =
		 * World.getRegion(regionId).getPlayerIndexes(); if (playerIndexes ==
		 * null) continue; for (int playerIndex : playerIndexes) { Player p2 =
		 * World.getPlayers().get(playerIndex); if (p2 == null || p2 == player
		 * || p2.isDead() || !p2.hasStarted() || p2.hasFinished() ||
		 * !p2.withinDistance(player, 4)) continue; if (!p2.isAcceptingAid())
		 * continue; player.setNextGraphics(new Graphics(736, 0, 150));
		 * p2.getPackets().sendGameMessage(
		 * "You have been cured of all illnesses!"); affectedPeopleCount++; } }
		 * player.setNextAnimation(new Animation(4411));
		 * player.getPackets().sendGameMessage("The spell affected " +
		 * affectedPeopleCount + " nearby people."); break; default:
		 * if(Settings.DEBUG) Logger.log(Magic.class, "Component " + spellId);
		 * break; }
		 */
	}

	public static void useHomeTele(Player player) {
		if (player.isUnderCombat()) {
			player.getPackets().sendGameMessage(
					"You can't do this while under combat.");
			return;
		}
		player.stopAll();
		// dont refresh cuz it makes buttons reset to main inter
		player.getInterfaceManager().sendCentralInterface(1092);
	}

	public static final boolean checkSpellRequirements(Player player,
			int level, boolean delete, int... runes) {
		return checkSpellRequirements(player, level, delete, false, runes);
	}

	public static final boolean checkSpellRequirements(Player player,
			int level, boolean delete, boolean dungeoneering, int... runes) {
		if (!checkSpellLevel(player, level))
			return false;
		return checkRunes(player, delete, dungeoneering, runes);
	}

	public static boolean checkSpellLevel(Player player, int level) {
		if (player.getSkills().getLevel(Skills.MAGIC) < level
				&& player.getSkills().getLevelForXp(Skills.MAGIC) < level) {
			player.getPackets().sendGameMessage(
					"Your Magic level is not high enough for this spell.");
			return false;
		}
		return true;
	}

	public static boolean hasStaffOfLight(int weaponId) {
		if (weaponId == 15486 || weaponId == 22207 || weaponId == 22209
				|| weaponId == 22211 || weaponId == 22213)
			return true;
		return false;
	}

	public static final boolean checkRunes(Player player, boolean delete,
			int... runes) {
		return checkRunes(player, delete, false, runes);
	}

	public static final boolean checkRunes(Player player, boolean delete,
			boolean dungeoneering, int... values) {
		int weaponId = player.getEquipment().getWeaponId();
		int shieldId = player.getEquipment().getShieldId();
		int runesCount = 0;
		while (runesCount < values.length) {
			int runeId = values[runesCount++];
			int amount = values[runesCount++];
			if (hasInfiniteRunes(runeId, weaponId, shieldId))
				continue;
			else if (hasSpecialRunes(player, runeId, amount))
				continue;
			else if (dungeoneering) {
				if (player.getInventory().containsItem(runeId - 1689, amount))
					continue;
			}
			if (!player.getInventory().containsItem(runeId, amount)) {
				player.getPackets().sendGameMessage(
						"You do not have enough "
								+ ItemDefinitions.getItemDefinitions(runeId)
										.getName().replace("rune", "Rune")
								+ "s to cast this spell.");
				return false;
			}

		}
		if (delete) {
			if (hasStaffOfLight(weaponId) && !containsRune(LAW_RUNE, values)
					&& !containsRune(NATURE_RUNE, values)
					&& Utils.random(8) == 0) {
				player.getPackets()
						.sendGameMessage(
								"The power of your staff of light saves some runes from being drained.",
								true);
				return true;
			}
			runesCount = 0;
			while (runesCount < values.length) {
				int runeId = values[runesCount++];
				int amount = values[runesCount++];
				if (hasInfiniteRunes(runeId, weaponId, shieldId))
					continue;
				else if (hasSpecialRunes(player, runeId, amount))
					runeId = getRuneForId(runeId);
				else if (dungeoneering) {
					int bindedRune = runeId - 1689;
					if (player.getInventory().containsItem(bindedRune, amount)) {
						player.getInventory().deleteItem(bindedRune, amount);
						continue; // won't delete the extra rune anyway.
					}
				}
				player.getInventory().deleteItem(runeId, amount);
			}
		}
		return true;
	}

	private static boolean containsRune(int rune, int[] integer) {
		for (int id : integer) {
			if (rune == id)
				return true;
		}
		return false;
	}

	public static final void sendAncientTeleportSpell(Player player, int level,
			double xp, WorldTile tile, int... runes) {
		sendTeleportSpell(player, 1979, -1, 1681, -1, level, xp, tile, 5, true,
				MAGIC_TELEPORT, runes);
	}

	public static final void sendLunarTeleportSpell(Player player, int level,
			double xp, WorldTile tile, int... runes) {
		sendTeleportSpell(player, 9606, -2, 1685, -1, level, xp, tile, 5, true,
				MAGIC_TELEPORT, runes);
	}

	public static final boolean sendNormalTeleportSpell(Player player,
			int level, double xp, WorldTile tile, int... runes) {
		return sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp,
				tile, 3, true, MAGIC_TELEPORT, runes);
	}

	public static final boolean sendItemTeleportSpell(Player player,
			boolean randomize, int upEmoteId, int upGraphicId, int delay,
			WorldTile tile) {
		return sendTeleportSpell(player, upEmoteId, -2, upGraphicId, -1, 0, 0,
				tile, delay, randomize, ITEM_TELEPORT);
	}

	public static void pushLeverTeleport(final Player player,
			final WorldTile tile) {
		pushLeverTeleport(player, tile, 2140, null, null);
	}

	public static void pushLeverTeleport(final Player player,
			final WorldTile tile, int emote, String startMessage,
			final String endMessage) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		player.setNextAnimation(new Animation(emote));
		if (startMessage != null)
			player.getPackets().sendGameMessage(startMessage, true);
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.unlock();
				Magic.sendObjectTeleportSpell(player, false, tile);
				if (endMessage != null)
					player.getPackets().sendGameMessage(endMessage, true);
			}
		}, 1);
	}

	public static final void sendObjectTeleportSpell(Player player,
			boolean randomize, WorldTile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3,
				randomize, OBJECT_TELEPORT);
	}

	public static final void sendDelayedObjectTeleportSpell(Player player,
			int delay, boolean randomize, WorldTile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, delay,
				randomize, OBJECT_TELEPORT);
	}

	public static final boolean sendTeleportSpell(final Player player,
			int upEmoteId, final int downEmoteId, int upGraphicId,
			final int downGraphicId, int level, final double xp,
			final WorldTile tile, int delay, final boolean randomize,
			final int teleType, int... runes) {
		if (player.isLocked())
			return false;
		if (player.getSkills().getLevel(Skills.MAGIC) < level) {
			player.getPackets().sendGameMessage(
					"Your Magic level is not high enough for this spell.");
			return false;
		}
		if (player.getDungManager().isInside()) {
			player.getPackets().sendGameMessage(
					"Use the normal exit to leave dungeoneering match.");
			return false;
		}
		if (!checkRunes(player, false, runes))
			return false;
		final WorldTile checkTile = tile == null ? player.getHouse()
				.getLocation().getTile() : tile;
		if (teleType == MAGIC_TELEPORT) {
			if (!player.getControlerManager().processMagicTeleport(checkTile))
				return false;
		} else if (teleType == ITEM_TELEPORT) {
			if (!player.getControlerManager().processItemTeleport(checkTile))
				return false;
		} else if (teleType == OBJECT_TELEPORT) {
			if (!player.getControlerManager().processObjectTeleport(checkTile))
				return false;
		}
		checkRunes(player, true, runes);
		player.stopAll();
		if (upEmoteId != -1)
			player.setNextAnimation(new Animation(upEmoteId));
		if (upGraphicId != -1)
			player.setNextGraphics(new Graphics(upGraphicId));
		player.lock(3 + delay);
		WorldTasksManager.schedule(new WorldTask() {

			boolean removeDamage;

			@Override
			public void run() {
				if (!removeDamage) {
					WorldTile teleTile = checkTile;
					if (randomize) {
						// attemps to randomize tile by 4x4 area
						for (int trycount = 0; trycount < 10; trycount++) {
							teleTile = new WorldTile(checkTile, 2);
							if (World.isTileFree(checkTile.getPlane(),
									teleTile.getX(), teleTile.getY(),
									player.getSize()))
								break;
							teleTile = checkTile;
						}
					}
					player.setNextWorldTile(teleTile);
					player.getControlerManager().magicTeleported(teleType);
					if (xp != 0)
						player.getSkills().addXp(Skills.MAGIC, xp);
					if (downEmoteId != -1)
						player.setNextAnimation(new Animation(
								downEmoteId == -2 ? -1 : downEmoteId));
					if (downGraphicId != -1)
						player.setNextGraphics(new Graphics(downGraphicId));
					if (teleType == MAGIC_TELEPORT) {
						player.setNextFaceWorldTile(new WorldTile(teleTile
								.getX(), teleTile.getY() - 1, teleTile
								.getPlane()));
					}
					if (tile == null && !player.getHouse().isArriveInPortal())
						player.getHouse().enterMyHouse();
					else if (player.getControlerManager().getControler() == null)
						teleControlersCheck(player, teleTile);
					removeDamage = true;
				} else {
					player.resetReceivedHits();
					stop();
				}
			}
		}, delay, 0);
		return true;
	}

	private final static WorldTile[] TABS = { new WorldTile(3217, 3426, 0),
			new WorldTile(3222, 3218, 0), new WorldTile(2965, 3379, 0),
			new WorldTile(2758, 3478, 0), new WorldTile(2660, 3306, 0),
			new WorldTile(2549, 3115, 2), null };

	public static void useVecnaSkull(Player player) {
		Long time = (Long) player.getTemporaryAttributtes().get(
				"VecnaSkullDelay");
		long currentTime = Utils.currentTimeMillis();
		if (time != null && time >= currentTime) {
			int minutes = (int) ((time - currentTime) / 1000 / 60);
			player.getPackets()
					.sendGameMessage(
							"The skull has not yet regained its mysterious aura. You will have to wait another "
									+ minutes + " minutes.");
			return;
		}
		int newLevel = player.getSkills().getLevel(Skills.MAGIC) + 6;
		int maxLevel = player.getSkills().getLevelForXp(Skills.MAGIC) + 6;
		if (newLevel > maxLevel)
			newLevel = maxLevel;
		player.getSkills().set(Skills.MAGIC, newLevel);
		player.setNextAnimation(new Animation(10530));
		player.setNextGraphics(new Graphics(738, 0, 100));
		player.getTemporaryAttributtes().put("VecnaSkullDelay",
				currentTime + 1000 * 60 * 8);
		player.getPackets()
				.sendGameMessage(
						"The skull feeds off the life arround you, boosting your magical ability.");
	}

	public static boolean useTabTeleport(final Player player, final int itemId) {
		if (itemId < 8007 || itemId > 8007 + TABS.length - 1)
			return false;
		if (useTeleTab(player, TABS[itemId - 8007]))
			player.getInventory().deleteItem(itemId, 1);
		return true;
	}

	public static boolean useTeleTab(final Player player, final WorldTile tile) {
		if (!player.getControlerManager().processItemTeleport(tile))
			return false;
		player.lock();
		player.setNextAnimation(new Animation(9597));
		player.setNextGraphics(new Graphics(1680));
		final boolean arriveInHouse = player.getHouse().isArriveInPortal();
		WorldTasksManager.schedule(new WorldTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.setNextAnimation(new Animation(4731));
					stage = 1;
				} else if (stage == 1) {
					WorldTile checkTile = tile == null ? player.getHouse()
							.getLocation().getTile() : tile;
					WorldTile teleTile = null;
					// attemps to randomize tile by 4x4 area
					for (int trycount = 0; trycount < 10; trycount++) {
						teleTile = new WorldTile(checkTile, 2);
						if (World.isTileFree(checkTile.getPlane(),
								teleTile.getX(), teleTile.getY(),
								player.getSize()))
							break;
						teleTile = checkTile;
					}
					player.setNextWorldTile(teleTile);
					player.getControlerManager().magicTeleported(ITEM_TELEPORT);
					player.setNextFaceWorldTile(new WorldTile(teleTile.getX(),
							teleTile.getY() - 1, teleTile.getPlane()));
					player.setDirection(6);
					player.setNextAnimation(new Animation(-1));
					if (tile == null && !arriveInHouse)
						player.getHouse().enterMyHouse();
					else if (player.getControlerManager().getControler() == null)
						teleControlersCheck(player, teleTile);
					stage = 2;
				} else if (stage == 2) {
					player.resetReceivedHits();
					if (tile != null || arriveInHouse)
						player.unlock();
					stop();
				}

			}
		}, 2, 1);
		return true;
	}

	public static void teleControlersCheck(Player player, WorldTile teleTile) {
		if (Kalaboss.isAtKalaboss(teleTile))
			player.getControlerManager().startControler("Kalaboss");
		else if (Wilderness.isAtWild(teleTile))
			player.getControlerManager().startControler("Wilderness");
		else if (ClanWarRequestController.inWarRequest(player))
			player.getControlerManager().startControler("clan_wars_request");
	}

	private Magic() {

	}

	public static void useEctoPhial(final Player player, Item item) {
		player.getInventory().deleteItem(item);
		player.getInventory().addItem(4252, 1);
		player.setNextGraphics(new Graphics(1688));
		player.setNextAnimation(new Animation(9609));
		player.getPackets().sendGameMessage(
				"You empty the ectoplasm onto the ground around your feet...",
				true);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				sendTeleportSpell(player, 8939, 8941, 1678, 1679, 0, 0,
						new WorldTile(3662, 3518, 0), 4, true, ITEM_TELEPORT);
			}
		}, 6);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage(
						"...and the world changes around you.", true);
				Ectofuntus.handleWorship(player);
			}
		}, 13);
	}

	public static void sendAssasianTeleport(final Player player, int i, int j,
			final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		if (player.isLocked())
			return;
		if (player.getDungManager().isInside()) {
			player.getPackets().sendGameMessage(
					"Use the normal exit to leave dungeoneering match.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.lock(3);
					player.setNextAnimation(new Animation(17074));
					player.setNextGraphics(new Graphics(3215));
				} else if (loop == 6) {
					player.setNextWorldTile(tile);
				} else if (loop == 7) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void sendPegasusTeleport(final Player player, int i, int j,
			final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		if (player.isLocked())
			return;
		if (player.getDungManager().isInside()) {
			player.getPackets().sendGameMessage(
					"Use the normal exit to leave dungeoneering match.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.lock(3);
					player.setNextAnimation(new Animation(17106));
					player.setNextGraphics(new Graphics(3223));
				} else if (loop == 6) {
					player.setNextWorldTile(tile);
				} else if (loop == 7) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void sendSkyJumpTeleport(final Player player, int i, int j,
			final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		if (player.isLocked())
			return;
		if (player.getDungManager().isInside()) {
			player.getPackets().sendGameMessage(
					"Use the normal exit to leave dungeoneering match.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.lock(3);
					player.setNextAnimation(new Animation(17317));
					player.setNextGraphics(new Graphics(3311));
					player.setNextGraphics(new Graphics(3310));
					player.setNextGraphics(new Graphics(3309));
				} else if (loop == 9) {
					player.setNextWorldTile(tile);
				} else if (loop == 10) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				} else if (loop == 11) {
					player.setNextAnimation(new Animation(808));
					player.setNextGraphics(new Graphics(-1));
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void sendDemonicTeleport(final Player player, int i, int j,
			final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		if (player.isLocked())
			return;
		if (player.getDungManager().isInside()) {
			player.getPackets().sendGameMessage(
					"Use the normal exit to leave dungeoneering match.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.lock(3);
					player.setNextAnimation(new Animation(17108));
					player.setNextGraphics(new Graphics(3224));
					player.setNextGraphics(new Graphics(3225));
				} else if (loop == 8) {
					player.setNextWorldTile(tile);
				} else if (loop == 10) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void sendGnomeTeleport(final Player player, int i, int j,
			final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		if (player.isLocked())
			return;
		if (player.getDungManager().isInside()) {
			player.getPackets().sendGameMessage(
					"Use the normal exit to leave dungeoneering match.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.lock(3);
					player.setNextAnimation(new Animation(17191));
					player.setNextGraphics(new Graphics(3254));
				} else if (loop == 6) {
					player.setNextWorldTile(tile);
				} else if (loop == 7) {
					player.setNextAnimation(new Animation(16386));
					player.setNextGraphics(new Graphics(3019));
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void sendPyramidTeleport(final Player player, int i, int j,
			final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		if (player.isLocked())
			return;
		if (player.getDungManager().isInside()) {
			player.getPackets().sendGameMessage(
					"Use the normal exit to leave dungeoneering match.");
			return;
		}
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.lock(3);
					player.setNextAnimation(new Animation(23315));
					player.setNextGraphics(new Graphics(2602));
				} else if (loop == 2) {
					player.setNextWorldTile(tile);
				}
				loop++;
			}
		}, 0, 1);
	}
}