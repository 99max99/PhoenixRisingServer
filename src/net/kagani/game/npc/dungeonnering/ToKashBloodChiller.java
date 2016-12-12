package net.kagani.game.npc.dungeonnering;

import net.kagani.game.WorldTile;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;

@SuppressWarnings("serial")
public class ToKashBloodChiller extends DungeonBoss {

	private boolean specialAttack;

	public ToKashBloodChiller(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 1.5;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 1.5;
	}

	public void setSpecialAttack(boolean specialAttack) {
		this.specialAttack = specialAttack;
	}

	public boolean canSpecialAttack() {
		return !specialAttack;
	}
}
