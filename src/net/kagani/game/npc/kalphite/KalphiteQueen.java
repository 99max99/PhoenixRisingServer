package net.kagani.game.npc.kalphite;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.HeadIcon;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.TimersManager.RecordKey;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class KalphiteQueen extends NPC {

	private HeadIcon[][] PRAYER_ICONS = { { new HeadIcon(440, 6) },// Familiar,
																	// Magic,
																	// Range
			{ new HeadIcon(440, 0) },// Melee
	};

	// private int nextSoldierTick;

	public KalphiteQueen(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(0);
		setForceAgressive(true);
		setIntelligentRouteFinder(true);
		requestIconRefresh();
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (getId() == 1158 || getId() == 16707) {
			Entity src = hit.getSource();
			if (src instanceof Familiar
					|| hit.getLook() == HitLook.RANGE_DAMAGE
					|| hit.getLook() == HitLook.MAGIC_DAMAGE)
				hit.setDamage(0);
		} else {
			if (hit.getLook() == HitLook.MELEE_DAMAGE)
				hit.setDamage(0);
		}
		super.handleIngoingHit(hit);
	}

	@Override
	public void processNPC() {
		if (getId() == 1160 || getId() == 16708) {
			/*
			 * nextSoldierTick++; if (nextSoldierTick % 60 == 0) {//Approx 30
			 * seconds.
			 * 
			 * nextSoldierTick = 0; }
			 */
		}
		super.processNPC();
	}

	@Override
	public void sendDeath(Entity source) {
		if (getId() != 1158 && getId() != 16707)
			increaseKills(RecordKey.KALPHITE_QUEEN, false);
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
					if (getId() == 1158 || getId() == 16707) {
						setCantInteract(true);
						setNextNPCTransformation(getId() == 16707 ? 16708
								: 1160);
						setNextGraphics(new Graphics(getId() == 16707 ? 5037
								: 5038));
						setNextAnimation(new Animation(24293));
						setDirection(Utils.getAngle(-1, 0));// I hope this is
															// west XD
						WorldTasksManager.schedule(new WorldTask() {

							@Override
							public void run() {
								reset();
								setCantInteract(false);
								requestIconRefresh();
							}

						}, 6);
					} else {
						giveXP();
						drop();
						reset();
						setLocation(getRespawnTile());
						loadMapRegions();
						finish();
						if (!isSpawned())
							setRespawnTask();
						setNextNPCTransformation(getId() == 16708 ? 16707
								: 1158);
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public HeadIcon[] getIcons() {
		return PRAYER_ICONS[getId() == 1160 || getId() == 16708 ? 1 : 0];
	}
}
