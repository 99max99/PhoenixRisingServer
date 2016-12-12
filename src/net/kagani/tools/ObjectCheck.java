package net.kagani.tools;

import java.io.IOException;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.ObjectDefinitions;

public class ObjectCheck {

	public static void main(String[] args) throws IOException {
		Cache.init();
		/*
		 * for (int i = 0; i < Utils.getObjectDefinitionsSize(); i++) {
		 * ObjectDefinitions def = ObjectDefinitions.getObjectDefinitions(i); if
		 * (def.containsOption("Steal-from")) { System.out.println(def.id +
		 * " - " + def.name); } }
		 */
		int minX = 3033;
		int minY = 3426;
		int maxX = 3308;
		int maxY = 3948;
		minX -= minX % 9;
		minY -= minY % 9;
		maxX -= maxX % 9;
		maxY -= maxY % 9;

		for (int x = minX; x < maxX; x += 9) {
			for (int y = minY; y < maxY; y += 9) {
				int objectId = x == minX || x == maxX || y == minX || y == maxY ? 3700
						: 3701;
				System.out.println(objectId + " 10 0 - " + x + " " + y + " 3");
			}
		}

		System.out.println(maxX + ", " + maxY);
		System.out.println(ObjectDefinitions.getObjectDefinitions(3701).sizeX);
		System.out.println(ObjectDefinitions.getObjectDefinitions(3701).sizeY);

	}

}
