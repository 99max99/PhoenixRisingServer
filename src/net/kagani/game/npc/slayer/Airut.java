package net.kagani.game.npc.slayer;

import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;

public class Airut extends NPC {

	public Airut(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getSource() instanceof Player) {
			int damage = hit.getDamage();
			int newDamage = 0;
			int hp = getHitpoints();
			int maxHp = getMaxHitpoints();
			if (hp < (maxHp - (maxHp * 0.75))) {
				newDamage = (int) (damage - (damage * 0.70));
			} else if (hp < (maxHp - (maxHp * 0.50))) {
				newDamage = (int) (damage - (damage * 0.50));
			} else if (hp < (maxHp - (maxHp * 0.25))) {
				newDamage = (int) (damage - (damage * 0.20));
			} else {
				newDamage = damage;
			}
			hit.setDamage(newDamage);
		}
		super.handleIngoingHit(hit);
	}
}