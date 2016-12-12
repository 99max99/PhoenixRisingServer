package net.kagani.game.player.dialogues.impl.skills;

import net.kagani.game.item.Item;
import net.kagani.game.player.actions.Herblore;
import net.kagani.game.player.content.SkillsDialogueOld;
import net.kagani.game.player.dialogues.Dialogue;

public class HerbloreD extends Dialogue {

	private int items;
	private Item first;
	private Item second;

	@Override
	public void start() {
		items = (Integer) parameters[0];
		first = (Item) parameters[1];
		second = (Item) parameters[2];
		int amount;
		if (first.getId() == Herblore.PESTLE_AND_MORTAR)
			amount = player.getInventory().getItems()
					.getNumberOf(second.getId());
		else if (second.getId() == Herblore.PESTLE_AND_MORTAR)
			amount = player.getInventory().getItems()
					.getNumberOf(first.getId());
		else {
			amount = player.getInventory().getItems()
					.getNumberOf(first.getId());
			if (amount > player.getInventory().getItems()
					.getNumberOf(second.getId()))
				amount = player.getInventory().getItems()
						.getNumberOf(second.getId());
		}
		SkillsDialogueOld
				.sendSkillsDialogue(
						player,
						SkillsDialogueOld.MAKE,
						"Choose how many you wish to make,<br>then click on the item to begin.",
						amount, new int[] { items }, null);

	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(
				new Herblore(first, second, SkillsDialogueOld
						.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {
	}
}
