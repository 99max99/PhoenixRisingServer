package net.kagani.game.player.dialogues.impl;

import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.content.ItemConstants;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.controllers.Wilderness;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class HighValueItemOption extends Dialogue {

	public Item itemName;

	@Override
	public void start() {
		itemName = (Item) parameters[0];
		sendOptionsDialogue(
				"Drop "
						+ itemName.getName()
						+ " (worth:"
						+ Utils.format(GrandExchange.getPrice(itemName.getId()))
						+ " gp)?", "Yes, drop it!", "No, don't!");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1) {
			player.getInventory().deleteItem(itemName);
			if (player.getCharges().degradeCompletly(itemName))
				return;
			if (player.getRights() >= 2) {
				end();
				return;
			}
			if (player.isBeginningAccount()) {
				World.addGroundItem(itemName, new WorldTile(player), player,
						true, 60, 2, 0);
			} else if (player.getControlerManager().getControler() instanceof Wilderness
					&& ItemConstants.isTradeable(itemName))
				World.addGroundItem(itemName, new WorldTile(player), player,
						false, -1);
			else
				World.addGroundItem(itemName, new WorldTile(player), player,
						true, 60);
			Logger.globalLog(player.getUsername(), player.getSession().getIP(),
					new String(" has dropped item [ id: " + itemName.getId()
							+ ", amount: " + itemName.getAmount() + " ]."));
			end();
		} else if (componentId == OPTION_2) {
			end();
		}
	}

	@Override
	public void finish() {

	}
}