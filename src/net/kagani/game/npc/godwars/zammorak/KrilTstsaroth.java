package net.kagani.game.npc.godwars.zammorak;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.minigames.GodWarsBosses;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.player.Player;
import net.kagani.game.player.TimersManager.RecordKey;
import net.kagani.game.player.content.FriendsChat;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.player.controllers.GodWars;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class KrilTstsaroth extends NPC {

	public KrilTstsaroth(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setIntelligentRouteFinder(true);
		setDropRateFactor(3); // triples chances
		setLureDelay(30000);// Lureable
	}

	/*
	 * gotta override else setRespawnTask override doesnt work
	 */
	@Override
	public void sendDeath(final Entity source) {
		increaseKills(RecordKey.KRIL_TSUTSAROTH, false);
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						List<Player> players = FriendsChat
								.getLootSharingPeople(player);
						if (players != null) {
							for (Player p : players) {
								if (p == null)
									continue;
								Controller controler = p.getControlerManager()
										.getControler();
								if (controler != null
										&& controler instanceof GodWars) {
									GodWars godControler = (GodWars) controler;
									godControler.incrementKillCount(3);
								}
							}
						}
					}
					giveXP();
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		final NPC npc = this;
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				setFinished(false);
				World.addNPC(npc);
				npc.setLastRegionId(0);
				World.updateEntityRegion(npc);
				loadMapRegions();
				GodWarsBosses.respawnZammyMinions();
			}
		}, getCombatDefinitions().getRespawnDelay() * 600,
				TimeUnit.MILLISECONDS);
	}

}
