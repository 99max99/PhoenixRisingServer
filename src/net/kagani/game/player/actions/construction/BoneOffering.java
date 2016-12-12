package net.kagani.game.player.actions.construction;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.content.construction.HouseConstants;
import net.kagani.game.player.content.prayer.Burying.Bone;

public class BoneOffering extends Action {

	private static final double[] BASE_ALTAR_PERCENT_BOOST = { 13D, 13.1D, 13.25D,
			13.5D, 13.75D, 14D, 14.5D };
	private static final Animation OFFERING_ANIMATION = new Animation(3705);
	private static final Graphics OFFERING_GRAPHICS = new Graphics(624, 0, 100);

	private double totalExperience;
	private final int litBurners;
	private final Bone bone;
	private final WorldObject altar;
	private int ticks;

	public BoneOffering(WorldObject altar, Bone bone, int litBurners) {
		this.altar = altar;
		this.bone = bone;
		this.litBurners = litBurners;
	}

	@Override
	public boolean start(Player player) {
		ticks = player.getInventory().getAmountOf(bone.getId());
		totalExperience = bone.getExperience()
				* (BASE_ALTAR_PERCENT_BOOST[HouseConstants.Builds.ALTAR
						.getSingleHObjectSlot(altar.getId())] + (litBurners > 2 ? 2
						: litBurners * 0.5));
		return true;
	}

	@Override
	public boolean process(Player player) {
		return ticks > 0;
	}

	@Override
	public int processWithDelay(Player player) {
		if (ticks > 0) {
			player.setNextAnimation(OFFERING_ANIMATION);
			World.sendGraphics(player, OFFERING_GRAPHICS, altar);
			player.getSkills().addXp(Skills.PRAYER, totalExperience);
			player.getInventory().deleteItem(bone.getId(), 1);
			ticks--;
			return 2;
		}
		return -1;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}
}
