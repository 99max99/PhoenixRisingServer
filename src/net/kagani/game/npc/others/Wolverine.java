package net.kagani.game.npc.others;

import java.util.Random;

import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

@SuppressWarnings("serial")
public class Wolverine extends NPC {

	public Wolverine(Player target, int id, WorldTile tile,
			int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		setCombatLevel(target.getSkills().getCombatLevel()
				+ new Random().nextInt(100) + 100);
		int hitpoints = 10000 + this.getCombatLevel()
				+ target.getSkills().getCombatLevel() / 2
				+ new Random().nextInt(10);
		super.getCombatDefinitions().setHitpoints(hitpoints);
		setHitpoints(hitpoints);
		setRandomWalk(NORMAL_WALK);
		setForceAgressive(true);
		setAttackedBy(target);
		faceEntity(target);
	}
}