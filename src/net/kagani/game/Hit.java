package net.kagani.game;

import net.kagani.game.player.Player;

public final class Hit {

	public static enum HitLook {

		MISSED(141), REGULAR_DAMAGE(144), MELEE_DAMAGE(132), RANGE_DAMAGE(135), MAGIC_DAMAGE(
				138), REFLECTED_DAMAGE(146), ABSORB_DAMAGE(148), POISON_DAMAGE(
				142), DESEASE_DAMAGE(142), // rs
		// removed
		// desease
		HEALED_DAMAGE(143), CANNON_DAMAGE(145);
		private int mark;

		private HitLook(int mark) {
			this.mark = mark;
		}

		public int getMark(boolean legacy) {
			return mark;
		}

		public int getMark() {
			return mark;
		}

		public void setMark(int mark) {
			this.mark = mark;
		}
	}

	private Entity source;
	private HitLook look;
	private int damage;
	private boolean critical;
	private boolean ability;
	private Hit soaking;
	private int delay;

	public void setCriticalMark() {
		critical = true;
		setAbilityMark();
	}

	public void setAbilityMark() {
		ability = true;
	}

	public void setHealHit() {
		look = HitLook.HEALED_DAMAGE;
		critical = false;
		ability = false;
	}

	public Hit(Entity source, int damage, HitLook look) {
		this(source, damage, look, 0);
	}

	public Hit(Entity source, int damage, HitLook look, int delay) {
		this.source = source;
		this.damage = damage;
		this.look = look;
		this.delay = delay;
	}

	public void setLook(HitLook look) {
		this.look = look;
	}

	public boolean missed() {
		return damage == 0;
	}

	public boolean interactingWith(Player player, Entity victm) {
		return player == victm || player == source;
	}

	public int getMark(Player player, Entity victm) {
		if (HitLook.HEALED_DAMAGE == look)
			return look.getMark(player.isLegacyMode());
		if (damage == 0) {
			return HitLook.MISSED.getMark(player.isLegacyMode());
		}
		int mark = look.getMark(/* player.isLegacyMode() */false);
		if (look == HitLook.MELEE_DAMAGE || look == HitLook.RANGE_DAMAGE
				|| look == HitLook.MAGIC_DAMAGE) {
			if (critical)
				mark++;
			if (ability)
				mark++;
		}

		if (!interactingWith(player, victm))
			mark += /* player.isLegacyMode() ? (critical ? 3 : 10) : */17;
		return mark;
	}

	public HitLook getLook() {
		return look;
	}

	public int getDamage() {
		return damage;
	}

	public int getDamageDisplay(Player player) {
		int dmg = damage;
		if (dmg != 0 && dmg < 10 && player.isLegacyMode())
			dmg = 10;
		return dmg;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public Entity getSource() {
		return source;
	}

	public void setSource(Entity source) {
		this.source = source;
	}

	public boolean isCriticalHit() {
		return critical;
	}

	public Hit getSoaking() {
		return soaking;
	}

	public void setSoaking(Hit soaking) {
		this.soaking = soaking;
	}

	public int getDelay() {
		return delay;
	}

	public int getMSDelay() {
		return delay * 10;
	}

	public boolean isAbility() {
		return ability;
	}
}