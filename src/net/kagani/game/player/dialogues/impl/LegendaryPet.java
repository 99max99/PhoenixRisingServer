package net.kagani.game.player.dialogues.impl;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.item.Item;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

/**
 * 
 * @author Kyle Proctor
 * @contact<skype;SaviourZz><email;kylejohnproctor@gmail.com>
 */

public class LegendaryPet extends Dialogue {

	public Item item;

	@Override
	public void start() {
		item = (Item) parameters[0];
		options("What would you like to do with " + item.getName().toLowerCase() + "?", "Alch The Item",
				"Note The Item");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			int itemId = (item.getDefinitions().isNoted() ? (item.getId() - 1) : item.getId());
			int alchPrice = GrandExchange.getPrice(itemId);
			alchPrice = (alchPrice / (int) 2);
			alchPrice = (alchPrice * player.getInventory().getAmountOf(item.getId()));
			if (alchPrice == 0)
				return;
			player.getInventory().addItem(995, alchPrice);
			player.getInventory().deleteItem(item.getId(), player.getInventory().getAmountOf(item.getId()));
			player.getPackets().sendGameMessage("<col=E86100>" + player.getPet().getName() + " has alched "
					+ item.getName().toLowerCase() + " for " + Utils.formatNumber(alchPrice) + "gp.");
		} else {
			int notedItem = item.getId() + 1;
			ItemDefinitions defs = new ItemDefinitions(notedItem);
			if (defs.isNoted()) {
				for (int i = 0; i < player.getInventory().getItemsContainerSize(); i++) {
					if (!player.getInventory().containsOneItem(item.getId()))
						continue;
					player.getInventory().deleteItem(item.getId(), item.getAmount());
					player.getInventory().addItem(notedItem, i);
				}
				player.getPackets().sendGameMessage("<col=E86100>" + player.getPet().getName() + " notes the "
						+ item.getName().toLowerCase() + "...");
			} else
				player.getPackets().sendGameMessage("You are unable to note this item.");
		}
		finish();
	}

	@Override
	public void finish() {

	}
}