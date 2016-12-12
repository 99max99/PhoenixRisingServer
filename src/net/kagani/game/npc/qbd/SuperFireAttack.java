package net.kagani.game.npc.qbd;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

/**
 * Handles the super dragonfire attack.
 * 
 * @author Emperor
 * 
 */
public final class SuperFireAttack implements QueenAttack {

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16745);

	/**
	 * The graphics.
	 */
	private static final Graphics GRAPHIC = new Graphics(3152);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		npc.setNextGraphics(GRAPHIC);
		victim.getPackets()
				.sendGameMessage(
						"<col=FFCC00>The Queen Black Dragon gathers her strength to breath extremely hot flames.</col>");
		WorldTasksManager.schedule(new WorldTask() {
			int count = 0;

			@Override
			public void run() {
				int hit = 1950;
				int distance = Utils.getDistance(
						npc.getBase().transform(33, 31, 0), victim);
				hit /= (distance / 3) + 1;
				victim.setNextAnimation(new Animation(Combat
						.getDefenceEmote(victim)));
				victim.applyHit(new Hit(npc, hit, HitLook.REGULAR_DAMAGE));
				if (++count == 3) {
					stop();
				}
			}
		}, 4, 1);
		return Utils.random(8, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}

}