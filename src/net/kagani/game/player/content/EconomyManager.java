package net.kagani.game.player.content;

import java.util.TimerTask;

import net.kagani.Settings;
import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.World;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.content.surpriseevents.SurpriseEvent;
import net.kagani.game.player.dialogues.Dialogue;
import net.kagani.network.LoginClientChannelManager;
import net.kagani.network.LoginProtocol;
import net.kagani.network.encoders.LoginChannelsPacketEncoder;
import net.kagani.utils.Logger;
import net.kagani.utils.ShopsHandler;
import net.kagani.utils.Utils;

public class EconomyManager {
	
	private static int[] ROOT_COMPONENTS = new int[] { 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 };
	private static int[] TEXT_COMPONENTS = new int[] { 38, 46, 54, 62, 70, 78, 86, 94, 102, 110, 118, 126, 134, 142, 150, 158, 166, 174, 182, 190 };
	private static int[] CLICK_COMPONENTS = new int[] { 35, 43, 51, 59, 67, 75, 83, 91, 99, 107, 115, 123, 131, 139, 147, 155, 163, 171, 179, 187 };

	private static String[] SHOPS_NAMES = new String[]
	{
		"General store",
		"Vote shop",
		"PKP shop 1",
		"PKP shop 2",
		"Melee Weapons",
		"Ranging Weapons",
		"Melee armor",
		"Ranged armor",
		"Magic armor",
		"Food & Potions",
		"Runes",
		"Ammo",
		"Summoning pouches",
		"Capes",
		"Jewelry",
		"Quest items",
		"Skilling stuff 1",
		"Skilling stuff 2",
		"Heblore Secundaries 1",
		"Heblore Secundaries 2",
		"Back" };

	private static int[] SHOPS_IDS = new int[]
	{ 1200, 1201, 500, 501, 1202, 1203, 1205, 1207, 1208, 1209, 1210, 1211, 1212, 1213, 1214, 1215, 1216, 1217, 1218, 1219, -1 };

	public static int[] MANAGER_NPC_IDS = new int[]
	{ 16180 };
	public static String[] MANAGER_NPC_TEXTS = new String[]
	{ "I seek the evil power!", "I smell the darkness...", "I sense the darkness...", "Evil forces are getting stronger...", "Come to me, traveler!" };

	private static String[] NEWBIE_LOC_NAMES = new String[]
	{ "Stronghold of security", "Karamja & Crandor", "Rock Crabs", "Back" };
	private static WorldTile[] NEWBIE_LOCATIONS = new WorldTile[]
	{ new WorldTile(3080, 3418, 0), new WorldTile(2861, 9570, 0), new WorldTile(2674, 3710, 0), null };

	private static String[] CITIES_NAMES = new String[]
	{
		"Lumbridge",
		"Varrock",
		"Edgeville",
		"Falador",
		"Seer's village",
		"Ardougne",
		"Yannile",
		"Keldagrim",
		"Dorgesh-Kan",
		"Lletya",
		"Etceteria",
		"Daemonheim",
		"Canifis",
		"Tzhaar area",
		"Burthrope",
		"Al-Kharid",
		"Draynor village",
		"Zanaris",
		"Shilo village",
		"Darkmeyer",
		"Back" };
	private static WorldTile[] CITIES_LOCATIONS = new WorldTile[]
	{
		new WorldTile(3222, 3219, 0),
		new WorldTile(3212, 3422, 0),
		new WorldTile(3094, 3502, 0),
		new WorldTile(2965, 3386, 0),
		new WorldTile(2725, 3491, 0),
		new WorldTile(2662, 3305, 0),
		new WorldTile(2605, 3093, 0),
		new WorldTile(2845, 10210, 0),
		new WorldTile(2720, 5351, 0),
		new WorldTile(2341, 3171, 0),
		new WorldTile(2614, 3894, 0),
		new WorldTile(3450, 3718, 0),
		new WorldTile(3496, 3489, 0),
		new WorldTile(4651, 5151, 0),
		new WorldTile(2889, 3528, 0),
		new WorldTile(3275, 3166, 0),
		new WorldTile(3079, 3250, 0),
		new WorldTile(2386, 4458, 0),
		new WorldTile(2849, 2958, 0),
		new WorldTile(3613, 3371, 0),
		null };

