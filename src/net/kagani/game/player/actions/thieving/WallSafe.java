package net.kagani.game.player.actions.thieving;

import net.kagani.game.Animation;
import net.kagani.game.Hit;
import net.kagani.game.WorldObject;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.utils.Utils;

public class WallSafe extends Action {

	public boolean checked;
	public int EMERALD = 1621;
	public int RUBY = 1619;
	public int SAPPHIRE = 1623;
	public WorldObject safe;

	public WallSafe(WorldObject safe) {
		this.safe = safe;
	}

	public Item getLoot(boolean b) {
		Item item = null;
		final int r = Utils.random(100);
		if (!b)
			if (r >= 0 && r <= 38)
				item = new Item(995, Utils.random(20, 40));
			else if (r >= 39 && r <= 50)
				item = new Item(SAPPHIRE, 1);
			else if (r >= 51 && r <= 58)
				item = new Item(EMERALD, 1);
			else if (r >= 59 && r <= 63)
				item = new Item(RUBY, 1);
			else if (r >= 0 && r <= 66)
				item = new Item(995, Utils.random(20, 40));
			else if (r >= 67 && r <= 75)
				item = new Item(SAPPHIRE, 1);
			else if (r >= 76 && r <= 81)
				item = new Item(EMERALD, 1);
			else if (r >= 82 && r <= 85)
				item = new Item(RUBY, 1);
		return item;
	}

	@Override
	public boolean process(Player player) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		if (!checked) {
			player.getPackets().sendGameMessage(
					"You attempt to pick the lock...");
			player.setNextAnimation(new Animation(2247));
			checked = true;
			return 2;
		} else {
			final Item loot = getLoot(player.getInventory().containsItem(5560,
					1));
			if (loot == null) {
				player.getPackets().sendGameMessage(
						"You fail and trigger a trap!");
				player.setNextAnimation(new Animation(3170));
				if (player.getSkills().getLevel(Skills.HITPOINTS) <= 30)
					player.applyHit(new Hit(player, Utils.random(40),
							Hit.HitLook.REGULAR_DAMAGE));
				else
					player.applyHit(new Hit(player, Utils.random(100),
							Hit.HitLook.REGULAR_DAMAGE));
			} else {
				int amount = loot.getAmount();
				if (loot.getId() == 995)
					amount = loot.getAmount() * (80 + Utils.random(80));
				player.getInventory().addItemMoneyPouch(
						new Item(loot.getId(), amount));
				player.getSkills().addXp(Skills.THIEVING, 70);
				player.getPackets().sendGameMessage(
						"You successfully crack the safe!");

			}
		}
		return -1;
	}

	@Override
	public boolean start(Player player) {
		if (player.getSkills().getLevel(Skills.THIEVING) < 50) {
			player.getPackets()
					.sendGameMessage(
							"You must have a thieving level of atleast 50 in order to crack a safe.");
			return false;
		}
		checked = false;
		return true;
	}

	@Override
	public void stop(Player player) {
		this.setActionDelay(player, 2);
	}
}