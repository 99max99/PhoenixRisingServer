package net.kagani.game.npc.others;

import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Combat;

@SuppressWarnings("serial")
public class Kurask extends NPC {

	public Kurask(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getSource() instanceof Player) {
			Player player = (Player) hit.getSource();
			if (!(player.getEquipment().getWeaponId() == 13290 || player
					.getEquipment().getWeaponId() == 4158)
					&& !((player.getCombatDefinitions().getStyle(true) == Combat.RANGE_TYPE || player
							.getCombatDefinitions().getStyle(false) == Combat.RANGE_TYPE) && (player
							.getEquipment().getAmmoId() == 13280 || player
							.getEquipment().getAmmoId() == 4160)))
				hit.setDamage(0);
		}
		super.handleIngoingHit(hit);
	}
}
