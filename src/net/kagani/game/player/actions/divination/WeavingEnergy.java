package net.kagani.game.player.actions.divination;

import net.kagani.Settings;
import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.utils.Utils;

public class WeavingEnergy extends Action {

	/**
	 * @author: Dylan Page
	 */

	public enum Energy {

		/**
		 * Pale Energy
		 */
		DIVINE_CRAYFISH_BUBBLE(1, 1.0, new Item[] { new Item(29313, 5),
				new Item(13435, 20) }, new Item(31080)),

		PORTENT_OF_RESTORATION_1(2, 1.0, new Item[] { new Item(29313, 30),
				new Item(13433, 5) }, new Item(29209)),

		DIVINE_BRONZE_ROCK(3, 1.2, new Item[] { new Item(29313, 20),
				new Item(436, 20) }, new Item(29294)),

		DIVINE_KEBBIT_BURRO(4, 1.3, new Item[] { new Item(29313, 25),
				new Item(9986, 20) }, new Item(29300)),

		ATTUNED_PORTENT_OF_RESTORATION_1(5, 1.5, new Item[] {
				new Item(29313, 30), new Item(13433, 1) }, new Item(29259)),

		SIGN_OF_THE_PORTER_1(6, 1.7, new Item[] { new Item(29313, 30),
				new Item(1656, 1) }, new Item(29275)),

		DIVINE_TREE(7, 1.8,
				new Item[] { new Item(29313, 5), new Item(1511, 20) },
				new Item(29304)),

		SIGN_OF_RESPITE_1(8, 2.0, new Item[] { new Item(29313, 25),
				new Item(3211, 4) }, new Item(29269)),

		BOON_OF_FLICKERING_ENERGY(10, 3.0, new Item[] { new Item(29313, 100) },
				new Item(29373)),

		/**
		 * Flickering Energy
		 */
		DIVINE_HERRING_BUBBLE(11, 3.0, new Item[] { new Item(29314, 15),
				new Item(345, 20) }, new Item(31081)),

		DIVINE_HERB_PATCH_I(12, 3.1, new Item[] { new Item(29314, 5),
				new Item(249, 10) }, new Item(29310)),

		DIVINE_IRON_ROCK(19, 4.0, new Item[] { new Item(29314, 20),
				new Item(440, 15) }, new Item(29295)),

		/**
		 * Bright Energy
		 */
		DIVINE_TROUT_BUBBLE(20, 5.0, new Item[] { new Item(29315, 15),
				new Item(335, 20) }, new Item(31082)),

		DIVINE_OAK_TREE(21, 5.1, new Item[] { new Item(29315, 15),
				new Item(1521, 20) }, new Item(29305)),

		DIVINE_BIRD_SNARE(24, 5.4, new Item[] { new Item(29315, 30),
				new Item(9978, 20) }, new Item(29301)),

		/**
		 * Glowing Energy
		 */
		DIVINE_SALMON_BUBBLE(30, 7.0, new Item[] { new Item(29316, 45),
				new Item(331, 20) }, new Item(31083)),

		DIVINE_WILLOW_TREE(31, 7.2, new Item[] { new Item(29316, 20),
				new Item(1519, 20) }, new Item(29306)),

		DIVINE_COAL_ROCK(31, 7.2, new Item[] { new Item(29316, 30),
				new Item(453, 20) }, new Item(29296)),

		DIVINE_DEADFALL_TRAP(34, 7.5, new Item[] { new Item(29316, 45),
				new Item(10113, 24) }, new Item(29302)),

		/**
		 * Sparkling Energy
		 */
		DIVINE_LOBSTER_BUBBLE(41, 10.0, new Item[] { new Item(29317, 70),
				new Item(377, 20) }, new Item(31084)),

		DIVINE_MAPLE_TREE(44, 9.3, new Item[] { new Item(29317, 25),
				new Item(1517, 20) }, new Item(29307)),

		/**
		 * Gleaming Energy
		 */
		DIVINE_HERB_PATCH_II(51, 11.3, new Item[] { new Item(29318, 20),
				new Item(259, 20) }, new Item(29311)),

		DIVINE_SWORDFISH_BUBBLE(53, 13.0, new Item[] { new Item(29318, 15),
				new Item(371, 20) }, new Item(31082)),

		/**
		 * Vibrant Energy
		 */
		DIVINE_MITHRIL_ROCK(61, 13.1, new Item[] { new Item(29319, 30),
				new Item(447, 20) }, new Item(29297)),

		DIVINE_YEW_TREE(62, 13.2, new Item[] { new Item(29319, 30),
				new Item(1515, 20) }, new Item(29308)),

		DIVINE_BOX_TRAP(64, 13.4, new Item[] { new Item(29319, 45),
				new Item(10033, 20) }, new Item(29303)),

		/**
		 * Lustrous Energy
		 */
		DIVINE_ADAMANITE_ROCK(73, 15.3, new Item[] { new Item(29320, 40),
				new Item(449, 25) }, new Item(29298)),

		DIVINE_SHARK_BUBBLE(79, 16.0, new Item[] { new Item(29320, 60),
				new Item(383, 20) }, new Item(31086)),

		/**
		 * Elder Energy
		 */
		// Nothing

		/**
		 * Brilliant Energy
		 */
		DIVINE_HERB_PATCH_III(82, 17.5, new Item[] { new Item(29321, 10),
				new Item(265, 5) }, new Item(29312)),

		DIVINE_MAGIC_TREE(83, 18, new Item[] { new Item(29321, 40),
				new Item(1513, 5) }, new Item(29309)),

		/**
		 * Radiant Energy
		 */
		DIVINE_CAVEFISH_BUBBLE(89, 20.0, new Item[] { new Item(29322, 70),
				new Item(15264, 15) }, new Item(31087)),

