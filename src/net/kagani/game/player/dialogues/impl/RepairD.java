package net.kagani.game.player.dialogues.impl;

import net.kagani.game.item.Item;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class RepairD extends Dialogue {

	private int[] prices;
	private int slot;
	private int newId;
	private boolean dungeoneering;

	@Override
	public void start() {
		slot = (int) this.parameters[0];
		prices = (int[]) this.parameters[1];
		newId = (int) this.parameters[2];
		dungeoneering = (boolean) this.parameters[3];

		if (dungeoneering) {
			sendOptionsDialogue("Would you like to repair item?", "Yes. Pay "
					+ Utils.getFormattedNumber(prices[0]) + " coins.",
					"Yes. Pay " + Utils.getFormattedNumber(prices[1])
							+ " tokens.", "No way.");
		} else
			sendOptionsDialogue(
					"Would you like to pay "
							+ Utils.getFormattedNumber(prices[0])
							+ " coins to repair item?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			if (player.getInventory().getCoinsAmount() >= prices[0])
				player.getInventory().removeItemMoneyPouch(
						new Item(995, prices[0]));
			else {
				sendDialogue("You don't have enough coins, you need "
						+ Utils.getFormattedNumber(prices[0])
						+ " coins to repair this item.");
				return;
			}
			repairItem();
		} else if (dungeoneering && componentId == OPTION_2) {
			if (player.getDungManager().getTokens() >= prices[1])
				player.getDungManager().addTokens(-prices[1]);
			else {
				sendDialogue("You don't have enough tokens, you need "
						+ Utils.getFormattedNumber(prices[1])
						+ " tokens to repair this item.");
				return;
			}
			repairItem();
		}
		end();
	}

	private void repairItem() {
		Item item = player.getInventory().getItem(slot);
		if (item.getId() == newId)
			player.getCharges().resetCharges(newId);
		else {
			item.setId(newId);
			player.getInventory().refresh(slot);
		}
	}

	@Override
	public void finish() {

	}
}