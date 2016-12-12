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

public class DivineHunting extends Action {

	public enum Herbs {

		polar_kebbit_fur(10117, 1, 60),

		raw_beast_meat(9986, 1, 60),

		common_kebbit_fur(10121, 3, 74),

		feldip_weasel_fur(10119, 7, 94),

		desert_devil_fur(10123, 13, 132),

		long_kebbit_spike(10107, 49, 700),

		red_feathers(10088, 1, 68),

		raw_bird_meat(9978, 1, 68),

		yellow_feathers(10090, 5, 94),

		orange_feathers(10091, 9, 122),

		blue_feathers(10089, 11, 130),

		stripy_feathers(10087, 19, 180),

		wimpy_feathers(11525, 39, 251),

		kebbit_claws(10113, 23, 148),

		barb_tail_harpoon(10129, 33, 168),

		kebbit_spike(10105, 37, 202),

		diseased_kebbit_fur(12567, 44, 200),

		kebbit_teeth(12567, 51, 200),

		Chinchompas(10033, 53, 80),

		red_chinchompas(10034, 63, 106),

		raw_pawya_meat(12535, 66, 160),

		grenwall_spikes(12539, 77, 440),

		;

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

	public enum DivineHuntingSpots {

		DIVINE_KEBBIT_BURROW(87270, 1, -1, new Animation(21236), 10,
				Herbs.polar_kebbit_fur, Herbs.raw_beast_meat,
				Herbs.common_kebbit_fur, Herbs.feldip_weasel_fur,
				Herbs.desert_devil_fur, Herbs.long_kebbit_spike),

		DIVINE_BIRD_SNARE(87271, 1, -1, new Animation(21236), 10,
				Herbs.red_feathers, Herbs.raw_bird_meat, Herbs.yellow_feathers,
				Herbs.orange_feathers, Herbs.blue_feathers,
				Herbs.stripy_feathers, Herbs.wimpy_feathers),

		DIVINE_DEADFALL_TRAP(87272, 1, -1, new Animation(21236), 10,
				Herbs.kebbit_claws, Herbs.barb_tail_harpoon,
				Herbs.kebbit_spike, Herbs.diseased_kebbit_fur,
				Herbs.kebbit_teeth),

		DIVINE_BOX_TRAP(87273, 1, -1, new Animation(21236), 10,
				Herbs.Chinchompas, Herbs.red_chinchompas, Herbs.raw_pawya_meat,
				Herbs.grenwall_spikes);

		private final Herbs[] herbs;
		private final int id, option, bait;
		private final Animation animation;

		static final Map<Integer, DivineHuntingSpots> spot = new HashMap<Integer, DivineHuntingSpots>();

		public static DivineHuntingSpots forId(int id) {
			return spot.get(id);
		}

		static {
			for (DivineHuntingSpots spots : DivineHuntingSpots.values())
				spot.put(spots.id | spots.option << 24, spots);
		}
		private int randomLifeProbability;

		private DivineHuntingSpots(int id, int option, int bait,
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

	private DivineHuntingSpots spot;
	WorldObject spott;
	private WorldTile tile;
	private int fishId;

	private boolean multipleCatch;

	public DivineHunting(DivineHuntingSpots spot, int x, int y, int plane,
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
				"You start hunting from the divine.", true);
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
		int difference = player.getSkills().getLevel(Skills.HUNTER)
				- spot.getHerbs()[random].getLevel();
		if (difference < -1)
			return random = 0;
		if (random < -1)
			return random = 0;
		return random;
	}

	@Override
	public int processWithDelay(Player player) {
		addItem(player);
		if (Utils.random(spot.getRandomLifeProbability()) == 0) {
			player.stopAll();
			player.setNextAnimation(new Animation(-1));
			return -1;
		}
		return getFishingDelay(player);
	}

	private void addItem(Player player) {
		Item hitem = new Item(spot.getHerbs()[fishId].getId(),
				multipleCatch ? 2 : 1);
		Item hnitem = new Item(spot.getHerbs()[fishId].getId() + 1,
				multipleCatch ? 2 : 1);
		double totalXp = spot.getHerbs()[fishId].getXp();
		player.getSkills().addXp(Skills.HUNTER, totalXp);

		int roll = Utils.random(100);
		Player owner = spott.getOwner();
		if (player == owner && roll >= 60) {
			player.getInventory().addItem(hitem);
			String name = hitem.getDefinitions().getName().toLowerCase();
			player.getPackets().sendGameMessage("You caught a " + name + ".",
					true);
		} else {
			String name = hitem.getDefinitions().getName().toLowerCase();
			owner.getInventory().addItem(hnitem);
			owner.getPackets().sendGameMessage(
					"" + player.getUsername() + " caught some " + name
							+ " for you.");
			player.getInventory().addItem(hitem);
			player.getPackets().sendGameMessage(
					"You caught some " + name + ".", true);
		}
		player.getPackets().sendGameMessage(getMessage(hitem), true);
		if (player.getFamiliar() != null) {
			if (Utils.random(50) == 0
					&& getSpecialFamiliarBonus(player.getFamiliar().getId()) > 0) {
				player.getInventory().addItem(hitem);
				player.getSkills().addXp(Skills.HUNTER, 5.5);
			}
		}
		fishId = getRandomFish(player);
	}

	private String getMessage(Item hunt) {

		if (multipleCatch)
			return "Your high level in hunter allows you to catch two "
					+ hunt.getDefinitions().getName().toLowerCase() + ".";
		else
			return "You manage to catch a "
					+ hunt.getDefinitions().getName().toLowerCase() + ".";
	}

	private boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Skills.HUNTER) < spot.getHerbs()[fishId]
				.getLevel()) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a hunter level of "
							+ spot.getHerbs()[fishId].getLevel()
							+ " to hunt here.");
			return false;
		}
		if (!player.getInventory().containsOneItem(spot.getBait())
				&& spot.getBait() != -1) {
			player.getPackets()
					.sendGameMessage(
							"You don't have "
									+ new Item(spot.getBait()).getDefinitions()
											.getName().toLowerCase()
									+ " to hunt here.");
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
			player.setNextAnimation(new Animation(-1));
			return false;

		}
		return true;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}