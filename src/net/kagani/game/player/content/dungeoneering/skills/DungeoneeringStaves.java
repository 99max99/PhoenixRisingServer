package net.kagani.game.player.content.dungeoneering.skills;

import net.kagani.game.Animation;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.dialogues.impl.dungeoneering.DungRunecraftingD;

public class DungeoneeringStaves extends Action {

	private static final int[] EMPTY_STAVES = { 16977, 16979, 16981, 16983,
			16985, 16987, 16989, 16991, 16993, 16995 };
	private static final int[] LEVELS = { 10, 20, 30, 40, 50, 60, 70, 80, 90,
			99 };
	private static final double[] EXPERIENCE = { 5.5, 12, 20.5, 29, 39.5, 51,
			63.5, 76, 90.5, 106 };

	private final int index;
	private int cycles;

	public DungeoneeringStaves(int index, int cycles) {
		this.index = index;
		this.cycles = cycles;
	}

	@Override
	public boolean start(Player player) {
		int levelReq = LEVELS[index];
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < levelReq) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a Runecrafting level of " + levelReq
							+ " in order to imbue this stave.");
			return false;
		}
		int staves = getUsableStaves(player, index);
		if (staves == 0)
			return false;
		if (cycles < staves)
			cycles = staves;
		if (cycles > 28)
			cycles = 28;
		return true;
	}

	@Override
	public boolean process(Player player) {
		return cycles > 0;
	}

	@Override
	public int processWithDelay(Player player) {
		cycles--;

		int stave = getNextStave(player, index);
		if (stave == -1)
			return -1;

		player.setNextAnimation(new Animation(13662));

		player.getInventory().deleteItem(new Item(stave, 1));
		player.getInventory().addItem(
				new Item(DungRunecraftingD.RUNES[3][index]));

		double experience = EXPERIENCE[index];
		player.getSkills().addXp(Skills.RUNECRAFTING, experience);
		player.getSkills().addXp(Skills.MAGIC, experience);
		return 3;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

	private int getUsableStaves(Player player, int beginningIndex) {
		int staves = 0;
		for (int i = beginningIndex; i < EMPTY_STAVES.length; i++) {
			staves += player.getInventory().getAmountOf(EMPTY_STAVES[i]);
		}
		return staves;
	}

	private int getNextStave(Player player, int beginningIndex) {
		for (int i = beginningIndex; i < EMPTY_STAVES.length; i++) {
			int stave = EMPTY_STAVES[i];
			if (player.getInventory().containsItem(stave, 1))
				return stave;
		}
		return -1;
	}
}
