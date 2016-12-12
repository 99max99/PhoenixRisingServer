package net.kagani.game.npc.dungeonnering;

import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.impl.dung.ToKashBloodChillerCombat;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class FrozenAdventurer extends NPC {

	private transient Player player;

	public FrozenAdventurer(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, true);
	}

	@Override
	public void processNPC() {
		if (player == null || player.isDead() || player.hasFinished()) {
			finish();
			return;
		} else if (!player.getAppearence().isNPC()) {
			ToKashBloodChillerCombat.removeSpecialFreeze(player);
			finish();
			return;
		}
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Player getFrozenPlayer() {
		return player;
	}

}
