package net.kagani.game.player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.World;
import net.kagani.game.item.Item;
import net.kagani.game.player.content.treasurehunter.Prize;
import net.kagani.game.player.content.treasurehunter.PrizeCategory;
import net.kagani.game.player.content.treasurehunter.Rarity;
import net.kagani.game.player.content.treasurehunter.Rewards;
import net.kagani.utils.Color;
import net.kagani.utils.ItemExamines;
import net.kagani.utils.Utils;

public class SquealOfFortune implements Serializable {

	/**
	 * @author: Dylan Page
	 */

	private static final long serialVersionUID = -5330047553089876572L;

	private transient Player player;

	private static List<Integer> receivedItems = new ArrayList<Integer>();
	private static List<int[]> veryRareItems = new ArrayList<int[]>();

	public Prize reward;

	private Item[] rewards;

	public Rewards itemRewards;

	public int daily = 0, earned = 1, bought = 2;

	private int dailyKeys, earnedKeys, boughtKeys;

	private int frozenHearts = 100;

	private long delay;

	private boolean claimed = false;

	private int rarityType;

	private static final int COMMON = 0, UNCOMMON = 1, RARE = 2, VERY_RARE = 3;

	public static final double[] CHANCES = new double[] { 1.0D, 0.43D, 0.01D,
			0.005D };

	public static final int[] COMMON_COINS_AMOUNT = new int[] { 100, 250, 500,
			1000 };

	public static final int[] UNCOMMON_COINS_AMOUNT = new int[] { 2000, 5000,
			7500, 10000 };

	public static final int[] RARE_COINS_AMOUNT = new int[] { 100000, 250000,
			500000, 1000000 };

	public static final int[] VERY_RARE_COINS_AMOUNT = new int[] { 10 * 100000,
			5 * 100000, 666, 1337 };

	public static final int[] COMMON_LAMPS = new int[] { 23713, 23717, 23721,
			23725, 23729, 23737, 23733, 23741, 23745, 23749, 23753, 23757,
			23761, 23765, 23769, 23778, 23774, 23786, 23782, 23794, 23790,
			23802, 23798, 23810, 23806, 23814, 29545 };

	public static final int[] UNCOMMON_LAMPS = new int[] { 23714, 23718, 23722,
			23726, 23730, 23738, 23734, 23742, 23746, 23750, 23754, 23758,
			23762, 23766, 23770, 23779, 23775, 23787, 23783, 23795, 23791,
			23803, 23799, 23811, 23807, 23815, 29546 };

	public static final int[] RARE_LAMPS = new int[] { 23715, 23719, 23723,
			23727, 23731, 23739, 23735, 23743, 23747, 23751, 23755, 23759,
			23763, 23767, 23771, 23780, 23776, 23788, 23784, 23796, 23792,
			23804, 23800, 23812, 23808, 23816, 29547 };

	public static final int[] VERY_RARE_LAMPS = new int[] { 23716, 23720,
			23724, 23728, 23732, 23740, 23736, 23744, 23748, 23752, 23756,
			23760, 23764, 23768, 23773, 23781, 23777, 23789, 23785, 23797,
			23793, 23805, 23801, 23813, 23809, 23817, 29548 };

	public static final int[] COMMON_OTHERS = new int[] { 27153, 15273, 374,
			7947, 8783, 961, 8781, 15271, 13436, 25550, 1748, 1752, 2358, 2,
			1618, 1514, 1778, 556, 561, 565, 537, 535, 24336, 454, 445, 450,
			448, 437, 452, 2364 };

	public static final int[] UNCOMMON_OTHERS = new int[] { 27154, 30372,
			31167, 27234, 27235, 27236, 27620, 31091, 31770, 31771, 31772 };

	public static final int[] RARE_OTHERS = new int[] { 27155, 27148, 27149,
			27150, 27151, 27152, 27626, 27627, 27628, 27629, 27630, 28145,
			25186, 25187, 25188, 25189, 25195, 25196, 25197, 25198, 25199,
			25190, 25191, 25192, 25193, 25194, 29865, 29866, 29867, 29868,
			29869, 25180, 25181, 25182, 25183, 25184, 27587, 27588, 27589,
			27590, 27591, 28995, 28996, 28997, 28998, 28999, 31344, 31345,
			31346, 31347, 30920, 28483, 28488, 28493, 28498, 28503, 27618,
			27622, 27624, 28688, 28690, 28692, 28694, 29972, 29973, 28508,
			27616, 28686, 25205, 31041, 31042, 31043, 31044 };

	public static final int[] VERY_RARE_OTHERS = new int[] { 23679, 23680,
			23681, 23682, 23683, 23684, 23685, 23686, 23687, 23688, 23689,
			23690, 23691, 23692, 23693, 23694, 23695, 23696, 23697, 23698,
			23699, 23700, 30815, 30816, 30817, 30818, 30819, 30820, 30821,
			30822, 30823, 6199, 26384, 30750, 30755, 30760, 30761, 23674,
			20929, 24433, 33634, 33296 };

	public SquealOfFortune() {
		delay = Utils.currentTimeMillis();
		dailyKeys = 0;
		earnedKeys = 0;
		boughtKeys = 0;
	}

