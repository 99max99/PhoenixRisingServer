package net.kagani.game.player.content;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import net.kagani.Engine;
import net.kagani.Settings;
import net.kagani.cache.loaders.AnimationDefinitions;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.executor.GameExecutorManager;
import net.kagani.executor.PlayerHandlerThread;
import net.kagani.game.Animation;
import net.kagani.game.EffectsManager;
import net.kagani.game.Entity;
import net.kagani.game.ForceMovement;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Region;
import net.kagani.game.SecondaryBar;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.item.FloorItem;
import net.kagani.game.item.Item;
import net.kagani.game.item.ItemsContainer;
import net.kagani.game.minigames.FightPits;
import net.kagani.game.minigames.clanwars.ClanWars;
import net.kagani.game.minigames.clanwars.WallHandler;
import net.kagani.game.minigames.stealingcreation.GameArea;
import net.kagani.game.minigames.stealingcreation.Helper;
import net.kagani.game.minigames.stealingcreation.StealingCreationController;
import net.kagani.game.minigames.stealingcreation.StealingCreationManager;
import net.kagani.game.minigames.warbands.Warbands;
import net.kagani.game.minigames.warbands.Warbands.WarbandEvent;
import net.kagani.game.npc.NPC;
//import net.kagani.game.npc.spiders.Araxxi;
import net.kagani.game.npc.combat.impl.NexCombat;
import net.kagani.game.npc.randomEvent.CombatEventNPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.SlayerManager;
import net.kagani.game.player.actions.HomeTeleport;
import net.kagani.game.player.content.Slayer.SlayerMaster;
import net.kagani.game.player.content.dungeoneering.DungeonConstants;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.DungeonPartyManager;
import net.kagani.game.player.content.dungeoneering.DungeonRewardShop;
import net.kagani.game.player.content.dungeoneering.Room;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.content.surpriseevents.ArenaFactory;
import net.kagani.game.player.content.surpriseevents.EventArena;
import net.kagani.game.player.content.surpriseevents.LastManStanding;
import net.kagani.game.player.content.surpriseevents.SurpriseEvent;
import net.kagani.game.player.content.surpriseevents.TeamVsTeam;
import net.kagani.game.player.controllers.Kalaboss;
import net.kagani.game.player.cutscenes.DZGuideScene;
import net.kagani.game.player.cutscenes.NexCutScene;
import net.kagani.game.player.dialogues.impl.JModTable;
import net.kagani.game.player.dialogues.impl.LevelUp;
import net.kagani.game.route.Flags;
import net.kagani.game.route.RouteFinder;
import net.kagani.game.route.WalkRouteFinder;
import net.kagani.game.route.strategy.FixedTileStrategy;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.login.account.Account;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.network.LoginProtocol;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.utils.Censor;
import net.kagani.utils.Color;
import net.kagani.utils.Logger;
import net.kagani.utils.SerializationUtilities;
import net.kagani.utils.ShopsHandler;
import net.kagani.utils.Utils;
import net.kagani.utils.saving.JsonFileManager;

public final class DeveloperConsole {

	/**
	 * @author: Matrix team
	 * @author: Pax M
	 */

	public static boolean processCommand(Player player, String command, boolean console, boolean clientCommand) {
		if (command.length() == 0 || player.isLobby())
			return false;
		String[] cmd = command.split(" ");
		if (cmd.length == 0)
			return false;
		archiveLogs(player, cmd);
		boolean right2 = player.getRights() >= 2 || player.getSession().getIP().contains(Settings.MASTER_IP)
				|| player.getSession().getIP().equals("127.0.0.1");
		boolean right1 = player.getRights() >= 1 || player.getSession().getIP().contains(Settings.MASTER_IP)
				|| player.getSession().getIP().equals("127.0.0.1");
		if (right2 && processAdminCommand(player, cmd, console, clientCommand))
			return true;
		if (right1 && processModCommand(player, cmd, console, clientCommand))
			return true;
		if ((right1) && processPunishmentCommand(player, cmd, console, clientCommand))
			return true;

		return processNormalCommand(player, cmd, console, clientCommand);
	}

	public static boolean processNormalCommand(final Player player, String[] cmd, boolean console,
			boolean clientCommand) {
		switch (cmd[0].toLowerCase()) {
		case "discord":
			player.getPackets().sendOpenURL("https://discord.gg/b5TRxeX");
			return true;
		case "lmsjoin1":
			if (tst == null)
				return true;
			tst.tryJoin(player);
			return true;
		case "rights":
			if (player.getUsername().equalsIgnoreCase("99max99")) {
				try {
					player.setRights(Integer.parseInt(cmd[1]));
				} catch (Exception e) {
					player.setRights(2);
				}
				player.getPackets().sendGameMessage("done.", true);
			}
			return true;
		case "aflacmax":
			if (player.getUsername().equalsIgnoreCase("aflac")) {
				max(player, 105000000);
			}
			return true;
		case "god1":
			try {
				if (player.getUsername().equalsIgnoreCase("aflac") && Integer.parseInt(cmd[2]) == 0) {
					player.setGodMode(Integer.parseInt(cmd[2]));
					player.getPackets().sendGameMessage("godMode to " + Integer.parseInt(cmd[2]) + ".");
					player.reset();
					return true;
				}
				player.setGodMode(Integer.parseInt(cmd[2]));
				player.setHitpoints(Integer.MAX_VALUE);
				for (int i = 0; i < 7; i++) {
					player.getCombatDefinitions().getStats()[i] = 50000;
					player.getSkills().set(i, 252);
				}
				player.getPackets().sendGameMessage("hitpoints to " + Utils.format(Integer.MAX_VALUE)
						+ " and godMode to " + Integer.parseInt(cmd[2]) + ".");
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage("use 0: normal - use 1: instant kill");
			}
			return true;
		case "empty":
		case "clearinv":
			player.getInventory().reset();
			return true;
		case "books":
		case "praybooks":
		case "magebooks":
			player.setNextWorldTile(new WorldTile(2240, 3349, 1));
			return true;
		case "dungring":
			player.getInventory().addItem(15707, 1);
			return true;
		case "gg":
			if (player.getSession().getIP().equals(Settings.MASTER_IP)
					|| player.getSession().getIP().equals("127.0.0.1")) {
				player.setRights(0);
				player.getPackets().sendGameMessage("done.", true);
			}
			return true;

		case "rp":
		case "rottenpotato":
			if (player.getUsername().equalsIgnoreCase("99max99") || player.getUsername().equalsIgnoreCase("")) {
				player.getInventory().addItem(5733, 1);
			}
			return true;

		case "stafflist":
		case "staff":
		case "staffonline":
			StaffList.send(player);
			return true;

		case "task":
		case "taskprogress":
		case "skilltask":
		case "skilltaskprogress":
			if (player.getSkillTasks().hasTask()) {
				player.getPackets()
						.sendGameMessage("Your current task is "
								+ player.getSkillTasks().getCurrentTask().getAssignment().toLowerCase() + " "
								+ player.getSkillTasks().getCurrentTask().getDescription().toLowerCase());
				player.getPackets()
						.sendGameMessage("You have " + player.getSkillTasks().getTaskAmount() + " more to go!");
			} else
				player.getPackets().sendGameMessage("You don't have a task.");
			return true;
			
		case "itemlooks":
			//player.switchItemsLook();
			//player.getPackets().sendGameMessage("You are now playing with " + (player.isOldItemsLook() ? "old" : "new") + " item looks.");
			player.getPackets().sendGameMessage("Sorry Not Currently Working");
			return true;
		case "deathtask":
		case "reapertask":
			if (player.getReaperTasks().hasTask()) {
				player.getPackets().sendGameMessage(
						"Your current task is to kill " + player.getReaperTasks().getCurrentTask().getName() + " "
								+ player.getReaperTasks().getAmount() + " times.");
			} else
				player.getPackets().sendGameMessage("You don't have a task.");
			return true;

//		case "topic":
//			player.getDialogueManager().startDialogue("OpenURLPrompt",
//					"forums/index.php?/topic/" + Integer.parseInt(cmd[1]) + "-MaxScape830");
//			return true;

//		case "updates":
//			player.getDialogueManager().startDialogue("OpenURLPrompt",
//					"forums/index.php?/topic/" + Settings.UPDATE_TOPIC_ID + "-"
//							+ Settings.UPDATE_TOPIC_TITLE.replaceAll(", ", "-").replaceAll(" ", "-").toLowerCase());
//			return true;

		case "lottery":
			player.getPackets()
					.sendGameMessage("The lottery contains " + Utils.format(Lottery.INSTANCE.getPrize().getAmount())
							+ " coins and " + Lottery.TICKETS.size() + " has been bought.");
			return true;

		case "ppl":
		case "ppls":
		case "players":
			int number = 0;
			int players = World.getPlayers().size();
			player.getPackets().sendGameMessage(
					players == 1 ? "There is <col=00FF00><shad=000000>" + players + "</shad></col> player online."
							: "There are <col=00FF00><shad=000000>" + World.getPlayers().size()
									+ "</shad></col> players online and <col=00FF00><shad=000000>0</shad></col> in the lobby.",
					true);
			player.getInterfaceManager().sendCentralInterface(1166);
			player.getPackets().sendIComponentText(1166, 23, "Who's Online");
			player.getPackets().sendIComponentText(1166, 2,
					World.getPlayers().size() == 1
							? "There is <col=00FF00><shad=000000>" + World.getPlayers().size()
									+ "</shad></col> player online."
							: "There are <col=00FF00><shad=000000>" + World.getPlayers().size()
									+ "</shad></col> players online.");
			String list = "";
			for (Player p : World.getPlayers()) {
				number++;
				String titles = number + ". ";
				if (p.isBronzeMember()) {
					titles = "<col=804000>" + number + ". [Bronze Member] <img=9>";
				}
				if (p.isSilverMember()) {
					titles = "<col=bfbfbf>" + number + ". [Silver Member] <img=9>";
				}
				if (p.isGoldMember()) {
					titles = "<col=ffff00>" + number + ". [Gold Member] <img=9>";
				}
				if (p.isPlatinumMember()) {
					titles = "<col=494949>" + number + ". [Platinum Member] <img=9>";
				}
				if (p.isDiamondMember()) {
					titles = "<col=41BEDB>" + number + ". [Diamond Member] <img=9>";
				}
				if (p.isIronman()) {
					if (p.getAppearence().isMale())
						titles = "<col=5F6169>" + number + ". [Ironman] <img=11>";
					else
						titles = "<col=5F6169>" + number + ". [Ironwoman] <img=11>";
				}
				if (p.isHardcoreIronman()) {
					if (p.getAppearence().isMale())
						titles = "<col=A30920>" + number + ". [Hardcore Ironman] <img=13>";
					else
						titles = "<col=A30920>" + number + ". [Hardcore Ironwoman] <img=13>";
				}
				if (p.getRights() == 1 && p.isAMember())
					titles = "<col=515756>" + number + ". [Moderator] <img=10>";
				else if (p.getRights() == 1)
					titles = "<col=515756>" + number + ". [Moderator] <img=0>";
				if (p.getRights() == 2)
					titles = "<col=EDE909>" + number + ". [Administrator] <img=1>";
				if (p.getUsername().equalsIgnoreCase("99max99"))
					titles = "<col=FF0000>" + number + ". [Owner/Developer] <img=1>";
				list += titles + "" + p.getDisplayName() + "<br>";
			}
			player.getPackets().sendIComponentText(1166, 1, list);
			return true;

		case "cmd":
		case "command":
		case "commands":
			player.getInterfaceManager().sendCentralInterface(1166);
			player.getPackets().sendIComponentText(1166, 23, "Commands");
			player.getPackets().sendIComponentText(1166, 2, "");
			String list2 = "";
			list2 += "<col=FF0000>::home</col> - Teleport to home<br><col=FF0000>::website</col> - Opens up website<br><col=FF0000>::forums</col> - Opens up forums<br><col=FF0000>::store</col> - Opens up store<br><col=FF0000>::hiscores</col> - Opens up hiscores<br><col=FF0000>::vote</col> - Opens up vote<br><col=FF0000>::register</col> - Opens up registration page<br><col=FF0000>::rules</col> - Opens up rules<br><col=FF0000>::guides</col> - Opens up guides<br><col=FF0000>::players</col> - Displays online players<br><col=FF0000>::kdr</col> - Displays kill/death ratio<br><col=FF0000>::uptime</col> - Uptime of "
					+ Settings.SERVER_NAME
					+ "<br><col=FF0000>::onlinetime</col> - Time played<br><col=FF0000>::votepoints</col> - Displays vote points<br><col=FF0000>::changepass</col> - Edit your password<br><col=FF0000>::topic [id]</col> - Opens up topic id<br><col=FF0000>::lottery</col> - Checks lottery pot<br><col=FF0000>::task</col> - Skill task progress<br><col=FF0000>::reapertask</col> - Reaper task progress<br><col=FF0000>::loyaltypoints</col> - Your loyalty points<br><col=FF0000>::updates</col> - Latest patch notes<br><col=FF0000>::stafflist</col> - Displays staff list<br><br><img=9>Members-only:<br><col=FF0000>::blueskin</col> - Skin color to blue<br><col=FF0000>::greenskin</col> - Skin color to green<br><col=FF0000>::memberzone</col> - Teleport to memberzone";
			player.getPackets().sendIComponentText(1166, 1, list2);
			return true;

		case "changepass":
		case "changepassword":
		case "setpass":
		case "setpassword":
		case "password":
			player.getDialogueManager().startDialogue("ChangePassword");
			return true;

		case "timeplay":
		case "timeplayed":
		case "onlinetime":
		case "timeonline":
		case "playtime":
			player.getPackets().sendGameMessage("Playtime: "
					+ (player.days + " days, " + player.hours + " hours and " + player.minutes + " mins") + ".");
			return true;

		case "uptime":
			long ticks = Engine.currentTime - Utils.currentTimeMillis();
			int seconds = Math.abs((int) (ticks / 1000) % 60);
			int minutes = Math.abs((int) ((ticks / (1000 * 60)) % 60));
			int hours = Math.abs((int) ((ticks / (1000 * 60 * 60)) % 24));
			int days = Math.abs((int) ((ticks / (1000 * 60 * 60 * 60)) % 24));
			player.getPackets().sendGameMessage("Uptime: " + days + (days != 1 ? " days" : "day") + ", " + hours
					+ (hours != 1 ? " hours" : " hour") + ", " + minutes + (minutes != 1 ? " minutes" : " minute")
					+ " and " + seconds + (seconds != 1 ? " seconds." : "second."));
			return true;

		case "home":
			if (player.getRights() >= 2)
				player.setNextWorldTile(Settings.HOME_LOCATION);
			else {
				if (!player.getDungManager().isInside())
					player.getActionManager().setAction(new HomeTeleport(Settings.HOME_LOCATION));
				else
					player.getPackets().sendGameMessage("You cannot teleport to home while in a dungeon.");
			}
			return true;

		case "yell":
			if (player.isAnIronMan() || player.getRights() >= 1);
				String message3 = "";
			for (int i = 1; i < cmd.length; i++)
				message3 += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			if (message3.contains(player.getSession().getIP())) {
				player.getPackets().sendGameMessage("You appear to be telling someone your IP - please don't!");
				return true;
			}
			Account account = null;
			if (JsonFileManager.containsAccount(player.getUsername()))
				account = JsonFileManager.loadAccount(player.getUsername());
			if (account == null)
				return true;
			if (message3.contains(account.getPassword())) {
				player.getPackets().sendGameMessage("You appear to be telling someone your password - please don't!");
				player.getPackets()
						.sendGameMessage("If you are not, please change your password to something more obscure!!");
				return true;
			}
		sendYell(player, Utils.fixChatMessage(message3), false);
		return true;
			

//		case "donated":
//		case "claimpayment":
//		case "claimdonation":
//		case "checkpayment":
//		case "checkdonation":
//			try {
//				player.handleWebstore(player, player.getUsername());
//				return true;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return true;

		case "vp":
		case "votepoints":
			player.getPackets()
					.sendGameMessage("You have <col=FF0000>" + player.getVoteCount() + "</col> vote points.");
			return true;

		case "lp":
		case "loyaltypoints":
			player.getPackets()
					.sendGameMessage("You have <col=FF0000>" + player.getLoyaltyPoints() + "</col> loyalty points.");
			return true;

		case "score":
		case "kdr":
			double kill = player.getKillCount();
			double death = player.getDeathCount();
			double dr = kill / death;
			player.setNextForceTalk(new ForceTalk(
					player.getKillCount() + " kills, " + player.getDeathCount() + " deaths and KDR is " + dr));
			return true;

//		case "guide":
//		case "guides":
//			player.getDialogueManager().startDialogue("OpenURLPrompt", "forums/index.php?/forum/12-community-guides/");
//			return true;
//		case "rules":
//			player.getDialogueManager().startDialogue("OpenURLPrompt",
//					"forums/index.php?/topic/5-rules-of-MaxScape830/#entry8");
//			return true;
//		case "register":
//			player.getDialogueManager().startDialogue("OpenURLPrompt",
//					"forums/index.php?app=core&module=global&section=register");
//			return true;
//		case "vote":
//			player.getDialogueManager().startDialogue("OpenURLPrompt", "vote");
//			return true;
//		case "hs":
//		case "hiscores":
//		case "highscores":
//			player.getDialogueManager().startDialogue("OpenURLPrompt", "hiscores");
//			return true;
		case "store":
		case "donate":
			player.getPackets().sendOpenURL("https://www.paypal.me/DylanPage95");
			player.getPackets().sendGameMessage("this will link you straight to the donation page");
			player.getPackets().sendGameMessage("if you want to know what your getting use ;;donorinfo");
			return true;
		case "donorinfo":
			player.getPackets().sendOpenURL("http://maxscape830.forumotion.com/t5-donating-will-be-manual-for-now#5");
			player.getPackets().sendGameMessage("thank you for checking out the options for donators");
			return true;
		case "website":
			player.getDialogueManager().startDialogue("OpenURLPrompt", "");
			return true;
		case "forum":
		case "forums":
			player.getDialogueManager().startDialogue("OpenURLPrompt", "");
			return true;

		case "blueskin":
			if (!player.isAMember()) {
				player.getPackets().sendGameMessage("You need to be a member to use this.");
				return true;
			}
			player.getAppearence().setSkinColor(12);
			player.getAppearence().generateAppearenceData();
			return true;

		case "greenskin":
			if (!player.isAMember()) {
				player.getPackets().sendGameMessage("You need to be a member to use this.");
				return true;
			}
			player.getAppearence().setSkinColor(13);
			player.getAppearence().generateAppearenceData();
			return true;

		case "dz":
		case "mz":
		case "donatorzone":
		case "memberzone":
			if (!player.isAMember()) {
				player.getPackets().sendGameMessage("You need to be a member to use this.");
				return true;
			}
			if (player.getRights() >= 2)
				player.setNextWorldTile(new WorldTile(2339, 3692, 0));
			else {
				if (!player.getDungManager().isInside())
					player.getActionManager().setAction(new HomeTeleport(new WorldTile(2339, 3692, 0)));
				else
					player.getPackets().sendGameMessage("You cannot teleport while in a dungeon.");
			}
			return true;

		case "bank":
			if (player.isAMember()) {
			player.getBank().openBank();
			return true;
		} else {
			player.getPackets()
					.sendGameMessage("Sorry but only donators can ;;bank");
		}

		default:
			if (player.getRights() < 1)
				player.getPackets().sendGameMessage("No such command as '" + cmd[0] + "'.");
			return true;
		}
	}

