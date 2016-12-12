package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.WorldObject;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Harps extends Action {

	/**
	 * @author: Dylan Page
	 */

	private WorldObject object;

	public Harps(WorldObject object) {
		this.object = object;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.setNextAnimation(new Animation(25022));
		setActionDelay(player, getDelay(player));
		return true;
	}

	private int getDelay(Player player) {
		int randomTime = 20;
		int timer = randomTime - (player.getSkills().getLevel(Skills.CRAFTING));
		if (timer < 1 + randomTime)
			timer = 1 + Utils.random(randomTime);
		timer /= player.getAuraManager().getMininingAccurayMultiplier();
		return timer;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(25021));
		player.faceObject(object);
		if (checkAll(player)) {
			if (Utils.random(220) == 0) {
				player.stopAll();
			}
			return true;
		}
		return false;
	}

	private boolean checkAll(Player player) {
		if (!hasLevel(player)) {
			return false;
		}
		return true;
	}

	private boolean hasLevel(Player player) {
		if (75 > player.getSkills().getLevel(Skills.CRAFTING)) {
			player.getPackets().sendGameMessage(
					"You need a crafting level of 75 to play on this harp.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (Utils.random(3) == 0) {
			addXP(player);
		}
		return getDelay(player);
	}

	private void addXP(Player player) {
		player.getSkills().addXp(Skills.CRAFTING, 95);
		if (Utils.random(2) < 1) {
			int amount = Utils.random(7, 8);
			player.getInventory().addItem(32622, amount);
		}
	}

	@Override
	public void stop(final Player player) {
		player.getEmotesManager().setNextEmoteEnd(2400);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(16702));
				player.getAppearence().setRenderEmote(-1);
			}
		}, 3);
	}
}