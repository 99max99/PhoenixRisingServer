package net.kagani.game.npc.fightkiln;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class HarAkenTentacle extends NPC {

	private HarAken aken;

	public HarAkenTentacle(int id, WorldTile tile, HarAken aken) {
		super(id, tile, -1, true, true);
		setCantFollowUnderCombat(true);
		setNextAnimation(new Animation(id == 15209 ? 16238 : 16241));
		this.aken = aken;
	}

	@Override
	public void sendDeath(Entity source) {
		aken.removeTentacle(this);
		super.sendDeath(source);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>(1);
		List<Integer> playerIndexes = World.getRegion(getRegionId())
				.getPlayerIndexes();
		if (playerIndexes != null) {
			for (int npcIndex : playerIndexes) {
				Player player = World.getPlayers().get(npcIndex);
				if (player == null || player.isDead() || player.hasFinished()
						|| !player.isRunning()
						|| player.getAppearence().isHidden())
					continue;
				possibleTarget.add(player);
			}
		}
		return possibleTarget;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.1;
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.1;
	}
}
