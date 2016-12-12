package net.kagani.game.player.content;

import java.util.HashMap;
import java.util.Map;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class Burying {

	public enum Bone {
		NORMAL(526, 100),

		BURNT(528, 100),

		WOLF(2859, 100),

		MONKEY(3183, 125),

		BAT(530, 125),

		BIG(532, 200),

		JOGRE(3125, 200),

		ZOGRE(4812, 250),

		SHAIKAHAN(3123, 300),

		BABY(534, 350),

		WYVERN(6812, 400),

		DRAGON(536, 500),

		FAYRG(4830, 525),

		RAURG(4832, 550),

		DAGANNOTH(6729, 650),

		OURG(4834, 750),

		AIRUT(30209, 532.5),

		FROST_DRAGON(18830, 850),
		
		FROST_DRAGON1(18832, 850);

		private int id;
		private double experience;

		private static Map<Integer, Bone> bones = new HashMap<Integer, Bone>();

		static {
			for (Bone bone : Bone.values()) {
				bones.put(bone.getId(), bone);
			}
		}

		public static Bone forId(int id) {
			return bones.get(id);
		}

		private Bone(int id, double experience) {
			this.id = id;
			this.experience = experience;
		}

		public int getId() {
			return id;
		}

		public double getExperience() {
			return experience;
		}

		public static final Animation BURY_ANIMATION = new Animation(18009);

		public static void bury(final Player player, int inventorySlot) {
			final Item item = player.getInventory().getItem(inventorySlot);
			if (item == null || Bone.forId(item.getId()) == null)
				return;
			final Bone bone = Bone.forId(item.getId());
			final ItemDefinitions itemDef = new ItemDefinitions(item.getId());
			player.lock(2);
			player.setNextAnimation(BURY_ANIMATION);
			player.getPackets().sendGameMessage(
					"You dig a hole in the ground...");
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.getPackets().sendGameMessage(
							"You bury the " + itemDef.getName().toLowerCase()
									+ ".");
					player.getInventory().deleteItem(inventorySlot, item);
					double xp = bone.getExperience()
							* player.getAuraManager().getPrayerMultiplier()
							* 2.6;
					final int maxPrayer = player.getSkills().getLevelForXp(
							Skills.PRAYER) * 10;
					if (player.getEquipment().containsOneItem(19888)) {
						switch (bone.getId()) {
						case 526:
						case 528:
						case 530:
						case 20264:
							if (player.getPrayer().getPrayerpoints() < maxPrayer) {
								player.getPrayer()
										.setPrayerpoints(
												player.getPrayer()
														.getPrayerpoints() + 50);
								player.getPackets()
										.sendGameMessage(
												"Your demon horn necklace boosts your prayer points.",
												true);
							}
							break;
						case 532:
						case 534:
						case 3125:
						case 6812:
							if (player.getPrayer().getPrayerpoints() < maxPrayer) {
								player.getPrayer()
										.setPrayerpoints(
												player.getPrayer()
														.getPrayerpoints() + 100);
								player.getPackets()
										.sendGameMessage(
												"Your demon horn necklace boosts your prayer points.",
												true);
							}
							break;
						case 536:
						case 6729:
						case 4834:
						case 4835:
						case 14793:
						case 14794:
						case 18832:
						case 18830:
						case 18831:
						case 30209:
						case 20268:
							if (player.getPrayer().getPrayerpoints() < maxPrayer) {
								player.getPrayer()
										.setPrayerpoints(
												player.getPrayer()
														.getPrayerpoints() + 150);
								player.getPackets()
										.sendGameMessage(
												"Your demon horn necklace boosts your prayer points.",
												true);
							}
							break;
						}
					}
					player.getSkills().addXp(Skills.PRAYER, xp);
					Double lastPrayer = (Double) player
							.getTemporaryAttributtes().get("current_prayer_xp");
					if (lastPrayer == null) {
						lastPrayer = 0.0;
					}
					double total = xp + lastPrayer;
					int amount = (int) (total / 500);
					if (amount != 0) {
						double restore = player.getAuraManager()
								.getPrayerRestoration()
								* (player.getSkills().getLevelForXp(
										Skills.PRAYER) * 10);
						player.getPrayer().restorePrayer(
								(int) (amount * restore));
						total -= amount * 500;
					}
					player.getTemporaryAttributtes().put("current_prayer_xp",
							total);
					stop();
				}

			});
		}
	}
}