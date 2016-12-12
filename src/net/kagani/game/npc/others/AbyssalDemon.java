package net.kagani.game.npc.others;

import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class AbyssalDemon extends NPC {

	public AbyssalDemon(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		Entity target = getCombat().getTarget();
		if (target != null
				&& Utils.isOnRange(target.getX(), target.getY(),
						target.getSize(), getX(), getY(), getSize(), 4)
				&& Utils.random(50) == 0) {
			sendTeleport(target);
			sendTeleport(this);
		}
	}

	private void sendTeleport(Entity entity) {
		entity.setNextGraphics(new Graphics(409));
		entity.setNextWorldTile(Utils.getFreeTile(new WorldTile(entity), 1));
	}
}
