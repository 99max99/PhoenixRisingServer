package net.kagani.game.player.content.agility;

import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;

public class Agility {

	public static boolean hasLevel(Player player, int level) {
		if (player.getSkills().getLevel(Skills.AGILITY) < level) {
			player.getPackets().sendGameMessage(
					"You need an Agility level of " + level
							+ " to use this obstacle.");
			return false;
		}
		return true;
	}
}