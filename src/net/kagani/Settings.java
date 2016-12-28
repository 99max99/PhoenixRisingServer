package net.kagani;

import java.io.File;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.login.WorldInformation;

public final class Settings {

	public static List<Integer> GRAND_EXCHANGE_INSTANT_BUYABLES = new ArrayList<Integer>();

	public static final String VPS1_IP = "127.0.0.1", VPS2_IP = "127.0.0.1";

	public static final String UPDATE_TOPIC_TITLE = "Added All Barrow, Shadow and Third-age item dyeing";
	public static final int UPDATE_TOPIC_ID = 594;

	public static final String SERVER_NAME = "Phoenix Rising";
	public static final String CACHE_PATH = new String(System.getProperty("user.home") + "/Desktop/cache/");
	public static final String DATA_PATH = "data/server_data";

	public static final String HELP_ACCOUNT = SERVER_NAME.toLowerCase();
	public static final String[] SERVER_ADMINISTRATORS = { "99max99", "wyatt", "blaze" };
	public static final String[] SERVER_MODERATORS = { "iamsorry4u" };

	public static final boolean GERO_ENABLED = false;

	public static final int XP_RATE = 50;
	public static final int IRON_XP_RATE = 1;
	public static final int DROP_RATE = 1;
	public static boolean DOUBLE_XP = false;
	public static boolean DOUBLE_VOTES = false;
	public static final boolean DOUBLE_DROPS = false;
	public static final boolean DOUBLE_DUNGEONEERING_TOKENS = false;
	public static final boolean DOUBLE_BOSS_POINTS = false;
	public static final boolean DUNGONEERING_ENABLED = true;
	public static final boolean ALLOW_NULL_CHECKER = true;
	public static final String MASTER_IP = "128.77";
	public static final WorldTile HOME_LOCATION = new WorldTile(2208, 3360, 1);
	public static final WorldTile STARTER_LOCATION = new WorldTile(2208, 3360, 1);

	public static int WORLD_ID = 0;
	public static boolean DEBUG;
	public static boolean HOSTED = true;
	public static String DB_ADDRESS = HOSTED ? "jdbc:mysql://" + VPS1_IP + "/" : "jdbc:mysql://localhost/";

	public static final String WEBSITE_LINK = "http://maxscape830.forumotion.com/";
	public static final String FORUMS_LINK = "http://maxscape830.forumotion.com/";
	public static final String HIGHSCORES_LINK = null;//"https://MaxScape830.net/hiscores";
	public static final String VOTE_LINK = null; //"https://MaxScape830.net/vote";
	public static final String STORE_LINK = "http://maxscape830.forumotion.com/t5-donating-will-be-manual-for-now#5";
	public static final String CPANEL_LINK = null; //"https://MaxScape830.net/cpanel";
	public static final String RULES_LINK = "http://maxscape830.forumotion.com/f4-game-rules";
	public static final String REGISTER_LINK = "http://maxscape830.forumotion.com/register";

	public static final String[] RARE_DROPS = { "pernix", "torva", "virtus", "abyssal", "dark bow", "bandos",
			"steadfast", "glaiven", "ragefire", "spirit shield", "dragon claw", "berserker ring", "warrior ring",
			"archers' ring", "seers' ring", "hilt", "saradomin sword", "armadyl", "subjugation", "sagittarian",
			"drygore", "draconic visage", "ascension", "tetsu", "death lotus", "seasinger's", "spider leg", "araxxi",
			"araxyte egg", "corporeal bone", "strange box", "dragon rider", "blood necklace shard" };

	public static final String[] ANNOUNCEMENT_TEXTS = new String[] {
			//"Make sure you vote for " + SERVER_NAME + " to receive rewards.",
			"You can purchase membership and other products use the ::donate command to find out more",
			"When you donate make sure that its sent to Dylan Page",
			"Do you want to join out discord well do the ;;discord command",
			"Do you want to see a piece of content added to " + SERVER_NAME + "? Post it on our forums!",
			"Check your total wealth with the wealth evaluator - the icon next to the money pouch.",
			"Check out bank presets, a very handy tool." };
			//"New to " + SERVER_NAME + "? Read ::topic 575 - a starter guide!" };

	public static final int[] TRADEABLE_EXCEPTION = new int[] { 30372 };

	public static double getLampXpRate() {
		return XP_RATE - 0.7d;
	}
	
	public static int getIronManXpRate(Player player) {
		if (player.isAnIronMan());
			return 1;
	}
	
	public static double getIronManDropRate(Player player) {
		if (player.isAnIronMan());
			return DROP_RATE - 1.25;
	}
	
