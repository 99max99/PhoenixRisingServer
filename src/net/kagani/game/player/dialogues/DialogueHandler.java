package net.kagani.game.player.dialogues;

import java.util.HashMap;

import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public final class DialogueHandler {

	private static final HashMap<Object, Class<? extends Dialogue>> handledDialogues = new HashMap<Object, Class<? extends Dialogue>>();

	@SuppressWarnings({ "unchecked" })
	public static final void init() {
		try {
			Class<Dialogue>[] classes = Utils
					.getClasses("net.kagani.game.player.dialogues.impl");
			for (Class<Dialogue> c : classes) {
				if (c.isAnonymousClass()) // next
					continue;
				if (handledDialogues.put(c.getSimpleName(), c) != null)
					Logger.log(DialogueHandler.class,
							"ERROR, double dialogue: " + c);
			}
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void reload() {
		handledDialogues.clear();
		init();
	}

	public static final Dialogue getDialogue(Object key) {
		if (key instanceof Dialogue)
			return (Dialogue) key;
		Class<? extends Dialogue> classD = handledDialogues.get(key);
		if (classD == null)
			return null;
		try {
			return classD.newInstance();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	private DialogueHandler() {

	}
}