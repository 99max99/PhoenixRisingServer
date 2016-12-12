package net.kagani.game.player.actions;

import java.util.HashMap;
import java.util.Map;

import net.kagani.game.Animation;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.FishingSpotsHandler;
import net.kagani.game.player.content.skillertasks.SkillTasks;
import net.kagani.utils.Utils;

public class Fishing extends Action {

	public enum Fish {

		/**
		 * LEVEL 15
		 */
		ANCHOVIES(321, 15, 40),

		/**
		 * LEVEL 46
		 */
		BASS(363, 46, 100),

		/**
		 * LEVEL 23
		 */
		COD(341, 23, 45),

		/**
		 * LEVEL 85
		 */
		CAVE_FISH(15264, 85, 300), /**
		 * LEVEL 10
		 */
		HERRING(345, 10, 30), /**
		 * LEVEL 40
		 */
		LOBSTER(377, 40, 90), /**
		 * LEVEL 16
		 */
		MACKEREL(353, 16, 20), /**
		 * LEVEL 81
		 */
		MANTA(389, 81, 46), /**
		 * LEVEL 62
		 */
		MONKFISH(7944, 62, 120), /**
		 * LEVEL 25
		 */
		PIKE(349, 25, 60), /**
		 * LEVEL 30
		 */
		SALMON(331, 30, 70), /**
		 * LEVEL 5
		 */
		SARDINES(327, 5, 20), /**
		 * LEVEL 79
		 */
		SEA_TURTLE(395, 79, 38), /**
		 * LEVEL 30
		 */
		SEAWEED(401, 30, 15), /**
		 * LEVEL 30
		 */
		OYSTER(407, 30, 15), /**
		 * LEVEL 76
		 */
		SHARK(383, 76, 110), /**
		 * LEVEL 1
		 */
		SHRIMP(317, 1, 10), /**
		 * LEVEL 50
		 */
		SWORDFISH(371, 50, 100), /**
		 * LEVEL 20
		 */
		TROUT(335, 20, 50), /**
		 * LEVEL 35
		 */
		TUNA(359, 35, 80), /**
		 * LEVEL 65
		 */
		KARAMBWAN(3142, 65, 130), /**
		 * LEVEL 85
		 */
		CAVEFISH(15264, 85, 180), /**
		 * LEVEL 90
		 */
		ROCKTAIL(15270, 90, 220), /**
		 * LEVEL 48
		 */
		LEAPING_TROUT(11328, 48, 75), /**
		 * LEVEL 58
		 */
		LEAPING_SALMON(11330, 58, 125), /**
		 * LEVEL 70
		 */
		LEAPING_STURGEON(11332, 70, 225);

		private final int id, level;
		private final double xp;

		private Fish(int id, int level, double xp) {
			this.id = id;
			this.level = level;
			this.xp = xp;
		}

		public int getId() {
			return id;
		}

		public int getLevel() {
			return level;
		}

		public double getXp() {
			return xp;
		}
	}

	public enum FishingSpots {
		CAVEFISH_SHOAL(8841, 1, 307, 313, new Animation(622), Fish.CAVE_FISH), ROCKTAIL_SHOAL(
				8842, 1, 307, 15263, new Animation(622), Fish.ROCKTAIL), NET(
				327, 1, 303, -1, new Animation(621), Fish.SHRIMP,
				Fish.ANCHOVIES), //
		LURE(328, 1, 309, 314, new Animation(622), Fish.TROUT, Fish.SALMON), //
		LURE2(329, 1, 309, 314, new Animation(622), Fish.TROUT, Fish.SALMON), LURE3(
				317, 1, 309, 314, new Animation(622), Fish.TROUT, Fish.SALMON), BAIT2(
				328, 2, 307, 313, new Animation(622), Fish.PIKE), //
		BAIT3(329, 2, 307, 313, new Animation(622), Fish.PIKE, Fish.CAVE_FISH), NET4(
				325, 1, 303, -1, new Animation(621), Fish.SHRIMP,
				Fish.ANCHOVIES), BAIT4(325, 2, 307, 313, new Animation(622),
				Fish.SARDINES, Fish.HERRING), CAGE(6267, 1, 301, -1,
				new Animation(619), Fish.LOBSTER), LURE4(3019, 1, 11323, 314,
				new Animation(622), Fish.LEAPING_TROUT, Fish.LEAPING_SALMON,
				Fish.LEAPING_STURGEON), LURE5(317, 1, 11323, 314,
				new Animation(622), Fish.LEAPING_TROUT, Fish.LEAPING_SALMON,
				Fish.LEAPING_STURGEON), CAGE2(312, 1, 301, -1, new Animation(
				619), Fish.LOBSTER), HARPOON(312, 2, 311, -1,
				new Animation(618), Fish.TUNA, Fish.SWORDFISH), BIG_NET(313, 1,
				305, -1, new Animation(620), Fish.MACKEREL, Fish.COD,
				Fish.BASS, Fish.SEAWEED, Fish.OYSTER), HARPOON2(313, 2, 311,
				-1, new Animation(618), Fish.SHARK), HARPOON3(3848, 1, 311, -1,
				new Animation(618), Fish.TUNA, Fish.SWORDFISH), NET3(3848, 2,
				303, -1, new Animation(621), Fish.MONKFISH), VESSEL(1177, 1,
				3157, -1, new Animation(621), Fish.KARAMBWAN), NET2(952, 1,
				303, -1, new Animation(621), Fish.SHRIMP);