	private static String[] DUNGEON_NAMES = new String[]
	{
		"God Wars",
		"King black dragon",
		"Corporeal beast",
		"Tormented demons",
		"Stronghold of security",
		"Karamja & Crandor",
		"Brimhaven dungeon",
		"TzHaar",
		"Jungle Strykewyrms",
		"Desert Skrykewyrms",
		"Ice Strykewyrms",
		"Kalphite hive",
		"Asgarnia ice dungeon",
		"Mos le harmless jungle",
		"Gorak",
		"Lumbridge swamp caves",
		"Grotworm lair (QBD)",
		"Fremennik slayer dungeon",
		"Lumbridge Catacombs",
		"Back" };
	private static WorldTile[] DUNGEON_LOCATIONS = new WorldTile[]
	{
		new WorldTile(2908, 3707, 0),
		new WorldTile(3051, 3519, 0),
		new WorldTile(2966, 4383, 2),
		new WorldTile(2562, 5739, 0),
		new WorldTile(3080, 3418, 0),
		new WorldTile(2861, 9570, 0),
		new WorldTile(2745, 3152, 0),
		new WorldTile(4673, 5116, 0),
		new WorldTile(2450, 2898, 0),
		new WorldTile(3381, 3162, 0),
		new WorldTile(3508, 5516, 0),
		new WorldTile(3228, 3106, 0),
		new WorldTile(3010, 3150, 0),
		new WorldTile(3731, 3039, 0),
		new WorldTile(3035, 5346, 0),
		new WorldTile(3169, 3171, 0),
		new WorldTile(2990, 3237, 0),
		new WorldTile(2794, 3615, 0),
		new WorldTile(2972, 5565, 0),
		null };

	private static String[] MINIGAMES_NAMES = new String[]
	{
		"Duel arena",
		"Dominion tower",
		"God Wars",
		"Barrows",
		"Fight pits",
		"Fight caves",
		"Kiln",
		"Puro-puro",
		"Clan wars & Stealing creations",
		"High & Low runespan",
		"Sorceror's garden",
		"Crucible",
		"Pest Control",
		"Back" };
	private static WorldTile[] MINIGAMES_LOCATIONS = new WorldTile[]
	{
		new WorldTile(3370, 3270, 0),
		new WorldTile(3361, 3082, 0),
		new WorldTile(2857, 3573, 0),
		new WorldTile(3565, 3306, 0),
		new WorldTile(4602, 5062, 0),
		new WorldTile(4615, 5129, 0),
		new WorldTile(4743, 5170, 0),
		new WorldTile(2428, 4441, 0),
		new WorldTile(2961, 9675, 0),
		new WorldTile(3106, 3160, 0),
		new WorldTile(3323, 3139, 0),
		new WorldTile(3120, 3519, 0),
		new WorldTile(2659, 2676, 0),
		null };

	private static String[] OTHER_NAMES = new String[]
	{ "Mages bank", "Multi pvp (Wilderness)", "Wests (Wilderness)", "Easts (Wilderness)", "Oracle of darkness (Wilderness)", "Back" };
	private static WorldTile[] OTHER_LOCATIONS = new WorldTile[]
	{ new WorldTile(2538, 4715, 0), new WorldTile(3240, 3611, 0), new WorldTile(2984, 3596, 0), new WorldTile(3360, 3658, 0), new WorldTile(3194, 3922, 0), null };

	/**
	 * Whether task was submitted.
	 */
	private static boolean eventTaskSubmitted;
	/**
	 * Current surprise event.
	 */
	private static SurpriseEvent surpriseEvent;
	/**
	 * Whether event is happening.
	 */
	private static boolean tileEventHappening;
	/**
	 * The location of event.
	 */
	private static WorldTile eventTile;
	/**
	 * The invite text of event.
	 */
	private static String eventText;


