package net.kagani.game.player.dialogues.impl;

import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.minigames.clanwars.ClanWars;
import net.kagani.game.player.Player;
import net.kagani.game.player.dialogues.Dialogue;

/**
 * Handles the clan wars viewing dialogue.
 * 
 * @author Emperor
 * 
 */
public final class ClanWarsViewing extends Dialogue {

	@Override
	public void start() {
		// TITLE: "Your clan does not appear to be in a war."
		sendOptionsDialogue("Your clan does not appear to be in a war.",
				"I want to watch a friend's clan war.",
				"Show me a battle - any battle.", "Oh, forget it.");
	}

	@Override
	public void run(int interfaceId, int componentId) {

		if (componentId == Dialogue.OPTION_1) {
			end();
			player.getPackets().sendInputNameScript(
					"Enter the player name whose battle you wish to see:");
			player.getTemporaryAttributtes().put(Key.CLAN_WARS_VIEW,
					Boolean.TRUE);
		} else if (componentId == Dialogue.OPTION_2) {
			for (ClanWars war : ClanWars.getCurrentwars()) {
				Player friend = war.getPlayerInside();
				if (friend != null) {
					end();
					ClanWars.enter(player, friend, true);
					return;
				}
			}
			end();
			player.getPackets().sendGameMessage(
					"There are no clan wars going on currently.");
		} else if (componentId == Dialogue.OPTION_3) {
			end();
		}
	}

	@Override
	public void finish() {
	}
}