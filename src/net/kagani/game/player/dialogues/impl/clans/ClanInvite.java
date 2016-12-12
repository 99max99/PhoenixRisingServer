package net.kagani.game.player.dialogues.impl.clans;

import net.kagani.game.minigames.stealingcreation.StealingCreationLobbyController;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.clans.ClansManager;
import net.kagani.game.player.dialogues.Dialogue;

public class ClanInvite extends Dialogue {

	Player inviter;

	@Override
	public void start() {
		inviter = (Player) parameters[0];
		ClansManager manager = inviter.getClanManager();
		if (manager == null || player.getClanManager() != null) {
			end();
			return;
		}
		if (player.getInterfaceManager().containsScreenInterface()
				|| player.getControlerManager().getControler() != null
				&& player.getControlerManager().getControler() instanceof StealingCreationLobbyController) {
			end();
			return;
		}
		player.getPackets().sendClanSettings(manager, false);
		player.getInterfaceManager().sendCentralInterface(1095);
		player.getPackets().sendIComponentText(
				1095,
				2,
				"You have been invited to join clanname by "
						+ inviter.getDisplayName() + ".");
		if (manager.getClan().getMottifTop() != 0)
			player.getPackets().sendIComponentSprite(
					1095,
					44,
					ClansManager.getMottifSprite(manager.getClan()
							.getMottifTop()));
		if (manager.getClan().getMottifBottom() != 0)
			player.getPackets().sendIComponentSprite(
					1095,
					54,
					ClansManager.getMottifSprite(manager.getClan()
							.getMottifBottom()));
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 33)
			ClansManager.joinClan(player, inviter);
		end();

	}

	@Override
	public void finish() {

	}

}