	public void setPlayer(Player player) {
		try {
			this.player = player;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void processClick(int packetId, int interfaceId, int componentId,
			int slotId, int slotId2) {
		switch (interfaceId) {
		case 1139:
			switch (componentId) {
			case 12:
				if (player.getInterfaceManager()
						.containsTreasureHunterInterface()) {
					player.getPackets()
							.sendGameMessage(
									"Please finish what you are doing before opening Treasure Hunter.");
					return;
				}
				openSpinInterface(true, false, true);
				player.getPackets().sendGameMessage(
						"You can spam click 'I' to skip animations.");
				break;
			default:
				player.getDialogueManager().startDialogue("OpenURLPrompt",
						"store");
				break;
			}
			break;
		case 1252:
			switch (componentId) {
			case 5:
				player.getInterfaceManager().closeTreasureHunter();
				break;
			default:
				if (player.getInterfaceManager()
						.containsTreasureHunterInterface()) {
					player.getPackets()
							.sendGameMessage(
									"Please finish what you are doing before opening Treasure Hunter.");
					return;
				}
				openSpinInterface(false, false, true);
				player.getPackets().sendGameMessage(
						"You can spam click 'I' to skip animations.");
				break;
			}
			break;
		case 1253:
			switch (componentId) {
			case 35:
				player.getDialogueManager().startDialogue("Alice");
				break;
			case 346:
				player.getVarsManager().setVarBit(1450, -1140842495);
				player.getPackets().sendCSVarInteger(4081, -1);
				player.getPackets().sendCSVarInteger(2045, 1);
				player.getPackets().sendCSVarInteger(1784, 0);
				break;
			case 542:
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getVarsManager().setVarBit(4146, 0);
				player.getPackets().sendHideIComponent(1253, 244, true);
				player.getPackets().sendHideIComponent(1253, 234, false);
				player.getPackets().sendHideIComponent(1253, 552, true);
				player.getPackets().sendCSVarInteger(4082, 133);
				player.getPackets().sendHideIComponent(1253, 552, false);
				player.getPackets().sendCSVarInteger(1993, 1);
				break;
			case 385:
				if (getAllKeys() < 1) {
					player.getPackets().sendGameMessage(
							"You do not have enough keys.");
					reset();
					return;
				}
				selectReward(0);
				break;
			case 387:
			case 420:
				if (getAllKeys() < 1) {
					player.getPackets().sendGameMessage(
							"You do not have enough keys.");
					reset();
					return;
				}
				selectReward(1);
				break;
			case 379:
				if (getAllKeys() < 1) {
					player.getPackets().sendGameMessage(
							"You do not have enough keys.");
					reset();
					return;
				}
				selectReward(2);
				break;
			case 381:
				if (getAllKeys() < 1) {
					player.getPackets().sendGameMessage(
							"You do not have enough keys.");
					reset();
					return;
				}
				selectReward(3);
				break;
			case 383:
				if (getAllKeys() < 1) {
					player.getPackets().sendGameMessage(
							"You do not have enough keys.");
					reset();
					return;
				}
				selectReward(4);
				break;
			case 440:
			case 659:
				player.getDialogueManager().startDialogue("OpenURLPrompt",
						"store");
				break;
			case 455:
			case 447:
				if (!claimed) {
					Item item = rewards[0];
					String name = item.getDefinitions().getName().toLowerCase();
					if (!receivedItems.equals(item.getId())) {
						if (name.contains("warlord") || name.contains("mask")
								|| name.contains("archon")
								|| name.contains("ramokee")
								|| name.contains("artisan")
								|| name.contains("blacksmith")
								|| name.contains("botanist")
								|| name.contains("diviner")
								|| name.contains("sous chef")
								|| name.contains("first age")
								|| name.contains("shaman")
								|| name.contains("farmer")
								|| name.contains("skirmisher"))
							receivedItems.add(item.getId());
					}
					if (!player.getInventory().hasFreeSlots()) {
						if (rarityType == VERY_RARE)
							announceWinner(item.getId(), item.getAmount());
						player.getBank().addItem(item.getId(),
								item.getAmount(), true);
						player.getPackets().sendGameMessage(
								"Your prize has been placed in your bank.");
						try {
							DateFormat dateFormat2 = new SimpleDateFormat(
									"MM/dd/yy HH:mm:ss");
							Calendar cal2 = Calendar.getInstance();
							final String FILE_PATH = "data/logs/treasurehunter/";
							BufferedWriter writer = new BufferedWriter(
									new FileWriter(FILE_PATH
											+ player.getUsername() + ".txt",
											true));
							writer.write("["
									+ dateFormat2.format(cal2.getTime())
									+ ", IP: " + player.getSession().getIP()
									+ ", bank] got " + item.getName() + " ("
									+ item.getId() + ") amount: "
									+ item.getAmount());
							writer.newLine();
							writer.flush();
							writer.close();
						} catch (IOException er) {
							er.printStackTrace();
						}
					} else {
						if (rarityType == VERY_RARE)
							announceWinner(item.getId(), item.getAmount());
						player.getInventory().addItemDrop(item.getId(),
								item.getAmount());
						try {
							DateFormat dateFormat2 = new SimpleDateFormat(
									"MM/dd/yy HH:mm:ss");
							Calendar cal2 = Calendar.getInstance();
							final String FILE_PATH = "data/logs/treasurehunter/";
							BufferedWriter writer = new BufferedWriter(
									new FileWriter(FILE_PATH
											+ player.getUsername() + ".txt",
											true));
							writer.write("["
									+ dateFormat2.format(cal2.getTime())
									+ ", IP: " + player.getSession().getIP()
									+ ", inventory] got " + item.getName()
									+ " (" + item.getId() + ") amount: "
									+ item.getAmount());
							writer.newLine();
							writer.flush();
							writer.close();
						} catch (IOException er) {
							er.printStackTrace();
						}
					}
					if (getAllKeys() >= 1)
						player.getPackets().sendIComponentText(interfaceId,
								458, "Play again");
					else
						player.getPackets().sendIComponentText(interfaceId,
								458, "Exit");
					player.getPackets().sendGameMessage(
							"Your prize has been placed in your inventory: "
									+ item.getName() + ".", true);
					claimed = true;
				} else {
					if (System.currentTimeMillis() - player.delay < 1500)
						return;
					player.delay = System.currentTimeMillis();
					if (getAllKeys() >= 1) {
						player.getInterfaceManager().removeInterface(1253);
						openSpinInterface(true, true, false);
					} else
						player.getInterfaceManager().removeInterface(1253);
				}
				player.getPackets().sendCSVarInteger(4080, 1);
				break;
			case 471:
				if (!claimed) {
					Item item = rewards[0];
					player.getMoneyPouch().setAmount(
							ItemDefinitions.getItemDefinitions(item.getId())
									.getPrice() * item.getAmount(), false);
					player.getPackets().sendIComponentText(interfaceId, 458,
							"Play again");
					player.getPackets().sendGameMessage(
							"Your prize has been placed in your money pouch.",
							true);
					claimed = true;
				} else {
					if (System.currentTimeMillis() - player.delay < 1500)
						return;
					player.delay = System.currentTimeMillis();
					if (getAllKeys() >= 1) {
						player.getInterfaceManager().removeInterface(1253);
						openSpinInterface(true, true, false);
					} else
						player.getInterfaceManager().removeInterface(1253);
				}
				player.getPackets().sendCSVarInteger(4080, 1);
				break;
			case 356:
			case 479:
				player.getInterfaceManager().removeInterface(interfaceId);
				break;
			}
			break;
		}
	}

	private void generateRewards(int type) {
		rewards = new Item[13];
		for (int i = 0; i < rewards.length; i++) {
			rewards[i] = generateReward(type, Utils.random(0, 4));
		}
	}

	private Item generateReward(int type, int rarityType) {
		boolean isLamp = Utils.random(4) == 0;
		if (isLamp) {
			int[] lamps = COMMON_LAMPS;
			if (rarityType == VERY_RARE) {
				if (Utils.random(10) == 0)
					lamps = VERY_RARE_LAMPS;
				else
					lamps = RARE_LAMPS;
				this.rarityType = rarityType;
			} else if (rarityType == RARE) {
				lamps = RARE_LAMPS;
				this.rarityType = rarityType;
			} else if (rarityType == UNCOMMON) {
				lamps = UNCOMMON_LAMPS;
				this.rarityType = rarityType;
			}
			return new Item(lamps[Utils.random(lamps.length)], 1);
		} else {
			int[] items = COMMON_OTHERS;
			/*
			 * if (rarityType == VERY_RARE) { if (Utils.random(10) == 0) items =
			 * VERY_RARE_OTHERS; else items = RARE_OTHERS; this.rarityType =
			 * rarityType; } else
			 */if (rarityType == RARE) {
				items = RARE_OTHERS;
				this.rarityType = rarityType;
			} else if (rarityType == UNCOMMON) {
				items = UNCOMMON_OTHERS;
				this.rarityType = rarityType;
			}
			int itemId = items[Utils.random(items.length)];
			int amount;
			if (itemId == 995) {
				int[] amounts = COMMON_COINS_AMOUNT;
				if (rarityType == VERY_RARE) {
					amounts = VERY_RARE_COINS_AMOUNT;
				} else if (rarityType == RARE) {
					amounts = RARE_COINS_AMOUNT;
				} else if (rarityType == UNCOMMON) {
					amounts = UNCOMMON_COINS_AMOUNT;
				}
				amount = amounts[Utils.random(amounts.length)];
			} else {
				ItemDefinitions defs = ItemDefinitions
						.getItemDefinitions(itemId);
				amount = rarityType > COMMON
						|| (!defs.isStackable() && !defs.isNoted()) ? 1
						: (Utils.random(10) + 1);
			}
			if (receivedItems.contains(itemId))
				generateReward(0, Utils.random(0, 4));
			int flaviusisggay = getAmount(itemId, amount);
			return new Item(itemId, flaviusisggay == 0 ? amount : flaviusisggay);
		}
	}

	private void announceWinner(int itemId, int amount) {
		String itemName = ItemDefinitions.getItemDefinitions(itemId).getName()
				.toLowerCase();
		if (!veryRareItems.contains(VERY_RARE_LAMPS))
			veryRareItems.add(VERY_RARE_LAMPS);
		else if (!veryRareItems.contains(VERY_RARE_OTHERS))
			veryRareItems.add(VERY_RARE_OTHERS);
		if (!veryRareItems.contains(itemId))
			return;
		World.sendNews(player,
				player.getDisplayName() + " has won x" + Utils.format(amount)
						+ " " + itemName + " on treasure hunter.", 1);
		player.getPackets().sendGameMessage(Color.PURPLE,
				"Congratulations! You've just won a very rare prize!");
	}

	private int getAmount(int itemId, int amount) {
		switch (itemId) {
		case 30372:
			return Utils.random(10, 250);
		case 15273:
		case 374:
		case 7947:
			return Utils.random(
					player.getSkills().getLevel(Skills.HITPOINTS) / 2, player
							.getSkills().getLevel(Skills.HITPOINTS));
		case 15271:
		case 13436:
			return Utils.random(player.getSkills().getLevel(Skills.COOKING),
					player.getSkills().getLevel(Skills.COOKING) + 10);
		case 8783:
		case 961:
		case 8781:
			return Utils.random(player.getSkills()
					.getLevel(Skills.CONSTRUCTION), player.getSkills()
					.getLevel(Skills.CONSTRUCTION) + 10);
		case 25550:
		case 1748:
		case 1752:
			return Utils.random(player.getSkills().getLevel(Skills.CRAFTING),
					player.getSkills().getLevel(Skills.CRAFTING) + 10);
		case 2358:
		case 2364:
		case 2:
			return Utils.random(player.getSkills().getLevel(Skills.SMITHING),
					player.getSkills().getLevel(Skills.SMITHING) + 10);
		case 1514:
			return Utils.random(
					player.getSkills().getLevel(Skills.WOODCUTTING), player
							.getSkills().getLevel(Skills.WOODCUTTING) + 10);
		case 1778:
			return Utils.random(player.getSkills().getLevel(Skills.FLETCHING),
					player.getSkills().getLevel(Skills.FLETCHING) + 10);
		case 556:
		case 561:
		case 565:
			return Utils.random(
					player.getSkills().getLevel(Skills.MAGIC) + 100, player
							.getSkills().getLevel(Skills.MAGIC) + 300);
		case 537:
		case 535:
			if (player.getSkills().getLevel(Skills.PRAYER) >= 1)
				return Utils.random(5, 15);
			else if (player.getSkills().getLevel(Skills.PRAYER) >= 40)
				return Utils.random(10, 25);
			else if (player.getSkills().getLevel(Skills.PRAYER) >= 70)
				return Utils.random(15, 35);
			else if (player.getSkills().getLevel(Skills.PRAYER) >= 90)
				return Utils.random(30, 50);
		case 24336:
			return Utils.random(10, 50);
		case 454:
		case 445:
		case 450:
		case 448:
		case 437:
		case 452:
			return Utils.random(10, 20);
		}
		return amount;
	}

	public void selectReward(int chestIndex) {
		if (System.currentTimeMillis() - player.delay < 1500)
			return;
		player.delay = System.currentTimeMillis();
		if (reward != null) {
			open(true, false);
			return;
		}
		claimed = false;
		reward = new Prize(new Item(23679, 1), Rarity.VERY_RARE,
				PrizeCategory.LUCKY, 1000000);
		generateRewards(0);
		sendReward(reward, chestIndex, true, false);
	}

	public void sendReward(Prize prize, int chestIndex, boolean instant,
			boolean openAll) {
		if (getAllKeys() < 1) {
			player.getPackets().sendGameMessage("You do not have enough keys.");
			reset();
			return;
		}
		chestIndex += 1;
		int cashoutValue = ItemDefinitions.getItemDefinitions(
				rewards[0].getId()).getPrice()
				* rewards[0].getAmount();
		player.getVarsManager().setVarBit(20736, 1290);
		player.getVarsManager().setVarBit(20747, 0);
		player.getVarsManager().setVarBit(20742, 0);
		player.getVarsManager().setVarBit(20738, 1);
		player.getVarsManager().setVarBit(20749, 0);
		player.getVarsManager().setVarBit(20739, 1);
		player.getVarsManager().setVarBit(20752, 1);
		player.getVarsManager().setVarBit(20751, 0);
		player.getVarsManager().setVarBit(25533, 1);
		player.getVarsManager().setVarBit(28369, 1);
		player.getVarsManager().setVarBit(28370, 0);
		player.getVarsManager().setVarBit(20744, 12);
		player.getVarsManager().setVarBit(20738, 0);
		player.getVarsManager().setVarBit(20749, 90);
		player.getVarsManager().setVarBit(20739, 0);
		player.getVarsManager().setVarBit(20750, 73);
		player.getVarsManager().setVarBit(20752, 0);
		player.getVarsManager().setVarBit(20751, 30);
		player.getVarsManager().setVarBit(25533, 0);
		player.getVarsManager().setVarBit(25534, 30);
		player.getVarsManager().setVarBit(28369, 0);
		player.getVarsManager().setVarBit(28370, 15);
		player.getVarsManager().setVarBit(20736, 1289);
		player.getVarsManager().setVarBit(20747, 1);
		player.getVarsManager().setVarBit(20742, 1);
		player.getVarsManager().setVarBit(20738, 1);
		player.getVarsManager().setVarBit(20749, 0);
		player.getVarsManager().setVarBit(20739, 1);
		player.getVarsManager().setVarBit(20752, 1);
		player.getVarsManager().setVarBit(20751, 0);
		player.getVarsManager().setVarBit(25533, 1);
		player.getVarsManager().setVarBit(28369, 1);
		player.getVarsManager().setVarBit(28370, 0);
		player.getVarsManager().setVarBit(20744, 11);
		player.getVarsManager().setVarBit(20738, 0);
		player.getVarsManager().setVarBit(20749, 89);
		player.getVarsManager().setVarBit(20739, 0);
		player.getVarsManager().setVarBit(20750, 72);
		player.getVarsManager().setVarBit(20752, 0);
		player.getVarsManager().setVarBit(20751, 29);
		player.getVarsManager().setVarBit(25533, 0);
		player.getVarsManager().setVarBit(25534, 29);
		player.getVarsManager().setVarBit(28369, 0);
		player.getVarsManager().setVarBit(28370, 14);
		player.getVarsManager().setVarBit(20736, 1288);
		player.getVarsManager().setVarBit(20747, 2);
		player.getVarsManager().setVarBit(20742, 2);
		player.getVarsManager().setVarBit(20738, 1);
		player.getVarsManager().setVarBit(20749, 0);
		player.getVarsManager().setVarBit(20739, 1);
		player.getVarsManager().setVarBit(20752, 1);
		player.getVarsManager().setVarBit(20751, 0);
		player.getVarsManager().setVarBit(25533, 1);
		player.getVarsManager().setVarBit(28369, 1);
		player.getVarsManager().setVarBit(28370, 0);
		player.getVarsManager().setVarBit(20744, 10);
		player.getVarsManager().setVarBit(20738, 0);
		player.getVarsManager().setVarBit(20749, 88);
		player.getVarsManager().setVarBit(20739, 0);
		player.getVarsManager().setVarBit(20750, 71);
		player.getVarsManager().setVarBit(20752, 0);
		player.getVarsManager().setVarBit(20751, 28);
		player.getVarsManager().setVarBit(25533, 0);
		player.getVarsManager().setVarBit(25534, 28);
		player.getVarsManager().setVarBit(28369, 1);
		player.getVarsManager().setVarBit(28370, 13);
		player.getPackets().sendExecuteScript(7486,
				new Object[] { 41615361, 24130232 });
		player.getVarsManager().setVarBit(1450, -1140846591);
		player.getVarsManager().setVarBit(4478, 0);
		player.getVarsManager().setVarBit(1452, 18832243);
		player.getVarsManager().setVarBit(1449, 1342968724);
		player.getVarsManager().setVarBit(1448, 138412032);
		player.getVarsManager().setVarBit(1451, 139460608);
		player.getVarsManager().setVarBit(4141, 1);
		player.getVarsManager().setVarBit(1444, 268473236);
		player.getVarsManager().setVarBit(1450, -1140846591);
		player.getVarsManager().setVarBit(1454, cashoutValue);
		player.getVarsManager().setVarBit(1455, 1);
		player.getVarsManager().setVarBit(1451, 139722752);
		player.getPackets().sendCSVarInteger(2045, 1);
		player.getPackets().sendExecuteScript(4121, new Object[] { 1 });
		player.getPackets().sendCSVarInteger(4082, 1);
		player.getPackets().sendCSVarInteger(4077, 1);
		player.getPackets().sendCSVarInteger(4097, 1);
		player.getPackets().sendCSVarInteger(1993, 1);
		handleKeys(1, true);
		player.getPackets().sendCSVarInteger(1800,
				getAllKeys() < 1 ? 0 : getAllKeys());
	}

	public void openSpinInterface(boolean force, boolean instant, boolean first) {
		if (getAllKeys() < 1) {
			player.getPackets().sendGameMessage("You do not have enough keys.");
		}
		if (!force) {
			if (player.getInterfaceManager().containsInventoryInter()
					|| player.getInterfaceManager().containsScreenInterface()
					|| player.getInterfaceManager()
							.containsTreasureHunterInterface()) {
				player.getPackets()
						.sendGameMessage(
								"Please finish what you are doing before opening Treasure Hunter.");
				return;
			}
		}
		if (player.getControlerManager().getControler() != null) {
			player.getPackets().sendGameMessage(
					"You can't open Treasure Hunter in this area.");
			return;
		}
		player.stopAll();
		player.getInterfaceManager().setFairyRingInterface(true, 1253);
		open(instant, first);
	}

	public void open(boolean instant, boolean first) {
		player.getVarsManager().setVarBit(4143, 0);
		player.getVarsManager().setVarBit(1451, 5242880);
		player.getVarsManager().setVarBit(1450, -1140842495);
		player.getVarsManager().setVarBit(1449, -1877732866);
		player.getVarsManager().setVarBit(4065, -1);
		player.getVarsManager().setVarBit(4066, 3);
		player.getVarsManager().setVarBit(4066, 0);
		player.getVarsManager().setVarBit(4067, -1);
		player.getVarsManager().setVarBit(4068, 2);
		player.getVarsManager().setVarBit(4068, 0);
		player.getVarsManager().setVarBit(4069, -1);
		player.getVarsManager().setVarBit(4070, 1);
		player.getVarsManager().setVarBit(4070, 0);
		player.getVarsManager().setVarBit(4071, -1);
		player.getVarsManager().setVarBit(4072, 1);
		player.getVarsManager().setVarBit(4072, 0);
		player.getVarsManager().setVarBit(4073, -1);
		player.getVarsManager().setVarBit(4074, 1);
		player.getVarsManager().setVarBit(4074, 0);
		player.getVarsManager().setVarBit(4075, -1);
		player.getVarsManager().setVarBit(4076, 1);
		player.getVarsManager().setVarBit(4076, 0);
		player.getVarsManager().setVarBit(4077, -1);
		player.getVarsManager().setVarBit(4078, 4);
		player.getVarsManager().setVarBit(4078, 0);
		player.getVarsManager().setVarBit(4079, -1);
		player.getVarsManager().setVarBit(4081, -1);
		player.getVarsManager().setVarBit(4082, 1);
		player.getVarsManager().setVarBit(4082, 0);
		player.getVarsManager().setVarBit(4083, -1);
		player.getVarsManager().setVarBit(4084, 1);
		player.getVarsManager().setVarBit(4084, 0);
		player.getVarsManager().setVarBit(4085, -1);
		player.getVarsManager().setVarBit(4086, 2);
		player.getVarsManager().setVarBit(4086, 0);
		player.getVarsManager().setVarBit(4087, -1);
		player.getVarsManager().setVarBit(4088, 2);
		player.getVarsManager().setVarBit(4088, 0);
		player.getVarsManager().setVarBit(4089, -1);
		player.getVarsManager().setVarBit(4090, 2);
		player.getVarsManager().setVarBit(4090, 0);
		player.getVarsManager().setVarBit(4091, -1);
		player.getVarsManager().setVarBit(4092, 1);
		player.getVarsManager().setVarBit(4092, 0);
		player.getVarsManager().setVarBit(4093, -1);
		player.getVarsManager().setVarBit(4094, 1);
		player.getVarsManager().setVarBit(4094, 0);
		player.getVarsManager().setVarBit(4095, -1);
		player.getVarsManager().setVarBit(4096, 1);
		player.getVarsManager().setVarBit(4096, 0);
		player.getVarsManager().setVarBit(4097, -1);
		player.getVarsManager().setVarBit(4098, 1);
		player.getVarsManager().setVarBit(4098, 0);
		player.getVarsManager().setVarBit(4099, -1);
		player.getVarsManager().setVarBit(4100, 1);
		player.getVarsManager().setVarBit(4100, 0);
		player.getVarsManager().setVarBit(4101, -1);
		player.getVarsManager().setVarBit(4102, 1);
		player.getVarsManager().setVarBit(4102, 0);
		player.getVarsManager().setVarBit(4103, -1);
		player.getVarsManager().setVarBit(4104, 1);
		player.getVarsManager().setVarBit(4104, 0);
		player.getVarsManager().setVarBit(4105, -1);
		player.getVarsManager().setVarBit(4106, 1);
		player.getVarsManager().setVarBit(4106, 0);
		player.getVarsManager().setVarBit(4107, -1);
		player.getVarsManager().setVarBit(4108, 1);
		player.getVarsManager().setVarBit(4108, 0);
		player.getVarsManager().setVarBit(4109, -1);
		player.getVarsManager().setVarBit(4110, 2);
		player.getVarsManager().setVarBit(4110, 0);
		player.getVarsManager().setVarBit(4111, -1);
		player.getVarsManager().setVarBit(4112, 2);
		player.getVarsManager().setVarBit(4112, 0);
		player.getVarsManager().setVarBit(4113, -1);
		player.getVarsManager().setVarBit(4114, 3);
		player.getVarsManager().setVarBit(4114, 0);
		player.getVarsManager().setVarBit(4115, -1);
		player.getVarsManager().setVarBit(4116, 1);
		player.getVarsManager().setVarBit(4116, 0);
		player.getVarsManager().setVarBit(4117, -1);
		player.getVarsManager().setVarBit(4118, 1);
		player.getVarsManager().setVarBit(4118, 0);
		player.getVarsManager().setVarBit(4119, -1);
		player.getVarsManager().setVarBit(4120, 4);
		player.getVarsManager().setVarBit(4120, 0);
		player.getVarsManager().setVarBit(4121, -1);
		player.getVarsManager().setVarBit(4122, 4);
		player.getVarsManager().setVarBit(4122, 0);
		player.getVarsManager().setVarBit(4123, -1);
		player.getVarsManager().setVarBit(4124, 1);
		player.getVarsManager().setVarBit(4124, 0);
		player.getVarsManager().setVarBit(4125, -1);
		player.getVarsManager().setVarBit(4126, 5);
		player.getVarsManager().setVarBit(4126, 0);
		player.getVarsManager().setVarBit(4127, -1);
		player.getVarsManager().setVarBit(4128, 5);
		player.getVarsManager().setVarBit(4128, 0);
		player.getVarsManager().setVarBit(4129, -1);
		player.getVarsManager().setVarBit(4130, 2);
		player.getVarsManager().setVarBit(4130, 0);
		player.getVarsManager().setVarBit(4131, -1);
		player.getVarsManager().setVarBit(4132, 0);
		player.getVarsManager().setVarBit(4132, 0);
		player.getVarsManager().setVarBit(4133, -1);
		player.getVarsManager().setVarBit(4134, 0);
		player.getVarsManager().setVarBit(4134, 0);
		player.getVarsManager().setVarBit(4135, -1);
		player.getVarsManager().setVarBit(4136, 0);
		player.getVarsManager().setVarBit(4136, 0);
		player.getVarsManager().setVarBit(4137, -1);
		player.getVarsManager().setVarBit(4138, 0);
		player.getVarsManager().setVarBit(4138, 0);
		player.getVarsManager().setVarBit(4335, -1);
		player.getVarsManager().setVarBit(4336, 0);
		player.getVarsManager().setVarBit(4336, 0);
		player.getVarsManager().setVarBit(4066, 1);
		player.getVarsManager().setVarBit(4066, 17);
		player.getVarsManager().setVarBit(4065, 30527);
		player.getVarsManager().setVarBit(4068, 2);
		player.getVarsManager().setVarBit(4068, 18);
		player.getVarsManager().setVarBit(4067, 30502);
		player.getVarsManager().setVarBit(4070, 2);
		player.getVarsManager().setVarBit(4070, 18);
		player.getVarsManager().setVarBit(4069, 30523);
		player.getVarsManager().setVarBit(4072, 3);
		player.getVarsManager().setVarBit(4072, 19);
		player.getVarsManager().setVarBit(4071, 29901);
		player.getVarsManager().setVarBit(4074, 2);
		player.getVarsManager().setVarBit(4074, 18);
		player.getVarsManager().setVarBit(4073, 30523);
		player.getVarsManager().setVarBit(4076, 1);
		player.getVarsManager().setVarBit(4076, 481);
		player.getVarsManager().setVarBit(4075, 31350);
		player.getVarsManager().setVarBit(4078, 1);
		player.getVarsManager().setVarBit(4078, 17);
		player.getVarsManager().setVarBit(4077, 30543);
		player.getVarsManager().setVarBit(4079, 1513);
		player.getVarsManager().setVarBit(4082, 1);
		player.getVarsManager().setVarBit(4082, 17);
		player.getVarsManager().setVarBit(4081, 30550);
		player.getVarsManager().setVarBit(4084, 1);
		player.getVarsManager().setVarBit(4084, 17);
		player.getVarsManager().setVarBit(4083, 30534);
		player.getVarsManager().setVarBit(4086, 2);
		player.getVarsManager().setVarBit(4086, 18);
		player.getVarsManager().setVarBit(4085, 30523);
		player.getVarsManager().setVarBit(4088, 1);
		player.getVarsManager().setVarBit(4088, 17);
		player.getVarsManager().setVarBit(4087, 23810);
		player.getVarsManager().setVarBit(4090, 1);
		player.getVarsManager().setVarBit(4090, 17);
		player.getVarsManager().setVarBit(4089, 30550);
		player.getVarsManager().setVarBit(4092, 1);
		player.getVarsManager().setVarBit(4092, 17);
		player.getVarsManager().setVarBit(4091, 30547);
		player.getVarsManager().setVarBit(4094, 1);
		player.getVarsManager().setVarBit(4094, 17);
		player.getVarsManager().setVarBit(4093, 31770);
		player.getVarsManager().setVarBit(4096, 1);
		player.getVarsManager().setVarBit(4096, 17);
		player.getVarsManager().setVarBit(4095, 23721);
		player.getVarsManager().setVarBit(4098, 1);
		player.getVarsManager().setVarBit(4098, 17);
		player.getVarsManager().setVarBit(4097, 31770);
		player.getVarsManager().setVarBit(4100, 2);
		player.getVarsManager().setVarBit(4100, 18);
		player.getVarsManager().setVarBit(4099, 23787);
		player.getVarsManager().setVarBit(4102, 2);
		player.getVarsManager().setVarBit(4102, 18);
		player.getVarsManager().setVarBit(4101, 30515);
		player.getVarsManager().setVarBit(4104, 1);
		player.getVarsManager().setVarBit(4104, 17);
		player.getVarsManager().setVarBit(4103, 23806);
		player.getVarsManager().setVarBit(4106, 2);
		player.getVarsManager().setVarBit(4106, 42002);
		player.getVarsManager().setVarBit(4105, 30824);
		player.getVarsManager().setVarBit(4108, 2);
		player.getVarsManager().setVarBit(4108, 18);
		player.getVarsManager().setVarBit(4107, 23758);
		player.getVarsManager().setVarBit(4110, 1);
		player.getVarsManager().setVarBit(4110, 17);
		player.getVarsManager().setVarBit(4109, 30535);
		player.getVarsManager().setVarBit(4112, 1);
		player.getVarsManager().setVarBit(4112, 17);
		player.getVarsManager().setVarBit(4111, 23778);
		player.getVarsManager().setVarBit(4114, 1);
		player.getVarsManager().setVarBit(4114, 17);
		player.getVarsManager().setVarBit(4113, 30550);
		player.getVarsManager().setVarBit(4116, 1);
		player.getVarsManager().setVarBit(4116, 1601);
		player.getVarsManager().setVarBit(4115, 29316);
		player.getVarsManager().setVarBit(4118, 1);
		player.getVarsManager().setVarBit(4118, 17);
		player.getVarsManager().setVarBit(4117, 27234);
		player.getVarsManager().setVarBit(4120, 4);
		player.getVarsManager().setVarBit(4120, 20);
		player.getVarsManager().setVarBit(4119, 27633);
		player.getVarsManager().setVarBit(4122, 4);
		player.getVarsManager().setVarBit(4122, 20);
		player.getVarsManager().setVarBit(4121, 29866);
		player.getVarsManager().setVarBit(4124, 1);
		player.getVarsManager().setVarBit(4124, 17);
		player.getVarsManager().setVarBit(4123, 30550);
		player.getVarsManager().setVarBit(4126, 5);
		player.getVarsManager().setVarBit(4126, 21);
		player.getVarsManager().setVarBit(4125, 30820);
		player.getVarsManager().setVarBit(4128, 5);
		player.getVarsManager().setVarBit(4128, 21);
		player.getVarsManager().setVarBit(4127, 28023);
		player.getVarsManager().setVarBit(4130, 0);
		player.getVarsManager().setVarBit(4130, 0);
		player.getVarsManager().setVarBit(4129, -1);
		player.getVarsManager().setVarBit(4132, 0);
		player.getVarsManager().setVarBit(4132, 0);
		player.getVarsManager().setVarBit(4131, -1);
		player.getVarsManager().setVarBit(4134, 0);
		player.getVarsManager().setVarBit(4134, 0);
		player.getVarsManager().setVarBit(4133, -1);
		player.getVarsManager().setVarBit(4136, 0);
		player.getVarsManager().setVarBit(4136, 0);
		player.getVarsManager().setVarBit(4135, -1);
		player.getVarsManager().setVarBit(4138, 0);
		player.getVarsManager().setVarBit(4138, 0);
		player.getVarsManager().setVarBit(4137, -1);
		player.getVarsManager().setVarBit(4336, 0);
		player.getVarsManager().setVarBit(4336, 0);
		player.getVarsManager().setVarBit(4335, -1);
		player.getPackets().sendCSVarInteger(4082, frozenHearts);
		player.getPackets().sendCSVarInteger(3906, 0);
		player.getPackets().sendCSVarInteger(4142, 10);
		player.getPackets().sendCSVarInteger(1800,
				getAllKeys() < 1 ? 0 : getAllKeys());
		player.getPackets().sendCSVarInteger(1781, 0);
		player.getPackets().sendCSVarInteger(2911, -1);
		player.getPackets().sendCSVarInteger(4038, 0);
		player.getPackets().sendCSVarString(3947,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4039, 0);
		player.getPackets().sendCSVarString(3948,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4040, 0);
		player.getPackets().sendCSVarString(3949,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4041, 0);
		player.getPackets().sendCSVarString(3950,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4042, 0);
		player.getPackets().sendCSVarString(3951,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4043, 1);
		player.getPackets()
				.sendCSVarString(
						3952,
						"This is a stackable bar that can be worked for Smithing XP based on your Smithing level.");
		player.getPackets().sendCSVarInteger(4044, 0);
		player.getPackets().sendCSVarString(3953,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4045, 1);
		player.getPackets().sendCSVarString(3954,
				" Logs cut from a magic tree.");
		player.getPackets().sendCSVarInteger(4046, 0);
		player.getPackets().sendCSVarString(3955,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4047, 0);
		player.getPackets().sendCSVarString(3956,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4048, 0);
		player.getPackets().sendCSVarString(3957,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4049, 1);
		player.getPackets().sendCSVarString(3958,
				"Rub this lamp to get some Farming XP.");
		player.getPackets().sendCSVarInteger(4050, 0);
		player.getPackets().sendCSVarString(3959,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4051, 0);
		player.getPackets().sendCSVarString(3960,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4052, 0);
		player.getPackets()
				.sendCSVarString(
						3961,
						"Deploy this to create an elite training dummy, on which to train your melee skills.");
		player.getPackets().sendCSVarInteger(4053, 0);
		player.getPackets().sendCSVarString(3962,
				"Rub this lamp to get some Strength XP.");
		player.getPackets().sendCSVarInteger(4054, 0);
		player.getPackets()
				.sendCSVarString(
						3963,
						"Deploy this to create an elite training dummy, on which to train your melee skills.");
		player.getPackets().sendCSVarInteger(4055, 0);
		player.getPackets().sendCSVarString(3964,
				"Rub this lamp to get some Mining XP.");
		player.getPackets().sendCSVarInteger(4056, 0);
		player.getPackets().sendCSVarString(3965,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4057, 0);
		player.getPackets().sendCSVarString(3966,
				"Rub this lamp to get some Woodcutting XP.");
		player.getPackets().sendCSVarInteger(4058, 0);
		player.getPackets()
				.sendCSVarString(3967,
						"A dungeoneering token, used to get rewards from Dungeoneering.");
		player.getPackets().sendCSVarInteger(4059, 1);
		player.getPackets().sendCSVarString(3968,
				"Rub this lamp to get some Agility XP.");
		player.getPackets().sendCSVarInteger(4060, 0);
		player.getPackets().sendCSVarString(3969,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4061, 1);
		player.getPackets().sendCSVarString(3970,
				"Rub this lamp to get some Slayer XP.");
		player.getPackets().sendCSVarInteger(4062, 0);
		player.getPackets().sendCSVarString(3971,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4063, 1);
		player.getPackets()
				.sendCSVarString(
						3972,
						"A chunk of tier 4 harvested divine energy. It can be manipulated to create or transmute objects.");
		player.getPackets().sendCSVarInteger(4064, 1);
		player.getPackets().sendCSVarString(3973,
				"Resets a daily D&D of your choice.");
		player.getPackets().sendCSVarInteger(4065, 0);
		player.getPackets().sendCSVarString(3974,
				"The chestwear of an Armadylean archon.");
		player.getPackets().sendCSVarInteger(4066, 1);
		player.getPackets().sendCSVarString(3975,
				"Increases Divination experience gained by 1% when worn.");
		player.getPackets().sendCSVarInteger(4067, 0);
		player.getPackets().sendCSVarString(3976,
				"This star can be drained of energy to give you bonus XP.");
		player.getPackets().sendCSVarInteger(4068, 1);
		player.getPackets()
				.sendCSVarString(
						3977,
						"A garb worn by magic-using followers of Zamorak. Requires Defence (70), Magic (70).");
		player.getPackets().sendCSVarInteger(4069, 1);
		player.getPackets().sendCSVarString(3978, "Provides piercing ideas.");
		player.getPackets().sendCSVarInteger(4142, 10);
		player.getPackets().sendCSVarInteger(1790, 0);
		player.getPackets().sendCSVarInteger(4079, 0);
		player.getPackets().sendCSVarInteger(4080, 1);
		player.getPackets().sendCSVarInteger(1993, 1);

		player.getVarsManager().setVarBit(4143, 0);
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 0 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 1 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 2 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 3 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 4 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 5 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 6 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 7 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 8 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 0, "", -1, 9 });
		player.getPackets().sendExecuteScript(1522,
				new Object[] { 1, "Treasure Hunter", 25687, 0 });
		player.getPackets().sendExecuteScript(2412, new Object[] { 88 });
		player.getPackets().sendCSVarInteger(3906, 0);
		player.getPackets().sendCSVarInteger(4142, 10);
		player.getPackets().sendCSVarInteger(4082, frozenHearts);
		player.getPackets().sendCSVarInteger(1800, getAllKeys() - 1);
		player.getPackets().sendCSVarInteger(2911, -1);
		player.getPackets().sendExecuteScript(187, new Object[] { 1, 7 });
		// player.getInterfaceManager().openWidget(1477, 486, 1253, false);
		player.getPackets().sendExecuteScript(8178, new Object[] {});
		player.getPackets().sendCSVarInteger(1928, 0); // enabling colorful
		player.getPackets().sendExecuteScript(6973, new Object[] {});
		player.getPackets().sendCSVarInteger(1993, 1);
		player.getPackets().sendExecuteScript(11189,
				new Object[] { 35, 24130227, 5012, 32102 });
		generateRewards(0);
		claimed = false;
		String description = ItemExamines.getExamine(rewards[0]);
		int enoughSpace = 1;
		player.getPackets().sendExecuteScript(
				9122,
				new Object[] {
						instant ? 1 : 0,
						rewards[0].getId(),
						rewards[0].getAmount(),
						0,// rarity
						0,// category
						0,
						ItemDefinitions.getItemDefinitions(rewards[0].getId())
								.getPrice() * rewards[0].getAmount(), 2,
						enoughSpace, 1, description, rewards[0].getAmount(),
						rewards[0].getId(), instant ? 1 : 0 });
		if (!player.getInventory().hasFreeSlots())
			player.getPackets().sendIComponentText(1253, 458, "Bank");
		else
			player.getPackets().sendIComponentText(1253, 458, "Backpack");
		player.getPackets().sendHideIComponent(1253, 466, true);
		if (!first)
			sendReward(reward, 2, true, false);
	}

