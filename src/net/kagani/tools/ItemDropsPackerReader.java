package net.kagani.tools;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import net.kagani.cache.Cache;
import net.kagani.utils.Logger;

public class ItemDropsPackerReader {

	/**
	 * @author: Dylan Page
	 */

	public static void main(String[] args) throws IOException {
		Cache.init();
		DataInputStream input = new DataInputStream(new FileInputStream(
				"data/npcs/packedDrops.d"));
		
	}
}