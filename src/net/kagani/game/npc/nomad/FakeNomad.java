package net.kagani.game.npc.nomad;

import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;

@SuppressWarnings("serial")
public class FakeNomad extends NPC {

	private Nomad nomad;

	public FakeNomad(WorldTile tile, Nomad nomad) {
		super(8529, tile, -1, true, true);
		this.nomad = nomad;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		nomad.destroyCopy(this);
	}

}
