package net.kagani.game.minigames.duel;

import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.Controller;

public class DuelControler extends Controller {

	@Override
	public void start() {
		/*
		 * sendInterfaces(); player.getPackets().sendPlayerOption("Challenge",
		 * 1, false);
		 */
	}

	@Override
	public boolean login() {
		start();
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void forceClose() {
		remove();
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
		remove();
	}

	@Override
	public void moved() {
		if (!isAtDuelArena(player)) {
			removeControler();
			remove();
		}
	}

	@Override
	public boolean canPlayerOption1(final Player target) {
		player.stopAll();
		if (target.getInterfaceManager().containsScreenInterface()
				|| target.isLocked()) {
			player.getPackets().sendGameMessage("The other player is busy.");
			return false;
		}
		if (player.isBeginningAccount() || target.isBeginningAccount()) {
			player.getPackets()
					.sendGameMessage(
							"Starter accounts cannot duel or be dueled until after at least one hour of playing time.");
			return false;
		}
		if (!player.getBank().hasVerified(10))
			return false;
		if (target.getTemporaryAttributtes().get("DuelChallenged") == player) {
			player.getControlerManager().removeControlerWithoutCheck();
			target.getControlerManager().removeControlerWithoutCheck();
			target.getTemporaryAttributtes().remove("DuelChallenged");
			player.setLastDuelRules(new DuelRules(player, target));
			target.setLastDuelRules(new DuelRules(target, player));
			player.getControlerManager().startControler("DuelArena", target,
					target.getTemporaryAttributtes().get("DuelFriendly"));
			target.getControlerManager().startControler("DuelArena", player,
					target.getTemporaryAttributtes().remove("DuelFriendly"));
			return false;
		}
		player.getTemporaryAttributtes().put("DuelTarget", target);
		player.getInterfaceManager().sendCentralInterface(640);
		player.getTemporaryAttributtes().put("WillDuelFriendly", true);
		player.getVarsManager().sendVar(283, 67108864);
		return false;
	}

	public static void challenge(Player player) {
		player.closeInterfaces();
		Boolean friendly = (Boolean) player.getTemporaryAttributtes().remove(
				"WillDuelFriendly");
		if (friendly == null)
			return;
		Player target = (Player) player.getTemporaryAttributtes().remove(
				"DuelTarget");
		if (target == null
				|| target.hasFinished()
				|| !target.withinDistance(player, 14)
				|| !(target.getControlerManager().getControler() instanceof DuelControler)) {
			player.getPackets().sendGameMessage(
					"Unable to find "
							+ (target == null ? "your target" : target
									.getDisplayName()));
			return;
		}
		player.getTemporaryAttributtes().put("DuelChallenged", target);
		player.getTemporaryAttributtes().put("DuelFriendly", friendly);
		player.getPackets().sendGameMessage(
				"Sending " + target.getDisplayName() + " a request...");
		target.getPackets().sendDuelChallengeRequestMessage(player, friendly);
	}

	public void remove() {
		player.getInterfaceManager().removeMinigameInterface();
		player.getPackets().sendPlayerOption("null", 1, false);
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendMinigameInterface(638);
	}

	public static boolean isAtDuelArena(Player player) {
		return /* player.withinArea(3341, 3265, 3387, 3281); */false;
	}
}