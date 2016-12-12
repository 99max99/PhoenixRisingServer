package net.kagani.login;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.kagani.Settings;
import net.kagani.login.account.Account;
import net.kagani.utils.saving.JsonFileManager;

public class GameWorld {

	/**
	 * Contains world information.
	 */
	private WorldInformation information;
	/**
	 * Contains login address of the world.
	 */
	private InetSocketAddress loginAddress;
	/**
	 * Contains list of online lobby players.
	 */
	private List<Account> lobbyPlayers;
	/**
	 * Contains list of online game players.
	 */
	private List<Account> gamePlayers;

	public GameWorld(WorldInformation information) {
		this.information = information;
		this.loginAddress = new InetSocketAddress(
				Settings.HOSTED ? "149.56.0.42" : "127.0.0.1",
				Settings.LOGIN_CLIENT_ADDRESS_BASE.getPort()
						+ information.getId());
		this.lobbyPlayers = new ArrayList<Account>();
		this.gamePlayers = new ArrayList<Account>();
	}

	/**
	 * Save's all online accounts.
	 */
	public void saveAccounts() {
		synchronized (lobbyPlayers) {
			for (Account account : lobbyPlayers)
				JsonFileManager.saveAccount(account);
		}

		synchronized (gamePlayers) {
			for (Account account : gamePlayers)
				JsonFileManager.saveAccount(account);
		}
	}

	/**
	 * Fire's account pm status change event, informing every other account
	 * thats logged in.
	 */
	public void onAccountPmStatusChange(Account account, int previousStatus,
			int currentStatus) {
		synchronized (lobbyPlayers) {
			for (Account acc : lobbyPlayers)
				acc.getFriendsIgnores().onAccountPmStatusChange(account,
						previousStatus, currentStatus);
		}

		synchronized (gamePlayers) {
			for (Account acc : gamePlayers)
				acc.getFriendsIgnores().onAccountPmStatusChange(account,
						previousStatus, currentStatus);
		}
	}

	/**
	 * Fire's account display name change event, informing every other account
	 * thats logged in.
	 */
	public void onAccountDisplayNameChange(Account account) {
		synchronized (lobbyPlayers) {
			for (Account acc : lobbyPlayers)
				acc.getFriendsIgnores().onAccountDisplayNameChange(account);
		}

		synchronized (gamePlayers) {
			for (Account acc : gamePlayers)
				acc.getFriendsIgnores().onAccountDisplayNameChange(account);
		}
	}

	/**
	 * Return's new list containing all online accounts.
	 */
	public List<Account> getAllOnlineAccountsCopy() {
		List<Account> all = new ArrayList<Account>();
		synchronized (lobbyPlayers) {
			all.addAll(lobbyPlayers);
		}
		synchronized (gamePlayers) {
			all.addAll(gamePlayers);
		}
		return all;
	}

	/**
	 * Return's number of players online with specific ip.
	 */
	public int getPlayersOnline(String ip) {
		int count = 0;
		synchronized (lobbyPlayers) {
			for (Account account : lobbyPlayers)
				if (account.getIp().equals(ip))
					count++;
		}
		synchronized (gamePlayers) {
			for (Account account : gamePlayers)
				if (account.getIp().equals(ip))
					count++;
		}
		return count;
	}

	/**
	 * Add's account.
	 */
	public void add(Account account) {
		if (account.isLobby()) {
			synchronized (lobbyPlayers) {
				lobbyPlayers.add(account);
			}
		} else {
			synchronized (gamePlayers) {
				gamePlayers.add(account);
			}
		}
	}

	/**
	 * Remove's account.
	 */
	public void remove(Account account) {
		if (account.isLobby()) {
			synchronized (lobbyPlayers) {
				lobbyPlayers.remove(account);
			}
		} else {
			synchronized (gamePlayers) {
				gamePlayers.remove(account);
			}
		}
	}

	/**
	 * Find's online account.
	 */
	public Account findAccount(String username) {
		synchronized (gamePlayers) {
			for (Account account : gamePlayers)
				if (account.getUsername().equals(username))
					return account;
		}
		synchronized (lobbyPlayers) {
			for (Account account : lobbyPlayers)
				if (account.getUsername().equals(username))
					return account;
		}
		return null;
	}

	/**
	 * Find's online accounts by ip.
	 */
	public int findAccountsByIp(List<Account> list, String ip) {
		int count = 0;
		synchronized (gamePlayers) {
			for (Account account : gamePlayers)
				if (account.getIp().equals(ip)) {
					list.add(account);
					count++;
				}
		}
		synchronized (lobbyPlayers) {
			for (Account account : lobbyPlayers)
				if (account.getIp().equals(ip)) {
					list.add(account);
					count++;
				}
		}
		return count;
	}

	/**
	 * Return's count of players online.
	 */
	public int getGamePlayersOnline() {
		synchronized (gamePlayers) {
			return gamePlayers.size();
		}
	}

	/**
	 * Return's count of lobby players online.
	 */
	public int getLobbyPlayersOnline() {
		synchronized (lobbyPlayers) {
			return lobbyPlayers.size();
		}
	}

	/**
	 * Return's world id.
	 */
	public int getId() {
		return information.getId();
	}

	/**
	 * Return's class holding various information about the world.
	 */
	public WorldInformation getInformation() {
		return information;
	}

	/**
	 * Return's login address of this world.
	 */
	public InetSocketAddress getLoginAddress() {
		return loginAddress;
	}
}