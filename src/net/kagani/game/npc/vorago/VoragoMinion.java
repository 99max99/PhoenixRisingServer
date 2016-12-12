package net.kagani.game.npc.vorago;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.vorago.combat.VoragoMinionCombat;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

@SuppressWarnings({ "serial" })
public class VoragoMinion extends NPC {

	Vorago rago = VoragoHandler.vorago;
	int killersDamage[] = new int[VoragoHandler.getPlayersCount()];
	Player players[] = VoragoHandler.getPlayers().toArray(new Player[VoragoHandler.getPlayersCount()]);
	NPCCombatDefinitions defs = getCombatDefinitions();

	public static final List<Player> playersOn = Collections.synchronizedList(new ArrayList<Player>());

	public VoragoMinion(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setNoDistanceCheck(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (getId() >= 17158 && getId() <= 17160) {
			if (getCombat().process()) {// TODO work out why this doesn't work
				this.getDefinitions().renderEmote = 2688;
			}
		}
	}

	@Override
	public void sendDeath(Entity source) {
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		if (!isDead())
			setHitpoints(0);
		final int deathDelay = defs.getDeathDelay() - (getId() == 50 ? 2 : 1);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= deathDelay) {
					if (getId() == 17185) {// Scopulus
						rago.scopDead++;
						/*
						 * for (int i = 0;i<VoragoHandler.getPlayersCount();i++) {//Causing errors killersDamage[i] = getDamageReceived(players[i]); }
						 */
						if (rago.scopDead == 2) {
							rago.canDie = true;
							rago.sendDeath(rago);
							rago.canBeAttacked = true;
							rago.killers[2] = getMostDamageReceivedSourcePlayer();
							/*
							 * int maxIndex = 0; for (int i = 1; i < killersDamage.length; i++){ int newnumber = killersDamage[i]; if ((newnumber > killersDamage[maxIndex])){ maxIndex = i; } } rago.killers[2] = players[maxIndex];
							 */

						} else {
							VoragoMinionCombat.EnrageMessage = false;
						}
					} else if (getId() == 17158 || getId() == 17159 || getId() == 17160) {// Stone clone
						rago.cloneOut = false;
					}
					if (source instanceof Player)
						((Player) source).getControlerManager().processNPCDeath(VoragoMinion.this);
					drop();
					reset();
					finish();
					if (source.getAttackedBy() == VoragoMinion.this) { // no need to wait after u kill
						source.setAttackedByDelay(0);
						source.setAttackedBy(null);
						source.setFindTargetDelay(0);
					}
					stop();
				}
				loop++;
			}
		}, 0, 1);

	}

}