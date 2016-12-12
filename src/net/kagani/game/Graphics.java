package net.kagani.game;

import java.io.Serializable;

//serializable now in order to allow to be saved for abilities
public final class Graphics implements Serializable {

	private static final long serialVersionUID = 90129437862239571L;

	private int id, height, speed, rotation;
	private boolean forceRefresh;

	public Graphics(int id) {
		this(id, 0, 0);

	}

	public Graphics(int id, int speed, int height) {
		this(id, speed, height, 0, false);
	}

	public Graphics(int id, int speed, int height, int rotation,
			boolean forceRefresh) {
		this.id = id;
		this.speed = speed;
		this.height = height;
		this.rotation = rotation;
		this.forceRefresh = forceRefresh;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + id;
		result = prime * result + rotation;
		result = prime * result + speed;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Graphics other = (Graphics) obj;
		if (height != other.height)
			return false;
		if (id != other.id)
			return false;
		if (rotation != other.rotation)
			return false;
		if (speed != other.speed)
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public int getSettingsHash() {
		return (speed & 0xffff) | (height << 16);
	}

	public int getSettings2Hash() {
		int hash = 0;
		hash |= rotation & 0x7;
		// hash |= value << 3;
		hash |= (forceRefresh ? 1 : 0) << 7;
		return hash;
	}

	public int getSpeed() {
		return speed;
	}

	public int getHeight() {
		return height;
	}

	public boolean isForceRefresh() {
		return forceRefresh;
	}
}
