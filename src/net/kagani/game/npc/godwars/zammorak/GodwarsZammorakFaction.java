package net.kagani.game.npc.godwars.zammorak;

import java.util.ArrayList;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.combat.NPCCombatDefinitions;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.npc.godwars.zaros.ZarosMinion;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.player.controllers.GodWars;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class GodwarsZammorakFaction extends NPC {

	public GodwarsZammorakFaction(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(6000);// Lurable boss
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		if (!withinDistance(new WorldTile(2881, 5306, 0), 200))
			return super.getPossibleTargets();
		else {
			ArrayList<Entity> targets = getPossibleTargets(true, true);
			ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
			for (Entity t : targets) {
				if (t instanceof GodwarsZammorakFaction
						|| (t instanceof Player && hasGodItem((Player) t))
						|| t instanceof Familiar)
					continue;
				targetsCleaned.add(t);
			}
			return targetsCleaned;
		}
	}

	public static boolean hasGodItem(Player player) {
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item == null || item.getId() == -1)
				continue; // shouldn't happen
			String name = item.getDefinitions().getName().toLowerCase();
			if (name.contains("zamorak") || name.contains("unholy")
					|| ZarosMinion.isNexArmour(name))
				return true;
		}
		return false;
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
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					if (source instanceof Player) {
						Player player = (Player) source;
						Controller controler = player.getControlerManager()
								.getControler();
						if (controler != null && controler instanceof GodWars) {
							GodWars godControler = (GodWars) controler;
							godControler.incrementKillCount(3);
						}
					}
					giveXP();
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					if (!isSpawned())
						setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
}