	public void handleKeys(int amount, boolean remove) {
		if (remove) {
			if (dailyKeys >= 1)
				dailyKeys -= amount;
			else if (earnedKeys >= 1)
				dailyKeys -= amount;
			else if (boughtKeys >= 1)
				dailyKeys -= amount;
		} else {
			if ((Utils.currentTimeMillis() - delay) < (12 * 60 * 60 * 1000))
				return;
			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
				player.grandExchangeLimit[i] = 0;
			}
			delay = Utils.currentTimeMillis();
			if (player.isBronzeMember() || player.isSilverMember()
					|| player.isGoldMember())
				dailyKeys += 2;
			else if (player.isPlatinumMember())
				dailyKeys += 3;
			else if (player.isDiamondMember())
				dailyKeys += 4;
			else
				dailyKeys += 1;
			player.getPackets().sendGameMessage(
					"You have received your daily Treasure Hunter "
							+ (player.isAMember() ? "keys" : "key") + ".");
		}
	}

	public void reset() {
		dailyKeys = 0;
		earnedKeys = 0;
		boughtKeys = 0;
	}

	public int getAllKeys() {
		return dailyKeys + earnedKeys + boughtKeys;
	}

	public int getKeys() {
		return dailyKeys;
	}

	public void setKeys(int dailyKeys) {
		this.dailyKeys = dailyKeys;
	}

	public int getEarnedKeys() {
		return earnedKeys;
	}

	public void setEarnedKeys(int earnedKeys) {
		this.earnedKeys = earnedKeys;
	}

	public int getBoughtKeys() {
		return boughtKeys;
	}

	public void setBoughtKeys(int boughtKeys) {
		this.boughtKeys = boughtKeys;
	}

	public Rewards getItemRewards() {
		return itemRewards;
	}

	public void setItemReward(Rewards itemRewards) {
		this.itemRewards = itemRewards;
	}

	public Prize getReward() {
		return reward;
	}

	public void setReward(Prize reward) {
		this.reward = reward;
	}

	public void handleBoughtKeys(int amount) {
		this.boughtKeys += amount;
	}

	public void handleEarnedKeys(int amount) {
		this.earnedKeys += amount;
	}
}