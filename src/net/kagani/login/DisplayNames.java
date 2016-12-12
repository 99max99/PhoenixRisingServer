package net.kagani.login;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.kagani.utils.Utils;
import net.kagani.utils.saving.JsonFileManager;

public class DisplayNames implements Serializable {
	/**
	 * Our serial UID.
	 */
	private static final long serialVersionUID = 3849428802737803252L;

	/**
	 * Contains display name to user name mapping.
	 */
	private Map<String, String> displayToUser;
	/**
	 * Contains reverse mapping of user name to display name.
	 */
	private transient Map<String, String> userToDisplay;
	/**
	 * Contains previous display name(s) for username(s).
	 */
	private Map<String, String> previousDisplayNames;

	public DisplayNames() {
		displayToUser = new HashMap<String, String>();
		userToDisplay = new HashMap<String, String>();
		previousDisplayNames = new HashMap<String, String>();
	}

	/**
	 * Initialize's reverse mapping, this needs to be done after loading this
	 * class.
	 */
	public synchronized void initReverseMapping() {
		userToDisplay = new HashMap<String, String>();
		Iterator<Map.Entry<String, String>> it$ = displayToUser.entrySet()
				.iterator();
		while (it$.hasNext()) {
			Map.Entry<String, String> pair = it$.next();
			if (userToDisplay.containsKey(pair.getValue()))
				throw new RuntimeException(
						"Display names are corrupted, reset is a must!");
			userToDisplay.put(pair.getValue(), pair.getKey());
		}
	}

	/**
	 * Tries to assign any display name for specific user, giving priority to
	 * preferred name.
	 */
	public synchronized boolean assignAnyDisplayName(String username,
			String preferredDisplayName) {
		if (preferredDisplayName != null
				&& reserveDisplayName(username, preferredDisplayName))
			return true;

		for (int i = 0; i < 50; i++)
			if (reserveDisplayName(username, "#" + Utils.random(1000000000)))
				return true;
		return false;
	}

	/**
	 * Reserve's display name for specific user while removing previous display
	 * name (if it had one).
	 */
	public synchronized boolean reserveDisplayName(String username,
			String displayname) {
		if (displayToUser.containsKey(displayname)
				|| JsonFileManager.isValidAccount(displayname))
			return false;

		String previousName = userToDisplay.remove(username);
		if (previousName != null) {
			displayToUser.remove(previousName);
			previousDisplayNames.put(username, previousName);
		}

		displayToUser.put(displayname, username);
		userToDisplay.put(username, displayname);
		return true;
	}

	/**
	 * Remove's specific display name.
	 */
	public synchronized boolean removeDisplayName(String displayname) {
		if (!displayToUser.containsKey(displayname))
			return false;
		String user = displayToUser.remove(displayname);
		if (user != null)
			userToDisplay.remove(user);
		return true;
	}

	/**
	 * Find's user name by display name.
	 */
	public synchronized String getUsername(String displayname) {
		return displayToUser.get(displayname);
	}

	/**
	 * Find's display name by user name.
	 */
	public synchronized String getDisplayName(String username) {
		return userToDisplay.get(username);
	}

	/**
	 * Find's previous display name by user name.
	 */
	public synchronized String getPreviousDisplayName(String username) {
		return previousDisplayNames.get(username);
	}
}