		static final Map<Integer, FishingSpots> spot = new HashMap<Integer, FishingSpots>();

		static {
			for (final FishingSpots spots : FishingSpots.values())
				spot.put(spots.id | spots.option << 24, spots);
		}

		public static FishingSpots forId(int id) {
			return spot.get(id);
		}

		private final Fish[] fish;

		private final int id, option, tool, bait;

		private final Animation animation;

		private FishingSpots(int id, int option, int tool, int bait,
				Animation animation, Fish... fish) {
			this.id = id;
			this.tool = tool;
			this.bait = bait;
			this.animation = animation;
			this.fish = fish;
			this.option = option;
		}

		public Animation getAnimation() {
			return animation;
		}

		public int getBait() {
			return bait;
		}

		public Fish[] getFish() {
			return fish;
		}

		public int getId() {
			return id;
		}

		public int getOption() {
			return option;
		}

		public int getTool() {
			return tool;
		}
	}

	private FishingSpots spot;

	private NPC npc;
	private WorldTile tile;
	private int fishId;

	private final int[] BONUS_FISH = { 341, 349, 401, 407 };

	private boolean multipleCatch;

	public Fishing(FishingSpots spot, NPC npc) {
		this.spot = spot;
		this.npc = npc;
		tile = new WorldTile(npc);
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		fishId = getRandomFish(player);
		if (spot.getFish()[fishId] == Fish.TUNA
				|| spot.getFish()[fishId] == Fish.SHARK
				|| spot.getFish()[fishId] == Fish.SWORDFISH) {
			if (Utils.random(50) <= 5) {
				if (player.getSkills().getLevel(Skills.AGILITY) >= spot
						.getFish()[fishId].getLevel())
					multipleCatch = true;
			}
		}
		player.getPackets().sendGameMessage("You attempt to capture a fish...",
				true);
		setActionDelay(player, getFishingDelay(player));
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(spot.getAnimation());
		return checkAll(player);
	}

	private int getFishingDelay(Player player) {
		int playerLevel = player.getSkills().getLevel(Skills.FISHING);
		int fishLevel = spot.getFish()[fishId].getLevel();
		int modifier = spot.getFish()[fishId].getLevel();
		int randomAmt = Utils.random(4);
		double cycleCount = 1, otherBonus = 0;
		if (player.getFamiliar() != null)
			otherBonus = getSpecialFamiliarBonus(player.getFamiliar().getId());
		cycleCount = Math
				.ceil(((fishLevel + otherBonus) * 50 - playerLevel * 10)
						/ modifier * 0.25 - randomAmt * 4);
		if (cycleCount < 1)
			cycleCount = 1;
		int delay = (int) cycleCount + 1;
		delay /= player.getAuraManager().getFishingAccurayMultiplier();
		return delay * 3;

	}

	private int getSpecialFamiliarBonus(int id) {
		switch (id) {
		case 6796:
		case 6795:// rock crab
			return 1;
		}
		return -1;
	}

	private int getRandomFish(Player player) {
		int random = Utils.random(spot.getFish().length);
		int difference = player.getSkills().getLevel(Skills.FISHING)
				- spot.getFish()[random].getLevel();
		if (difference < -1)
			return random = 0;
		if (random < -1)
			return random = 0;
		return random;
	}

	@Override
	public int processWithDelay(Player player) {
		addFish(player);
		return getFishingDelay(player);
	}

