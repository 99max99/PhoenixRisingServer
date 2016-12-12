package net.kagani.game.player.dialogues.impl;

import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class Gertrude extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		this.npcId = (Integer) parameters[0];
		sendPlayerDialogue(NORMAL, "Hello again.");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 8;
			sendNPCDialogue(npcId, NORMAL, "Hello, my dear. How are things?");
			break;
		case 8:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "I'm fine thanks.",
					"Do you have any kittens?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(NORMAL, "I'm fine thanks.");
				break;
			default:
			case OPTION_2:
				stage = 2;
				sendPlayerDialogue(NORMAL, "Do you have any kittens?");
				break;
			}
			break;
		case 1:
			stage = -2;
			sendNPCDialogue(npcId, NORMAL, "Good good. See you again.");
			break;
		case 2:
			stage = 3;
			player.getInterfaceManager().sendDialogueInterface(737);
			break;
		case 3:
			if (componentId >= 3 && componentId <= 8) {
				selectedCat = 1555 + (componentId - 3);
				sendPlayerDialogue(NORMAL, "This one please.");
				stage = 4;
			} else
				end();
			break;
		case 4:
			stage = 5;
			sendNPCDialogue(npcId, NORMAL, "500 gold.");
			break;
		case 5:
			stage = 6;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"Okay, I'll take the cat.", "No thanks.");
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				stage = 7;
				sendPlayerDialogue(NORMAL, "Okay, I'll take the cat.");
				break;
			case OPTION_2:
			default:
				stage = -2;
				sendPlayerDialogue(NORMAL, "No thanks.");
				break;
			}
			break;
		case 7:
			if (player.getInventory().getCoinsAmount() < 500) {
				end();
				player.getPackets().sendGameMessage(
						"You don't have enough coins.");
				return;
			}
			if (!player.getInventory().hasFreeSlots()) {
				end();
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				return;
			}
			stage = -2;
			sendNPCDialogue(npcId, NORMAL,
					"There you go! I hope you two get on.");
			player.getInventory().removeItemMoneyPouch(new Item(995, 500));
			player.getInventory().addItem(selectedCat, 1);
			break;
		case -2:
			end();
			break;
		}

	}

	private int selectedCat;

	public static void sellShards(Player player) {
		int shardsCount = player.getInventory().getAmountOf(12183);
		if (shardsCount == 0) {
			player.getPackets().sendGameMessage(
					"You do not have any spirit shards.");
			return;
		}
		player.getPackets().sendInputIntegerScript(
				"How many will you sell? (25 each, you have " + shardsCount
						+ ")");
		player.getTemporaryAttributtes().put(Key.SELL_SPIRIT_SHARDS,
				Boolean.TRUE);
	}

	public static void sellShards(Player player, int quantity) {
		int shardsCount = player.getInventory().getAmountOf(12183);
		if (quantity > shardsCount) {
			quantity = shardsCount;
			player.getPackets().sendGameMessage(
					"You do not have that many spirit shards.");
		}
		player.getInventory().deleteItem(new Item(12183, quantity));
		int money = quantity * 25;
		player.getInventory().addItemMoneyPouch(new Item(995, money));
		player.getPackets().sendGameMessage(
				"You sell " + quantity + " spirit shard for " + money
						+ " coins.");

	}

	public static void openShop(Player player) {
		ShopsHandler.openShop(player, 57);
	}

	@Override
	public void finish() {

	}

}
