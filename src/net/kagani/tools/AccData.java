package net.kagani.tools;

import java.io.Serializable;

import net.kagani.game.item.Item;
import net.kagani.game.player.GrandExchangeManager;

public class AccData implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 4697826089433977340L;
	public String username;
	public String password;

	public int pouch;

	public Item[] bank;
	public Item[] inventory;
	public Item[] equipment;

	public double[] exp;
	public GrandExchangeManager ge;
}