package net.kagani.game.player.quest;

public enum Quests {

	/**
	 * @author: Dylan Page
	 */

	COOKSASSISTANT(0),

	DEMONSLAYER(0),

	DORICSQUEST(0),

	DRUIDICRITUAL(0),

	IMPCATCHER(0),

	THEBLOODPACT(0),

	THERESTLESSGHOST(0),

	;

	private int index;

	/**
	 * Quests parameter index
	 * 
	 * @param index
	 */
	private Quests(int index) {
		this.index = index;
	}

	/**
	 * Gets the index of the quest.
	 * 
	 * @return index
	 */
	public int index() {
		return index;
	}

	/**
	 * Returns the quest value
	 * 
	 * @param id
	 * @return quest
	 */
	public static Quests getQuest(int id) {
		for (Quests quest : Quests.values()) {
			if (quest.index() == id)
				return quest;
		}
		return null;
	}
}