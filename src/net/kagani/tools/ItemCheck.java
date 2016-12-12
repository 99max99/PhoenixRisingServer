package net.kagani.tools;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.player.Equipment;
import net.kagani.utils.Utils;

public class ItemCheck {

	private static List<Integer> items = new LinkedList<Integer>();

	public static final void main(String[] args) throws IOException {
		Cache.init();

		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);
			if ((def.equipSlot != Equipment.SLOT_WEAPON && def.equipSlot != Equipment.SLOT_ARROWS)
					|| def.lended || def.noted)
				continue;
			if (isPoisoned(def))
				continue;
			ItemDefinitions p = getPoison(def, "p");
			ItemDefinitions p_ = getPoison(def, "p+");
			ItemDefinitions p__ = getPoison(def, "p++");
			// System.out.println(p+", "+p_+", "+p__);
			if (p == null || p_ == null || p__ == null)
				continue;
			System.out.println(", "
					+ (Utils.formatPlayerNameForProtocol(def.name)
							.toUpperCase()) + "(" + itemId + ", " + p.id + ", "
					+ p_.id + ", " + p__.id + ")");

			// System.out.println(itemId+", "+def.name+", "+p.id+", "+p.name+",
			// "+p_.id+", "+p_.name+", "+p__.id+", "+p__.name);
		}
	}

	private static ItemDefinitions getPoison(ItemDefinitions def, String string) {
		for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
			ItemDefinitions def2 = ItemDefinitions.getItemDefinitions(i);
			if (def2.getName()
					.equals(def.getName().concat(" (" + string + ")"))) {
				return def2;
			}
		}
		return null;
	}

	private static boolean isPoisoned(ItemDefinitions def) {
		String name = def.getName().toLowerCase();
		return name.contains("(p)") || name.contains("(p+)")
				|| name.contains("(p++)") || name.contains("(b)");
	}

	private static int getItemId(String name) {
		for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
			ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);
			name = name.replace(" legs", " platelegs");
			name = name.replace(" body", " platebody");
			if (def.name.equalsIgnoreCase(name))
				return itemId;
		}
		return -1;
	}
}