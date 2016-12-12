package net.kagani.game.minigames.warbands;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.others.Pet;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

public class WarbandsMinion extends NPC {

	private static final long serialVersionUID = 1906246101481738286L;
	public Warbands warband;
	public boolean attackable;

	public WarbandsMinion(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceAgressive(true);
		setForceMultiArea(true);
	}

	@Override
	public void processEntity() {
		super.processEntity();
		if (Warbands.warband == null || this.warband != null
				&& this.warband != Warbands.warband)
			sendDeath(this);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTargets = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playerIndexes != null) {
				for (int index : playerIndexes) {
					Player p = World.getPlayers().get(index);
					if (p == null || p.isDead() || p.hasFinished()
							|| !p.hasStarted() || !p.withinDistance(this, 64)
							|| !clipedProjectile(p, false))
						continue;
					possibleTargets.add(p);
				}
			}
			List<Integer> npcIndexes = World.getRegion(regionId)
					.getNPCsIndexes();
			if (npcIndexes != null) {
				for (int npcIndex : npcIndexes) {
					NPC n = World.getNPCs().get(npcIndex);
					if (n == null || n.isDead() || n.hasFinished()
							|| !n.withinDistance(this, 64)
							|| !clipedProjectile(n, false)
							|| n instanceof WarbandsMinion
							|| n instanceof WarbandsChief || n instanceof Pet)
						continue;
					possibleTargets.add(n);
				}
			}
		}
		return possibleTargets;
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getSource() instanceof Player) {
			Player player = (Player) hit.getSource();
			if (this.warband == null) {
				hit.setDamage(0);
				player.getPackets()
						.sendGameMessage(
								"You cannot attack this npc while there is not an active warband event!",
								true);
			}
		}
		super.handleIngoingHit(hit);
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs != null ? defs
							.getDeathEmote() : 836));
				} else if (loop >= defs.getDeathDelay()) {
					if (source != null && source instanceof Player) {
						Player p = (Player) source;
						if (p != null && p.hasStarted() && !p.hasFinished())
							p.getSkills().addXp(Skills.SLAYER, 25);
					}
					if (Warbands.warband != null && warband != null
							&& warband == Warbands.warband)
						Warbands.warband.remainingOccupants--;
					reset();
					finish();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}