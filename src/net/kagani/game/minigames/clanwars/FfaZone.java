package net.kagani.game.minigames.clanwars;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

/**
 * Handles the FFA Clan Wars zone.
 * 
 * @author Emperor
 * 
 */
public final class FfaZone extends Controller {

	/**
	 * If the player was in the ffa pvp area.
	 */
	private transient boolean wasInArea;

	@Override
	public void start() {
		sendInterfaces();
	}

	private boolean isRisk() {
		if (getArguments() == null || getArguments().length < 1)
			return false;
		return (Boolean) getArguments()[0];
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendMinigameInterface(789);
	}

	@Override
	public boolean sendDeath() {
		player.lock(8);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(player.getDeathAnimation());
				} else if (loop == 1) {
					player.getPackets().sendGameMessage(
							"Oh dear, you have died.");
				} else if (loop == 3) {
					if (isRisk()) {
						Player killer = player
								.getMostDamageReceivedSourcePlayer();
						if (killer != null) {
							player.giveXP();
							killer.reduceDamage(player);
							player.sendItemsOnDeath(killer, true);
						}
					}
					player.setNextWorldTile(new WorldTile(2993, 9679, 0));
					remove(true);
					player.reset();
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getMusicsManager().playMusicEffect(
							MusicsManager.DEATH_MUSIC_EFFECT);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		remove(true);
	}

	@Override
	public void forceClose() {
		remove(false);
	}

	private void remove(boolean needRemove) {
		if (needRemove)
			removeControler();
		if (wasInArea)
			player.setCanPvp(false);
		player.getInterfaceManager().removeMinigameInterface();
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		switch (object.getId()) {
		case 38700:
			remove(true);
			player.useStairs(-1, new WorldTile(2993, 9679, 0), 0, 1);
			// player.getControlerManager().startControler("clan_wars_request");
			return false;
		}
		return true;
	}

	@Override
	public void moved() {
		boolean inArea = inPvpArea(player);
		if (inArea && !wasInArea) {
			player.setCanPvp(true);
			wasInArea = true;
		} else if (!inArea && wasInArea) {
			player.setCanPvp(false);
			wasInArea = false;
		}
	}

	@Override
	public boolean canAttack(Entity target) {
		if (canHit(target))
			return true;
		return false;
	}

	@Override
	public boolean canHit(Entity target) {
		if (target instanceof NPC)
			return true;
		Player p2 = (Player) target;
		if (player.isBeginningAccount() || p2.isBeginningAccount()) {
			player.getPackets()
					.sendGameMessage(
							"Starter acccounts cannot attack or be attacked for the first hour of playing time.");
			return false;
		}
		return true;
	}

	private boolean inPvpArea(Player player) {
		return player.getY() >= 5512;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean login() {
		start();
		moved();
		return false;
	}

	/**
	 * Checks if a player's overload effect is changed (due to being in the risk
	 * ffa zone, in pvp)
	 * 
	 * @param player
	 *            The player.
	 * @return {@code True} if so.
	 */
	public static boolean isOverloadChanged(Player player) {
		if (!(player.getControlerManager().getControler() instanceof FfaZone)) {
			return false;
		}
		return player.isCanPvp()
				&& ((FfaZone) player.getControlerManager().getControler())
						.isRisk();
	}
}