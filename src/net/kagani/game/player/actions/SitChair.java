package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.construction.HouseConstants;

public class SitChair extends Action {

	private int chair;
	private WorldTile originalTile;
	private WorldTile chairTile;
	private boolean tped;

	public SitChair(Player player, int chair, WorldObject object) {
		this.chair = chair;
		this.originalTile = new WorldTile(player);
		chairTile = object;
		WorldTile face = new WorldTile(player);
		if (object.getType() == 10) {
			if (object.getRotation() == 0)
				face.moveLocation(0, -1, 0);
			else if (object.getRotation() == 2)
				face.moveLocation(0, 1, 0);
		} else if (object.getType() == 11) {
			if (object.getRotation() == 1)
				face.moveLocation(-1, 1, 0);
			else if (object.getRotation() == 2)
				face.moveLocation(1, 1, 0);
		}
		player.setNextFaceWorldTile(face);
	}

	@Override
	public boolean start(Player player) {
		setActionDelay(player, 1);
		return true;
	}

	@Override
	public boolean process(Player player) {
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (!tped) {
			player.setNextWorldTile(chairTile);
			tped = true;
		}
		player.setNextAnimation(new Animation(
				HouseConstants.CHAIR_EMOTES[chair]));
		return 0;
	}

	@Override
	public void stop(final Player player) {
		player.lock(1);
		player.setNextWorldTile(originalTile);
		player.setNextAnimation(new Animation(-1));
	}
}
