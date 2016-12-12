package net.kagani.game.player.actions.divination;

public enum MemoryInfo {

	PALE(29384, 1, 3), FLICKERING(29385, 10, 4), BRIGHT(29386, 20, 5), GLOWING(
			29387, 30, 7), SPARKLING(29388, 40, 12), GLEAMING(29389, 50, 19), VIBRANT(
			29390, 60, 25), LUSTROUS(29391, 70, 32), BRILLIANT(29392, 80, 35), RADIANT(
			29393, 85, 38), LUMINOUS(29394, 90, 42), INCANDESCENT(29395, 95, 45);

	private int level;
	private int memoryId;
	private int xp;

	private MemoryInfo(int memoryId, int level, int xp) {
		this.memoryId = memoryId;
		this.level = level;
		this.xp = xp;
	}

	public int getLevel() {
		return level;
	}

	public int getMemoryId() {
		return memoryId;
	}

	public int getEnrichedMemoryId() {
		if (this == PALE)
			return 29384;
		return 29384 + (this.ordinal()) + 11;
	}
	
	public int getXp() {
		return xp;
	}

	public int getEnergyId() {
		return 29313 + this.ordinal();
	}
}
