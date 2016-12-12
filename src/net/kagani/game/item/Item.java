package net.kagani.game.item;

import java.io.Serializable;

import net.kagani.cache.loaders.ItemDefinitions;

/**
 * Represents a single item.
 * <p/>
 * 
 * @author Graham / edited by Dragonkk(Alex)
 */
public class Item implements Serializable {

	private static final long serialVersionUID = -6485003878697568087L;

	private int id;
	protected int amount;

	public int getId() {
		return id;
	}

	@Override
	public Item clone() {
		return new Item(id, amount);
	}

	public Item(int id) {
		this(id, 1);
	}

	public Item(int id, int amount) {
		this(id, amount, -1);
	}

	public Item(int id, int amount, int degrade) {
		this(id, amount, degrade, false);
	}

	public Item(int id, int amount, int degrade, boolean amt0) {
		this.id = id;
		this.amount = amount;
		if (this.amount <= 0 && !amt0) {
			this.amount = 1;
		}
	}

	public Item(Item item) {
		this.id = item.getId();
		this.amount = item.getAmount();
	}

	public ItemDefinitions getDefinitions() {
		return ItemDefinitions.getItemDefinitions(id);
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmount() {
		return amount;
	}

	public String getName() {
		return getDefinitions().getName();
	}

	@Override
	public String toString() {
		return "Item (" + id + ", " + amount + ")";
	}
}