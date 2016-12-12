package net.kagani.game.minigames.pest;

import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.HerbCleaning.Herbs;
import net.kagani.game.player.content.Nest;
import net.kagani.utils.Utils;

public class CommendationExchange {

	private static final int PURCHASE_INTERFACE = 1011, EXP_TYPE = 1,
			VOID_TYPE = 2, CONSUMABLE_TYPE = 3;

	enum CommendationStock {

		ATTACK_XP(1, Skills.ATTACK | EXP_TYPE << 24, new int[] { 66, 84, 86 }), STRENGTH_XP(
				1, Skills.STRENGTH | EXP_TYPE << 24, new int[] { 98, 100, 102 }), DEFENCE_XP(
				1, Skills.DEFENCE | EXP_TYPE << 24, new int[] { 114, 116, 118 }), CONSTITUTION_XP(
				1, Skills.HITPOINTS | EXP_TYPE << 24,
				new int[] { 130, 132, 134 }), RANGE_XP(1, Skills.RANGE
				| EXP_TYPE << 24, new int[] { 146, 148, 150 }), MAGE_XP(1,
				Skills.MAGIC | EXP_TYPE << 24, new int[] { 164, 166, 168 }), PRAYER_XP(
				1, Skills.PRAYER | EXP_TYPE << 24, new int[] { 179, 181, 183 }), SUMMONING_XP(
				1, Skills.SUMMONING | EXP_TYPE << 24,
				new int[] { 338, 340, 342 }),

		VOID_KNIGHT_MELEE_HELM(200, 11676 | VOID_TYPE << 24, new int[] { 362 }), VOID_KNIGHT_RANGER_HELM(
				200, 11675 | VOID_TYPE << 24, new int[] { 374 }), VOID_KNIGHT_MAGE_HELM(
				200, 11674 | VOID_TYPE << 24, new int[] { 386 }), VOID_KNIGHT_TOP(
				250, 8839 | VOID_TYPE << 24, new int[] { 398 }), VOID_KNIGHT_ROBES(
				250, 8840 | VOID_TYPE << 24, new int[] { 410 }), VOID_KNIGHT_GLOVES(
				150, 8842 | VOID_TYPE << 24, new int[] { 422 }),

		VOID_KNIGHT_MACE(250, 8841 | VOID_TYPE << 24, new int[] { 195 }), VOID_KNIGHT_DEFLECTOR(
				150, 19711 | VOID_TYPE << 24, new int[] { 209 }), VOID_KNIGHT_SEAL(
				10, 11666 | VOID_TYPE << 24, new int[] { 220 }), JESSIKA_SWORD(
				350, 31639 | VOID_TYPE << 24, new int[] { 433 }),

		HERBLORE_PACK(30, 0 | CONSUMABLE_TYPE << 24, new int[] { 231 }), MINERAL_PACK(
				15, 0 | CONSUMABLE_TYPE << 24, new int[] { 242 }), SEED_PACK(
				15, 0 | CONSUMABLE_TYPE << 24, new int[] { 253 }), ARMOUR_PATCH(
				444, 31640 | CONSUMABLE_TYPE << 24, new int[] { 444 }),

		SPINNER_CHARMS(2, 27062 | CONSUMABLE_TYPE << 24, new int[] { 264, 266,
				268 }), TORCHER_CHARMS(2, 27063 | CONSUMABLE_TYPE << 24,
				new int[] { 279, 281, 283 }), RAVAGER_CHARMS(2,
				27060 | CONSUMABLE_TYPE << 24, new int[] { 294, 296, 298 }), SHIFTER_CHARMS(
				2, 27061 | CONSUMABLE_TYPE << 24, new int[] { 309, 311, 313 }), ;

		private CommendationStock(int cost, int typeHash, int[] components) {
			this.cost = cost;
			this.typeHash = typeHash;
			this.components = components;
		}

		private int[] components;
		private int cost, typeHash;

		public int[] getComponents() {
			return components;
		}

		public int getCost() {
			return cost;
		}

		public int getTypeHash() {
			return typeHash;
		}

		public String getName() {
			return Utils.formatPlayerNameForDisplay(name());
		}
	}

