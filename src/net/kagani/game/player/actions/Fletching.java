package net.kagani.game.player.actions;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;

public class Fletching extends Action {

	private static final int LEVEL_OPCODE = 2645, BASE_OPCODE = 2655,
			PERMENANT_SECONDARY_OPCODE = 2650,
			REMOVEABLE_SECONDARY_OPCODE = 2656, EXPERIENCE_OPCODE = 2697,
			CREATION_COUNT_OPCODE = 2653;

	public static final int KNIFE = 946, CHISLE = 1755, BOW_STRING = 1777,
			CROSSBOW_STRING = 9438;

	public static final int DUNGEONEERING_KNIFE = 17754,
			DUNGEEONEERING_BOW_STRING = 17752, DUNGEONEERING_HEADLESS = 17747;

	/*
	 * private static final int[] PRODUCTS = { 2861, 52, 53, 54, 60, 64, 68, 72,
	 * 806, 839, 841, 843, 847, 849, 851, 853, 855, 857, 859, 861, 877, 879,
	 * 882, 2864, 2866, 9174, 9446, 9450, 10158, 16867, 16427, 17742, 25982,
	 * 25983, 25984, 25985, 25986, 25987, 25988, 25989, 25990, 17747, 21364,
	 * 21582, 24122, 29736, 30574 };
	 */

	private static final int[] PRODUCTS = { 2861, 52, 53, 48, 50, 54, 56, 58,
			60, 62, 64, 66, 68, 70, 72, 806, 839, 841, 843, 845, 847, 849, 851,
			853, 855, 857, 859, 861, 2864, 2866, 9174, 9446, 9450, 10158,
			16867, 16427, 17742, 25982, 25983, 25984, 25985, 25986, 25987,
			25988, 25989, 25990, 17747, 21364, 21582, 24122, 29736, 30574 // BOWS
	// STRINGING
	// ID
	};

	private final FletchData data;
	private int product, cycles, incrementPerCycle, level;
	private double experience;

	public static Animation getAnimationItem(int itemId) {
		if (itemId == 48 || itemId == 50 || itemId == 54 || itemId == 56
				|| itemId == 58 || itemId == 60 || itemId == 62 || itemId == 64
				|| itemId == 66 || itemId == 68 || itemId == 70 || itemId == 72)
			return new Animation(1248);

		else if (itemId == 839)
			return new Animation(6678);
		else if (itemId == 841)
			return new Animation(6684);
		else if (itemId == 843)
			return new Animation(6679);
		else if (itemId == 845)
			return new Animation(6685);
		else if (itemId == 847)
			return new Animation(6680);
		else if (itemId == 849)
			return new Animation(6686);
		else if (itemId == 851)
			return new Animation(6681);
		else if (itemId == 853)
			return new Animation(6687);
		else if (itemId == 855)
			return new Animation(6682);
		else if (itemId == 857)
			return new Animation(6688);
		else if (itemId == 859)
			return new Animation(6683);
		else if (itemId == 861)
			return new Animation(6689);

		return new Animation(-1);
	}

	/*
	 * 
	 * 
	 * STRUNG_MAGIC_LONG_BOW(70, 1777, new int[] { 859 }, new int[] { 85 }, new
	 * double[] { 91.5 }, new Animation(6689)),
	 */

	public Fletching(FletchData data, int product, int cycles) {
		this.data = data;
		this.product = product;
		this.cycles = cycles;
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(product);
		this.incrementPerCycle = defs.getCSOpcode(CREATION_COUNT_OPCODE);
		this.level = defs.getCSOpcode(LEVEL_OPCODE);
		this.experience = defs.getCSOpcode(EXPERIENCE_OPCODE) / 10.0;
	}

