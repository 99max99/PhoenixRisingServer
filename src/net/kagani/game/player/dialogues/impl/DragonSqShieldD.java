package net.kagani.game.player.dialogues.impl;

import net.kagani.game.Animation;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Smithing;
import net.kagani.game.player.dialogues.Dialogue;

public class DragonSqShieldD extends Dialogue {

	private static int LEFT = 2366, RIGHT = 2368, DRAGON_SQ_SHIELD = 1187;

	public static boolean isDragonSqShieldPart(int id) {
		return id == LEFT || id == RIGHT;
	}

	public static void joinPieces(Player player) {
		player.getDialogueManager().startDialogue("DragonSqShieldD");
	}

	@Override
	public void start() {
		if (!player.getInventory().containsItemToolBelt(Smithing.HAMMER)) {
			sendDialogue("You need a hammer in order to work with the shield halfs.");
			return;
		}
		if (player.getSkills().getLevel(Skills.SMITHING) < 60) {
			sendDialogue("You need a Smithing level of 60 to forge a dragon sq shield.");
			return;
		}
		if (!player.getInventory().containsItem(LEFT, 1)) {
			sendDialogue("You need a shield left half to forge a dragon sq shield.");
			return;
		}
		if (!player.getInventory().containsItem(RIGHT, 1)) {
			sendDialogue("You need a shield right half to forge a dragon sq shield.");
			return;
		}
		sendDialogue("You set to work trying to fix the ancient shield. It's seen some heavy action and needs some serious work doing to it.");
		stage = 0;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			player.lock(2);
			player.setNextAnimation(new Animation(898));
			player.getInventory().deleteItem(LEFT, 1);
			player.getInventory().deleteItem(RIGHT, 1);
			player.getInventory().addItem(DRAGON_SQ_SHIELD, 1);
			player.getSkills().addXp(Skills.SMITHING, 80);
			sendDialogue("Even for an experienced armourer its not an easy easy, but eventually it is ready. You have restored the dragon square shield to its former glory.");
			stage = -1;
			break;
		default:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}
