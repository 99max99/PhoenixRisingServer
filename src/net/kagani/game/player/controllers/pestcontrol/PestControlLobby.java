package net.kagani.game.player.controllers.pestcontrol;

import net.kagani.game.WorldObject;
import net.kagani.game.minigames.pest.Lander;
import net.kagani.game.player.controllers.Controller;
import net.kagani.utils.Utils;

public final class PestControlLobby extends Controller {

	private Lander lander;

	@Override
	public void start() {
		this.lander = Lander.getLanders()[(Integer) getArguments()[0]];
	}

	@Override
	public void sendInterfaces() {
		int remainingTime = lander.getTimer().getMinutes();
		player.getPackets().sendIComponentText(407, 3,
				Utils.fixChatMessage(lander.toString()));
		player.getPackets().sendIComponentText(
				407,
				13,
				"Next Departure: " + remainingTime + " minutes "
						+ (!(remainingTime % 2 == 0) ? " 30 seconds" : ""));
		player.getPackets().sendIComponentText(407, 14,
				"Player's Ready: " + lander.getByStanders().size());
		player.getPackets().sendIComponentText(407, 16,
				"Commendations: " + player.getCommendation());
		player.getInterfaceManager().sendMinigameInterface(407);
	}

	@Override
	public void magicTeleported(int teleType) {
		player.getControlerManager().forceStop();
	}

	@Override
	public boolean sendDeath() {
		player.getControlerManager().forceStop();
		return true;
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeMinigameInterface();
		lander.exit(player);
	}

	@Override
	public boolean logout() {
		lander.remove(player);
		return false;
	}

	@Override
	public boolean canSummonFamiliar() {
		player.getPackets()
				.sendGameMessage(
						"You feel it's best to keep your Familiar away during this game.");
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		switch (object.getId()) {
		case 14314:
		case 25629:
		case 25630:
			player.getDialogueManager().startDialogue("LanderD");
			return true;
		}
		return true;
	}
}
