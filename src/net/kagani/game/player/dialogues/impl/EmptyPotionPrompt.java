package net.kagani.game.player.dialogues.impl;

import net.kagani.game.item.Item;
import net.kagani.game.player.content.Drinkables;
import net.kagani.game.player.content.Drinkables.Drink;
import net.kagani.game.player.dialogues.Dialogue;

public class EmptyPotionPrompt extends Dialogue {

	/**
	 * @author: Dylan Page
	 */

	private Item item;

	private String previousName;

	private int slotId;

	@Override
	public void start() {
		item = (Item) parameters[0];
		previousName = (String) parameters[1];
		slotId = (int) parameters[2];
		sendOptionsDialogue("Are you sure you want to empty this potion?",
				"Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			Drink pot = Drinkables.getDrink(item.getId());
			if (pot == null || pot.isFlask())
				return;
			item.setId(Drinkables.VIAL);
			player.getInventory().refresh(slotId);
			player.getPackets().sendGameMessage(
					"You empty the " + previousName + ".", true);
			end();
			break;
		case OPTION_2:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}