package net.kagani.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.utils.Utils;

public class NPCListDumper {

	public static void main(String[] args) throws IOException {
		Cache.init();
		File file = new File("npclist1.txt");
		if (file.exists())
			file.delete();
		else
			file.createNewFile();
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.flush();
		for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
			NPCDefinitions def = NPCDefinitions.getNPCDefinitions(id);
			writer.append(id + " - " + def.name);
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}
}