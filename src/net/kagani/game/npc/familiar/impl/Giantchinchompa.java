package net.kagani.game.npc.familiar.impl;

import java.util.List;

import net.kagani.game.Animation;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.Summoning.Pouch;
import net.kagani.utils.Utils;

public class Giantchinchompa extends Familiar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7708802901929527088L;

	public Giantchinchompa(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Explode";
	}

	@Override
	public String getSpecialDescription() {
		return "Explodes, damaging nearby enemies.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 3;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		setNextAnimation(new Animation(7750));
		setNextGraphics(new Graphics(1310));
		setNextForceTalk(new ForceTalk("Squeek!"));
		Player player = getOwner();
		List<Integer> playerIndexes = World.getRegion(player.getRegionId())
				.getPlayerIndexes();
		if (playerIndexes != null) {
			for (int playerIndex : playerIndexes) {
				Player p2 = World.getPlayers().get(playerIndex);
				if (p2 == null || p2.isDead() || p2 != player
						|| !p2.isRunning() || !p2.withinDistance(player, 2))
					continue;
				p2.applyHit(new Hit(this, Utils.random(130),
						HitLook.MAGIC_DAMAGE));
			}
			return true;
		}
		return false;
	}
}
