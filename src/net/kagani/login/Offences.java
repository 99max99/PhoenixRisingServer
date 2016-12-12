package net.kagani.login;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.kagani.utils.Utils;

public class Offences implements Serializable {

	/**
	 * Our serial UID.
	 */
	private static final long serialVersionUID = 8210363743646992498L;

	/**
	 * All ip bans
	 */
	private List<Metadata> ipBans;
	/**
	 * All ip mutes
	 */
	private List<Metadata> ipMutes;
	/**
	 * All bans
	 */
	private List<Metadata> bans;
	/**
	 * All mutes
	 */
	private List<Metadata> mutes;

	public Offences() {
		ipBans = new ArrayList<Metadata>();
		ipMutes = new ArrayList<Metadata>();
		bans = new ArrayList<Metadata>();
		mutes = new ArrayList<Metadata>();
	}

	/**
	 * Whether this ip is banned.
	 */
	public synchronized boolean isIpBanned(String ip) {
		for (Metadata m : ipBans)
			if (m.getIp().equals(ip) && !m.hasExpired())
				return true;
		return false;
	}

	public synchronized boolean isImprisoned(String ip) {
		for (Metadata m : ipBans)
			if (m.getIp().equals(ip) && !m.hasExpired())
				return true;
		return false;
	}

	/**
	 * Whether this ip is muted.
	 */
	public synchronized boolean isIpMuted(String ip) {
		for (Metadata m : ipMutes)
			if (m.getIp().equals(ip) && !m.hasExpired())
				return true;
		return false;
	}

	/**
	 * Whether this user is banned.
	 */
	public synchronized boolean isBanned(String username) {
		for (Metadata m : bans)
			if (m.getUsername().equals(username) && !m.hasExpired())
				return true;
		return false;
	}

	/**
	 * Whether this user is banned.
	 */
	public synchronized boolean isMuted(String username) {
		for (Metadata m : mutes)
			if (m.getUsername().equals(username) && !m.hasExpired())
				return true;
		return false;
	}

	/**
	 * Find's all offences made by given username's or ip's.
	 */
	public synchronized Map<Integer, List<Metadata>> findAllOffences(
			List<String> usernames, List<String> ips) {
		Map<Integer, List<Metadata>> offences = new HashMap<Integer, List<Metadata>>();
		for (int i = 0; i < 5; i++)
			offences.put(i, new ArrayList<Metadata>());
		for (Metadata m : ipBans)
			if (usernames.contains(m.getUsername()) || ips.contains(m.getIp()))
				offences.get(0).add(m);
		for (Metadata m : ipMutes)
			if (usernames.contains(m.getUsername()) || ips.contains(m.getIp()))
				offences.get(1).add(m);
		for (Metadata m : bans)
			if (usernames.contains(m.getUsername()) || ips.contains(m.getIp()))
				offences.get(2).add(m);
		for (Metadata m : mutes)
			if (usernames.contains(m.getUsername()) || ips.contains(m.getIp()))
				offences.get(3).add(m);
		return offences;
	}

	/**
	 * Add's ip ban with given details.
	 */
	public synchronized void ipBan(String username, String ip,
			String moderator, String reason, long expires) {
		ipBans.add(new Metadata(username, ip, moderator, reason, expires));
	}

	/**
	 * Add's ip mute with given details.
	 */
	public synchronized void ipMute(String username, String ip,
			String moderator, String reason, long expires) {
		ipMutes.add(new Metadata(username, ip, moderator, reason, expires));
	}

	/**
	 * Add's ban with given details.
	 */
	public synchronized void ban(String username, String ip, String moderator,
			String reason, long expires) {
		bans.add(new Metadata(username, ip, moderator, reason, expires));
	}

	/**
	 * Add's mute with given details.
	 */
	public synchronized void mute(String username, String ip, String moderator,
			String reason, long expires) {
		mutes.add(new Metadata(username, ip, moderator, reason, expires));
	}

	/**
	 * Remove's all bans for specific user.
	 */
	public synchronized int unbanByUser(String username) {
		int count = 0;
		Iterator<Metadata> it$ = ipBans.iterator();
		while (it$.hasNext()) {
			Metadata m = it$.next();
			if (m.getUsername().equals(username)) {
				it$.remove();
				count++;
			}
		}

		it$ = bans.iterator();
		while (it$.hasNext()) {
			Metadata m = it$.next();
			if (m.getUsername().equals(username)) {
				it$.remove();
				count++;
			}
		}

		return count;
	}

	/**
	 * Remove's all mutes for specific user.
	 */
	public synchronized int unmuteByUser(String username) {
		int count = 0;
		Iterator<Metadata> it$ = ipMutes.iterator();
		while (it$.hasNext()) {
			Metadata m = it$.next();
			if (m.getUsername().equals(username)) {
				it$.remove();
				count++;
			}
		}

		it$ = mutes.iterator();
		while (it$.hasNext()) {
			Metadata m = it$.next();
			if (m.getUsername().equals(username)) {
				it$.remove();
				count++;
			}
		}

		return count;
	}

	/**
	 * Clean's up by removing expired offences.
	 */
	public synchronized void cleanup() {
		cleanup(ipBans);
		cleanup(ipMutes);
		cleanup(bans);
		cleanup(mutes);
	}

	/**
	 * Clean's up specific offences list.
	 */
	private void cleanup(List<Metadata> list) {
		Iterator<Metadata> it$ = list.iterator();
		while (it$.hasNext()) {
			Metadata metadata = it$.next();
			if (metadata.hasExpired())
				it$.remove();
		}

	}

	/**
	 * Metadata for offence. None of the fields inside are guaranteed to be
	 * filled.
	 */
	public static class Metadata implements Serializable {
		/**
		 * Our serial version UID.
		 */
		private static final long serialVersionUID = -5082795333477460692L;

		/**
		 * Target username who was taken action against. (Even if it was ipban
		 * or ipmute)
		 */
		private String username;
		/**
		 * Time when this offence was added.
		 */
		private long time;
		/**
		 * Ip of the offender.
		 */
		private String ip;
		/**
		 * Username of moderator who did action.
		 */
		private String moderator;
		/**
		 * Moderator reasonining.
		 */
		private String reason;
		/**
		 * Time when this offence expires.
		 */
		private long expires;

		public Metadata(String username, String ip, String moderator,
				String reason, long expires) {
			this.time = Utils.currentTimeMillis();
			this.username = username;
			this.ip = ip;
			this.moderator = moderator;
			this.reason = reason;
			this.expires = expires;
		}

		public boolean hasExpired() {
			return Utils.currentTimeMillis() >= expires;
		}

		public long getTime() {
			return time;
		}

		public String getUsername() {
			return username;
		}

		public String getIp() {
			return ip;
		}

		public String getModerator() {
			return moderator;
		}

		public String getReason() {
			return reason;
		}

		public long getExpires() {
			return expires;
		}
	}
}