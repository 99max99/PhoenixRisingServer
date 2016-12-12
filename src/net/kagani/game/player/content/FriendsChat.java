package net.kagani.game.player.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.cache.loaders.QuickChatOptionDefinition;
import net.kagani.game.minigames.clanwars.ClanWars;
import net.kagani.game.player.Player;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;

public class FriendsChat {

	/**
	 * Contains all cached chats.
	 */
	private static Map<String, FriendsChat> cache;
	/**
	 * Contains the channel key(aka owner username)
	 */
	private String channel;
	/**
	 * Contains list of local (this world) members.
	 */
	private CopyOnWriteArrayList<Player> localMembers;
	/**
	 * Clan wars instance.
	 */
	private ClanWars clanWars;

	public static void init() {
		cache = new HashMap<String, FriendsChat>();
	}

	public FriendsChat(String channel) {
		this.channel = channel;
		this.localMembers = new CopyOnWriteArrayList<Player>();
	}

	/**
	 * Attaches player to given channel.
	 */
	public static void attach(Player player, String channel) {
		if (player.getCurrentFriendsChat() != null)
			detach(player);

		synchronized (cache) {
			FriendsChat chat = cache.get(channel);
			if (chat == null) {
				chat = new FriendsChat(channel);
				cache.put(channel, chat);
			}

			chat.getLocalMembers().add(player);
			player.setCurrentFriendsChat(chat);
		}

	}

	public static void detach(Player player) {
		if (player.getCurrentFriendsChat() == null)
			return;
		synchronized (cache) {
			FriendsChat chat = player.getCurrentFriendsChat();
			player.setCurrentFriendsChat(null);
			chat.getLocalMembers().remove(player);
			player.disableLootShare();
			if (chat.clanWars != null)
				chat.clanWars.leaveFC(player);
			if (chat.getLocalMembers().size() <= 0)
				removeChat(chat);

		}
	}

	public static void removeChat(FriendsChat chat) {
		cache.remove(chat.getChannel());
		if (chat.clanWars != null)
			chat.clanWars.endWar();
	}

	/**
	 * Requests joining of specific channel.
	 */
	public static void requestJoin(Player player, String name) {
		player.getFriendsIgnores().fcSystemMessage(
				"Attempting to join channel...");
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendsChatJoinLeaveRequest(player.getUsername(),
						name).getBuffer());
	}

	/**
	 * Requests leaving of current channel.
	 */
	public static void requestLeave(Player player) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendsChatJoinLeaveRequest(player.getUsername(),
						null).getBuffer());
	}

	/**
	 * Send's message request.
	 */
	public void sendMessage(Player player, String message) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendsChatMessageRequest(player.getUsername(),
						message).getBuffer());
	}

	/**
	 * Send's quick chat message request.
	 */
	public void sendMessage(Player player, QuickChatOptionDefinition option,
			long[] qcData) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendsChatMessageRequest(player, option, qcData)
				.getBuffer());
	}

	/**
	 * Send's kick request.
	 */
	public void kickMember(Player player, String target) {
		LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder
				.encodePlayerFriendsChatKickRequest(player.getUsername(),
						target).getBuffer());
	}

	/**
	 * Send's lootshare request.
	 */
	public void toogleLootshare(Player player) {
		if (player.isLootShareEnabled())
			player.disableLootShare();
		else {
			LoginClientChannelManager
					.sendReliablePacket(LoginChannelsPacketEncoder
							.encodePlayerFriendsChatLootshareRequest(
									player.getUsername()).getBuffer());
		}
	}

	/**
	 * Get's list of loot sharing people.
	 */
	public static List<Player> getLootSharingPeople(Player player) {
		if (!player.isLootShareEnabled())
			return null;
		FriendsChat chat = player.getCurrentFriendsChat();
		if (chat == null)
			return null;
		List<Player> players = new ArrayList<Player>();
		for (Player p2 : player.getCurrentFriendsChat().getLocalMembers()) {
			if (p2.isLootShareEnabled() && p2.withinDistance(player))
				players.add(p2);
		}
		return players;
	}

	/**
	 * Send's message to all local members.
	 */
	public void sendLocalMessage(String message) {
		for (Player p2 : localMembers)
			p2.getPackets().sendGameMessage(message);
	}

	public String getChannel() {
		return channel;
	}

	public CopyOnWriteArrayList<Player> getLocalMembers() {
		return localMembers;
	}

	public ClanWars getClanWars() {
		return clanWars;
	}

	public void setClanWars(ClanWars clanWars) {
		this.clanWars = clanWars;
	}
}