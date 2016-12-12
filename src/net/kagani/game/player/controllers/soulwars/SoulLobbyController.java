/**
 * @author 99max99 Magenz - Ventyz Productions
 *
 * SoulLobbyController.java created on 31.des.2014.
 */
package net.kagani.game.player.controllers.soulwars;

import net.kagani.game.WorldTile;
import net.kagani.game.minigames.soulwars.SoulLobby;
import net.kagani.game.player.controllers.Controller;

// TODO: Auto-generated Javadoc
/**
 * The Class SoulLobbyController.
 */
public class SoulLobbyController extends Controller {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.ventyz.server.model.character.controllers.Controller#start()
	 */
	@Override
	public void start() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kagani.game.player.controlers.Controler#login()
	 */

	@Override
	public boolean login() {
		if (player.didPassBlue) {
			SoulLobby.removeBlue(player);
		} else if (player.didPassRed) {
			SoulLobby.removeRed(player);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.kagani.game.player.controlers.Controler#logout()
	 */
	@Override
	public boolean logout() {
		if (player.didPassBlue) {
			SoulLobby.removeBlue(player);
		}
		if (player.didPassRed) {
			SoulLobby.removeRed(player);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.kagani.game.player.controlers.Controler#processMagicTeleport(net.
	 * MaxScape830.game.WorldTile)
	 */

	// TODO object click for exit

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager()
				.startDialogue("SimpleMessage",
						"A magical force prevents you from teleporting from the arena.");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.ventyz.server.model.character.controllers.Controller#processItemTeleport
	 * (net.ventyz.server.world.WorldTile)
	 */
	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager()
				.startDialogue("SimpleMessage",
						"A magical force prevents you from teleporting from the arena.");
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.ventyz.server.model.character.controllers.Controller#magicTeleported
	 * (int)
	 */
	@Override
	public void magicTeleported(int type) {
		player.getControlerManager().forceStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.ventyz.server.model.character.controllers.Controller#processButtonClick
	 * (int, int, int, int)
	 */
	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (interfaceId == 193 && componentId == 48) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You cant teleport during SoulWars.");
			return false;
		}
		if (interfaceId == 192 && componentId == 24) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You cant teleport during SoulWars.");
			return false;
		}
		if (interfaceId == 182 && componentId == 13) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Please leave SoulWars before doing this.");
			return false;
		}
		if (interfaceId == 182 && componentId == 6) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Please leave SoulWars before doing this.");
			return false;
		}
		return true;
	}

}
