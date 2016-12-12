package net.kagani.game.player.content;

import net.kagani.Settings;
import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;

public class JewellerySmithing extends Action {

	public enum Jewellery {

		/**
		 * Pale Energy Products
		 */
		GOLD_RING(5, 15.0, new Item[] { new Item(2357), new Item(1635) },
				new Item(11073)), SAPPHIRE_BRACELET(23, 60.0, new Item[] {
				new Item(2357), new Item(1607) }, new Item(11072)),

		/**
		 * Flicking Energy Products
		 */

		;

		public static Jewellery getProduce(int id) {
			for (Jewellery jewel : Jewellery.values()) {
				if (jewel.getProduce().getId() == id)
					return jewel;
			}
			return null;
		}

		public static Jewellery getBar(int id) {
			for (Jewellery jewel : Jewellery.values()) {
				for (Item item : jewel.getItemsRequired())
					if (item.getId() == id)
						return jewel;
			}
			return null;
		}

		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private Item produce;

		private Jewellery(int levelRequired, double experience,
				Item[] itemsRequired, Item produce) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.itemsRequired = itemsRequired;
			this.produce = produce;
		}

		public Item[] getItemsRequired() {
			return itemsRequired;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item getProduce() {
			return produce;
		}

		public double getExperience() {
			return experience;
		}

	}

	public Jewellery jewellery;
	public Item item;
	public int ticks;

	public JewellerySmithing(Jewellery bar, Item item, int ticks) {
		this.item = item;
		this.jewellery = bar;
		this.ticks = ticks;
	}

	@Override
	public boolean start(Player player) {
		if (jewellery == null || player == null || item == null) {
			return false;
		}
		if (!player.getInventory().containsItemToolBelt(
				jewellery.getItemsRequired()[0].getId(),
				jewellery.getItemsRequired()[0].getAmount())) {
			player.getPackets().sendGameMessage(
					"You need "
							+ jewellery.getItemsRequired()[0].getDefinitions()
									.getName() + " to create a "
							+ jewellery.getProduce().getDefinitions().getName()
							+ ".");
			return false;
		}
		if (jewellery.getItemsRequired().length > 1) {
			if (!player.getInventory().containsItemToolBelt(
					jewellery.getItemsRequired()[1].getId(),
					jewellery.getItemsRequired()[1].getAmount())) {
				player.getPackets().sendGameMessage(
						"You need "
								+ jewellery.getItemsRequired()[1]
										.getDefinitions().getName()
								+ " to create a "
								+ jewellery.getProduce().getDefinitions()
										.getName() + ".");
				return false;
			}
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < jewellery
				.getLevelRequired()) {
			player.getPackets()
					.sendGameMessage(
							"You need a Crafting level of at least "
									+ jewellery.getLevelRequired()
									+ " to weave "
									+ jewellery.getProduce().getDefinitions()
											.getName());
			return false;
		}
		player.getPackets().sendGameMessage(
				"You smelt the bar and attempt to create a "
						+ jewellery.getProduce().getDefinitions().getName()
								.toLowerCase().replace(" bar", "") + ".", true);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (jewellery == null || player == null || item == null) {
			return false;
		}
		if (!player.getInventory().containsItemToolBelt(
				jewellery.getItemsRequired()[0].getId(),
				jewellery.getItemsRequired()[0].getAmount())) {
			player.getPackets().sendGameMessage(
					"You need "
							+ jewellery.getItemsRequired()[0].getDefinitions()
									.getName() + " to create a "
							+ jewellery.getProduce().getDefinitions().getName()
							+ ".");
			return false;
		}
		if (jewellery.getItemsRequired().length > 1) {
			if (!player.getInventory().containsItemToolBelt(
					jewellery.getItemsRequired()[1].getId(),
					jewellery.getItemsRequired()[1].getAmount())) {
				player.getPackets().sendGameMessage(
						"You need "
								+ jewellery.getItemsRequired()[1]
										.getDefinitions().getName()
								+ " to create a "
								+ jewellery.getProduce().getDefinitions()
										.getName() + ".");
				return false;
			}
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < jewellery
				.getLevelRequired()) {
			player.getPackets()
					.sendGameMessage(
							"You need a Crafting level of at least "
									+ jewellery.getLevelRequired()
									+ " to smelt "
									+ jewellery.getProduce().getDefinitions()
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
		double xp = jewellery.getExperience();
		player.getSkills().addXp(Skills.CRAFTING, xp);
		for (Item required : jewellery.getItemsRequired()) {
			if (required.getId() == 4 || required.getId() == 2976
					|| required.getId() == 1594 || required.getId() == 1599
					|| required.getId() == 5523)
				continue;
			player.getInventory().deleteItem(required.getId(),
					required.getAmount());
		}
		int amount = jewellery.getProduce().getAmount();
		if (jewellery.getProduce().getDefinitions().isStackable())
			amount *= Settings.getCraftRate(player);
		player.getInventory().addItem(jewellery.getProduce().getId(), amount);
		player.getPackets().sendGameMessage(
				"You successfully smelt the gold into a "
						+ jewellery.getProduce().getDefinitions().getName()
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
