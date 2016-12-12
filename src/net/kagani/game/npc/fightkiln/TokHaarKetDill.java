package net.kagani.game.npc.fightkiln;

import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.controllers.FightKiln;

@SuppressWarnings("serial")
public class TokHaarKetDill extends FightKilnNPC {

	private int receivedHits;

	public TokHaarKetDill(int id, WorldTile tile, FightKiln controler) {
		super(id, tile, controler);
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		handleHit(hit);
		super.handleIngoingHit(hit);
	}

	public void handleHit(Hit hit) {
		if (receivedHits != -1) {
			Entity source = hit.getSource();
			if (source == null || !(source instanceof Player))
				return;
			hit.setDamage(0);
			if (hit.getLook() != HitLook.MELEE_DAMAGE)
				return;
			Player playerSource = (Player) source;
			int weaponId = playerSource.getEquipment().getWeaponId();
			if (weaponId == 1275 || weaponId == 13661 || weaponId == 15259) {
				receivedHits++;
				if ((weaponId == 1275 && receivedHits >= 5)
						|| ((weaponId == 13661 || weaponId == 15259) && receivedHits >= 3)) {
					receivedHits = -1;
					setNextNPCTransformation(getId() + 1);
					playerSource
							.getPackets()
							.sendGameMessage(
									"Your pickaxe breaks the TokHaar-Ket-Dill's thick armour!");
				} else
					playerSource
							.getPackets()
							.sendGameMessage(
									"Your pickaxe slowy  cracks its way through the TokHaar-Ket-Dill's armour.");
			}
		}
	}

}
