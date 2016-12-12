package net.kagani.tools;

import java.io.IOException;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.utils.Utils;

public class NPCCheck {

	public static void main(String[] args) throws IOException {
		Cache.init();
		for (int id = 0; id < Utils.getNPCDefinitionsSize(); id++) {
			NPCDefinitions def = NPCDefinitions.getNPCDefinitions(id);
			if (def.name.contains("Elemental")) {
				System.out.println(id + " - " + def.name);
			}
		}
	}
}