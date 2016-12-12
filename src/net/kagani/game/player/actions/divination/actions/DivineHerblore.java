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

public class DivineHerblore extends Action {

	public enum Herbs {

		GUAM(199, 9, 100),

		TARRONMIN(203, 14, 15),

		MARRENTILL(201, 19, 15),

		HARRALANDER(205, 26, 48),

		RANARR(207, 32, 60),

		TOADFLAX(3049, 38, 68),

		SPIRIT_WEED(12174, 36, 68),

		IRIT(209, 44, 98),

		WERGALI(14836, 46, 104),

		AVANTOE(211, 50, 120),

		KWUARM(213, 56, 150),

		SNAPDRAGON(3051, 62, 200),

		CADANTINE(215, 67, 140),

		LANTADYME(2485, 73, 300),

		DWARF_WEED(267, 79, 400),

		TORSTOL(219, 85, 480),

		FELLSTALK(21626, 91, 640);

		private final int id, level;
		private final double xp;

		private Herbs(int id, int level, double xp) {
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

	public enum DivineHerbsSpots {

		DIVINE_HERB_I(87280, 1, -1, new Animation(21237), 10, Herbs.GUAM,
				Herbs.MARRENTILL, Herbs.TARRONMIN, Herbs.HARRALANDER,
				Herbs.RANARR, Herbs.SPIRIT_WEED, Herbs.TOADFLAX),

		DIVINE_HERB_II(87281, 1, -1, new Animation(21237), 10, Herbs.IRIT,
				Herbs.WERGALI, Herbs.AVANTOE, Herbs.KWUARM, Herbs.SNAPDRAGON),

		DIVINE_HERB_III(87282, 1, -1, new Animation(21237), 10,
				Herbs.CADANTINE, Herbs.LANTADYME, Herbs.DWARF_WEED,
				Herbs.TORSTOL, Herbs.FELLSTALK);

		private final Herbs[] herbs;
		private final int id, option, bait;
		private final Animation animation;

		static final Map<Integer, DivineHerbsSpots> spot = new HashMap<Integer, DivineHerbsSpots>();

		public static DivineHerbsSpots forId(int id) {
			return spot.get(id);
		}

		static {
			for (DivineHerbsSpots spots : DivineHerbsSpots.values())
				spot.put(spots.id | spots.option << 24, spots);
		}

		private int randomLifeProbability;

		private DivineHerbsSpots(int id, int option, int bait,
				Animation animation, int randomLifeProbability, Herbs... herbs) {
			this.id = id;
			this.bait = bait;
			this.animation = animation;
			this.randomLifeProbability = randomLifeProbability;
			this.herbs = herbs;
			this.option = option;
		}

		public Herbs[] getHerbs() {
			return herbs;
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

		public int getRandomLifeProbability() {
			return randomLifeProbability;
		}
	}

	private DivineHerbsSpots spot;
	WorldObject spott;
	private WorldTile tile;
	private int fishId;

	private boolean multipleCatch;

	public DivineHerblore(DivineHerbsSpots spot, int x, int y, int plane,
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
		player.getPackets().sendGameMessage(
				"You start harvesting from the divine.", true);
		setActionDelay(player, getFishingDelay(player));
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(spot.getAnimation());
		return checkAll(player);
	}

	private int getFishingDelay(Player player) {
		int summoningBonus = player.getFamiliar() != null ? (player
				.getFamiliar().getId() == 6808 || player.getFamiliar().getId() == 6807) ? 10
				: 0
				: 0;
		int wcTimer = spot.getRandomLifeProbability()
				- (player.getSkills().getLevel(15) + summoningBonus);
		if (wcTimer < 1 + spot.getRandomLifeProbability())
			wcTimer = 1 + Utils.random(spot.getRandomLifeProbability());
		wcTimer /= player.getAuraManager().getFishingAccurayMultiplier();
		return wcTimer;
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
		int random = Utils.random(spot.getHerbs().length);
		int difference = player.getSkills().getLevel(Skills.HERBLORE)
				- spot.getHerbs()[random].getLevel();
		if (difference < -1)
			return random = 0;
		if (random < -1)
			return random = 0;
		return random;
	}

	@Override
	public int processWithDelay(Player player) {
		addFish(player);
		if (Utils.random(spot.getRandomLifeProbability()) == 0) {
			player.stopAll();
			player.setNextAnimation(new Animation(-1));
			return -1;
		}
		return getFishingDelay(player);
	}

	private void addFish(Player player) {
		Item fish = new Item(spot.getHerbs()[fishId].getId(), multipleCatch ? 2
				: 1);
		Item notedfish = new Item(spot.getHerbs()[fishId].getId() + 1,
				multipleCatch ? 2 : 1);
		double totalXp = spot.getHerbs()[fishId].getXp();
		player.getSkills().addXp(Skills.HERBLORE, totalXp);
		int roll = Utils.random(100);
		Player owner = spott.getOwner();
		if (player == owner && roll >= 60) {
			player.getInventory().addItem(fish);
			String fishName = fish.getDefinitions().getName().toLowerCase();
			player.getPackets().sendGameMessage(
					"You harvest a " + fishName + ".", true);
		} else {
			String fishName = fish.getDefinitions().getName().toLowerCase();
			owner.getInventory().addItem(notedfish);
			owner.getPackets().sendGameMessage(
					"" + player.getUsername() + " harvested some " + fishName
							+ " for you.");
			player.getInventory().addItem(fish);
			player.getPackets().sendGameMessage(
					"You harvest some " + fishName + ".", true);
		}
		player.getPackets().sendGameMessage(getMessage(fish), true);
		if (player.getFamiliar() != null) {
			if (Utils.random(50) == 0
					&& getSpecialFamiliarBonus(player.getFamiliar().getId()) > 0) {
				player.getInventory().addItem(fish);
				player.getSkills().addXp(Skills.HERBLORE, 5.5);
			}
		}
		fishId = getRandomFish(player);
	}

	private String getMessage(Item fish) {

		if (multipleCatch)
			return "Your high level in farming allows you to harvest two "
					+ fish.getDefinitions().getName().toLowerCase() + ".";
		else
			return "You manage to harvest a "
					+ fish.getDefinitions().getName().toLowerCase() + ".";
	}

	private boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Skills.HERBLORE) < spot.getHerbs()[fishId]
				.getLevel()) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a herblore level of "
							+ spot.getHerbs()[fishId].getLevel()
							+ " to harvest here.");
			return false;
		}
		if (!player.getInventory().containsOneItem(spot.getBait())
				&& spot.getBait() != -1) {
			player.getPackets().sendGameMessage(
					"You don't have "
							+ new Item(spot.getBait()).getDefinitions()
									.getName().toLowerCase()
							+ " to harvest here.");
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