package net.kagani.game.player.content;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.World;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.utils.Utils;

public class MysteryBox {

	/**
	 * @author: Dylan Page
	 */

	private static int COMMON[] = { 24144, 995, 4151, 4708, 4710, 4712, 4714,
			4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736,
			4738, 4740, 11732, 30372 };

	private static int UNCOMMON[] = { 24431, 26493, 28597, 33619, 33628, 33622,
			10330, 10332, 10334, 10336, 10338, 10340, 10342, 10344, 10346,
			10348, 10350, 10352, 21371, 15273, 386 };

	private static int RARE[] = { 23679, 23680, 23681, 23682, 23683, 23684,
			23685, 23686, 23687, 23688, 23689, 23690, 23691, 23692, 23693,
			23694, 23695, 23696, 23697, 23698, 23699, 23700, 30815, 30816,
			30817, 30818, 30819, 30820, 30821, 30822, 30823, 26384 };

	private static int VERY_RARE[] = { 1053, 1055, 1057, 1050, 30412, 21787,
			21790, 21793, 1959, 981, 1961, 1989, 19308, 19311, 19314, 19317,
			19320 };

	private static int SUPER_RARE[] = { 1419, 33625, 1038, 1040, 1042, 1044,
			1046, 1048, 962, 31725, 31729, 31733, 26384 };

	private static int CASKET[] = { 1419, 33625, 1038, 1040, 1042, 1044, 1046,
			1048, 962, 31725, 31729, 31733, 26384, 26322, 26323, 26324, 26334,
			26335, 26336, 26352, 26353, 26354, 24433 };

	private static long last = 0;

	private static int rewardId = 0;

	private static int amount = 1;

	private static String rate = "Common";

	public static void handleReward(Player player, Item item, int slotId) {
		if (System.currentTimeMillis() - last > 500
				&& player.getInventory().containsItem(item.getId(), 1)) {
			if (item.getId() == 26384
					&& player.getInventory().getFreeSlots() < 1) {
				player.getPackets().sendGameMessage(
						"Not enough space in your inventory.");
				return;
			}
			last = System.currentTimeMillis();
			amount = 1;
			player.getInventory().deleteItem(slotId, item);
			int r = Utils.random(100);
			if (item.getId() == 6199) {
				if (r <= 38) {
					rewardId = COMMON[(int) (Math.random() * COMMON.length)];
					rate = "Common";
				} else if (r >= 39 && r <= 57) {
					rewardId = UNCOMMON[(int) (Math.random() * UNCOMMON.length)];
					rate = "Uncommon";
				} else if (r >= 58 && r <= 83) {
					rewardId = RARE[(int) (Math.random() * RARE.length)];
					rate = "Rare";
				} else if (r >= 84 && r <= 96) {
					rewardId = VERY_RARE[(int) (Math.random() * VERY_RARE.length)];
					rate = "Very Rare";
				} else if (r >= 97 && r <= 100) {
					rewardId = SUPER_RARE[(int) (Math.random() * SUPER_RARE.length)];
					rate = "Super rare";
				}
			} else if (item.getId() == 26384) {
				if (r <= 28) {
					rewardId = COMMON[(int) (Math.random() * COMMON.length)];
					rate = "Common";
					amount = 2;
				} else if (r >= 29 && r <= 45) {
					rewardId = UNCOMMON[(int) (Math.random() * UNCOMMON.length)];
					rate = "Uncommon";
					amount = 2;
				} else if (r >= 46 && r <= 78) {
					rewardId = RARE[(int) (Math.random() * RARE.length)];
					rate = "Rare";
				} else if (r >= 79 && r <= 94) {
					rewardId = VERY_RARE[(int) (Math.random() * VERY_RARE.length)];
					rate = "Very Rare";
				} else if (r >= 95 && r <= 100) {
					rewardId = CASKET[(int) (Math.random() * CASKET.length)];
					rate = "Super rare";
				}
			}
			if (rewardId == 995) {
				amount = item.getId() == 26384 ? Utils.random(450000, 800000)
						: Utils.random(150000, 500000);
				player.getMoneyPouch().setAmount(amount, false);
			} else if (rewardId == 30372)
				player.getInventory().addItem(rewardId, Utils.random(20, 50));
			else if (rewardId == 25952) {
				player.getInventory().addItem(rewardId, 1);
				player.getInventory().addItemDrop(23695, 1, player);
			} else if (rewardId == 4740)
				player.getInventory().addItem(rewardId, Utils.random(50, 250));
			else if (rewardId == 15273 || rewardId == 386)
				player.getInventory().addItem(rewardId, Utils.random(50, 250));
			else
				player.getInventory().addItemDrop(rewardId, amount);
			player.getMoneyPouch().setAmount(
					item.getId() == 26384 ? Utils.random(250000, 800000)
							: Utils.random(100000, 200000), false);
			int price = GrandExchange.getPrice(rewardId) * amount;
			if (rate == "Super rare")
				World.sendNews(player, player.getDisplayName()
						+ " received a"
						+ (ItemDefinitions.getItemDefinitions(rewardId)
								.getName().startsWith("a") ? "n" : "")
						+ " "
						+ ItemDefinitions.getItemDefinitions(rewardId)
								.getName()
						+ " from a "
						+ ItemDefinitions.getItemDefinitions(item.getId())
								.getName().toLowerCase() + ".", 1);
			player.getDialogueManager().startDialogue(
					"SimpleItemMessage",
					rewardId == 15273 ? 15272 : rewardId == 386 ? 385
							: rewardId,
					"Item: "
							+ ItemDefinitions.getItemDefinitions(rewardId)
									.getName() + ".<br>Rarity: " + rate
							+ ".<br>Value: " + Utils.format(price)
							+ " gp<br>Chance: " + r + "/100.");
		}
	}
}