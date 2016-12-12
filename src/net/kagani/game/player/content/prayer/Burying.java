package net.kagani.game.player.content.prayer;

import java.util.HashMap;
import java.util.Map;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class Burying {
	
	public enum Bone {
		NORMAL(526, 4.5),

		BURNT(528, 4.5),

		WOLF(2859, 4.5),

		MONKEY(3183, 5),

		BAT(530, 5),

		BIG(532, 15),

		JOGRE(3125, 15),

		ZOGRE(4812, 22.5),

		SHAIKAHAN(3123, 25),

		BABY(534, 30),

		WYVERN(6812, 50),

		DRAGON(536, 80),

		FAYRG(4830, 90),

		RAURG(4832, 105),

		DAGANNOTH(6729, 125),

		OURG(4834, 140),
		// dung ones
		FROST_DRAGON(18830, 185),

		FROST_DRAGON4(18833, 185)
		// real ones
		, FROST_DRAGON_3(18832, 185)

		, IMPIOUS(20264, 4, true)

		, ACCURSED(20266, 12.5, true)

		, INFERNAL(20268, 62.5, true)
		
		, TORTURED(32945, 240, true)

		, AIRUT(30209, 220, true);

		private int id;
		private double experience;
		private boolean ash;

		private static Map<Integer, Bone> bones = new HashMap<Integer, Bone>();

		static {
			for (Bone bone : Bone.values()) {
				bones.put(bone.getId(), bone);
			}
		}

		public static Bone forId(int id) {
			return bones.get(id);
		}

		private Bone(int id, double experience, boolean ash) {
			this.id = id;
			this.experience = experience;
			this.ash = ash;
		}

		private Bone(int id, double experience) {
			this.id = id;
			this.experience = experience;
		}

		public int getId() {
			return id;
		}

		public boolean isAsh() {
			return ash;
		}

		public double getExperience() {
			return experience;
		}

		public static final Animation BURY_ANIMATION = new Animation(827);

		public static void bury(final Player player, int inventorySlot) {
			final Item item = player.getInventory().getItem(inventorySlot);
			if (item == null)
				return;
			final Bone bone = Bone.forId(item.getId());
			if (bone == null)
				return;
			final ItemDefinitions itemDef = new ItemDefinitions(item.getId());
			player.lock(2);
			switch (item.getId()) {
			case 20264:
				player.setNextAnimation(new Animation(445));
				player.setNextGraphics(new Graphics(56));
				break;
			case 20266:
				player.setNextAnimation(new Animation(445));
				player.setNextGraphics(new Graphics(47));
				break;
			case 20268:
				player.setNextAnimation(new Animation(445));
				player.setNextGraphics(new Graphics(40));
				break;
			case 32945:
				player.setNextAnimation(new Animation(445));
				player.setNextGraphics(new Graphics(40));
				break;
			default:
				player.setNextAnimation(BURY_ANIMATION);
				break;
			}

			player.getPackets().sendGameMessage(
					bone.ash ? "You scatter the ashes in the wind."
							: "You dig a hole in the ground...", true);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (!bone.ash)
						player.getPackets()
								.sendGameMessage(
										"You bury the "
												+ itemDef.getName()
														.toLowerCase(), true);
					player.getInventory().deleteItem(inventorySlot,
							new Item(item));
					double xp = bone.getExperience()
							* player.getAuraManager().getPrayerMultiplier();
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