package net.kagani.game.player.content;

import net.kagani.game.player.Player;

public class CharacterSettings {

	/**
	 * @author: Dylan Page
	 */

	public static void handleButtons(Player player, int componentId) {
		switch (componentId) {
		case 2:
			player.getDialogueManager().startDialogue("LadyIthell", 19560);
			break;
		case 4:
			
			break;
		case 8:
			System.out.println(player.getUsername() + " attempted to use the GTL vote system, which has been removed.");
			break;
		case 12:
			player.handleWebstore(player, player.getUsername());
			break;
		case 6:
			player.getDialogueManager().startDialogue("OpenURLPrompt", "");
			break;
		case 10:
			player.getDialogueManager().startDialogue("OpenURLPrompt", "forums");
			break;
		case 14:
			player.getDialogueManager().startDialogue("OpenURLPrompt", "store");
			break;
		default:
			player.getPackets().sendGameMessage("This button does not have an action set.");
			break;
		}
	}
}