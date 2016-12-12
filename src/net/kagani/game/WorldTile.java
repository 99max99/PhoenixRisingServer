package net.kagani.game;

import java.io.Serializable;

import net.kagani.game.map.MapUtils;
import net.kagani.game.map.MapUtils.Structure;
import net.kagani.utils.Utils;

public class WorldTile implements Serializable {

	private static final long serialVersionUID = -6567346497259686765L;

	protected short x;

	protected short y;
	protected byte plane;

	public WorldTile(int x, int y, int plane) {
		this.x = (short) x;
		this.y = (short) y;
		this.plane = (byte) plane;
	}

	public WorldTile(int x, int y, int plane, int size) {
		this.x = (short) getCoordFaceX(x, size, size, -1);
		this.y = (short) getCoordFaceY(y, size, size, -1);
		this.plane = (byte) plane;
	}

	public WorldTile(WorldTile tile) {
		this.x = tile.x;
		this.y = tile.y;
		this.plane = tile.plane;
	}

	public WorldTile(WorldTile tile, int randomize) {
		this.x = (short) (tile.x + Utils.random(randomize * 2 + 1) - randomize);
		this.y = (short) (tile.y + Utils.random(randomize * 2 + 1) - randomize);
		this.plane = tile.plane;
	}

	public WorldTile(int hash) {
		this.x = (short) (hash >> 14 & 0x3fff);
		this.y = (short) (hash & 0x3fff);
		this.plane = (byte) (hash >> 28);
	}

	public void moveLocation(int xOffset, int yOffset, int planeOffset) {
		x += xOffset;
		y += yOffset;
		plane += planeOffset;
	}

	public final void setLocation(WorldTile tile) {
		setLocation(tile.x, tile.y, tile.plane);
	}

	public final void setLocation(int x, int y, int plane) {
		this.x = (short) x;
		this.y = (short) y;
		this.plane = (byte) plane;
	}

	public int getX() {
		return x;
	}

	public int getXInRegion() {
		return x & 0x3F;
	}

	public int getYInRegion() {
		return y & 0x3F;
	}

	public int getXInChunk() {
		return x & 0x7;
	}

	public int getYInChunk() {
		return y & 0x7;
	}

	public int getY() {
		return y;
	}

	public int getPlane() {
		if (plane > 3)
			return 3;
		return plane;
	}

	public int getChunkX() {
		return (x >> 3);
	}

	public int getChunkY() {
		return (y >> 3);
	}

	public int getRegionX() {
		return (x >> 6);
	}

	public int getRegionY() {
		return (y >> 6);
	}

	public int getRegionId() {
		return ((getRegionX() << 8) + getRegionY());
	}

	public int getChunkXInScene(Entity entity) {
		return getChunkX()
				- MapUtils
						.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[0];
	}

	public int getChunkYInScene(Entity entity) {
		return getChunkY()
				- MapUtils
						.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[1];
	}

	public int getXInScene(Entity entity) {
		return getX()
				- MapUtils
						.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[0]
				* 8;
	}

	public int getYInScene(Entity entity) {
		return getY()
				- MapUtils
						.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[1]
				* 8;
	}

	public int getZInScene(Entity entity) {
		return getPlane()
				- MapUtils
						.decode(Structure.CHUNK, entity.getSceneBaseChunkId())[2]
				* 8;
	}

	public int getRegionHash() {
		return getRegionY() + (getRegionX() << 8) + (plane << 16);
	}

	public int getTileHash() {
		return y + (x << 14) + (plane << 28);
	}

	public boolean withinDistance(WorldTile tile, int distance) {
		if (tile.plane != plane)
			return false;
		int deltaX = tile.x - x, deltaY = tile.y - y;
		return deltaX <= distance && deltaX >= -distance && deltaY <= distance
				&& deltaY >= -distance;
	}

	public boolean withinDistance(WorldTile tile) {
		return withinDistance(tile, 14);
	}

	public int getCoordFaceX(int sizeX) {
		return getCoordFaceX(sizeX, -1, -1);
	}

	public static final int getCoordFaceX(int x, int sizeX, int sizeY,
			int rotation) {
		return x + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
	}

	public static final int getCoordFaceY(int y, int sizeX, int sizeY,
			int rotation) {
		return y + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
	}

	public int getCoordFaceX(int sizeX, int sizeY, int rotation) {
		return x + ((rotation == 1 || rotation == 3 ? sizeY : sizeX) - 1) / 2;
	}

	public int getCoordFaceY(int sizeY) {
		return getCoordFaceY(-1, sizeY, -1);
	}

	public int getCoordFaceY(int sizeX, int sizeY, int rotation) {
		return y + ((rotation == 1 || rotation == 3 ? sizeX : sizeY) - 1) / 2;
	}

	public WorldTile transform(int x, int y, int plane) {
		return new WorldTile(this.x + x, this.y + y, this.plane + plane);
	}

	/**
	 * Checks if this world tile's coordinates match the other world tile.
	 * 
	 * @param other
	 *            The world tile to compare with.
	 * @return {@code True} if so.
	 */
	public boolean matches(WorldTile other) {
		return x == other.x && y == other.y && plane == other.plane;
	}

	public boolean withinArea(int a, int b, int c, int d) {
		return getX() >= a && getY() >= b && getX() <= c && getY() <= d;
	}

	@Override
	public String toString() {
		return "[ X: " + x + ", Y: " + y + ", Z: " + plane + " ]";
	}
}