package net.kagani.game.player.actions.sawmill;

import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.controllers.SawmillController;

public class PlanksTake extends Action {

	private SawmillController sawmill;
	private int amount;

	public PlanksTake(int amount, SawmillController sawmill) {
		this.amount = amount;
		this.sawmill = sawmill;
	}

	@Override
	public boolean start(Player player) {
		return process(player);
	}

	@Override
	public boolean process(Player player) {
		if (!player.getInventory().hasFreeSlots()) {
			player.getPackets().sendGameMessage("Your inventory is full.");
			return false;
		}
		if (!sawmill.hasPlanks()) {
			player.getPackets().sendGameMessage("You have no planks left.");
			return false;
		}
		return amount > 0;
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(new Animation(8908));
		player.getInventory().addItem(new Item(960));
		sawmill.removePlank();
		return amount-- == 1 ? -1 : 1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}