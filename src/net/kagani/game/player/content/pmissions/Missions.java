package net.kagani.game.player.content.pmissions;

import java.io.Serializable;

public enum Missions implements Serializable {

	LEARNING_THE_ROPES(0),

	;

	private int index;

	/**
	 * Mission parameter index
	 *
	 * @param index
	 */
	private Missions(int index) {
		this.index = index;
	}

	/**
	 * Gets the index of the job.
	 *
	 * @return index
	 */
	public int index() {
		return index;
	}

	/**
	 * Returns the mission value
	 *
	 * @param id
	 * @return mission
	 */
	public static Missions get(int id) {
		for (Missions missions : Missions.values()) {
			if (missions.index() == id)
				return missions;
		}

		return null;
	}
}