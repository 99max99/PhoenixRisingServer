package net.kagani.game.npc.others;

import net.kagani.game.Animation;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class FireSpirit extends NPC {

	private Player target;
	private long createTime;

	public FireSpirit(WorldTile tile, Player target) {
		super(15451, tile, -1, true, true);
		this.target = target;
		createTime = Utils.currentTimeMillis();
	}

	@Override
	public void processNPC() {
		if (target.hasFinished()
				|| createTime + 60000 < Utils.currentTimeMillis())
			finish();
	}

	public void giveReward(final Player player) {
		if (player != target || player.isLocked())
			return;
		player.lock();
		player.setNextAnimation(new Animation(16705));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.unlock();
				player.getInventory().addItem(
						new Item(12158, Utils.random(1, 6)));
				player.getInventory().addItem(
						new Item(12159, Utils.random(1, 6)));
				player.getInventory().addItem(
						new Item(12160, Utils.random(1, 6)));
				player.getInventory().addItem(
						new Item(12163, Utils.random(1, 6)));
				player.getPackets()
						.sendGameMessage(
								"The fire spirit gives you a reward to say thank you for freeing it, before disappearing.");
				finish();

			}

		}, 2);
	}

	@Override
	public boolean withinDistance(Player tile, int distance) {
		return tile == target && super.withinDistance(tile, distance);
	}

}
