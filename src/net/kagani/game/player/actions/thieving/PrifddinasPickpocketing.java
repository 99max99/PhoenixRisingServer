package net.kagani.game.player.actions.thieving;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class PrifddinasPickpocketing extends Action {

	/**
	 * @author: Dylan Page
	 */

	private NPC npc;

	public PrifddinasPickpocketing(NPC npc) {
		this.npc = npc;
	}

	@Override
	public boolean start(Player player) {
		if (player.getInventory().getFreeSlots() < 1) {
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			player.stopAll();
			return false;
		}
		switch (npc.getId()) {
		case 20312: // Ithell
		case 20313:
		case 20314:
		case 20315:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 92) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 92 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20316: // Amlodd
		case 20317:
		case 20318:
		case 20319:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 94) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 94 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20320: // Hefin
		case 20321:
		case 20322:
		case 20323:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 96) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 96 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20324: // Meilyr
		case 20325:
		case 20326:
		case 20327:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 98) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 98 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20113: // Iorwerth
		case 20114:
		case 20115:
		case 20116:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 91) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 91 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20125: // Trahaearn
		case 20126:
		case 20127:
		case 20128:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 95) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 95 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20121: // Cadarn
		case 20122:
		case 20123:
		case 20124:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 93) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 93 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20117: // Crwys
		case 20118:
		case 20119:
		case 20120:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 97) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 97 to pickpocket from this npc.");
				return false;
			}
			player.setNextAnimation(new Animation(24887));
			return true;
		}
		return false;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(new Animation(24887));
		if (player.getX() == npc.getX() && player.getY() == npc.getY())
			player.addWalkSteps(npc.getX() - 1, npc.getY(), -1, true);
		player.addWalkSteps(npc.getX(), npc.getY() - 1, -1, true);
		player.faceEntity(npc);
		if (checkAll(player)) {
			if (Utils.random(400) == 0) {
				player.stopAll();
			}
			if (Utils.random(player.getSkills().getLevel(Skills.THIEVING) * 10) == 0) {
				player.stopAll();
				npc.faceEntity(player);
				player.setNextAnimation(new Animation(424));
				player.setNextGraphics(new Graphics(80, 5, 60));
				player.getPackets().sendGameMessage(
						"You have raised their suspicions.", true);
				player.applyHit(new Hit(player, 300, HitLook.REGULAR_DAMAGE));
				player.lock(3);
			}
			return true;
		}
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		player.getSkills().addXp(Skills.THIEVING, xpAmount());
		addItems(player);
		return Utils.random(15, 20);
	}

	private void addItems(Player player) {
		if (player.getInventory().getFreeSlots() < 1) {
			player.getPackets().sendGameMessage(
					"Not enough space in your inventory.");
			player.stopAll();
			return;
		}
		player.getMoneyPouch().setAmount(Utils.random(4350, 7460), false);
		switch (Utils.random(16)) {
		case 0:
			player.getInventory().addItem(232, Utils.random(2, 15));
			break;
		case 1:
			player.getInventory().addItem(386, Utils.random(2, 15));
			break;
		case 2:
			player.getInventory().addItem(454, Utils.random(2, 15));
			break;
		case 3:
			player.getInventory().addItem(445, Utils.random(2, 15));
			break;
		case 4:
			player.getInventory().addItem(8779, Utils.random(2, 15));
			break;
		case 5:
			player.getInventory().addItem(1778, Utils.random(2, 15));
			break;
		case 7:
			player.getInventory().addItem(242, Utils.random(2, 15));
			break;
		case 9:
			player.getInventory().addItem(150, Utils.random(2, 15));
			break;
		case 10:
			player.getInventory().addItem(162, Utils.random(2, 15));
			break;
		case 11:
			player.getInventory().addItem(168, Utils.random(2, 15));
			break;
		case 6:
		case 8:
		case 12:
		case 13:
		case 14:
		case 15:
			break;
		}
	}

	private int xpAmount() {
		switch (npc.getId()) {
		case 20312: // Ithell
		case 20313:
		case 20314:
		case 20315:
			return 130;

		case 20316: // Amlodd
		case 20317:
		case 20318:
		case 20319:
			return 140;

		case 20320: // Hefin
		case 20321:
		case 20322:
		case 20323:
			return 150;

		case 20324: // Meilyr
		case 20325:
		case 20326:
		case 20327:
			return 170;

		case 20113: // Iorwerth
		case 20114:
		case 20115:
		case 20116:
			return 125;

		case 20125: // Trahaearn
		case 20126:
		case 20127:
		case 20128:
			return 145;

		case 20121: // Cadarn
		case 20122:
		case 20123:
		case 20124:
			return 135;

		case 20117: // Crwys
		case 20118:
		case 20119:
		case 20120:
			return 155;
		}
		return 10;
	}

	private boolean checkAll(Player player) {
		switch (npc.getId()) {
		case 20312: // Ithell
		case 20313:
		case 20314:
		case 20315:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 92) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 92 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20316: // Amlodd
		case 20317:
		case 20318:
		case 20319:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 94) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 94 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20320: // Hefin
		case 20321:
		case 20322:
		case 20323:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 96) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 96 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20324: // Meilyr
		case 20325:
		case 20326:
		case 20327:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 98) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 98 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20113: // Iorwerth
		case 20114:
		case 20115:
		case 20116:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 91) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 91 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20125: // Trahaearn
		case 20126:
		case 20127:
		case 20128:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 95) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 95 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20121: // Cadarn
		case 20122:
		case 20123:
		case 20124:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 93) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 93 to pickpocket from this npc.");
				return false;
			}
			return true;
		case 20117: // Crwys
		case 20118:
		case 20119:
		case 20120:
			if (player.getSkills().getLevelForXp(Skills.THIEVING) < 97) {
				player.getPackets()
						.sendGameMessage(
								"You need to have a thieving level of at least 97 to pickpocket from this npc.");
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void stop(Player player) {
		npc.setNextFaceEntity(null);
		player.getEmotesManager().setNextEmoteEnd(2400);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(-1));
				player.getAppearence().setRenderEmote(-1);
			}
		}, 3);
	}
}