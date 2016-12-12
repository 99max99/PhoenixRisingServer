package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class AscensionBoltCreation extends Action {

	/**
	 * @author: Dylan Page
	 */

	private int itemId;

	public AscensionBoltCreation(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.setNextAnimation(new Animation(1249));
		setActionDelay(player, 3);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (checkAll(player))
			return true;
		return false;
	}

	private boolean checkAll(Player player) {
		if (!hasLevel(player))
			return false;
		if (!player.getInventory().containsItem(28436, 10))
			return false;
		return true;
	}

	private boolean hasLevel(Player player) {
		if (90 > player.getSkills().getLevel(Skills.FLETCHING)) {
			player.getPackets().sendGameMessage(
					"You need a fletching level of 90 to fletch these.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		player.getInventory().deleteItem(itemId, 10);
		player.getInventory().addItem(28465, 10);
		player.getSkills().addXp(Skills.FLETCHING, 200);
		player.setNextAnimation(new Animation(1249));
		return 3;
	}

	@Override
	public void stop(final Player player) {
		player.getEmotesManager().setNextEmoteEnd(2400);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(-1));
				player.getAppearence().setRenderEmote(-1);
			}
		}, 1);
	}
}