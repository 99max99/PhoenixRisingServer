package net.kagani.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.kagani.Settings;
import net.kagani.game.World;
import net.kagani.game.player.Player;
import net.kagani.login.account.Account;
import net.kagani.network.LoginProtocol;
import net.kagani.network.LoginServerChannelManager;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.stream.OutputStream;
import net.kagani.utils.Utils;

public class FriendsChat {

	/**
	 * Username of the owner.
	 */
	private String owner;
	/**
	 * Name of this chat.
	 */
	private String name;
	/**
	 * Minimum requirement to enter this chat.
	 */
	private int enterReq;
	/**
	 * Minimum requirement to talk in this chat.
	 */
	private int talkReq;
	/**
	 * Minimum requirement to kick from this chat.
	 */
	private int kickReq;
	/**
	 * Minimum requirement to share loot in this chat.
	 */
	private int lootShareReq;
	/**
	 * Whether coin share is enabled.
	 */
	private boolean isCoinShare;

	/**
	 * All members of this chat.
	 */
	private List<Account> members;
	/**
	 * Contains member ranks map.
	 */
	private Map<String, Integer> memberRanks;
	/**
	 * List of accounts who are banned from joining this chat.
	 */
	private List<String> banList;

	public FriendsChat(String owner) {
		this.owner = owner;
		this.name = "N/A";
		this.enterReq = -1;
		this.talkReq = -1;
		this.kickReq = 7;
		this.lootShareReq = 0;
		this.isCoinShare = false;

		this.members = new ArrayList<Account>(100);
		this.memberRanks = new HashMap<String, Integer>();
		this.banList = new ArrayList<String>();
	}

	/**
	 * Happens when one of the members display name changes.
	 */
	public synchronized void onMemberDisplayNameChange() {
		refreshInformation();
	}

	/**
	 * Set's (updates) clan info.
	 */
	public synchronized void setInfo(String name, int enterReq, int talkReq,
			int kickReq, int lootShareReq, boolean isCoinShare) {
		String previousName = this.name;
		int previousKickReq = this.kickReq;

		this.name = name;
		this.enterReq = enterReq;
		this.talkReq = talkReq;
		this.kickReq = kickReq;
		this.lootShareReq = lootShareReq;
		this.isCoinShare = isCoinShare;

		if (!previousName.equals(name) || previousKickReq != kickReq)
			refreshInformation();
	}

	/**
	 * Set's rank of specific username.
	 */
	public synchronized void setRank(String username, int rank) {
		memberRanks.put(username, rank);
		for (Account member : members) {
			if (member.getUsername().equals(username)) {
				refreshInformation();
				break;
			}
		}
	}

	/**
	 * Set's rank of members from specific map.
	 */
	public synchronized void resetRanks(Map<String, Integer> ranks) {
		memberRanks.clear();
		Iterator<Map.Entry<String, Integer>> it$ = ranks.entrySet().iterator();
		while (it$.hasNext()) {
			Map.Entry<String, Integer> set = it$.next();
			memberRanks.put(set.getKey(), set.getValue());
		}
		refreshInformation();
	}

	/**
	 * Trie's to join specific friends chat.
	 */
	public synchronized void join(Account account) {
		if (members.size() >= 100) {
			LoginServerChannelManager
					.sendUnreliablePacket(
							account.getWorld(),
							LoginChannelsPacketEncoder
									.encodePlayerFriendsChatSystemMessage(
											account.getUsername(),
											"This chat is full.").getBuffer());
			return;
		}

		if (banList.contains(account.getUsername())) {
			LoginServerChannelManager.sendUnreliablePacket(
					account.getWorld(),
					LoginChannelsPacketEncoder
							.encodePlayerFriendsChatSystemMessage(
									account.getUsername(),
									"You have been banned from this channel.")
							.getBuffer());
			return;
		}

		if (getRank(account) < enterReq) {
			LoginServerChannelManager
					.sendUnreliablePacket(
							account.getWorld(),
							LoginChannelsPacketEncoder
									.encodePlayerFriendsChatSystemMessage(
											account.getUsername(),
											"You do not have a enough rank to join this friends chat channel.")
									.getBuffer());
			return;
		}

		members.add(account);
		account.setFriendsChat(this);

		LoginServerChannelManager.sendUnreliablePacket(
				account.getWorld(),
				LoginChannelsPacketEncoder
						.encodePlayerFriendsChatSystemMessage(
								account.getUsername(),
								"You are now talking in the friends chat channel "
										+ name).getBuffer());
		refreshInformation();
	}

	/**
	 * Trie's to leave this channel.
	 */
	public synchronized void leave(Account account) {
		members.remove(account);
		account.setFriendsChat(null);

		LoginServerChannelManager.sendUnreliablePacket(
				account.getWorld(),
				LoginChannelsPacketEncoder
						.encodePlayerFriendsChatSystemMessage(
								account.getUsername(),
								"You have left the channel.").getBuffer());
		LoginServerChannelManager.sendReliablePacket(
				account.getWorld(),
				LoginChannelsPacketEncoder
						.encodePlayerFriendsChatInformationUpdatePacket(
								account.getUsername()).getBuffer());
		refreshInformation();

	}

