package net.kagani.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import net.kagani.game.player.Player;
import net.kagani.game.player.content.clans.Clan;
import net.kagani.game.player.content.grandExchange.Offer;
import net.kagani.game.player.content.grandExchange.OfferHistory;
import net.kagani.login.DisplayNames;
import net.kagani.login.Offences;

import minifs.MiniFS;

public class SerializableFilesManager {

	private static final String ACCOUNT_PATH = "./data/accounts/";
	private static final String CLAN_PATH = "./data/clans/";
	private static final String GE_OFFERS = "grandExchangeOffers.ser";
	private static final String GE_OFFERS_HISTORY = "grandExchangeOffersTrack.ser";
	private static final String GE_PRICES = "grandExchangePrices.ser";

	/**
	 * Path where offences are stored.
	 */
	private static final String OFFENCES = "offences.ser";
	/**
	 * Path where display names are stored.
	 */
	private static final String DISPLAY_NAMES = "displayNames.ser";

	private static MiniFS filesystem;

	private SerializableFilesManager() {
		throw new Error();
	}

	public static synchronized void flush() {
		try {
			boolean ok = filesystem.flush();
			if (!ok)
				throw new RuntimeException("Couldn't flush fs.");
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized Offences loadOffences() {
		if (filesystem.fileExists(OFFENCES)) {
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

	public static synchronized void saveDisplayNames(ArrayList<String> names) {
		try {
			SerializableFilesManager.storeSerializableClass(names, new File(
					DISPLAY_NAMES));
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public synchronized static boolean containsClan(String name) {
		return new File(CLAN_PATH + name + ".c").exists();
	}

	public synchronized static Clan loadClan(String name) {
		try {
			return (Clan) loadObject(CLAN_PATH + name + ".c");
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	public synchronized static void saveClan(Clan clan) {
		try {
			storeObject(clan, CLAN_PATH + clan.getClanName() + ".c");
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public synchronized static void deleteClan(Clan clan) {
		try {
			new File(CLAN_PATH + clan.getClanName() + ".c").delete();
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized HashMap<Long, Offer> loadGEOffers() {
		if (new File(GE_OFFERS).exists()) {
			try {
				return (HashMap<Long, Offer>) loadObject(GE_OFFERS);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new HashMap<Long, Offer>();
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized ArrayList<OfferHistory> loadGEHistory() {
		if (new File(GE_OFFERS_HISTORY).exists()) {
			try {
				return (ArrayList<OfferHistory>) loadObject(GE_OFFERS_HISTORY);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new ArrayList<OfferHistory>();
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized HashMap<Integer, Integer> loadGEPrices() {
		if (new File(GE_PRICES).exists()) {
			try {
				return (HashMap<Integer, Integer>) loadObject(GE_PRICES);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new HashMap<Integer, Integer>();
		}
	}

	public static synchronized void saveGEOffers(HashMap<Long, Offer> offers) {
		try {
			SerializableFilesManager.storeObject(offers, GE_OFFERS);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized void saveGEHistory(
			ArrayList<OfferHistory> history) {
		try {
			SerializableFilesManager.storeObject(history, GE_OFFERS_HISTORY);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized void saveGEPrices(
			HashMap<Integer, Integer> prices) {
		try {
			SerializableFilesManager.storeObject(prices, GE_PRICES);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static final Object loadSersializedFile(File f) throws IOException,
			ClassNotFoundException {
		if (!f.exists()) {
			return null;
		}
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}

	public static final void storeSerializableClass(Serializable o, File f)
			throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}

	private static synchronized Object loadObject(String f) throws IOException,
			ClassNotFoundException {
		if (!(new File(f).exists()))
			return null;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}

	private static synchronized void storeObject(Serializable o, String f)
			throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}

	public static void deleteAccount(Player player) {
		try {
			new File(ACCOUNT_PATH + player.getUsername() + ".acc").delete();
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}
}