	public static void openExchangeShop(Player player) {
		player.getInterfaceManager().sendCentralInterface(PURCHASE_INTERFACE);
		player.getTemporaryAttributtes().put(Key.PEST_REWARD_TAB, 0);
		player.getVarsManager().sendVar(1660, 1250);// Conquest Ranking
		refreshCommendations(player);
	}

	private static void selectPurchase(Player player, CommendationStock stock,
			int index) {
		int key = stock.getTypeHash() & 0xFFFF;
		int type = stock.getTypeHash() >> 24;

		if (type == EXP_TYPE) {
			if (player.getSkills().getLevelForXp(key) < 25) {
				player.getPackets()
						.sendGameMessage(
								"A "
										+ Skills.SKILL_NAME[key]
										+ " level of at least 25 is required to purchase experience.");
				return;
			}
		} else if (type == VOID_TYPE) {
			if (stock != CommendationStock.VOID_KNIGHT_SEAL) {
				if (!player.getSkills().hasRequiriments(Skills.ATTACK, 42,
						Skills.STRENGTH, 42, Skills.DEFENCE, 42,
						Skills.HITPOINTS, 42, Skills.RANGE, 42, Skills.MAGIC,
						42, Skills.PRAYER, 22)) {
					player.getPackets()
							.sendGameMessage(
									"Higher levels are required in order to purchase Void Equipment.");
					return;
				}
			}
		} else if (type == CONSUMABLE_TYPE) {
			if (stock == CommendationStock.HERBLORE_PACK) {
				if (player.getSkills().getLevelForXp(Skills.HERBLORE) < 25) {
					player.getPackets()
							.sendGameMessage(
									"A Herblore level of 25 is required in order to purchase a herblore pack.");
					return;
				}
			} else if (stock == CommendationStock.MINERAL_PACK) {
				if (player.getSkills().getLevelForXp(Skills.MINING) < 25) {
					player.getPackets()
							.sendGameMessage(
									"A Mining level of 25 is required in order to purchase a mineral pack.");
					return;
				}
			} else if (stock == CommendationStock.SEED_PACK) {
				if (player.getSkills().getLevelForXp(Skills.FARMING) < 25) {
					player.getPackets()
							.sendGameMessage(
									"A Farming level of 25 is required in order to purchase a seed pack.");
					return;
				}
			}
		}
		int rate = (int) Math.pow(10, index);
		if (type == CONSUMABLE_TYPE && stock.getComponents().length > 1) // Charms
																			// exception
																			// required.
			rate = player.getCommendation() / 2;
		int cost = stock.getCost() * rate;
		if (player.getCommendation() < cost) {
			player.getPackets()
					.sendGameMessage(
							cost
									+ " Commendations are required in order to complete this purchase.");
			return;
		}
		player.getTemporaryAttributtes().put(Key.PEST_REWARD_RATE, rate);
		player.getTemporaryAttributtes().put(Key.PEST_REWARD_STOCK, stock);

		player.getPackets().sendHideIComponent(PURCHASE_INTERFACE, 4, false);
		player.getPackets().sendIComponentText(
				PURCHASE_INTERFACE,
				320,
				"Are you sure you wish to exchange " + cost
						+ " Commendations in return for "
						+ (stock.getComponents().length > 1 ? "" : "a ") + ""
						+ stock.getName() + "?");
	}

	private static void confirmPurchase(Player player, boolean complete) {
		if (complete)
			completePurchase(player);
		player.getTemporaryAttributtes().remove(Key.PEST_REWARD_STOCK);
		player.getTemporaryAttributtes().remove(Key.PEST_REWARD_RATE);
		player.getPackets().sendHideIComponent(PURCHASE_INTERFACE, 4, true);
	}

