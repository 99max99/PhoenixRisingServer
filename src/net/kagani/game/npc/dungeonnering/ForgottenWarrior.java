package net.kagani.game.npc.dungeonnering;

import java.util.ArrayList;

import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.content.dungeoneering.DungeonConstants;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.DungeonUtils;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class ForgottenWarrior extends Guardian {

	public ForgottenWarrior(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference, double multiplier) {
		super(id, tile, manager, reference, multiplier);
	}

	@Override
	public void drop() {
		super.drop();
		int size = getSize();
		ArrayList<Item> drops = new ArrayList<Item>();
		// just 1 for now
		for (int type = 0; type < DungeonConstants.FORGOTTEN_WARRIORS.length; type++) {
			for (int id : DungeonConstants.FORGOTTEN_WARRIORS[type])
				if (id == getId()) {
					int tier = getManager().getParty().getAverageCombatLevel() / 10;
					if (tier > 10)
						tier = 10;
					else if (tier < 1)
						tier = 1;
					// melee mage range
					if (type == 0)
						drops.add(new Item(DungeonUtils
								.getRandomMeleeGear(Utils.random(tier) + 1)));
					else if (type == 1)
						drops.add(new Item(DungeonUtils
								.getRandomMagicGear(Utils.random(tier) + 1)));
					else
						drops.add(new Item(DungeonUtils
								.getRandomRangeGear(Utils.random(tier) + 1)));
					break;
				}
		}
		for (Item item : drops)
			World.addGroundItem(item, new WorldTile(getCoordFaceX(size),
					getCoordFaceY(size), getPlane()));
	}

}
