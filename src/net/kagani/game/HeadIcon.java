package net.kagani.game;

public class HeadIcon {

	private int spriteId, fileId;

	public HeadIcon(int spriteId, int fileId) {
		this.spriteId = spriteId;
		this.fileId = fileId;
	}

	public int getSpriteId() {
		return spriteId;
	}

	public int getFileId() {
		return fileId;
	}
}