	public static synchronized void startEvent(String text, WorldTile tile, SurpriseEvent event) {
		if (!eventTaskSubmitted) {
			eventTaskSubmitted = true;
			GameExecutorManager.fastExecutor.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						if (tileEventHappening) {
							for (NPC npc : World.getNPCs()) {
								if (npc == null || npc.isDead() || npc.getNextForceTalk() != null)
									continue;
								int deltaX = npc.getX() - eventTile.getX();
								int deltaY = npc.getY() - eventTile.getY();
								if (npc.getPlane() == eventTile.getPlane() && !(deltaX < -25 || deltaX > 25 || deltaY < -25 || deltaY > 25))
									continue;
								if (Utils.random(10) != 0)
									continue;
	
								String message = "An event: " + eventText + " is currently happening! Talk toWizard Drakzis to get there!";
								if (isEconomyManagerNpc(npc.getId()))
									message = message.replace("Wizard Drakzis", "me");
								npc.setNextForceTalk(new ForceTalk(message));
							}
						}
						else if (surpriseEvent != null) {
							for (NPC npc : World.getNPCs()) {
								if (npc == null || npc.isDead() || npc.getNextForceTalk() != null)
									continue;
								if (Utils.random(10) != 0)
									continue;
	
								String message = "An event: " + eventText + " is currently happening! Talk to Wizard Drakzis to get there!";
								if (isEconomyManagerNpc(npc.getId()))
									message = message.replace("Wizard Drakzis", "me");
								npc.setNextForceTalk(new ForceTalk(message));
							}
						}
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 0, 600);
		}

