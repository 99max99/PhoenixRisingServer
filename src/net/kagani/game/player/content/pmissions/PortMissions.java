package net.kagani.game.player.content.pmissions;

import java.io.Serializable;

import net.kagani.game.player.Player;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class PortMissions implements Serializable {

	private static final long serialVersionUID = -488162445578633689L;
	private transient Player player;

	public PortMissions(Player player) {
		this.setPlayer(player);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
