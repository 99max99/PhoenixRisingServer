package net.kagani.game.player.content.surpriseevents;

import java.util.List;

import net.kagani.Settings;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public class TeamVsTeamController extends Controller {

	private transient TeamVsTeam game;
	private transient boolean isRed;
	private transient Player[] hint_targets;

	@Override
	public void start() {
		game = (TeamVsTeam) getArguments()[0];
		isRed = (Boolean) getArguments()[1];
		hint_targets = new Player[7];
		setArguments(null);
		sendInterfaces();
	}

	@Override
	public boolean logout() {
		player.setLocation(Settings.HOME_LOCATION);
		player.getControlerManager().forceStop();
		return true;
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendMinigameInterface(730);
		player.setLargeSceneView(true);
	}

	@Override
	public void process() {

		if (game == null
				|| (getPlayer().getX() < game.getArena().minX()
						|| getPlayer().getX() > game.getArena().maxX()
						|| player.getY() < game.getArena().minY() || player
						.getY() > game.getArena().maxY())) {
			getPlayer().getPackets().sendGameMessage(
					"An error has occured, please submit bug report.");
			player.getControlerManager().forceStop();
			return;
		}

		synchronized (game.getLock()) {
			player.getPackets().sendHideIComponent(730, 18, true);
			player.getPackets().sendIComponentText(730, 17,
					"            <col=FF0000>Team Vs Team</col>");
			player.getPackets().sendIComponentText(
					730,
					26,
					"<col=0000FF>Blue: " + game.blueScore()
							+ "</col><col=FF0000>                 Red: "
							+ game.redScore() + "</col>");
		}

		if (game.getPhase() == 2) {
			long minsleft = Math.max(0,
					(game.startTime() - Utils.currentTimeMillis())
							/ (1000 * 60));
			player.getPackets().sendIComponentText(
					730,
					7,
					"Event starts in: "
							+ (minsleft < 1 ? "<lt>1 min."
									: (minsleft + " min.")));
			player.getVarsManager().sendVar(1215, 0);

		} else if (game.getPhase() == 3) {
			player.getPackets().sendHideIComponent(730, 7, true);
			double percent = 1d - ((double) Math.max(0,
					game.endTime() - Utils.currentTimeMillis()) / (double) (TeamVsTeam.GAME_MINS * 60 * 1000));
			player.getVarsManager().sendVar(1215, (int) (percent * 1000d));

			synchronized (game.getLock()) {
				List<Player> enemy = isRed ? game.getBlue() : game.getRed();

				for (int i = 0; i < hint_targets.length; i++) {
					if (hint_targets[i] != null
							&& !enemy.contains(hint_targets[i])) {
						hint_targets[i] = null;
						player.getHintIconsManager().removeHintIcon(i);
					}
				}

				main: for (Player pl : enemy) {
					if (pl == player || Utils.random(2) == 0)
						continue;

					int free_slot = -1;
					for (int i = 0; i < hint_targets.length; i++) {
						if (free_slot == -1 && hint_targets[i] == null)
							free_slot = i;

						if (hint_targets[i] == pl)
							continue main;
					}

					if (free_slot == -1)
						break;

					player.getHintIconsManager().addHintIcon(free_slot,
							hint_targets[free_slot] = pl, 9, -1, true);
				}
			}
		}
	}

	@Override
	public boolean canAttack(Entity target) {
		if (!(target instanceof Player))
			return true;
		if (game == null || game.getPhase() != 3)
			return false;
		synchronized (game.getLock()) {
			if ((isRed && !game.getBlue().contains(target))
					|| (!isRed && !game.getRed().contains(target))) {
				player.getPackets().sendGameMessage(
						"You cannot attack this player.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canHit(Entity target) {
		if (!(target instanceof Player))
			return true;
		if (game == null || game.getPhase() != 3)
			return false;
		synchronized (game.getLock()) {
			if ((isRed && !game.getBlue().contains(target))
					|| (!isRed && !game.getRed().contains(target)))
				return false;
		}
		return true;
	}

	@Override
	public boolean canPlayerOption2(Player target) {
		return false;
	}

	@Override
	public boolean canPlayerOption3(Player target) {
		return false;
	}

	@Override
	public boolean canPlayerOption4(Player target) {
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile tile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage",
				"You can't leave just like that!");
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControlerManager().forceStop();
	}

	@Override
	public void processIngoingHit(final Hit hit) {
		if (hit.getSource() != null
				&& hit.getSource() != null
				&& player.isBound()
				&& (hit.getLook() == HitLook.RANGE_DAMAGE || hit.getLook() == HitLook.MAGIC_DAMAGE)) {
			final int reflectdmg = Math.max(10,
					Math.min((int) (hit.getDamage() * 0.6d), 350));
			if (Utils.getDistance(player, hit.getSource()) >= 2) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						hit.getSource().applyHit(
								new Hit(player, reflectdmg,
										HitLook.REFLECTED_DAMAGE));
					}
				});
			}
		}
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(player.getDeathAnimation());
					player.getPackets().sendGameMessage(
							"Oh dear, you have died!");
					if (game != null)
						game.inc(!isRed);
				} else if (loop == 1) {
					if (player.getFamiliar() != null)
						player.getFamiliar().sendDeath(player);
				} else if (loop == 3) {
					Player killer = player.getMostDamageReceivedSourcePlayer();
					if (killer != null) {
						player.giveXP();
						killer.reduceDamage(player);

						boolean target = false;
						Controller controller = killer.getControlerManager()
								.getControler();
						if (controller != null
								&& controller instanceof TeamVsTeamController) {
							Player[] targets = ((TeamVsTeamController) controller).hint_targets;
							if (targets != null) {
								for (int i = 0; i < targets.length; i++)
									if (targets[i] == player) {
										targets[i] = null;
										killer.getHintIconsManager()
												.removeHintIcon(i);
										target = true;
										break;
									}
							}
						}

						if (target) {
							killer.getPackets()
									.sendGameMessage(
											"You have killed your target, congratulations!");
							Helper.dropDeathReward(killer, player,
									TeamVsTeam.REWARD_DROP_MOD_PER_KILL,
									TeamVsTeam.REWARD_CASH_PER_KILL);
						} else {
							killer.getPackets()
									.sendGameMessage(
											"You have scored a kill, however it wasn't your target.");
						}

					}

					if (game != null && game.getArena() != null) {
						player.setNextWorldTile(game.getArena().randomSpawn());
					}

					player.stopAll();
					player.reset();
					for (int i = 0; i < hint_targets.length; i++)
						if (hint_targets[i] != null)
							player.getHintIconsManager().addHintIcon(i,
									hint_targets[i], 9, -1, true);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.resetWalkSteps();
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().removeMinigameInterface();
		player.getHintIconsManager().removeAll();
		player.setLargeSceneView(false);

		if (game != null)
			game.forceleave(player);
		else {
			player.setCanPvp(false);
			player.stopAll();
			player.reset();
			player.getAppearence().setHidden(false);
			player.getAppearence().setIdentityHide(false);
			player.getAppearence().setForcedCape(-1);
			player.useStairs(-1, Settings.HOME_LOCATION, 0, 1);
		}
	}
}
