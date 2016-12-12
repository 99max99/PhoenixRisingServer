package net.kagani.game.player.content.dungeoneering.rooms.puzzles;

import net.kagani.game.Animation;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.dungeonnering.DungeonNPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.rooms.PuzzleRoom;
import net.kagani.utils.Utils;

public class UnhappyGhostRoom extends PuzzleRoom {

	public static final int RING = 19879;
	public static final int GHOST = 11246;

	private static final int[] COFFIN = { 54582, 54571, 40181, 54593, 55465 };
	private static final int[] COFFIN_OPEN = { 54583, 54572, 55451, 54594,
			55466 };
	private static final int[] COFFIN_BLESSED = { 40172, 40175, 55452, 55463,
			55467 };
	private static final int[] JEWELLERY_BOX_EMPTY = { 54587, 54576, 55453,
			54598, 55468 };
	private static final int[] JEWELLERY_BOX = { 40173, 40180, 55454, 55464,
			55469 };
	private static final int[] BROKEN_POT = { 54588, 54577, 55455, 54599, 55470 };
	private static final int[] FIXED_POT = { 54589, 54578, 55456, 54600, 55471 };
	private static final int[] DAMAGED_PILLAR = { 54591, 54580, 55457, 54602,
			55472 };
	private static final int[] REPAIRED_PILLAR = { 54592, 54581, 55458, 54603,
			55473 };

	private int stage = 0;

	@Override
	public void openRoom() {
		manager.spawnItem(reference, new Item(RING, 1), 5, 9);
		new UnhappyGhost(manager.getTile(reference, 6, 9), manager);
		manager.spawnRandomNPCS(reference);
	}

	@Override
	public boolean processObjectClick1(Player player, WorldObject object) {
		// TODO: You can fail skill related tasks
		if (object.getId() == DAMAGED_PILLAR[type]) {
			if (!hasRequirement(player, Skills.CONSTRUCTION)) {
				player.getPackets().sendGameMessage(
						"You need a construction level of "
								+ getRequirement(Skills.CONSTRUCTION)
								+ " to repair this pillar.");
				return false;
			}
			giveXP(player, Skills.CONSTRUCTION);
			player.lock(4);
			player.setNextAnimation(new Animation(14566));
			replaceObject(object, REPAIRED_PILLAR[type]);
			advance(player);
			return false;
		} else if (object.getId() == JEWELLERY_BOX_EMPTY[type]) {
			if (!player.getInventory().containsItem(RING, 1)) {
				player.getPackets().sendGameMessage(
						"You don't have any jewellery to put in here.");
				return false;
			}
			player.lock(3);
			player.setNextAnimation(new Animation(833));
			player.getInventory().deleteItem(RING, 1);
			replaceObject(object, JEWELLERY_BOX[type]);
			advance(player);
			return false;
		} else if (object.getId() == BROKEN_POT[type]) {
			if (!hasRequirement(player, Skills.CONSTRUCTION)) {
				player.getPackets().sendGameMessage(
						"You need a construction level of "
								+ getRequirement(Skills.CONSTRUCTION)
								+ " to repair this pillar.");
				return false;
			}
			giveXP(player, Skills.CONSTRUCTION);
			player.lock(4);
			player.setNextAnimation(new Animation(14566));
			replaceObject(object, FIXED_POT[type]);
			advance(player);
			return false;
		} else if (object.getId() == COFFIN[type]) {
			if (!hasRequirement(player, Skills.THIEVING)) {
				player.getPackets().sendGameMessage(
						"You need a thieving level of "
								+ getRequirement(Skills.THIEVING)
								+ " to unlock this coffin.");
				return false;
			}
			giveXP(player, Skills.THIEVING);
			player.lock(3);
			player.setNextAnimation(new Animation(833));
			replaceObject(object, COFFIN_OPEN[type]);
			advance(player);
			return false;
		} else if (object.getId() == COFFIN_OPEN[type]) {
			if (!hasRequirement(player, Skills.PRAYER)) {
				player.getPackets().sendGameMessage(
						"You need a prayer level of "
								+ getRequirement(Skills.PRAYER)
								+ " to bless these remains.");
				return false;
			}
			// TODO: failing drains prayer
			giveXP(player, Skills.PRAYER);
			player.lock(3);
			player.setNextAnimation(new Animation(833));
			replaceObject(object, COFFIN_BLESSED[type]);
			advance(player);
			return false;
		}
		return true;
	}

	public void advance(Player player) {
		if (++stage == 5) {
			setComplete();
		}
	}

	@Override
	public boolean processNPCClick1(Player player, NPC npc) {
		if (npc.getId() == GHOST) {
			player.getDialogueManager().startDialogue("UnhappyGhostD", this);
			return false;
		}
		return true;
	}

	@SuppressWarnings("serial")
	public class UnhappyGhost extends DungeonNPC {

		public UnhappyGhost(WorldTile tile, DungeonManager manager) {
			super(GHOST, tile, manager, 0);

		}

		@Override
		public void processNPC() {
			if (!isComplete()) {
				if (Utils.random(5) == 0) {
					setNextAnimation(new Animation(860));
				}
			}
			super.processNPC();
		}

	}

}
