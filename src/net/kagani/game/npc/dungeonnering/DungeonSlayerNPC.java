package net.kagani.game.npc.dungeonnering;

import java.util.ArrayList;

import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class DungeonSlayerNPC extends DungeonNPC {

	public DungeonSlayerNPC(int id, WorldTile tile, DungeonManager manager,
			double multiplier) {
		super(id, tile, manager, multiplier);
	}

	@Override
	public void drop() {
		super.drop();
		int size = getSize();
		ArrayList<Item> drops = new ArrayList<Item>();
		if (getId() == 10694) {
			if (Utils.random(2) == 0)
				drops.add(new Item(17261));
			else if (Utils.random(10) == 0)
				drops.add(new Item(17263));
		} else if (getId() == 10695) {
			if (Utils.random(2) == 0)
				drops.add(new Item(17265));
			else if (Utils.random(10) == 0)
				drops.add(new Item(17267));
		} else if (getId() == 10696) {
			if (Utils.random(2) == 0)
				drops.add(new Item(17269));
			else if (Utils.random(10) == 0)
				drops.add(new Item(17271));
		} else if (getId() == 10697) {
			if (Utils.random(2) == 0)
				drops.add(new Item(17273));
		} else if (getId() == 10698) {
			if (Utils.random(10) == 0)
				drops.add(new Item(17279));
		} else if (getId() == 10699) {
			if (Utils.random(2) == 0)
				drops.add(new Item(17281));
			else if (Utils.random(10) == 0)
				drops.add(new Item(17283));
		} else if (getId() == 10700) {
			if (Utils.random(2) == 0)
				drops.add(new Item(17285));
			else if (Utils.random(10) == 0)
				drops.add(new Item(17287));
		} else if (getId() == 10701) {
			if (Utils.random(10) == 0)
				drops.add(new Item(17289));
		} else if (getId() == 10702) {
			if (Utils.random(10) == 0)
				drops.add(new Item(17293));
		} else if (getId() == 10704) {
			if (Utils.random(10) == 0)
				drops.add(new Item(17291));
		} else if (getId() == 10705) {
			if (Utils.random(10) == 0)
				drops.add(new Item(17295));
		}

		for (Item item : drops)
			World.addGroundItem(item, new WorldTile(getCoordFaceX(size),
					getCoordFaceY(size), getPlane()));
	}
}