package net.kagani.game.player.controllers;

import java.util.List;

import net.kagani.Settings;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.utils.Utils;

public class RiseOfTheSixController extends Controller {

	/**
	 * @author: Dylan Page
	 */

	private int[] regionBase;
	public WorldTile base;

	public boolean spawned;
	protected NPC bossNPC;

	private int WaveId;

	public int Chest = 18804;
	public int Barrier = 31314;

	public int dharok = 2026;
	public int verac = 2030;
	public int guthan = 2027;
	public int torag = 2029;
	public int ahrim = 2025;
	public int karil = 2028;

	private static final Item[] COMMUM_REWARDS = { new Item(558, 17950),
			new Item(562, 7730), new Item(560, 3910), new Item(565, 1640),
			new Item(4740, 1880), new Item(1128, 10), new Item(1514, 210),
			new Item(15271, 150), new Item(1748, 80), new Item(9245, 60),
			new Item(1392, 45), new Item(452, 34), new Item(5316, 4),
			new Item(5303, 9), new Item(5302, 10) };

	private static final Item[] RARE_REWARDS = { new Item(1149, 1),
			new Item(987, 1), new Item(985, 1), new Item(4708, 1),
			new Item(4710, 1), new Item(4712, 1), new Item(4714, 1),
			new Item(4716, 1), new Item(4718, 1), new Item(4720, 1),
			new Item(4722, 1), new Item(4724, 1), new Item(4726, 1),
			new Item(4728, 1), new Item(4730, 1), new Item(4732, 1),
			new Item(4734, 1), new Item(4736, 1), new Item(4738, 1),
			new Item(4745, 1), new Item(4747, 1), new Item(4749, 1),
			new Item(4751, 1), new Item(4753, 1), new Item(4755, 1),
			new Item(4757, 1), new Item(29942, 1) };

	Item[] BARROW_REWARDS = {

	new Item(29949, 1), new Item(29951, 1), new Item(29984, 1),
			new Item(29985, 1), new Item(29986, 1), new Item(29987, 1),
			new Item(29988, 1), new Item(29989, 1) };

	@Override
	public void start() {
		player.getPackets().sendGameMessage("todo.");
		removeControler();
		/*
		 * // = RegionBuilder.findEmptyChunkBound(8, 8); //
		 * RegionBuilder.copyAllPlanesMap(418, , regionChucks[0], //
		 * regionChucks[1], 8); regionBase = MapBuilder.findEmptyChunkBound(8,
		 * 8); MapBuilder.copyAllPlanesMap(418, 1176, regionBase[0],
		 * regionBase[1], 8, 8); player.setNextWorldTile(getWorldTile(14, 15));
		 * player.getInventory().deleteItem(29941, 1);
		 * player.getPackets().sendGameMessage(
		 * "As you enter a Barrow totem gets destroyed...");
		 * player.getPackets().sendGameMessage("Enter the barrier to begin.");
		 * WaveId = 0;
		 */
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == Chest) {
			lootChest();
		}
		if (object.getId() == Barrier) {
			passBarrier();
		}
		return false;
	}

	private boolean noSpaceOnInv;

	public void drop(Item item) {
		Item dropItem = new Item(item.getId(), Utils.random(item
				.getDefinitions().isStackable() ? item.getAmount() : item
				.getAmount()) + 1);
		if (!noSpaceOnInv && player.getInventory().addItem(dropItem))
			return;
		noSpaceOnInv = true;
		player.getBank().addItem(dropItem, false);
		player.getPackets().sendGameMessage(
				"Your loot was placed into your bank.");
	}

	public void lootChest() {
		if (Utils.random(20) == 0)
			drop(BARROW_REWARDS[Utils.random(BARROW_REWARDS.length)]);
		if (Utils.random(10) == 0)
			drop(RARE_REWARDS[Utils.random(RARE_REWARDS.length)]);
		if (Utils.random(1) == 0)
			drop(COMMUM_REWARDS[Utils.random(COMMUM_REWARDS.length)]);
		drop(new Item(995, 50000));
		drop(new Item(29940, 2));
		player.rosTrips++;
		player.getPackets()
				.sendGameMessage(
						"You managed to slay all the Barrows brothers and escape with some loot.");
		player.setNextWorldTile(new WorldTile(Settings.HOME_LOCATION));
		player.setForceMultiArea(false);
		removeControler();
	}

	@Override
	public void process() {
		if (spawned) {
			List<Integer> npcsInArea = World.getRegion(player.getRegionId())
					.getNPCsIndexes();
			if (npcsInArea == null || npcsInArea.isEmpty()) {
				spawned = false;
				WaveId += 1;
				nextWave(WaveId);
				System.out.println("next");
			}
		}

	}

	private void passBarrier() {
		player.setNextWorldTile(getWorldTile(14, 21));
		player.setForceMultiArea(true);
		barrowsBros1();
		barrowsBros2();
		barrowsBros3();
		barrowsBros4();
		barrowsBros5();
		barrowsBros6();
	}

	public void barrowsBros1() {
		bossNPC = new NPC(ahrim, getWorldTile(10, 27), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		spawned = true;
	}

	public void barrowsBros2() {
		bossNPC = new NPC(dharok, getWorldTile(13, 29), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		spawned = true;
	}

	public void barrowsBros3() {
		bossNPC = new NPC(guthan, getWorldTile(16, 29), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		spawned = true;
	}

	public void barrowsBros4() {
		bossNPC = new NPC(karil, getWorldTile(19, 27), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		spawned = true;
	}

	public void barrowsBros5() {
		bossNPC = new NPC(torag, getWorldTile(16, 26), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		spawned = true;
	}

	public void barrowsBros6() {
		bossNPC = new NPC(verac, getWorldTile(13, 26), -1, true, true);
		bossNPC.setForceMultiArea(true);
		bossNPC.setForceAgressive(true);
		spawned = true;
	}

	private void nextWave(int waveid) {
		if (waveid == 1) {
			player.getPackets().sendGameMessage(
					"Congratulations! You've defeated the Barrows Brothers.");
			player.getPackets().sendGameMessage(
					"You now have access to the chest.");
			World.spawnObject(new WorldObject(Chest, 10, 0,
					getWorldTile(14, 32)));
			return;
		}
	}

	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(regionBase[0] * 8 + mapX,
				regionBase[1] * 8 + mapY, 0);
	}

	@Override
	public boolean logout() {
		removeControler();
		return true;
	}

	@Override
	public boolean sendDeath() {
		player.getPackets().sendGameMessage("Oh dear! You just died.");
		removeControler();
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		removeControler();
	}
}