package net.kagani.game.player.content;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Smithing;

public class DragonfireShield {

	public static void joinPieces(Player player) {
		if (!player.getInventory().containsItemToolBelt(Smithing.HAMMER)) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need a hammer in order to work with the visage.");
			return;
		}
		if (player.getSkills().getLevel(Skills.SMITHING) < 90) {
			player.getDialogueManager()
					.startDialogue("SimpleMessage",
							"You need a Smithing level of 90 to forge a dragonfire shield.");
			return;
		}
		if (!player.getInventory().containsItem(1540, 1)) {
			player.getDialogueManager()
					.startDialogue("SimpleMessage",
							"You need a anti-dragon shield to forge a dragonfire shield.");
			return;
		}
		if (!player.getInventory().containsItem(11286, 1)) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need a dragonic visage to forge a dragonfire shield.");
			return;
		}
		player.lock(2);
		player.setNextAnimation(new Animation(898));
		player.getInventory().deleteItem(1540, 1);
		player.getInventory().deleteItem(11286, 1);
		player.getInventory().addItem(11284, 1);
		player.getSkills().addXp(Skills.SMITHING, 2000);
		player.getDialogueManager()
				.startDialogue(
						"SimpleDialogue",
						"Even for an experienced smith it is not an easy task, but eventually it is done.");
	}

	public static void chargeDFS(Player player, boolean fully) {
		int shieldId = player.getEquipment().getShieldId();
		if (shieldId != 11284 && shieldId != 11283)
			return;
		if (shieldId == 11284) {
			player.getEquipment().getItem(Equipment.SLOT_SHIELD).setId(11283);
			player.getEquipment().refresh(Equipment.SLOT_SHIELD);
			player.getAppearence().generateAppearenceData();
		}
		if (player.getCharges().getCharges(11283) == 50) {
			player.getPackets().sendGameMessage(
					"Your dragonfire shield is already full.", true);
			return;
		}
		player.getCharges().addCharges(11283, fully ? 50 : 1,
				Equipment.SLOT_SHIELD);
		player.getCombatDefinitions().refreshBonuses();
		player.setNextAnimationNoPriority(new Animation(6695));
		player.setNextGraphics(new Graphics(1164));
		player.getPackets().sendGameMessage(
				"Your dragonfire shield glows more brightly.", true);
	}

	public static void empty(Player player) {
		player.lock(1);
		player.getCharges().addCharges(11283, -50, -1);
		player.setNextGraphics(new Graphics(1168));
		player.setNextAnimation(new Animation(6700));
		player.getPackets()
				.sendGameMessage("You empty your dragonfire shield.");
	}

	public static boolean isDragonFireShield(int id) {
		if (id == 1540 || id == 11286)
			return true;
		return false;
	}
}