package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.WorldObject;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class Cleansing extends Action {

	/**
	 * @author: Dylan Page
	 */

	private WorldObject object;

	public Cleansing(WorldObject object) {
		this.object = object;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.setNextAnimation(new Animation(24556));
		setActionDelay(player, getDelay(player));
		return true;
	}

	private int getDelay(Player player) {
		int randomTime = 20;
		int timer = randomTime - (player.getSkills().getLevel(Skills.PRAYER));
		if (timer < 1 + randomTime)
			timer = 1 + Utils.random(randomTime);
		timer /= player.getAuraManager().getMininingAccurayMultiplier();
		return timer;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(24556));
		player.faceObject(object);
		if (checkAll(player)) {
			if (Utils.random(150) == 0)
				player.stopAll();
			return true;
		}
		return false;
	}

	private boolean checkAll(Player player) {
		if (!hasLevel(player))
			return false;
		if (!player.getInventory().containsItem(32615, 1)) {
			player.getDialogueManager()
					.startDialogue(
							"SimpleItemMessage",
							32615,
							"You will need to purchase a cleansing crystal from one of the monks in the Hefn Cathedral to cleanse the corruption from this Seren Stone.");
			return false;
		}
		return true;
	}

	private boolean hasLevel(Player player) {
		if (75 > player.getSkills().getLevel(Skills.PRAYER)) {
			player.getPackets().sendGameMessage(
					"You need a prayer level of at least 75.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		addXP(player);
		return getDelay(player);
	}

	private void addXP(Player player) {
		int xp = 0;
		switch (Utils.random(3)) {
		case 0:
			xp = 1400;
			break;
		case 1:
			xp = 10500;
			break;
		case 2:
			xp = 12000;
			break;
		}
		player.getSkills().addXp(Skills.PRAYER, xp);
		if (xp >= 10500)
			player.getInventory().deleteItem(32615, 1);
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