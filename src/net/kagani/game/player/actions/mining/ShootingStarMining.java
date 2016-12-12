package net.kagani.game.player.actions.mining;

import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.minigames.ShootingStars;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;

public class ShootingStarMining extends MiningBase {

	private WorldObject rock;
	private PickAxeDefinitions axeDefinitions;

	public ShootingStarMining(WorldObject rock) {
		this.rock = rock;
	}

	@Override
	public boolean start(Player player) {
		axeDefinitions = getPickAxeDefinitions(player, false);
		if (!checkAll(player))
			return false;
		player.getPackets().sendGameMessage(
				"You swing your pickaxe at the rock.");
		setActionDelay(player, getMiningDelay(player));
		return true;
	}

	private int getMiningDelay(Player player) {
		return ShootingStars.getStarSize() * 2;
	}

	private boolean checkAll(Player player) {
		if (axeDefinitions == null) {
			player.getPackets()
					.sendGameMessage(
							"You do not have a pickaxe or do not have the required level to use the pickaxe.");
			return false;
		}
		if (!hasMiningLevel(player))
			return false;
		if (!player.getInventory().hasFreeSlots()) {
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return false;
		}
		return true;
	}

	private boolean hasMiningLevel(Player player) {
		int level = ShootingStars.getLevel();
		if (level > player.getSkills().getLevel(Skills.MINING)) {
			player.getPackets().sendGameMessage(
					"You need a mining level of " + level
							+ " to mine this rock.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(axeDefinitions.getAnimationId()));
		return checkRock(player);
	}

	@Override
	public int processWithDelay(Player player) {
		addOre(player);
		if (!player.getInventory().hasFreeSlots()
				&& !player.getInventory().containsItem(ShootingStars.STARDUST,
						1)) {
			player.setNextAnimation(new Animation(-1));
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			return -1;
		}
		return getMiningDelay(player);
	}

	private void addOre(Player player) {
		player.getSkills().addXp(Skills.MINING, ShootingStars.getXP());
		if (!player.getInventory().containsItem(ShootingStars.STARDUST, 200))
			player.getInventory().addItem(ShootingStars.STARDUST, 1);
		player.getPackets().sendGameMessage("You mine some stardust.", true);
		ShootingStars.reduceStarLife();
	}

	private boolean checkRock(Player player) {
		return World.containsObjectWithId(rock, rock.getId());
	}
}