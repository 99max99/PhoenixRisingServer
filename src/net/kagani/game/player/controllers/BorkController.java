package net.kagani.game.player.controllers;

import java.util.TimerTask;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.map.MapInstance;
import net.kagani.game.map.MapInstance.Stages;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.others.Bork;
import net.kagani.game.player.MusicsManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.FadingScreen;
import net.kagani.game.player.cutscenes.Cutscene;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

public class BorkController extends Controller {

	private static final WorldTile OUTSIDE = new WorldTile(3143, 5545, 0);

	public static void enterBork(Player player) {
		if (Utils.currentTimeMillis() - player.getLastBork() <= ((player
				.isAMemberGreaterThanGold() ? 8 : 12) * 60 * 60 * 1000)) {
			player.getPackets()
					.sendGameMessage(
							"The portal appears to have stopped working for now. Perhaps you should return later?");
			return;
		}
		player.getControlerManager().startControler("BorkController");
	}

	private MapInstance instance;
	private NPC surokMagis;
	private Bork bork;
	private boolean earthquake;
	private int timer;

	@Override
	public void start() {
		enter();
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 77745) {
			leave(2);
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 7136 || npc.getId() == 7137) {
			player.getPackets().sendGameMessage("Your attack has no effect.");
			return false;
		}
		return true;
	}

	public void enter() {
		instance = new MapInstance(385, 690);
		player.lock();
		player.setNextAnimation(new Animation(17803));
		final long time = FadingScreen.fade(player);
		instance.load(new Runnable() {

			@Override
			public void run() {
				FadingScreen.unfade(player, time, new Runnable() {
					@Override
					public void run() {
						player.setNextWorldTile(instance.getTile(25, 17));
						surokMagis = World.spawnNPC(
								player.getLastBork() != 0 ? 7137 : 7136,
								instance.getTile(22, 20), -1, true, true);
						surokMagis.setCantSetTargetAutoRelatio(true);
						bork = new Bork(instance.getTile(12, 15),
								BorkController.this);
						sendCutscene(692, 10000);
					}
				});
			}

		});
	}

	// 0 - logout
	// 1 - teleport / death
	// 2 - leave
	public void leave(int type) {
		player.stopAll();
		if (type != 0) {
			if (type == 1)
				player.lock(3);
			else
				player.useStairs(17803, OUTSIDE, 2, 3);
			player.getMusicsManager().reset();
			if (earthquake)
				player.getPackets().sendStopCameraShake();
			removeControler();
		} else
			player.setLocation(OUTSIDE);
		instance.destroy(null);
	}

	@Override
	public boolean logout() {
		leave(0);
		return true;
	}

	@Override
	public boolean login() {
		player.setNextWorldTile(OUTSIDE);
		return true; // shouldnt happen
	}

	@Override
	public void magicTeleported(int type) {
		leave(1);
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
					leave(1);
					player.getControlerManager().startControler("DeathEvent",
							OUTSIDE, player.hasSkull());
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

	public void startFight() {
		player.unlock();
		bork.setCantInteract(false);
		surokMagis.setTarget(player);
	}

	public void startEarthquake() {
		player.unlock();
		player.getPackets()
				.sendGameMessage(
						"Something is shaking the whole cavern! You should get out of here quick!");
		player.getPackets().sendCameraShake(3, 12, 25, 12, 25);
		earthquake = true;
		timer = 30;
	}

	@Override
	public void process() {
		if (!earthquake)
			return;
		if (timer > 0) {
			timer--;
			return;
		}
		player.applyHit(new Hit(player, Utils.random(499) + 1,
				HitLook.REGULAR_DAMAGE));
		timer = 30;
	}

	public void spawnMinions() {
		player.getPackets().sendGameMessage(
				"Bork strikes the ground with his axe.");
		sendCutscene(691, 4200);
	}

	public void killBork() {
		bork = null;
		sendCutscene(693, 5400);
		surokMagis.setCantInteract(true);
		surokMagis.setNextFaceEntity(null);
		surokMagis.setNextWorldTile(instance.getTile(22, 20));
		player.getSkills().addXp(Skills.SLAYER,
				player.getLastBork() == 0 ? 500 : 300);
		player.setLastBork(Utils.currentTimeMillis());
	}

	public Stages getStage() {
		return instance.getStage();
	}

	public void sendCutscene(final int interfaceId, long time) {
		player.lock();
		player.getMusicsManager().forcePlayMusic(587);
		player.getInterfaceManager().sendMinigameInterface(interfaceId);
		player.getPackets().sendBlackOut(2);
		GameExecutorManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					if (instance.getStage() != Stages.RUNNING)
						return;
					player.getInterfaceManager().removeMinigameInterface();
					if (interfaceId == 692) {
						player.resetReceivedHits();
						player.getDialogueManager().startDialogue("SurokMagis",
								surokMagis.getId(), BorkController.this);
						player.getPackets().sendBlackOut(0);
					} else if (interfaceId == 693) {
						WorldTile lookTo = instance.getTile(22, 22);
						player.getPackets().sendCameraLook(
								Cutscene.getX(player, lookTo.getX()),
								Cutscene.getY(player, lookTo.getY()), 1000);
						WorldTile posTile = instance.getTile(22, 15);
						player.getPackets().sendCameraPos(
								Cutscene.getX(player, posTile.getX()),
								Cutscene.getY(player, posTile.getY()), 1500);
						surokMagis.setNextFaceWorldTile(posTile);
						surokMagis.setNextForceTalk(new ForceTalk(
								"Zamorak, avenge me!"));
						WorldTasksManager.schedule(new WorldTask() {

							boolean teleported;

							@Override
							public void run() {
								if (instance.getStage() != Stages.RUNNING) {
									stop();
									return;
								}
								if (!teleported) {
									surokMagis.setNextAnimation(new Animation(
											8939));
									surokMagis.setNextGraphics(new Graphics(
											1576));
									teleported = true;
								} else {
									surokMagis.finish();
									surokMagis = null;
									player.getDialogueManager().startDialogue(
											"SurokMagisT", BorkController.this);
									player.getPackets().sendBlackOut(0);
									player.getPackets().sendResetCamera();
									stop();
								}
							}

						}, 6, 3);
					} else {
						if (interfaceId == 691)
							bork.setMinions();
						player.unlock();
						player.getPackets().sendBlackOut(0);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time);
	}
}