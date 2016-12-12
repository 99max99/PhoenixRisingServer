package net.kagani.game.npc.combat;

import net.kagani.cache.loaders.AnimationDefinitions;

public class NPCCombatDefinitions {

	public static final int MELEE = 0;
	public static final int RANGE = 1;
	public static final int MAGE = 2;

	private int hitpoints;
	private int attackAnim;
	private int defenceAnim;
	private int deathAnim;
	private int respawnDelay;
	private int attackGfx;
	private int attackProjectile;
	private double xp;
	private boolean followClose;
	private boolean poisonImmune;
	private boolean poisonous;
	private boolean agressive;
	private int agroRatio;
	private int maxHit;

	public NPCCombatDefinitions(int hitpoints, int attackAnim, int defenceAnim,
			int deathAnim, int respawnDelay, int attackGfx,
			int attackProjectile, double xp, boolean followClose,
			boolean poisonImmune, boolean poisonous, boolean agressive,
			int agroRatio) {
		this.hitpoints = hitpoints;
		this.attackAnim = attackAnim;
		this.defenceAnim = defenceAnim;
		this.deathAnim = deathAnim;
		this.respawnDelay = respawnDelay;
		this.attackGfx = attackGfx;
		this.attackProjectile = attackProjectile;
		this.xp = xp;
		this.agressive = agressive;
		this.agroRatio = agroRatio;
		this.followClose = followClose;
		this.poisonous = poisonous;
		this.poisonImmune = poisonImmune;
	}

	public int getRespawnDelay() {
		return respawnDelay;
	}

	public int getDeathEmote() {
		return deathAnim;
	}

	public int getDefenceEmote() {
		return defenceAnim;
	}

	public int getAttackEmote() {
		return attackAnim;
	}

	public int getAttackGfx() {
		return attackGfx;
	}

	public boolean isAgressive() {
		return agressive;
	}

	public int getAttackProjectile() {
		return attackProjectile;
	}

	public void setHitpoints(int amount) {
		this.hitpoints = amount;
	}

	public int getHitpoints() { // or else they die too fast. keep this for now
		return hitpoints;
	}

	public int getDeathDelay() {
		return (AnimationDefinitions.getAnimationDefinitions(deathAnim)
				.getEmoteClientCycles() / 30) - 1;
	}

	public int getAgroRatio() {
		return agroRatio;
	}

	public double getXp() {
		return xp;
	}

	public boolean isFollowClose() {
		return followClose;
	}

	public boolean isPoisonImmune() {
		return poisonImmune;
	}

	public boolean isPoisonous() {
		return poisonous;
	}

	public static int getBestBonus(int melee, int range, int magic) {
		int bonus = melee;
		if (range > bonus)
			bonus = range;
		if (magic > bonus)
			bonus = magic;
		return bonus;
	}

	public void setFollowClose(boolean b) {
		this.followClose = b;
	}

	public int getMaxHit() {
		return maxHit;
	}
}