package net.kagani.game.player.content.jobs;

import java.io.Serializable;

/**
 * @Author arrow
 * @Contact<arrowrsps@gmail.com;skype:arrowrsps>
 */

public enum Jobs implements Serializable {

	BANANA_PLANTATION(0), ;

	private int index;

	/**
	 * Quests parameter index
	 *
	 * @param index
	 */
	private Jobs(int index) {
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
	 * Returns the job value
	 *
	 * @param id
	 * @return job
	 */
	public static Jobs get(int id) {
		for (Jobs job : Jobs.values()) {
			if (job.index() == id)
				return job;
		}
		return null;
	}
}