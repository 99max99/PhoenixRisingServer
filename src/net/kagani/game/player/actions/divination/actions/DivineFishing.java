package net.kagani.game.player.actions.divination.actions;

import java.util.HashMap;
import java.util.Map;

import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.utils.Utils;

public class DivineFishing extends Action {

	public enum Fish {

		ANCHOVIES(321, 15, 40),

		BASS(363, 46, 100),

		COD(341, 23, 45),

		CAVE_FISH(15264, 85, 300),

		HERRING(345, 10, 30),

		LOBSTER(377, 40, 90),

		MACKEREL(353, 16, 20),

		MANTA(389, 81, 46),

		MONKFISH(7944, 62, 120),

		PIKE(349, 25, 60),

		SALMON(331, 30, 70),

		SARDINES(327, 5, 20),

		SEA_TURTLE(395, 79, 38),

		SEAWEED(401, 30, 0),

		OYSTER(407, 30, 0),

		SHARK(383, 76, 110),

		SHRIMP(317, 1, 10),

		SWORDFISH(371, 50, 100),

		TROUT(335, 20, 50),

		TUNA(359, 35, 80),

		CAVEFISH(15264, 85, 300),

		ROCKTAIL(15270, 90, 385),

		LEAPING_TROUT(11328, 48, 75),

		LEAPING_SALMON(11330, 58, 125),

		LEAPING_STURGEON(11332, 70, 225),

		CRAYFISH(13435, 1, 10);

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

	public enum DivineFishingSpots {

		DIVINE_CRAYFISH(90223, 1, -1, new Animation(22899), Fish.CRAYFISH),

		DIVINE_HERRING(90224, 1, -1, new Animation(22899), Fish.HERRING),

		DIVINE_TROUT(90225, 1, -1, new Animation(22899), Fish.TROUT),

		DIVINE_SALMON(90226, 1, -1, new Animation(22899), Fish.SALMON),

		DIVINE_LOBSTER(90227, 1, -1, new Animation(22899), Fish.LOBSTER),

		DIVINE_SWORDFISH(90228, 1, -1, new Animation(22899), Fish.SWORDFISH),

		DIVINE_SHARK(90229, 1, -1, new Animation(22899), Fish.SHARK),

		DIVINE_CAVEFISH(90230, 1, -1, new Animation(22899), Fish.CAVE_FISH),

		DIVINE_ROCKTAIL(90231, 1, -1, new Animation(22899), Fish.ROCKTAIL), ;

		private final Fish[] fish;
		private final int id, option, bait;
		private final Animation animation;

		static final Map<Integer, DivineFishingSpots> spot = new HashMap<Integer, DivineFishingSpots>();

		public static DivineFishingSpots forId(int id) {
			return spot.get(id);
		}

		static {
			for (DivineFishingSpots spots : DivineFishingSpots.values())
				spot.put(spots.id | spots.option << 24, spots);
		}

		private DivineFishingSpots(int id, int option, int bait,
				Animation animation, Fish... fish) {
			this.id = id;
			this.bait = bait;
			this.animation = animation;
			this.fish = fish;
			this.option = option;
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

		public int getBait() {
			return bait;
		}

		public Animation getAnimation() {
			return animation;
		}
	}

	private DivineFishingSpots spot;

	WorldObject spott;

	private WorldTile tile;

	private int fishId;

	private final int[] BONUS_FISH = { 341, 349, 401, 407 };

	private boolean multipleCatch;

	public DivineFishing(DivineFishingSpots spot, int x, int y, int plane,
			WorldObject spott) {
		this.spot = spot;
		this.spott = spott;
		tile = new WorldTile(x, y, plane);
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
		return delay;

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
		Item notedfish = new Item(spot.getFish()[fishId].getId() + 1,
				multipleCatch ? 2 : 1);
		player.getInventory().deleteItem(spot.getBait(), 1);
		double totalXp = spot.getFish()[fishId].getXp();
		if (hasFishingSuit(player))
			totalXp *= 1.025;
		player.getSkills().addXp(Skills.FISHING, totalXp);
		int bigFish = Utils.random(2000);
		if (bigFish == 1) {
			if (fish.getId() == 363)
				player.getInventory().addItem(7989, 1);
			else if (fish.getId() == 371)
				player.getInventory().addItem(7991, 1);
			else if (fish.getId() == 383)
				player.getInventory().addItem(7993, 1);
			player.getPackets().sendGameMessage("You catch a enormous fish!");
		} else {
			int roll = Utils.random(100);
			Player owner = spott.getOwner();
			if (player == owner && roll >= 60) {
				player.getInventory().addItem(fish);
				String fishName = fish.getDefinitions().getName().toLowerCase();
				player.getPackets().sendGameMessage(
						"You caught a " + fishName + ".", true);
			} else {
				String fishName = fish.getDefinitions().getName().toLowerCase();
				owner.getInventory().addItem(notedfish);
				owner.getPackets().sendGameMessage(
						"" + player.getDisplayName() + " caught some "
								+ fishName + " for you.");
				player.getInventory().addItem(fish);
				player.getPackets().sendGameMessage(
						"You caught some " + fishName + ".", true);
			}
			// player.getInventory().addItem(fish);
			// player.getPackets().sendGameMessage(getMessage(fish), true);
			if (player.getFamiliar() != null) {
				if (Utils.random(50) == 0
						&& getSpecialFamiliarBonus(player.getFamiliar().getId()) > 0) {
					player.getInventory()
							.addItem(
									new Item(BONUS_FISH[Utils
											.random(BONUS_FISH.length)]));
					player.getSkills().addXp(Skills.FISHING, 5.5);
				}
			}
		}
		fishId = getRandomFish(player);
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
		if (!World.containsObjectWithId(tile, spot.getId())) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"The divine has disapeared.");
			return false;

		}
		return true;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}
