package net.kagani.game.player;

import java.io.Serializable;

import net.kagani.game.item.Item;

public class Toolbelt implements Serializable {

	private static final long serialVersionUID = -7244573478285647954L;

	private static final int[][] TOOLBELT_ITEMS = new int[][] {
			{ 946 }, // knife 1
			{ 1735 }, // shears 2
			{ 1595 }, // amulet mould 3
			{ 1755 }, // chisel 4
			{ 1599 }, // holy mould 5
			{ 1597 }, // necklake mould 6
			{ 1733 }, // needle 7
			{ 1592 }, // ring mold 8
			{ 5523 }, // tiara mold 9
			{ 13431 }, // crayfish cage 10
			{ 307 }, // fishing road 11
			{ 309 }, // fly fishing road 12
			{ 311 }, // harpoon 13
			{ 301 }, // lobster pot 14
			{ 303 }, // small fishing net 15
			{ 1265 }, // bronze pickaxe 16
			{ 1351 },// hammer 16
			{ 2985 }, // hatchet 18
			{ 590 }, // tinderbox 19
			{ -1 }, { 1267, 1269, 1273, 1271, 1275, 15259, 32646 },
			{ 1349, 1353, 1357, 1355, 1359, 6739 }, { 8794 }, { 4 }, { 9434 },
			{ 11065 }, { 1785 }, { 2976 }, { 1594 }, { 5343 }, { 5325 },
			{ 5341 }, { 5329 }, { 233 }, { 952 }, { 305 }, { 975 }, { 11323 },
			{ 2575 }, { 2576 }, { 13153 }, { 10150 } };
	private static final int[][] DUNG_TOOLBELT_ITEMS = new int[][] {
			{ 16295, 16297, 16299, 16301, 16303, 16305, 16307, 16309, 16311,
					16313, 16315 },
			{ 16361, 16363, 16365, 16367, 16369, 16371, 16373, 16375, 16377,
					16379, 16381 }, { 17883 }, { 17678 }, { 17794 }, { 17754 },
			{ 17446 }, { 17444 } };

	private static final int[] VAR_IDS = new int[] { 1102, 1103 };
	private static final int[] DUNG_VAR_IDS = new int[] { 1107 };
	private int[][] items;
	private transient Player player;
	private transient boolean dungeonnering;

	public Toolbelt() {
		items = new int[][] { new int[TOOLBELT_ITEMS.length],
				new int[DUNG_TOOLBELT_ITEMS.length] };
		setDefaultItems();
	}

	public void setDefaultItems() {
		for (int i = 0; i < items.length; i++)
			for (int i2 = 0; i2 < items[i].length; i2++)
				if (items[i][i2] != -1 && !(i == 0 && (i2 == 20 || i2 == 21)))
					items[i][i2] = 1;
	}

	public void setPlayer(Player player) {
		this.player = player;

		/*
		 * if(items.length <= TOOLBET_ITEMS.length) //if we add a new item items
		 * = Arrays.copyOf(items, TOOLBET_ITEMS.length);
		 */
	}

	public void init() {
		refreshConfigs();
	}

	private int getVarIndex(int i) {
		return i / 28;
	}

	public int getIncremment(int slot) {
		if (!dungeonnering)
			return slot == 20 || slot == 21 ? 4 : 1;
		return slot == 0 ? 5 : slot == 1 ? 4 : 1;
	}

	private void refreshConfigs() {

		int[] varValues = new int[getVars().length];
		int indexIncremment = 0;
		for (int i = 0; i < getItems().length; i++) {
			if (getItems()[i] != 0) {
				int index = getVarIndex(indexIncremment);
				varValues[index] |= getItems()[i] << (indexIncremment - (index * 28));
			}
			indexIncremment += getIncremment(i);
		}
		for (int i = 0; i < getVars().length; i++)
			player.getVarsManager().sendVar(getVars()[i], varValues[i]);
	}

	public int[] getItems() {
		return items[dungeonnering ? 1 : 0];
	}

	public int[] getVars() {
		return dungeonnering ? DUNG_VAR_IDS : VAR_IDS;
	}

	public int[][] getToolbeltItems() {
		return dungeonnering ? DUNG_TOOLBELT_ITEMS : TOOLBELT_ITEMS;
	}

	private int[] getItemSlot(int id) {
		for (int i = 0; i < getToolbeltItems().length; i++)
			for (int i2 = 0; i2 < getToolbeltItems()[i].length; i2++)
				if (getToolbeltItems()[i][i2] == id)
					return new int[] { i, i2 };
		return null;
	}

	public boolean addItem(int invSlot, Item item) {
		int[] slot = getItemSlot(item.getId());
		if (slot == null)
			return false;
		if (getItems()[slot[0]] == slot[1] + 1)
			player.getPackets().sendInventoryMessage(0, invSlot,
					"That is already on your tool belt.");
		else {
			getItems()[slot[0]] = slot[1] + 1;
			player.getInventory().deleteItem(invSlot, item);
			refreshConfigs();
			player.getPackets().sendGameMessage(
					"You add the " + item.getDefinitions().getName()
							+ " to your tool belt.");
		}
		return true;
	}

	public void switchDungeonneringToolbelt() {
		this.dungeonnering = !dungeonnering;
		player.getPackets().sendCSVarInteger(1725, dungeonnering ? 11 : 1);
		refreshConfigs();
	}

	public boolean containsItem(int id) {
		int[] slot = getItemSlot(id);
		return slot != null && getItems()[slot[0]] == slot[1] + 1;
	}

	public boolean isTool(int id) {
		return getItemSlot(id) != null;
	}
}