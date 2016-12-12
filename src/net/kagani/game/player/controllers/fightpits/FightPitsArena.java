package net.kagani.game.player.controllers.fightpits;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.minigames.FightPits;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class FightPitsArena extends Controller {

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 68222) {
			FightPits.leaveArena(player, 1);
			return false;
		}
		return true;
	}

	// fuck it dont dare touching here again or dragonkk(me) kills u irl :D btw
	// nice code it keeps nulling, fixed

	@Override
	public boolean logout() {
		FightPits.leaveArena(player, 0);
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You can't teleport out of the arena!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getPackets().sendGameMessage(
				"You can't teleport out of the arena!");
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		FightPits.leaveArena(player, 3); // teled out somehow, impossible usualy
	}

	@Override
	public boolean login() { // shouldnt happen
		removeControler();
		FightPits.leaveArena(player, 2);
		return false;
	}

	@Override
	public void forceClose() {
		FightPits.leaveArena(player, 3);
	}

	@Override
	public boolean canAttack(Entity target) {
		if (target instanceof Player) {
			if (canHit(target))
				return true;
			player.getPackets().sendGameMessage(
					"You're not allowed to attack yet!");
			return false;
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		return FightPits.canFight();
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
							"You have been defeated!");
				} else if (loop == 3) {
					FightPits.leaveArena(player, 2);
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
	public void sendInterfaces() {
		player.getInterfaceManager().sendMinigameInterface(373);
		if (FightPits.currentChampion != null)
			player.getPackets().sendIComponentText(373, 10,
					"Current Champion: JaLYt-Ket-" + FightPits.currentChampion);
	}
}
