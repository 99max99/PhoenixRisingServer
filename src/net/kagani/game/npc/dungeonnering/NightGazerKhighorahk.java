package net.kagani.game.npc.dungeonnering;

import java.util.TimerTask;

import net.kagani.executor.GameExecutorManager;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.dungeoneering.DungeonConstants;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.utils.Logger;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public final class NightGazerKhighorahk extends DungeonBoss {

	private boolean secondStage;
	private boolean usedSpecial;
	private int lightCount;

	public NightGazerKhighorahk(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
		setCantFollowUnderCombat(true); // force cant walk
	}

	public boolean isSecondStage() {
		return secondStage;
	}

	@Override
	public void sendDeath(final Entity source) {
		if (!secondStage) {
			secondStage = true;
			setNextAnimation(new Animation(getCombatDefinitions()
					.getDeathEmote()));
			setNextNPCTransformation(9739);
			setCombatLevel((int) (getCombatLevel() * 0.85)); // 15% nerf
			setHitpoints(getMaxHitpoints());
			resetBonuses();
			return;
		}
		super.sendDeath(source);
	}

	public boolean isUsedSpecial() {
		return usedSpecial;
	}

	public void setUsedSpecial(boolean usedSpecial) {
		this.usedSpecial = usedSpecial;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (!secondStage)
			reduceHit(hit);
		super.handleIngoingHit(hit);
	}

	public void reduceHit(Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE
				&& hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		hit.setDamage((int) (hit.getDamage() * lightCount * 0.25));
	}

	public void lightPillar(Player player, WorldObject object) {
		if (lightCount >= 4)
			return;
		if (!player.getInventory().containsItemToolBelt(
				DungeonConstants.TINDERBOX)) {
			player.getPackets().sendGameMessage(
					"You need a tinderbox to do this.");
			return;
		}
		player.setNextAnimation(new Animation(833));
		final WorldObject light = new WorldObject(object);
		light.setId(object.getId() + 1);

		World.spawnObject(light);
		lightCount++;

		GameExecutorManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					lightCount--;
					World.removeObject(light);
					for (Entity target : getPossibleTargets()) {
						if (target.withinDistance(light, 2)) {
							target.applyHit(new Hit(NightGazerKhighorahk.this,
									Utils.random((int) (target
											.getMaxHitpoints() * 0.25)) + 1,
									HitLook.REGULAR_DAMAGE));
							if (target instanceof Player)
								((Player) target)
										.getPackets()
										.sendGameMessage(
												"You are damaged by the shadows engulfing the pillar of light.");
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}

			}

		}, 30000 - getManager().getParty().getSize() * 3000);

	}

	/*
	 * @Override public void sendDeath(final Entity source) { final
	 * NPCCombatDefinitions defs = getCombatDefinitions(); resetWalkSteps();
	 * getCombat().removeTarget(); setNextAnimation(null);
	 * WorldTasksManager.schedule(new WorldTask() { int loop;
	 * 
	 * @Override public void run() { if (loop == 0) { setNextAnimation(new
	 * Animation(defs.getDeathEmote())); } else if (loop >=
	 * defs.getDeathDelay()) { if (source instanceof Player) ((Player)
	 * source).getControlerManager().processNPCDeath(NightGazerKhighorahk.this);
	 * drop(); reset(); if (source.getAttackedBy() == NightGazerKhighorahk.this)
	 * { //no need to wait after u kill source.setAttackedByDelay(0);
	 * source.setAttackedBy(null); source.setFindTargetDelay(0); }
	 * setCantInteract(true); setNextNPCTransformation(9781); stop(); } loop++;
	 * } }, 0, 1); getManager().openStairs(getReference()); }
	 */

}
