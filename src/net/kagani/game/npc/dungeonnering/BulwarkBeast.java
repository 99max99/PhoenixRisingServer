package net.kagani.game.npc.dungeonnering;

import net.kagani.game.Entity;
import net.kagani.game.Hit;
import net.kagani.game.WorldTile;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.player.Player;
import net.kagani.game.player.actions.mining.MiningBase;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public final class BulwarkBeast extends DungeonBoss {

	private int shieldHP;
	private int maxShieldHP;

	public BulwarkBeast(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
		maxShieldHP = shieldHP = 500;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		handleHit(hit);
		super.handleIngoingHit(hit);
	}

	public void handleHit(Hit hit) {
		if (shieldHP <= 0 || hit.getLook() == HitLook.MAGIC_DAMAGE)
			return;
		hit.setDamage(0);
		Entity source = hit.getSource();
		if (source == null || !(source instanceof Player))
			return;
		if (hit.getLook() != HitLook.MELEE_DAMAGE)
			return;
		Player playerSource = (Player) source;
		int weaponId = playerSource.getEquipment().getWeaponId();
		if (weaponId != -1
				&& MiningBase.getPickAxeDefinitions(playerSource, true) != null/*
																				 * (
																				 * weaponId
																				 * >=
																				 * 16295
																				 * &&
																				 * weaponId
																				 * <=
																				 * 16315
																				 * )
																				 * ||
																				 * (
																				 * weaponId
																				 * >=
																				 * 16142
																				 * &&
																				 * weaponId
																				 * <=
																				 * 16152
																				 * )
																				 */) {
			hit.setDamage(Utils.random(50));
			hit.setLook(HitLook.ABSORB_DAMAGE);
			shieldHP -= hit.getDamage();
			playerSource
					.getPackets()
					.sendGameMessage(
							shieldHP > 0 ? "Your pickaxe chips away at the beast's armour plates."
									: "Your pixkaxe finnaly breaks throught the heavy armour plates.");
			refreshBar();
		}
	}

	public int getShieldHP() {
		return shieldHP;
	}

	public void setShieldHP(int shieldHP) {
		this.shieldHP = shieldHP;
	}

	public boolean hasShield() {
		return shieldHP > 0 && !isDead() && !hasFinished();
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		refreshBar();
	}

	public void refreshBar() {
		if (hasShield())
			getManager().showBar(getReference(), "Bulwark Beast's Armour",
					shieldHP * 100 / maxShieldHP);
		else
			getManager().hideBar(getReference());
	}

}