		/**
		 * Luminous Energy
		 */
		DIVINE_ROCKTAIL_BUBBLE(91, 21.0, new Item[] { new Item(29323, 80),
				new Item(15270, 10) }, new Item(31088)),

		DIVINE_RUNITE_ROCK(94, 22.0, new Item[] { new Item(29323, 80),
				new Item(451, 6) }, new Item(29299)),

		/**
		 * Incandescent Energy
		 */
		// Nothing

		;

		public static Energy getEnergyProduce(int id) {
			for (Energy ener : Energy.values()) {
				if (ener.getProduceEnergy().getId() == id)
					return ener;
			}
			return null;
		}

		public static Energy getEnergy(int id) {
			for (Energy ener : Energy.values()) {
				for (Item item : ener.getItemsRequired())
					if (item.getId() == id)
						return ener;
			}
			return null;
		}

		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private Item energyProduce;

		private Energy(int levelRequired, double experience,
				Item[] itemsRequired, Item energyProduce) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.itemsRequired = itemsRequired;
			this.energyProduce = energyProduce;
		}

		public Item[] getItemsRequired() {
			return itemsRequired;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item getProduceEnergy() {
			return energyProduce;
		}

		public double getExperience() {
			return experience;
		}

	}

	public Energy energy;
	public Item item;
	public int ticks;

	public WeavingEnergy(Energy bar, Item item, int ticks) {
		this.item = item;
		this.energy = bar;
		this.ticks = ticks;
	}

	@Override
	public boolean start(Player player) {
		if (energy == null || player == null || item == null) {
			return false;
		}
		if ((Utils.currentTimeMillis() - player.lastDivineLocation) < (24 * 60 * 60 * 1000)) {
			player.getPackets().sendGameMessage(
					"You may only place one divine location each day.");
			return false;
		}
		if (!player.getInventory().containsItemToolBelt(
				energy.getItemsRequired()[0].getId(),
				energy.getItemsRequired()[0].getAmount())) {
			player.getPackets().sendGameMessage(
					"You need "
							+ energy.getItemsRequired()[0].getDefinitions()
									.getName()
							+ " to create a "
							+ energy.getProduceEnergy().getDefinitions()
									.getName() + ".");
			return false;
		}
		if (energy.getItemsRequired().length > 1) {
			if (!player.getInventory().containsItemToolBelt(
					energy.getItemsRequired()[1].getId(),
					energy.getItemsRequired()[1].getAmount())) {
				player.getPackets().sendGameMessage(
						"You need "
								+ energy.getItemsRequired()[1].getDefinitions()
										.getName()
								+ " to create a "
								+ energy.getProduceEnergy().getDefinitions()
										.getName() + ".");
				return false;
			}
		}
		if (player.getSkills().getLevel(Skills.DIVINATION) < energy
				.getLevelRequired()) {
			player.getPackets().sendGameMessage(
					"You need a Divination level of at least "
							+ energy.getLevelRequired()
							+ " to weave "
							+ energy.getProduceEnergy().getDefinitions()
									.getName());
			return false;
		}
		player.getPackets().sendGameMessage(
				"You being to weave the energy and attempt to create a "
						+ energy.getProduceEnergy().getDefinitions().getName()
								.toLowerCase().replace(" bar", "") + ".", true);
		player.lastDivineLocation = Utils.currentTimeMillis();
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (energy == null || player == null || item == null) {
			return false;
		}
		if (!player.getInventory().containsItemToolBelt(
				energy.getItemsRequired()[0].getId(),
				energy.getItemsRequired()[0].getAmount())) {
			player.getPackets().sendGameMessage(
					"You need "
							+ energy.getItemsRequired()[0].getDefinitions()
									.getName()
							+ " to create a "
							+ energy.getProduceEnergy().getDefinitions()
									.getName() + ".");
			return false;
		}
		if (energy.getItemsRequired().length > 1) {
			if (!player.getInventory().containsItemToolBelt(
					energy.getItemsRequired()[1].getId(),
					energy.getItemsRequired()[1].getAmount())) {
				player.getPackets().sendGameMessage(
						"You need "
								+ energy.getItemsRequired()[1].getDefinitions()
										.getName()
								+ " to create a "
								+ energy.getProduceEnergy().getDefinitions()
										.getName() + ".");
				return false;
			}
		}
		if (player.getSkills().getLevel(Skills.DIVINATION) < energy
				.getLevelRequired()) {
			player.getPackets().sendGameMessage(
					"You need a Divination level of at least "
							+ energy.getLevelRequired()
							+ " to smelt "
							+ energy.getProduceEnergy().getDefinitions()
									.getName());
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		ticks--;
		player.setNextAnimation(new Animation(21225));
		player.setNextGraphics(new Graphics(4249));
		double xp = energy.getExperience();
		player.getSkills().addXp(Skills.DIVINATION, xp);
		for (Item required : energy.getItemsRequired()) {
			if (required.getId() == 4 || required.getId() == 2976
					|| required.getId() == 1594 || required.getId() == 1599
					|| required.getId() == 5523)
				continue;
			player.getInventory().deleteItem(required.getId(),
					required.getAmount());
		}
		int amount = energy.getProduceEnergy().getAmount();
		if (energy.getProduceEnergy().getDefinitions().isStackable())
			amount *= Settings.getCraftRate(player);
		player.getInventory()
				.addItem(energy.getProduceEnergy().getId(), amount);
		player.getPackets().sendGameMessage(
				"You successfully weave the energy into a "
						+ energy.getProduceEnergy().getDefinitions().getName()
								.toLowerCase() + ".", true);
		if (ticks > 0) {
			return 1;
		}
		return -1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}