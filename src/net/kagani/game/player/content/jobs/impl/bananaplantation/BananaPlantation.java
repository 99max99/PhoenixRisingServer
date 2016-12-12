package net.kagani.game.player.content.jobs.impl.bananaplantation;

import java.io.Serializable;

import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.jobs.Job;
import net.kagani.game.player.content.jobs.JobReward;
import net.kagani.game.player.content.jobs.RewardType;

/**
 * @Author arrow
 * @Contact<arrowrsps@gmail.com;skype:arrowrsps>
 */
public class BananaPlantation extends Job implements Serializable {

	protected Player player;

	public BananaPlantation(Player player) {
		super(player);
	}

	@Override
	public JobReward[] getRewards() {
		return new JobReward[] { new JobReward(RewardType.ITEM, new Item(995,
				30)) };
	}
}
