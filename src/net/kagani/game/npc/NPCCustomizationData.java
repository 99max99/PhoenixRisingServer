package net.kagani.game.npc;

import net.kagani.cache.loaders.NPCDefinitions;

public class NPCCustomizationData {

	private short[] replaceTextures;
	private short[] replaceColors;
	private int[] replaceModels;
	private boolean reset;

	public NPCCustomizationData(NPCDefinitions definitions) {
		replaceTextures = definitions.aShortArray859.clone();
		replaceColors = definitions.aShortArray896.clone();

	}

	public NPCCustomizationData(short[] replaceTextures, short[] replaceColors) {
		this.replaceTextures = replaceTextures;
		this.replaceColors = replaceColors;
	}

	public short[] getReplaceTextures() {
		return replaceTextures;
	}

	public short[] getReplaceColors() {
		return replaceColors;
	}

	public NPCCustomizationData setColor(int index, int color) {
		replaceColors[index] = (short) color;
		return this;
	}

	public int[] getReplaceModels() {
		return replaceModels;
	}

	public void requestReset() {
		reset = true;
	}

	public boolean requestedReset() {
		return reset;
	}

	public void setTexture(int index, int texture) {
		replaceTextures[index] = (short) texture;
	}
}