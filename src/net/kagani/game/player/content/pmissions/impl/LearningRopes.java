package net.kagani.game.player.content.pmissions.impl;

import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.pmissions.Mission;
import net.kagani.game.player.content.pmissions.MissionReward;
import net.kagani.game.player.content.pmissions.MissionConstants.MissionType;
import net.kagani.game.player.content.pmissions.MissionConstants.RewardType;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class LearningRopes extends Mission {

	protected Player player;

	public LearningRopes(Player player) {
		super(player);
	}

	@Override
	public String getMissionName() {
		return "Learning the ropes";
	}

	@Override
	public int getTotalLevelRequirement() {
		return 1125;
	}

	@Override
	public MissionType getMissionType() {
		return MissionType.DAILY;
	}

	@Override
	public int getExperienceRateMultiplier() {
		return 3;
	}

	@Override
	public MissionReward[] getPossibleRewards() {
		return new MissionReward[] { new MissionReward(RewardType.EXPERIENCE,
				Skills.FISHING, 1231) };
	}

}
