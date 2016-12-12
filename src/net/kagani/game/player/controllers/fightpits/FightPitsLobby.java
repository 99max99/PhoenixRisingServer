package net.kagani.game.player.controllers.fightpits;

import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.FloorItem;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.FightPits;
import net.kagani.game.player.actions.ViewingOrb;
import net.kagani.game.player.controllers.Controller;

public class FightPitsLobby extends Controller {

	public static final WorldTile[] ORB_TELEPORTS = {
			new WorldTile(4571, 5092, 0), new WorldTile(4571, 5107, 0),
			new WorldTile(4590, 5092, 0), new WorldTile(4571, 5077, 0),
			new WorldTile(4557, 5092, 0) };

	@Override
	public void start() {

	}

	@Override
	public boolean login() {
		FightPits.enterLobby(player, true);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		FightPits.leaveLobby(player, 2);
	}

	@Override
	public boolean canTakeItem(FloorItem item) {
		return false;
	}

	@Override
	public boolean canDropItem(Item item) {
		return false;
	}

	// fuck it dont dare touching here again or dragonkk(me) kills u irl :D btw
	// nice code it keeps nulling, fixed

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 68223) {
			FightPits.leaveLobby(player, 1);
			return false;
		} else if (object.getId() == 68222) {
			player.getPackets().sendGameMessage(
					"The heat prevents you passing through.");
			return false;
		} else if (object.getId() == 68220) {
			player.getActionManager().setAction(new ViewingOrb(ORB_TELEPORTS));
			return false;
		}
		return true;
	}

	/**
	 * return let default death
	 */
	@Override
	public boolean sendDeath() {
		// if somehow dies on lobby example poisoned
		FightPits.leaveLobby(player, 2);
		return true;
	}

	@Override
	public boolean logout() {
		FightPits.leaveLobby(player, 0);
		return false;
	}

	@Override
	public void forceClose() {
		FightPits.leaveLobby(player, 2);
	}

}
