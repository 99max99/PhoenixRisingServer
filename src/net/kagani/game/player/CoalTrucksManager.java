package net.kagani.game.player;

import java.io.Serializable;

public class CoalTrucksManager implements Serializable {

	private static final long serialVersionUID = 20296244197867311L;

	private transient Player player;

	private int coal;

	private static final int MAX_COAL = 196;
	public static final int COAL = 453;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		refreshCoalTrucks();
	}

	public void refreshCoalTrucks() {
		player.getVarsManager().sendVar(74, coal);
	}

	public void investigate() {
		player.getPackets().sendGameMessage(
				"There are currently " + coal + " coals in the truck.");
	}

	public void removeCoal() {
		if (coal == 0)
			return;
		int slots = player.getInventory().getFreeSlots();
		if (slots == 0) {
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return;
		}
		if (coal < slots)
			slots = coal;
		player.getInventory().addItem(COAL, slots);
		coal -= slots;
		refreshCoalTrucks();
		player.getPackets().sendGameMessage(
				"You remove some of the coal from the truck.", true);
	}

	public void addCoal() {
		if (coal >= MAX_COAL) {
			player.getPackets().sendGameMessage("The coal truck is full.");
			return;
		}
		int addCoal = player.getInventory().getAmountOf(COAL);
		if (coal + addCoal >= MAX_COAL)
			addCoal = MAX_COAL - coal;
		player.getInventory().deleteItem(COAL, addCoal);
		coal += addCoal;
		refreshCoalTrucks();
		player.getPackets().sendGameMessage("You put the coal in the truck.",
				true);
	}
}