package net.kagani.game.npc.qbd;

import net.kagani.game.Animation;
import net.kagani.game.Hit;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

/**
 * Handles the Queen Black Dragon's melee attack.
 * 
 * @author Emperor
 * 
 */
public final class MeleeAttack implements QueenAttack {

	/**
	 * The default melee animation.
	 */
	private static final Animation DEFAULT = new Animation(16717);

	/**
	 * The east melee animation.
	 */
	private static final Animation EAST = new Animation(16744);

	/**
	 * The west melee animation.
	 */
	private static final Animation WEST = new Animation(16743);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		if (victim.getX() < npc.getBase().getX() + 31) {
			npc.setNextAnimation(WEST);
		} else if (victim.getX() > npc.getBase().getX() + 35) {
			npc.setNextAnimation(EAST);
		} else {
			npc.setNextAnimation(DEFAULT);
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				stop();
				int hit = Utils.random(900 + Utils.random(400), 2000);
				if (victim.getPrayer().isMeleeProtecting()) {
					victim.setNextAnimation(new Animation(Combat
							.getDefenceEmote(victim)));
					hit /= 2;
				} else {
					victim.setNextAnimation(new Animation(Combat
							.getDefenceEmote(victim)));
				}
				victim.applyHit(new Hit(npc, hit, hit == 0 ? HitLook.MISSED
						: HitLook.MELEE_DAMAGE));
			}
		});
		return Utils.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return victim.getY() > npc.getBase().getY() + 32;
	}

}