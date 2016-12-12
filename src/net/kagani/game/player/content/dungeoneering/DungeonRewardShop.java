package net.kagani.game.player.content.dungeoneering;

import java.util.HashMap;
import java.util.Map;

import net.kagani.cache.loaders.GeneralRequirementMap;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Utils;

public class DungeonRewardShop {

	public static final int REWARD_SHOP = 940;

	public static void openRewardShop(final Player player) {
		player.getInterfaceManager().sendCentralInterface(REWARD_SHOP);
		player.getPackets().sendUnlockIComponentOptionSlots(REWARD_SHOP, 87, 0,
				285, 0, 1, 2);
		refreshPoints(player);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getTemporaryAttributtes()
						.remove(Key.DUNGEON_REWARD_SLOT);
			}
		});
	}

	public static void purchase(Player player) {
		if (!canPurchase(player))
			return;
		removeConfirmationPurchase(player);
		int slot = (int) player.getTemporaryAttributtes().get(
				Key.DUNGEON_REWARD_SLOT);
		DungeonReward reward = DungeonReward.forId(slot);
		System.out.println(slot + " id;");
		if (reward != null) {
			player.getInventory().addItemDrop(reward.getId(), 1);
			player.getDungManager().addTokens(-reward.getCost());
		}
		refreshPoints(player);
	}

	public static void sendConfirmationPurchase(Player player) {
		if (!canPurchase(player)) {
			return;
		}
		player.getPackets().sendHideIComponent(REWARD_SHOP, 42, false);
	}

	public static void removeConfirmationPurchase(Player player) {
		player.getPackets().sendHideIComponent(REWARD_SHOP, 42, true);
	}

	public static void select(Player player, int slot) {
		player.getTemporaryAttributtes().put(Key.DUNGEON_REWARD_SLOT, slot);
	}

	private static boolean canPurchase(Player player) {
		if (player.getTemporaryAttributtes().get(Key.DUNGEON_REWARD_SLOT) == null)
			return false;
		int slot = (int) player.getTemporaryAttributtes().get(
				Key.DUNGEON_REWARD_SLOT);
		DungeonReward reward = DungeonReward.forId(slot);
		if (reward == null) {
			player.getPackets().sendGameMessage(
					"[Undefined slotId] Item not found. "
							+ (player.getRights() == 2 ? slot : ""));
			return false;
		} else {
			player.getTemporaryAttributtes().put("dungReward", reward);
		}
		boolean purchaseEnabled = true;
		GeneralRequirementMap map = GeneralRequirementMap.getMap(slot);
		int dungeoneeringLevel = reward.getRequirement(), price = reward
				.getCost();
		if (!(player.getRights() == 2)
				&& (player.getSkills().getLevel(Skills.DUNGEONEERING) < dungeoneeringLevel || player
						.getDungManager().getTokens() < price)) {
			player.getPackets().sendGameMessage(
					"You do not meet the requirements to purchase this item.");
			return false;
		} else {
			if (player.getInventory().getFreeSlots() >= 1) {
				if (player.getDungManager().getTokens() < reward.getCost()) {
					player.getPackets().sendGameMessage(
							reward.getName() + " costs "
									+ Utils.format(reward.getCost())
									+ " Dungeoneering tokens.");
					return false;
				}
				if (player.getSkills().getLevel(Skills.DUNGEONEERING) < reward
						.getRequirement())
					return false;
				player.getDungManager().setTokens(
						player.getDungManager().getTokens() - reward.getCost());
				player.getInventory().addItem(reward.getId(), 1);
				DungeonRewardShop.refreshPoints(player);
				player.getPackets().sendGameMessage(
						"You have bought a " + reward.getName() + " for "
								+ Utils.format(reward.getCost())
								+ " Dungeoneering tokens.");
			} else {
				player.getPackets().sendGameMessage(
						"You need more inventory space.");
			}
		}
		return true;

	}

	public static void refreshPoints(Player player) {
		player.getPackets().sendIComponentText(REWARD_SHOP, 89,
				Integer.toString(player.getDungManager().getTokens()));
	}

	public enum DungeonReward {
		// id, slotid, req, amount
		XP_LAMP(23749, 0, 1, 8000), RING_OF_KINSHIP_RESET(15707, 5, 1, 1), HOARDWALKER_RING(
				31436, 10, 10, 10000), WILD_CARD(31456, 15, 1, 10000), BONECRUSHER(
				18337, 20, 21, 34000), HERBICIDE(19675, 25, 21, 34000), CHARMING_IMP(
				27996, 30, 21, 100000), SCROLL_OF_LIFE(18336, 35, 25, 10000), GEM_BAG(
				18338, 40, 25, 2000), ARCANE_PULSE_NECKLACE(18333, 45, 30, 6500), FARSIGHT_QUICK_SHOT_NECKLACE(
				31443, 50, 30, 6500), BRAWLERS_JAB_NECKLACE(31446, 55, 30, 6500), TWISTED_BIRD_SKULL_NECKLACE(
				19886, 60, 30, 8500), COAL_BAG(18339, 65, 35, 20000), GEM_UPGRADE_BAG(
				31455, 70, 25, 20000), SHIELDBOW_SIGHT(29634, 75, 45, 10000), LAWSTAFF(
				18342, 80, 45, 10000), GRAVITE_RAPIER(18365, 85, 45, 40000), GRAVITE_LONGSWORD(
				18367, 90, 45, 40000), GRAVITE_2H(18369, 95, 45, 40000), GRAVITE_STAFF(
				18371, 100, 45, 40000), GRAVITE_SHORTBOW(18373, 105, 45, 40000), TOME_OF_FROST(
				18346, 110, 48, 43000), AMULET_OF_ZEALOTS(19892, 115, 45, 40000), SCROLL_OF_CLEANSING(
				19890, 120, 49, 20000), SPIRIT_CAPE(19893, 125, 50, 45000), NATURE_STAFF(
				18341, 130, 53, 12000), SCROLL_OF_EFFICIENCY(19670, 135, 55,
				20000), ARCANE_BLAST_NECKLACE(18334, 140, 50, 15500), FARSIGHT_SNAP_SHOT_NECKLACE(
				31444, 145, 50, 15500), BRAWLERS_HOOK_NECKLACE(31447, 150, 50,
				15500), SPLIT_DRAGONTOOTH_NECKLACE(19887, 155, 60, 17000), ANTI_POISON_TOTEM(
				18340, 160, 60, 44000), SCROLL_OF_PROFICIENCY(19670, 165, 60,
				20000), SCROLL_OF_DEXTERITY(31452, 170, 60, 20000), GOLD_ACCUMULATOR(
				31453, 175, 1, 20000), RING_OF_VIGOUR(19669, 180, 62, 50000), SCROLL_OF_RENEWAL(
				18343, 185, 65, 38000),
		// SCROLL_OF_DAEMONHEIM(31450190,
		// 190,
		// 70,
		// 30000),
		MERCENARY_GLOVES(18347, 195, 73, 48500), CHAOTIC_SPIKE(27068, 200, 80,
				20000), CHAOTIC_REMANT(31449, 205, 80, 200000), CHAOTIC_RAPIER(
				18349, 210, 80, 200000), CHAOTIC_LONGSWORD(18351, 215, 80,
				200000), CHAOTIC_MAUL(18353, 220, 80, 200000), CHAOTIC_STAFF(
				18355, 225, 80, 200000), CHAOTIC_CROSSBOW(18357, 230, 80,
				200000), OFF_HAND_CHAOTIC_RAPIER(25991, 235, 80, 100000), OFF_HAND_CHAOTIC_LONGSWORD(
				25993, 240, 80, 100000), OFF_HAND_CHAOTIC_CROSSBOW(25995, 245,
				80, 100000), SNEAKER_PEAKER_SPAWN(19894, 250, 80, 85000), CHAOTIC_KITESHIELD(
				18359, 255, 80, 200000), EAGLE_EYE_KITESHIELD(18361, 260, 80,
				200000), FARSEER_KITESHIELD(18363, 265, 80, 200000), DEMON_HORN_NECKLACE(
				19888, 270, 90, 35000), FROSTY(31459, 275, 99, 250000), MINI_BLINK(
				31457, 280, 99, 500000), HOPE_NIBBLER(31458, 285, 101, 1000000);

		private static Map<Integer, DungeonReward> rewards;

		public static DungeonReward forId(int id) {
			if (rewards == null)
				init();
			return rewards.get(id);
		}

		static void init() {
			rewards = new HashMap<Integer, DungeonReward>();
			for (DungeonReward dr : DungeonReward.values())
				rewards.put(dr.slotId, dr);
		}

		private int id;
		private int req;
		private int cost;
		private int slotId;
		private String name;

		private DungeonReward(int id, int slotId, int req, int cost) {
			this.id = id;
			this.req = req;
			this.cost = cost;
			this.slotId = slotId;
			this.name = ItemDefinitions.getItemDefinitions(id).getName();
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getCost() {
			return cost;
		}

		public int getSlotId() {
			return slotId;
		}

		public int getRequirement() {
			return req;
		}
	}
}