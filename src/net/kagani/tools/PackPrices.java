package net.kagani.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import net.kagani.cache.Cache;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.content.grandExchange.OfferHistory;
import net.kagani.utils.SerializableFilesManager;
import net.kagani.utils.Utils;

public class PackPrices {

	private static HashMap<Integer, Integer> PRICES = new HashMap<Integer, Integer>();
	private static HashMap<Integer, Integer> PRICES2 = new HashMap<Integer, Integer>();

	static {
		// put items we want to override here.
	}

	public static void main(String[] args) {
		try {
			// Settings.HOSTED = true;
			// SerializableFilesManager.loadGEPrices();
			GrandExchange.init();
			Cache.init();
			BufferedReader reader = new BufferedReader(new FileReader(
					"./data/items/prices.json"));
			System.out.println("Beginning.");
			try (RandomAccessFile raf = new RandomAccessFile(
					"./data/items/grand_exchange_db.emp", "rw")) {
				raf.readLong();
				int length = raf.readInt();
				for (int i = 0; i < length; i++) {
					int itemId = raf.readShort() & 0xFFFF;
					int value = raf.readInt();
					int logLength = raf.readByte() & 0xFF;
					for (int index = 0; index < logLength; index++) {
						raf.readInt();
					}
					raf.readShort();
					raf.readLong();
					raf.readLong();
					PRICES2.put(itemId, value);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			while (true) {
				String line = reader.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				line = line.replace("﻿", "");
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length < 2) {
					reader.close();
					throw new RuntimeException(
							"Invalid list for item examine line: " + line);
				}
				int itemId = Integer.valueOf(splitedLine[0]);
				// out.writeShort(itemId);
				int value = Integer.valueOf(splitedLine[1]);
				// out.writeInt(value);
				if (value == 0) {
					value = GrandExchange.getPrice(itemId);
					if (value == 0 || value == -1)
						value = PRICES2.get(itemId) == null ? 0 : PRICES2
								.get(itemId);
				}
				PRICES.put(itemId, value);
				if (Utils.random(5) == 0)
					System.out.println("Check " + itemId);
			}
			System.out.println("Done.");
			reader.close();
			SerializableFilesManager
					.saveGEPrices((new HashMap<Integer, Integer>(PRICES)));// sets
			// good
			// prices
			SerializableFilesManager
					.saveGEHistory(new ArrayList<OfferHistory>());// reset's
			// the
			// save
			// history
			SerializableFilesManager.flush();
			System.exit(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}