	private void addFish(Player player) {
		Item fish = new Item(spot.getFish()[fishId].getId(), multipleCatch ? 2
				: 1);
		player.getPackets().sendGameMessage(getMessage(fish), true);
		player.getInventory().deleteItem(spot.getBait(), 1);
		double totalXp = spot.getFish()[fishId].getXp();
		if (hasFishingSuit(player))
			totalXp *= 1.125;
		player.getSkills().addXp(Skills.FISHING, totalXp);
		player.getInventory().addItem(fish);

		if (spot.getFish()[fishId] == Fish.ANCHOVIES) {
			player.getSkillTasks().decreaseTask(SkillTasks.FANCHOVIES1);
			player.getSkillTasks().decreaseTask(SkillTasks.FANCHOVIES2);
		} else if (spot.getFish()[fishId] == Fish.HERRING) {
			player.getSkillTasks().decreaseTask(SkillTasks.FHERRING1);
			player.getSkillTasks().decreaseTask(SkillTasks.FHERRING2);
		} else if (spot.getFish()[fishId] == Fish.LOBSTER) {
			player.getSkillTasks().decreaseTask(SkillTasks.FLOBSTER1);
			player.getSkillTasks().decreaseTask(SkillTasks.FLOBSTER2);
		} else if (spot.getFish()[fishId] == Fish.ROCKTAIL) {
			player.getSkillTasks().decreaseTask(SkillTasks.FROCKTAIL1);
			player.getSkillTasks().decreaseTask(SkillTasks.FROCKTAIL2);
		} else if (spot.getFish()[fishId] == Fish.SALMON) {
			player.getSkillTasks().decreaseTask(SkillTasks.FSALMON1);
			player.getSkillTasks().decreaseTask(SkillTasks.FSALMON2);
		} else if (spot.getFish()[fishId] == Fish.SHARK) {
			player.getSkillTasks().decreaseTask(SkillTasks.FSHARK1);
			player.getSkillTasks().decreaseTask(SkillTasks.FSHARK2);
			player.getSkillTasks().decreaseTask(SkillTasks.FSHARK3);
		} else if (spot.getFish()[fishId] == Fish.SHRIMP) {
			player.getSkillTasks().decreaseTask(SkillTasks.FSHRIMP1);
			player.getSkillTasks().decreaseTask(SkillTasks.FSHRIMP2);
		} else if (spot.getFish()[fishId] == Fish.SWORDFISH) {
			player.getSkillTasks().decreaseTask(SkillTasks.FSWORD1);
			player.getSkillTasks().decreaseTask(SkillTasks.FSWORD2);
		} else if (spot.getFish()[fishId] == Fish.TROUT) {
			player.getSkillTasks().decreaseTask(SkillTasks.FTROUT1);
			player.getSkillTasks().decreaseTask(SkillTasks.FTROUT2);
		} else if (spot.getFish()[fishId] == Fish.TUNA) {
			player.getSkillTasks().decreaseTask(SkillTasks.FTUNA1);
			player.getSkillTasks().decreaseTask(SkillTasks.FTUNA2);
		}
		if (player.getDailyTask() != null)
			player.getDailyTask().incrementTask(player, 3, fish.getId(),
					Skills.FISHING);
		if (player.getFamiliar() != null) {
			if (Utils.random(50) == 0
					&& getSpecialFamiliarBonus(player.getFamiliar().getId()) > 0) {
				player.getInventory().addItem(
						new Item(BONUS_FISH[Utils.random(BONUS_FISH.length)]));
				player.getSkills().addXp(Skills.FISHING, 5.5);
			}
		}
		fishId = getRandomFish(player);
		if (Utils.random(50) == 0 && FishingSpotsHandler.moveSpot(npc))
			player.setNextAnimation(new Animation(-1));
	}

	private boolean hasFishingSuit(Player player) {
		if (player.getEquipment().getHatId() == 24427
				&& player.getEquipment().getChestId() == 24428
				&& player.getEquipment().getLegsId() == 24429
				&& player.getEquipment().getBootsId() == 24430)
			return true;
		return false;
	}

	private String getMessage(Item fish) {
		if (spot.getFish()[fishId] == Fish.ANCHOVIES
				|| spot.getFish()[fishId] == Fish.SHRIMP)
			return "You manage to catch some "
					+ fish.getDefinitions().getName().toLowerCase() + ".";
		else if (multipleCatch)
			return "Your quick reactions allow you to catch two "
					+ fish.getDefinitions().getName().toLowerCase() + ".";
		else
			return "You manage to catch a "
					+ fish.getDefinitions().getName().toLowerCase() + ".";
	}

	private boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Skills.FISHING) < spot.getFish()[fishId]
				.getLevel()) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a fishing level of "
							+ spot.getFish()[fishId].getLevel()
							+ " to fish here.");
			return false;
		}
		if (!player.getInventory().containsOneItem(spot.getTool())) {
			player.getPackets()
					.sendGameMessage(
							"You need a "
									+ new Item(spot.getTool()).getDefinitions()
											.getName().toLowerCase()
									+ " to fish here.");
			return false;
		}
		if (!player.getInventory().containsOneItem(spot.getBait())
				&& spot.getBait() != -1) {
			player.getPackets()
					.sendGameMessage(
							"You don't have "
									+ new Item(spot.getBait()).getDefinitions()
											.getName().toLowerCase()
									+ " to fish here.");
			return false;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.setNextAnimation(new Animation(-1));
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You don't have enough inventory space.");
			return false;
		}
		if (tile.getX() != npc.getX() || tile.getY() != npc.getY())
			return false;
		return true;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}