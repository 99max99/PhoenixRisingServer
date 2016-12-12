package net.kagani.game.player.content.pports;

import net.kagani.game.player.controllers.Controller;

public class PlayerPortsController extends Controller {

	private PlayerPorts playerPorts;

	@Override
	public void start() {
		playerPorts = (PlayerPorts) getArguments()[0];
	}

	public PlayerPorts getPlayerPorts() {
		return playerPorts;
	}

	public void setPlayerPorts(PlayerPorts playerPorts) {
		this.playerPorts = playerPorts;
	}

}
