package net.kagani.game.npc.others;

import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.minigames.ShootingStars;
import net.kagani.game.npc.NPC;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class StarSprite extends NPC {

	public StarSprite() {
		super(ShootingStars.SHADOW, ShootingStars.getNewLocation(), -1, true,
				true);
		this.setDirection(Utils.getAngle(-1, 0));
	}

	private WorldObject star;
	private int cycle;
	private boolean firstClick;
	private int starLife;
	private int visibleCycle;

	@Override
	public void processNPC() {
		if (cycle == 20) {
			setStar(38659);
			setNextNPCTransformation(ShootingStars.INVISIBLE);
		} else if (cycle == 22) {
			setStar(38660 + Utils.random(9));
			ShootingStars.generateNextLocation();
		} else if (cycle == ShootingStars.STAR_FALL_TIME) { // 2 hours
			cycle = 0;
			visibleCycle = 0;
			setNextWorldTile(ShootingStars.getNewLocation());
			setNextNPCTransformation(ShootingStars.SHADOW);
			removeStar();
			firstClick = false;
		} else if (getId() == ShootingStars.SPRITE) {
			visibleCycle++;
			if (visibleCycle == 1000) // 10min max to talk with it
				setNextNPCTransformation(ShootingStars.INVISIBLE);
		}
		cycle++;
	}

	public boolean isReady() {
		return cycle > 22 && getId() == ShootingStars.INVISIBLE;
	}

	public int getStarSize() {
		return star == null ? 0 : 38669 - star.getId();
	}

	public int getMiningLevel() {
		return getStarSize() * 10;
	}

	public void openStar() {
		setNextNPCTransformation(ShootingStars.SPRITE);
		removeStar();
	}

	public boolean isFirstClick() {
		return firstClick;
	}

	public void setFirstClick() {
		firstClick = true;
	}

	public void setStar(int id) {
		star = new WorldObject(id, 10, 0, getX(), getY(), getPlane());
		World.spawnObject(star);
		if (star.getId() != 38659)
			starLife = ShootingStars.STAR_DUST_Q[getStarSize() - 1]; // 1000
																		// dust
																		// per
																		// layer
																		// atm
	}

	public int getMinedPerc() {
		return 100 - (100 * starLife / ShootingStars.STAR_DUST_Q[getStarSize() - 1]);
	}

	public void reduceStarLife() {
		starLife--;
		if (starLife == 0) {
			if (star.getId() == 38668)
				openStar();
			else
				setStar(star.getId() + 1);
		}

	}

	public int getStarCycle() {
		return cycle;
	}

	public void removeStar() {
		if (star == null)
			return;
		World.removeObject(star);
		star = null;
	}
}
