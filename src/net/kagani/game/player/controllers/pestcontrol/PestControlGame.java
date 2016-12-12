package net.kagani.game.player.controllers.pestcontrol;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.minigames.pest.Lander;
import net.kagani.game.minigames.pest.PestControl;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class PestControlGame extends Controller {

	private PestControl control;
	private double points;

	@Override
	public void start() {
		control = (PestControl) getArguments()[0];
		setArguments(null);
		setPoints(0.0D);
		sendInterfaces();
		sendMusic();
	}

	private void sendMusic() {
		player.getMusicsManager().playMusic(588);
	}

	@Override
	public void sendInterfaces() {
		refreshDamagePoints();
		player.getInterfaceManager().sendMinigameInterface(408);
	}

	private void refreshDamagePoints() {
		boolean isGreen = getPoints() > 5000;
		player.getPackets().sendIComponentText(408, 7,
				(isGreen ? "<col=75AE49>" : "") + (int) getPoints() + "</col>");
	}

	@Override
	public void forceClose() {
		if (control != null) {
			if (control.getPortalCount() != 0)
				control.getPlayers().remove(player);
			player.useStairs(-1, Lander.getLanders()[control.getPestData()
					.ordinal()].getLanderRequirement().getExitTile(), 1, 2);
		} else
			player.useStairs(-1, new WorldTile(2657, 2639, 0), 1, 2);
		player.getInterfaceManager().removeMinigameInterface();
		player.reset();
	}

	@Override
	public void magicTeleported(int teleType) {
		player.getControlerManager().forceStop();
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave the pest control area like this.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave the pest control area like this.");
		return false;
	}

	@Override
	public boolean canMove(int dir) {
		WorldTile toTile = new WorldTile(player.getX()
				+ Utils.DIRECTION_DELTA_X[dir], player.getY()
				+ Utils.DIRECTION_DELTA_Y[dir], player.getPlane());
		return !control.isBrawlerAt(toTile);
	}

	@Override
	public boolean login() { // shouldnt happen
		player.useStairs(-1, new WorldTile(2657, 2639, 0), 1, 2);
		return true;
	}

	@Override
	public boolean logout() {
		if (control != null) {
			player.reset();
			player.setLocation(new WorldTile(2657, 2639, 0));
			control.getPlayers().remove(player);
		}
		return true;
	}

	@Override
	public boolean canSummonFamiliar() {
		player.getPackets()
				.sendGameMessage(
						"You feel it's best to keep your Familiar away during this game.");
		return false;
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
					player.reset();
					player.setNextWorldTile(control.getWorldTile(
							35 - Utils.random(4), 54 - (Utils.random(3))));
					player.setNextAnimation(new Animation(-1));
					sendMusic();
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
	public void processIncommingHit(Hit hit, Entity target) {
		if (hit.getLook() == HitLook.MELEE_DAMAGE
				|| hit.getLook() == HitLook.RANGE_DAMAGE
				|| hit.getLook() == HitLook.MAGIC_DAMAGE) {
			int hit_damage = hit.getDamage();
			if (hit_damage > 0) {
				setPoints(getPoints() + hit.getDamage());
				refreshDamagePoints();
			}
		}
	}

	public double getPoints() {
		return points;
	}

	public void setPoints(double points) {
		this.points = points;
	}
}