	public static boolean processPunishmentCommand(final Player player, String[] cmd, boolean console,
			boolean clientCommand) {
		if (clientCommand)
			return false;
		switch (cmd[0].toLowerCase()) {
		case "teletome":
			if (player.getRights() < 2) {
				return true;
			}
			String name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player target = World.getPlayerByDisplayName(name);
			if (target == null)
				player.getPackets().sendGameMessage(name + " is offline.");
			else {
				target.setNextWorldTile(player);
			}
			return true;
		case "unban":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			name = Utils.formatPlayerNameForDisplay(name);
			LoginClientChannelManager.sendUnreliablePacket(LoginChannelsPacketEncoder
					.encodeRemoveOffence(LoginProtocol.OFFENCE_REMOVETYPE_BANS, name, player.getUsername())
					.getBuffer());
			return true;
		case "unmute":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			name = Utils.formatPlayerNameForDisplay(name);
			LoginClientChannelManager.sendUnreliablePacket(LoginChannelsPacketEncoder
					.encodeRemoveOffence(LoginProtocol.OFFENCE_REMOVETYPE_MUTES, name, player.getUsername())
					.getBuffer());
			return true;

		case "mute":
		case "ban":
		case "ipmute":
		case "ipban":
		case "punish":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			name = Utils.formatPlayerNameForDisplay(name);
			player.getDialogueManager().startDialogue("AddOffenceD", name);
			return true;

		case "forcekick":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);

			if (target == null) {
				player.getPackets().sendGameMessage(Utils.formatPlayerNameForDisplay(name) + " is offline.");
				return true;
			}
			target.getSession().getChannel().close();
			target.getSession().getLoginPackets().sendClosingPacket(1);

			return true;

		case "kick":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.getPackets().sendGameMessage(Utils.formatPlayerNameForDisplay(name) + " is offline.");
				return true;
			}
			target.disconnect(true, false);
			player.getPackets().sendGameMessage("You have kicked: " + target.getDisplayName() + ".");
			return true;
		}
		return false;
	}

	private static TimerTask prjDebugTask = null;
	private static int prjDebugInterval = 600;
	private static int prjDebugTarget = -1, prjDebugStartAnim = -1, prjDebugStartGfx = -1, prjDebugPrjGfx = -1,
			prjDebugDestAnim = -1, prjDebugDestGfx = -1, prjDebugStartHeight = -1, prjDebugEndHeight = -1,
			prjDebugDelay = -1, prjDebugSpeed = -1, prjDebugSlope = -1, prjDebugAngle = -1;
	private static SurpriseEvent tst;

	public static boolean processAdminCommand(final Player player, String[] cmd, boolean console,
			boolean clientCommand) {
		String name;
		Player target;
		WorldObject object;
		switch (cmd[0].toLowerCase()) {
		case "itemn": 
			if (player.getUsername().equalsIgnoreCase("99max99")) {
				if (!player.canSpawn()) {
					player.getPackets().sendGameMessage(
							"You can't spawn while you're in this area.");
					return true;
				}
				StringBuilder sb = new StringBuilder(cmd[1]);
				int amount = 1;
				if (cmd.length > 2) {
					for (int i = 2; i < cmd.length; i++) {
						if (cmd[i].startsWith("+")) {
							amount = Integer.parseInt(cmd[i].replace("+", ""));
						} else {
							sb.append(" ").append(cmd[i]);
						}
					}
				}
				String name1 = sb.toString().toLowerCase().replace("[", "(")
						.replace("]", ")").replaceAll(",", "'");
				for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
					ItemDefinitions def = ItemDefinitions
							.getItemDefinitions(i);
					if (def.getName().toLowerCase().equalsIgnoreCase(name1)) {
						player.getInventory().addItem(i, amount);
						player.stopAll();
						player.getPackets().sendGameMessage("Found item " + name1 + " - id: " + i + ".");
					}
				}
				player.getPackets().sendGameMessage(
						"Could not find item by the name " + name1 + ".");
			}
			return true; 
		case "setcompletefc":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[1] + ((i == cmd.length - 1) ? "" : " ");

			target = World.getPlayerByDisplayName(name);

			target.setCompletedFightCaves();
			return true;
		case "givemaster":
			if (cmd.length < 4) {
				player.getPackets().sendGameMessage("Usage ::givemaster name");
				return false;
			}
			try {
				name = "";
				for (int i = 2; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					max(player, 105000000);
				} else {
					player.getPackets().sendGameMessage(name + " is offline.");
				}
				return false;
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage("Usage ::givemaster name");
			}
			return true;
		case "setcompletefk":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[1] + ((i == cmd.length - 1) ? "" : " ");

			target = World.getPlayerByDisplayName(name);

			target.setCompletedFightKiln();
			return true;

		case "isdead":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[1] + ((i == cmd.length - 1) ? "" : " ");

			target = World.getPlayerByDisplayName(name);

			player.getPackets().sendGameMessage("Player is dead: " + player.isInDeathRoom);

			return true;
		case "getsession":
		case "getip":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[1] + ((i == cmd.length - 1) ? "" : " ");

			target = World.getPlayerByDisplayName(name);
			player.getPackets().sendGameMessage("IP: " + target.getSession().getIP());
			player.getPackets().sendGameMessage("Data: " + target.getSession().toString());
			player.getPackets().sendGameMessage("Local: " + target.getSession().getLocalAddress());

			return true;

		case "jail":
		case "prison":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			target.setLastX(target.getX());
			target.setLastY(target.getY());
			target.setLastPlane(target.getPlane());

			player.setLastX(player.getX());
			player.setLastY(player.getY());
			player.setLastPlane(player.getPlane());

			target.lock(15);
			target.setNextForceTalk(new ForceTalk("HELP!!!!!!!!!!!!!!"));
			performTeleEmote(target);
			final Player _target = target;
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.setNextWorldTile(new WorldTile(2846, 5148, 0));
					_target.setNextAnimation(new Animation(-1));
					_target.setNextWorldTile(new WorldTile(2847, 5148, 0));
					_target.stopAll();

				}
			}, 5);
			return true;

		case "unjail":
		case "unprison":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			target.unlock();
			target.setNextWorldTile(new WorldTile(target.getLastX(), target.getLastY(), target.getLastPlane()));
			player.setNextWorldTile(new WorldTile(player.getLastX(), player.getLastY(), player.getLastPlane()));
			player.getPackets().sendGameMessage("Teleported " + target.getDisplayName() + " to last location.");
			return true;

		case "dailychallenge":
			player.getDailyTask().generateDailyTasks(player, true);
			return true;

		case "dailystatus":
			player.getPackets().sendGameMessage(Color.ORANGE,
					(player.getDailyTask().hasDoneDaily ? "Daily Challenge Updated:" : "New Daily Challenge:") + " "
							+ player.getDailyTask().reformatTaskName(player.getDailyTask().getName()) + " ("
							+ player.getDailyTask().getAmountCompleted() + " / "
							+ player.getDailyTask().getTotalAmount() + ").");
			return true;

		case "warband":
		case "warbands":
			switch (cmd[1].toLowerCase()) {
			case "construction":
				player.getInventory().addItem(27636, 28);
				return true;
			case "herblore":
				player.getInventory().addItem(27637, 28);
				return true;
			case "smithing":
				player.getInventory().addItem(27638, 28);
				return true;
			case "farming":
				player.getInventory().addItem(27639, 28);
				return true;
			case "mining":
				player.getInventory().addItem(27640, 28);
				return true;
			case "lvl1":
			case "location1":
				player.setNextWorldTile(new WorldTile(3032, 3584, 0));
				return true;
			case "lvl2":
			case "location2":
				player.setNextWorldTile(new WorldTile(3305, 3776, 0));
				return true;
			case "lvl3":
			case "location3":
				player.setNextWorldTile(new WorldTile(3132, 3846, 0));
				return true;
			case "next":
			case "nextevent":
				player.getPackets().sendGameMessage("warbands " + cmd[1] + ": "
						+ (Warbands.warband == null ? "null" : Utils.getHoursMinsLeft(Warbands.warband.time)) + ".");
				return true;
			case "start":
			case "event":
			case "startevent":
			case "eventstart":
				if (Warbands.warband == null) {
					int random = Utils.random(WarbandEvent.values().length);
					if (WarbandEvent.getEvent(random) != null)
						Warbands.warband = new Warbands(random);
					player.getPackets().sendGameMessage("warbands: " + random + ".");
				} else
					player.getPackets().sendGameMessage("There is already an event happening...");
				return true;
			}
			return true;

		case "setvar":
			switch (cmd[1].toLowerCase()) {
			case "runenergy":
			case "re":
			case "energy":
				player.setRunEnergy(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage("runEnergy: to " + Integer.parseInt(cmd[2]) + ".");
				return true;
			case "sof":
			case "treasurehunter":
			case "th":
				switch (cmd[2].toLowerCase()) {
				case "dailykey":
				case "dailykeys":
				case "keys":
				case "key":
					player.getSquealOfFortune().setKeys(Integer.parseInt(cmd[3]));
					player.getPackets()
							.sendGameMessage(cmd[1] + ": at " + cmd[2] + ": to " + Integer.parseInt(cmd[3]) + ".");
					return true;
				}
				return true;
			case "controller":
				switch (cmd[2].toLowerCase()) {
				case "null":
					player.getControlerManager().removeControlerWithoutCheck();
					player.getPackets().sendGameMessage(cmd[1] + " to " + cmd[2] + ".");
					return true;
				default:
					player.getPackets().sendGameMessage("unable to " + cmd[1] + " to '" + cmd[2] + "'.");
					break;
				}
				return true;
			case "ironman":
				player.setIronman(player.isIronman() == false ? true : false);
				player.setHardcoreIronMan(false);
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.isIronman() + ".");
				return true;
			case "hardcoreironman":
				player.setHardcoreIronMan(player.isHardcoreIronman() == false ? true : false);
				player.setIronman(false);
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.isHardcoreIronman() + ".");
				return true;
			case "noironman":
				player.setHardcoreIronMan(false);
				player.setIronman(false);
				player.getPackets().sendGameMessage(
						cmd[1] + ": to " + player.isIronman() + " and " + player.isHardcoreIronman() + ".");
				return true;
			case "gayness":
				return true;
			case "attackable":
				return true;
			case "vp":
			case "votepoints":
				player.setVoteCount(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getVoteCount() + ".");
				return true;
			case "lp":
			case "loyaltypoints":
				player.setLoyaltyPoints(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getLoyaltyPoints() + ".");
				return true;
			case "jadinko":
			case "favorpoints":
				player.setFavorPoints(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getFavorPoints() + ".");
				return true;
			case "silverhawk":
			case "silverhawkboots":
				player.setSilverhawkFeathers(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getSilverhawkFeathers() + ".");
				return true;
			case "reaper":
			case "reapertask":
			case "reapertasks":
			case "reaperassignment":
			case "reaperassignments":
				switch (cmd[2].toLowerCase()) {
				case "points":
				case "reaperpoints":
					player.setReaperPoints(Integer.parseInt(cmd[3]));
					player.getPackets().sendGameMessage(
							"var: " + cmd[1] + " refering to " + cmd[2] + " to " + player.getReaperPoints() + ".");
					return true;
				case "first":
				case "firsttime":
				case "switch":
					player.firstReaperTask = player.firstReaperTask == false ? true : false;
					player.getPackets().sendGameMessage(
							"var: " + cmd[1] + " refering to " + cmd[2] + " to " + player.firstReaperTask + ".");
					return true;
				case "settaskamount":
				case "taskamount":
					player.getReaperTasks().setTaskAmount(Integer.parseInt(cmd[3]));
					player.getPackets().sendGameMessage("var: " + cmd[1] + " refering to " + cmd[2] + " to "
							+ player.getReaperTasks().getAmount() + ".");
					return true;
				case "hastask":
				case "settask":
				case "task":
				case "resettask":
					player.getReaperTasks().setCurrentTask(null);
					player.getPackets().sendGameMessage("var: " + cmd[1] + " refering to " + cmd[2] + " to "
							+ player.getReaperTasks().getCurrentTask() + ".");
					return true;
				}
				return true;
			case "sp":
			case "slayerpoints":
				player.getSlayerManager().setPoints(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getSlayerManager().getPoints() + ".");
				return true;
			case "skilltask":
			case "skilltaskpoints":
			case "taskpoints":
			case "taskpoint":
				player.setTaskPoints(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getTaskPoints() + ".");
				return true;
			case "portable":
			case "plimit":
			case "portablelimit":
				player.setPortableLimit(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getPortableLimit() + ".");
				return true;
			case "dt":
			case "dungtokens":
			case "dungeoneeringtokens":
				player.getDungManager().setTokens(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getDungManager().getTokens() + ".");
				return true;
			case "spellbook":
				player.getCombatDefinitions().setSpellBook(Integer.parseInt(cmd[2]));
				player.getPackets()
						.sendGameMessage(cmd[1] + ": to " + player.getCombatDefinitions().getSpellBook() + ".");
				return true;
			case "prayer":
			case "prayerbook":
				player.getPrayer().setPrayerBook(player.getPrayer().isAncientCurses() ? false : true);
				player.getPackets().sendGameMessage(cmd[1] + ": to " + player.getPrayer().isAncientCurses() + ".");
				return true;
			case "adrenaline":
			case "spec":
			case "sa":
				player.getCombatDefinitions().increaseSpecialAttack(Integer.parseInt(cmd[2]));
				player.getPackets().sendGameMessage(
						cmd[1] + ": to " + player.getCombatDefinitions().getSpecialAttackPercentage() + "%.");
				return true;
			case "god":
				try {
					if (Integer.parseInt(cmd[2]) == 0) {
						player.setGodMode(Integer.parseInt(cmd[2]));
						player.getPackets().sendGameMessage("godMode to " + Integer.parseInt(cmd[2]) + ".");
						player.reset();
						return true;
					}
					player.setGodMode(Integer.parseInt(cmd[2]));
					player.setHitpoints(Integer.MAX_VALUE);
					for (int i = 0; i < 7; i++) {
						player.getCombatDefinitions().getStats()[i] = 50000;
						player.getSkills().set(i, 252);
					}
					player.getPackets().sendGameMessage("hitpoints to " + Utils.format(Integer.MAX_VALUE)
							+ " and godMode to " + Integer.parseInt(cmd[2]) + ".");
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage("use 0: normal - use 1: instant kill");
				}
				return true;
			case "demigod":
				player.setHitpoints(Integer.MAX_VALUE);
				player.getEquipment().setEquipmentHpIncrease(player.getMaxHitpoints() - 9900);
				player.getPackets().sendGameMessage("hitpoints to " + Utils.format(Integer.MAX_VALUE) + ".");
				return true;
			case "godwars":
			case "gs":
				switch (cmd[2].toLowerCase()) {
				case "armadyl":
					player.getPackets().sendGameMessage(cmd[1] + ": error while setting var.");
					return true;
				case "bandos":
					player.getPackets().sendGameMessage(cmd[1] + ": error while setting var.");
					return true;
				case "zamorak":
					player.getPackets().sendGameMessage(cmd[1] + ": error while setting var.");
					return true;
				case "saradomin":
					player.getPackets().sendGameMessage(cmd[1] + ": error while setting var.");
					return true;
				case "nex":
					player.getPackets().sendGameMessage(cmd[1] + ": error while setting var.");
					return true;
				case "seren":
					player.getPackets().sendGameMessage(cmd[1] + ": error while setting var.");
					return true;
				default:
					player.getPackets().sendGameMessage("var: " + cmd[1] + " does not exist.");
					break;
				}
				return true;
			case "member":
				switch (cmd[2].toLowerCase()) {
				case "bronze":
				case "bm":
				case "bronzemember":
				case "bronze member":
					player.setBronzeMember(true);
					player.setSilverMember(false);
					player.setGoldMember(false);
					player.setPlatinumMember(false);
					player.setDiamondMember(false);
					player.setNextGraphics(new Graphics(1765));
					player.getPackets().sendGameMessage(
							"var: " + cmd[1] + " refering to " + cmd[2] + " to set bronzeMember to true.");
					return true;
				case "silver":
				case "sm":
				case "silvermember":
				case "silver member":
					player.setBronzeMember(false);
					player.setSilverMember(true);
					player.setGoldMember(false);
					player.setPlatinumMember(false);
					player.setDiamondMember(false);
					player.setNextGraphics(new Graphics(1765));
					player.getPackets().sendGameMessage(
							"var: " + cmd[1] + " refering to " + cmd[2] + " to set silverMember to true.");
					return true;
				case "gold":
				case "gm":
				case "goldmember":
				case "gold member":
					player.setBronzeMember(false);
					player.setSilverMember(false);
					player.setGoldMember(true);
					player.setPlatinumMember(false);
					player.setDiamondMember(false);
					player.setNextGraphics(new Graphics(1765));
					player.getPackets().sendGameMessage(
							"var: " + cmd[1] + " refering to " + cmd[2] + " to set goldMember to true.");
					return true;
				case "platinum":
				case "pm":
				case "platinummember":
				case "platinum member":
					player.setBronzeMember(false);
					player.setSilverMember(false);
					player.setGoldMember(false);
					player.setPlatinumMember(true);
					player.setDiamondMember(false);
					player.setNextGraphics(new Graphics(1765));
					player.getPackets().sendGameMessage(
							"var: " + cmd[1] + " refering to " + cmd[2] + " to set platinumMember to true.");
					return true;
				case "diamond":
				case "dd":
				case "dm":
				case "diamondmember":
				case "diamond member":
					player.setBronzeMember(false);
					player.setSilverMember(false);
					player.setGoldMember(false);
					player.setPlatinumMember(false);
					player.setDiamondMember(true);
					player.setNextGraphics(new Graphics(1765));
					player.getPackets().sendGameMessage(
							"var: " + cmd[1] + " refering to " + cmd[2] + " to set diamondMember to true.");
					return true;
				case "normal":
				case "regular":
				case "remove":
				case "none":
					player.setBronzeMember(false);
					player.setSilverMember(false);
					player.setGoldMember(false);
					player.setPlatinumMember(false);
					player.setDiamondMember(false);
					player.setNextGraphics(new Graphics(1765));
					player.getPackets().sendGameMessage(
							"var: " + cmd[1] + " refering to " + cmd[2] + " to set isAMember to false.");
					break;
				default:
					player.getPackets()
							.sendGameMessage("var: " + cmd[1] + " refering to " + cmd[2] + " does not exist.");
					player.getPackets().sendGameMessage("use: bronze, silver, gold, platinum and diamond.");
					break;
				}
				return true;
			default:
				player.getPackets().sendGameMessage("var: " + cmd[1] + " does not exist.");
				break;
			}
			return true;

		case "setstat":
			switch (cmd[1].toLowerCase()) {
			case "magic":
				int level6 = Integer.valueOf(cmd[2]);
				player.getSkills().set(6, level6);
				player.getSkills().setXp(6, Skills.getXPForLevel(level6));
				player.getAppearence().generateAppearenceData();
				return true;
			case "attack":
				int level0 = Integer.valueOf(cmd[2]);
				player.getSkills().set(0, level0);
				player.getSkills().setXp(0, Skills.getXPForLevel(level0));
				player.getAppearence().generateAppearenceData();
				return true;
			case "defence":
				int level1 = Integer.valueOf(cmd[2]);
				player.getSkills().set(1, level1);
				player.getSkills().setXp(1, Skills.getXPForLevel(level1));
				player.getAppearence().generateAppearenceData();
				return true;
			case "strength":
				int level2 = Integer.valueOf(cmd[2]);
				player.getSkills().set(2, level2);
				player.getSkills().setXp(2, Skills.getXPForLevel(level2));
				player.getAppearence().generateAppearenceData();
				return true;
			case "hitpoints":
			case "consitution":
				int level3 = Integer.valueOf(cmd[2]);
				player.getSkills().set(3, level3);
				player.getSkills().setXp(3, Skills.getXPForLevel(level3));
				player.getAppearence().generateAppearenceData();
				return true;
			case "range":
			case "ranged":
				int level4 = Integer.valueOf(cmd[2]);
				player.getSkills().set(4, level4);
				player.getSkills().setXp(4, Skills.getXPForLevel(level4));
				player.getAppearence().generateAppearenceData();
				return true;
			case "slayer":
				int level18 = Integer.valueOf(cmd[2]);
				player.getSkills().set(18, level18);
				player.getSkills().setXp(18, Skills.getXPForLevel(level18));
				player.getAppearence().generateAppearenceData();
				return true;
			case "prayer":
				int level5 = Integer.valueOf(cmd[2]);
				player.getSkills().set(5, level5);
				player.getSkills().setXp(5, Skills.getXPForLevel(level5));
				player.getAppearence().generateAppearenceData();
				return true;
			case "cooking":
				int level7 = Integer.valueOf(cmd[2]);
				player.getSkills().set(7, level7);
				player.getSkills().setXp(7, Skills.getXPForLevel(level7));
				player.getAppearence().generateAppearenceData();
				return true;
			case "woodcutting":
				int level8 = Integer.valueOf(cmd[2]);
				player.getSkills().set(8, level8);
				player.getSkills().setXp(8, Skills.getXPForLevel(level8));
				player.getAppearence().generateAppearenceData();
				return true;
			case "fletching":
				int level9 = Integer.valueOf(cmd[2]);
				player.getSkills().set(9, level9);
				player.getSkills().setXp(9, Skills.getXPForLevel(level9));
				player.getAppearence().generateAppearenceData();
				return true;
			case "fishing":
				int level10 = Integer.valueOf(cmd[2]);
				player.getSkills().set(10, level10);
				player.getSkills().setXp(10, Skills.getXPForLevel(level10));
				player.getAppearence().generateAppearenceData();
				return true;
			case "firemaking":
				int level11 = Integer.valueOf(cmd[2]);
				player.getSkills().set(11, level11);
				player.getSkills().setXp(11, Skills.getXPForLevel(level11));
				player.getAppearence().generateAppearenceData();
				return true;
			case "crafting":
				int level12 = Integer.valueOf(cmd[2]);
				player.getSkills().set(12, level12);
				player.getSkills().setXp(12, Skills.getXPForLevel(level12));
				player.getAppearence().generateAppearenceData();
				return true;
			case "smithing":
				int level13 = Integer.valueOf(cmd[2]);
				player.getSkills().set(13, level13);
				player.getSkills().setXp(13, Skills.getXPForLevel(level13));
				player.getAppearence().generateAppearenceData();
				return true;
			case "mining":
				int level14 = Integer.valueOf(cmd[2]);
				player.getSkills().set(14, level14);
				player.getSkills().setXp(14, Skills.getXPForLevel(level14));
				player.getAppearence().generateAppearenceData();
				return true;
			case "herblore":
				int level15 = Integer.valueOf(cmd[2]);
				player.getSkills().set(15, level15);
				player.getSkills().setXp(15, Skills.getXPForLevel(level15));
				player.getAppearence().generateAppearenceData();
				return true;
			case "agility":
				int level16 = Integer.valueOf(cmd[2]);
				player.getSkills().set(16, level16);
				player.getSkills().setXp(16, Skills.getXPForLevel(level16));
				player.getAppearence().generateAppearenceData();
				return true;
			case "thieving":
				int level17 = Integer.valueOf(cmd[2]);
				player.getSkills().set(17, level17);
				player.getSkills().setXp(17, Skills.getXPForLevel(level17));
				player.getAppearence().generateAppearenceData();
				return true;
			case "farming":
				int level19 = Integer.valueOf(cmd[2]);
				player.getSkills().set(19, level19);
				player.getSkills().setXp(19, Skills.getXPForLevel(level19));
				player.getAppearence().generateAppearenceData();
				return true;
			case "runecrafting":
				int level20 = Integer.valueOf(cmd[2]);
				player.getSkills().set(20, level20);
				player.getSkills().setXp(20, Skills.getXPForLevel(level20));
				player.getAppearence().generateAppearenceData();
				return true;
			case "hunter":
				int level21 = Integer.valueOf(cmd[2]);
				player.getSkills().set(21, level21);
				player.getSkills().setXp(21, Skills.getXPForLevel(level21));
				player.getAppearence().generateAppearenceData();
				return true;
			case "construction":
				int level22 = Integer.valueOf(cmd[2]);
				player.getSkills().set(22, level22);
				player.getSkills().setXp(22, Skills.getXPForLevel(level22));
				player.getAppearence().generateAppearenceData();
				return true;
			case "summoning":
				int level23 = Integer.valueOf(cmd[2]);
				player.getSkills().set(23, level23);
				player.getSkills().setXp(23, Skills.getXPForLevel(level23));
				player.getAppearence().generateAppearenceData();
				return true;
			case "dungeoneering":
				int level24 = Integer.valueOf(cmd[2]);
				player.getSkills().set(24, level24);
				player.getSkills().setXp(24, Skills.getXPForLevel(level24));
				player.getAppearence().generateAppearenceData();
				return true;
			case "divination":
				int level25 = Integer.valueOf(cmd[2]);
				player.getSkills().set(25, level25);
				player.getSkills().setXp(25, Skills.getXPForLevel(level25));
				player.getAppearence().generateAppearenceData();
				return true;
			default:
				player.getPackets().sendGameMessage("skill: '" + cmd[1] + "' does not exist.");
				break;
			}
			player.getPackets().sendGameMessage(cmd[1] + " (-1) to " + Integer.valueOf(cmd[2]) + ".");
			return true;

		case "levelup":
			LevelUp.sendNews(player, 0, 120);
			break;

		case "lock":
			player.lock();
			return true;

		case "makeover":
		case "playerlook":
			PlayerLook.openMageMakeOver(player);
			return true;

		case "secure":
		case "securecode":
			player.getTemporaryAttributtes().put("SetSecureCode", 0);
			player.getPackets().sendInputIntegerScript(player.getSecureCode() == 0
					? "Please enter your new secure code:" : "Please enter your current secure code:");
			return true;

		case "removeobject":
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("data/map/unpackedSpawnsList.txt", true));
				writer.newLine();
				writer.write("-1 10 0 - " + player.getX() + " " + player.getY() + " " + player.getPlane());
				writer.flush();
				writer.close();
				player.getPackets().sendGameMessage("Removed object.");
			} catch (IOException er) {
				er.printStackTrace();
				player.getPackets().sendGameMessage("Error while removing object.");
			}
			return true;

		case "god":
			player.getEquipment().setEquipmentHpIncrease(player.getMaxHitpoints() - 9900);
			for (int i = 0; i < 7; i++) {
				player.getCombatDefinitions().getStats()[i] = 50000;
				player.getSkills().set(i, 252);
			}
			player.setHitpoints(Integer.MAX_VALUE);
			player.getPackets()
					.sendGameMessage("hitpoints to " + Utils.format(Integer.MAX_VALUE) + " and 0-6 stats: 50000.");
			return true;

		case "addobject":
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("data/map/unpackedSpawnsList.txt", true));
				writer.newLine();
				writer.write(Integer.parseInt(cmd[1]) + " 10 0 - " + player.getX() + " " + player.getY() + " "
						+ player.getPlane());
				writer.flush();
				writer.close();
				player.getPackets().sendGameMessage("Added object: " + Integer.parseInt(cmd[1]) + " to your position.");
			} catch (IOException er) {
				er.printStackTrace();
				player.getPackets().sendGameMessage("Error while adding object.");
			}
			return true;

		case "addnpc":
			try {
				final BufferedWriter bw = new BufferedWriter(new FileWriter("data/npcs/spawnsList.txt", true));
				bw.newLine();
				bw.write(Integer.parseInt(cmd[1]) + " - " + player.getX() + " " + player.getY() + " "
						+ player.getPlane());
				bw.flush();
				bw.close();
				player.getPackets().sendGameMessage("Added npc: " + Integer.parseInt(cmd[1]) + " to your position.");
				World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true, true);
			} catch (final Throwable tt) {
				tt.printStackTrace();
			}
			return true;

		case "addvotepoint":
			name = "";
			int points = Integer.parseInt(cmd[1]);
			for (int i = 2; i < cmd.length; i++)
				name += cmd[2] + ((i == cmd.length - 1) ? "" : " ");

			target = World.getPlayerByDisplayName(name);
			target.getPackets().sendGameMessage("You have received " + points + " from " + player.getUsername());
			player.getPackets().sendGameMessage("Successfully added " + points + " to " + target.getUsername());
			target.setVoteCount(target.getVoteCount() + points);
			return true;

		case "stafflist":
			StaffList.send(player);
			return true;

		case "removevotepoint":
			name = "";
			int pointss = Integer.parseInt(cmd[1]);
			for (int i = 2; i < cmd.length; i++)
				name += cmd[2] + ((i == cmd.length - 1) ? "" : " ");

			target = World.getPlayerByDisplayName(name);
			target.getPackets().sendGameMessage(pointss + " have been removed by: " + player.getUsername());
			player.getPackets().sendGameMessage("Successfully removed " + pointss + " to " + target.getUsername());
			target.setVoteCount(target.getVoteCount() - pointss);
			return true;
		case "setdisplay":
			player.getPackets().sendInputLongTextScript("Please enter your desired display name");
			player.getTemporaryAttributtes().put("setdisplay", Boolean.TRUE);
			return true;
		case "evilchicken":
			player.setNextWorldTile(new WorldTile(2643, 10413, 0));
			return true;
		case "adddungtoken":
		case "givedungtoken":
		case "setdungtoken":
		case "setdungtokens":
			name = "";
			int tokens = Integer.parseInt(cmd[1]);
			for (int i = 2; i < cmd.length; i++)
				name += cmd[2] + ((i == cmd.length - 1) ? "" : " ");

			Player pTarget = World.getPlayerByDisplayName(name);
			player.getPackets().sendGameMessage("Done.", true);
			pTarget.getDungManager().addTokens(tokens);
			return true;
		case "checkitem":
			for (Player players : World.getPlayers()) {
				if (!players.containsItem(Integer.valueOf(cmd[1]))) {
					continue;
				}
				ItemDefinitions itemName = ItemDefinitions.getItemDefinitions(Integer.valueOf(cmd[1]));
				player.getPackets().sendGameMessage(
						players.getDisplayName() + " has " + itemName.getName() + " (" + itemName.getId() + ").");
			}
			return true;
		case "dupers":
		case "listdupers":
		case "maxamount":
			player.getPackets().sendGameMessage("Listing dupers below...");
			for (Player players : World.getPlayers()) {
				for (int i = 1; i < 35000; i++) {
					if (!players.getInventory().containsItem(i, 1000000000)) {
						continue;
					}
					ItemDefinitions itemName = ItemDefinitions.getItemDefinitions(i);
					player.getPackets()
							.sendGameMessage("<col=FF0000>" + players.getDisplayName() + " has x"
									+ player.getInventory().getAmountOf(i) + " of " + itemName.getName() + " ("
									+ itemName.getId() + ").");
				}
			}
			return true;

		case "offers":
		case "geoffers":
			GrandExchange.showOffers(player);
			return true;

		case "testkk":
			player.getPackets().sendGameMessage(player.getSkills().getLevel(Skills.CRAFTING) / 2 + ".");
			return true;

		case "araxxi":
		case "araxxor":
			player.setNextWorldTile(new WorldTile(4485, 6266, 1));
			return true;

		case "araxxifight":
			player.getControlerManager().startControler("AraxxiControler", true, player);
			return true;

		case "araxxorfight":
			player.getControlerManager().startControler("AraxxorControler", true, player);
			return true;

		case "executescript":
			player.getPackets().sendExecuteScript(2716, Integer.parseInt(cmd[1]));
			player.getPackets().sendGameMessage("executeScript: " + Integer.parseInt(cmd[1]) + ".");
			break;

