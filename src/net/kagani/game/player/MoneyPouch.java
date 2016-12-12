package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.game.item.Item;
import net.kagani.game.player.controllers.DungeonController;
import net.kagani.utils.Utils;

public class MoneyPouch implements Serializable {

	private static final long serialVersionUID = -3847090682601697992L;

	private transient Player player;
	private boolean usingPouch;
	private int coinAmount;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void switchPouch() {
		usingPouch = !usingPouch;
	}

	public void init() {
		/*
		 * if (usingPouch) swap();
		 */
		refreshCoins();
	}

	public void examinePouch() {
		player.getPackets().sendGameMessage(
				"Your money pouch currently contains "
						+ Utils.getFormattedNumber(coinAmount) + " coins.");
	}

	public int getTotalAmount() {
		return coinAmount;
	}

	public void withdrawPouch() {
		if (player.getControlerManager().getControler() instanceof DungeonController) {
			player.getPackets()
					.sendGameMessage(
							"You cannot access your money pouch within the walls of Daemonheim.");
			return;
		}
		if (!player.getBank().hasVerified(12))
			return;
		player.getPackets().sendInputIntegerScript(
				"Your money pouch contains "
						+ Utils.getFormattedNumber(coinAmount)
						+ " coins.<br>How many would you like to withdraw?");
		player.getTemporaryAttributtes().put("withdrawingPouch", Boolean.TRUE);
	}

	public void refreshCoins() {
		player.getPackets().sendExecuteScript(5559, coinAmount);
		player.getPackets().sendItems(623,
				new Item[] { new Item(995, coinAmount, 0, true) });
	}

	public boolean sendDynamicInteraction(int amount, boolean remove) {
		return sendDynamicInteraction(amount, remove, TYPE_INV);
	}

	public static final int TYPE_POUCH_INVENTORY = 0, TYPE_REMOVE = 1,
			TYPE_INV = 2;

	/*
	 * TYPE_POUCH_INVENTORY - from pouch to inventory TYPE_REMOVE - remove from
	 * pouch as much as it can(example bank) TYPE_INV - remove/add from pouch
	 * and if not enough, inventory
	 */
	public boolean sendDynamicInteraction(int amount, boolean remove, int type) {
		if (amount == 0)
			return false;
		if (remove) {
			if (type == TYPE_POUCH_INVENTORY) {
				if (amount > coinAmount)
					amount = coinAmount;
				int invAmt = player.getInventory().getAmountOf(995);
				if (coinAmount != 0 && invAmt + amount <= 0) {
					amount = Integer.MAX_VALUE - invAmt;
					player.getPackets().sendGameMessage(
							"Not enough space in your inventory.");
				}
			} else if (type == TYPE_INV && amount > coinAmount) {
				int removeAmt = amount - coinAmount;
				if (player.getInventory().getAmountOf(995) < removeAmt)
					return false;
				player.getInventory().deleteItem(995, removeAmt);
				amount -= removeAmt;
			}
		} else if (!remove && amount + coinAmount <= 0) {
			if (type == TYPE_INV) // added from somewhere else example shop but
				// moneypouch full so adds to inv
				player.getInventory().addItem(995,
						amount - (Integer.MAX_VALUE - coinAmount));
			else
				player.getPackets()
						.sendGameMessage(
								"Your money-pouch is currently full. Your coins will now go to your inventory.");
			amount = Integer.MAX_VALUE - coinAmount;
		}
		if (amount == 0)
			return true;
		player.getPackets().sendGameMessage(
				Utils.getFormattedNumber(amount) + " coins have been "
						+ (remove ? "removed" : "added")
						+ " to your money pouch.");
		if (type == TYPE_POUCH_INVENTORY) {
			if (remove) {
				if (!player.getInventory().addItem(995, amount))
					return false;
			} else
				player.getInventory().deleteItem(995, amount);
		}
		setAmount(amount, remove);
		return true;
	}

	public void setAmount(int amt, boolean remove) {
		if (remove)
			coinAmount -= amt;
		else
			coinAmount += amt;
		player.getPackets().sendExecuteScriptReverse(5561, remove ? 0 : 1, amt);
		refreshCoins();
	}

	public int getCoinsAmount() {
		return coinAmount;
	}

	public void setCoinsAmount(int amt) {
		coinAmount = amt;
	}

	public boolean contains(int amount) {
		if (coinAmount >= amount)
			return true;
		return false;
	}

	public boolean contains(double amount) {
		if (coinAmount >= amount)
			return true;
		return false;
	}
}