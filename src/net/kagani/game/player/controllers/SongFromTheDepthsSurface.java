package net.kagani.game.player.controllers;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Equipment;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.QuestManager.Quests;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.controllers.SongFromTheDepths;
import net.kagani.game.player.cutscenes.Cutscene;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

import java.util.ArrayList;

/**
* @Auth Paty
**/

public class SongFromTheDepthsSurface extends Controller {
	
	private boolean login;
	private static ArrayList<NPC> npcs = new ArrayList<NPC>();
	private boolean tempvalue = false;
	
	@Override
	public void start() {
		FadingScreen.fade(player, new Runnable() {
			@Override
			public void run() {
				player.getPackets().sendGameMessage("Starting surface world controller.");
				player.getInterfaceManager().sendFadingInterface(1280);
			}
		});
	}
	
	
	@Override
	public boolean processObjectClick1(final WorldObject object) {
     	final int id = object.getId();
		final int ox = object.getX();
		final int oy = object.getY();
		final int py = player.getY();
		final int px = player.getX();
		
		if (id == 72450) {
			player.lock(6);
			//player.setNextAnimation(new Animation(?));
			FadingScreen.fade(player, new Runnable() {
				@Override
				public void run() {
					leave(0);
					player.getControlerManager().startControler("SongFromTheDepths");
				}

			});
        }
		
		return false;
	}


	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(player.getDeathAnimation());
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("You feel the effects of the potion fade and you return to the living realm.");
				} else if (loop == 3) {
					player.reset();
					removeControler();
					player.getInterfaceManager().removeFadingInterface();
					player.setNextWorldTile(new WorldTile (player.getX(), player.getY(), player.getPlane()));
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getMusicsManager().playMusicEffect(MusicsManager.DEATH_MUSIC_EFFECT);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}


	@Override
	public void magicTeleported(int type) {
		if (tempvalue) {
			this.forceClose();
		} else {
			tempvalue = true;
		}
	}

	public void leave(int type) {
		if (type == 0) {
			player.reset();
			removeControler();
			player.getInterfaceManager().removeFadingInterface();
			//player.setNextWorldTile(new WorldTile (player.getX(), player.getY(), player.getPlane()));
			player.setNextAnimation(new Animation(-1));
		}
		if (type == 1) {
			player.setNextAnimation(new Animation(-1));
			player.reset();
			removeControler();
			player.getInterfaceManager().removeFadingInterface();
		}
		if (type == 2) {
			removeControler();
			player.getPackets().sendGameMessage("You feel the effects of the potion fade and you return to the living realm.");
			FadingScreen.fade(player, new Runnable() {
				@Override
				public void run() {
					player.setNextAnimation(new Animation(-1));
					player.reset();
					player.getInterfaceManager().removeFadingInterface();
				}
			});
		}
	}
	
	@Override
	public boolean logout() {
		leave(0);
		return false;
	}
	
	@Override
	public boolean login() {
		player.getPackets().sendGameMessage("This should never happen [ERR: LOGIN SFTDS].");
		leave(0);
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {//why does this happen
	//player.getPackets().sendGameMessage("You can't do that at the moment [MAG].");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {//why does this happen
	//player.getPackets().sendGameMessage("You can't do that at the moment [IETM].");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
	player.getPackets().sendGameMessage("You can't do that at the moment [OBJ].");
		return false;
	}
	
	@Override
	public void moved() {
		if (player.getX() < 2938 || player.getY() < 3190 || player.getX() > 2998 || player.getY() > 3260) {
			leave(2);
		}
	}

	@Override
	public void forceClose() {
	    removeControler();
		player.setNextAnimation(new Animation(-1));
		player.getInterfaceManager().removeFadingInterface();
		player.reset();
	}
}
