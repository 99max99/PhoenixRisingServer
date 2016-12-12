package net.kagani.game.npc.fightpits;

import java.util.ArrayList;

import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.minigames.FightPits;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class FightPitsNPC extends NPC {

	public FightPitsNPC(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		setNoDistanceCheck(true);
	}

	@Override
	public void sendDeath(Entity source) {
		setNextGraphics(new Graphics(2924 + getSize()));
		super.sendDeath(source);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (Player player : FightPits.arena)
			possibleTarget.add(player);
		return possibleTarget;
	}

}