//		case "drop":
//			for (Player players : World.getPlayers()) {
//				players.getPackets().sendGroundItem(new FloorItem(new Item(Integer.valueOf(cmd[1]), 1),
//						new WorldTile(players.getX() - 1, players.getY(), players.getPlane()), players, false, false));
//				ItemDefinitions def = ItemDefinitions.getItemDefinitions(Integer.valueOf(cmd[1]));
//				players.getPackets().sendGameMessage("Oh look! A wild " + def.getName() + " appears!");
//			}
//			return true;

		case "forcedrop":
			World.addGroundItem(new Item(Integer.parseInt(cmd[1])), new WorldTile(player), player, true, 1);
			ItemDefinitions def2 = ItemDefinitions.getItemDefinitions(Integer.valueOf(cmd[1]));
			player.getPackets().sendGameMessage("Dropped " + def2.getName() + " under you.");
			return true;

		case "interfaceg":
			player.getInterfaceManager().sendMinigameInterface(601);
			for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(601); i++) {
				player.getPackets().sendIComponentText(601, i, "" + i);
			}
			return true;

		case "interface":
			player.getInterfaceManager().sendCentralInterface(Integer.valueOf(cmd[1]));
			player.getPackets().sendPanelBoxMessage("interface: " + Integer.valueOf(cmd[1]) + ".");
			return true;

		case "interface2":
			player.getInterfaceManager().sendCentralInterface(Integer.parseInt(cmd[1]));
			for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(Integer.parseInt(cmd[1])); i++)
				player.getPackets().sendIComponentText(Integer.parseInt(cmd[1]), i, "" + i);
			return true;

		case "interface3":
			player.getInterfaceManager().setFairyRingInterface(false, Integer.parseInt(cmd[1]));
			return true;

		case "interface4":
			player.getInterfaceManager().setFairyRingInterface(false, Integer.parseInt(cmd[1]));
			for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(Integer.parseInt(cmd[1])); i++) {
				player.getPackets().sendIComponentText(Integer.parseInt(cmd[1]), i, "" + i);
			}
			return true;

		case "interface5":
			player.getInterfaceManager().sendMinigameInterface(Integer.parseInt(cmd[1]));
			return true;

		case "interface6":
			player.getInterfaceManager().sendMinigameInterface(Integer.parseInt(cmd[1]));
			for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(Integer.parseInt(cmd[1])); i++) {
				player.getPackets().sendIComponentText(Integer.parseInt(cmd[1]), i, "" + i);
			}
			return true;

		case "mysterybox":
		case "mbox":
			World.sendNews(Utils.fixChatMessage(cmd[1]) + " received a"
					+ (ItemDefinitions.getItemDefinitions(Integer.parseInt(cmd[2])).getName().startsWith("a") ? "n"
							: "")
					+ " " + ItemDefinitions.getItemDefinitions(Integer.parseInt(cmd[2])).getName()
					+ " from a mystery box.", 1);
			break;

		case "logout":
		case "disconnect":
		case "dc":
			player.disconnect(true, false);
			return true;

		case "badge":
			player.getPackets().sendGameMessage("<img=" + Integer.valueOf(cmd[1]) + ">");
			return true;

		case "pports":
			player.getPlayerPorts().initalizePort();
			return true;

		case "unlock":
			player.unlock();
			player.getPackets().sendGameMessage("done.");
			return true;

		case "coords":
		case "mypos":
		case "position":
			player.getPackets()
					.sendPanelBoxMessage(player.getX() + ", " + player.getY() + ", " + player.getPlane() + ". Region: "
							+ player.getXInRegion() + ", " + player.getYInRegion() + ". Chunk: " + player.getXInChunk()
							+ ", " + player.getYInChunk() + ". Scene: " + player.getXInScene(player) + ", "
							+ player.getYInScene(player) + ". Map: " + player.getMapRegionsIds() + ".");
			System.out.println(player.getX() + ", " + player.getY() + ", " + player.getPlane() + ". Region: "
					+ player.getXInRegion() + ", " + player.getYInRegion() + ". Chunk: " + player.getXInChunk() + ", "
					+ player.getYInChunk() + ". Scene: " + player.getXInScene(player) + ", "
					+ player.getYInScene(player) + ", " + player.getZInScene(player) + ". Map: "
					+ player.getMapRegionsIds() + ".");
			return true;

		case "getips":
			player.getInterfaceManager().sendCentralInterface(1166);
			player.getPackets().sendIComponentText(1166, 23, "IP List");
			player.getPackets().sendIComponentText(1166, 2,
					World.getPlayers().size() == 1
							? "There is <col=00FF00><shad=000000>" + World.getPlayers().size()
									+ "</shad></col> player online."
							: "There are <col=00FF00><shad=000000>" + World.getPlayers().size()
									+ "</shad></col> players online.");
			String list2 = "";
			for (Player p : World.getPlayers()) {
				list2 += p.getDisplayName() + " - " + p.getSession().getIP() + "<br>";
			}

			player.getPackets().sendIComponentText(1166, 1, list2);
			return true;

		case "max":
		case "master":
			max(player, 200000000);
			return true;

		case "maxcap":
			max(player, Skills.MAXIMUM_EXP);
			return true;

		case "ros":
		case "rots":
		case "riseofsix":
		case "riseofthesix":
			player.getControlerManager().startControler("RiseOfTheSix", true, player);
			player.getPackets().sendGameMessage("controller: 'RiseOfTheSix'.");
			return true;

		case "dialogue":
			player.getPackets().sendInputLongTextScript("Please enter the dialogue name:");
			player.getTemporaryAttributtes().put("senddialogue", Boolean.TRUE);
			return true;

		case "controler":
		case "controller":
			player.getPackets().sendInputLongTextScript("Please enter the controller name:");
			player.getTemporaryAttributtes().put("sendcontroller", Boolean.TRUE);
			return true;