		eventText = text;
		if (tile != null) {
			tileEventHappening = true;
			eventTile = tile;
		}
		else {
			surpriseEvent = event;
			event.start();
		}
	}

	public static synchronized void stopEvent() {
		tileEventHappening = false;
		surpriseEvent = null;
	}

	public static boolean isEconomyManagerNpc(int id) {
		for (int i = 0; i < MANAGER_NPC_IDS.length; i++)
			if (MANAGER_NPC_IDS[i] == id)
				return true;
		return false;
	}

	private static void sendOptionsInterface(Player player) {
		player.getInterfaceManager().sendCentralInterface(216);
		player.getPackets().sendHideIComponent(216, 26, true);
	}

	public static void setupInterface(Player player, String[] options) {
		for (int i = 0; i < ROOT_COMPONENTS.length; i++) {
			if (options[i] == null) {
				player.getPackets().sendHideIComponent(216, ROOT_COMPONENTS[i], true);
			} else {
				player.getPackets().sendHideIComponent(216, ROOT_COMPONENTS[i], false);
				player.getPackets().sendIComponentText(216, TEXT_COMPONENTS[i], options[i]);
			}
		}
	}

	public static void processManagerNpcClick(final Player player, final int npcId) {
		if (!player.getBank().hasVerified(11))
			return;
		player.getDialogueManager().startDialogue(new Dialogue() {
			private int pageId = 0;
			private String[] currentOptions;
			private int currentOptionsOffset;

			@Override
			public void start() {
				sendOptionsInterface(player);
				setTitlePage();
			}

			@Override
			public void run(int interfaceId, int componentId) {
				int buttonId = -1;
				for (int i = 0; i < CLICK_COMPONENTS.length; i++) {
					if (componentId == CLICK_COMPONENTS[i]) {
						buttonId = i;
						break;
					}
				}

				if (currentOptions == null || buttonId == -1)
					return;

				int length = currentOptions.length - currentOptionsOffset;
				if (currentOptionsOffset != 0 || length > ROOT_COMPONENTS.length) {
					if (buttonId >= 0 && buttonId <= (ROOT_COMPONENTS.length - 2)) {
						if ((buttonId + currentOptionsOffset) >= currentOptions.length || currentOptions[buttonId + currentOptionsOffset] == null)
							return;
						handlePage(currentOptionsOffset + buttonId);
					} else {
						// more button
						if ((currentOptionsOffset + (ROOT_COMPONENTS.length - 1)) >= currentOptions.length) {
							currentOptionsOffset = 0;
						} else {
							currentOptionsOffset += ROOT_COMPONENTS.length - 1;
						}
						updateCurrentPage();
					}
				} else {
					if ((buttonId + currentOptionsOffset) >= currentOptions.length || currentOptions[buttonId + currentOptionsOffset] == null)
						return;
					handlePage(currentOptionsOffset + buttonId);
				}
			}

			private void setPage(int page, String tip, String... options) {
				pageId = page;
				currentOptions = options;
				currentOptionsOffset = 0;
				sendEntityDialogueNoContinue(player, Dialogue.IS_NPC, "Wizard Drakzis", npcId, 9810, tip);
				updateCurrentPage();
			}

			private void updateCurrentPage() {
				String[] buffer = new String[ROOT_COMPONENTS.length];
				int length = currentOptions.length - currentOptionsOffset;
				if (currentOptionsOffset != 0 || length > ROOT_COMPONENTS.length) {
					System.arraycopy(currentOptions, currentOptionsOffset, buffer, 0, Math.min(length, ROOT_COMPONENTS.length - 1));
					buffer[ROOT_COMPONENTS.length - 1] = "More"; // copy up to (len-1) options + more button
				} else {
					System.arraycopy(currentOptions, currentOptionsOffset, buffer, 0, length);
				}

				setupInterface(player, buffer);
			}

			private void handlePage(int optionId) {
				if (pageId == 0) { // title page
					if (optionId == 0) // information & links
						setPage(1, "This section contains links to our Website. If you are beginner, it is strongly advisted to read our beginners guide.", "Website & Forums", "Wiki", "Beginners guide", "Back") ;
					else if (optionId == 1) // Account & character management.
						setManagementPage();
					else if (optionId == 2) // Teleports
						setTeleportsTitlePage();
					else if (optionId == 3) { // Dungeoneering
						player.setNextGraphics(new Graphics(3224));
						Magic.sendTeleportSpell(player, 17108, -2, 3225, 3019, 1, 0, new WorldTile(3448, 3698, 0), 18, true, 0);
					} else if (optionId == 4) // Shops
						setPage(4, "Here you can access various global shops.", SHOPS_NAMES);
					else if (optionId == 5) // Vote
						player.getPackets().sendOpenURL(Settings.VOTE_LINK);
					else if (optionId == 6) // Donate
						player.getPackets().sendOpenURL(Settings.STORE_LINK);
					else if (optionId == 7) { // Ticket
						if (player.isMuted()) {
							player.getPackets().sendGameMessage("You can't submit ticket when you are muted.");
							return;
						}
						end();
						player.getDialogueManager().startDialogue("TicketDialouge");
					} else if (optionId == 8) // nevermind
						end();
				} else if (pageId == 1) { // information & links
					if (optionId == 0)
						player.getPackets().sendOpenURL(Settings.WEBSITE_LINK);
					else if (optionId == 1)
						player.getPackets().sendOpenURL(Settings.FORUMS_LINK);
					else if (optionId == 2)
						player.getPackets().sendOpenURL(Settings.FORUMS_LINK);
					else if (optionId == 3)
						setTitlePage();
				} else if (pageId == 2) { // character management
					if (optionId == 0) { // change your password
//						player.getPackets().sendOpenURL(Settings.PASSWORD_LINK);
					} else if (optionId == 1) { // auth forum acc
						player.getTemporaryAttributtes().put("forum_authuserinput", true);
						player.getPackets().sendInputLongTextScript("Enter your forum username:");
					} else if (optionId == 2) { // display name
						setPage(10, "Here you can set your display name or remove it.", "Set display name", "Remove display name", "Back");
					} else if (optionId == 3) { // switch items look
						player.switchItemsLook();
						setManagementPage();
					} else if (optionId == 4) { // title select
						String[] page = getTitlesPage();
						setPage(11, "Here you can set your title, which will be displayed before or after your characters name.", page);
					} else if (optionId == 5) { // lock xp
						player.setXpLocked(!player.isXpLocked());
						setManagementPage();
					} else if (optionId == 6) { // toogle yellf
						player.setYellOff(!player.isYellOff());
						setManagementPage();
					} else if (optionId == 7) { // set yell color
						if (!player.isGoldMember()) {
							player.getPackets().sendGameMessage("This feature is only available to extreme donators!");
							return;
						}
						player.getTemporaryAttributtes().put("yellcolor", Boolean.TRUE);
						player.getPackets().sendInputLongTextScript("Please enter the yell color in HEX format.");
					} else if (optionId == 8) { // set baby troll name
						if (!player.isGoldMember()) {
							player.getPackets().sendGameMessage("This feature is only available to extreme donators!");
							return;
						}
						player.getTemporaryAttributtes().put("change_troll_name", true);
						player.getPackets().sendInputLongTextScript("Enter your baby troll name (type none for default):");
					} else if (optionId == 9) { // redesign character
						if (!player.isGoldMember()) {
							player.getPackets().sendGameMessage("This feature is only available to extreme donators!");
							return;
						}
						end();
						PlayerLook.openCharacterCustomizing(player);
					} else if (optionId == 10) { // back
						setTitlePage();
					}
				} else if (pageId == 3) { // teleports
					if (optionId == 0) { // current event
						if (tileEventHappening) {
							Magic.sendNormalTeleportSpell(player, 0, 0, eventTile);
						}
						else if (surpriseEvent != null) {
							end();
							surpriseEvent.tryJoin(player);
						}
						else {
							player.getPackets().sendGameMessage("No official event is currently happening.");
						}
					} else if (optionId == 1) { // current starter town
						Magic.sendNormalTeleportSpell(player, 0, 0, Settings.STARTER_LOCATION);
					} else if (optionId == 2) { // safe pvp
						end();
						player.setNextWorldTile(new WorldTile(2815, 5511, 0));
						player.getControlerManager().startControler("clan_wars_ffa", false);
					} else if (optionId == 3) { // Combat training spots
						setPage(12, "This section contains various teleports to locations recommended for beginners.", NEWBIE_LOC_NAMES);
					} else if (optionId == 4) { // cities & towns
						setPage(13, "This section contains teleports to various cities & towns.", CITIES_NAMES);
					} else if (optionId == 5) { // dungeons & pvm
						setPage(14, "This section contains teleports to various pvm locations.", DUNGEON_NAMES);
					} else if (optionId == 6) { // minigames
						setPage(15, "This section contains teleports to various minigames locations.", MINIGAMES_NAMES);
					} else if (optionId == 7) { // others
						setPage(16, "This section contains various miscellaneous teleports.", OTHER_NAMES);
					} else if (optionId == 8) { // back
						setTitlePage();
					}
				} else if (pageId == 4) { // shops
					int shopId = SHOPS_IDS[optionId];
					if (shopId < 0) { // back
						setTitlePage();
					} else {
						end();
						ShopsHandler.openShop(player, shopId);
					}
				} else if (pageId == 10) { // display name management
					if (optionId == 0) { // set display name
						if (!player.isBronzeMember()) {
							player.getPackets().sendGameMessage("This feature is only available to donators!");
							return;
						}
						player.getTemporaryAttributtes().put("setdisplay", Boolean.TRUE);
						player.getPackets().sendInputLongTextScript("Enter display name you want to be set:");
					} else if (optionId == 1) { // remove display name
						LoginClientChannelManager.sendReliablePacket(LoginChannelsPacketEncoder.encodeAccountVarUpdate(player.getUsername(), LoginProtocol.VAR_TYPE_DISPLAY_NAME, Utils.formatPlayerNameForDisplay(player.getUsername())).getBuffer());
					} else if (optionId == 2) { // back
						setManagementPage();
					}
				} else if (pageId == 11) { // titles page
					int[] ids = getTitlesIds();
					if (currentOptions.length != ids.length) {
						// error
						setManagementPage();
						return;
					}

					int titleId = ids[optionId];
					if (titleId == -2) { // back button
						setManagementPage();
					} else if (titleId == -1) { // no title
						player.getAppearence().setTitle(0);
						setManagementPage();
					} else if (titleId > 0) {
						player.getAppearence().setTitle(titleId);
						setManagementPage();
					} else {
						setManagementPage();
					}
				} else if (pageId == 12) { // newbie teles
					if (NEWBIE_LOCATIONS[optionId] == null) { // back
						setTeleportsTitlePage();
					} else {
						Magic.sendLunarTeleportSpell(player, 0, 0, NEWBIE_LOCATIONS[optionId]);
					}
				} else if (pageId == 13) { // teleports cities & towns
					if (CITIES_LOCATIONS[optionId] == null) { // back
						setTeleportsTitlePage();
					} else {
						Magic.sendLunarTeleportSpell(player, 0, 0, CITIES_LOCATIONS[optionId]);
					}
				} else if (pageId == 14) { // dungeons
					if (DUNGEON_LOCATIONS[optionId] == null) { // back
						setTeleportsTitlePage();
					} else {
						if (DUNGEON_NAMES[optionId].contains("(GWD)")) {
							player.setNextWorldTile(DUNGEON_LOCATIONS[optionId]);
							player.stopAll();
							player.getControlerManager().startControler("GodWars");
						} else {
							Magic.sendLunarTeleportSpell(player, 0, 0, DUNGEON_LOCATIONS[optionId]);
						}
					}
				} else if (pageId == 15) { // minigames
					if (MINIGAMES_LOCATIONS[optionId] == null) { // back
						setTeleportsTitlePage();
					} else {
						Magic.sendLunarTeleportSpell(player, 0, 0, MINIGAMES_LOCATIONS[optionId]);
					}
				} else if (pageId == 16) { // others
					if (OTHER_LOCATIONS[optionId] == null) { // back
						setTeleportsTitlePage();
					} else {
						Magic.sendLunarTeleportSpell(player, 0, 0, OTHER_LOCATIONS[optionId]);
						if (OTHER_NAMES[optionId].contains("(Wilderness")) {
							player.getControlerManager().startControler("Wilderness");
						}
					}
				} else if (pageId == 99) { // temp page
					setTeleportsTitlePage();
				}
			}

			private void setTitlePage() {
				setPage(0, "Welcome to " + Settings.SERVER_NAME + "!<br>I provide various services to make your life here easier.", "Information & Links", "Account & Character management", (tileEventHappening || surpriseEvent != null) ? "Teleports (Click here for event)" : "Teleports", "Dungeoneering", "Shops", "Vote", "Donate", "Submit a ticket", "Close");
			}

			private void setManagementPage() {
				setPage(2, "This section contains features, which will help you to manage your account easier.", "Change password", "Authenticate your forum account", "Display name management", player.isOldItemsLook() ? "Switch to new items look" : "Switch to old items look", "Set your title", player.isXpLocked() ? "Unlock XP" : "Lock XP", player.isYellOff() ? "Toogle yell on" : "Toogle yell off", "Set yell color", "Set baby troll name", "Redesign character", "Back");
			}

			private void setTeleportsTitlePage() {
				setPage(3, "This section contains teleports to various different locations.", "Current event", "Home", "Safe PvP", "Combat training spots", "Cities & Towns", "Dungeons & PVM Locations", "Minigames", "Others", "Back");
			}

			private String[] getTitlesPage() {
				String[] buffer = new String[102];
				int count = 0;

				buffer[count++] = "No title";

				ClientScriptMap map = player.getAppearence().isMale() ? ClientScriptMap.getMap(1093) : ClientScriptMap.getMap(3872);
				for (Object value : map.getValues().values()) {
					if (value instanceof String && ((String) value).length() > 0) {
						buffer[count++] = (String) value;
					}

					if (count >= (buffer.length - 2))
						break;
				}

				buffer[count++] = "Back";

				if (count != buffer.length) {
					String[] rebuff = new String[count];
					System.arraycopy(buffer, 0, rebuff, 0, rebuff.length);
					return rebuff;
				} else {
					return buffer;
				}
			}

			private int[] getTitlesIds() {
				int[] buffer = new int[102];
				int count = 0;

				buffer[count++] = -1;

				ClientScriptMap map = player.getAppearence().isMale() ? ClientScriptMap.getMap(1093) : ClientScriptMap.getMap(3872);
				for (Object value : map.getValues().values()) {
					if (value instanceof String && ((String) value).length() > 0) {
						buffer[count++] = (int) map.getKeyForValue(value);
					}

					if (count >= (buffer.length - 2))
						break;
				}

				buffer[count++] = -2;

				if (count != buffer.length) {
					int[] rebuff = new int[count];
					System.arraycopy(buffer, 0, rebuff, 0, rebuff.length);
					return rebuff;
				} else {
					return buffer;
				}
			}

			@Override
			public void finish() {
				closeNoContinueDialogue(player);
				player.getInterfaceManager().removeCentralInterface();

			}

		});
	}

	public static final void processStorePurchase(final Player player, String item) {
		if (item.equals("Random nex set")) {
			int[][] sets = new int[][]
			{ new int[]
			{ 20159, 20163, 20167 }, new int[]
			{ 20147, 20151, 20155 }, new int[]
			{ 20135, 20139, 20143 } };
			int[] set = sets[Utils.random(sets.length)];
			for (int itemid : set)
				player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Random chaotic item")) {
			int[] items = new int[]
			{ 18349, 18351, 18353, 18355, 18357, 18359, };
			int itemid = items[Utils.random(items.length)];
			player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Random spirit shield")) {
			int[] items = new int[]
			{ 13738, 13740, 13742, 13744 };
			int itemid = items[Utils.random(items.length)];
			player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Random godsword")) {
			int[] items = new int[]
			{ 11694, 11696, 11698, 11700 };
			int itemid = items[Utils.random(items.length)];
			player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Random partyhat")) {
			int[] items = new int[]
			{ 1038, 1040, 1042, 1044, 1046, 1048 };
			int itemid = items[Utils.random(items.length)];
			player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Random haloween mask")) {
			int[] items = new int[]
			{ 1053, 1055, 1057, };
			int itemid = items[Utils.random(items.length)];
			player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Experience (Random skill)")) {
			int skill = Utils.random(Skills.SKILL_NAME.length);
			player.getSkills().addXpStore(skill, 3000000.0D);
		} else if (item.equals("All barrows sets")) {
			int[] items = new int[]
			{ 11846, 11848, 11850, 11852, 11854, 11856 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Bandos set (With godsword)")) {
			int[] items = new int[]
			{ 11696, 11724, 11726, 11728 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Armadyl set (With godsword)")) {
			int[] items = new int[]
			{ 11694, 11718, 11720, 11722 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Divine spirit shield")) {
			int[] items = new int[]
			{ 13740 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Dragon claws")) {
			int[] items = new int[]
			{ 14484 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Abyssal whip")) {
			int[] items = new int[]
			{ 4151 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Coins")) {
			int[] items = new int[]
			{ 995 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 100000000);
//		} else if (item.equals("Vote tokens")) {
//			int[] items = new int[]
//			{ Settings.VOTE_TOKENS_ITEM_ID };
//			for (int itemid : items)
//				player.getInventory().addItemDrop(itemid, 10000000);
		} else if (item.equals("Fire cape")) {
			player.setCompletedFightCaves();
			int[] items = new int[]
			{ 6570 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 1);
		} else if (item.equals("Kiln cape")) {
			player.setCompletedFightKiln();
			int[] items = new int[]
			{ 23659 };
			for (int itemid : items)
				player.getInventory().addItemDrop(itemid, 1);
		}

		else if (item.startsWith("vote_tokens:")) {
			if (!player.hasVotedInLast12Hours())
				player.setVoteCount(0);
			int votes = player.getVoteCount();
			if (votes >= 3) {
				player.getPackets().sendGameMessage("You may only claim a vote three times a day. This auth has been terminated.");
				player.getPackets().sendGameMessage("For more news please refer to ::thread 75672.");
				return;
			}
			player.setVoteCount(player.getVoteCount() + 1);
			int amount = Integer.parseInt(item.substring(12));
//			Item tokens = new Item(Settings.VOTE_TOKENS_ITEM_ID, amount);
//			if (player.getBank().addItems(new Item[] {tokens}, true) == 0)
//				player.getInventory().addItemDrop(tokens.getId(), tokens.getAmount());
//			if (amount >= Settings.VOTE_MIN_AMOUNT)
//				player.refreshLastVote();
			World.sendNews(player, Utils.formatPlayerNameForDisplay(player.getDisplayName()) + " has just voted and received " + amount + " vote tokens! (::vote)", 0);
		} else {
			player.getPackets().sendGameMessage("Unknown purchase:" + item);
		}
	}
}
