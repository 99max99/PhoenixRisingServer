package net.kagani.game.player.content.dungeoneering;

import net.kagani.game.player.Player;

public class DungeonPartyPlayer {

	private Player player;
	private int deaths;

	public DungeonPartyPlayer(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void refreshDeaths() {
		player.getVarsManager().sendVarBit(2365, deaths); // deaths
	}

	public void increaseDeaths() {
		if (deaths == 15)
			return;
		deaths++;
		refreshDeaths();
	}
}
