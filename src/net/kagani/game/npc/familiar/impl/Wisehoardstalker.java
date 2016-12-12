package net.kagani.game.npc.familiar.impl;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Summoning.Pouch;
import net.kagani.game.player.content.dungeoneering.DungeonConstants;
import net.kagani.utils.Utils;

public class Wisehoardstalker extends Familiar {

	private static final long serialVersionUID = -7037718748109234870L;
	private int forageTicks;

	public Wisehoardstalker(Player owner, Pouch pouch, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		forageTicks++;
		if (forageTicks == 300) {
			forageTicks = 0;
			getBob().getBeastItems().add(
					new Item(DungeonConstants.HOARDSTALKER_ITEMS[7][Utils
							.random(5)], 1));
		}
	}

	@Override
	public String getSpecialName() {
		return "Aptitude";
	}

	@Override
	public String getSpecialDescription() {
		return "Boosts all of your non-combat skills by 8.";
	}

	@Override
	public int getBOBSize() {
		return 30;
	}

	@Override
	public int getSpecialAmount() {
		return 20;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		player.setNextGraphics(new Graphics(1300));
		player.setNextAnimation(new Animation(7660));
		for (int skill = 0; skill < Skills.SKILL_NAME.length; skill++) {
			if (skill == Skills.SUMMONING || skill == Skills.ATTACK
					|| skill == Skills.DEFENCE || skill == Skills.STRENGTH
					|| skill == Skills.RANGE || skill == Skills.MAGIC
					|| skill == Skills.PRAYER || skill == Skills.DUNGEONEERING)
				continue;
			player.getSkills().set(skill,
					player.getSkills().getLevelForXp(skill) + 8);
		}
		return true;
	}
}
