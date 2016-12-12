package net.kagani.game.player.content.lending;

import java.io.Serializable;

import net.kagani.game.item.Item;

public class Lend implements Serializable {

	private static final long serialVersionUID = -1131979864451286325L;
	private String lender;
	private String lendee;
	private Item item;
	private long timeTill;

	public Lend(String lender, String lendee, Item item, long timeTill) {
		this.lender = lender;
		this.lendee = lendee;
		this.item = item;
		this.timeTill = timeTill;
	}

	public String getLender() {
		return lender;
	}

	public String getLendee() {
		return lendee;
	}

	public Item getItem() {
		return item;
	}

	public long getTime() {
		return timeTill;
	}

}