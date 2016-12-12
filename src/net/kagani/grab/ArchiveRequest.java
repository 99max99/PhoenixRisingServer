package net.kagani.grab;

public class ArchiveRequest {

	private int index, archive, bytesSent;

	public ArchiveRequest(int index, int archive) {
		this.index = index;
		this.archive = archive;
	}

	@Override
	public int hashCode() {
		return index + (archive << 9);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ArchiveRequest))
			return false;
		ArchiveRequest o = (ArchiveRequest) other;
		return index == o.index && archive == o.archive;
	}

	public int getIndex() {
		return index;
	}

	public int getArchive() {
		return archive;
	}

	public void setBytesSent(int i) {
		bytesSent = i;
	}

	public int getBytesSent() {
		return bytesSent;
	}

}
