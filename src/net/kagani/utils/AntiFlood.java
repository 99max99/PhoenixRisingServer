package net.kagani.utils;

import java.util.ArrayList;

import net.kagani.Settings;

public final class AntiFlood {

	/**
	 * @author: Apache Ah64
	 * @author: Dylan Page
	 */

	private static ArrayList<String> connections = new ArrayList<String>(
			Settings.PLAYERS_LIMIT * 100);

	public static void add(String ip) {
		connections.add(ip);
	}

	public static void remove(String ip) {
		connections.remove(ip);
	}

	public static int getSessionsIP(String ip) {
		int amount = 1;
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i).equalsIgnoreCase(ip))
				amount++;
		}
		return amount;
	}
}