package net.kagani.game.player.content.dungeoneering.skills;

import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.dungeonnering.MastyxTrap;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class DungeoneeringTraps {

	public static final int[] ITEM_TRAPS = { 17756, 17758, 17760, 17762, 17764,
			17766, 17768, 17770, 17772, 17774 };
	private static final int[] MASTRYX_HIDES = { 17424, 17426, 17428, 17430,
			17432, 17434, 17436, 17438, 17440, 17442 };
	private static final int[] HUNTER_LEVELS = { 1, 10, 20, 30, 40, 50, 60, 70,
			80, 90 };

	public static void placeTrap(final Player player,
			final DungeonManager manager, final int index) {
		int levelRequired = HUNTER_LEVELS[index];
		if (manager.getMastyxTraps().size() > 5) {
			player.getPackets()
					.sendGameMessage(
							"Your party has already placed the maximum amount of traps allowed.");
			return;
		} else if (player.getSkills().getLevel(Skills.HUNTER) < levelRequired) {
			player.getPackets().sendGameMessage(
					"You need a Hunter level of " + levelRequired
							+ " in order to place this trap.");
			return;
		}
		player.lock(2);
		player.setNextAnimation(new Animation(827));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				manager.addMastyxTrap(new MastyxTrap(player.getDisplayName(),
						11076 + index, player, -1, false));
				player.getInventory()
						.deleteItem(new Item(ITEM_TRAPS[index], 1));
				player.getPackets().sendGameMessage(
						"You lay the trap onto the floor.");
			}
		}, 2);
	}

	public static void removeTrap(Player player, MastyxTrap trap,
			DungeonManager manager) {
		if (player.getDisplayName().equals(trap.getPlayerName())) {
			player.setNextAnimation(new Animation(827));
			player.getPackets().sendGameMessage("You dismantle the trap.");
			player.getInventory().addItem(ITEM_TRAPS[trap.getTier()], 1);
			manager.removeMastyxTrap(trap);
		} else
			player.getPackets().sendGameMessage(
					"This trap is not yours to remove!");
	}

	public static void skinMastyx(Player player, NPC npc) {
		player.setNextAnimation(new Animation(827));
		player.getInventory()
				.addItemDrop(MASTRYX_HIDES[getNPCTier(npc.getId() - 10)],
						Utils.random(2, 5));
		npc.finish();
	}

	public static int getNPCTier(int id) {
		return id - 11086;
	}
}
