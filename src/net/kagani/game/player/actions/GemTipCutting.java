package net.kagani.game.player.actions;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.utils.Utils;

public class GemTipCutting extends Action {
	
	/**
	 * @author: Dylan Page
	 */

	public enum GemTips {
		
		OPAL(1609, 15.0, 1, 886, 45),

		JADE(1611, 20, 13, 886, 9187),

		RED_TOPAZ(1613, 25, 16, 887, 9188),

		SAPPHIRE(1607, 50, 20, 888, 9189),

		EMERALD(1605, 67, 27, 889, 9190),

		RUBY(1603, 85, 34, 887, 9191),

		DIAMOND(1601, 107.5, 43, 890, 9192),

		DRAGONSTONE(1615, 137.5, 55, 885, 9193),

		ONYX(6573, 167.5, 67, 2717, 9194),

		HYDRIX(31855, 197.5, 79, 2717, 31867);

		private double experience;
		private int levelRequired;
		private int cut;

		private int emote;
		private int boltTips;

		private GemTips(int cut, double experience, int levelRequired, int emote, int boltTips) {
			this.cut = cut;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.emote = emote;
			this.boltTips = boltTips;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public double getExperience() {
			return experience;
		}

		public int getCut() {
			return cut;
		}

		public int getEmote() {
			return emote;
		}

		public int getBoltTips() {
			return boltTips;
		}

	}

	public static void cut(Player player, GemTips gem) {
		player.getActionManager().setAction(new GemTipCutting(gem, player.getInventory().getAmountOf(gem.getCut())));
	}

	private GemTips gem;
	private int quantity;

	public GemTipCutting(GemTips gem, int quantity) {
		this.gem = gem;
		this.quantity = quantity;
	}

	public boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Skills.CRAFTING) < gem.getLevelRequired()) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need a crafting level of " + gem.getLevelRequired() + " to cut that gem.");
			return false;
		}
		if (!player.getInventory().containsOneItem(gem.getCut())) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You don't have any "
					+ ItemDefinitions.getItemDefinitions(gem.getCut()).getName().toLowerCase() + " to cut.");
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			setActionDelay(player, 1);
			player.setNextAnimation(new Animation(gem.getEmote()));
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.getInventory().deleteItem(gem.getCut(), 1);
		player.getInventory().addItem(gem.getBoltTips(), Utils.random(10, 15));
		player.getSkills().addXp(Skills.CRAFTING, gem.getExperience());
		player.getPackets().sendGameMessage(
				"You cut the " + ItemDefinitions.getItemDefinitions(gem.getCut()).getName().toLowerCase() + ".", true);
		quantity--;
		if (quantity <= 0)
			return -1;
		player.setNextAnimation(new Animation(gem.getEmote()));
		return 0;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}