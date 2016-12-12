package net.kagani.game.player.content.treasurehunter;

import java.io.Serializable;
import java.util.Random;

import net.kagani.game.item.Item;

public enum Rewards implements Serializable {

	/**
	 * @author: Dylan Page
	 */

	ITEM(new Item(23717), Rarity.COMMON, PrizeCategory.ATTACK_XP, 1000),

	ITEM2(new Item(23721), Rarity.COMMON, PrizeCategory.STRENGTH_XP, 1000),

	ITEM3(new Item(23725), Rarity.COMMON, PrizeCategory.DEFENCE_XP, 1000),

	;

	final Item item;

	final Rarity rarity;

	final PrizeCategory category;

	final int cashoutValue;

	Rewards(Item item, Rarity rarity, PrizeCategory category, int cashoutValue) {
		this.item = item;
		this.rarity = rarity;
		this.category = category;
		this.cashoutValue = cashoutValue;
	}

	public Item getItem() {
		return item;
	}

	public Rarity getRarity() {
		return rarity;
	}

	public PrizeCategory getCategory() {
		return category;
	}

	public int getCashoutValue() {
		return cashoutValue;
	}

	public static Rewards getCurrent() {
		// TODO
		return null;
	}

	public static Rewards generateReward() {
		int pick = new Random().nextInt(Rewards.values().length - 1);
		return Rewards.values()[pick];
	}
}
