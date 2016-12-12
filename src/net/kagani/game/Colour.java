package net.kagani.game;

public class Colour {

	private int colours;
	private int delay;
	private int duration;

	public Colour(int delay, int duration, int c1, int c2, int c3, int c4) {
		this.delay = delay;
		this.duration = duration;
		this.colours = c1 | c2 << 8 | c3 << 16 | c4 << 24;
	}

	public int getColours() {
		return colours;
	}

	public int getDelay() {
		return delay;
	}

	public int getDuration() {
		return duration;
	}

	public static int getSeconds(int seconds) {
		return seconds * 50;
	}

}
