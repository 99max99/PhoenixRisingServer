package net.kagani.game.player.content.ectofuntus;

import java.util.HashMap;
import java.util.Map;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.prayer.Burying;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class Ectofuntus {

	/**
	 * @author: Dylan Page
	 */

	public static final int EMPTY_POT = 1931;
	public static final int EMPTY_BUCKET = 1925;
	public static final int ECTO_TOKEN = 4278;
	public static final int BUCKET_OF_SLIME = 4286;
	public static final int ECTOPHIAL = 4251;

	public static final int HOPPER_OBJECT = 11162;
	public static final int GRINDER_OBJECT = 11163;
	public static final int BIN_OBJECT = 11164;

	public static enum BoneMeal {
		BONES(526, 4255), BAT_BONES(530, 4256), BIG_BONES(532, 4257), BABY_DRAGON_BONES(
				534, 4260), DRAGON_BONES(536, 4261), DAGANNOTH_BONES(6729, 6728), WYVERN_BONES(
				6812, 6810), OURG_BONES(4834, 4855), FROST_BONES(18830, 18834), FROSTD_BONES(
				18832, 18834), IMPIOUS_ASHES(20264, 20264), ACCURSED_ASHES(
				20266, 20266), INFERNAL_ASHES(20268, 20268);

		private int boneId;
		private int boneMealId;

		private static Map<Integer, BoneMeal> bonemeals = new HashMap<Integer, BoneMeal>();
		private static Map<Integer, BoneMeal> bones = new HashMap<Integer, BoneMeal>();

		public static BoneMeal forBoneId(int itemId) {
			return bonemeals.get(itemId);
		}

		public static BoneMeal forMealId(int itemId) {
			return bones.get(itemId);
		}

		static {
			for (final BoneMeal bonemeal : BoneMeal.values()) {
				bonemeals.put(bonemeal.boneId, bonemeal);
			}
			for (final BoneMeal bonemeal : BoneMeal.values()) {
				bones.put(bonemeal.boneMealId, bonemeal);
			}
		}

		private BoneMeal(int boneId, int boneMealId) {
			this.boneId = boneId;
			this.boneMealId = boneMealId;
		}

		public int getBoneId() {
			return boneId;
		}

		public int getBoneMealId() {
			return boneMealId;
		}
	}

	public static void handleWorship(Player player) {
		if (player.getInventory().containsItem(4252, 1)) {
			player.setNextAnimation(new Animation(1649));
			player.getInventory().deleteItem(4252, 1);
			player.getInventory().addItem(4251, 1);
			player.getPackets().sendGameMessage(
					"You refill the ectophial from the Ectofuntus.");
			return;
		}
		if (!player.getInventory().containsItem(BUCKET_OF_SLIME, 1)) {
			player.getPackets()
					.sendGameMessage(
							"You need a bucket of slime before you can worship the ectofuntus.");
			return;
		}
		for (Item item : player.getInventory().getItems().getItems()) {
			if (item == null)
				continue;
			BoneMeal bone = BoneMeal.forMealId(item.getId());
			if (bone != null) {
				Burying.Bone boneData = Burying.Bone.forId(bone.getBoneId());
				if (boneData == null) {
					player.getPackets()
							.sendGameMessage(
									"Error bone not added.. Please post the bone you tried to add on the forums.");
					return;
				}
				player.setNextAnimation(new Animation(1651));
				player.getInventory().deleteItem(bone.getBoneMealId(), 1);
				player.getInventory().addItem(EMPTY_POT, 1);
				player.getInventory().deleteItem(BUCKET_OF_SLIME, 1);
				player.getInventory().addItem(EMPTY_BUCKET, 1);
				player.getSkills().addXp(Skills.PRAYER,
						boneData.getExperience() * 4);
				player.unclaimedEctoTokens += 5;
				return;
			}
		}
	}

	public static boolean handleObjects(final Player player, final int objectId) {
		switch (objectId) {
		case 5268: {
			player.setNextAnimation(new Animation(828));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(new WorldTile(3669, 9888, 3));
				}
			}, 0);
		}
			return true;

		case 5264: {
			player.setNextAnimation(new Animation(828));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(new WorldTile(3654, 3519, 0));
				}
			}, 0);
		}
			return true;

		case 9308: {
			if (player.getSkills().getLevel(Skills.AGILITY) < 53) {
				player.getPackets().sendGameMessage(
						"You need 53 Agility to maneuver this obstacle.");
				return true;
			}
			player.setNextAnimation(new Animation(828));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(new WorldTile(3671, 9888, 2));
				}
			}, 1);
		}
			return true;

		case 9307: {
			if (player.getSkills().getLevel(Skills.AGILITY) < 53) {
				player.getPackets().sendGameMessage(
						"You need 53 Agility to maneuver this obstacle.");
				return true;
			}
			player.setNextAnimation(new Animation(828));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(new WorldTile(3670, 9888, 3));
				}
			}, 1);
		}
			return true;

		case 5263:
			if (player.getPlane() == 3)
				player.setNextWorldTile(new WorldTile(3688, 9888, 2));
			if (player.getPlane() == 2)
				player.setNextWorldTile(new WorldTile(3675, 9887, 1));
			if (player.getPlane() == 1)
				player.setNextWorldTile(new WorldTile(3683, 9888, 0));
			return true;

		case 5262:
			if (player.getPlane() == 2)
				player.setNextWorldTile(new WorldTile(3692, 9888, 3));
			if (player.getPlane() == 1)
				player.setNextWorldTile(new WorldTile(3671, 9888, 2));
			if (player.getPlane() == 0)
				player.setNextWorldTile(new WorldTile(3687, 9888, 1));
			return true;

		case 5282:
			handleWorship(player);
			return true;

		case GRINDER_OBJECT:
			if (player.boneType != -1 && !player.bonesGrinded) {
				player.getPackets()
						.sendGameMessage(
								"You turn the grinder, some crushed bones fall into the bin.");
				player.setNextAnimation(new Animation(1648));
				player.bonesGrinded = true;
			} else {
				player.setNextAnimation(new Animation(1648));
			}
			return true;

		case BIN_OBJECT:
			if (player.boneType == -1) {
				player.getPackets()
						.sendGameMessage(
								"You need to put some bones in the hopper and grind them first.");
				return true;
			}
			if (!player.bonesGrinded) {
				player.getPackets()
						.sendGameMessage(
								"You need to grind the bones by turning the grinder first.");
				return true;
			}
			if (!player.getInventory().containsItem(EMPTY_POT, 1)) {
				player.getPackets()
						.sendGameMessage(
								"You need an empty pot to fill with the crushed bones.");
				return true;
			}
			if (player.boneType != -1 && player.bonesGrinded) {
				BoneMeal meal = BoneMeal.forBoneId(player.boneType);
				if (meal != null) {
					player.getPackets().sendGameMessage(
							"You fill an empty pot with bones.");
					player.setNextAnimation(new Animation(1650));
					player.getInventory().deleteItem(EMPTY_POT, 1);
					player.getInventory().addItem(meal.getBoneMealId(), 1);
					player.boneType = -1;
					player.bonesGrinded = false;
				} else {
					player.boneType = -1;
				}
			}
			return true;
		}
		return false;
	}

	public static boolean handleItemOnObject(Player player, int itemId,
			int objectId) {
		ObjectDefinitions objectDefs = ObjectDefinitions
				.getObjectDefinitions(objectId);
		ItemDefinitions itemDefs = ItemDefinitions.getItemDefinitions(itemId);

		if (itemId == EMPTY_BUCKET && objectId == 17119) {
			player.getActionManager().setAction(new SlimeBucketFill());
			return true;
		}

		if (itemDefs.getName().toLowerCase().contains("bone")
				&& objectId == HOPPER_OBJECT) {
			if (player.boneType != -1) {
				player.getPackets().sendGameMessage(
						"You already have some bones in the hopper.");
				return true;
			}
			BoneMeal meal = BoneMeal.forBoneId(itemId);
			if (meal != null) {
				player.boneType = meal.getBoneId();
				player.getPackets().sendGameMessage(
						"You put the bones in the hopper.");
				player.setNextAnimation(new Animation(1649));
				player.getInventory().deleteItem(meal.getBoneId(), 1);
			} else {
				player.boneType = -1;
			}
		}
		return false;
	}
}