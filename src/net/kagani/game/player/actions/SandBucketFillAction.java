package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.player.Player;

public class SandBucketFillAction extends Action {

	public static final int EMPTY_BUCKET = 1925, BUCKET_OF_SAND = 1783;

	public SandBucketFillAction() {

	}

	@Override
	public boolean start(Player player) {
		return true;
	}

	@Override
	public boolean process(Player player) {
		return player.getInventory().containsItem(EMPTY_BUCKET, 1);
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimation(new Animation(895));
		player.getInventory().replaceItem(BUCKET_OF_SAND, 1,
				player.getInventory().getItems().getThisItemSlot(EMPTY_BUCKET));
		player.getPackets().sendGameMessage("You fill the bucket with sand.",
				true);
		return 3;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

}