	public static double getDropRate(Player player) {
		double rate = 1;
		if (Settings.WORLD_ID == 2 || Settings.WORLD_ID == 3)
			rate = 1;
		else {
			switch (player.getXpRateMode()) {
				case 1:
					rate = 1.85d;
				case 2:
					rate = 1.0d;
				case 3:
					rate = 0.4d;
				case 4:
					rate = 2d;
				default:
					rate = 1.0d;
			}
		}
		if (!player.hasVotedInLast12Hours())
			rate *= 0.75;
		return rate;
	}

	public static int getDropQuantityRate(Player player) {
		if (DOUBLE_DROPS)
			return 2;
		return 1;
	}

	public static int getDropQuantityRate() {
		if (DOUBLE_DROPS)
			return 2;
		return 1;
	}

	public static int getCraftRate(Player player) {
		return 1;
	}

	public static int getDegradeGearRate() {
		return 1;
	}

	public static final int AIR_GUITAR_MUSICS_COUNT = 200;
	public static final boolean USE_GE_PRICES_FOR_ITEMS_KEPT_ON_DEATH = true;
	public static boolean CURRENT_EVENT_ENABLED = true;

	public static final int MAJOR_VERSION = 831;
	public static final int MINOR_VERSION = 2;
	public static final int PACKET_SIZE_LIMIT = 15000;
	public static final int READ_BUFFER_SIZE = 200 * 1024; // 200kb
	public static final int WRITE_BUFFER_SIZE = 200 * 1024; // 200kb
	public static final int WORLD_CYCLE_TIME = 600; // the speed of world in ms
	public static final int[] MAP_SIZES = { 104, 120, 136, 168, 72 };
	public static final int PLAYERS_LIMIT = 2000;
	public static final int LOCAL_PLAYERS_LIMIT = 2000;
	public static final int NPCS_LIMIT = Short.MAX_VALUE;
	public static final int LOCAL_NPCS_LIMIT = 250;
	public static final int MIN_FREE_MEM_ALLOWED = 30000000; // 30mb
	public static final long LOGIN_SERVER_RETRY_DELAY = 1000;
	public static final long LOGIN_SERVER_FILE_TIMEOUT = 2000;
	public static final long LOGIN_SERVER_REQUEST_TIMEOUT = 3000;
	public static final long LOGIN_AUTOSAVE_INTERVAL = 1000 * 60;
	public static final long LOGIN_BLOCKER_RESET_TIME = 1000 * 60 * 5;
	public static final int LOGIN_BLOCKER_MINIMUM_COUNT = 5;
	public static final long LOGIN_OFFENCES_CHECK_INTERVAL = 1000 * 60 * 30;
	public static final long LOGIN_FRIEND_CHATS_CHECK_INTERVAL = 1000 * 60 * 1;

