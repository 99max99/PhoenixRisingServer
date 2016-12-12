package net.kagani.game.player.dialogues.impl;

import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.Utils;

public class ChristmasCrackerD extends Dialogue {
	private Player usedOn;

	final static Item[] PARTYHATS = { new Item(1038, 1), new Item(1040, 1),
			new Item(1042, 1), new Item(1044, 1), new Item(1046, 1),
			new Item(1048, 1) };

	final static Item[] EXTRA_ITEMS = { new Item(1969, 1),
			new Item(2355, Utils.random(1, 2)), new Item(1217, 1),
			new Item(1635, 1), new Item(441, 5), new Item(441, 10),
			new Item(1973, 1), new Item(1718, 1), new Item(950, 1),
			new Item(563, 1), new Item(1987, 1) };

	static Item getExtraItems() {
		return EXTRA_ITEMS[(int) (Math.random() * EXTRA_ITEMS.length)];
	}

	static Item getPartyhats() {
		return PARTYHATS[(int) (Math.random() * PARTYHATS.length)];
	}

	@Override
	public void finish() {
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			if (player.getInventory().containsItem(962, 1)) {
				if (usedOn == null) {
					player.getPackets().sendGameMessage(
							usedOn.getDisplayName() + " is offline.");
					return;
				}
				player.getPackets().sendGameMessage(
						"You pull a Christmas cracker...", true);
				player.getInventory().deleteItem(962, 1);
				usedOn.faceEntity(player);
				player.setNextAnimation(new Animation(15153));
				usedOn.setNextAnimation(new Animation(15153));
				if (Utils.random(100) <= 50) {
					Item phat = getPartyhats();
					World.sendIgnoreableWorldMessage(player,
							"<col=00FF00><img=5>" + player.getDisplayName()
									+ " received the a " + phat.getName()
									+ " from the Christmas cracker!", false);
					player.getInventory().addItem(phat);
					player.getInventory().addItem(getExtraItems());
				} else {
					Item phat = getPartyhats();
					World.sendIgnoreableWorldMessage(usedOn,
							"<col=00FF00><img=5>" + usedOn.getDisplayName()
									+ " received the a " + phat.getName()
									+ " from the Christmas cracker!", false);
					usedOn.getInventory().addItem(phat);
					usedOn.getInventory().addItem(getExtraItems());
				}
			}
			end();
			break;
		default:
			end();
			break;
		}
	}

	@Override
	public void start() {
		usedOn = (Player) parameters[0];
		sendOptionsDialogue("Are you sure you want to pull the cracker?",
				"Yes, poll the cracker!", "Stop!");
	}
}