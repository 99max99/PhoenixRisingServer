package net.kagani.login;

public class WorldInformation {

	/**
	 * Contains world id.
	 */
	private int id;
	/**
	 * Contains country flag ID.
	 */
	private int countryFlagID;
	/**
	 * Contains country name.
	 */
	private String countryName;
	/**
	 * World location.
	 */
	private int location;
	/**
	 * Contains world settings flags.
	 */
	private int flags;
	/**
	 * Contains world activity name.
	 */
	private String activity;
	/**
	 * Contains world ip.
	 */
	private String ip;
	/**
	 * Id of player files on this world. All worlds that have same file id will
	 * share same player file.
	 */
	private int playerFilesId;

	public WorldInformation(int id, int countryFlag, String countryName,
			int location, int flags, String activity, String ip,
			int playerFilesId) {
		this.id = id;
		this.countryFlagID = countryFlag;
		this.countryName = countryName;
		this.location = location;
		this.flags = flags;
		this.activity = activity;
		this.ip = ip;
		this.playerFilesId = playerFilesId;
	}

	public int getId() {
		return id;
	}

	public int getCountryFlagID() {
		return countryFlagID;
	}

	public String getCountryName() {
		return countryName;
	}

	public int getLocation() {
		return location;
	}

	public int getFlags() {
		return flags;
	}

	public String getActivity() {
		return activity;
	}

	public String getIp() {
		return ip;
	}

	public int getPlayerFilesId() {
		return playerFilesId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += id << 1;
		hash += countryFlagID << 5;
		hash += countryName.hashCode() << 9;
		hash += location << 14;
		hash += flags << 17;
		hash += activity.hashCode() << 20;
		hash += getIp().hashCode() << 26;
		return hash;
	}
}