	/**
	 * Kick's every member.
	 */
	public synchronized void disable() {
		for (Account account : members) {
			account.setFriendsChat(null);

			LoginServerChannelManager
					.sendUnreliablePacket(
							account.getWorld(),
							LoginChannelsPacketEncoder
									.encodePlayerFriendsChatSystemMessage(
											account.getUsername(),
											"This friends chat channel has been disabled.")
									.getBuffer());
			LoginServerChannelManager.sendReliablePacket(
					account.getWorld(),
					LoginChannelsPacketEncoder
							.encodePlayerFriendsChatInformationUpdatePacket(
									account.getUsername()).getBuffer());
		}
		members.clear();
	}

	public String test(Account account) {
		return this.name;
	}

	/**
	 * Trie's to send message.
	 */
	public synchronized void sendMessage(Account account, String message) {
		if (account.isMuted()) {
			LoginServerChannelManager
					.sendUnreliablePacket(
							account.getWorld(),
							LoginChannelsPacketEncoder
									.encodePlayerFriendsChatSystemMessage(
											account.getUsername(),
											"You have been temporarily muted due to breaking a rule. This mute will remain for a further -1 hours.")
									.getBuffer());
			return;
		}

		if (getRank(account) < talkReq) {
			LoginServerChannelManager
					.sendUnreliablePacket(
							account.getWorld(),
							LoginChannelsPacketEncoder
									.encodePlayerFriendsChatSystemMessage(
											account.getUsername(),
											"You do not have a enough rank to talk on this friends chat channel.")
									.getBuffer());
			return;
		}
		// String name = owner.equals(Settings.HELP_ACCOUNT) ? "World " +
		// Utils.toNumString(account.getWorld().getId()) : this.name;
		String name = this.name;// this.name;
		long uid = Login.createNewUid();
		for (Account member : members)
			LoginServerChannelManager.sendReliablePacket(
					member.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatMessage(
							member.getUsername(), account.getDisplayName(),
							name, uid, account.getMessageIcon(), message)
							.getBuffer());
	}

	/**
	 * Trie's to send message.
	 */
	public synchronized void sendMessage(Account account, int qcFileId,
			byte[] qcData) {
		if (getRank(account) < talkReq) {
			LoginServerChannelManager
					.sendUnreliablePacket(
							account.getWorld(),
							LoginChannelsPacketEncoder
									.encodePlayerFriendsChatSystemMessage(
											account.getUsername(),
											"You do not have a enough rank to talk on this friends chat channel.")
									.getBuffer());
			return;
		}

		long uid = Login.createNewUid();
		for (Account member : members)
			LoginServerChannelManager.sendReliablePacket(
					member.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerFriendsChatMessage(
							member.getUsername(), account.getDisplayName(),
							name, uid, account.getMessageIcon(), qcFileId,
							qcData).getBuffer());
	}

	/**
	 * Trie's to kick given target.
	 */
	public synchronized void kickMember(Account account, String target) {
		if (getRank(account) < kickReq) {
			LoginServerChannelManager
					.sendUnreliablePacket(
							account.getWorld(),
							LoginChannelsPacketEncoder
									.encodePlayerFriendsChatSystemMessage(
											account.getUsername(),
											"You do not have a enough rank to kick on this friends chat channel.")
									.getBuffer());
			return;
		}

		target = Utils.formatPlayerNameForProtocol(target);
		String displayName;
		Player p2 = World.getPlayerByDisplayName(target);
		if (p2 != null) {
			displayName = p2.getDisplayName();
			target = p2.getUsername();
		} else
			displayName = Utils.formatPlayerNameForDisplay(target);
		if (target == null) {
			LoginServerChannelManager.sendUnreliablePacket(
					account.getWorld(),
					LoginChannelsPacketEncoder
							.encodePlayerFriendsChatSystemMessage(
									account.getUsername(),
									"This player is not in this channel.")
							.getBuffer());
			return;
		}

		Account acc = Login.findAccount(target);
		if (acc == null || !members.contains(acc)
				|| acc.getFriendsChat() != this) {
			LoginServerChannelManager.sendUnreliablePacket(
					account.getWorld(),
					LoginChannelsPacketEncoder
							.encodePlayerFriendsChatSystemMessage(
									account.getUsername(),
									"This player is not in this channel.")
							.getBuffer());
			return;
		}

		if (account == acc) {
			LoginServerChannelManager.sendUnreliablePacket(
					account.getWorld(),
					LoginChannelsPacketEncoder
							.encodePlayerFriendsChatSystemMessage(
									account.getUsername(),
									"You cannot kick or ban yourself.")
							.getBuffer());
			return;
		}

		if (getRank(account) <= getRank(acc)) {
			LoginServerChannelManager
					.sendUnreliablePacket(
							account.getWorld(),
							LoginChannelsPacketEncoder
									.encodePlayerFriendsChatSystemMessage(
											account.getUsername(),
											"You do not have a enough rank to kick this member.")
									.getBuffer());
			return;
		}

		members.remove(acc);
		banList.add(acc.getUsername());
		acc.setFriendsChat(null);

		LoginServerChannelManager.sendUnreliablePacket(
				account.getWorld(),
				LoginChannelsPacketEncoder
						.encodePlayerFriendsChatSystemMessage(
								account.getUsername(),
								"You have kicked " + displayName
										+ " from friends chat channel.")
						.getBuffer());
		LoginServerChannelManager
				.sendUnreliablePacket(
						acc.getWorld(),
						LoginChannelsPacketEncoder
								.encodePlayerFriendsChatSystemMessage(
										acc.getUsername(),
										"You have been kicked from the friends chat channel.")
								.getBuffer());
		LoginServerChannelManager.sendReliablePacket(
				acc.getWorld(),
				LoginChannelsPacketEncoder
						.encodePlayerFriendsChatInformationUpdatePacket(
								acc.getUsername()).getBuffer());
		refreshInformation();
	}

