package net.kagani.game.player.content.treasurehunter;

import java.io.Serializable;

import net.kagani.game.item.Item;

public final class Prize implements Serializable {

	/**
	 * @author: Emperor
	 * @author: Dylan Page
	 */

	private static final long serialVersionUID = -7917895406908920300L;

	private final Item item;

	private final PrizeCategory category;

	private final Rarity rarity;

	private final int cashoutValue;

	public Prize(Item item, Rarity rarity, PrizeCategory category,
			int cashoutValue) {
		this.item = item;
		this.rarity = rarity;
		this.category = category;
		this.cashoutValue = cashoutValue;
	}

	public Item getItem() {
		return item;
	}

	public PrizeCategory getCategory() {
		return category;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public int getCashoutValue() {
		return cashoutValue;
	}
}