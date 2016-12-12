package net.kagani.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.utils.ItemExamines;
import net.kagani.utils.Logger;
import net.kagani.utils.NPCExamines;
import net.kagani.utils.ObjectExamines;
import net.kagani.utils.Utils;

public class ConfigDumper {

	/**
	 * @author: Dylan Page
	 */

	public static long currentTime = 0;

	public static void main(String args[]) {
		currentTime = Utils.currentTimeMillis();
		try {
			Logger.log("ConfigDumper", "Initiating cache...");
			Cache.init();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Logger.logErr("ConfigDumper",
				"Launched - took " + (Utils.currentTimeMillis() - currentTime)
						+ " milliseconds.");

		try {
			Logger.log("ConfigDumper", "Initiating process...");
			dumpObjects();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void dumpItems() throws IOException {
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter(
				"information/dumps/itemconfigs.txt", true));
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			Item item = new Item(ItemDefinitions.getItemDefinitions(itemId)
					.getId());
			if (ItemDefinitions.getItemDefinitions(itemId).getName()
					.contains("'")
					|| ItemExamines.getExamine(item).contains("'"))
				continue;
			writer.write("('"
					+ ItemDefinitions.getItemDefinitions(itemId).getId()
					+ "','"
					+ ItemDefinitions.getItemDefinitions(itemId).getName()
					+ "','" + ItemExamines.getExamine(item) + "'),");
		}
		writer.flush();
		writer.close();
		Logger.logErr("ConfigDumper",
				"Finished process - took "
						+ (Utils.currentTimeMillis() - currentTime)
						+ " milliseconds.");
	}

	private static void dumpNpcs() throws IOException {
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter(
				"information/dumps/npcconfigs.txt", true));
		for (int npcId = 0; npcId < Utils.getNPCDefinitionsSize(); npcId++) {
			NPC npc = new NPC(NPCDefinitions.getNPCDefinitions(npcId).getId(),
					new WorldTile(0, 0, 0));
			if (NPCDefinitions.getNPCDefinitions(npcId).getName().contains("'"))
				continue;
			writer.write("(#"
					+ NPCDefinitions.getNPCDefinitions(npcId).getId()
					+ "#,#"
					+ NPCDefinitions.getNPCDefinitions(npcId).getName()
					+ "#,#"
					+ NPCExamines.getExamine(npc).replaceAll("It#s a",
							"It is a") + "#),");
			writer.newLine();
		}
		writer.flush();
		writer.close();
		Logger.logErr("ConfigDumper",
				"Finished process - took "
						+ (Utils.currentTimeMillis() - currentTime)
						+ " milliseconds.");
	}

	private static void dumpObjects() throws IOException {
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter(
				"information/dumps/objectconfigs.txt", true));
		for (int objectId = 0; objectId < Utils.getObjectDefinitionsSize(); objectId++) {
			WorldObject object = new WorldObject(
					ObjectDefinitions.getObjectDefinitions(objectId).id, 0, 0, 0,
					0, 0);
			if (ObjectDefinitions.getObjectDefinitions(objectId).name
					.contains("'"))
				continue;
			writer.write("(#"
					+ ObjectDefinitions.getObjectDefinitions(objectId).id
					+ "#,#"
					+ ObjectDefinitions.getObjectDefinitions(objectId).name
					+ "#,#"
					+ ObjectExamines.getExamine(object).replaceAll("It#s a",
							"It is a") + "#),");
		}
		writer.flush();
		writer.close();
		Logger.logErr("ConfigDumper",
				"Finished process - took "
						+ (Utils.currentTimeMillis() - currentTime)
						+ " milliseconds.");
	}
}