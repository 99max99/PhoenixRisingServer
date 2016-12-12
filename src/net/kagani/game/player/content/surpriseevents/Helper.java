package net.kagani.game.player.content.surpriseevents;

import java.util.List;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.Drop;
import net.kagani.game.npc.Drops;
import net.kagani.game.player.Player;
import net.kagani.utils.NPCDrops;
import net.kagani.utils.Utils;

public class Helper {

	/**
	 * Ids of npcs whose drop's we are generating.
	 */
	private static final int[] EMULATED_DROP_NPC_IDS = new int[] { 50, 2642,
			9462, 3200, 8133, 13447, 13451, 13452, 13453, 13454, 13465, 13465,
			13465, 13465, 6260, 6222, 6203, 6247, 8349 };
	/**
	 * Random food ids.
	 */
	private static final int[] RANDOM_CONSUMABLE_IDS = new int[] { 391, 15272,
			385, 7946, 6685, 3024, 2434, };

	/**
	 * Drop's death reward cash and more.
	 */
	public static void dropDeathReward(Player killer, Player victim,
			double dropmod, int cashamt) {
		Drops drops = NPCDrops.getDrops(EMULATED_DROP_NPC_IDS[Utils
				.random(EMULATED_DROP_NPC_IDS.length)]);
		if (drops == null)
			return;

		double dropRate = 1 * dropmod;
		List<Drop> dropL = drops.generateDrops(killer, dropRate);
		drops.addCharms(dropL, victim.getSize());

		if (cashamt > 0)
			dropL.add(new Drop(995, (int) (cashamt
					/ Settings.getDropQuantityRate() * 0.9d), cashamt));

		if (Utils.random(100) < 25) {
			dropL.add(new Drop(RANDOM_CONSUMABLE_IDS[Utils
					.random(RANDOM_CONSUMABLE_IDS.length)], 1, 6));
		}

		for (Drop drop : dropL) {
			if (killer.getTreasureTrailsManager().isScroll(drop.getItemId())) {
				if (killer.getTreasureTrailsManager().hasClueScrollItem())
					continue;
				killer.getTreasureTrailsManager().resetCurrentClue();
			}
			sendDrop(victim, killer, drop);
		}
	}

	@SuppressWarnings("deprecation")
	private static Item sendDrop(Player dropper, Player player, Drop drop) {
		if ((drop.getItemId() >= 20135 && drop.getItemId() <= 20174) // nex
																		// piece
				|| (drop.getItemId() >= 24974 && drop.getItemId() <= 24991) // nex
																			// gloves/boots
				|| (drop.getItemId() >= 13746 && drop.getItemId() <= 13753) // sigil
				|| drop.getItemId() == 11335 // dragon full helm
		)
			World.sendNews(player, player.getDisplayName()
					+ " has received "
					+ ItemDefinitions.getItemDefinitions(drop.getItemId())
							.getName() + " drop!", 1);
		else if ((drop.getItemId() >= 21787 && drop.getItemId() <= 21795) // glacor
																			// boots
				|| (drop.getItemId() >= 11702 && drop.getItemId() <= 11709) // godsword
																			// hilts
				|| (drop.getItemId() >= 11716 && drop.getItemId() <= 11731) // godwars
																			// gear
				|| (drop.getItemId() >= 24992 && drop.getItemId() <= 25039) // godwars
																			// gear
				|| drop.getItemId() == 14484 // dragon claws
				|| drop.getItemId() == 15259 // dragon pickaxe
				|| drop.getItemId() == 32646 // crystal pickaxe
				|| drop.getItemId() == 11286 // draconic visage
				|| drop.getItemId() == 13902 // status warhammer
				|| drop.getItemId() == 13899 // vesta's longsword
		)
			World.sendNews(player, player.getDisplayName()
					+ " has received "
					+ ItemDefinitions.getItemDefinitions(drop.getItemId())
							.getName() + " drop!", 2);
		boolean stackable = ItemDefinitions
				.getItemDefinitions(drop.getItemId()).isStackable();
		Item item = stackable ? new Item(drop.getItemId(),
				(drop.getMinAmount() * Settings.getDropQuantityRate(player))
						+ Utils.random(drop.getExtraAmount()
								* Settings.getDropQuantityRate(player)))
				: new Item(drop.getItemId(), drop.getMinAmount()
						+ Utils.random(drop.getExtraAmount()));
		if (!stackable && item.getAmount() > 1) {
			for (int i = 0; i < item.getAmount(); i++)
				World.addGroundItem(new Item(item.getId(), 1), new WorldTile(
						dropper.getX(), dropper.getY(), dropper.getPlane()),
						player, true, 60);
		} else
			World.addGroundItem(
					item,
					new WorldTile(dropper.getX(), dropper.getY(), dropper
							.getPlane()), player, true, 60);
		return item;
	}

}
