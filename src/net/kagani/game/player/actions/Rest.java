package net.kagani.game.player.actions;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public class Rest extends Action {

	private static int[][] FIRST = { { 5713, 1549, 5748 }, { 11786, 1550, 11788 }, { 5713, 1551, 2921 } }; // Normal

	private static int[][] SECOND = { { 20286, 20287, 20288 }, { 20286, 20287, 20288 }, { 20286, 20287, 20288 } }; // Arcane

	private static int[] SECOND_GFX = { 4003, 4004, 4005 };

	private static int[][] THIRD = { { 17301, 17302, 17303 }, { 17301, 17302, 17303 }, { 17301, 17302, 17303 } }; // Zen

	private int index;
	private boolean musician;

	public Rest(boolean musician) {
		this.musician = musician;
	}

	@Override
	public boolean start(Player player) {
		if (!process(player))
			return false;
		player.setResting(musician ? 2 : 1);
		switch (player.getRestAnimation()) {
		case 0:
		case 1:
			index = Utils.random(FIRST.length);
			player.setNextAnimation(new Animation(FIRST[index][0]));
			player.getAppearence().setRenderEmote(FIRST[index][1]);
			break;
		case 2:
			index = Utils.random(SECOND.length);
			player.setNextAnimation(new Animation(SECOND[index][0]));
			player.getAppearence().setRenderEmote(SECOND[index][1]);
			player.setNextGraphics(new Graphics(SECOND_GFX[0]));
			break;
		case 3:
			index = Utils.random(THIRD.length);
			player.setNextAnimation(new Animation(THIRD[index][0]));
			player.getAppearence().setRenderEmote(THIRD[index][1]);
			break;
		default:
			index = Utils.random(FIRST.length);
			player.setNextAnimation(new Animation(FIRST[index][0]));
			player.getAppearence().setRenderEmote(FIRST[index][1]);
			break;
		}
		setActionDelay(player, 1);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (player.getEffectsManager().hasActiveEffect(EffectType.POISON)) {
			player.getPackets().sendGameMessage("You can't rest while you're poisoned.");
			return false;
		}
		if (player.isUnderCombat()) {
			player.getPackets().sendGameMessage("You can't rest until 10 seconds after the end of combat.");
			return false;
		}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		return 0;
	}

	@Override
	public void stop(Player player) {
		player.setResting(0);
		switch (player.getRestAnimation()) {
		case 0:
		case 1:
			index = Utils.random(FIRST.length);
			player.setNextAnimation(new Animation(FIRST[index][2]));
			break;
		case 2:
			index = Utils.random(SECOND.length);
			player.setNextAnimation(new Animation(SECOND[index][2]));
			player.setNextGraphics(new Graphics(SECOND_GFX[2]));
			break;
		case 3:
			index = Utils.random(THIRD.length);
			player.setNextAnimation(new Animation(THIRD[index][2]));
			break;
		default:
			index = Utils.random(FIRST.length);
			player.setNextAnimation(new Animation(FIRST[index][2]));
			break;
		}
		player.getEmotesManager().setNextEmoteEnd();
		player.getAppearence().setRenderEmote(-1);
	}
}