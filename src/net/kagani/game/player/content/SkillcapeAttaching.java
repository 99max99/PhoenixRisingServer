package net.kagani.game.player.content;

import net.kagani.game.player.Player;

public class SkillcapeAttaching {

	/**
	 * @author: Dylan Page
	 */

	enum Configs {

		ATTACK(1, 1),

		DEFENCE(1, 1),

		STRENGTH(1, 1),

		HITPOINTS(1, 1),

		RANGED(1, 1),

		PRAYER(1, 1),

		MAGIC(1, 1)

		;

		int used;
		int with;

		Configs(int used, int with) {
			this.used = used;
			this.with = with;
		}

		public int getUsed() {
			return used;
		}

		public int getWith() {
			return with;
		}
	}

	private void attachCape(Player player) {

	}
}