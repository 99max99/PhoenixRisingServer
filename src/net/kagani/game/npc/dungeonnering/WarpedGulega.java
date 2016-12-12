package net.kagani.game.npc.dungeonnering;

import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;

@SuppressWarnings("serial")
public class WarpedGulega extends DungeonBoss {

	public WarpedGulega(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
	}

	// thats default lol
	/*
	 * @Override public double getMeleePrayerMultiplier() { return 0.0;//Fully
	 * block it. }
	 * 
	 * @Override public double getRangePrayerMultiplier() { return 0.0;//Fully
	 * block it. }
	 * 
	 * @Override public double getMagePrayerMultiplier() { return 0.0;//Fully
	 * block it. }
	 */

	@Override
	public void processHit(Hit hit) {
		if (!(hit.getSource() instanceof Familiar))
			hit.setDamage((int) (hit.getDamage() * 0.45D));
		super.processHit(hit);
	}
}
