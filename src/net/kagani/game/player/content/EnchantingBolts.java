package net.kagani.game.player.content;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;

public class EnchantingBolts {

	public enum Bolts {
		OPAL(14, 4, new Item[] { new Item(564), new Item(556, 2) }, 879, 9236,
				9), SAPPHIRE(29, 7, new Item[] { new Item(564), new Item(555),
				new Item(558) }, 9337, 9240, 17), JADE(18, 14, new Item[] {
				new Item(564), new Item(557, 2) }, 9335, 9237, 19), PEARL(22,
				24, new Item[] { new Item(564), new Item(556, 2) }, 880, 9238,
				29), EMERALD(32, 27, new Item[] { new Item(564),
				new Item(556, 3), new Item(561) }, 9338, 9241, 37), RED_TOPAZ(
				26, 29, new Item[] { new Item(564), new Item(554, 2) }, 9336,
				9239, 33), RUBY(35, 49, new Item[] { new Item(564),
				new Item(554, 5), new Item(565) }, 9339, 9242, 59), DIAMOND(38,
				57, new Item[] { new Item(564), new Item(557, 10),
						new Item(563, 2) }, 9340, 9243, 67), DRAGONSTONE(41,
				68, new Item[] { new Item(564), new Item(557, 15),
						new Item(566) }, 9341, 9244, 78), ONYX(44, 87,
				new Item[] { new Item(564), new Item(554, 20), new Item(560) },
				9342, 9245, 97);
		private final int componentId;
		private final int levelRequired;
		private final Item[] runes;
		private final int original;
		private final int product;
		private final double xp;

		Bolts(int componentId, int levelRequired, Item[] runes, int original,
				int product, double xp) {
			this.componentId = componentId;
			this.levelRequired = levelRequired;
			this.runes = runes;
			this.original = original;
			this.product = product;
			this.xp = xp;
		}

		public static Bolts forId(int id) {
			for (Bolts bolts : Bolts.values()) {
				if (bolts.getComponentId() == id)
					return bolts;
			}
			return null;
		}

		public int getComponentId() {
			return componentId;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item[] getRunes() {
			return runes;
		}

		public int getProduct() {
			return product;
		}

		public int getOriginal() {
			return original;
		}

		public double getXp() {
			return xp;
		}
	}

	public static void process(Player player, int button, int amount) {
		Bolts bolts = Bolts.forId(button);
		if (!player.getInventory().containsItem(bolts.getOriginal(), amount)) {
			player.getPackets().sendGameMessage(
					"You need " + amount + " "
							+ new Item(bolts.getOriginal()).getName()
							+ " to enchant!");
			return;
		}
		for (Item rune : bolts.getRunes()) {
			if (!player.getInventory().containsItem(rune.getId(),
					rune.getAmount() * (amount / 10))) {
				player.getPackets().sendGameMessage(
						"You need " + rune.getAmount() + " " + rune.getName()
								+ (rune.getAmount() > 1 ? "s" : "")
								+ " to enchant this bolt!");
				return;
			}
		}
		player.getInventory().deleteItem(bolts.getOriginal(), amount);
		player.getInventory().removeItems(bolts.getRunes());
		player.setNextAnimation(new Animation(4462));
		player.setNextGraphics(new Graphics(759));
		player.getInventory().addItem(bolts.getProduct(), amount);
		player.getSkills().addXp(Skills.MAGIC, amount);
		player.getPackets()
				.sendGameMessage(
						"The magic of the runes coaxes out the true nature of the gem tips.");
	}
}