package net.kagani.game.npc.others;

import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.controllers.Barrows;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class BarrowsBrother extends NPC {

	private Barrows barrows;

	public BarrowsBrother(int id, WorldTile tile, Barrows barrows) {
		super(id, tile, -1, true, true);
		this.barrows = barrows;
		setIntelligentRouteFinder(true);
	}

	@Override
	public void sendDeath(Entity source) {
		if (barrows != null) {
			barrows.targetDied();
			barrows = null;
		}
		super.sendDeath(source);
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return getId() != 2030 ? 0 : Utils.random(3) == 0 ? 1 : 0;
	}

	public void disapear() {
		barrows = null;
		finish();
	}

	@Override
	public void finish() {
		if (hasFinished())
			return;
		if (barrows != null) {
			barrows.targetFinishedWithoutDie();
			barrows = null;
		}
		super.finish();
	}

}
