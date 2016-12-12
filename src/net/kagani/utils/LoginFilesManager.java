package net.kagani.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import net.kagani.login.DisplayNames;
import net.kagani.login.Offences;

public class LoginFilesManager {

	/**
	 * Path where offences are stored.
	 */
	private static final String OFFENCES = "offences.ser";
	/**
	 * Path where display names are stored.
	 */
	private static final String DISPLAY_NAMES = "displayNames.ser";

	/**
	 * Filesystem, into which files are stored, might be null.
	 */

	public static synchronized Offences loadOffences() {
		if (new File(OFFENCES).exists()) {
			try {
				return (Offences) loadObject(OFFENCES);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new Offences();
		}
	}

	public static synchronized void saveOffences(Offences offences) {
		try {
			storeObject(offences, OFFENCES);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized DisplayNames loadDisplayNames() {
		if (new File(DISPLAY_NAMES).exists()) {
			try {
				return (DisplayNames) loadObject(DISPLAY_NAMES);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new DisplayNames();
		}
	}

	public static synchronized void saveDisplayNames(DisplayNames displayNames) {
		try {
			storeObject(displayNames, DISPLAY_NAMES);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	private static synchronized Object loadObject(String f) throws IOException,
			ClassNotFoundException {
		if (!new File(f).exists()) {
			return null;
		}
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}

	public static final void storeObject(Serializable o, String f)
			throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}

}
