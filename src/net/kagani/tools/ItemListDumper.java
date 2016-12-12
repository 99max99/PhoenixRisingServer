package net.kagani.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.GeneralRequirementMap;

public class ItemListDumper {

	public static void main(String[] args) {
		try {
			new ItemListDumper();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ItemListDumper() throws IOException {
		Cache.init();
		File file = new File("generalRequimentMap.txt"); // = new
		// File("information/itemlist.txt");
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		// writer.append("//Version = 709\n");
		writer.flush();
		for (int i = 0; i < 100000; i++) {
			HashMap<Long, Object> map = GeneralRequirementMap.getMap(i)
					.getValues();
			if (map != null && !map.isEmpty()) {
				writer.append(i + " - " + map.toString());
				writer.newLine();
				writer.flush();
			}
		}
		writer.close();
		file = new File("clientScriptMap.txt"); // = new
												// File("information/itemlist.txt");
		writer = new BufferedWriter(new FileWriter(file));
		writer.flush();
		for (int i = 0; i < 100000; i++) {
			HashMap<Long, Object> map = ClientScriptMap.getMap(i).getValues();
			int v = ClientScriptMap.getMap(i).getDefaultIntValue();
			String s = ClientScriptMap.getMap(i).getDefaultStringValue();
			if (map != null) {
				writer.append(i + " - '" + ClientScriptMap.getMap(i).keyType
						+ "':'" + ClientScriptMap.getMap(i).valueType + "' - "
						+ v + " - " + s + " - " + map.toString());
				writer.newLine();
				writer.flush();
			}
		}
		writer.close();
	}

	public static int convertInt(String str) {
		try {
			int i = Integer.parseInt(str);
			return i;
		} catch (NumberFormatException e) {
		}
		return 0;
	}

}
