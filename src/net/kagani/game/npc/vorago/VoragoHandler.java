package net.kagani.game.npc.vorago;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public final class VoragoHandler {

private static final List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	
	/*** TODO Make it so they aren't initiated like this ***/
	public static Vorago vorago = new Vorago(17182, new WorldTile(3141, 6132, 0), -1, true, true);
	public static VoragoMinion scop1 = new VoragoMinion(17185, new WorldTile(3141, 6132, 0), -1, true, true);
	public static VoragoMinion scop2 = new VoragoMinion(17185, new WorldTile(3141, 6132, 0), -1, true, true);

	public static String CHALLENGER_NAME;
	

	public static int getPlayersCount() {
		return players.size();
	}
	

	public static List<Player> getPlayers() {
		return players;
	}
	
	private static void spawnNPCs() {
	    deleteNPCS();
	    if (vorago == null) {
		    vorago = new Vorago(17182, new WorldTile(3141, 6132, 0), -1, true, true);
		    vorago.setCantInteract(true);
		}
	    if (scop1 == null) {
		scop1 = new VoragoMinion(17185, new WorldTile(3141, 6132, 0), -1, true, true);
	    }
	    if (scop2 == null) {
		scop2 = new VoragoMinion(17185, new WorldTile(3141, 6132, 0), -1, true, true);
	    }
	}

	public static void addPlayer(Player player) {
		if (players.contains(player)) {
			player.setNextWorldTile(vorago.getRandomJump());
			System.out.println("Error with VoragoHandler.java");
			return;
		}
		players.add(player);
		//spawnNPCs();
		player.setNextWorldTile(vorago.getRandomJump());
		player.setNextAnimation(new Animation(20401));
	}

	public static void removePlayer(Player player) {
		players.remove(player);
		cancelFight();
	}

	public static void deleteNPCS() {
	    if (vorago != null) {
		vorago.finish();
		vorago = null;
	}
	if (scop1 != null) {
		scop1.finish();
		scop1 = null;
	}
	if (scop2 != null) {
		scop2.finish();
		scop2 = null;
	}
	}

	private static void cancelFight() {
		if (getPlayersCount() == 0) {
		    	spawnNPCs();
		    	vorago.resetVariables();
		}
	}

	public static ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(players.size());
		for (Player player : players) {
			if (player == null || player.isDead() || player.hasFinished() || !player.isRunning())
				continue;
			possibleTarget.add(player);
		}
		return possibleTarget;
	}

	public static void endFight() {
	   spawnNPCs();
	}

	public static void beginFight() {
				WorldTasksManager.schedule(new WorldTask() {
					private int count = 0;

					@Override
					public void run() {
						if (count == 1) {
							vorago.setNextWorldTile(vorago.getCentre());
							vorago.setNextAnimation(new Animation(20367));
							vorago.setNextGraphics(new Graphics(4020));
						}
						if (count == 4) {
							if (vorago != null)
								vorago.setCantInteract(false);
							else
								endFight();
						}
						count++;
					}
				}, 0, 1);
		}

}