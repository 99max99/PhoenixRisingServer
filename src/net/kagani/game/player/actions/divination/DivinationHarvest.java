package net.kagani.game.player.actions.divination;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.player.content.skillertasks.SkillTasks;
import net.kagani.utils.Utils;

/**
 * 
 * @author Trenton
 * 
 */
public class DivinationHarvest extends Action {

	private Wisp wisp;
	private WispInfo info;

	public DivinationHarvest(Player player, Object[] args) {
		this.wisp = (Wisp) args[0];
		this.info = (WispInfo) args[1];
	}

	public static boolean checkAll(Player player, WispInfo info) {
		if (player.getSkills().getLevel(Skills.DIVINATION) < info.getLevel()) {
			player.getPackets().sendGameMessage(
					"You need a Divination level of " + info.getLevel()
							+ " to harvest from this spring.");
			return false;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.getPackets().sendGameMessage(
					"You don't have enough space in your inventory.");
			return false;
		}
		return true;
	}

	public NPC getWisp() {
		return wisp;
	}

	public WispInfo getInfo() {
		return info;
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player, info)) {
			return false;
		}
		player.setNextAnimation(new Animation(21231));
		player.faceEntity(wisp);
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (wisp == null || wisp.isUsedUp())
			return false;
		if (!checkAll(player, info)) {
			return false;
		}
		return true;
	}

	@Override
	public void stop(Player player) {
		player.setNextAnimation(new Animation(21229));
	}

	@Override
	public int processWithDelay(Player player) {
		player.getInventory().addItem(info.getEnergyId(),
				player.getBoon(info.ordinal()) ? 2 : 1);
		int chance = Utils.random(2);
		if (Utils.random(30) >= 10) {
			if (info != WispInfo.PALE
					&& (Utils.random(30) <= 5 + (player.getSkills().getLevel(
							Skills.DIVINATION) - info.getLevel()))) {
				player.getSkills().addXp(Skills.DIVINATION,
						info.getHarvestXp() * 2);
				player.setNextGraphics(new Graphics(4236));
				player.getInventory().addItem(info.getEnrichedMemoryId(), 1);
			} else {
				player.getSkills()
						.addXp(Skills.DIVINATION, info.getHarvestXp());
				player.setNextGraphics(new Graphics(4235));
				if (chance != 2)
					player.getInventory().addItem(info.getMemoryId(), 1);
			}
		} else {
			player.getSkills().addXp(Skills.DIVINATION, info.getHarvestXp());
		}
		if (info.getMemoryId() == 29384) {
			player.getSkillTasks().decreaseTask(SkillTasks.DIV1);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV1_2);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV1_3);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV1_4);
		} else if (info.getMemoryId() == 29389) {
			player.getSkillTasks().decreaseTask(SkillTasks.DIV2);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV2_2);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV2_3);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV2_4);
		} else if (info.getMemoryId() == 29392) {
			player.getSkillTasks().decreaseTask(SkillTasks.DIV3);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV3_2);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV3_3);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV3_4);
		} else if (info.getMemoryId() == 29395) {
			player.getSkillTasks().decreaseTask(SkillTasks.DIV4);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV4_2);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV4_3);
			player.getSkillTasks().decreaseTask(SkillTasks.DIV4_4);
		}
		return 2;
	}
}