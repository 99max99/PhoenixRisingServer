package net.kagani.game;

import java.io.Serializable;

import net.kagani.cache.loaders.AnimationDefinitions;

public final class Animation implements Serializable {

	/**
         * 
         */
	private static final long serialVersionUID = 1L;

	private int[] ids;
	private int speed;

	public Animation(int id) {
		this(id, 0);
	}

	public Animation(int id, int speed) {
		this(id, id, id, id, speed);
	}

	public Animation(int id1, int id2, int id3, int id4, int speed) {
		this.ids = new int[] { id1, id2, id3, id4 };
		for (int i = 0; i < ids.length; i++)
			if (ids[i] == 65535) // quick fix to anim now using bigsmart instead
									// of short. jsut to be safe, remove this
									// once 100% sure
				ids[i] = -1;
		this.speed = speed;
	}

	public int[] getIds() {
		return ids;
	}

	public int getDelay() {
		return speed;
	}

	public AnimationDefinitions getDefinitions() {
		return AnimationDefinitions.getAnimationDefinitions(ids[0]);
	}

	public void setDelay(int delay) {
		this.speed = delay;
	}
}
