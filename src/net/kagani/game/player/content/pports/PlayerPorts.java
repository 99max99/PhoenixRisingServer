package net.kagani.game.player.content.pports;

import net.kagani.game.WorldTile;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class PlayerPorts {

	private Player player;
	public int[] boundChuncks;
	public boolean insidePort;

	public void initalizePort() {
		if (!isInsidePort()) {
			player.getControlerManager().startControler(
					"PlayerPortsController", this);
			boundChuncks = MapBuilder.findEmptyRegionBound(12, 14);
			MapBuilder.copyAllPlanesMap(1534, 911, boundChuncks[0],
					boundChuncks[1], 12, 14);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(new WorldTile(
							boundChuncks[0] * 0 + 96,
							boundChuncks[1] * 0 + 110, 0));
				}

			});
		}
	}

	public boolean isInsidePort() {
		return insidePort;
	}

	public boolean setInsidePort(boolean value) {
		return insidePort = value;
	}

	public int[] getBoundChuncks() {
		return boundChuncks;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
}