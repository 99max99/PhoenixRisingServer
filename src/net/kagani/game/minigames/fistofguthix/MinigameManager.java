package net.kagani.game.minigames.fistofguthix;


public class MinigameManager {

	private FistOfGuthix fistOfGuthix;
	private static MinigameManager INSTANCE = new MinigameManager();

	public FistOfGuthix fistOfGuthix() {
		if (fistOfGuthix == null)
			fistOfGuthix(new FistOfGuthix(60));
		return fistOfGuthix;
	}

	public void fistOfGuthix(FistOfGuthix fog) {
		this.fistOfGuthix = fog;
	}

	public static MinigameManager INSTANCE() {
		return INSTANCE;
	}
}