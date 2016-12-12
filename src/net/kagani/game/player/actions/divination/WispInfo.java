package net.kagani.game.player.actions.divination;

/**
 * 
 * @author Trenton
 * 
 */
public enum WispInfo {

	PALE(18150, 1), FLICKERING(18151, 10), BRIGHT(18153, 20), GLOWING(18155, 30), SPARKLING(
			18157, 40), GLEAMING(18159, 50), VIBRANT(18161, 60), LUSTROUS(
			18163, 70), BRILLIANT(18165, 80), RADIANT(18167, 85), LUMINOUS(
			18169, 90), INCANDESCENT(18171, 95);

	private int npcId;
	private int level;

	private WispInfo(int npcId, int level) {
		this.npcId = npcId;
		this.level = level;
	}

	public static WispInfo forNpcId(int id) {
		for (WispInfo info : WispInfo.values()) {
			if (info.getNpcId() == id)
				return info;
		}
		return null;
	}

	public int getHarvestXp() {
		return this.ordinal() + 1;
	}

	public int getNpcId() {
		return npcId;
	}

	public int getSpringNpcId() {
		return npcId + 23;
	}

	public int getEnrichedSpringNpcId() {
		return getEnrichedNpcId() + 23;
	}

	public int getEnrichedNpcId() {
		if (this == PALE)
			return 18150;
		return npcId + 1;
	}

	public int getLevel() {
		return level;
	}

	public int getEnergyId() {
		return 29313 + this.ordinal();
	}

	public int getMemoryId() {
		return 29384 + this.ordinal();
	}

	public int getEnrichedMemoryId() {
		if (this == PALE)
			return 29384;
		return 29384 + (this.ordinal()) + 11;
	}
}