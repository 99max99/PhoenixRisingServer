package net.kagani.game.player.dialogues.impl.cities.taverly;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.utils.ShopsHandler;

public class PetShopOwner extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		this.npcId = (Integer) parameters[0];
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
				"Can I see your shop, please?",
				"How much is that puppy in the window?",
				"So, what sort of pets are available?",
				"Are you interested in buying spirit shards?");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				stage = 0;
				sendPlayerDialogue(NORMAL, "Can I see your shop please?");
				break;
			case OPTION_2:
				stage = 22;
				sendPlayerDialogue(NORMAL,
						"How much is that puppy in the window?");
				break;
			case OPTION_3:
				stage = 1;
				sendPlayerDialogue(NORMAL,
						"So, what short of pets are available?");
				break;
			default:
			case OPTION_4:
				stage = 20;
				sendPlayerDialogue(NORMAL,
						"Are you interested in buying spirit shards?");
				break;
			}
			break;
		case 0:
			end();
			openShop(player);
			break;
		case 1:
			stage = 2;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Well, here we sell dogs, but we also have supplies for any other creatures you might want to raise.");
			break;
		case 2:
			stage = 3;
			sendPlayerDialogue(NORMAL, "Such as?");
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Well, we sell nuts. Those can be used to feed squirrels. If you want to capture a squirrel, you'll need to use nuts on the trap you set, as the little scamps won't be fooled by anything else.");
			break;
		case 4:
			stage = 5;
			sendPlayerDialogue(NORMAL, "I'll bear that in mind.");
			break;
		case 5:
			stage = 6;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"There are also a number of birds that live in the woodlands of the world. If you can find their eggs then you can use the incubator over there to hatch it.");
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"So long as you are the first thing they see out of the shell, they will follow you anywhere.");
			break;
		case 7:
			stage = 8;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"After that, you just need to feed the chink ground fishing bait until its old enough to eat it solid.");
			break;
		case 8:
			stage = 9;
			sendPlayerDialogue(NORMAL,
					"I'll make sure to keep an eye on them if I go anywhere dangerous.");
			break;
		case 9:
			stage = 10;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"There are also number of fabulous and exotic lizards in the Karamja. Some can be caught easily in a box trap, while others will need to be raised from an egg.");
			break;
		case 10:
			stage = 11;
			sendPlayerDialogue(NORMAL, "Will the incubator work for them, too?");
			break;
		case 11:
			stage = 12;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Of course! I'll keep an eye on all the egs you put in there, so they never end up hard-boiled.");
			break;
		case 12:
			stage = 13;
			sendPlayerDialogue(NORMAL, "Thank goodness!");
			break;
		case 13:
			stage = 14;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"The geckos of Karamja are quite easy to trap, like raccoons. Both will investigate a trap happily without any special bait.");
			break;
		case 14:
			stage = 15;
			sendNPCDialogue(npcId, NORMAL,
					"Monkeys are a different story, however!");
			break;
		case 15:
			stage = 16;
			sendPlayerDialogue(NORMAL, "What do you mean?");
			break;
		case 16:
			stage = 17;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Well, they are clever little things and can easily get out of a box trap, unless they are stuck. The easiest way to do that is to put a banana in the workings.");
			break;
		case 17:
			stage = 18;
			sendNPCDialogue(npcId, NORMAL,
					"They will hang on tight, and never let go, even when the trap closes!");
			break;
		case 18:
			stage = 19;
			sendPlayerDialogue(NORMAL,
					"Thanks a lot, you've been very helpful!");
			break;
		case 19:
			stage = -2;
			sendNPCDialogue(npcId, NORMAL,
					"It's always a please to help a fellow animal-lover. Come back and visit soon.");
			break;
		case 20:
			stage = 21;
			sendNPCDialogue(npcId, NORMAL, "I certainly am. Lots of them, too!");
			break;
		case 21:
			stage = -2;
			sendPlayerDialogue(NORMAL, "Thanks, I'll bear that in mind.");
			break;
		case 22:
			stage = 23;
			sendNPCDialogue(npcId, NORMAL, "The one with the waggly tail?");
			break;
		case 23:
			stage = 24;
			player.getInterfaceManager().sendDialogueInterface(668);
			break;
		case 24:
			if (componentId >= 3 && componentId <= 8) {
				selectedDog = DOGS[componentId - 3];
				sendPlayerDialogue(NORMAL, "No, the " + getPetName() + ".");
				stage = 25;
			} else
				end();
			break;
		case 25:
			stage = 26;
			sendNPCDialogue(npcId, NORMAL, "500 gold.");
			break;
		case 26:
			stage = 27;
			sendPlayerDialogue(NORMAL, "Isn't that a little step?");
			break;
		case 27:
			stage = 28;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"Well, if we gave them away for free then people would just buy them and dump them without a care.");
			break;
		case 28:
			stage = 29;
			sendNPCDialogue(npcId, NORMAL,
					"Dogs are a big responsability and you should be cared for.");
			break;
		case 29:
			stage = 30;
			sendNPCDialogue(
					npcId,
					NORMAL,
					"If a personis unwilling to invest 500 coins, then they don't deserve to have the puppy in the first place.");
			break;
		case 30:
			stage = 31;
			sendNPCDialogue(npcId, NORMAL, "So, do you still want one?");
			break;
		case 31:
			stage = 32;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Okay, I'll take the "
					+ getPetName() + ".", "No thanks.");
			break;
		case 32:
			switch (componentId) {
			case OPTION_1:
				stage = 33;
				sendPlayerDialogue(NORMAL, "Okay, I'll take the "
						+ getPetName() + ".");
				break;
			case OPTION_2:
			default:
				stage = -2;
				sendPlayerDialogue(NORMAL, "No thanks.");
				break;
			}
			break;
		case 33:
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
			player.getInventory().addItem(selectedDog, 1);
			break;
		case -2:
			end();
			break;
		}

	}

	private String getPetName() {
		return ItemDefinitions.getItemDefinitions(selectedDog).getName()
				.toLowerCase().replace(" puppy", "");
	}

	private int selectedDog;

	private static final int[] DOGS = new int[] { 12522, 12518, 12514, 12512,
			12520, 12516 };

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