	/**
	 * Trie's to enable lootshare for given target.
	 */
	public synchronized void enableLootshare(Account account) {
		if (!owner.equalsIgnoreCase(Settings.HELP_ACCOUNT)) {
			if (getRank(account) < lootShareReq) {
				LoginServerChannelManager.sendReliablePacket(
						account.getWorld(),
						LoginChannelsPacketEncoder.encodePlayerVarUpdate(
								account.getUsername(),
								LoginProtocol.VAR_TYPE_LOOTSHARE, 0)
								.getBuffer());
				LoginServerChannelManager
						.sendUnreliablePacket(
								account.getWorld(),
								LoginChannelsPacketEncoder
										.encodePlayerFriendsChatSystemMessage(
												account.getUsername(),
												"You do not have a enough rank to use lootshare on this friends chat channel.")
										.getBuffer());
				return;
			}
		}
		if (account.isLobby()
				|| (account.getWorld().getInformation().getFlags() & 0x8) == 0) {
			LoginServerChannelManager.sendReliablePacket(
					account.getWorld(),
					LoginChannelsPacketEncoder.encodePlayerVarUpdate(
							account.getUsername(),
							LoginProtocol.VAR_TYPE_LOOTSHARE, 0).getBuffer());
			LoginServerChannelManager.sendUnreliablePacket(
					account.getWorld(),
					LoginChannelsPacketEncoder
							.encodePlayerFriendsChatSystemMessage(
									account.getUsername(),
									"LootShare is disabled on this world.")
							.getBuffer());
			return;
		}

		LoginServerChannelManager.sendReliablePacket(
				account.getWorld(),
				LoginChannelsPacketEncoder.encodePlayerVarUpdate(
						account.getUsername(),
						LoginProtocol.VAR_TYPE_LOOTSHARE, 1).getBuffer());
	}

	/**
	 * Return's count of members on this chat.
	 */
	public synchronized int membersCount() {
		return members.size();
	}

	/**
	 * Refreshe's channel information.
	 */
	private void refreshInformation() {
		String ownerName = Login.getDisplayName(owner);
		if (ownerName == null)
			ownerName = "N/A"; // just in case

		OutputStream packet = LoginChannelsPacketEncoder
				.createPlayerFriendsChatInformation(owner, ownerName, name,
						kickReq, members.size());
		for (Account member : members)
			LoginChannelsPacketEncoder.appendFriendsChatMember(packet,
					member.getDisplayName(), member.getWorld().getId(),
					getRank(member), (member.isLobby() ? "Lobby " : "World ")
							+ member.getWorld().getId());
		LoginChannelsPacketEncoder.finishPlayerFriendsChatInformation(packet);

		for (Account member : members)
			LoginServerChannelManager.sendReliablePacket(
					member.getWorld(),
					LoginChannelsPacketEncoder
							.encodePlayerFriendsChatInformationUpdatePacket(
									member.getUsername(), packet).getBuffer());
	}

	/**
	 * Find's rank of specific account.
	 */
	private int getRank(Account account) {
		for (int names = 0; names < Settings.SERVER_ADMINISTRATORS.length; names++) {
			if (account.getUsername().equalsIgnoreCase(
					Settings.SERVER_ADMINISTRATORS[names])) {
				return 127;
			}
		}
		if (account.getUsername().equals(owner))
			return 7;
		Integer rank = memberRanks.get(account.getUsername());
		if (rank != null)
			return rank;
		return -1;
	}

	public String getOwner() {
		return owner;
	}

	public String getName() {
		return name;
	}

	public int getEnterReq() {
		return enterReq;
	}

	public int getTalkReq() {
		return talkReq;
	}

	public int getKickReq() {
		return kickReq;
	}

	public int getLootShareReq() {
		return lootShareReq;
	}

	public boolean isCoinShare() {
		return isCoinShare;
	}
}