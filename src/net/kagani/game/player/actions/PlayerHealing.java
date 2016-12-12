package net.kagani.game.player.actions;

import net.kagani.Settings;
import net.kagani.game.player.Player;

public class PlayerHealing {

	public static boolean heal(Player player) {
		if (edgeville(player) || grandExchange(player) || varrock(player) || varrockEast(player) || camelot(player)
				|| camelot2(player) || lumbridge(player) || falador(player) || faladorEast(player) || ardougne(player)
				|| ardougneSouth(player) || player.withinDistance(Settings.HOME_LOCATION, 120)) {
			if (player.getHitpoints() >= player.getSkills().getLevel(3))
				return false;
			player.setHitpoints(player.getHitpoints() + player.getSkills().getLevel(3) / 10);
		}
		return true;
	}

	public static boolean edgeville(Player player) {
		return (player.getX() > 3090 && player.getY() < 3500 && player.getX() < 3099 && player.getY() > 3487);
	}

	public static boolean grandExchange(Player player) {
		return (player.getX() > 3143 && player.getY() < 3513 && player.getX() < 3186 && player.getY() > 3471);
	}

	public static boolean varrock(Player player) {
		return (player.getX() > 3178 && player.getY() < 3447 && player.getX() < 3195 && player.getY() > 3431);
	}

	public static boolean varrockEast(Player player) {
		return (player.getX() > 3249 && player.getY() < 3426 && player.getX() < 3259 && player.getY() > 3413);
	}

	public static boolean camelot(Player player) {
		return (player.getX() > 2718 && player.getY() < 3498 && player.getX() < 2731 && player.getY() > 3486);
	}

	public static boolean camelot2(Player player) {
		return (player.getX() > 2805 && player.getY() < 3446 && player.getX() < 2813 && player.getY() > 3437);
	}

	public static boolean lumbridge(Player player) {
		return (player.getX() > 3201 && player.getY() < 3237 && player.getX() < 3229 && player.getY() > 3200);
	}

	public static boolean falador(Player player) {
		return (player.getX() > 2942 && player.getY() < 3374 && player.getX() < 2950 && player.getY() > 3367);
	}

	public static boolean faladorEast(Player player) {
		return (player.getX() > 3008 && player.getY() < 3359 && player.getX() < 3020 && player.getY() > 3352);
	}

	public static boolean ardougne(Player player) {
		return (player.getX() > 2611 && player.getY() < 3336 && player.getX() < 2622 && player.getY() > 3329);
	}

	public static boolean ardougneSouth(Player player) {
		return (player.getX() > 2648 && player.getY() < 3288 && player.getX() < 2659 && player.getY() > 3279);
	}
}