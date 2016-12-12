package net.kagani.game.npc.familiar.impl;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.stealingcreation.Score;
import net.kagani.game.minigames.stealingcreation.StealingCreationController;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.Summoning.Pouch;

public class Clayfamiliarclass4 extends Familiar {

	private static final long serialVersionUID = 7289857564889907408L;

	public Clayfamiliarclass4(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Clay Deposit";
	}

	@Override
	public String getSpecialDescription() {
		return "Deposit all items in the beast of burden's possession in exchange for points.";
	}

	@Override
	public int getBOBSize() {
		return 18;
	}

	@Override
	public int getSpecialAmount() {
		return 30;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		if (getOwner().getControlerManager().getControler() == null
				|| !(getOwner().getControlerManager().getControler() instanceof StealingCreationController)) {
			dissmissFamiliar(false);
			return false;
		}
		getOwner().setNextGraphics(new Graphics(1316));
		getOwner().setNextAnimation(new Animation(7660));
		StealingCreationController sc = (StealingCreationController) getOwner()
				.getControlerManager().getControler();
		Score score = sc.getGame().getScore(getOwner());
		if (score == null)
			return false;
		for (Item item : getBob().getBeastItems().getItems()) {
			if (item == null)
				continue;
			sc.getGame().sendItemToBase(getOwner(), item, sc.getTeam(), true,
					false);
		}
		return true;
	}
}