	public boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Skills.FLETCHING) < level)
			return false;
		return continueNextCycle(player);
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		return true;
	}

	@Override
	public boolean process(Player player) {
		return continueNextCycle(player) && cycles > 0;
	}

	private boolean continueNextCycle(Player player) {
		return player.getInventory().containsItemToolBelt(data.getSecondary())
				&& player.getInventory().containsItem(data.getNode(), 1);
	}

	@Override
	public int processWithDelay(Player player) {
		cycles--;
		player.setNextAnimation(getAnimationItem(product));
		boolean isShaft = product == 52;
		if (incrementPerCycle > 0) {
			/*
			 * Double Cheking.
			 */
			if (data.canDeleteSecondary()) {
				int nodeCount = player.getInventory().getAmountOf(
						data.getNode());
				int secondaryCount = player.getInventory().getAmountOf(
						data.getSecondary());
				if (!isShaft) {
					if (nodeCount < incrementPerCycle)
						incrementPerCycle = nodeCount;
					if (secondaryCount < incrementPerCycle)
						incrementPerCycle = secondaryCount;
				}
			}
		} else
			incrementPerCycle = 1;

		boolean isStringing = false;
		boolean deletedSecondary = false;

		if (data.canDeleteSecondary()
				|| player.getInventory().containsItem(1777, 1)) {
			isStringing = true;
			deletedSecondary = true;
			player.getInventory().deleteItem(1777, 1);
		}

		if (data.getNode() != data.getSecondary() && !deletedSecondary)
			player.getInventory().deleteItem(data.getSecondary(), 1);

		player.getInventory().deleteItem(data.getNode(),
				isShaft ? 1 : incrementPerCycle);

		player.getSkills().addXp(Skills.FLETCHING,
				experience * incrementPerCycle);

		if (!isStringing)
			player.getInventory().addItem(
					product,
					incrementPerCycle
							* (incrementPerCycle != 1 ? Settings
									.getCraftRate(player) : incrementPerCycle));
		else
			player.getInventory().addItem(
					getProduct(data.getNode()),
					incrementPerCycle
							* (incrementPerCycle != 1 ? Settings
									.getCraftRate(player) : incrementPerCycle));

		// You carefully cut the wood into a Shieldbow (u).
		// You add a string to the bow.

		return 4;
	}

	// 48, 50, 54, 56, 58, 60, 62, 64, 66, 68, 70, 72
	// 839, 841, 843, 845, 847, 849, 851, 853, 855, 857, 859, 861
	public int getProduct(int itemId) {
		if (itemId == 48)
			return 839;
		else if (itemId == 50)
			return 841;
		else if (itemId == 54)
			return 843;
		else if (itemId == 56)
			return 845;
		else if (itemId == 58)
			return 847;
		else if (itemId == 60)
			return 849;
		else if (itemId == 62)
			return 851;
		else if (itemId == 64)
			return 853;
		else if (itemId == 66)
			return 855;
		else if (itemId == 68)
			return 857;
		else if (itemId == 70)
			return 859;
		else if (itemId == 72)
			return 861;
		else if (itemId == 28436)
			return 28465;
		return -1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

	public static FletchData isFletching(int material) {
		for (int product : PRODUCTS) {
			ItemDefinitions defs = ItemDefinitions.getItemDefinitions(product);
			int secondary = defs.getCSOpcode(PERMENANT_SECONDARY_OPCODE);
			if (secondary == 0)
				continue;
			int nodeId = defs.getCSOpcode(BASE_OPCODE);
			if (nodeId == material)
				return new FletchData(product, nodeId, secondary,
						secondary == 0);
		}
		return null;
	}

	public static FletchData isFletchingCombination(Item node, Item secondary) {
		for (int product : PRODUCTS) {
			ItemDefinitions defs = ItemDefinitions.getItemDefinitions(product);
			int nodeId = defs.getCSOpcode(BASE_OPCODE);
			int fixedSecondaryId = defs.getCSOpcode(PERMENANT_SECONDARY_OPCODE);
			int removeableSecondaryId = defs
					.getCSOpcode(REMOVEABLE_SECONDARY_OPCODE);
			if (fixedSecondaryId == 0 && removeableSecondaryId == 0)
				removeableSecondaryId = 314;// Feather isn't in cache idk why
			if (node.getId() == nodeId
					&& (secondary.getId() == fixedSecondaryId || secondary
							.getId() == removeableSecondaryId))
				return new FletchData(product, nodeId, secondary.getId(),
						fixedSecondaryId == 0);
			else if ((node.getId() == fixedSecondaryId || node.getId() == removeableSecondaryId)
					&& secondary.getId() == nodeId)
				return new FletchData(product, secondary.getId(), nodeId,
						fixedSecondaryId == 0);
		}
		return null;
	}

	public static class FletchData {
		private int product;
		private final int node;
		private final int secondary;
		private final boolean deleteSecondary;

		public FletchData(int product, int node, int secondary,
				boolean deleteSecondary) {
			this.product = product;
			this.node = node;
			this.secondary = secondary;
			this.deleteSecondary = deleteSecondary;
		}

		public int getProduct() {
			return product;
		}

		public int getNode() {
			return node;
		}

		public int getSecondary() {
			return secondary;
		}

		public boolean canDeleteSecondary() {
			return deleteSecondary;
		}
	}
}