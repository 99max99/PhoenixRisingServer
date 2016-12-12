package net.kagani.game.player.controllers;

import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonManager;

public class Kalaboss extends Controller {

	private boolean showingOption;

	@Override
	public void start() {
		setInviteOption(true);
	}

	@Override
	public boolean canPlayerOption1(Player target) {
		player.setNextFaceWorldTile(target);
		player.getDungManager().invite(target.getDisplayName());
		return false;
	}

	@Override
	public boolean login() {
		moved();
		DungeonManager.checkRejoin(player);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		setInviteOption(false);
		player.getDungManager().leaveParty();
		removeControler();
	}

	@Override
	public boolean sendDeath() {
		setInviteOption(false);
		player.getDungManager().leaveParty();
		removeControler();
		return true;
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void forceClose() {
		setInviteOption(false);
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 9707)
			player.getDungManager().leaveParty();
		return true;
	}

	/**
	 * return process normaly
	 */
	@Override
	public boolean processNPCClick2(NPC npc) {
		if (npc.getId() == 9707)
			player.getDungManager().leaveParty();
		return true;
	}

	@Override
	public void moved() {
		if (player.getDungManager().isInside())
			return;
		if ((player.getX() == 3385 || player.getX() == 3384)
				&& player.getY() == 3615) {
			setInviteOption(false);
			player.getDungManager().leaveParty();
			removeControler();
			player.getControlerManager().startControler("Wilderness");
		} else {
			if (!isAtKalaboss(player)) {
				setInviteOption(false);
				player.getDungManager().leaveParty();
				removeControler();
			} else
				setInviteOption(true);
		}
	}

	public static boolean isAtKalaboss(WorldTile tile) {
		return tile.getX() >= 3385 && tile.getX() <= 3513
				&& tile.getY() >= 3605 && tile.getY() <= 3794;
	}

	public void setInviteOption(boolean show) {
		if (show == showingOption)
			return;
		showingOption = show;
		player.getPackets()
				.sendPlayerOption(show ? "Invite" : "null", 1, false);
	}
}
