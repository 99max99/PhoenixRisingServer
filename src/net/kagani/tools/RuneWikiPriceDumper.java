package net.kagani.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.utils.Utils;

public class RuneWikiPriceDumper {

	/**
	 * @author: Matrix team
	 * @author: 99max99 M
	 */

	public static final void main(String[] args) throws IOException {
		Cache.init();
		for (int itemId = 9800; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			dumpItem(itemId);
		}
	}

	public static boolean dumpItem(int itemId) {
		String pageName = ItemDefinitions.getItemDefinitions(itemId).getName()
				.replace(" (black)", "").replace(" (white)", "")
				.replace(" (yellow)", "").replace(" (red)", "");
		if (pageName == null || pageName.equals("null"))
			return false;
		pageName = pageName.replace(" (p)", "");
		pageName = pageName.replace(" (p+)", "");
		pageName = pageName.replace(" (p++)", "");
		pageName = pageName.replace(" Broken", "");
		pageName = pageName.replace(" 25", "");
		pageName = pageName.replace(" 50", "");
		pageName = pageName.replace(" 75", "");
		pageName = pageName.replace(" 100", "");
		pageName = pageName.replace("jav'n", "javelin");
		pageName = pageName.replaceAll(" ", "_");

		try {
			WebPage page = new WebPage(
					"http://runescape.wikia.com/wiki/Exchange:" + pageName);
			try {
				page.load();
			} catch (Exception e) {
				System.out.println("Invalid page: " + pageName + " (" + itemId
						+ ").");
				return false;
			}
			int price = 0;
			stringLoop: for (String line : page.getLines()) {
				if (line.contains("GEPrice")) {
					price = getValue(line);
					System.err.println(price);
					break stringLoop;
				}
			}
			if (price == 0) {
				System.out.println("Skipping, price is " + price + " of "
						+ pageName + " (" + itemId + ").");
				return false;
			}
			try {
				BufferedWriter writer = new BufferedWriter(
						new FileWriter(
								System.getProperty("user.home")
										+ "/Dropbox/RS3/MaxScape830 Server/information/dumps/prices.txt",
								true));
				writer.write(itemId + " - " + price);
				writer.newLine();
				writer.flush();
				writer.close();
				System.out.println("Dumped: " + pageName + " (" + itemId
						+ ") for " + price + ".");
			} catch (IOException e) {

			}
			return true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static int getValue(String s) {
		s = s.replaceAll(", ", "").replaceAll(",", "");
		StringBuilder sb = new StringBuilder();
		char c;
		boolean foundStart = false;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (Character.isDigit(c)) {
				sb.append(c);
				foundStart = true;
			} else if (foundStart) {
				break;
			}
		}
		try {
			int amount = Integer.parseInt(sb.toString());
			System.out.println("success " + sb.toString());
			return amount;
		} catch (NumberFormatException e) {
			System.out.println("failed " + sb.toString());
			// e.printStackTrace();
			return 0;
		}
	}
}