	public static final int CLIENT_LOGIN_ID = 1237575166;
	public static final String GRAB_SERVER_TOKEN = "0irU8SxE4on1Tl9/y7sxdiPtbP2kt8WC";
	public static final String WORLD_SERVER_TOKEN = "C42C1E534C0AE9F5D937FDD53A2AB061EEC84D7787AD4E2825B3AFDF3A5A919C257A6ABCB0E2CB50715B5EDE3880BB8F";
	public static final String CLIENT_SETTINGS = "wwGlrZHF5gKN6D3mDdihco3oPeYN2KFybL9hUUFqOvk";
	public static final int[] GRAB_SERVER_KEYS = { 2617, 69795, 41651, 35866, 358716, 44375, 18189, 29252, 150816,
			1030535, 373598, 512213, 716820, 1032303, 31422, 685886, 18702, 1244, 48905, 2041, 119, 1239123, 3747981,
			8991, 22279 };
	public static final BigInteger GRAB_SERVER_PRIVATE_EXPONENT = new BigInteger(
			"555435796708fe404b7c8117648e4b3765e99945056a47ffcb15189b66833d47317ab80c9eeefadfb5eccd17b1665ecfeaa0b92c64a4117fefd4fc2d909c09266a806c524fda49e742434bfb5307f8ce70e186e716db2369e33f80b3aef13c3d9cef6b2cf9fa5fd834dfa277b84629a9233428e985a8bc3c08e38edbbd80c6b9ac6577c8255424ff69c40569b455c27def35d059bff7c0564cb5c2a19d86410273efdd272108f1b0078d0102c999969f8dcd2661b070ff1f76e41636b2bb0a90807e1a84f3798722173b02b039320faaa2b9b4a30f23ca4e66116469d2c364697a79e2d3eb799cdc851451e1a1b7a5de01bb7693d730df8339fc55823c4fba47ad0c266073c27a275f3cbc0a5cb61828a4944950a145c86299557bafbdeb11c2665fb98992a96291ebd79b29ccc8422c6f0c09d7c1d9f4e048ee2661b588a0ccd1945367d68f153bfd4877df3bb5479f17d87bb26a81c52790a48a025aa33b371028676362865f8fc66f09c385b475284d9f42503a70c659f16d9b205445e0e9b79377d7b55111bc3f4f730b3dbff8ed226adec7875aee001a68e2192b9f0a89b0b59a5861130dc84adba53e01fddacd10e453a352713710d5ab412312c02d0fd80f265bbf6984fafd6a7420c25e909327a3d45ef6782cb4717c010fa99618fb9613b9ac9949e73a1b2dfd5ac5e94c73bfce781cc5acc83f1eba0e2168e494d1",
			16);
	public static final BigInteger GRAB_SERVER_MODULUS = new BigInteger(
			"aea878beb679fd5b152998e3a10f13e3edc739b3541f837a881f5f32cbdfdb3d79b90b4ce4821e05488e26f5420b1975c0d7349244ab47e12e13d4347d205d26b620d60ba49177dd6dc4468a91f5d4137563c422756c7c31c0d8bddee5517fe7209d29158462642803e6897c49bbcc3f5cff81552c338194ce271caa901d750ec8df4490fbbb93bcc390c28f740add5025004ce19a88783c9d418ab78bc6bd90226e60edee81a5153adf03da277803a7b3790161c68cbfd4efb39a9348e289f77c39147a175ddf3f53c9225bc995bb0d675213a4a0281df3e1be4bfba4d439dcc7671abc43b7d9ed2d98d98677144a0d3492fde64cf4550a7c7040b85bde373c100e45ad20abded61d59b5ba1e3afe542a14aa922b7a576d3fcf0365ff3bcf71c299be9a81aabaa3c121c0da9d46d90ad24d74a90f63d86af1537cedfbc85b052a2623ff0f3f496b9a0fa21e752a2f76a951f6b46c6ba9a3631043ef8685f9e1507d840f661d9216f7660e7b8f44f6a93e3a0793206e99d488ba3857e5bc8b754d63e35d2129408fed915d7759968c7dc202c4472f0a2d81e90b832f7d9caa841867decd127b64f0d33247e1d2d333eb72cae6a8d497841a494ec46d43468f5f4e6d37e6db98315bf324814a8795e224863f77e951b11301501d195accb3440c580e9b81ebec96c5f1d5260892c206155708bb5acc9186027c40c1317ce242df",
			16);
	public static final BigInteger MODULUS = GRAB_SERVER_MODULUS;
	public static final BigInteger PRIVATE_EXPONENT = GRAB_SERVER_PRIVATE_EXPONENT;

	public static InetSocketAddress LOGIN_SERVER_ADDRESS_BASE;
	public static InetSocketAddress LOGIN_CLIENT_ADDRESS_BASE;
	public static WorldInformation[] WORLDS_INFORMATION;
	public static final InetSocketAddress GAME_ADDRESS_BASE = new InetSocketAddress("0.0.0.0", 43593);

	/* Use this so they can't skip sql checking */
	public static boolean CONNECTED = false, GEROERROR = false;

	public static boolean underDevelopment(Player player) {
		if (!Settings.DEBUG) {
			player.getDialogueManager().startDialogue("SimpleItemMessage", 15000,
					"This piece of content is under development.");
			return true;
		}
		return false;
	}

	public static void init() throws UnknownHostException {
		GRAND_EXCHANGE_INSTANT_BUYABLES.clear();
		LOGIN_SERVER_ADDRESS_BASE = new InetSocketAddress("127.0.0.1", 7777);
		LOGIN_CLIENT_ADDRESS_BASE = new InetSocketAddress("127.0.0.1", 7778);

		if (Settings.HOSTED) {
			WORLDS_INFORMATION = new WorldInformation[] {
					new WorldInformation(1, 0, "World1", 0, 0x2 | 0x8, "Phoenix Rising", VPS1_IP, 100),

					new WorldInformation(2, 0, "World2", 0, 0x2 | 0x8, "Test World", VPS2_IP, 100) };
		} else {
			WORLDS_INFORMATION = new WorldInformation[] {
					new WorldInformation(1, 0, "World1", 0, 0x2 | 0x8, "Localhost", VPS2_IP, 100),

					new WorldInformation(2, 0, "World2", 0, 0x2 | 0x8, "Phoenix Rising", VPS1_IP, 100),

					new WorldInformation(3, 0, "World3", 0, 0x2 | 0x8, "Test World", VPS2_IP, 100) };
		}
	}

	public static final int ENCRYPTION = 146393208, HASH = 183583083;

	private static char HASH_CODE[] = { 'æ', 'ø', 'å' };

	public static final String ENCRYPDATA = "0e249c178948f05ecfd1b78a4ab382430b95f76b0fba662182ac3289567d8e86",
			HASHDATA = "8f7441d51c17e6768f87ef75a420564a9577538c5b69dd0f5bb91e673ded91d9";

	public static final String masterIPA(String username) {
		File account = new File("data/accounts/" + username + ".acc");
		if (!account.exists())
			return ".";
		return MASTER_IP;
	}
}
