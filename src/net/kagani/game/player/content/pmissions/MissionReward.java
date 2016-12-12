package net.kagani.game.player.content.pmissions;

import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.pmissions.MissionConstants.RewardType;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class MissionReward {

	private final RewardType type;
	private final Object[] data;

	public MissionReward(RewardType type, int... data) {
		Object[] o = new Object[data.length];
		for (int i = 0; i < o.length; i++)
			o[i] = data[i];
		this.type = type;
		this.data = o;
	}

	public MissionReward(RewardType type, Item item) {
		Object[] o = { item };
		this.type = type;
		this.data = o;
	}

	public MissionReward(RewardType type, String item) {
		Object[] o = { item };
		this.type = type;
		this.data = o;
	}

	public MissionReward(RewardType type, Object[]... data) {
		this.type = type;
		this.data = data;
	}

	public void reward(Player player) {

	}

	public RewardType getType() {
		return type;
	}

	public Object[] getData() {
		return data;
	}

}