	private static void completePurchase(Player player) {
		CommendationStock stock = (CommendationStock) player
				.getTemporaryAttributtes().get(Key.PEST_REWARD_STOCK);
		int rate = (int) player.getTemporaryAttributtes().get(
				Key.PEST_REWARD_RATE);

		int key = stock.getTypeHash() & 0xFFFF;
		int type = stock.getTypeHash() >> 24;
		if (type == EXP_TYPE) {
			double experience = getExperience(player, key, rate);
			player.getSkills().addXp(key, experience);
			player.getPackets().sendGameMessage(
					"You exchange " + rate + " Commendations and recieve "
							+ (int) experience + " " + stock.getName()
							+ " in return.");
		} else if (type == VOID_TYPE || type == CONSUMABLE_TYPE) {
			boolean spaceAvailable = player.getInventory().hasFreeSlots();
			Item[] packs = new Item[stock == CommendationStock.SEED_PACK ? 3
					: 2];
			if (key == 0) {
				if (stock == CommendationStock.HERBLORE_PACK) {
					packs[0] = new Item(
							Herbs.values()[Utils.random(5)].getHerbId() + 1,
							Utils.random(1, 4));
					packs[1] = new Item(Herbs.values()[Utils.random(Herbs
							.values().length - 1)].getHerbId() + 1,
							Utils.random(1, 3));
				} else if (stock == CommendationStock.MINERAL_PACK) {
					packs[0] = new Item(441, Utils.random(1, 30));
					packs[1] = new Item(454, Utils.random(1, 20));
				} else if (stock == CommendationStock.SEED_PACK) {
					packs[0] = new Item(
							Nest.SEEDS[0][Utils.random(Nest.SEEDS[0].length)],
							Utils.random(1, 5));
					packs[1] = new Item(
							Nest.SEEDS[0][Utils.random(Nest.SEEDS[0].length)],
							Utils.random(1, 3));
					packs[2] = new Item(
							Nest.SEEDS[1][Utils.random(Nest.SEEDS[1].length)],
							Utils.random(1, 2));
				}
				spaceAvailable = player.getInventory().getFreeSlots(packs)
						- packs.length >= 0;
				if (!spaceAvailable)
					spaceAvailable = player.getInventory().containsItems(packs);
			}
			if (!spaceAvailable) {
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				return;
			}
			if (key != 0)
				player.getInventory().addItem(key, rate);
			else {
				for (Item pack : packs)
					player.getInventory().addItem(pack);
			}
		}
		player.setCommendation(player.getCommendation()
				- (stock.getCost() * rate));
		refreshCommendations(player);
	}

	public static void handleButtonOptions(Player player, int componentId) {
		if (componentId == 344 || componentId == 6 || componentId == 68)
			switchTab(
					player,
					0,
					(int) player.getTemporaryAttributtes().get(
							Key.PEST_REWARD_TAB));
		else if (componentId == 8 || componentId == 24 || componentId == 70)
			switchTab(
					player,
					1,
					(int) player.getTemporaryAttributtes().get(
							Key.PEST_REWARD_TAB));
		else if (componentId == 26 || componentId == 72 || componentId == 348)
			switchTab(
					player,
					2,
					(int) player.getTemporaryAttributtes().get(
							Key.PEST_REWARD_TAB));
		else if (componentId == 12 || componentId == 28 || componentId == 350)
			switchTab(
					player,
					3,
					(int) player.getTemporaryAttributtes().get(
							Key.PEST_REWARD_TAB));
		else if (componentId == 323 || componentId == 325)
			confirmPurchase(player, componentId == 323);
		else {
			for (CommendationStock stock : CommendationStock.values())
				for (int count = 0; count < stock.getComponents().length; count++)
					if (componentId == stock.getComponents()[count])
						selectPurchase(player, stock, count);
		}
	}

	private static void refreshCommendations(Player player) {
		player.getVarsManager().sendVarBit(4861, player.getCommendation());
	}

	private static void switchTab(Player player, int tab, int previousTab) {
		player.getPackets().sendHideIComponent(PURCHASE_INTERFACE, tab, false);
		player.getPackets().sendHideIComponent(PURCHASE_INTERFACE, previousTab,
				true);
		player.getTemporaryAttributtes().put(Key.PEST_REWARD_TAB, tab);
	}

	private static double getExperience(Player player, int skill, int rate) {
		int level = player.getSkills().getLevelForXp(skill);
		double experience = Math.pow(level, 2)
				/ ((skill == Skills.MAGIC || skill == Skills.RANGE) ? 5.195
						: (skill == Skills.PRAYER || skill == Skills.SUMMONING) ? 19.23
								: 5.0);
		double bonus = experience * (Math.floor(0.1D * rate) * 0.1D);
		return experience * rate + bonus;
	}
}
