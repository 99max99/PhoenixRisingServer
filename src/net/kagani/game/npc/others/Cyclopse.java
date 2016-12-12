package net.kagani.game.npc.others;

import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.WarriorsGuild;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.godwars.bandos.GodwarsBandosFaction;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class Cyclopse extends GodwarsBandosFaction {

	public Cyclopse(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (source instanceof Player) {
			WarriorsGuild.killedCyclopses++;
			final NPC npc = this;
			final Player player = (Player) source;
			Controller controler = player.getControlerManager().getControler();
			if (controler == null || !(controler instanceof WarriorsGuild)
					|| Utils.random(15) != 0)
				return;
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					World.addGroundItem(
							new Item(WarriorsGuild.getBestDefender(player)),
							new WorldTile(getCoordFaceX(npc.getSize()),
									getCoordFaceY(npc.getSize()), getPlane()),
							player, true, 60);
				}
			}, getCombatDefinitions().getDeathDelay());
		}
	}
}