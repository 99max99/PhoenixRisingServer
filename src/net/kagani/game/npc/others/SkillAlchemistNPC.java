package net.kagani.game.npc.others;

import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;

@SuppressWarnings("serial")
public class SkillAlchemistNPC extends NPC {

	public SkillAlchemistNPC(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		setName("Skill Alchemist");
	}
}