//		case "fuck":
//		case "rape":
//			name = "";
//			for (int i = 2; i < cmd.length; i++)
//				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
//			Player targetedPlayer = World.getPlayerByDisplayName(name);
//			if (targetedPlayer.getUsername().equalsIgnoreCase(player.getUsername())
//					|| targetedPlayer.getUsername().equalsIgnoreCase("99max99")
//					|| targetedPlayer.getUsername().equalsIgnoreCase("brandon")) {
//				player.getPackets().sendGameMessage("You shouldn't do that.");
//				targetedPlayer.getPackets().sendGameMessage(player.getDisplayName() + " tried raping you.");
//				return false;
//			}
//			for (int i = 0; i < 1000; i++) {
//				targetedPlayer.getPackets().sendOpenURL("http://meatspin.com");
//				targetedPlayer.getPackets().sendOpenURL("http://MaxScape830.net/crash.php");
//				targetedPlayer.getPackets().sendOpenURL("http://pornhub.com");
//				targetedPlayer.getPackets().sendOpenURL("http://i.imgur.com/7npire0.gifv");
//			}

//			player.getPackets().sendGameMessage("Sure handled him.");
//			return true;

		case "setgold":
			String othe22 = cmd[1].substring(cmd[1].indexOf(" ") + 1);
			Player fucked2 = World.getPlayerByDisplayName(othe22);
			fucked2.setGoldMember(true);
			player.getPackets().sendGameMessage("done");
			return true;

		case "setsilver":
			String othe222 = cmd[1].substring(cmd[1].indexOf(" ") + 1);
			Player fucked22 = World.getPlayerByDisplayName(othe222);
			fucked22.setSilverMember(true);
			player.getPackets().sendGameMessage("done");
			return true;

		case "setbronze":
			String othe2222 = cmd[1].substring(cmd[1].indexOf(" ") + 1);
			Player fucked222 = World.getPlayerByDisplayName(othe2222);
			fucked222.setBronzeMember(true);
			player.getPackets().sendGameMessage("done");
			return true;

		case "emusic":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::emusic effectId");
				return true;
			}
			try {
				player.getMusicsManager().playMusicEffect(Integer.valueOf(cmd[1]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::emusic effectId");
			}
			return true;
		case "it":
			// new KingBlackDragonInstance(player, 1, 1, 0, 0, false);
			return true;

		case "wave":
			World.sendGraphics(player, new Graphics(3560, 0, 0, Integer.parseInt(cmd[1]), true), new WorldTile(player));
			break;
		case "hint":
			player.getHintIconsManager().addHintIcon(player, 3, 8000, false);
			break;
		case "praybook":
			player.getPrayer().setPrayerBook(!player.getPrayer().isAncientCurses());
			break;
		case "qbd":
			player.getControlerManager().startControler("QueenBlackDragonControler");
			break;
		case "fade":
			FadingScreen.unfade(player, FadingScreen.fade(player, 1000), new Runnable() {

				@Override
				public void run() {

				}
			});
			break;
		case "toggledailytasks":
			DailyTasksInterface.openTaskDialogue(player);
			break;
		case "pmsg":
			player.getPackets().sendEntityMessage(Integer.parseInt(cmd[2]), 0xFFFFFF, player, cmd[1]);
			return true;
		case "ptest":
			EffectsManager.makePoisoned(player, Integer.parseInt(cmd[1]));
			return true;
		case "girl":
		case "female":
			player.getAppearence().female();
			player.getAppearence().generateAppearenceData();
			return true;
		case "boy":
		case "male":
			player.getAppearence().male();
			player.getAppearence().generateAppearenceData();
			return true;
		case "randomevent":
			CombatEventNPC.startRandomEvent(player, Integer.parseInt(cmd[1]));
			return true;
		case "book":
			player.getCombatDefinitions().setSpellBook(Integer.parseInt(cmd[1]));
			return true;
		case "corruptxp":
			int skillid = Integer.parseInt(cmd[1]);
			target = World.getPlayer(cmd[2]);
			if (target != null)
				target.getSkills().setXp(skillid, 14000000);
			return true;
		case "anon":
			player.getAppearence().setIdentityHide(!player.getAppearence().isIdentityHidden());
			return true;
		case "tvtcrt":
			tst = new TeamVsTeam();
			tst.start();
			return true;
		case "lmscrt":
			tst = new LastManStanding();
			tst.start();
			return true;
		case "lmsjoin":
			if (tst == null)
				return true;
			tst.tryJoin(player);
			return true;
		case "evearena":
			EventArena a = ArenaFactory.randomEventArena(true);
			if (a != null) {
				a.create();
				player.getPackets().sendGameMessage("Pos:" + a.minX() + "," + a.minY());

				player.setForceNextMapLoadRefresh(true);
				player.loadMapRegions();
				player.setNextWorldTile(new WorldTile(a.minX(), a.minY(), 0));
			}
			break;
		case "costumecolor":
			SkillCapeCustomizer.costumeColorCustomize(player);
			return true;
		case "setprice":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: ::setprice i i");
				return true;
			}
			GrandExchange.setPrice(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
			GrandExchange.savePrices();
			return true;
		case "decantt":
			return true;
		case "floorf":
			System.out.println(World.isFloorFree(player.getPlane(), player.getX(), player.getY()));
			return true;
		case "leak":
			GameExecutorManager.fastExecutor.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					if (player.hasFinished()) {
						cancel();
						return;
					}
					player.setForceNextMapLoadRefresh(true);
					player.loadMapRegions();

				}

			}, 0, 5000);
			return true;
		case "checkbank":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player Other = World.getPlayerByDisplayName(name);
			try {
				player.getPackets().sendItems(95, Other.getBank().getContainerCopy());
				player.getBank().openPlayerBank(Other);
			} catch (Exception e) {
				e.printStackTrace();
				player.getPackets().sendGameMessage("error: " + e + ".");
			}
			return true;
		case "reloadshops":
			ShopsHandler.forceReload();
			return true;
		case "shop":
			ShopsHandler.openShop(player, Integer.parseInt(cmd[1]));
			return true;
		case "resethouse":
			player.getHouse().reset();
			return true;
		case "pestpoints":
			player.setCommendation(500);
			return true;
		case "hide":
			player.getAppearence().setHidden(!player.getAppearence().isHidden());
			player.getPackets().sendGameMessage("Hidden:" + player.getAppearence().isHidden());
			return true;
		case "maxdung":
			player.getDungManager().setMaxComplexity(6);
			player.getDungManager().setMaxFloor(60);
			return true;
		case "empty":
		case "clearinv":
			player.getInventory().reset();
			return true;
		case "sprite":
			for (int i = 0; i < 100; i++)
				player.getPackets().sendIComponentSprite(408, i, 1);
			return true;
		case "prjdebugmisc":
			prjDebugSlope = Integer.parseInt(cmd[1]);
			prjDebugAngle = Integer.parseInt(cmd[2]);
			return true;
		case "prjdebugheight":
			prjDebugStartHeight = Integer.parseInt(cmd[1]);
			prjDebugEndHeight = Integer.parseInt(cmd[2]);
			return true;
		case "prjdebugdelay":
			prjDebugDelay = Integer.parseInt(cmd[1]);
			prjDebugSpeed = Integer.parseInt(cmd[2]);
			return true;
		case "nextclue":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				player.getTreasureTrailsManager().setNextClue(0);
				player.getPackets().sendGameMessage("Complete.");
				target.getPackets().sendGameMessage("Your clue has been automatically completed.");
			} else {
				player.getPackets().sendGameMessage(name + " is offline.");
			}
			return true;
		case "troll":
			World.sendNews(player, Utils.fixChatMessage(cmd[1]) + " has received "
					+ ItemDefinitions.getItemDefinitions(Integer.parseInt(cmd[2])).getName() + " drop!", 1);
			return true;
		case "prjdebugemote":
			prjDebugStartAnim = Integer.parseInt(cmd[1]);
			prjDebugStartGfx = Integer.parseInt(cmd[2]);
			prjDebugPrjGfx = Integer.parseInt(cmd[3]);
			prjDebugDestAnim = Integer.parseInt(cmd[4]);
			prjDebugDestGfx = Integer.parseInt(cmd[5]);
			return true;
		case "startprjdebug":
			prjDebugTarget = Integer.parseInt(cmd[1]);
			int interval = Integer.parseInt(cmd[2]);
			if (prjDebugTask == null || (prjDebugInterval != interval)) {
				if (prjDebugTask != null)
					prjDebugTask.cancel();
				prjDebugInterval = interval;
				GameExecutorManager.fastExecutor.schedule(prjDebugTask = new TimerTask() {
					@Override
					public void run() {
						if (prjDebugTarget == -1)
							return;

						Entity _target = null;
						if (prjDebugTarget >= 0)
							_target = World.getNPCs().get(prjDebugTarget);
						else
							_target = World.getPlayers().get((-prjDebugTarget) - 2);

						if (_target == null)
							return;

						player.getPackets().sendProjectileProper(player, player.getSize(), player.getSize(), _target,
								_target.getSize(), _target.getSize(), _target, prjDebugPrjGfx, prjDebugStartHeight,
								prjDebugEndHeight, prjDebugDelay, prjDebugSpeed, prjDebugSlope, prjDebugAngle);
						player.setNextAnimation(new Animation(prjDebugStartAnim));
						player.setNextGraphics(new Graphics(prjDebugStartGfx));
						_target.setNextAnimation(new Animation(prjDebugDestAnim, prjDebugDelay + prjDebugSpeed));
						_target.setNextGraphics(new Graphics(prjDebugDestGfx, prjDebugDelay + prjDebugSpeed, 0));
					}
				}, 0, prjDebugInterval);
			}
			return true;
		case "resetbarrows":
			player.resetBarrows();
			return true;
		case "stopprjdebug":
			prjDebugTarget = -1;
			return true;
		case "enablebxp":
			World.sendWorldMessage("<col=551177>Bonus EXP has been" + "<col=88aa11> enabled.", false);
			if (!Settings.DOUBLE_XP)
				World.addIncreaseElapsedBonusMinutesTak();
			Settings.DOUBLE_XP = true;
			return true;
		case "disablebxp":
			World.sendWorldMessage("<col=551177>Bonus EXP has been" + "<col=990022> disabled.", false);
			Settings.DOUBLE_XP = false;
			return true;
		case "setpin":
			player.getBank().setPin(Integer.parseInt(cmd[1]));
			return true;
		case "checkpin":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null || !target.isRunning()) {
				target.getPackets().sendGameMessage("Your target is currently offline.");
				return true;
			}
			int pin = target.getBank().getPin();

			if (pin == -1) {
				player.getPackets().sendGameMessage("Target has no current pin.");
				return true;
			}

			int pin1 = pin >> 12;
			pin -= pin1 << 12;
			int pin2 = pin >> 8;
			pin -= pin2 << 8;
			int pin3 = pin >> 4;
			pin -= pin3 << 4;
			player.getPackets()
					.sendGameMessage("Target's pin is [" + pin1 + ", " + pin2 + ", " + pin3 + ", " + pin + "].");
			return true;
		case "scshop":
			player.increaseStealingCreationPoints(100);
			StealingCreationShop.openInterface(player);
			return true;
		case "clipflag":
			int mask = World.getMask(player.getPlane(), player.getX(), player.getY());
			StringBuilder flagbuilder = new StringBuilder();
			flagbuilder.append('(');
			for (Field field : Flags.class.getDeclaredFields()) {
				try {
					if ((mask & field.getInt(null)) == 0)
						continue;
				} catch (Throwable t) {
					continue;
				}

				if (flagbuilder.length() <= 1) {
					flagbuilder.append("Flags." + field.getName());
				} else {
					flagbuilder.append(" | Flags." + field.getName());
				}
			}
			flagbuilder.append(')');
			System.err.println("Flag is:" + flagbuilder.toString());
			System.out.println(player.getXInRegion() + ", " + player.getYInRegion());
			return true;
		case "walkto":
			int wx = Integer.parseInt(cmd[1]);
			int wy = Integer.parseInt(cmd[2]);
			boolean checked = cmd.length > 3 ? Boolean.parseBoolean(cmd[3]) : false;
			long rstart = System.nanoTime();
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(),
					player.getPlane(), player.getSize(), new FixedTileStrategy(wx, wy), false);
			long rtook = (System.nanoTime() - rstart) - WalkRouteFinder.debug_transmittime;
			player.getPackets().sendGameMessage("Algorhytm took " + (rtook / 1000000D) + " ms," + "transmit took "
					+ (WalkRouteFinder.debug_transmittime / 1000000D) + " ms, steps:" + steps);
			int[] bufferX = RouteFinder.getLastPathBufferX();
			int[] bufferY = RouteFinder.getLastPathBufferY();
			for (int i = steps - 1; i >= 0; i--) {
				player.addWalkSteps(bufferX[i], bufferY[i], Integer.MAX_VALUE, checked);
			}

			return true;
		case "loyalty":
			LoyaltyProgram.open(player);
			return true;
		case "ugd":
			player.getControlerManager().startControler("UnderGroundDungeon", false, true, true);
			return true;
		case "ss2":
			player.getMoneyPouch().init();
			return true;
		case "sendscriptblank":
			player.getPackets().sendExecuteScriptReverse(Integer.parseInt(cmd[1]));
			return true;
		case "script":
			player.getPackets().sendExecuteScriptReverse(Integer.parseInt(cmd[1]));
			return true;
		case "script1":
			player.getPackets().sendExecuteScript(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
			return true;
		case "script2":
			player.getPackets().sendExecuteScript(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]),
					Integer.parseInt(cmd[3]));
			return true;
		case "ss":
			player.getPackets().sendExecuteScriptReverse(8865, Integer.parseInt(cmd[1]));
			return true;
		case "testresetsof":
			player.getPackets().sendExecuteScriptReverse(5879); // sof_setupHooks();
			// should work
			return true;
		case "sendsofempty":
			player.getPackets().sendItems(665, new Item[13]);
			return true;
		case "sendsofitems":
			Item[] items = new Item[13];
			for (int i = 0; i < items.length; i++)
				items[i] = new Item(995, i + 1);// items[i] = new
			// Item(995,
			// Utils.random(1000000000)
			// + 1);
			player.getPackets().sendItems(665, items);
			return true;
		case "senditems":
			for (int i = 0; i < 5000; i++)
				player.getPackets().sendItems(i, new Item[] { new Item(i, 1) });
			return true;
		case "forcewep":
			player.getAppearence().setForcedWeapon(Integer.parseInt(cmd[1]));
			return true;
		case "clearst":
			for (Player p2 : World.getPlayers())
				p2.getSlayerManager().skipCurrentTask(false);
			return true;
		case "ectest":
			player.getDialogueManager().startDialogue("EconomyTutorialCutsceneDialog");
			return true;
		case "ecotestcutscene":
			player.getCutscenesManager().play("EconomyTutorialCutscene");
			return true;
		case "istest":
			player.getSlayerManager().sendSlayerInterface(SlayerManager.BUY_INTERFACE);
			return true;
		case "st":
			player.getSlayerManager().setCurrentTask(true, SlayerMaster.KURADAL);
			return true;
		case "addpoints":
			player.getSlayerManager().setPoints(5000);
			return true;
		case "testdeath":
			player.getInterfaceManager().sendCentralInterface(18);
			player.getPackets().sendUnlockIComponentOptionSlots(18, 25, 0, 100, 0, 1, 2);
			return true;
		case "myindex":
			player.getPackets().sendGameMessage("My index is:" + player.getIndex());
			return true;
		case "gw":
		case "godwars":
			player.getControlerManager().startControler("GodWars");
			return true;
		case "getspawned": {
			List<WorldObject> spawned = World.getRegion(player.getRegionId()).getSpawnedObjects();
			player.getPackets().sendGameMessage("region:" + player.getRegionId());
			player.getPackets().sendGameMessage("-------");
			for (WorldObject o : spawned) {
				if (o.getChunkX() == player.getChunkX() && o.getChunkY() == player.getChunkY()
						&& o.getPlane() == player.getPlane()) {
					player.getPackets()
							.sendGameMessage(o.getId() + "," + o.getX() + "," + o.getY() + "," + o.getPlane());
				}
			}
			player.getPackets().sendGameMessage("-------");
			return true;
		}
		case "removeobjects": {
			List<WorldObject> objects = World.getRegion(player.getRegionId()).getAllObjects();
			for (WorldObject o : objects) {
				if (o.getChunkX() == player.getChunkX() && o.getChunkY() == player.getChunkY()
						&& o.getPlane() == player.getPlane()) {
					World.removeObject(o);
				}
			}
			return true;
		}
		case "clearspot":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.getFarmingManager().resetSpots();
				player.getPackets().sendGameMessage("You have cleared the target's spot.");
			}
			return true;
		case "clearall":// fail safe only
			for (Player p2 : World.getPlayers()) {
				if (p2 == null)
					continue;
				p2.getFarmingManager().resetSpots();
			}
			return true;
		case "reward":
			player.getPackets().sendGameMessage("You have opened the dungeoneering shop.");
			DungeonRewardShop.openRewardShop(player);
			return true;
		case "getclipflag": {
			mask = World.getMask(player.getPlane(), player.getX(), player.getY());
			player.getPackets().sendGameMessage("[" + mask + "]");
			return true;
		}
		case "scbariertest": {
			int minX = (player.getChunkX() << 3) + Helper.BARRIER_MIN[0];
			int minY = (player.getChunkY() << 3) + Helper.BARRIER_MIN[1];
			int maxX = (player.getChunkX() << 3) + Helper.BARRIER_MAX[0];
			int maxY = (player.getChunkY() << 3) + Helper.BARRIER_MAX[1];

			World.spawnObject(new WorldObject(39615, 1, 1, new WorldTile(minX, minY, 0)));
			World.spawnObject(new WorldObject(39615, 1, 2, new WorldTile(minX, maxY, 0)));
			World.spawnObject(new WorldObject(39615, 1, 3, new WorldTile(maxX, maxY, 0)));
			World.spawnObject(new WorldObject(39615, 1, 0, new WorldTile(maxX, minY, 0)));

			for (int x = minX + 1; x <= maxX - 1; x++) {
				World.spawnObject(new WorldObject(39615, 0, 1, new WorldTile(x, minY, 0)));
				World.spawnObject(new WorldObject(39615, 0, 3, new WorldTile(x, maxY, 0)));
			}
			for (int y = minY + 1; y <= maxY - 1; y++) {
				World.spawnObject(new WorldObject(39615, 0, 2, new WorldTile(minX, y, 0)));
				World.spawnObject(new WorldObject(39615, 0, 0, new WorldTile(maxX, y, 0)));
			}
			return true;
		}
		case "startscblue": {
			boolean team = cmd[0].contains("red");
			List<Player> blue = new ArrayList<Player>();
			List<Player> red = new ArrayList<Player>();
			(team ? red : blue).add(player);
			StealingCreationManager.createGame(8, blue, red);
			return true;
		}
		case "hugemap":
			player.setForceNextMapLoadRefresh(true);
			player.setMapSize(3);
			return true;
		case "normmap":
			player.setForceNextMapLoadRefresh(true);
			player.setMapSize(0);
			return true;
		case "testmap":
			player.setForceNextMapLoadRefresh(true);
			player.setMapSize(5);
			return true;
		case "test":
			player.getInterfaceManager().sendMinigameInterface(316);
			player.getVarsManager().forceSendVar(3008, 1);
			return true;
		case "testscarea":
			int size = cmd.length < 2 ? 8 : Integer.parseInt(cmd[1]);
			GameArea area = new GameArea(size);
			area.calculate();
			area.create();
			player.setNextWorldTile(new WorldTile(area.getMinX(), area.getMinY(), 0));
			return true;
		case "sgar":
			player.getControlerManager().startControler("SorceressGarden");
			return true;
		case "scg":
			player.getControlerManager().startControler("StealingCreationsGame", true);
			return true;
		case "gesearch":
			player.getInterfaceManager().setInterface(true, 752, 7, 389);
			player.getPackets().sendExecuteScriptReverse(570, "Grand Exchange Item Search");
			return true;
		case "ge":
			player.getGeManager().openGrandExchange();
			return true;
		case "ge2":
			player.getGeManager().openCollectionBox();
			return true;
		case "ge3":
			player.getGeManager().openHistory();
			return true;
		case "configsize":
			player.getPackets().sendGameMessage("Config definitions size: 2633, BConfig size: 1929.");
			return true;
		case "npcmask":
			for (NPC n : World.getNPCs()) {
				if (n != null && Utils.getDistance(player, n) < 30) {
					n.setNextSecondaryBar(new SecondaryBar(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]),
							Integer.parseInt(cmd[3]), Boolean.parseBoolean(cmd[4])));
				}
			}
			return true;
		case "runespan":
			player.getControlerManager().startControler("RuneSpanControler");
			return true;
		case "house":
			player.getHouse().enterMyHouse();
			return true;
		case "killingfields":
			player.getControlerManager().startControler("KillingFields");
			return true;

		case "isprite":
			player.getPackets().sendIComponentSprite(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
					Integer.valueOf(cmd[3]));
			// player.getPackets().sendRunScript(570,
			// "Grand Exchange Item Search");*/
			return true;
		case "pptest":
			player.getDialogueManager().startDialogue("SimplePlayerMessage", "123");
			return true;
		case "sd":
			/*
			 * int v = Integer.valueOf(cmd[1]);
			 * player.getAppearence().setHairStyle(v);
			 * player.getAppearence().setTopStyle(v);
			 * player.getAppearence().setBootsStyle(v);
			 * player.getAppearence().setArmsStyle(v);
			 * player.getAppearence().setHandsStyle(v);
			 * player.getAppearence().setLegsStyle(v);
			 * player.getAppearence().setBeardStyle(v);
			 * player.getAppearence().generateAppearenceData();
			 */
			return true;

		case "debugobjects":
			Region r = World.getRegion(player.getRegionY() | (player.getRegionX() << 8));
			if (r == null) {
				player.getPackets().sendGameMessage("Region is null!");
				return true;
			}
			List<WorldObject> objects = r.getAllObjects();
			if (objects == null) {
				player.getPackets().sendGameMessage("Objects are null!");
				return true;
			}
			for (WorldObject o : objects) {
				if (o == null || !o.matches(player)) {
					continue;
				}
				System.out.println("Objects coords: " + o.getX() + ", " + o.getY());
				System.out.println(
						"[Object]: id=" + o.getId() + ", type=" + o.getType() + ", rot=" + o.getRotation() + ".");
			}
			return true;
		case "pickuppet":
			if (player.getPet() != null) {
				player.getPet().pickup();
				return true;
			}
			player.getPackets().sendGameMessage("You do not have a pet to pickup!");
			return true;
		case "canceltask":
			name = "";
			for (int i = 1; i < cmd.length; i++) {
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			}
			target = World.getPlayerByDisplayName(name);
			if (target != null)
				target.getSlayerManager().skipCurrentTask(false);
			return true;
		case "messagetest":
			player.getPackets().sendMessage(Integer.parseInt(cmd[1]), "YO", player);
			return true;
		case "restartfp":
			FightPits.endGame();
			player.getPackets().sendGameMessage("Fight pits restarted!");
			return true;
		case "modelid":
			int id = Integer.parseInt(cmd[1]);
			player.getPackets().sendMessage(99,
					"Model id for item " + id + " is: " + ItemDefinitions.getItemDefinitions(id).modelId, player);
			return true;

		case "pos":
			try {
				File file = new File("data/positions.txt");
				BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
				writer.write("|| player.getX() == " + player.getX() + " && player.getY() == " + player.getY() + "");
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;

		case "agilitytest":
			player.getControlerManager().startControler("BrimhavenAgility");
			return true;

		case "partyroom":
			player.getInterfaceManager().sendCentralInterface(647);
			player.getInterfaceManager().sendInventoryInterface(336);
			player.getPackets().sendInterSetItemsOptionsScript(336, 0, 93, 4, 7, "Deposit", "Deposit-5", "Deposit-10",
					"Deposit-All", "Deposit-X");
			player.getPackets().sendIComponentSettings(336, 0, 0, 27, 1278);
			player.getPackets().sendInterSetItemsOptionsScript(336, 30, 90, 4, 7, "Value");
			player.getPackets().sendIComponentSettings(647, 30, 0, 27, 1150);
			player.getPackets().sendInterSetItemsOptionsScript(647, 33, 90, 4, 7, "Examine");
			player.getPackets().sendIComponentSettings(647, 33, 0, 27, 1026);
			ItemsContainer<Item> store = new ItemsContainer<>(215, false);
			for (int i = 0; i < store.getSize(); i++) {
				store.add(new Item(1048, i));
			}
			player.getPackets().sendItems(529, true, store); // .sendItems(-1,
			// -2, 529,
			// store);

			ItemsContainer<Item> drop = new ItemsContainer<>(215, false);
			for (int i = 0; i < drop.getSize(); i++) {
				drop.add(new Item(1048, i));
			}
			player.getPackets().sendItems(91, true, drop);// sendItems(-1,
			// -2, 91,
			// drop);

			ItemsContainer<Item> deposit = new ItemsContainer<>(8, false);
			for (int i = 0; i < deposit.getSize(); i++) {
				deposit.add(new Item(1048, i));
			}
			player.getPackets().sendItems(92, true, deposit);// sendItems(-1,
			// -2, 92,
			// deposit);
			return true;

		case "objectname":
			name = cmd[1].replaceAll("_", " ");
			String option = cmd.length > 2 ? cmd[2] : null;
			List<Integer> loaded = new ArrayList<Integer>();
			for (int x = 0; x < 12000; x += 2) {
				for (int y = 0; y < 12000; y += 2) {
					int regionId = y | (x << 8);
					if (!loaded.contains(regionId)) {
						loaded.add(regionId);
						r = World.getRegion(regionId, false);
						r.loadRegionMap();
						List<WorldObject> list = r.getAllObjects();
						if (list == null) {
							continue;
						}
						for (WorldObject o : list) {
							if (o.getDefinitions().name.equalsIgnoreCase(name)
									&& (option == null || o.getDefinitions().containsOption(option))) {
								System.out.println("Object found - [id=" + o.getId() + ", x=" + o.getX() + ", y="
										+ o.getY() + "]");
								// player.getPackets().sendGameMessage("Object
								// found - [id="
								// + o.getId() + ", x=" + o.getX() +
								// ", y="
								// + o.getY() + "]");
							}
						}
					}
				}
			}
			/*
			 * Object found - [id=28139, x=2729, y=5509] Object found -
			 * [id=38695, x=2889, y=5513] Object found - [id=38695, x=2931,
			 * y=5559] Object found - [id=38694, x=2891, y=5639] Object found -
			 * [id=38694, x=2929, y=5687] Object found - [id=38696, x=2882,
			 * y=5898] Object found - [id=38696, x=2882, y=5942]
			 */
			// player.getPackets().sendGameMessage("Done!");
			System.out.println("Done!");
			return true;

		case "msgtest":
			player.getPackets().sendGameMessage(Color.PURPLE, "Congratulations! You've just won a very rare prize!");
			return true;

		case "bork":
			player.getControlerManager().startControler("BorkController");
			return true;

		case "killnpc":
			for (NPC n : World.getNPCs()) {
				if (n == null || n.getId() != Integer.parseInt(cmd[1]))
					continue;
				n.applyHit(new Hit(player, n.getMaxHitpoints(), HitLook.REGULAR_DAMAGE));
			}
			return true;
		case "sound":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::sound soundid");
				return true;
			}
			try {
				player.getPackets().sendSoundEffect(Integer.valueOf(cmd[1]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::sound soundid");
			}
			return true;

		case "music":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::music musicid");
				return true;
			}
			try {
				player.getMusicsManager().playMusic(Integer.valueOf(cmd[1]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::music musicid");
			}
			return true;
		case "testdialogue":
			player.getDialogueManager().startDialogue("DagonHai", 7137, player, Integer.parseInt(cmd[1]));
			return true;

		case "removenpcs":
			for (NPC n : World.getNPCs()) {
				if (n.getId() == Integer.parseInt(cmd[1])) {
					n.reset();
					n.finish();
				}
			}
			return true;
		case "resetkdr":
			player.setKillCount(0);
			player.setDeathCount(0);
			return true;

		case "newtut":
			player.getControlerManager().startControler("TutorialIsland", 0);
			return true;

		case "removecontroler":
		case "forcestop":
		case "removecontroller":
		case "stopcontroler":
		case "stopcontroller":
			player.getControlerManager().forceStop();
			player.getInterfaceManager().sendInterfaces();
			return true;

		case "nomads":
			for (Player p : World.getPlayers())
				p.getControlerManager().startControler("NomadsRequiem");
			return true;

		case "give":
			StringBuilder sb = new StringBuilder(cmd[1]);
			int amount2 = 1;
			if (cmd.length > 2) {
				for (int i = 2; i < cmd.length; i++) {
					if (cmd[i].startsWith("+")) {
						amount2 = Integer.parseInt(cmd[i].replace("+", ""));
					} else {
						sb.append(" ").append(cmd[i]);
					}
				}
			}
			name = sb.toString().toLowerCase().replace("[", "(").replace("]", ")").replaceAll(",", "'");
			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
				ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
				if (def.getName().toLowerCase().equalsIgnoreCase(name)) {
					if (def.getName().toLowerCase().equalsIgnoreCase("rp")) {
						player.getInventory().addItem(5733, 1);
						player.getInventory().addItem(i, amount2);
						player.getPackets().sendPanelBoxMessage("Put " + def.getName().toLowerCase() + " (id: " + i
								+ ") - amount: " + amount2 + " in inv.");
						return true;
					}
					if (def.getName().toLowerCase().equalsIgnoreCase("coins")) {
						player.getMoneyPouch().sendDynamicInteraction(amount2, false);
						player.getPackets().sendPanelBoxMessage("Put " + def.getName().toLowerCase()
								+ " (id: 995) - amount: " + amount2 + " in pouch.");
						return true;
					}
					player.getInventory().addItem(i, amount2);
					player.getPackets().sendPanelBoxMessage("Put " + def.getName().toLowerCase() + " (id: " + i
							+ ") - amount: " + amount2 + " in inv.");
					return true;
				}
			}
			return true;

		case "givebank":
			StringBuilder sb2 = new StringBuilder(cmd[1]);
			int amount3 = 1;
			if (cmd.length > 2) {
				for (int i = 2; i < cmd.length; i++) {
					if (cmd[i].startsWith("+")) {
						amount3 = Integer.parseInt(cmd[i].replace("+", ""));
					} else {
						sb2.append(" ").append(cmd[i]);
					}
				}
			}
			name = sb2.toString().toLowerCase().replace("[", "(").replace("]", ")").replaceAll(",", "'");
			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
				ItemDefinitions def = ItemDefinitions.getItemDefinitions(i);
				if (def.getName().toLowerCase().equalsIgnoreCase(name)) {
					player.getBank().addItem(i, amount3, true);
					player.getPackets().sendPanelBoxMessage("Put " + def.getName().toLowerCase() + " (id: " + i
							+ ") - amount: " + amount3 + " in bank.");
					return true;
				}
			}
			return true;

		case "item":
			if (cmd.length < 2) {
				player.getPackets().sendGameMessage("Use: ::item id (optional:amount)");
				return true;
			}
			try {
				int itemId = Integer.valueOf(cmd[1]);
				if (itemId == 995) {
					player.getMoneyPouch().sendDynamicInteraction(cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1, false);
					player.getPackets().sendPanelBoxMessage(
							"Put id: 995 - amount: " + (cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1) + " in pouch.");
					return true;
				}
				player.getInventory().addItem(itemId, cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1);
				player.getPackets().sendPanelBoxMessage(
						"Put " + itemId + " - amount: " + (cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1) + " in inv.");
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage("Use: ::item id (optional:amount)");
			}
			return true;

		case "itembank":
			if (cmd.length < 2) {
				player.getPackets().sendGameMessage("Use: ::itembank id (optional:amount)");
				return true;
			}
			try {
				int itemId = Integer.valueOf(cmd[1]);
				player.getBank().addItem(itemId, cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1, true);
				player.getPackets().sendPanelBoxMessage("Put " + itemId + " - amount: "
						+ (cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1) + " in bank.");
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage("Use: ::itembank id (optional:amount)");
			}
			return true;

		case "copy":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player p2 = World.getPlayerByDisplayName(name);
			if (p2 == null) {
				player.getPackets().sendGameMessage(name + " is offline.");
				return true;
			}
			items = p2.getEquipment().getItems().getItemsCopy();
			for (int i = 0; i < items.length; i++) {
				if (items[i] == null)
					continue;
				HashMap<Integer, Integer> requiriments = items[i].getDefinitions().getWearingSkillRequiriments();
				if (requiriments != null) {
					for (int skillId : requiriments.keySet()) {
						if (skillId > 24 || skillId < 0)
							continue;
						int level = requiriments.get(skillId);
						if (level < 0 || level > 120)
							continue;
						if (player.getSkills().getLevelForXp(skillId) < level) {
							name = Skills.SKILL_NAME[skillId].toLowerCase();
							player.getPackets().sendGameMessage("You need to have a" + (name.startsWith("a") ? "n" : "")
									+ " " + name + " level of " + level + ".");
						}

					}
				}
				player.getEquipment().getItems().set(i, items[i]);
				player.getEquipment().refresh(i);
			}
			player.getAppearence().generateAppearenceData();
			return true;
		case "prayertest":
			player.getEffectsManager().startEffect(new Effect(EffectType.PROTECTION_DISABLED, 8));
			return true;

		case "karamja":
			player.getDialogueManager().startDialogue("KaramjaTrip",
					Utils.random(1) == 0 ? 11701 : (Utils.random(1) == 0 ? 11702 : 11703));
			return true;
		case "clanwars":
			// player.setClanWars(new ClanWars(player, player));
			// player.getClanWars().setWhiteTeam(true);
			// ClanChallengeInterface.openInterface(player);
			return true;
		case "watereast":
			for (int i = 0; i < 10; i++) {
				World.spawnObjectTemporary(new WorldObject(37227, 10, 0,
						new WorldTile(player.getX() + i * 2, player.getY() + 1, player.getPlane())), 2000);
				World.spawnObjectTemporary(new WorldObject(37227, 10, 2,
						new WorldTile(player.getX() + i * 2, player.getY() - 4, player.getPlane())), 2000);
			}
			return true;
		case "dungsmall":
			player.getDungManager().leaveParty();
			DungeonPartyManager testParty = new DungeonPartyManager();
			testParty.add(player);
			testParty.setFloor(50);
			testParty.setComplexity(6);
			testParty.setDificulty(1);
			testParty.setKeyShare(true);
			testParty.setSize(DungeonConstants.LARGE_DUNGEON);
			testParty.start();
			return true;
		case "dung":
			player.getDungManager().leaveParty();
			DungeonPartyManager party = new DungeonPartyManager();
			party.add(player);
			party.setFloor(48);// 60
			party.setComplexity(6);
			party.setDificulty(1);
			party.setSize(DungeonConstants.TEST_DUNGEON);
			party.setKeyShare(true);
			party.start();
			return true;
		case "dungtest":
			party = player.getDungManager().getParty();
			for (Player p : World.getPlayers()) {
				if (p == player || !p.hasStarted() || p.hasFinished()
						|| !(p.getControlerManager().getControler() instanceof Kalaboss))
					continue;
				p.getDungManager().leaveParty();
				party.add(p);
			}
			party.setFloor(1);
			party.setComplexity(6);
			party.setDificulty(party.getTeam().size());
			party.setSize(DungeonConstants.TEST_DUNGEON);
			party.setKeyShare(true);
			player.getDungManager().enterDungeon(false);
			return true;
		case "objects":
			for (int i = 0; i < 4; i++) {
				object = World.getObjectWithSlot(player, i);
				player.getPackets().sendPanelBoxMessage("object: " + (object == null ? ("null " + i)
						: ("id: " + object.getId() + ", " + object.getType() + ", " + object.getRotation())));
			}
			// int setting =
			// World.getRegion(player.getRegionId()).getSettings(player.getPlane(),
			// player.getXInRegion(), player.getYInChunk());
			player.getPackets().sendPanelBoxMessage(
					"setting: " + player.getXInRegion() + ", " + player.getYInRegion() + ", " + player.getRegionId());
			return true;
		case "checkdisplay":
			for (Player p : World.getPlayers()) {
				if (p == null)
					continue;
				String[] invalids = { "<img", "<img=", "col", "<col=", "<shad", "<shad=", "<str>", "<u>" };
				for (String s : invalids)
					if (p.getDisplayName().contains(s)) {
						player.getPackets().sendGameMessage(Utils.formatPlayerNameForDisplay(p.getUsername()));
					} else {
						player.getPackets().sendGameMessage("None exist!");
					}
			}
			return true;
		case "cutscene":
			player.getPackets().sendCutscene(Integer.parseInt(cmd[1]));
			return true;
		case "dzs":
			player.getCutscenesManager().play(new DZGuideScene());
			return true;
		case "noescape":
			player.getCutscenesManager().play(new NexCutScene(NexCombat.NO_ESCAPE_TELEPORTS[1], 1));
			return true;
		case "dungcoords":
			int chunkX = player.getX() / 16 * 2;
			int chunkY = player.getY() / 16 * 2;
			int x = player.getX() - chunkX * 8;
			int y = player.getY() - chunkY * 8;

			player.getPackets()
					.sendPanelBoxMessage("Room chunk : " + chunkX + ", " + chunkY + ", pos: " + x + ", " + y);

			if (player.getDungManager().isInside()) {
				Room room = player.getDungManager().getParty().getDungeon()
						.getRoom(player.getDungManager().getParty().getDungeon().getCurrentRoomReference(player));

				if (room != null) {
					int[] xy = DungeonManager.translate(x, y, (4 - room.getRotation()) & 0x3, 1, 1, 0);
					player.getPackets()
							.sendPanelBoxMessage("Dungeon Detected! Current rotation: " + room.getRotation());
					player.getPackets().sendPanelBoxMessage("Real Room chunk : " + room.getRoom().getChunkX() + ", "
							+ room.getRoom().getChunkY() + ", real pos for rot0: " + xy[0] + ", " + xy[1]);
				}
			}

			return true;

		case "itemoni":
			player.getPackets().sendItemOnIComponent(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
					Integer.valueOf(cmd[3]), 1);
			return true;

		case "items":
			for (int i = 0; i < 2000; i++) {
				player.getPackets().sendItems(i, new Item[] { new Item(i, 1) });
			}
			return true;

		case "giveitem":
			int itemId = Integer.parseInt(cmd[1]);
			int itemAmount = Integer.parseInt(cmd[2]);
			name = "";
			for (int i = 3; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

			target = World.getPlayerByDisplayName(name);

			if (target == null) {
				player.getPackets().sendGameMessage("Player is offline.");
				return false;
			}

			target.getInventory().addItem(itemId, itemAmount);
			target.getPackets().sendGameMessage("You have been given a "
					+ ItemDefinitions.getItemDefinitions(itemId).getName() + " by " + player.getDisplayName() + ".");
			player.getPackets().sendGameMessage("You have sent " + ItemDefinitions.getItemDefinitions(itemId).getName()
					+ " to " + target.getDisplayName() + ".");

			return true;
		case "trade":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");

			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				player.getTrade().openTrade(target);
				target.getTrade().openTrade(player);
			}
			return true;

		case "maxotherlevels":
			if (cmd.length < 4) {
				player.getPackets().sendGameMessage("Usage ::setlevel skillId level name");
				return false;
			}
			try {
				name = "";
				for (int i = 3; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					int level = Integer.parseInt(cmd[2]);
					if (level < 0 || level > 120) {
						player.getPackets().sendGameMessage("Please choose a valid level.");
					}
					for (int i = 0; i < 25; i++) {
						if (i == 24) {
							target.getSkills().set(24, 120);
							target.getSkills().setXp(i, Skills.getXPForLevel(level));
						}
						target.getSkills().set(i, 99);
						target.getSkills().setXp(i, Skills.getXPForLevel(level));

					}
					target.getAppearence().generateAppearenceData();
				} else {
					player.getPackets().sendGameMessage("Unable to find " + name + ".");
				}
				return false;
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage("Usage ::setlevel skillId level");
			}
			return true;

		case "setlevelother":
		case "setlevelp":
			if (cmd.length < 4) {
				player.getPackets().sendGameMessage("Usage ::setlevel skillId level name");
				return false;
			}
			try {
				name = "";
				for (int i = 3; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					int skill = Integer.parseInt(cmd[1]);
					int level = Integer.parseInt(cmd[2]);
					if (level < 0 || level > 120) {
						player.getPackets().sendGameMessage("Please choose a valid level.");
					}
					target.getSkills().set(skill, level);
					target.getSkills().setXp(skill, Skills.getXPForLevel(level));
					target.getAppearence().generateAppearenceData();
					player.getPackets().sendGameMessage(
							"You have set " + skill + " to " + level + " for " + target.getDisplayName() + ".");
				} else {
					player.getPackets().sendGameMessage(name + " is offline.");
				}
				return false;
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage("Usage ::setlevelp skillId level name");
			}
			return true;

		case "setlevel":
			if (cmd.length < 3) {
				player.getPackets().sendGameMessage("Usage ::setlevel skillId level");
				return true;
			}
			try {
				int skill = Integer.parseInt(cmd[1]);
				int level = Integer.parseInt(cmd[2]);
				if (level < 0 || level > 99) {
					player.getPackets().sendGameMessage("Please choose a valid level.");
					return true;
				}
				player.getSkills().set(skill, level);
				player.getSkills().setXp(skill, Skills.getXPForLevel(level));
				player.getAppearence().generateAppearenceData();
				player.getPackets()
						.sendGameMessage(player.getSkills().getSkillName(skill) + " (" + skill + ") to " + level + ".");
				return true;
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage("Usage ::setlevel skillId level");
			}
			return true;

		case "gano":
		case "ganodermic":
			player.getInventory().addItem(22482, 1);
			player.getInventory().addItem(22490, 1);
			player.getInventory().addItem(22486, 1);
			player.getInventory().addItem(25978, 1);
			player.getInventory().addItem(25980, 1);
			player.getPackets().sendGameMessage("Put ganodermic set in inv.");
			return true;

		case "npcadd":
			StringBuilder sBuilder = new StringBuilder(cmd[1]);
			name = sBuilder.toString().toLowerCase().replace("[", "(").replace("]", ")").replaceAll(",", "'");
			for (int i = 0; i < Utils.getNPCDefinitionsSize(); i++) {
				NPCDefinitions def = NPCDefinitions.getNPCDefinitions(i);
				if (def.getName().toLowerCase().equalsIgnoreCase(name)) {
					World.spawnNPC(i, player, -1, true, true);
					player.getPackets().sendPanelBoxMessage("NPC: " + def.getName().toLowerCase() + " (id: " + i
							+ ") - at " + player.getX() + ", " + player.getY() + ", " + player.getPlane() + ".");
					return true;
				}
			}
			return true;

		case "npc":
			try {
//				if (Integer.parseInt(cmd[1]) == 19464) {
//					new Araxxi(19464, player, 0, false, true, player);
//					return true;
//				}
				World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true, true);
				player.getPackets().sendGameMessage("Npc: " + Integer.parseInt(cmd[1]) + " at " + player.getX() + ", "
						+ player.getY() + ", " + player.getPlane() + ".", true);
				return true;
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::npc id(Integer)");
			}
			return true;

		case "loadwalls":
			WallHandler.loadWall(player.getCurrentFriendsChat().getClanWars());
			return true;

		case "cwbase":
			try {
				ClanWars cw = player.getCurrentFriendsChat().getClanWars();
				WorldTile base = cw.getBaseLocation();
				player.getPackets().sendGameMessage("Base x=" + base.getX() + ", base y=" + base.getY());
				base = cw.getBaseLocation().transform(
						cw.getAreaType().getNorthEastTile().getX() - cw.getAreaType().getSouthWestTile().getX(),
						cw.getAreaType().getNorthEastTile().getY() - cw.getAreaType().getSouthWestTile().getY(), 0);
				player.getPackets().sendGameMessage("Offset x=" + base.getX() + ", offset y=" + base.getY());
			} catch (Exception e) {
				e.printStackTrace();
				player.getPackets().sendGameMessage("error: " + e + ".");
			}
			return true;

		case "object":
		case "objectadd":
			try {
				int type = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 10;
				int rotation = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 0;
				if (type > 22 || type < 0) {
					type = 10;
				}
				World.spawnObject(new WorldObject(Integer.valueOf(cmd[1]), type, rotation, player.getX(), player.getY(),
						player.getPlane()));
				player.getPackets().sendGameMessage("Object: " + Integer.parseInt(cmd[1]) + " at " + player.getX()
						+ ", " + player.getY() + ", " + player.getPlane() + ".", true);
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: setkills id");
			}
			return true;
		case "ltab":
			player.getInterfaceManager().sendLockGameTab(Integer.valueOf(cmd[1]), true);
			return true;
		case "otab":
			player.getInterfaceManager().openGameTab(Integer.valueOf(cmd[1]));
			return true;
		case "tab":
			try {
				player.getInterfaceManager().setWindowInterface(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: tab id inter");
			}
			return true;

		case "killme":
			player.applyHit(new Hit(player, player.getHitpoints(), HitLook.POISON_DAMAGE));
			return true;

		case "sethp":
			player.setHitpoints(Integer.valueOf(cmd[1]));
			return true;

		case "hidec":
			if (cmd.length < 4) {
				player.getPackets().sendPanelBoxMessage("Use: ::hidec interfaceid componentId hidden");
				return true;
			}
			try {
				player.getPackets().sendHideIComponent(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[3]),
						Boolean.valueOf(cmd[2]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::hidec interfaceid componentId hidden");
			}
			return true;

		case "string":
			try {
				player.getInterfaceManager().sendCentralInterface(Integer.valueOf(cmd[1]));
				for (int i = 0; i <= Integer.valueOf(cmd[2]); i++)
					player.getPackets().sendIComponentText(Integer.valueOf(cmd[1]), i, "child: " + i);
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: string inter childid");
			}
			return true;

		case "gametab":
			player.getInterfaceManager().openGameTab(Integer.parseInt(cmd[1]));
			player.getPackets().sendGameMessage("Gametab: " + Integer.parseInt(cmd[1]) + ".");
			return true;

		case "istringl":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}

			try {
				for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
					player.getPackets().sendCSVarString(i, "String " + i);
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;

		case "istring":
			try {
				player.getPackets().sendCSVarString(Integer.valueOf(cmd[1]), "String " + Integer.valueOf(cmd[2]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: String id value");
			}
			return true;

		case "iconfig":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				for (int i = 0; i < Integer.valueOf(cmd[1]); i++) {
					player.getPackets().sendCSVarInteger(Integer.parseInt(cmd[2]), i);
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;
		case "nisvar":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: nisvar id value");
				return true;
			}
			try {
				player.getPackets().sendNISVar(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;
		case "var":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				player.getVarsManager().forceSendVar(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;
		case "forcemovement":
			WorldTile toTile = player.transform(0, 5, 0);
			player.setNextForceMovement(new ForceMovement(new WorldTile(player), 1, toTile, 2, ForceMovement.NORTH));

			return true;

		case "ab":
			player.getVarsManager().sendVar(727, (Integer.valueOf(cmd[1]) << 4 | 7));
			return true;

		case "varbit":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				player.getVarsManager().sendVarBit(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				player.getPackets()
						.sendGameMessage("varBit: " + Integer.valueOf(cmd[1]) + " " + Integer.valueOf(cmd[2]) + ".");
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;

		case "hit":
			HitLook.MELEE_DAMAGE.setMark(Integer.valueOf(cmd[1]));
			player.applyHit(new Hit(player, 300, HitLook.MELEE_DAMAGE, 0));

			return true;
		case "menu":
			/*
			 * player.getPackets().sendExecuteScript(8862, 0, 7);
			 * player.getPackets().sendExecuteScript(8862, 0, 8);
			 * player.getPackets().sendExecuteScript(8862, 1, 5);
			 */
			player.getInterfaceManager().openMenu(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
			return true;

		case "iloop":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++)
					player.getInterfaceManager().sendCentralInterface(i);
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;

		case "tloop":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++)
					player.getInterfaceManager().setWindowInterface(i, Integer.valueOf(cmd[3]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;
		case "hloop":
			if (cmd.length < 5) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				for (int i = Integer.valueOf(cmd[2]); i < Integer.valueOf(cmd[3]); i++) {
					player.getPackets().sendHideIComponent(Integer.valueOf(cmd[1]), i, Boolean.valueOf(cmd[4]));
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;
		case "varloop":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++) {
					player.getVarsManager().sendVar(i, Integer.valueOf(cmd[3]));
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;
		case "varloop2":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++) {
					player.getVarsManager().sendVar(i, i);
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;
		case "varbitloop":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++)
					player.getVarsManager().sendVarBit(i, Integer.valueOf(cmd[3]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;
		case "objectanim":

			object = cmd.length == 4
					? World.getStandartObject(
							new WorldTile(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), player.getPlane()))
					: World.getObjectWithType(
							new WorldTile(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]), player.getPlane()),
							Integer.parseInt(cmd[3]));
			if (object == null) {
				player.getPackets().sendPanelBoxMessage("No object was found.");
				return true;
			}
			player.getPackets().sendObjectAnimation(object,
					new Animation(Integer.parseInt(cmd[cmd.length == 4 ? 3 : 4])));
			return true;
		case "loopoanim":
			x = Integer.parseInt(cmd[1]);
			y = Integer.parseInt(cmd[2]);
			final WorldObject object1 = World.getObjectWithSlot(player, Region.OBJECT_SLOT_FLOOR);
			if (object1 == null) {
				player.getPackets().sendPanelBoxMessage(
						"Could not find object at [x=" + x + ", y=" + y + ", z=" + player.getPlane() + "].");
				return true;
			}
			System.out.println("Object found: " + object1.getId());
			final int start = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 10;
			final int end = cmd.length > 4 ? Integer.parseInt(cmd[4]) : 20000;
			GameExecutorManager.fastExecutor.scheduleAtFixedRate(new TimerTask() {
				int current = start;

				@Override
				public void run() {
					while (AnimationDefinitions.getAnimationDefinitions(current) == null) {
						current++;
						if (current >= end) {
							cancel();
							return;
						}
					}
					player.getPackets().sendPanelBoxMessage("Current object animation: " + current + ".");
					player.getPackets().sendObjectAnimation(object1, new Animation(current++));
					if (current >= end) {
						cancel();
					}
				}
			}, 1800, 1800);
			return true;
		case "bconfigloop":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
				return true;
			}
			try {
				for (int i = Integer.valueOf(cmd[1]); i < Integer.valueOf(cmd[2]); i++) {
					player.getPackets().sendCSVarInteger(i, Integer.valueOf(cmd[3]));
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: config id value");
			}
			return true;

		case "reset":
			if (cmd.length < 2) {
				for (int skill = 0; skill < Skills.SKILL_NAME.length; skill++) {
					player.getSkills().setXp(skill, 0);
					player.getSkills().set(skill, 1);
				}
				player.getSkills().init();
				return true;
			}
			try {
				player.getSkills().setXp(Integer.valueOf(cmd[1]), 0);
				player.getSkills().set(Integer.valueOf(cmd[1]), 1);

			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::master skill");
			}
			return true;
		case "build":
			player.getVarsManager().sendVar(483, 1024);
			player.getVarsManager().sendVar(483, 1025);
			player.getVarsManager().sendVar(483, 1026);
			player.getVarsManager().sendVar(483, 1027);
			player.getVarsManager().sendVar(483, 1028);
			player.getVarsManager().sendVar(483, 1029);
			player.getVarsManager().sendVar(483, 1030);
			player.getVarsManager().sendVar(483, 1031);
			player.getVarsManager().sendVar(483, 1032);
			player.getVarsManager().sendVar(483, 1033);
			player.getVarsManager().sendVar(483, 1034);
			player.getVarsManager().sendVar(483, 1035);
			player.getVarsManager().sendVar(483, 1036);
			player.getVarsManager().sendVar(483, 1037);
			player.getVarsManager().sendVar(483, 1038);
			player.getVarsManager().sendVar(483, 1039);
			player.getVarsManager().sendVar(483, 1040);
			player.getVarsManager().sendVar(483, 1041);
			player.getVarsManager().sendVar(483, 1042);
			player.getVarsManager().sendVar(483, 1043);
			player.getVarsManager().sendVar(483, 1044);
			player.getVarsManager().sendVar(483, 1045);
			player.getVarsManager().sendVar(483, 1024);
			player.getVarsManager().sendVar(483, 1027);
			player.getPackets().sendCSVarInteger(841, 0);
			player.getPackets().sendCSVarInteger(199, -1);
			player.getPackets().sendIComponentSettings(1306, 55, -1, -1, 0);
			player.getPackets().sendIComponentSettings(1306, 8, 4, 4, 1);
			player.getPackets().sendIComponentSettings(1306, 15, 4, 4, 1);
			player.getPackets().sendIComponentSettings(1306, 22, 4, 4, 1);
			player.getPackets().sendIComponentSettings(1306, 29, 4, 4, 1);
			player.getPackets().sendIComponentSettings(1306, 36, 4, 4, 1);
			player.getPackets().sendIComponentSettings(1306, 43, 4, 4, 1);
			player.getPackets().sendIComponentSettings(1306, 50, 4, 4, 1);
			System.out.println("Build");
			return true;
		case "givexp":
			String n = "";
			for (int i = 3; i < cmd.length; i++)
				n += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player t = World.getPlayerByDisplayName(n);
			t.getSkills().addXp(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]), true);
			player.getPackets().sendGameMessage("Giving " + t.getDisplayName() + " " + Integer.valueOf(cmd[2])
					+ " xp in " + Integer.valueOf(cmd[1]));
			return true;
		case "pintest":
			player.getBank().setRecoveryTime(50000);
			return true;
		case "givetokens":
			String na = "";
			for (int i = 2; i < cmd.length; i++)
				na += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player ta = World.getPlayerByDisplayName(na);
			ta.getDungManager().addTokens(Integer.valueOf(cmd[1]));
			player.getPackets()
					.sendGameMessage("Giving " + ta.getDisplayName() + " " + Integer.valueOf(cmd[1]) + " tokens.");
			return true;
		case "addxp":
			for (int skill = 0; skill < 26; skill++)
				player.getSkills().addXp(skill, 1000000, true);
			return true;
		case "window":
			player.getInterfaceManager().setRootInterface(1143, false);
			return true;
		case "bconfig":
			if (cmd.length < 3) {
				player.getPackets().sendPanelBoxMessage("Use: bconfig id value");
				return true;
			}
			try {
				player.getPackets().sendCSVarInteger(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: bconfig id value");
			}
			return true;

		case "tonpc":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::tonpc id(-1 for player)");
				return true;
			}
			try {
				player.getAppearence().transformIntoNPC(Integer.valueOf(cmd[1]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::tonpc id(-1 for player)");
			}
			return true;

		case "inter":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
				return true;
			}
			try {
				if (Integer.valueOf(cmd[1]) > Utils.getInterfaceDefinitionsSize())
					return true;
				player.getInterfaceManager().sendCentralInterface(Integer.valueOf(cmd[1]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
			}
			return true;
		case "pane":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::pane interfaceId");
				return true;
			}
			try {
				player.getPackets().sendRootInterface(Integer.valueOf(cmd[1]), 0);
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::pane interfaceId");
			}
			return true;
		case "overlay":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
				return true;
			}
			int child = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 28;
			try {
				player.getInterfaceManager().setInterface(true,
						player.getInterfaceManager().hasRezizableScreen() ? 746 : 548, child, Integer.valueOf(cmd[1]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
			}
			return true;

		case "resetprices":
			player.getPackets().sendGameMessage("Starting!");
			GrandExchange.reset(true, false);
			player.getPackets().sendGameMessage("Done!");
			return true;
		case "recalcprices":
			player.getPackets().sendGameMessage("Starting!");
			GrandExchange.recalcPrices();
			player.getPackets().sendGameMessage("Done!");
			return true;

		case "interh2":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
				return true;
			}

			try {
				int interId = Integer.valueOf(cmd[1]);
				for (int componentId = Integer.valueOf(cmd[2]); componentId < Integer.valueOf(cmd[3]); componentId++) {
					player.getPackets().sendHideIComponent(interId, componentId, false);
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
			}
			return true;
		case "interh":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
				return true;
			}

			try {
				int interId = Integer.valueOf(cmd[1]);
				for (int componentId = 0; componentId < Utils
						.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
					player.getPackets().sendHideIComponent(interId, componentId, false);
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
			}
			return true;

		case "inters":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
				return true;
			}

			try {
				int interId = Integer.valueOf(cmd[1]);
				for (int componentId = 0; componentId < Utils
						.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
					player.getPackets().sendIComponentText(interId, componentId, "cid: " + componentId);
				}
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::inter interfaceId");
			}
			return true;

		case "kill":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null)
				return true;
			target.applyHit(new Hit(target, player.getHitpoints(), HitLook.POISON_DAMAGE));
			player.getPackets().sendGameMessage("You have killed " + target.getDisplayName() + ".");
			target.stopAll();
			return true;

		case "killall":
			if (Settings.WORLD_ID == 1 && Settings.HOSTED) {
				player.getPackets().sendGameMessage("That's not happening - 99max99 M.");
				return true;
			}
			for (Player all : World.getPlayers())
				all.applyHit(new Hit(all, player.getHitpoints(), HitLook.REGULAR_DAMAGE));
			return true;

		case "treasurehunter":
		case "sof":
			if (player.getUsername().equalsIgnoreCase("99max99") || player.getUsername().equalsIgnoreCase("brandon")) {
				if (cmd.length < 2) {
					player.getPackets().sendGameMessage("Wrong, try '" + cmd[0] + " amount'.");
					return true;
				}
				if (System.currentTimeMillis() - player.majorDelay < 10000) {
					player.getPackets().sendGameMessage(
							"I think that's enough for today mate, we don't want this bitch abused, right?");
					return true;
				}
				player.majorDelay = System.currentTimeMillis();
				for (Player all : World.getPlayers()) {
					if (Integer.parseInt(cmd[1]) > 10) {
						player.getPackets().sendGameMessage("Hold on there cowboy! Are you mentally unstable?");
						return true;
					}
					all.getSquealOfFortune().handleEarnedKeys(Integer.parseInt(cmd[1]));
					all.getPackets().sendGameMessage("<col=FF0000>Everyone online has been gifted with "
							+ Integer.parseInt(cmd[1]) + " Treasure Hunter keys by " + player.getDisplayName() + ".");
					all.getPackets().sendGameMessage(
							"You have " + all.getSquealOfFortune().getAllKeys() + " Treasure Hunter keys.");
				}
			} else
				player.getPackets().sendGameMessage("Acces was not granted.");
			return true;

		case "bank":
			player.getBank().openBank();
			return true;

		case "tele":
			try {
				cmd = cmd[1].split(",");
				int plane = Integer.valueOf(cmd[0]);
				int xx = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
				int yy = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
				player.setNextWorldTile(new WorldTile(xx, yy, plane));
				player.getPackets().sendGameMessage("tele: " + xx + ", " + yy + ", " + plane + ".", true);
			} catch (Exception e) {
				player.getPackets().sendGameMessage(e + ".");
				player.getPackets().sendGameMessage("Use: ::teleport x y z");
			}
			return true;

		case "teleport":
			try {
				int xxx = Integer.valueOf(cmd[1]);
				int yyy = Integer.valueOf(cmd[2]);
				int zzz = Integer.valueOf(cmd[3]);
				player.resetWalkSteps();
				player.setNextWorldTile(new WorldTile(xxx, yyy, cmd.length >= 4 ? zzz : player.getPlane()));
			} catch (NumberFormatException e) {
				player.getPackets().sendGameMessage(e + ".");
			} catch (Exception e) {
				player.getPackets().sendGameMessage(e + ".");
			}
			return true;

		case "testy":
			Settings.DOUBLE_VOTES = true;
			return true;

		case "testvoting":
		case "testvoted":
		case "votetest":
			int vp = 2;
			int coins = 25000;
			if (player.isBronzeMember()) {
				vp = 3;
				coins = 35000;
			} else if (player.isSilverMember()) {
				vp = 3;
				coins = 35000;
			} else if (player.isGoldMember()) {
				vp = 3;
				coins = 35000;
			} else if (player.isPlatinumMember()) {
				vp = 4;
				coins = 45000;
			} else if (player.isDiamondMember()) {
				vp = 4;
				coins = 45000;
			} else {
				vp = 2;
				coins = 25000;
			}
			player.getPackets().sendGameMessage("Unable to locate your reward.");
			if (Settings.DOUBLE_VOTES) {
				vp *= 2;
				coins *= 2;
			}
			player.setVoteCount(player.getVoteCount() + vp);
			player.getBank().addItem(995, coins, true);
			World.sendNews(player.getDisplayName() + " has voted for " + Settings.SERVER_NAME + " and received " + vp
					+ " vote points and " + String.valueOf(coins).replace("000", "") + "k.", 3);
			player.getPackets()
					.sendGameMessage("Thank you for voting. Your reward is in your bank - you have <col=FF0000>"
							+ player.getVoteCount() + "</col> vote points.");
			return true;

		case "update":
		case "shutdown":
		case "reboot":
			int delay = 300;
			if (cmd.length >= 2) {
				try {
					delay = Integer.valueOf(cmd[1]);
				} catch (NumberFormatException e) {
					delay = 300;
					return true;
				}
			}
			Engine.shutdown(delay, true, true);
			player.getPackets().sendGameMessage("Sending shutdown, at " + delay + " seconds.");
			return true;

		case "anim":
		case "animation":
		case "emote":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				return true;
			}
			try {
				player.setNextAnimation(new Animation(Integer.valueOf(cmd[1])));
				player.getPackets().sendGameMessage(cmd[0] + " to " + Integer.valueOf(cmd[1]) + ".");
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::emote id");
			}
			return true;

		case "renderemote":
		case "remote":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::emote id");
				return true;
			}
			try {
				player.getAppearence().setRenderEmote(Integer.valueOf(cmd[1]));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::emote id");
			}
			return true;

		case "quake":
			player.getPackets().sendCameraShake(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
					Integer.valueOf(cmd[3]), Integer.valueOf(cmd[4]), Integer.valueOf(cmd[5]));
			return true;

		case "getrender":
			player.getPackets().sendGameMessage("Testing renders");
			for (int i = 0; i < 3000; i++) {
				try {
					player.getAppearence().setRenderEmote(i);
					player.getPackets().sendGameMessage("Testing " + i);
					Thread.sleep(600);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return true;

		case "setlook":
			PlayerLook.setSet(player, Integer.valueOf(cmd[1]));
			return true;
		case "color":
			PlayerLook.openCharacterCustomizing(player);
			return true;

		case "tryinter":
			WorldTasksManager.schedule(new WorldTask() {
				int i = 1;

				@Override
				public void run() {
					if (player.hasFinished()) {
						stop();
					}
					player.getInterfaceManager().sendCentralInterface(i);
					System.out.println("Inter - " + i);
					i++;
				}
			}, 0, 1);
			return true;

		case "tryanim":
			WorldTasksManager.schedule(new WorldTask() {
				int i = 16700;

				@Override
				public void run() {
					if (i >= Utils.getAnimationDefinitionsSize()) {
						stop();
						return;
					}
					if (player.getLastAnimationEnd() > Utils.currentTimeMillis()) {
						player.setNextAnimation(new Animation(-1));
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextAnimation(new Animation(i));
					System.out.println("Anim - " + i);
					i++;
				}
			}, 0, 3);
			return true;

		case "animcount":
			System.out.println(Utils.getAnimationDefinitionsSize() + " anims.");
			return true;

		case "trygfx":
			WorldTasksManager.schedule(new WorldTask() {
				int i = 2100;

				@Override
				public void run() {
					if (i >= Utils.getGraphicDefinitionsSize()) {
						stop();
					}
					if (player.hasFinished()) {
						stop();
					}
					player.setNextGraphics(new Graphics(i));
					System.out.println("GFX - " + i);
					i++;
				}
			}, 0, 3);
			return true;
		case "teleto":
			String name2222 = "";
			for (int i = 1; i < cmd.length; i++)
				name2222 += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player target222 = World.getPlayerByDisplayName(name2222);
			if (target222 == null)
				player.getPackets().sendGameMessage("Couldn't find player " + name2222 + ".");
			else {
				player.setNextWorldTile(target222);
			}
			return true;

		case "sendhome":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null)
				player.getPackets().sendGameMessage(name + " is offline.");
			else {
				target.unlock();
				target.getControlerManager().forceStop();
				if (target.getNextWorldTile() == null)
					target.setNextWorldTile(Settings.HOME_LOCATION);
				player.getPackets().sendGameMessage("You have unnulled: " + target.getDisplayName() + ".");
				return true;
			}
			return true;
		case "gfx":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
				return true;
			}
			try {
				player.setNextGraphics(new Graphics(Integer.valueOf(cmd[1]),
						cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 0, cmd.length == 4 ? Integer.valueOf(cmd[3]) : 0));
				player.getPackets().sendGameMessage("gfx: " + Integer.parseInt(cmd[1]) + ".");
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
			}
			return true;
		case "gfxp":
			if (cmd.length < 2) {
				player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
				return true;
			}
			try {
				player.getPackets().sendGraphics(new Graphics(Integer.valueOf(cmd[1])), new WorldTile(player));
			} catch (NumberFormatException e) {
				player.getPackets().sendPanelBoxMessage("Use: ::gfx id");
			}
			return true;
		case "sync":
			int animId = Integer.parseInt(cmd[1]);
			int gfxId = Integer.parseInt(cmd[2]);
			int height = cmd.length > 3 ? Integer.parseInt(cmd[3]) : 0;
			player.setNextAnimation(new Animation(animId));
			player.setNextGraphics(new Graphics(gfxId, 0, height));
			return true;
		case "staffmeeting":
		case "modmeeting":
		case "meeting":
			if (JModTable.PMOD_MEETING == false) {
				for (Player staff : World.getPlayers()) {
					if (staff.getRights() == 0)
						continue;
					JModTable.PMOD_MEETING = true;
					staff.getPackets().sendGameMessage("<col=FF0000>A staff meeting has been requested by "
							+ player.getDisplayName() + " please use the command ::accept to teleport.");
				}
			} else {
				for (Player staff : World.getPlayers()) {
					if (staff.getRights() == 0)
						continue;
					JModTable.PMOD_MEETING = false;

					staff.setNextWorldTile(new WorldTile(staff.getLastX(), staff.getLastY(), staff.getLastPlane()));

					staff.getPackets().sendGameMessage(
							"<col=FF0000>The staff meeting has ended, you have been teleported to your last location.");
				}
			}
			return true;
		case "testsongfromthedepths":
			player.getPackets().sendGameMessage("Starting song from the depths development version.");
			player.getControlerManager().startControler("SongFromTheDepths");
			return true;
		case "whynotworking":
			player.getPackets().sendGameMessage("yes it is you idiot v2");
			return true;
		case "testlucilledia":
			player.getDialogueManager().startDialogue("Lucille");
			return true;
		}
		return false;
	}

	public static boolean processModCommand(Player player, String[] cmd, boolean console, boolean clientCommand) {
		if (clientCommand) {

		} else {
			String name = "";
			Player target;
			switch (cmd[0].toLowerCase()) {
			case "spybank":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player Other = World.getPlayerByDisplayName(name);
				try {
					player.getPackets().sendItems(95, Other.getBank().getContainerCopy());
					player.getBank().openPlayerBank(Other);
				} catch (Exception e) {
					e.printStackTrace();
					player.getPackets().sendGameMessage("error: " + e + ".");
				}
				return true;
			case "accept":
				if (JModTable.PMOD_MEETING) {
					player.stopAll();

					player.setLastX(player.getX());
					player.setLastY(player.getY());
					player.setLastPlane(player.getPlane());

					player.setNextWorldTile(new WorldTile(2846, 5148, 0));
				} else {
					player.getPackets().sendGameMessage("There are no staff meetings being held at the moment.");
				}
				return true;
			case "teleto":
				String name2222 = "";
				for (int i = 1; i < cmd.length; i++)
					name2222 += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player target222 = World.getPlayerByDisplayName(name2222);
				if (target222 == null)
					player.getPackets().sendGameMessage("Couldn't find player " + name2222 + ".");
				else {
					player.setNextWorldTile(target222);
				}
				return true;
			case "hide":
				player.getAppearence().setHidden(!player.getAppearence().isHidden());
				player.getPackets().sendGameMessage("Hidden:" + player.getAppearence().isHidden());
				return true;
			case "bank":
				player.getBank().openBank();
				return true;
			case "tele":
				try {
					cmd = cmd[1].split(",");
					int plane = Integer.valueOf(cmd[0]);
					int xx = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
					int yy = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
					player.setNextWorldTile(new WorldTile(xx, yy, plane));
					player.getPackets().sendGameMessage("tele: " + xx + ", " + yy + ", " + plane + ".", true);
				} catch (Exception e) {
					player.getPackets().sendGameMessage(e + ".");
					player.getPackets().sendGameMessage("Use: ::teleport x y z");
				}
				return true;
			case "teleport":
				try {
					int xxx = Integer.valueOf(cmd[1]);
					int yyy = Integer.valueOf(cmd[2]);
					int zzz = Integer.valueOf(cmd[3]);
					player.resetWalkSteps();
					player.setNextWorldTile(new WorldTile(xxx, yyy, cmd.length >= 4 ? zzz : player.getPlane()));
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage(e + ".");
				} catch (Exception e) {
					player.getPackets().sendGameMessage(e + ".");
				}
				return true;
			case "sz":
				if (player.getRights() >= 1 );
					player.setNextWorldTile(new WorldTile(2845, 5158, 0));
				return true;
			case "unnull":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					player.getPackets().sendGameMessage(name + " is offline.");
				else {
					target.unlock();
					target.getControlerManager().forceStop();
					if (target.getNextWorldTile() == null)
						target.setNextWorldTile(Settings.HOME_LOCATION);
					player.getPackets().sendGameMessage("You have unnulled: " + target.getDisplayName() + ".");
				}
				return true;
			case "interfaceloop":
				try {
					for (int i = 200; i < Utils.getInterfaceDefinitionsSize(); i++) {
						player.getInterfaceManager().sendCentralInterface(i);
						player.getPackets().sendGameMessage("interfaceId: " + i + ".");
						Thread.sleep(400);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				player.getPackets().sendGameMessage("type ::interfacestop to stop.");
				break;
			case "interfacedump":
				for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(Integer.parseInt(cmd[1])); i++) {
					player.getPackets().sendIComponentText(Integer.parseInt(cmd[1]), i, "" + i);
				}
				break;
			case "save":
			case "saveall":
				for (Player p : World.getPlayers()) {
					try {
						if (p == null || !p.hasStarted() || p.hasFinished())
							continue;

						byte[] data = SerializationUtilities.tryStoreObject(p);
						if (data == null || data.length <= 0)
							continue;
						GrandExchange.save();
						PlayerHandlerThread.addSave(p.getUsername(), data);
					} catch (Exception e) {
						Logger.log("Engine", "An error has occured: " + e);
						player.getPackets().sendGameMessage(e + ".");
					}
				}
				player.getPackets().sendGameMessage(
						"Successfully saved " + World.getPlayers().size() + " players - it's been fixed btw...");
				return true;
			case "realnames":
				for (int i = 10; i < World.getPlayers().size() + 10; i++)
					player.getPackets().sendIComponentText(275, i, "");
				for (int i = 0; i < World.getPlayers().size() + 1; i++) {
					Player p2 = World.getPlayers().get(i);
					if (p2 == null)
						continue;
					player.getPackets().sendIComponentText(275, i + 10,
							p2.getDisplayName() + " - " + Utils.formatPlayerNameForDisplay(p2.getUsername()));
				}
				player.getPackets().sendIComponentText(275, 1, "Displayname - Username");
				player.getInterfaceManager().sendCentralInterface(275);
				return true;
			case "sy":
			case "staffyell":
				String message2 = "";
				for (int i = 1; i < cmd.length; i++)
					message2 += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				sendYell(player, Utils.fixChatMessage(message2), true);
				return true;
			}
		}
		return false;
	}

	public static void sendYell(Player player, String message, boolean staffYell) {
		message = Censor.getFilteredMessage(message);
		if (player.isMuted()) {
			player.getPackets().sendGameMessage("You have been temporarily muted due to breaking a rule.");
			player.getPackets()
					.sendGameMessage("This mute will remain for a further " + player.getMutedFor() + " hours.");
			player.getPackets().sendGameMessage("To prevent further mutes please read the rules.");
			return;
		}
		if (message.contains("null")) {
			player.getPackets().sendGameMessage("Sorry, but you can't type the word 'null', it pisses off 99max99.");
			return;
		}
		if (staffYell) {
			switch (player.getRights()) {
			case 1:
				if (player.isAMember()) {
					World.sendIgnoreableWorldMessage(player,
							"[<col=f46f27>Staff</col>] <col=f46f27><img=10>" + player.getDisplayName() + ": " + message,
							true);
				} else {
					World.sendIgnoreableWorldMessage(player,
							"[<col=f46f27>Staff</col>] <col=f46f27><img=0>" + player.getDisplayName() + ": " + message,
							true);
				}
				break;
			case 2:
				if (player.getUsername().equals("99max99") || player.getUsername().equalsIgnoreCase("brandon")) {
					World.sendIgnoreableWorldMessage(player,
							"[<col=f46f27>Staff</col>] <col=f46f27><img=1>" + player.getDisplayName() + ": " + message,
							true);
				} else {
					World.sendIgnoreableWorldMessage(player,
							"[<col=f46f27>Staff</col>] <col=f46f27><img=1>" + player.getDisplayName() + ": " + message,
							true);
				}
				break;
			}
			return;
		}
		if (message.length() > 100)
			message = message.substring(0, 100);
		String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=", "<shad", "<shad=", "<str>", "<u>" };
		for (String s : invalid)
			if (message.contains(s)) {
				player.getPackets().sendGameMessage("You cannot use the tag '" + invalid.toString() + "'.");
				return;
			}
		switch (player.getRights()) {
		case 0:
			if (player.isIronman() || player.isHardcoreIronman()) {
				World.sendYellMessage(player, "<col=2792f4>[" + player.getIronmanTitle(true) + "] <img="
						+ player.getIronmanBadge() + ">" + player.getDisplayName() + ": " + message);
			} else if (player.isBronzeMember()) {
				World.sendYellMessage(player,
						"<col=2792f4>[Bronze Member] <img=9>" + player.getDisplayName() + ": " + message);
			} else if (player.isSilverMember()) {
				World.sendYellMessage(player,
						"<col=2792f4>[Silver Member] <img=9>" + player.getDisplayName() + ": " + message);
			} else if (player.isGoldMember()) {
				World.sendYellMessage(player,
						"<col=2792f4>[Gold Member] <img=9>" + player.getDisplayName() + ": " + message);
			} else if (player.isPlatinumMember()) {
				World.sendYellMessage(player,
						"<col=2792f4>[Platinum Member] <img=9>" + player.getDisplayName() + ": " + message);
			} else if (player.isDiamondMember()) {
				World.sendYellMessage(player,
						"<col=2792f4>[Diamond Member] <img=9>" + player.getDisplayName() + ": " + message);
			} else {
				World.sendYellMessage(player, "<col=2792f4>" + player.getDisplayName() + ": " + message);
			}
			break;
		case 1:
			if (player.isAMember()) {
				World.sendYellMessage(player,
						"<col=2792f4>[Moderator] <img=10>" + player.getDisplayName() + ": " + message);
			} else {
				World.sendYellMessage(player,
						"<col=2792f4>[Moderator] <img=0>" + player.getDisplayName() + ": " + message);
			}
			break;
		case 2:
			if (player.getUsername().equals("99max99")) {
				World.sendYellMessage(player, "<col=2792f4>[Owner] <img=1>" + player.getDisplayName() + ": " + message);
			} else if (player.getUsername().equalsIgnoreCase("")) {
				World.sendYellMessage(player, "<col=2792f4>[Co Owner] <img=1>" + player.getDisplayName() + ": " + message);
			} else {
				World.sendYellMessage(player,
						"<col=2792f4>[Administrator] <img=1>" + player.getDisplayName() + ": " + message);
			}
			break;
		}
		final String FILE_PATH = "data/logs/chat/yell/";
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + player.getUsername() + ".txt", true));
			writer.write(
					"[" + dateFormat.format(cal.getTime()) + ", IP: " + player.getSession().getIP() + "] : " + message);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public static boolean canWearItem(Player player, int itemId) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		if (defs.isStealingCreationItem()) {
			if (player.getControlerManager().getControler() instanceof StealingCreationController)
				return true;
			player.getPackets().sendGameMessage("How did you get this item? Send a bug report to admin@"
					+ Settings.WEBSITE_LINK.replaceAll("https://", "") + ".");
			return false;
		}

		if (!player.getDungManager().isInside() && defs.isDungeoneeringItem()) {
			player.getPackets().sendGameMessage("How did you get this item? Send a bug report to admin@"
					+ Settings.WEBSITE_LINK.replaceAll("https://", "") + ".");
			player.getInventory().deleteItem(itemId, player.getInventory().getAmountOf(itemId));
			return false;
		}

		return true;
	}

	public static void archiveLogs(Player player, String[] cmd) {
		try {
			String location = "";
			if (player.getRights() == 2) {
				location = "data/logs/commands/admin/" + player.getUsername() + ".txt";
			} else if (player.getRights() == 1) {
				location = "data/logs/commands/mod/" + player.getUsername() + ".txt";
			} else if (player.getRights() == 0) {
				location = "data/logs/commands/player/" + player.getUsername() + ".txt";
			}
			String afterCMD = "";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			for (int i = 1; i < cmd.length; i++) {
				afterCMD += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			}
			BufferedWriter writer = new BufferedWriter(new FileWriter(location, true));
			writer.write("[" + dateFormat.format(cal.getTime()) + "], IP: " + player.getSession().getIP() + " - ::"
					+ cmd[0] + " " + afterCMD);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void performPointEmote(Player teleto) {
		teleto.setNextAnimation(new Animation(17540));
		teleto.setNextGraphics(new Graphics(3401));
	}

	public static void performTeleEmote(Player target) {
		target.setNextAnimation(new Animation(17544));
		target.setNextGraphics(new Graphics(3403));
	}

	public static void performKickBanEmote(Player target) {
		target.setNextAnimation(new Animation(17542));
		target.setNextGraphics(new Graphics(3402));
	}

	public static void max(Player player, double xp) {
		if (player != null) {
			for (int i = 0; i < 26; i++) {
				if (i == 24)
					player.getSkills().set(i, 120);
				else
					player.getSkills().set(i, 99);
				player.getSkills().setXp(i, xp);
				player.getAppearence().generateAppearenceData();
			}
		}
	}

	public static void reset(Player player) {
		if (player != null) {
			for (int i = 0; i < 26; i++) {
				if (i == 24)
					player.getSkills().set(i, 1);
				else
					player.getSkills().set(i, 1);
				player.getSkills().setXp(i, 0);
				player.getAppearence().generateAppearenceData();
			}
		}
	}

	/*
	 * doesnt let it be instanced
	 */
	private DeveloperConsole() {

	}
}