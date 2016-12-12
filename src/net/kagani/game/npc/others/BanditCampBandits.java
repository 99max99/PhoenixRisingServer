package net.kagani.game.npc.others;

import java.util.ArrayList;

import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.godwars.saradomin.GodwarsSaradominFaction;
import net.kagani.game.npc.godwars.zammorak.GodwarsZammorakFaction;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class BanditCampBandits extends NPC {

	public BanditCampBandits(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceAgressive(true); // to ignore combat lvl
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> targets = super.getPossibleTargets();
		ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
		for (Entity t : targets) {
			if (!(t instanceof Player)
					|| (!GodwarsZammorakFaction.hasGodItem((Player) t) && !GodwarsSaradominFaction
							.hasGodItem((Player) t)))
				continue;
			targetsCleaned.add(t);
		}
		return targetsCleaned;
	}

	@Override
	public void setTarget(Entity entity) {
		if (entity instanceof Player
				&& (GodwarsZammorakFaction.hasGodItem((Player) entity) || GodwarsSaradominFaction
						.hasGodItem((Player) entity)))
			setNextForceTalk(new ForceTalk(
					GodwarsZammorakFaction.hasGodItem((Player) entity) ? "Time to die, Saradominist filth!"
							: "Prepare to suffer, Zamorakian scum!"));
		super.setTarget(entity);
	}

}
