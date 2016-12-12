package net.kagani.game.player.events;

import java.util.HashMap;

import net.kagani.utils.Logger;

public class EventHandler {

	/**
	 * @author: Dylan Page
	 */

	private static final HashMap<Object, Class<? extends Event>> handledEvents = new HashMap<Object, Class<? extends Event>>();

	@SuppressWarnings("unchecked")
	public static final void init() {
		try {

		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void reload() {
		handledEvents.clear();
		init();
	}

	public static final Event getEvent(Object key) {
		if (key instanceof Event)
			return (Event) key;
		Class<? extends Event> classC = handledEvents.get(key);
		if (classC == null)
			return null;
		try {
			return classC.newInstance();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}
}