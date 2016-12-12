package net.kagani.game.npc.fightcaves;

import java.util.ArrayList;

import net.kagani.game.Entity;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class Yt_HurKot extends FightCavesNPC {

	private TzTok_Jad tzTok_Jad;
	private int nexHealTick;

	public Yt_HurKot(TzTok_Jad tzTok_Jad, int id, WorldTile tile) {
		super(id, tile);
		this.tzTok_Jad = tzTok_Jad;
		setRun(true);
		setForceAgressive(false);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (tzTok_Jad == null || tzTok_Jad.isDead()) {
			finish();
			return;
		}
		if (!isUnderCombat()) {
			calcFollow(tzTok_Jad, true);
			if (tzTok_Jad.getHitpoints() == tzTok_Jad.getMaxHitpoints()
					|| !Utils.isOnRange(this, tzTok_Jad, 6)) {
				return;
			}
			nexHealTick++;
			if (nexHealTick % 2 == 0)
				tzTok_Jad.setNextGraphics(new Graphics(2992, 0, 300));
			if (nexHealTick % 5 == 0) {// Approx 3 seconds
				setNextFaceEntity(tzTok_Jad);
				tzTok_Jad.heal((int) (tzTok_Jad.getMaxHitpoints() * .03));
			}
		}
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		return super.getPossibleTargets(false, true);
	}
}
