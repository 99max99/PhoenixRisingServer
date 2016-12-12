package net.kagani.game.player.content.clans.citadel;

import java.util.ArrayList;

import net.kagani.game.player.Player;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class Citadel {

	public ArrayList<Player> members = new ArrayList<Player>();
	public ArrayList<Player> guests = new ArrayList<Player>();

	public ArrayList<Player> getMembers() {
		return members;
	}

	public ArrayList<Player> getGuests() {
		return guests;
	}
}