package net.kagani.game.player.actions.invention;

public class Invention {

	/**
	 * @author: Dylan Page
	 */

	private enum items {

		FIRST(1),

		SECOND(2),

		;

		private int itemId;

		items(int itemId) {
			this.itemId = itemId;
		}

		public int getItemId() {
			return itemId;
		}
	}
}