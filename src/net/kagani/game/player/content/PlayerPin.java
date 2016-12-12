package net.kagani.game.player.content;

import net.kagani.game.player.Player;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class PlayerPin {

	public Player player;

	/**
	 * @contr Initalizes new class
	 * @param player
	 */
	public PlayerPin(Player player) {
		playerPin = -1;
	}

	/**
	 * Represents Players pin
	 */
	public int playerPin;

	/**
	 * Represents if player has a pin
	 */
	public boolean hasPin;

	/**
	 * Requests pin upon login
	 * 
	 * @param player
	 */
	public void initalizePin(Player player) {
		if (playerPin == -1)
			return;
		player.getTemporaryAttributtes().put("PLAYER_PIN", Boolean.FALSE);
	}

	/**
	 * Removes Player pin
	 * 
	 * @return
	 */
	public void removePin() {
		playerPin = -1;
		hasPin = false;
	}

	/**
	 * Grabs the players pin
	 * 
	 * @return playerPin
	 */
	public int getPlayerPin() {
		return playerPin;
	}

	/**
	 * Sets the players pin
	 * 
	 * @param playerPin
	 * @return new playerPin
	 */
	public int setPlayerPin(int playerPin) {
		return this.playerPin = playerPin;
	}

	/**
	 * Grabs if player has pin
	 * 
	 * @return hasPin
	 */
	public boolean hasPin() {
		return hasPin;
	}

	/**
	 * Sets if player has pin
	 * 
	 * @param hasPin
	 * @return
	 */
	public boolean setHasPin(boolean hasPin) {
		return this.hasPin = hasPin;
	}

	/**
	 * 
	 * @param player
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

}
