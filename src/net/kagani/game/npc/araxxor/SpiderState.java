package net.kagani.game.npc.araxxor;

/**
 * Represents the Queen Black Dragon's states.
 * 
 * @author Emperor
 *
 */
public enum SpiderState {
	/**
	 * magic form
	 */
	SLEEPING(-1, null),

	/**
	 * magic form
	 */
	MAGIC(19462, null),

	/**
	 * Range form
	 */
	RANGE(19463, null),

	/**
	 * Melee form
	 */
	MELEE(19457, null),

	/**
	 * Final Spider Araxxi (weak to magic attacks, strong against melee/range).
	 */
	ARRAXI(19464, null), /**
							 * Melee form with acid (ready to come on ramp).
							 */
	MELEE_ACID(19465, null),

	/**
	 * Range form with acid (ready to come on ramp).
	 */
	RANGE_ACID(19467, null),

	/**
	 * Magic form with acid (ready to come on ramp).
	 */
	MAGIC_ACID(19466, null), /**
								 * Magic form with acid (ready to come on ramp).
								 */
	ARAXXI(19464, null);
	/**
	 * The NPC id.
	 */
	private final int npcId;

	/**
	 * The message to be send to the player.
	 */
	private final String message;

	/**
	 * Constructs a new {@code QueenState} {@code Object}.
	 * 
	 * @param npcId
	 *            The NPC id.
	 * @param message
	 *            The message to send.
	 */
	private SpiderState(int npcId, String message) {
		this.npcId = npcId;
		this.message = message;
	}

	/**
	 * Gets the npcId.
	 * 
	 * @return The npcId.
	 */
	public int getNpcId() {
		return npcId;
	}

	/**
	 * Gets the message.
	 * 
	 * @return The message.
	 */
	public String getMessage() {
		return message;
	}
}