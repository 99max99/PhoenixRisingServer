package net.kagani.game.log;

import java.io.Serializable;

import net.kagani.utils.Utils;

public class LogEntry implements Serializable {
	/**
	 * Serial number.
	 */
	private static final long serialVersionUID = -9141858878691509737L;

	/**
	 * Type of the entry.
	 */
	private int type;
	/**
	 * Date of the log.
	 */
	private long date;

	/**
	 * String tags for this log entry.
	 */
	private String[] stags;

	/**
	 * Int tags for this log entry.
	 */
	private int[] itags;

	private LogEntry(int type) {
		this.type = type;
		this.date = Utils.currentTimeMillis();
	}

	public int getType() {
		return type;
	}

	public long getDate() {
		return date;
	}

}
