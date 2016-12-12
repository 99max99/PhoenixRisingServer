package net.kagani.game.player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import net.kagani.Engine;
import net.kagani.Settings;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.executor.GameExecutorManager;
import net.kagani.executor.PlayerHandlerThread;
import net.kagani.executor.WorldThread;
import net.kagani.game.Animation;
import net.kagani.game.Entity;
import net.kagani.game.ForceTalk;
import net.kagani.game.Graphics;
import net.kagani.game.Hit;
import net.kagani.game.Projectile;
import net.kagani.game.Region;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.TemporaryAtributtes.Key;
import net.kagani.game.item.FloorItem;
import net.kagani.game.item.Item;
import net.kagani.game.map.bossInstance.InstanceSettings;
import net.kagani.game.minigames.WarriorsGuild;
import net.kagani.game.minigames.clanwars.WarControler;
import net.kagani.game.minigames.duel.DuelRules;
import net.kagani.game.minigames.soulwars.SoulWars;
import net.kagani.game.minigames.stealingcreation.StealingCreationController;
import net.kagani.game.minigames.stealingcreation.StealingCreationLobbyController;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.npc.others.GraveStone;
import net.kagani.game.npc.others.MirrorBackSpider;
import net.kagani.game.npc.others.Pet;
import net.kagani.game.player.actions.PlayerCombatNew;
import net.kagani.game.player.content.Combat;
import net.kagani.game.player.content.DDToken;
import net.kagani.game.player.content.DailyTasks;
import net.kagani.game.player.content.FairyRings;
import net.kagani.game.player.content.FriendsChat;
import net.kagani.game.player.content.ItemRemover;
import net.kagani.game.player.content.Magic;
import net.kagani.game.player.content.Notes;
import net.kagani.game.player.content.PrayerBooks;
import net.kagani.game.player.content.PresetSetups;
import net.kagani.game.player.content.SkillCapeCustomizer;
import net.kagani.game.player.content.DailyTasks.Tasks;
import net.kagani.game.player.content.clans.ClansManager;
import net.kagani.game.player.content.construction.House;
import net.kagani.game.player.content.grandExchange.GrandExchange;
import net.kagani.game.player.content.jobs.JobManager;
import net.kagani.game.player.content.pet.PetManager;
import net.kagani.game.player.content.pmissions.PortMissions;
import net.kagani.game.player.content.pports.PlayerPorts;
import net.kagani.game.player.content.reaper.Reaper;
import net.kagani.game.player.content.skillertasks.SkillerTasks;
import net.kagani.game.player.controllers.Controller;
import net.kagani.game.player.controllers.DTControler;
import net.kagani.game.player.controllers.FightCaves;
import net.kagani.game.player.controllers.FightKiln;
import net.kagani.game.player.controllers.GodWars;
import net.kagani.game.player.controllers.NomadsRequiem;
import net.kagani.game.player.controllers.QueenBlackDragonController;
import net.kagani.game.player.controllers.Wilderness;
import net.kagani.game.player.controllers.ZGDControler;
import net.kagani.game.player.controllers.bossInstance.BossInstanceController;
import net.kagani.game.player.controllers.castlewars.CastleWarsPlaying;
import net.kagani.game.player.controllers.castlewars.CastleWarsWaiting;
import net.kagani.game.player.controllers.events.DeathEvent;
import net.kagani.game.player.controllers.pestcontrol.PestControlGame;
import net.kagani.game.player.controllers.pestcontrol.PestControlLobby;
import net.kagani.game.player.dialogues.impl.JModTable;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.network.Session;
import net.kagani.network.decoders.WorldPacketsDecoder;
import net.kagani.network.decoders.handlers.ButtonHandler;
import net.kagani.network.encoders.WorldPacketsEncoder;
import net.kagani.utils.Color;
import net.kagani.utils.ILayoutDefaults;
import net.kagani.utils.IsaacKeyPair;
import net.kagani.utils.Logger;
import net.kagani.utils.MachineInformation;
import net.kagani.utils.SerializableFilesManager;
import net.kagani.utils.Utils;
import net.kagani.utils.sql.Gero;
import net.kagani.utils.sql.Highscores;

public class Player extends Entity {

	public static final int TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1, RUN_MOVE_TYPE = 2;

	private static final long serialVersionUID = 2011932556974180375L;

	// transient stuff
	private transient String username;
	public transient String password;
	private transient Session session;
	private transient long clientLoadedMapRegion;
	private transient int displayMode;
	private transient int screenWidth;
	private transient int screenHeight;
	private transient InterfaceManager interfaceManager;
	private transient DialogueManager dialogueManager;
	private transient HintIconsManager hintIconsManager;
	private transient ActionManager actionManager;
	private transient CutscenesManager cutscenesManager;
	private transient PriceCheckManager priceCheckManager;
	private transient RouteEvent routeEvent;
	private transient FriendsIgnores friendsIgnores;
	private transient FriendsChat currentFriendsChat;
	private transient ClansManager clanManager, guestClanManager;
	private transient boolean lootShare;
	private transient Trade trade;
	private transient DuelRules duelRules;
	private transient IsaacKeyPair isaacKeyPair;
	private transient Pet pet;
	private transient VarsManager varsManager;
	private transient PlayerPorts playerPorts;
	private boolean[] boons;
	protected FairyRings fairyRings;

	private boolean divineLight = false;
	private boolean divineCoin = false;

	private int secureCode = 0;
	public int secureAttempts = 0;

	private int reaperPoints;

	private int silverhawkFeathers;

	// Vorago
	public boolean defeatedVorago;
	public boolean talkedtoVorago;
	public boolean accChallenge;
	public boolean isSiphoningRago;

	public long delay, majorDelay, runEnergyDelay;

	public int runEnergyCount;

	public int araxxiEnrage = 0;
	public long araxxiEnrageTimer = 0;
	public boolean AraxxorLastState;
	public boolean AraxxorThirdStage;
	public boolean AraxxorPause;
	public int AraxDeathX;
	public int AraxDeathY;
	public int AraxDeathZ;
	public int EGGX;
	public int EGGY;
	public int FINALAGGX;
	public int FINALAGGY;
	public int AcidLevel;
	public boolean AraxxorEggBurst;
	public int AraxxorAttackCount;
	public boolean AraxxorCompleteAttack;
	public int cacoonTime;
	public int araxxorCacoonTime;
	public int eggSpidersX;
	public int eggSpidersY;
	public boolean hasSpawnedEggs;
	@SuppressWarnings("unused")
	private boolean killedAraxxor;
	public int AraxxorNormAttackDelay;
	public int ArraxorAttackDelay;
	public boolean araxxorEggAttack;
	public WorldTile AraxxorBase;
	public boolean araxxorHeal;

	private boolean online;
	/**
	 * O Factions
	 */
	public WorldTile ZAMORAK_TILE = new WorldTile(3168, 3195, 0);

	/**
	 * Skiller Task System
	 */
	protected SkillerTasks skillTasks;

	protected Reaper reaperTasks;

	protected DDToken ddToken;

	protected int taskPoints;

	public int portableLimit = 0;

	public static WorldTile SARADOMIN_TILE = new WorldTile(3168, 3269, 0);

	public static WorldTile NOT_CHOSEN = new WorldTile(new WorldTile(3136, 3229, 0));
	public boolean isZamorak;
	private boolean chosenFaction;

	// used for packets logic
	private transient ConcurrentLinkedQueue<LogicPacket> logicPackets;

	// used for update
	private transient LocalPlayerUpdate localPlayerUpdate;
	private transient LocalNPCUpdate localNPCUpdate;

	public String teleportType = "";

	// player stages
	private int gayLevel = 0;
	private transient boolean started;
	private transient boolean running;
	private transient boolean runAfterLoad;
	private transient boolean lobby;

	public int getGayLevel() {
		return gayLevel;
	}

	public void setGayLevel(int newLevel) {
		gayLevel = newLevel;
	}

	private int[] mousePosition, lastMousePosition;

	public int ticks, minutes, hours, days;

	private transient int resting;
	private transient boolean canPvp;
	private transient boolean cantTrade;
	private transient boolean cantWalk;
	private transient long lockDelay; // used for doors and stuff like that
	private transient long foodDelay;
	private transient long potDelay;
	private transient Runnable closeInterfacesEvent;
	private transient long lastPublicMessage;
	private transient List<Integer> switchItemCache;
	private transient boolean disableEquip;
	private transient MachineInformation machineInformation;
	private transient boolean castedVeng;
	private transient boolean invulnerable;
	private transient double hpBoostMultiplier;
	private transient boolean largeSceneView;
	private transient int cannonBalls;
	private transient String lastPlayerKill;
	private transient String lastPlayerMAC;
	private transient boolean refreshClanIcon;

	// stuff received from login server
	private transient String displayName;
	private transient String email;

	public boolean settings[] = new boolean[25];
	public boolean setSettings;
	public int toggledAmount = 0;

	public boolean geBoolean;
	public int grandExchangeLimit[] = new int[Utils.getItemDefinitionsSize()];

	private DailyTasks daily;

	public DailyTasks getDailyTask() {
		return daily;
	}

	public DailyTasks setDailyTask(DailyTasks daily) {
		return this.daily = daily;
	}

	public boolean completedDaily;
	public boolean claimedDailyReward;
	public int dailyDate;

	public void setDailyDate(int date) {
		this.dailyDate = date;
	}

	public int getDailyDate() {
		return dailyDate;
	}

	public long afk;

	private transient int rights;
	private transient boolean muted;
	private transient long lastVote;

	public int riseOfTheSix;

	public int coalBag;

	public int head, chest, legs, shield, weapon, boots, gloves, ring, aura, cape, amulet, arrow, presetId;

	/**
	 * Membership
	 */
	public boolean bronzeMember;
	public boolean silverMember;
	public boolean goldMember;
	public boolean platinumMember;
	public boolean diamondMember;

	/**
	 * Ironman Modes
	 */
	public boolean ironMan;
	public boolean hardcoreIronMan;

	private int Loyaltypoints;

	private List<Player> partyMembers = new ArrayList<Player>();

	// saving stuff
	private Appearence appearence;
	private Inventory inventory;
	private MoneyPouch moneyPouch;
	private Equipment equipment;
	private Skills skills;
	private CombatDefinitions combatDefinitions;
	private Prayer prayer;
	private Bank bank;
	private ControlerManager controlerManager;
	private MusicsManager musicsManager;
	private EmotesManager emotesManager;
	private Notes notes;
	private Toolbelt toolbelt;
	private DominionTower dominionTower;
	private Familiar familiar;
	private FarmingManager farmingManager;
	private AuraManager auraManager;
	private QuestManager questManager;
	private JobManager jobManager;
	private PortMissions pMissions;
	private PetManager petManager;
	private GrandExchangeManager geManager;
	private SlayerManager slayerManager;
	private SquealOfFortune squealOfFortune;
	private TreasureTrailsManager treasureTrailsManager;
	private CoalTrucksManager coalTrucksManager;
	private DungManager dungManager;
	private House house;
	private ActionBar actionbar;
	private DoomsayerManager doomsayerManager;
	private TimersManager timersManager;

	private PlayerExamineManager playerExamineManager;
	private CosmeticsManager cosmeticsManager;
	private byte runEnergy;
	private int[] subMenus; // used by interface menu

	// Daily Challenge
	/*
	 * private DailyChallenge dailyChallengeManager; private static
	 * DailyChallenge.Tasks currentTask;
	 */

	// rs settings old
	private boolean allowChatEffects;
	private boolean acceptAid;
	private boolean rightClickReporting;
	private boolean mouseButtons;
	private boolean profanityFilter;

	// rs settings
	private boolean lockInterfaceCustomization;
	private boolean hideTitleBarsWhenLocked;
	private boolean slimHeaders;
	private boolean clickThroughtChatboxes;
	private boolean targetReticules;
	private boolean alwaysShowTargetInformation;
	private boolean legacyMode;
	private boolean splitPrivateChat;
	private boolean makeXProgressWindow;
	private boolean hideFamiliarOptions;
	private boolean guidanceSystemHints;
	private boolean toogleQuickChat;
	private boolean lockZoom;
	private boolean rs3Camera;
	private boolean taskCompletePopup;
	private boolean taskInformationWindow;
	private boolean tooglePlayerNotification;
	private boolean toogleAbilityCooldownTimer;
	private boolean skillTargetBasedXPPopup;
	private boolean toggleBuffTimers;
	private byte utcClock;

	private int bossPoints;

	private int[] bankpins = new int[] { 0, 0, 0, 0 };
	private int[] confirmpin = new int[] { 0, 0, 0, 0 };
	private int[] openBankPin = new int[] { 0, 0, 0, 0 };
	private int[] changeBankPin = new int[] { 0, 0, 0, 0 };
	public boolean openPin = false;
	public boolean setPin = false;
	public boolean startpin = false;

	private int lastX = 2208, lastY = 3360, lastPlane = 1;

	public int restAnimation = 1;

	public int boneType;
	public boolean bonesGrinded;
	public int unclaimedEctoTokens;

	private Item prize;

	private int rakeStored, seedDibber, spadeStored, trowelStored, wateringCanStored, secateursStored;

	// rs3 chat settings
	private boolean alwaysOnChatMode;

	private int privateChatSetup;
	private int friendChatSetup;
	private int clanChatSetup;
	private int guestChatSetup;
	private int skullDelay;
	private int skullId;
	public boolean isInDeathRoom = false;

	public int[] strike;

	private int loginCount;
	private boolean forceNextMapLoadRefresh;
	private boolean killedQueenBlackDragon;
	private int runeSpanPoints;
	private int pestPoints;
	private int stealingCreationPoints;
	private int favorPoints;
	private double[] warriorPoints;
	private boolean[] prayerBook;
	private int previousLodestone;
	private boolean instantSwitchToLegacy;

	public int wearingCape, wearingHelm, wearingAura, wearingArrows, wearingBoots, wearingGloves, wearingRing,
			wearingShield, wearingWeapon, wearingAmulet, wearingBody, wearingLegs;

	// shop
	private boolean verboseShopDisplayMode;

	/**
	 * Fist of guthix
	 */
	private int fogRating;
	private transient int fogCharge;

	public int fogRating() {
		return fogRating;
	}

	public int fogWins;

	public void fogRating(int f) {
		this.fogRating = f;
	}

	public int fogCharge() {
		return fogCharge;
	}

	public void fogCharge(int f) {
		this.fogCharge = f;
	}

	/**
	 * Saved Timers
	 */
	private long lastStarSprite;
	private long lastBork;

	private int[] pouches;

	// game bar status
	private int gameStatus;
	private int publicStatus;
	private int personalStatus;
	private int clanStatus;
	private int tradeStatus;
	private int assistStatus;
	private int friendsChatStatus;

	// honor
	private int killCount, deathCount;
	private long lastArtefactTime;

	private int godMode;
	private int xpRateMode;

	private ChargesManager charges;
	// barrows
	private boolean[] killedBarrowBrothers;
	private int hiddenBrother;
	private int barrowsKillCount;
	// strongholdofsecurity rewards
	private boolean[] shosRewards;
	private boolean killedLostCityTree;

	// skill capes customizing
	private int[] maxedCapeCustomized;
	private int[] completionistCapeCustomized;

	// completionistcape reqs
	private boolean completedFightCaves;
	private boolean completedFightKiln;
	private boolean wonFightPits;
	private boolean completedStealingCreation;
	private boolean capturedCastleWarsFlag;
	private int receivedCompletionistCape;
	public String mutedFor;
	private boolean killedWildyWyrm;
	private boolean foundShootingStar;

	// trimmed compcape
	private int finishedCastleWars;
	private int finishedStealingCreations;

	// crucible
	private boolean talkedWithMarv;
	private int crucibleHighScore;

	private int ecoClearStage;

	// gravestone
	private int graveStone;

	private String lastFriendsChat;
	private int lastFriendsChatRank = -1;
	private String clanName;// , guestClanChat;
	private boolean connectedClanChannel;

	private int summoningLeftClickOption;
	private transient boolean pouchFilter;
	private List<String> ownedObjectsManagerKeys;

	private String customTitle = "";
	private String customYellTag = "";
	private String customTitleColor = "551A8B";
	private boolean customTitleCapitalize = false;
	private boolean customTitleActive = false;
	private boolean customYellTagActive = false;

	private boolean hasDied = false;

	public int MaxScape830V1Membership;

	private int trollsToKill;
	private int trollsKilled;

	/**
	 * Objects
	 */
	// kalphite
	private boolean khalphiteLairEntranceSetted;
	private boolean khalphiteLairSetted;
	// red sandstone
	private int redStoneCount;
	private long redStoneDelay;

	private boolean xpLocked;
	private boolean yellOff;

	private String yellColor = "ff0000";
	private boolean oldItemsLook; // selects whenever to play with old or new
	// items visual

	private int votes;
	private boolean enteredDonatorZone;

	private String lastGameIp;
	private String lastGameMAC;
	private transient boolean masterLogin;
	private long lastGameLogin;

	private long onlineTime;

	private String lastBossInstanceKey;
	private InstanceSettings lastBossInstanceSettings;

	// new nsi system
	private Map<Integer, Integer> iLayoutVars;

	public boolean recievedStarter;

	// creates Player and saved classes
	public Player() {
		super(Settings.STARTER_LOCATION);
		super.setHitpoints(1000);
		appearence = new Appearence();
		inventory = new Inventory();
		moneyPouch = new MoneyPouch();
		equipment = new Equipment();
		skills = new Skills();
		combatDefinitions = new CombatDefinitions();
		prayer = new Prayer();
		bank = new Bank();
		controlerManager = new ControlerManager();
		musicsManager = new MusicsManager();
		emotesManager = new EmotesManager();
		notes = new Notes();
		toolbelt = new Toolbelt();
		dominionTower = new DominionTower();
		charges = new ChargesManager();
		auraManager = new AuraManager();
		questManager = new QuestManager();
		jobManager = new JobManager(this);
		pMissions = new PortMissions(this);
		petManager = new PetManager();
		farmingManager = new FarmingManager();
		geManager = new GrandExchangeManager();
		slayerManager = new SlayerManager();
		squealOfFortune = new SquealOfFortune();
		treasureTrailsManager = new TreasureTrailsManager();
		coalTrucksManager = new CoalTrucksManager();
		dungManager = new DungManager();
		house = new House();
		actionbar = new ActionBar();
		skillTasks = new SkillerTasks();
		reaperTasks = new Reaper();
		doomsayerManager = new DoomsayerManager();
		timersManager = new TimersManager();
		playerExamineManager = new PlayerExamineManager();
		cosmeticsManager = new CosmeticsManager();
		playerPorts = new PlayerPorts();
		// dailyChallengeManager = new DailyChallenge(this);
		runEnergy = 100;
		allowChatEffects = true;
		mouseButtons = true;
		profanityFilter = true;
		guidanceSystemHints = true;
		toogleQuickChat = true;
		makeXProgressWindow = true;
		taskInformationWindow = true;
		rs3Camera = true;
		targetReticules = true;
		alwaysShowTargetInformation = true;
		toggleBuffTimers = true;
		utcClock = 1;
		pouches = new int[4];
		resetBarrows();
		shosRewards = new boolean[4];
		warriorPoints = new double[6];
		subMenus = new int[9];
		this.boons = new boolean[12];
		prayerBook = new boolean[PrayerBooks.BOOKS.length];
		SkillCapeCustomizer.resetSkillCapes(this);
		ownedObjectsManagerKeys = new LinkedList<String>();
		resetILayoutVars();
		setEcoClearStage(10);
	}

	public void init(Session session, boolean lobby, String username, String displayName, String lastGameMAC,
			String email, int rights, int messageIcon, boolean masterLogin, boolean donator, boolean extremeDonator,
			boolean support, boolean gfxDesigner, boolean muted, long lastVote, int displayMode, int screenWidth,
			int screenHeight, MachineInformation machineInformation, IsaacKeyPair isaacKeyPair) {
		// temporary deleted after reset all chars
		if (doomsayerManager == null)
			doomsayerManager = new DoomsayerManager();
		if (timersManager == null)
			timersManager = new TimersManager();
		if (playerExamineManager == null)
			playerExamineManager = new PlayerExamineManager();
		if (cosmeticsManager == null)
			cosmeticsManager = new CosmeticsManager();
		if (playerPorts == null)
			playerPorts = new PlayerPorts();
		if (jobManager == null)
			jobManager = new JobManager(this);
		if (pMissions == null)
			pMissions = new PortMissions(this);
		if (boons == null)
			boons = new boolean[12];
		if (skillTasks == null)
			skillTasks = new SkillerTasks();
		if (reaperTasks == null)
			reaperTasks = new Reaper();
		if (fairyRings == null)
			fairyRings = new FairyRings(this);
		if (ddToken == null)
			ddToken = new DDToken(this);
		if (setSettings == false) {
			settings = new boolean[25];
			for (int i = 0; i < 25; i++)
				settings[i] = true;
			setSettings = true;
		}
		if (geBoolean == false) {
			grandExchangeLimit = new int[Utils.getItemDefinitionsSize()];
			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++)
				grandExchangeLimit[i] = 0;
			geBoolean = true;
		}
		this.session = session;
		this.lobby = lobby;
		this.username = username;
		this.displayName = displayName;
		this.email = email;
		this.rights = rights;
		this.masterLogin = masterLogin;
		this.muted = muted;
		this.lastVote = lastVote;
		this.lastGameMAC = lastGameMAC;
		this.displayMode = displayMode;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.machineInformation = machineInformation;
		this.isaacKeyPair = isaacKeyPair;
		interfaceManager = new InterfaceManager(this);
		dialogueManager = new DialogueManager(this);
		hintIconsManager = new HintIconsManager(this);
		priceCheckManager = new PriceCheckManager(this);
		localPlayerUpdate = new LocalPlayerUpdate(this);
		localNPCUpdate = new LocalNPCUpdate(this);
		actionManager = new ActionManager(this);
		cutscenesManager = new CutscenesManager(this);
		trade = new Trade(this);
		varsManager = new VarsManager(this);
		friendsIgnores = new FriendsIgnores(this);
		// loads player on saved instances
		appearence.setPlayer(this);
		skillTasks.setPlayer(this);
		reaperTasks.setPlayer(this);
		inventory.setPlayer(this);
		moneyPouch.setPlayer(this);
		equipment.setPlayer(this);
		skills.setPlayer(this);
		combatDefinitions.setPlayer(this);
		prayer.setPlayer(this);
		bank.setPlayer(this);
		controlerManager.setPlayer(this);
		musicsManager.setPlayer(this);
		emotesManager.setPlayer(this);
		notes.setPlayer(this);
		toolbelt.setPlayer(this);
		dominionTower.setPlayer(this);
		auraManager.setPlayer(this);
		charges.setPlayer(this);
		questManager.setPlayer(this);
		jobManager.setPlayer(this);
		petManager.setPlayer(this);
		house.setPlayer(this);
		actionbar.setPlayer(this);
		doomsayerManager.setPlayer(this);
		timersManager.setPlayer(this);
		playerExamineManager.setPlayer(this);
		cosmeticsManager.setPlayer(this);
		farmingManager.setPlayer(this);
		geManager.setPlayer(this);
		slayerManager.setPlayer(this);
		squealOfFortune.setPlayer(this);
		// treasureHunter.setPlayer(this);
		treasureTrailsManager.setPlayer(this);
		coalTrucksManager.setPlayer(this);
		dungManager.setPlayer(this);
		playerPorts.setPlayer(this);
		initEntity(); // generates hash thats why
		if (!lobby) {
			setDirection(Utils.getAngle(0, -1));
			logicPackets = new ConcurrentLinkedQueue<LogicPacket>();
			switchItemCache = Collections.synchronizedList(new ArrayList<Integer>());
			World.addPlayer(this);
			World.updateEntityRegion(this);
		} else
			World.addLobbyPlayer(this);
		afk = Utils.currentTimeMillis() + (Settings.DEBUG ? Integer.MAX_VALUE : 30 * 60 * 1000);
		afk();
		// PlayersOnline.update();
		Logger.log("Login", "Players online: " + World.getPlayers().size());
	}

	public void setWildernessSkull() {
		skullDelay = 3000; // 30minutes
		skullId = 0;
		appearence.generateIconsData();
	}

	public void setFightPitsSkull() {
		skullDelay = Integer.MAX_VALUE; // infinite
		skullId = 1;
		appearence.generateIconsData();
	}

	public void setSkullInfiniteDelay(int skullId) {
		skullDelay = Integer.MAX_VALUE; // infinite
		this.skullId = skullId;
		appearence.generateIconsData();
	}

	public void removeSkull() {
		skullDelay = -1;
		appearence.generateIconsData();
	}

	public boolean hasSkull() {
		return skullDelay > 0;
	}

	public int setSkullDelay(int delay) {
		return this.skullDelay = delay;
	}

	public void completeReset() {
		bank = new Bank();
		bank.setPlayer(this);
		equipment.reset();
		inventory.reset();
		moneyPouch.setCoinsAmount(0);
		actionbar = new ActionBar();
		actionbar.setPlayer(this);
		for (int skill = 0; skill < 25; skill++) {
			skills.setXp(skill, 0);
			skills.set(skill, 1);
		}
		skills.init();
	}

	public void refreshSpawnedItems() {
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId).getGroundItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if (item.isInvisible() && (item.hasOwner() && !getUsername().equals(item.getOwner())))
					continue;
				getPackets().sendRemoveGroundItem(item);
			}
		}
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId).getGroundItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if ((item.isInvisible()) && (item.hasOwner() && !getUsername().equals(item.getOwner())))
					continue;
				getPackets().sendGroundItem(item);
			}
		}
	}

	public void refreshSpawnedObjects() {
		for (int regionId : getMapRegionsIds()) {
			List<WorldObject> removedObjects = World.getRegion(regionId).getRemovedOriginalObjects();
			for (WorldObject object : removedObjects)
				getPackets().sendRemoveObject(object);
			List<WorldObject> spawnedObjects = World.getRegion(regionId).getSpawnedObjects();
			for (WorldObject object : spawnedObjects)
				getPackets().sendAddObject(object);
		}
	}

	// now that we inited we can start showing game
	public void start() {
		Logger.globalLog(username, session.getIP(), new String(" has logged in."));
		loadMapRegions();
		started = true;
		setOnline(true);
		run();
	}

	public void startLobby() {
		started = true;
		sendLobbyVars();
		runLobby();
		// fix to stuck cuz ofv ars dumping
		// if(getRights() == 2)
		// resetILayoutVars();
	}

	public void runLobby() {

		interfaceManager.sendInterfaces();

		if (Engine.delayedShutdownStart != 0) {
			int delayPassed = (int) ((Utils.currentTimeMillis() - Engine.delayedShutdownStart) / 1000);
			getPackets().sendSystemUpdate(Engine.delayedShutdownDelay - delayPassed, true);
		}

		friendsIgnores.initialize();
		if (lastFriendsChat != null) {
			FriendsChat.requestJoin(this, lastFriendsChat);
			lastFriendsChat = null;
		}
		if (clanName != null) {
			if (!ClansManager.connectToClan(this, clanName, false))
				clanName = null;
		}

		/*
		 * friendsIgnores.init(); if (currentFriendChatOwner != null) {
		 * FriendChatsManager.joinChat(currentFriendChatOwner, this); if
		 * (currentFriendChat == null) // failed currentFriendChatOwner = null;
		 * } // connect to current clan if (clanName != null) { if
		 * (!ClansManager.connectToClan(this, clanName, false)) clanName = null;
		 * }
		 */

	}

	private void sendLobbyVars() {
		getPackets().refreshLobbyInformation();
		// black screen if those not enabled

		getVarsManager().sendVar(1751, 6757431); // x
		getVarsManager().sendVar(1752, 6766321);// y

		getVarsManager().sendVar(1753, 225);
		getVarsManager().sendVar(1754, 69);

		// 1 - normal login. 2 ask for email(+ reason in varbit 16465)
		getVarsManager().sendVarBit(16464, 1);

		getVarsManager().sendVarBit(16465, 0);

		getPackets().sendCSVarInteger(3905, -1);// Enables banner clicking &
		// middle banner
		getPackets().sendCSVarInteger(4266, 0);// Treasure chest key amount
		getPackets().sendCSVarInteger(4267, 0);// Treasure chest heart amount
		getPackets().sendCSVarInteger(4263, -1);// Enables Treasure trail banner
		getPackets().sendCSVarInteger(4264, -1);// Boss pets display
		getPackets().sendCSVarInteger(4265, -1);// Enables second right banner
		getPackets().sendCSVarInteger(4660, 0);// Loyalty Points
		getPackets().sendCSVarInteger(4659, 0);// Runecoins

		getPackets().sendCSVarString(2508, displayName);
		// script_10012

	}

	public void stopAll() {
		stopAll(true);
	}

	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
	}

	public void stopAll(boolean stopWalk, boolean stopInterface) {
		stopAll(stopWalk, stopInterface, true);
	}

	// as walk done clientsided
	public void stopAll(boolean stopWalk, boolean stopInterfaces, boolean stopActions) {
		routeEvent = null;
		if (stopInterfaces)
			closeInterfaces();
		if (stopWalk && !cantWalk)
			resetWalkSteps();
		if (stopActions)
			actionManager.forceStop();
		combatDefinitions.resetSpells(false);
	}

	@Override
	public void setHitpoints(int hitpoints) {
		super.setHitpoints(hitpoints);
		refreshHitPoints();
	}

	@Override
	public void reset(boolean attributes) {
		super.reset(attributes);
		hintIconsManager.removeAll();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(false);
		resting = 0;
		skullDelay = 0;
		foodDelay = 0;
		potDelay = 0;
		castedVeng = false;
		cantWalk = false;
		setRunEnergy(100);
		appearence.generateAppearenceData();
		appearence.generateIconsData();
	}

	@Override
	public void reset() {
		reset(true);
	}

	public void closeInterfaces() {
		if (interfaceManager.containsScreenInterface())
			interfaceManager.removeCentralInterface();
		if (interfaceManager.containsBankInterface())
			interfaceManager.removeBankInterface();
		if (interfaceManager.containsInventoryInter())
			interfaceManager.removeInventoryInterface();
		if (interfaceManager.containsInputTextInterface())
			interfaceManager.removeInputTextInterface();
		if (interfaceManager.containsWorldMapInterface())
			interfaceManager.removeWorldMapInterface();
		dialogueManager.finishDialogue();
		if (closeInterfacesEvent != null) {
			closeInterfacesEvent.run();
			closeInterfacesEvent = null;
		}
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = Utils.currentWorldCycle() + 30;
	}

	@Override
	public void loadMapRegions() {
		boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		setClientHasntLoadedMapRegion();
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicGameScene(!started);
			if (!wasAtDynamicRegion)
				localNPCUpdate.reset();
		} else {
			getPackets().sendGameScene(!started);
			if (wasAtDynamicRegion)
				localNPCUpdate.reset();
		}
		forceNextMapLoadRefresh = false;
	}

	public void addAdrenalineBar() {
		if (getNextHitBars().isEmpty())
			getNextHitBars().add(new AdrenalineHitBar(this));
	}

	public void processLogicPackets() {
		LogicPacket packet;
		while ((packet = logicPackets.poll()) != null)
			WorldPacketsDecoder.decodeLogicPacket(this, packet);
	}

	public void processProjectiles() {

		for (int regionId : getMapRegionsIds()) {
			Region region = World.getRegion(regionId);
			for (Projectile projectile : region.getProjectiles()) {

				int fromSizeX, fromSizeY;
				if (projectile.getFrom() instanceof Entity)
					fromSizeX = fromSizeY = ((Entity) projectile.getFrom()).getSize();
				else if (projectile.getFrom() instanceof WorldObject) {
					ObjectDefinitions defs = ((WorldObject) projectile.getFrom()).getDefinitions();
					fromSizeX = defs.getSizeX();
					fromSizeY = defs.getSizeY();
				} else
					fromSizeX = fromSizeY = 1;
				int toSizeX, toSizeY;
				if (projectile.getTo() instanceof Entity)
					toSizeX = toSizeY = ((Entity) projectile.getTo()).getSize();
				else if (projectile.getTo() instanceof WorldObject) {
					ObjectDefinitions defs = ((WorldObject) projectile.getTo()).getDefinitions();
					toSizeX = defs.getSizeX();
					toSizeY = defs.getSizeY();
				} else
					toSizeX = toSizeY = 1;

				getPackets().sendProjectileNew(projectile.getFrom(), fromSizeX, fromSizeY, projectile.getTo(), toSizeX,
						toSizeY, projectile.getFrom() instanceof Entity ? (Entity) projectile.getFrom() : null,
						projectile.getTo() instanceof Entity ? (Entity) projectile.getTo() : null,
						projectile.isAdjustFlyingHeight(), projectile.isAdjustSenderHeight(),
						projectile.getSenderBodyPart(), projectile.getGraphicId(), projectile.getStartHeight(),
						projectile.getEndHeight(), projectile.getStartTime(), projectile.getEndTime(),
						projectile.getSlope(), projectile.getAngle(), 0);
			}
		}
	}

	@Override
	public void processEntityUpdate() {
		super.processEntityUpdate();
	}

	@Override
	public void processEntity() {
		processLogicPackets();
		// actionbar.processQueuedShortcut();
		actionManager.process();
		if (routeEvent != null && routeEvent.processEvent(this))
			routeEvent = null;
		super.processEntity();
		charges.process();
		auraManager.process();
		timersManager.process();
		prayer.processPrayer();
		controlerManager.process();
		farmingManager.process();
		cutscenesManager.process();
		if (isDead())
			return;

		if (musicsManager.musicEnded())
			musicsManager.replayMusic();
		if (hasSkull()) {
			skullDelay--;
			if (!hasSkull())
				appearence.generateAppearenceData();
		}
		getCombatDefinitions().processCombatStance();
		if (hasStarted() && !hasFinished()) {
			ticks++;
			if (ticks >= 100) {
				minutes++;
				ticks -= 100;
			}
			if (minutes >= 60) {
				hours++;
				minutes -= 60;
			}
			if (hours >= 24) {
				days++;
				hours -= 24;
			}
		}
	}

	@Override
	public void processReceivedHits() {
		if (isLocked())
			return;
		super.processReceivedHits();
	}

	@Override
	public void applyHit(Hit hit) {
		Entity source = hit.getSource();
		if (source != this && source instanceof Player && !isCanPvp())
			return;
		super.applyHit(hit);
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		/*
		 * if (!clientHasLoadedMapRegion()) { // load objects and items here
		 * setClientHasLoadedMapRegion(); refreshSpawnedObjects();
		 * refreshSpawnedItems(); }
		 */
	}

	public void toogleRun(boolean update) {
		super.setRun(!getRun());
		if (update)
			sendRunButtonConfig();
	}

	public void setRunHidden(boolean run) {
		super.setRun(run);
	}

	@Override
	public void setRun(boolean run) {
		if (run != getRun()) {
			super.setRun(run);
			sendRunButtonConfig();
		}
	}

	public void sendRunButtonConfig() {
		getVarsManager().sendVar(463, resting == 1 ? 3 : resting == 2 ? 4 : getRun() ? 1 : 0);
	}

	public void restoreRunEnergy() {
		if (getNextRunDirection() != -1)
			return;
		if (runEnergy >= 100)
			runEnergy = 100;
		if (isBronzeMember())
			runEnergy += Utils.random(2, 4);
		else if (isSilverMember())
			runEnergy += Utils.random(2, 4);
		else if (isGoldMember())
			runEnergy += Utils.random(4, 8);
		else if (isPlatinumMember())
			runEnergy += Utils.random(4, 8);
		else if (isDiamondMember())
			runEnergy = 100;
		else
			runEnergy++;
		getPackets().sendRunEnergy();
	}

	/*
	 * notice this may end up not being called if player dcs at same time he
	 * logs in use it just for stuff that cant be sent right away
	 */
	public void runAfterLoad() {
		interfaceManager.refreshInterface(true);
		skills.sendCombatLevel();
		runAfterLoad = true;
	}

	public void run() {
		if (isHardcoreIronman() && getHasDied()) {
			SerializableFilesManager.deleteAccount(this);
			disconnect(true, false);
			return;
		}
		checkList();
		sendTabs();
		sendMisc();
		sendLoginMessages();
		sendInit();
		sendSecurity();
		logLogin();
	}

	private void sendMisc() {
		if (getX() == 2171 && getY() == 3437 && getPlane() == 1)
			setNextAnimation(new Animation(25010));
	}

	private void sendLoginMessages() {
		getPackets().sendForceGameMessage(Color.MAROON,
				"Please consider checking out RuneRewind at www.runerewind.com - an oldschool project.");
		getPackets().sendForceGameMessage(
				!Settings.DEBUG ? "Welcome to " + Settings.SERVER_NAME + "." : "Welcome to 99max99' crazy world.");
		getPackets().sendGameMessage(Color.MAROON, "Read update topic: '" + Settings.UPDATE_TOPIC_TITLE
				+ "' at ::topic " + Settings.UPDATE_TOPIC_ID + ".");
		if (getRights() >= 1 && JModTable.PMOD_MEETING)
			getPackets().sendGameMessage(
					"<col=FF0000>A staff meeting has been requested, please use the command ::accept to teleport.");
		if (!isAMember())
			getPackets().sendGameMessage("You are not a member. Consider becoming one to support us.", true);
		if (Settings.DOUBLE_DROPS)
			getPackets().sendGameMessage("<col=FF0000>We are hosting double drop rates, ends x/x/2016 12:00 GMT.",
					true);
		if (Settings.DOUBLE_XP)
			getPackets().sendGameMessage("<col=FF0000>We are hosting double xp, ends on Sunday 23:59 GMT.", true);
		if (Settings.DOUBLE_VOTES)
			getPackets().sendGameMessage("<col=FF0000>We are hosting double vote points, ends 22/07/2016 12:00 GMT.",
					true);
		if (Settings.DOUBLE_DUNGEONEERING_TOKENS)
			getPackets().sendGameMessage(
					"<col=FF0000>We are hosting double dungeoneering tokens, ends x/x/2016 12:00 GMT.", true);
		if (getPrize() != null)
			getPackets().sendGameMessage("<col=FF0000>You have won the lottery, talk to Gambler to claim your prize.");
	//	if (getRights() == 2 && !getInventory().containsItem(5733, 1))
	//		getInventory().addItem(5733, 1);
	//	if (getRights() == 2 && !getInventory().containsItem(20428, 1))
	//		getInventory().addItem(20428, 1);
//		if (!Gero.validateAccount(getUsername(), getPassword(), getSession().getIP())) {
//			getPackets().sendGameMessage(
//					"<col=FF0000>Your account does not appear to have a forums account, please register one by using the command ::register.");
//			if (getSkills().getTotalLevel() >= 250)
//				getDialogueManager().startDialogue("ValidateAccount");
		}
//	}

	private void checkList() {
		if (!masterLogin)
			lastGameIp = getSession().getIP();
		lastGameLogin = Utils.currentTimeMillis();
		loginCount += 1;
		refreshInterfaceVars();
		interfaceManager.sendInterfaces();
		if (Engine.delayedShutdownStart != 0) {
			int delayPassed = (int) ((Utils.currentTimeMillis() - Engine.delayedShutdownStart) / 1000);
			getPackets().sendSystemUpdate(Engine.delayedShutdownDelay - delayPassed, false);
		}
	}

	private void sendTabs() {
		getPackets().sendRunEnergy();
		getPackets().sendItemsLook();
		refreshAllowChatEffects();
		refreshAcceptAid();
		refreshRightClickReporting();
		refreshMouseButtons();
		refreshHideFamiliarOptions();
		refreshGuidanceSystemHints();
		refreshToogleQuickChat();
		refreshProfanityFilter();
		refreshLockZoom();
		refreshCameraType();
		refreshChatsSetup();
		sendRunButtonConfig();
		Familiar.refreshDefaultPetOptions(this);
	}

	private void sendInit() {

		getInterfaceManager().sendCustom(this);
		sendMuteInterface();
		sendDefaultPlayersOptions();
		inventory.init();
		moneyPouch.init();
		equipment.checkItems();
		equipment.init();
		bank.init();
		skills.init();
		combatDefinitions.init();
		prayer.init();
		refreshHitPoints();
		warriorCheck();
		prayer.refreshPrayerPoints();
		getVarsManager().sendVar(1295, 1000); // unlock cant do this under
		// tutorial for chat
		getVarsManager().sendVarBit(20940, 120); // unlocks new tut
		getPackets().sendGameBarStages();
		musicsManager.init();
		emotesManager.init();
		questManager.init();
		notes.init();
		house.init();
		actionbar.init();
		doomsayerManager.init();
		timersManager.init();
		playerExamineManager.init();
		cosmeticsManager.init();
		farmingManager.init();
		toolbelt.init();
		geManager.init();
		coalTrucksManager.init();
		refreshBuffs();
		ItemRemover.check(this);
		sendUnlockedObjectConfigs();
		friendsIgnores.initialize();
		if (lastFriendsChat != "99max99") {
			FriendsChat.requestJoin(this, "99max99");
			lastFriendsChat = null;
		}
		if (clanName != null) {
			if (!ClansManager.connectToClan(this, clanName, false))
				clanName = null;
		}
		if (familiar != null)
			familiar.respawnFamiliar(this);
		else
			petManager.init();
		if (getDailyTask() == null)
			daily = DailyTasks.generateDailyTask(this, Tasks.SKILLING);
		else
			getDailyTask().generateDailyTasks(this, false);
		running = true;
		appearence.generateAppearenceData();
		appearence.generateIconsData();
		controlerManager.login();
		OwnedObjectManager.linkKeys(this);
		GraveStone.linkPlayer(this);
		updateStore();
		squealOfFortune.handleKeys(squealOfFortune.daily, false);
		if (machineInformation != null)
			machineInformation.sendSuggestions(this);
		if (isDead())
			sendDeath(null);
	}

	private void logLogin() {
		try {
			DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal2 = Calendar.getInstance();
			final String FILE_PATH = "data/logs/logins/";
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + getUsername() + ".txt", true));
			writer.write("[" + dateFormat2.format(cal2.getTime()) + "] IP: " + getSession().getIP());
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException er) {
			er.printStackTrace();
		}
		if (!recievedStarter)
			appendStarter();
	}

	private void sendSecurity() {
		if (getSecureCode() != 0) {
			lock();
			getInterfaceManager().sendCentralInterface(793);
			getPackets().sendIComponentText(793, 5, "Secure Code");
			getPackets().sendIComponentText(793, 13,
					"Please enter your secure code below.<br><br><br><col=e55d12>Session ID: " + getSessionTime()
							+ "</col><br><br><br>Note: all actions are being recorded.");
			getPackets().sendIComponentText(793, 12, getSession().getIP());
			getPackets().sendIComponentText(793, 14, "Help");
			getPackets().sendIComponentText(793, 15, "Logout");
			getTemporaryAttributtes().put("SecureCode", 0);
			getPackets()
					.sendInputIntegerScript("Please enter your Secure Code (" + secureAttempts + "/5 attempts left):");
		}
	}

	private void sendMuteInterface() {
		if (isMuted()) {
			getInterfaceManager().sendCentralInterface(801);
			getPackets().sendIComponentText(801, 15, "Muted - Quick Chat");
			getPackets().sendIComponentText(801, 18,
					"Please note that your accout has been restricted to Quick Chat, as your account have been involved in serious rule breaking - to prevent further mutes please read the rules.");
			getPackets().sendGameMessage("You have been temporarily muted due to breaking a rule.");
			getPackets().sendGameMessage("This mute will remain for a further " + getMutedFor() + " hours.");
			getPackets().sendGameMessage("To prevent further mutes please read the rules.");
		}
	}

	private void sendUnlockedObjectConfigs() {
		refreshKalphiteLairEntrance();
		refreshKalphiteLair();
		refreshLodestoneNetwork();
		refreshFightKilnEntrance();
		refreshLairofTarnRazorlorEntrance();
		refreshTreeofJadinko();
	}

	private void refreshTreeofJadinko() {
		getVarsManager().sendVarBit(9513, 1);
	}

	private void refreshLairofTarnRazorlorEntrance() {
		getVarsManager().sendVar(382, 11);
	}

	private void refreshLodestoneNetwork() {
		getVarsManager().sendVar(3, -1); // unlocks all lodestone teleports
		getVarsManager().sendVarBit(9482, 15); // bandit lodestone, desert
		// treasure q
		getVarsManager().sendVarBit(10236, 190); // lunar isle lodestone,
		// diplomacy q
	}

	private void refreshKalphiteLair() {
		if (khalphiteLairSetted)
			getVarsManager().sendVarBit(16280, 1);
	}

	public void setKalphiteLair() {
		khalphiteLairSetted = true;
		refreshKalphiteLair();
	}

	private void refreshFightKilnEntrance() {
		if (completedFightCaves)
			getVarsManager().sendVarBit(3910, 1);
	}

	private void refreshKalphiteLairEntrance() {
		if (khalphiteLairEntranceSetted)
			getVarsManager().sendVarBit(16281, 1);
	}

	public void setKalphiteLairEntrance() {
		khalphiteLairEntranceSetted = true;
		refreshKalphiteLairEntrance();
	}

	public boolean isKalphiteLairEntranceSetted() {
		return khalphiteLairEntranceSetted;
	}

	public boolean isKalphiteLairSetted() {
		return khalphiteLairSetted;
	}

	private void appendStarter() {
		if (recievedStarter == true)
			return;
		getInventory().addItem(1351, 1);
		getInventory().addItem(590, 1);
		getInventory().addItem(303, 1);
		getInventory().addItem(316, 100);
		getInventory().addItem(1925, 1);
		getInventory().addItem(1931, 1);
		getInventory().addItem(2309, 1);
		getInventory().addItem(1265, 1);
		getInventory().addItem(1205, 1);
		getInventory().addItem(1277, 1);
		getInventory().addItem(1171, 1);
		getInventory().addItem(841, 1);
		getInventory().addItem(882, 250);
		getInventory().addItem(556, 250);
		getInventory().addItem(558, 250);
		getInventory().addItem(555, 250);
		getInventory().addItem(557, 250);
		getInventory().addItem(559, 250);
		getMoneyPouch().sendDynamicInteraction(50000, false);
		FriendsChat.requestJoin(this, "99max99");
		getDialogueManager().startDialogue("MaxScape830Guide");
		squealOfFortune.setKeys(3);
		getPackets().sendGameMessage("You have 2 Treasure Hunter keys.");
		getPackets().sendForceGameMessage(Color.PURPLE, "Please view ::topic 575 for an indept starter guide!");
		recievedStarter = true;
	}

	public void sendDefaultPlayersOptions() {
		getPackets().sendPlayerOption("Follow", 2, false);
		getPackets().sendPlayerOption("Trade with", 4, false);
		getPackets().sendPlayerOption("Examine", 6, false);
	}

	/**
	 * Logs the player out.
	 * 
	 * @param lobby
	 *            If we're logging out to the lobby.
	 */
	public void logout(boolean lobby) {
		if (!running || !started)
			return;
		long currentTime = Utils.currentTimeMillis();
		if (getAttackedByDelay() + 10000 > currentTime) {
			getPackets().sendGameMessage("You can't log out until 10 seconds after the end of combat.");
			return;
		}
		if (getEmotesManager().getNextEmoteEnd() >= currentTime) {
			getPackets().sendGameMessage("You can't log out while performing an action.");
			return;
		}
		/*
		 * if (dungManager.isInside()) { dungManager.leaveParty();
		 * getInventory().reset(); }
		 */

		if (familiar != null)
			familiar.dissmissFamiliar(true);

		if (isLocked()) {
			getPackets().sendGameMessage("You can't log out while performing an action.");
			return;
		}
		GrandExchange.save();
		disconnect(false, lobby);
	}

	public void disconnect(boolean immediate, boolean lobby) {
		immediateFinish = immediate;
		ChannelFuture future = getPackets().sendLogout(lobby);
		if (isLobby() || immediate) { // worldthread does this anyway, so just
			// need to send packet for normal logout
			if (future == null)
				getSession().getChannel().close();
			else
				future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private transient boolean immediateFinish;
	private transient boolean finishing;

	public int divine;
	public transient Player divines;

	public int portable;
	public transient Player portables;

	public int Oreid;

	@Override
	public void finish() {
		finish(0);
	}

	public boolean isFinishing() {
		return finishing;
	}

	public void finish(final int tryCount) {
		if (finishing || hasFinished())
			return;
		finishing = true;
		setOnline(false);
		if (lobby) {
			finishLobby();
			return;
		}
		// if combating doesnt stop when xlog this way ends combat
		stopAll(false, true, !(actionManager.getAction() instanceof PlayerCombatNew));

		if (!immediateFinish && ((isDead() || isUnderCombat() || isLocked() || getEmotesManager().isDoingEmote()))
				&& tryCount < 4) {
			GameExecutorManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						finishing = false;
						finish(tryCount + 1);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 10, TimeUnit.SECONDS);
			return;
		}
		realFinish();
	}

	public void finishLobby() {
		if (hasFinished())
			return;
		/*
		 * friendsIgnores.sendFriendsMyStatus(false); if (currentFriendChat !=
		 * null) currentFriendChat.leaveChat(this, true); if (clanManager !=
		 * null) clanManager.disconnect(this, false);
		 */
		if (currentFriendsChat != null)
			FriendsChat.detach(this);
		if (clanManager != null)
			clanManager.disconnect(this, false);
		setFinished(true);
		session.getChannel().close();
		PlayerHandlerThread.addLogout(this);
		World.removeLobbyPlayer(this);
	}

	public void realFinish() {
		if (hasFinished())
			return;
		Logger.globalLog(username, session.getIP(), new String(" has logged out."));
		// login
		running = false;
		stopAll();
		onlineTime += getSessionTime();
		cutscenesManager.logout();
		controlerManager.logout(); // checks what to do on before logout for
		house.finish();
		dungManager.finish();
		GrandExchange.unlinkOffers(this);
		if (currentFriendsChat != null)
			FriendsChat.detach(this);
		if (clanManager != null)
			clanManager.disconnect(this, false);
		if (guestClanManager != null)
			guestClanManager.disconnect(this, true);
		if (familiar != null && !familiar.isFinished())
			familiar.dissmissFamiliar(true);
		else if (pet != null)
			pet.finish();
		if (slayerManager.getSocialPlayer() != null)
			slayerManager.resetSocialGroup(true);
		if (getNextWorldTile() != null)
			setLocation(getNextWorldTile());
		setFinished(true);
		session.getChannel().close();
		updateHighscores();
		PlayerHandlerThread.addLogout(this);
		World.updateEntityRegion(this);
		World.removePlayer(this);
		Logger.log("Logout", username + ":" + getSession().getIP());
	}

	private void updateStore() {
		if (Settings.HOSTED == false)
			return;
		handleWebstore(this, getUsername());
	}

	private void updateHighscores() {
		if (Settings.HOSTED == false)
			return;
		Highscores.saveHighScore(this);
	}

	public void refreshHitPoints() {
		if (getVarsManager().sendVarBit(1668, getHitpoints()))
			updateBuffs();
	}

	@Override
	public int getMaxHitpoints() {
		return skills.getLevel(Skills.HITPOINTS) * 100 + equipment.getEquipmentHpIncrease();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public void processHit(Hit hit) {
		if (appearence.isHidden())
			return;
		super.processHit(hit);
	}

	public int getRights() {
		return rights;
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	public int getMessageIcon() {
		return getRights() == 2 ? 2 : getRights();
		/*
		 * return getRights() == 2 ? 2 : isAMember() && getRights() == 1 &&
		 * getChatBadge() == true ? 10 : getRights() == 1 ? getRights() :
		 * isAMember() && getChatBadge() == true ? 9 : getRights();
		 */
	}

	public void setMessageIcon(int icon) {

	}

	public WorldPacketsEncoder getPackets() {
		return session.getWorldPackets();
	}

	public boolean hasStarted() {
		return started;
	}

	public boolean isRunning() {
		return running;
	}

	public String getEmail() {
		return email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String newName) {
		this.displayName = newName;
		getAppearence().generateAppearenceData();
	}

	public String getMutedFor() {
		return mutedFor;
	}

	public void setMutedFor(String mutedFor) {
		this.mutedFor = mutedFor;
	}

	public Appearence getAppearence() {
		return appearence;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public LocalPlayerUpdate getLocalPlayerUpdate() {
		return localPlayerUpdate;
	}

	public LocalNPCUpdate getLocalNPCUpdate() {
		return localNPCUpdate;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public Session getSession() {
		return session;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public boolean clientHasLoadedMapRegion() {
		return clientLoadedMapRegion < Utils.currentWorldCycle();
	}

	public void setClientHasLoadedMapRegion() {
		clientLoadedMapRegion = -1;
	}

	public boolean clientHasLoadedMapRegionFinished() {
		return clientLoadedMapRegion == -1;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Skills getSkills() {
		return skills;
	}

	public byte getRunEnergy() {
		return runEnergy;
	}

	public double getWeight() {
		return inventory.getInventoryWeight() + equipment.getEquipmentWeight();
	}

	public boolean hasItem(int item) {
		return (getInventory().containsItem(item, 1) ? true
				: (bank.getItem(item) != null ? true : equipment.hasItem(item)));
	}

	public boolean hasItem(int item, int amount) {
		return (getInventory().containsItem(item, amount) ? true
				: (bank.getItem(item) != null ? true : equipment.hasItem(item)));
	}

	public void drainRunEnergy() {
		if (dungManager.isInside())
			return;
		if (isDiamondMember()) {
			runEnergy = 100;
			return;
		}
		setRunEnergy(runEnergy - 1);
	}

	public void setRunEnergy(int runEnergy) {
		if (runEnergy == this.runEnergy)
			return;
		if (runEnergy < 0)
			runEnergy = 0;
		else if (runEnergy > 100)
			runEnergy = 100;
		this.runEnergy = (byte) runEnergy;
		getPackets().sendRunEnergy();
	}

	public boolean isResting() {
		return resting > 0;
	}

	public void setResting(int resting) {
		this.resting = resting;
		sendRunButtonConfig();
	}

	public ActionManager getActionManager() {
		return actionManager;
	}

	public void setRouteEvent(RouteEvent routeEvent) {
		this.routeEvent = routeEvent;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}

	public CombatDefinitions getCombatDefinitions() {
		return combatDefinitions;
	}

	@Override
	public double getMagePrayerMultiplier() {

		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMeleePrayerMultiplier() {

		if (Utils.random(5) == 0 && Combat.fullVeracsEquipped(this))
			return 1.0;

		return 0.6;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		if (hit.getLook() == HitLook.REGULAR_DAMAGE) {
			if (getEffectsManager().hasActiveEffect(EffectType.REFLECT))
				hit.setDamage(0);
			return;
		}
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if (invulnerable) {
			hit.setDamage(0);
			return;
		}
		if (auraManager.usingPenance()) {
			int amount = (int) (hit.getDamage() * 0.05);
			if (amount > 0)
				prayer.restorePrayer(amount);
		}
		final Entity source = hit.getSource();
		if (source == null)
			return;
		if (getEffectsManager().hasActiveEffect(EffectType.VESTA_IMMUNITY)) {
			if (equipment.getWeaponId() != 13905 && equipment.getWeaponId() != 13907)
				getEffectsManager().removeEffect(EffectType.VESTA_IMMUNITY);
			else {
				int damage = (int) (hit.getDamage() * 0.5);
				hit.setDamage(damage);
				source.applyHit(new Hit(Player.this, damage, HitLook.REFLECTED_DAMAGE));
			}
		} else if (getEffectsManager().hasActiveEffect(EffectType.MIRRORBACK_SPIDER)) {
			Effect e = getEffectsManager().getEffectForType(EffectType.MIRRORBACK_SPIDER);
			if (e != null) {
				MirrorBackSpider spider = (MirrorBackSpider) e.getArguments()[0];
				if (spider.isDead())
					getEffectsManager().removeEffect(e.getType());
				else {
					int damage = (int) (hit.getDamage() * 0.5);
					hit.setDamage(damage);
					Hit reflectedHit = new Hit(this, damage, HitLook.REFLECTED_DAMAGE);
					spider.applyHit(reflectedHit);
					source.applyHit(reflectedHit);
				}
			}
		} else if (getEffectsManager().hasActiveEffect(EffectType.REFLECT)) {
			int damage = (int) (hit.getDamage() * 0.5);
			hit.setDamage(damage);
			source.applyHit(new Hit(Player.this, damage, HitLook.REFLECTED_DAMAGE));
		} else if (getEffectsManager().hasActiveEffect(EffectType.BARRICADE))
			hit.setDamage(0);
		if (getEffectsManager().hasActiveEffect(EffectType.DEBILITATE))
			hit.setDamage((int) (hit.getDamage() * 0.50));
		if (getEffectsManager().hasActiveEffect(EffectType.IMMORTALITY))
			hit.setDamage((int) (hit.getDamage() * 0.75));
		if (getEffectsManager().hasActiveEffect(EffectType.STAFF_OF_LIGHT)) {
			Item staff = equipment.getItem(Equipment.SLOT_WEAPON);
			if (staff == null || !staff.getName().equals("Staff of light"))
				getEffectsManager().removeEffect(EffectType.STAFF_OF_LIGHT);
			else {
				if (hit.getLook() == HitLook.MELEE_DAMAGE)
					hit.setDamage((int) (hit.getDamage() * 0.5));
			}
		}
		if (getEffectsManager().hasActiveEffect(EffectType.BERSERK))
			hit.setDamage((int) (hit.getDamage() * 1.5));
		if (getEffectsManager().hasActiveEffect(EffectType.RESONANCE)) {
			int damage = hit.getDamage();
			if (damage > 1) {
				hit.setDamage(1);
				heal(damage, 0, 0, true);
				getEffectsManager().removeEffect(EffectType.RESONANCE);
			}
		}
		Effect revengeEffect = getEffectsManager().getEffectForType(EffectType.REVENGE);
		if (revengeEffect != null) {
			if (hit.getDamage() > 0) {
				double nextDmgMultiplier = (double) revengeEffect.getArguments()[0] + 0.10;
				revengeEffect.getArguments()[0] = nextDmgMultiplier > 2.0 ? 2.0 : nextDmgMultiplier;
			}
		}
		if (prayer.hasPrayersOn() && hit.getDamage() != 0) {
			if (source instanceof Familiar) {
				Familiar fam = (Familiar) source;
				if (!fam.hasSpecialOn())
					return;
				Player owner = fam.getOwner();
				if (owner == null)
					return;
				if (prayer.usingPrayer(0, 10))
					hit.setDamage((int) (hit.getDamage() * 0.5));
				else if (prayer.usingPrayer(1, 10)) {
					hit.setDamage((int) (hit.getDamage() * 0.5));
					int deflectedDamage = (int) (hit.getDamage() * 0.1);
					if (deflectedDamage > 0) {
						owner.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2227));
						setNextAnimationNoPriority(new Animation(12573));
					}
				}
			} else {
				boolean isPvPReducation = source instanceof Player;
				if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
					if (prayer.usingPrayer(0, 11)) {
						hit.setDamage((int) (hit.getDamage() * (getEffectsManager().hasActiveEffect(EffectType.DEVOTION)
								? isPvPReducation ? 0.25 : 0.0 : source.getMagePrayerMultiplier())));
						if (hit.getDamage() == 0)
							hit.setDamage(1);
					} else if (prayer.usingPrayer(1, 11)) {
						int deflectedDamage = (int) (hit.getDamage() * 0.1);
						hit.setDamage((int) (hit.getDamage() * (getEffectsManager().hasActiveEffect(EffectType.DEVOTION)
								? isPvPReducation ? 0.25 : 0.0 : source.getMagePrayerMultiplier())));
						if (hit.getDamage() == 0)
							hit.setDamage(1);
						if (deflectedDamage > 0 && prayer.canReflect(source)) {
							source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
							setNextGraphics(new Graphics(2228));
							setNextAnimationNoPriority(new Animation(12573));
						}
					}
				} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
					if (prayer.usingPrayer(0, 12)) {
						hit.setDamage((int) (hit.getDamage() * (getEffectsManager().hasActiveEffect(EffectType.DEVOTION)
								? isPvPReducation ? 0.25 : 0.0 : source.getRangePrayerMultiplier())));
						if (hit.getDamage() == 0)
							hit.setDamage(1);
					} else if (prayer.usingPrayer(1, 12)) {
						int deflectedDamage = (int) (hit.getDamage() * 0.1);
						hit.setDamage((int) (hit.getDamage() * (getEffectsManager().hasActiveEffect(EffectType.DEVOTION)
								? isPvPReducation ? 0.25 : 0.0 : source.getRangePrayerMultiplier())));
						if (hit.getDamage() == 0)
							hit.setDamage(1);
						if (deflectedDamage > 0 && prayer.canReflect(source)) {
							source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
							setNextGraphics(new Graphics(2229));
							setNextAnimationNoPriority(new Animation(12573));
						}
					}
				} else if (hit.getLook() == HitLook.MELEE_DAMAGE) {
					if (prayer.usingPrayer(0, 13)) {
						hit.setDamage((int) (hit.getDamage() * (getEffectsManager().hasActiveEffect(EffectType.DEVOTION)
								? isPvPReducation ? 0.25 : 0.0 : source.getMeleePrayerMultiplier())));
						if (hit.getDamage() == 0)
							hit.setDamage(1);
					} else if (prayer.usingPrayer(1, 13)) {
						int deflectedDamage = (int) (hit.getDamage() * 0.1);
						hit.setDamage((int) (hit.getDamage() * (getEffectsManager().hasActiveEffect(EffectType.DEVOTION)
								? isPvPReducation ? 0.25 : 0.0 : source.getMeleePrayerMultiplier())));
						if (hit.getDamage() == 0)
							hit.setDamage(1);
						if (deflectedDamage > 0 && prayer.canReflect(source)) {
							source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
							setNextGraphics(new Graphics(2230));
							setNextAnimationNoPriority(new Animation(12573));
						}
					}
				}
			}
		}
		int shieldId = equipment.getShieldId();
		if (shieldId == 13740 || shieldId == 23698 || shieldId == 13742 || shieldId == 23699 || shieldId == 13738
				|| shieldId == 23697) { // divine,
										// eylsian,
										// and
										// arcane
			int drain = (int) (Math.ceil(hit.getDamage() * 0.06));
			if (prayer.getPrayerpoints() >= drain) {
				hit.setDamage((int) (hit.getDamage() * 0.70));
				prayer.drainPrayer(drain);
			}
		}
		if (castedVeng && hit.getDamage() >= 4) {
			castedVeng = false;
			setNextForceTalk(new ForceTalk("Taste vengeance!"));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					source.applyHit(new Hit(Player.this, (int) (hit.getDamage() * 0.75), HitLook.REGULAR_DAMAGE));
				}
			});
		}
		getControlerManager().processIngoingHit(hit);
		if (source instanceof Player) {
			((Player) source).getPrayer().handleHitPrayers(this, hit);
			((Player) source).getControlerManager().processIncommingHit(hit, this);
		}

	}

	@Override
	public void removeHitpoints(final Hit hit) {
		super.removeHitpoints(hit);
		if (isDead())
			return;
		if (getEquipment().getRingId() == 2550) {
			if (hit.getSource() != null && hit.getSource() != this && hit.getDamage() > 0
					&& (hit.getLook() == HitLook.MELEE_DAMAGE || hit.getLook() == HitLook.RANGE_DAMAGE
							|| hit.getLook() == HitLook.MAGIC_DAMAGE)) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						hit.getSource().applyHit(new Hit(hit.getSource(),
								(int) (Math.ceil(hit.getDamage() * 0.01) * 10), HitLook.REGULAR_DAMAGE));
					}
				});
			}
		}
		if (getPrayer().hasPrayersOn()) {
			if (getPrayer().usingPrayer(0, 15) && (getHitpoints() < getMaxHitpoints() * 0.1)) {
				setNextGraphics(new Graphics(436));
				setHitpoints((int) (getHitpoints() + getSkills().getLevelForXp(Skills.PRAYER) * 2.5));
				getSkills().set(Skills.PRAYER, 0);
				getPrayer().setPrayerpoints(0);
			}
		}
		if (getEquipment().getAmuletId() == 11090 && getHitpoints() <= getMaxHitpoints() * 0.2) {// priority
			// over
			// ring
			// of
			// life
			heal((int) (getMaxHitpoints() * 0.3));
			getEquipment().deleteItem(11090, 1);
			getAppearence().generateAppearenceData();
			resetReceivedHits();
			getPackets().sendGameMessage("Your pheonix necklace heals you, but is destroyed in the process.");
		} else if (getEquipment().getAmuletId() != 11090 && getEquipment().getRingId() == 11090
				&& getHitpoints() <= getMaxHitpoints() * 0.1) {
			Magic.sendNormalTeleportSpell(this, 1, 0, DeathEvent.getRespawnHub(this));
			getEquipment().deleteItem(11090, 1);
			resetReceivedHits();
			getPackets().sendGameMessage("Your ring of life saves you, but is destroyed in the process.");
		}
	}

	@Override
	public void sendDeath(final Entity source) {
		if (getEffectsManager().hasActiveEffect(EffectType.IMMORTALITY)) {
			resetReceivedHits();
			setHitpoints((int) (getMaxHitpoints() * 0.40));
			setNextAnimation(new Animation(18119));
			setNextGraphics(new Graphics(3630));
			World.sendGraphics(this, new Graphics(3631, 0, 0, getDirection(), true), this);
			getEffectsManager().removeEffect(EffectType.IMMORTALITY);
			return;
		}
		if (prayer.hasPrayersOn() && getTemporaryAttributtes().get("startedDuel") != Boolean.TRUE) {
			if (prayer.usingPrayer(0, 14)) {
				setNextGraphics(new Graphics(437));
				final Player target = this;

				for (int regionId : getMapRegionsIds()) {
					List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
					if (playersIndexes != null) {
						for (int playerIndex : playersIndexes) {
							Player player = World.getPlayers().get(playerIndex);
							if (player == null || !player.hasStarted() || player.isDead() || player.hasFinished()
									|| !player.withinDistance(this, 1) || !player.isCanPvp()
									|| !target.getControlerManager().canHit(player))
								continue;
							player.applyHit(
									new Hit(target, Utils.random((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)),
											HitLook.REGULAR_DAMAGE));
						}
					}
					List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
					if (npcsIndexes != null) {
						for (int npcIndex : npcsIndexes) {
							NPC npc = World.getNPCs().get(npcIndex);
							if (npc == null || npc.isDead() || npc.hasFinished() || !npc.withinDistance(this, 1)
									|| !npc.getDefinitions().hasAttackOption()
									|| !target.getControlerManager().canHit(npc))
								continue;
							npc.applyHit(
									new Hit(target, Utils.random((int) (skills.getLevelForXp(Skills.PRAYER) * 25.00)),
											HitLook.REGULAR_DAMAGE));
						}
					}
				}

				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY(), target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY(), target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX(), target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX(), target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY() + 1, target.getPlane()));
					}
				});
			} else if (prayer.usingPrayer(1, 23)) {
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() + 2, getPlane()), 2261, 24, 0, 41, 35, 30,
						0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY(), getPlane()), 2261, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() - 2, getPlane()), 2261, 41, 0, 41, 35, 30,
						0);

				World.sendProjectile(this, new WorldTile(getX() - 2, getY() + 2, getPlane()), 2261, 41, 0, 41, 35, 30,
						0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY(), getPlane()), 2261, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY() - 2, getPlane()), 2261, 41, 0, 41, 35, 30,
						0);

				World.sendProjectile(this, new WorldTile(getX(), getY() + 2, getPlane()), 2261, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX(), getY() - 2, getPlane()), 2261, 41, 0, 41, 35, 30, 0);
				final Player target = this;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						setNextGraphics(new Graphics(2259));

						for (int regionId : getMapRegionsIds()) {
							List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
							if (playersIndexes != null) {
								for (int playerIndex : playersIndexes) {
									Player player = World.getPlayers().get(playerIndex);
									if (player == null || !player.hasStarted() || player.isDead()
											|| player.hasFinished() || !player.isCanPvp()
											|| !player.withinDistance(target, 2)
											|| !target.getControlerManager().canHit(player))
										continue;
									player.applyHit(new Hit(target,
											(int) Utils.random((skills.getLevelForXp(Skills.PRAYER) * 25.00)),
											HitLook.REGULAR_DAMAGE));
								}
							}
							List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
							if (npcsIndexes != null) {
								for (int npcIndex : npcsIndexes) {
									NPC npc = World.getNPCs().get(npcIndex);
									if (npc == null || npc.isDead() || npc.hasFinished()
											|| !npc.withinDistance(target, 2) || !npc.getDefinitions().hasAttackOption()
											|| !target.getControlerManager().canHit(npc))
										continue;
									npc.applyHit(new Hit(target,
											(int) Utils.random((skills.getLevelForXp(Skills.PRAYER) * 25.00)),
											HitLook.REGULAR_DAMAGE));
								}
							}
						}

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() + 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() - 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX(), getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX(), getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 1, getY() + 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 1, getY() - 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 1, getY() + 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 1, getY() - 1, getPlane()));
					}
				});
			}
		}
		setNextAnimation(new Animation(-1));
		if (!controlerManager.sendDeath())
			return;
		lock(8);
		stopAll();
		if (familiar != null)
			familiar.sendDeath(this);
		final WorldTile deathTile = new WorldTile(this);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(getDeathAnimation());
				} else if (loop == 1) {
					getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					controlerManager.startControler("DeathEvent", deathTile, hasSkull());
					final String FILE_PATH = "data/logs/deaths/";
					try {
						DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
						Calendar cal = Calendar.getInstance();
						BufferedWriter writer = new BufferedWriter(
								new FileWriter(FILE_PATH + getUsername() + ".txt", true));
						writer.write(
								"[" + dateFormat.format(cal.getTime()) + ", IP: " + getSession().getIP() + "] : died.");
						writer.newLine();
						writer.write("[" + dateFormat.format(cal.getTime()) + "] player location: " + getX() + ", "
								+ getY() + ", " + getPlane() + ".");
						writer.newLine();
						writer.flush();
						writer.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (loop == 4) {
					getMusicsManager().playMusicEffect(MusicsManager.DEATH_MUSIC_EFFECT);
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public void sendItemsOnDeath(Player killer, boolean dropItems, boolean dropLostItems) {
		Integer[][] slots = GraveStone.getItemSlotsKeptOnDeath(this, true, dropItems, getPrayer().isProtectingItem());
		sendItemsOnDeath(killer, getLastWorldTile(), new WorldTile(this), true, slots, dropLostItems);
	}

	/*
	 * default items on death, now only used for wilderness
	 */
	public void sendItemsOnDeath(Player killer, boolean dropItems) {
		sendItemsOnDeath(killer, dropItems, true);
	}

	/*
	 * default items on death, now only used for wilderness
	 */
	public void sendItemsOnDeath(Player killer) {
		sendItemsOnDeath(killer, hasSkull());
	}

	public void sendItemsOnDeath(Player killer, WorldTile deathTile, WorldTile respawnTile, boolean wilderness,
			Integer[][] slots, boolean dropLostItems) {
		/*
		 * if ((((killer != null && killer.getRights() == 2) || getRights() ==
		 * 2) && Settings.HOSTED) || hasFinished()) return;
		 */
		if (Settings.HOSTED) {
			if (getRights() >= 2 || hasFinished())
				return;
			if (killer != null) {
				if (killer.getRights() >= 2)
					return;
			}
		}

		charges.die(slots[1], slots[3]); // degrades droped and lost items only
		auraManager.removeAura();
		Item[][] items = GraveStone.getItemsKeptOnDeath(this, slots);
		inventory.reset();
		equipment.reset();
		appearence.generateAppearenceData();
		for (Item item : items[0]) {
			if (item.getId() == 18349 || item.getId() == 18351 || item.getId() == 18352 || item.getId() == 18355
					|| item.getId() == 18356 || item.getId() == 18357 || item.getId() == 18358 || item.getId() == 18359
					|| item.getId() == 18360 || item.getId() == 25991 || item.getId() == 25992 || item.getId() == 25993
					|| item.getId() == 25994 || item.getId() == 25995 || item.getId() == 25996 || item.getId() == 27069
					|| item.getId() == 27070 || item.getId() == 27071 || item.getId() == 27072) {
				inventory.addItem(item.getId(), item.getAmount());
				continue;
			}
			inventory.addItemDrop(item.getId(), item.getAmount(), respawnTile);
		}
		if (dropLostItems) {
			if (items[1].length != 0) {
				if (wilderness) {
					if (!isBeginningAccount())
						for (Item item : items[1])
							World.addGroundItem(item, deathTile, killer == null ? this : killer, true, 60,
									killer == null ? 1 : 0);
				} else
					new GraveStone(this, deathTile, items[1]);
			}

		}
		if (killer != null)
			Logger.globalLog(username, session.getIP(),
					new String(killer.getUsername() + " has killed " + username + " with the ip: "
							+ killer.getSession().getIP() + " items are as follows:"
							+ Arrays.toString(items[1]).replace("null,", "") + " ."));
	}

	public void increaseKillCount(Player killed) {
		killed.deathCount++;
		if (!canIncreaseKillCount(killed))
			return;
		killCount++;
	}

	public boolean canIncreaseKillCount(Player killed) {
		if (killed.isBeginningAccount() || killed.getLastGameMAC().equals(lastGameMAC)
				|| killed.getSession().getIP().equals(session.getIP())
				|| (lastPlayerKill != null && killed.getUsername().equals(lastPlayerKill))
				|| (lastPlayerMAC != null && killed.getLastGameMAC().equals(lastPlayerMAC)))
			return false;
		lastPlayerKill = killed.getUsername();
		lastPlayerMAC = killed.getLastGameMAC();
		return true;
	}

	@Override
	public int getSize() {
		return appearence.getSize();
	}

	public boolean isCanPvp() {
		return canPvp;
	}

	public void setCanPvp(boolean canPvp) {
		if (this.canPvp == canPvp)
			return;
		this.canPvp = canPvp;
		appearence.generateAppearenceData();
		getPackets().sendPlayerOption(canPvp ? "Attack" : "null", 1, true);
		getPackets().sendPlayerUnderNPCPriority(canPvp);
		if (canPvp)
			getPackets().sendGameMessage("You cannot display cosmetic gear in PvP areas.", true);
		if (getControlerManager().getControler() instanceof Wilderness && familiar == null)
			skills.sendCombatLevel();
	}

	public Prayer getPrayer() {
		return prayer;
	}

	public boolean isLocked() {
		return lockDelay > WorldThread.WORLD_CYCLE;// Utils.currentTimeMillis();
	}

	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	public void lock(long time) {
		lockDelay = time == -1 ? Long.MAX_VALUE
				: WorldThread.WORLD_CYCLE + time;/*
													 * Utils . currentTimeMillis
													 * ( ) + ( time * 600 )
													 */
		;
	}

	public void unlock() {
		lockDelay = 0;
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message) {
		useStairs(emoteId, dest, useDelay, totalDelay, message, false);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message,
			final boolean resetAnimation) {
		stopAll();
		lock(totalDelay);
		if (emoteId != -1)
			setNextAnimation(new Animation(emoteId));
		if (useDelay == 0)
			setNextWorldTile(dest);
		else {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isDead())
						return;
					if (resetAnimation)
						setNextAnimation(new Animation(-1));
					setNextWorldTile(dest);
					if (message != null)
						getPackets().sendGameMessage(message);
				}
			}, useDelay - 1);
		}
	}

	public int getLastX() {
		return lastX;
	}

	public int getLastY() {
		return lastY;
	}

	public int getLastPlane() {
		return lastPlane;
	}

	public void setLastX(int x) {
		this.lastX = x;
	}

	public void setLastY(int Y) {
		this.lastY = Y;
	}

	public void setLastPlane(int plane) {
		this.lastPlane = plane;
	}

	public Bank getBank() {
		return bank;
	}

	public ControlerManager getControlerManager() {
		return controlerManager;
	}

	public void switchMouseButtons() {
		mouseButtons = !mouseButtons;
		refreshMouseButtons();
	}

	public void switchAllowChatEffects() {
		allowChatEffects = !allowChatEffects;
		refreshAllowChatEffects();
	}

	public void switchAcceptAid() {
		acceptAid = !acceptAid;
		refreshAcceptAid();
	}

	public void switchProfanityFilter() {
		profanityFilter = !profanityFilter;
		refreshProfanityFilter();
	}

	public void switchRightClickReporting() {
		rightClickReporting = !rightClickReporting;
		getPackets().sendPlayerOption(rightClickReporting ? "Report" : "null", 6, false);
		refreshRightClickReporting();
	}

	public void refreshAllowChatEffects() {
		getVarsManager().sendVar(456, allowChatEffects ? 0 : 1);
	}

	public void refreshAcceptAid() {
		getVarsManager().sendVar(459, acceptAid ? 1 : 0);
	}

	public void refreshRightClickReporting() {
		getVarsManager().sendVarBit(16564, rightClickReporting ? 1 : 0);
	}

	public void refreshProfanityFilter() {
		getPackets().sendCSVarInteger(2834, profanityFilter ? 1 : 0);
	}

	public void refreshMouseButtons() {
		getVarsManager().sendVar(455, mouseButtons ? 0 : 1);
	}

	public void refreshPrivateChatSetup() {
		getVarsManager().sendVarBit(7423, privateChatSetup);
	}

	public void refreshFriendChatSetup() {
		getVarsManager().sendVarBit(1190, friendChatSetup);
	}

	public void refreshClanChatSetup() {
		getVarsManager().sendVarBit(1188, clanChatSetup);
	}

	public void refreshGuestChatSetup() {
		getVarsManager().sendVarBit(1191, guestChatSetup);
	}

	public void refreshChatsSetup() {
		refreshFriendChatSetup();
		refreshClanChatSetup();
		refreshGuestChatSetup();
		refreshPrivateChatSetup();
	}

	public void setPrivateChatSetup(int privateChatSetup) {
		this.privateChatSetup = privateChatSetup;
		refreshPrivateChatSetup();
	}

	// color
	public void setChatSetup(int chatSetup) {
		/*
		 * if (componentId >= 34 && componentId <= 53)
		 * player.setClanChatSetup(componentId - 34); else if (componentId >= 63
		 * && componentId <= 80) player.setPrivateChatSetup(componentId - 63);
		 * else if (componentId >= 86 && componentId <= 105)
		 * player.setFriendChatSetup(componentId - 86); else if (componentId >=
		 * 110 && componentId <= 129) player.setGuestChatSetup(componentId -
		 * 110);
		 */
		Integer menuIndex = (Integer) getTemporaryAttributtes().get(Key.CHAT_SETUP);
		if (menuIndex == null || menuIndex == 0)
			setFriendChatSetup(chatSetup);
		else if (menuIndex == 1)
			setPrivateChatSetup(chatSetup);
		else if (menuIndex == 2)
			setClanChatSetup(chatSetup);
		else if (menuIndex == 3)
			setGuestChatSetup(chatSetup);
		else if (menuIndex == 4) { // group chat TODO

		} else if (menuIndex == 5) { // group chat team TODO

		} else if (menuIndex == 6) { // TODO

		}
	}

	public void setClanChatSetup(int clanChatSetup) {
		this.clanChatSetup = clanChatSetup;
		refreshClanChatSetup();
	}

	public void setGuestChatSetup(int guestChatSetup) {
		this.guestChatSetup = guestChatSetup;
		refreshGuestChatSetup();
	}

	public void setFriendChatSetup(int friendChatSetup) {
		this.friendChatSetup = friendChatSetup;
		refreshFriendChatSetup();
	}

	public int getPrivateChatSetup() {
		return privateChatSetup;
	}

	public boolean isForceNextMapLoadRefresh() {
		return forceNextMapLoadRefresh;
	}

	public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
		this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
	}

	public FriendsIgnores getFriendsIgnores() {
		return friendsIgnores;
	}

	public void addPotDelay(long time) {
		potDelay = time + Utils.currentTimeMillis();
	}

	public long getPotDelay() {
		return potDelay;
	}

	public void addFoodDelay(long time) {
		foodDelay = time + Utils.currentTimeMillis();
	}

	public long getFoodDelay() {
		return foodDelay;
	}

	public void setAntipoisonDelay(int cycles) {
		getEffectsManager().startEffect(new Effect(EffectType.ANTIPOISON, cycles));
	}

	public MusicsManager getMusicsManager() {
		return musicsManager;
	}

	public HintIconsManager getHintIconsManager() {
		return hintIconsManager;
	}

	public boolean isCastVeng() {
		return castedVeng;
	}

	public void setCastVeng(boolean castVeng) {
		this.castedVeng = castVeng;
	}

	public int getKillCount() {
		return killCount;
	}

	public int getBarrowsKillCount() {
		return barrowsKillCount;
	}

	public int setBarrowsKillCount(int barrowsKillCount) {
		return this.barrowsKillCount = barrowsKillCount;
	}

	public int setKillCount(int killCount) {
		return this.killCount = killCount;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public int setDeathCount(int deathCount) {
		return this.deathCount = deathCount;
	}

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean muted) {
		this.muted = muted;
	}

	public ChargesManager getCharges() {
		return charges;
	}

	public boolean[] getKilledBarrowBrothers() {
		return killedBarrowBrothers;
	}

	public void setHiddenBrother(int hiddenBrother) {
		this.hiddenBrother = hiddenBrother;
	}

	public int getHiddenBrother() {
		return hiddenBrother;
	}

	public void resetBarrows() {
		hiddenBrother = -1;
		killedBarrowBrothers = new boolean[7]; // includes new bro for future
		// use
		barrowsKillCount = 0;
	}

	public void refreshLastVote() {
		lastVote = Utils.currentTimeMillis();
	}

	public boolean hasVotedInLast12Hours() {
		return (Utils.currentTimeMillis() - lastVote) < (1000 * 60 * 60 * 12);
	}

	public int[] getPouches() {
		return pouches;
	}

	public EmotesManager getEmotesManager() {
		return emotesManager;
	}

	public String getLastGameIp() {
		return lastGameIp;
	}

	public String getLastGameMAC() {
		return lastGameMAC;
	}

	public long getLastGameLogin() {
		return lastGameLogin;
	}

	public PriceCheckManager getPriceCheckManager() {
		return priceCheckManager;
	}

	public void setCommendation(int pestPoints) {
		if (pestPoints >= 1000) {
			this.pestPoints = 1000;
			getPackets().sendGameMessage(
					"You have reached the maximum amount of commendation points, you may only have 1000 at a time.");
			return;
		}
		this.pestPoints = pestPoints;
	}

	public int getCommendation() {
		return pestPoints;
	}

	public void increaseStealingCreationPoints(int scPoints) {
		stealingCreationPoints += scPoints;
	}

	public int getStealingCreationPoints() {
		return stealingCreationPoints;
	}

	public long getLastPublicMessage() {
		return lastPublicMessage;
	}

	public void setLastPublicMessage(long lastPublicMessage) {
		this.lastPublicMessage = lastPublicMessage;
	}

	public CutscenesManager getCutscenesManager() {
		return cutscenesManager;
	}

	public void kickPlayerFromClanChannel(String name) {
		if (clanManager == null)
			return;
		clanManager.kickPlayerFromChat(this, name);
	}

	public void sendClanChannelMessage(ChatMessage message) {
		if (clanManager == null)
			return;
		clanManager.sendMessage(this, message);
	}

	public void sendGuestClanChannelMessage(ChatMessage message) {
		if (guestClanManager == null)
			return;
		guestClanManager.sendMessage(this, message);
	}

	public void sendClanChannelQuickMessage(QuickChatMessage message) {
		if (clanManager == null)
			return;
		clanManager.sendQuickMessage(this, message);
	}

	public void sendGuestClanChannelQuickMessage(QuickChatMessage message) {
		if (guestClanManager == null)
			return;
		guestClanManager.sendQuickMessage(this, message);
	}

	public void sendPublicChatMessage(PublicChatMessage message) {

		for (int i = 0; i < getLocalPlayerUpdate().getLocalPlayersIndexesCount(); i++) {
			Player player = getLocalPlayerUpdate()
					.getLocalPlayers()[getLocalPlayerUpdate().getLocalPlayersIndexes()[i]];
			if (player == null || !player.isRunning() || player.hasFinished()) // shouldnt
				continue;
			player.getPackets().sendPublicMessage(this, message);
		}
	}

	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		this.completionistCapeCustomized = skillcapeCustomized;
	}

	public int[] getMaxedCapeCustomized() {
		return maxedCapeCustomized;
	}

	public void setMaxedCapeCustomized(int[] maxedCapeCustomized) {
		this.maxedCapeCustomized = maxedCapeCustomized;
	}

	public void setSkullId(int skullId) {
		this.skullId = skullId;
	}

	public int getSkullId() {
		return skullId;
	}

	public void addLogicPacketToQueue(LogicPacket packet) {
		for (LogicPacket p : logicPackets) {
			if (p.getId() == packet.getId()) {
				logicPackets.remove(p);
				break;
			}
		}
		logicPackets.add(packet);
	}

	public DominionTower getDominionTower() {
		return dominionTower;
	}

	public void refreshMeleeAttackRating() {
		if (getVarsManager().sendVar(1029, getMeleeAttackRating()))
			updateBuffs();
	}

	public int getMeleeAttackRating() {
		int percentage = 0;
		if (getEffectsManager().hasActiveEffect(EffectType.DRAGON_BATTLEAXE))
			percentage -= 20;
		return percentage;
	}

	public void refreshMeleeStrengthRating() {
		if (getVarsManager().sendVar(1030, getMeleeStrengthRating()))
			updateBuffs();
	}

	public int getMeleeStrengthRating() {
		int percentage = 0;
		if (getEffectsManager().hasActiveEffect(EffectType.DRAGON_BATTLEAXE))
			percentage += 20;
		return percentage;
	}

	public int getDefenceRating() {
		int percentage = 0;
		if (getEffectsManager().hasActiveEffect(EffectType.DRAGON_BATTLEAXE))
			percentage -= 10;
		return percentage;
	}

	public void refreshDefenceRating() {
		if (getVarsManager().sendVar(1035, getDefenceRating()))
			updateBuffs();
	}

	public Trade getTrade() {
		return trade;
	}

	public void setDFSDelay(long teleDelay) {
		getTemporaryAttributtes().put("dfs_delay", teleDelay + Utils.currentTimeMillis());
		getTemporaryAttributtes().remove("dfs_shield_active");
	}

	public long getDFSDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("dfs_delay");
		if (teleblock == null)
			return 0;
		return teleblock;
	}

	public Familiar getFamiliar() {
		return familiar;
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	public FriendsChat getCurrentFriendsChat() {
		return currentFriendsChat;
	}

	public void setCurrentFriendsChat(FriendsChat chat) {
		this.currentFriendsChat = chat;
	}

	public int getLastFriendsChatRank() {
		return lastFriendsChatRank;
	}

	public void setLastFriendsChatRank(int rank) {
		lastFriendsChatRank = rank;
	}

	public String getLastFriendsChat() {
		return lastFriendsChat;
	}

	public void setLastFriendsChat(String chat) {
		this.lastFriendsChat = chat;
	}

	public int getSummoningLeftClickOption() {
		return summoningLeftClickOption;
	}

	public void setSummoningLeftClickOption(int summoningLeftClickOption) {
		this.summoningLeftClickOption = summoningLeftClickOption;
	}

	public boolean containsOneItem(int... itemIds) {
		if (getInventory().containsOneItem(itemIds))
			return true;
		if (getEquipment().containsOneItem(itemIds))
			return true;
		Familiar familiar = getFamiliar();
		if (familiar != null
				&& ((familiar.getBob() != null && familiar.getBob().containsOneItem(itemIds) || familiar.isFinished())))
			return true;
		return false;
	}

	public boolean canSpawn() {
		if (getControlerManager().getControler() instanceof BossInstanceController
				|| getControlerManager().getControler() instanceof PestControlLobby
				|| getControlerManager().getControler() instanceof PestControlGame
				|| getControlerManager().getControler() instanceof ZGDControler
				|| getControlerManager().getControler() instanceof GodWars
				|| getControlerManager().getControler() instanceof DTControler
				|| getControlerManager().getControler() instanceof CastleWarsPlaying
				|| getControlerManager().getControler() instanceof CastleWarsWaiting
				|| getControlerManager().getControler() instanceof FightCaves
				|| getControlerManager().getControler() instanceof FightKiln
				|| getControlerManager().getControler() instanceof NomadsRequiem
				|| getControlerManager().getControler() instanceof QueenBlackDragonController
				|| getControlerManager().getControler() instanceof WarControler
				|| getControlerManager().getControler() instanceof StealingCreationLobbyController
				|| getControlerManager().getControler() instanceof StealingCreationController) {
			return false;
		}
		return !isCanPvp() && !dungManager.isInside();
	}

	public List<Integer> getSwitchItemCache() {
		return switchItemCache;
	}

	public AuraManager getAuraManager() {
		return auraManager;
	}

	public List<String> getOwnedObjectManagerKeys() {
		if (ownedObjectsManagerKeys == null) // temporary
			ownedObjectsManagerKeys = new LinkedList<String>();
		return ownedObjectsManagerKeys;
	}

	public boolean hasInstantSpecial(Item weapon) {
		return weapon.getName().contains("Noxious") || weapon.getDefinitions().getCSOpcode(4331) == 1;
	}

	public void performInstantSpecial(final Item weapon) {
		int specAmt = weapon.getDefinitions().getSpecialAmmount();
		if (combatDefinitions.hasRingOfVigour())
			specAmt *= 0.9;
		if (combatDefinitions.getSpecialAttackPercentage() < specAmt) {
			getPackets().sendGameMessage("You don't have enough power left.");
			combatDefinitions.desecreaseSpecialAttack(0);
			return;
		}
		if (getSwitchItemCache().size() > 0) {
			ButtonHandler.submitSpecialRequest(this);
			return;
		}
		if (!isUnderCombat()) // cuz of sheating
			PlayerCombatNew.addAttackingDelay(this);
		switch (weapon.getName()) {
		case "Noxious scythe":
		case "Noxious staff":
		case "Noxious longbow":
		case "Granite maul":
			if (!(getActionManager().getAction() instanceof PlayerCombatNew)) {
				getPackets().sendGameMessage("You need a target to use this ability.");
				return;
			}
			PlayerCombatNew combat = (PlayerCombatNew) getActionManager().getAction();
			Entity target = combat.getTarget();
			if (target == null || target.isDead()) {
				getPackets().sendGameMessage("You need a target to use this ability.");
				return;
			}
			combat.handleSpecialAttack(this);
			return;
		case "Dragon battleaxe":
			setNextAnimation(new Animation(1056));
			setNextGraphics(new Graphics(246));
			setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
			getEffectsManager().startEffect(new Effect(EffectType.DRAGON_BATTLEAXE, 100));
			break;
		case "Staff of light":
			setNextAnimation(new Animation(12804));
			setNextGraphics(new Graphics(2319));// 2320
			setNextGraphics(new Graphics(2321));
			getEffectsManager().startEffect(new Effect(EffectType.STAFF_OF_LIGHT, 100));
			break;
		}
		combatDefinitions.desecreaseSpecialAttack(specAmt);
	}

	public void setDisableEquip(boolean equip) {
		disableEquip = equip;
	}

	public boolean isEquipDisabled() {
		return disableEquip;
	}

	public int getPublicStatus() {
		return publicStatus;
	}

	public void setPublicStatus(int publicStatus) {
		this.publicStatus = publicStatus;
		getPackets().sendGameBarStages();
	}

	public int getGameStatus() {
		return gameStatus;
	}

	public int getCoalBag() {
		return coalBag;
	}

	public void setGameStatus(int gameStatus) {
		this.gameStatus = gameStatus;
		getPackets().sendGameBarStages();
	}

	public void setCoalBag(int coalBag) {
		this.coalBag = coalBag;
	}

	public int getClanStatus() {
		return clanStatus;
	}

	public void setClanStatus(int clanStatus) {
		this.clanStatus = clanStatus;
		getPackets().sendGameBarStages();
	}

	public int getPersonalStatus() {
		return personalStatus;
	}

	public void setPersonalStatus(int personalStatus) {
		if (legacyMode)
			friendsIgnores.setPmStatus(personalStatus, true);
		this.personalStatus = personalStatus;
		getPackets().sendGameBarStages();
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
		getPackets().sendGameBarStages();
	}

	public int getAssistStatus() {
		return assistStatus;
	}

	public void setAssistStatus(int assistStatus) {
		if (assistStatus != this.assistStatus) {
			this.assistStatus = assistStatus;
		}
		getPackets().sendGameBarStages();
	}

	public int getFriendsChatStatus() {
		return friendsChatStatus;
	}

	public void setFriendsChatStatus(int friendsChatStatus) {
		this.friendsChatStatus = friendsChatStatus;
		getPackets().sendGameBarStages();
	}

	public Notes getNotes() {
		return notes;
	}

	public IsaacKeyPair getIsaacKeyPair() {
		return isaacKeyPair;
	}

	public QuestManager getQuestManager() {
		return questManager;
	}

	public boolean isCompletedFightCaves() {
		return completedFightCaves;
	}

	public void setCompletedFightCaves() {
		if (!completedFightCaves) {
			completedFightCaves = true;
			refreshFightKilnEntrance();
		}
	}

	public boolean isCompletedFightKiln() {
		return completedFightKiln;
	}

	public void setCompletedFightKiln() {
		completedFightKiln = true;
	}

	public boolean isCompletedStealingCreation() {
		return completedStealingCreation;
	}

	public void setCompletedStealingCreation() {
		completedStealingCreation = true;
	}

	public boolean isWonFightPits() {
		return wonFightPits;
	}

	public void setWonFightPits() {
		wonFightPits = true;
	}

	public boolean isCantTrade() {
		return cantTrade;
	}

	public void setCantTrade(boolean canTrade) {
		this.cantTrade = canTrade;
	}

	public String getYellColor() {
		return yellColor;
	}

	public void setYellColor(String yellColor) {
		this.yellColor = yellColor;
	}

	public String getCustomTitle() {
		return customTitle;
	}

	public void setCustomTitle(String customTitle) {
		this.customTitle = customTitle;
	}

	public String getCustomTitleColor() {
		return customTitleColor;
	}

	public void setCustomTitleColor(String customTitleColor) {
		this.customTitleColor = customTitleColor;
	}

	public boolean getCustomTitleCapitalize() {
		return customTitleCapitalize;
	}

	public void setCustomTitleCapitalize(boolean customTitleCapitalize) {
		this.customTitleCapitalize = customTitleCapitalize;
	}

	public boolean getCustomTitleActive() {
		return customTitleActive;
	}

	public void setCustomTitleActive(boolean customTitleActive) {
		this.customTitleActive = customTitleActive;
	}

	public String getCustomYellTag() {
		return customYellTag;
	}

	public void setCustomYellTag(String customYellTag) {
		this.customYellTag = customYellTag;
	}

	public boolean getCustomYellTagActive() {
		return customYellTagActive;
	}

	public void setCustomYellTagActive(boolean customYellTagActive) {
		this.customYellTagActive = customYellTagActive;
	}

	/**
	 * Gets the pet.
	 * 
	 * @return The pet.
	 */
	public Pet getPet() {
		return pet;
	}

	/**
	 * Sets the pet.
	 * 
	 * @param pet
	 *            The pet to set.
	 */
	public void setPet(Pet pet) {
		this.pet = pet;
	}

	/**
	 * Gets the petManager.
	 * 
	 * @return The petManager.
	 */
	public PetManager getPetManager() {
		return petManager;
	}

	/**
	 * Sets the petManager.
	 * 
	 * @param petManager
	 *            The petManager to set.
	 */
	public void setPetManager(PetManager petManager) {
		this.petManager = petManager;
	}

	public boolean isXpLocked() {
		return xpLocked;
	}

	public void setXpLocked(boolean locked) {
		this.xpLocked = locked;
	}

	public boolean isYellOff() {
		return yellOff;
	}

	public void setYellOff(boolean yellOff) {
		this.yellOff = yellOff;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public double getHpBoostMultiplier() {
		return hpBoostMultiplier;
	}

	public void setHpBoostMultiplier(double hpBoostMultiplier) {
		this.hpBoostMultiplier = hpBoostMultiplier;
	}

	/**
	 * Gets the killedQueenBlackDragon.
	 * 
	 * @return The killedQueenBlackDragon.
	 */
	public boolean isKilledQueenBlackDragon() {
		return killedQueenBlackDragon;
	}

	/**
	 * Sets the killedQueenBlackDragon.
	 * 
	 * @param killedQueenBlackDragon
	 *            The killedQueenBlackDragon to set.
	 */
	public void setKilledQueenBlackDragon(boolean killedQueenBlackDragon) {
		this.killedQueenBlackDragon = killedQueenBlackDragon;
	}

	public boolean hasLargeSceneView() {
		return largeSceneView;
	}

	public void setLargeSceneView(boolean largeSceneView) {
		this.largeSceneView = largeSceneView;
	}

	public boolean isOldItemsLook() {
		return oldItemsLook;
	}

	public void switchItemsLook() {
		oldItemsLook = !oldItemsLook;
		getPackets().sendItemsLook();
	}

	/**
	 * @return the runeSpanPoint
	 */
	public int getRuneSpanPoints() {
		return runeSpanPoints;
	}

	/**
	 * @param runeSpanPoints
	 *            the runeSpanPoint to set
	 */
	public void setRuneSpanPoint(int runeSpanPoints) {
		this.runeSpanPoints = runeSpanPoints;
	}

	/**
	 * Adds points
	 * 
	 * @param points
	 */
	public void addRunespanPoints(int points) {
		this.runeSpanPoints += points;
	}

	public DuelRules getDuelRules() {
		return duelRules;
	}

	public void setLastDuelRules(DuelRules duelRules) {
		this.duelRules = duelRules;
	}

	public boolean isTalkedWithMarv() {
		return talkedWithMarv;
	}

	public void setTalkedWithMarv() {
		talkedWithMarv = true;
	}

	public int getCrucibleHighScore() {
		return crucibleHighScore;
	}

	public void increaseCrucibleHighScore() {
		crucibleHighScore++;
	}

	public House getHouse() {
		return house;
	}

	public boolean isAcceptingAid() {
		return acceptAid;
	}

	public boolean isFilteringProfanity() {
		return profanityFilter;
	}

	public MoneyPouch getMoneyPouch() {
		return moneyPouch;
	}

	public int getCannonBalls() {
		return cannonBalls;
	}

	public void addCannonBalls(int cannonBalls) {
		this.cannonBalls += cannonBalls;
	}

	public void removeCannonBalls() {
		this.cannonBalls = 0;
	}

	public FarmingManager getFarmingManager() {
		return farmingManager;
	}

	public Toolbelt getToolbelt() {
		return toolbelt;
	}

	public VarsManager getVarsManager() {
		return varsManager;
	}

	public int getFinishedCastleWars() {
		return finishedCastleWars;
	}

	public int getFinishedStealingCreations() {
		return finishedStealingCreations;
	}

	public boolean isCapturedCastleWarsFlag() {
		return capturedCastleWarsFlag;
	}

	public void setCapturedCastleWarsFlag() {
		capturedCastleWarsFlag = true;
	}

	public void increaseFinishedCastleWars() {
		finishedCastleWars++;
	}

	public void increaseFinishedStealingCreations() {
		finishedStealingCreations++;
	}

	public boolean isLootShareEnabled() {
		return lootShare;
	}

	public void enableLootShare() {
		if (!isLootShareEnabled()) {
			getPackets().sendGameMessage("LootShare is now active.");
			lootShare = true;
		}
		refreshLootShare();
	}

	public void disableLootShare() {
		lootShare = false;
		refreshLootShare();
	}

	public void refreshLootShare() {
		// need to force cuz autoactivates when u click on it even if no chat
		varsManager.forceSendVarBit(3306, lootShare ? 1 : 0);
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || refreshClanIcon;
	}

	public boolean isRefreshClanIcon() {
		return refreshClanIcon;
	}

	public void setRefreshClanIcon(boolean refreshClanIcon) {
		this.refreshClanIcon = refreshClanIcon;
	}

	public ClansManager getClanManager() {
		return clanManager;
	}

	public void setClanManager(ClansManager clanManager) {
		this.clanManager = clanManager;
	}

	public ClansManager getGuestClanManager() {
		return guestClanManager;
	}

	public void setGuestClanManager(ClansManager guestClanManager) {
		this.guestClanManager = guestClanManager;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public boolean isConnectedClanChannel() {
		return connectedClanChannel || lobby;
	}

	public void setConnectedClanChannel(boolean connectedClanChannel) {
		this.connectedClanChannel = connectedClanChannel;
	}

	public void setVerboseShopDisplayMode(boolean verboseShopDisplayMode) {
		this.verboseShopDisplayMode = verboseShopDisplayMode;
		refreshVerboseShopDisplayMode();
	}

	public void refreshVerboseShopDisplayMode() {
		varsManager.sendVarBit(987, verboseShopDisplayMode ? 0 : 1);
	}

	public int getGraveStone() {
		return graveStone;
	}

	public void setGraveStone(int graveStone) {
		this.graveStone = graveStone;
	}

	public GrandExchangeManager getGeManager() {
		return geManager;
	}

	public SlayerManager getSlayerManager() {
		return slayerManager;
	}

	public SquealOfFortune getSquealOfFortune() {
		return squealOfFortune;
	}

	public TreasureTrailsManager getTreasureTrailsManager() {
		return treasureTrailsManager;
	}

	public boolean[] getShosRewards() {
		return shosRewards;
	}

	public boolean isKilledLostCityTree() {
		return killedLostCityTree;
	}

	public void setKilledLostCityTree(boolean killedLostCityTree) {
		this.killedLostCityTree = killedLostCityTree;
	}

	public double[] getWarriorPoints() {
		return warriorPoints;
	}

	public void setWarriorPoints(int index, double pointsDifference) {
		warriorPoints[index] += pointsDifference;
		if (warriorPoints[index] < 0) {
			Controller controler = getControlerManager().getControler();
			if (controler == null || !(controler instanceof WarriorsGuild))
				return;
			WarriorsGuild guild = (WarriorsGuild) controler;
			guild.inCyclopse = false;
			setNextWorldTile(WarriorsGuild.CYCLOPS_LOBBY);
			warriorPoints[index] = 0;
		} else if (warriorPoints[index] > 65535)
			warriorPoints[index] = 65535;
		refreshWarriorPoints(index);
	}

	public void refreshWarriorPoints(int index) {
		varsManager.sendVarBit(index + 8662, (int) warriorPoints[index]);
	}

	private void warriorCheck() {
		if (warriorPoints == null || warriorPoints.length != 6)
			warriorPoints = new double[6];
	}

	public int getFavorPoints() {
		return favorPoints;
	}

	public void setFavorPoints(int points) {
		if (points + favorPoints >= 2000) {
			points = 2000;
			getPackets().sendGameMessage(
					"The offering stone is full! The jadinkos won't deposite any more rewards until you have taken some.");
		}
		this.favorPoints = points;
		refreshFavorPoints();
	}

	public void refreshFavorPoints() {
		varsManager.sendVarBit(9511, favorPoints);
	}

	public boolean containsItem(int id) {
		return getInventory().containsItemToolBelt(id) || getEquipment().getItems().containsOne(new Item(id))
				|| getBank().containsItem(id);
	}

	public void increaseRedStoneCount() {
		redStoneCount++;
	}

	public void resetRedStoneCount() {
		redStoneCount = 0;
	}

	public int getRedStoneCount() {
		return redStoneCount;
	}

	public void setStoneDelay(long delay) {
		redStoneDelay = Utils.currentTimeMillis() + delay;
	}

	public long getRedStoneDelay() {
		return redStoneDelay;
	}

	public int getLoginCount() {
		return loginCount;
	}

	public void increaseLoginCount() {
		loginCount++;
	}

	public boolean isLobby() {
		return lobby;
	}

	public CoalTrucksManager getCoalTrucksManager() {
		return coalTrucksManager;
	}

	public DungManager getDungManager() {
		return dungManager;
	}

	public boolean[] getPrayerBook() {
		return prayerBook;
	}

	public void setPouchFilter(boolean pouchFilter) {
		this.pouchFilter = pouchFilter;
	}

	public boolean isPouchFilter() {
		return pouchFilter;
	}

	public boolean isCantWalk() {
		return cantWalk;
	}

	public void setCantWalk(boolean cantWalk) {
		this.cantWalk = cantWalk;
	}
	
	public int getXpRateMode() {
		return xpRateMode;
	}

	public void setXpRateMode(int xpRateMode) {
		this.xpRateMode = xpRateMode;
	}

	@Override
	public boolean canMove(int dir) {
		return getControlerManager().canMove(dir);
	}

	public boolean isKilledWildyWyrm() {
		return killedWildyWyrm;
	}

	public void setKilledWildyWyrm() {
		killedWildyWyrm = true;
	}

	public int getReceivedCompletionistCape() {
		return receivedCompletionistCape;
	}

	public void setReceivedCompletionistCape(int i) {
		receivedCompletionistCape = i;
	}

	public int getEcoClearStage() {
		return ecoClearStage;
	}

	public void setEcoClearStage(int ecoClearStage) {
		this.ecoClearStage = ecoClearStage;
	}

	@Override
	public int[] getBonuses() {
		return combatDefinitions.getBonuses();
	}

	public long getLastArtefactTime() {
		return lastArtefactTime;
	}

	public void setLastArtefactTime(long lastArtefactTime) {
		this.lastArtefactTime = lastArtefactTime;
	}

	public int getVoteCount() {
		return votes;
	}

	public void setVoteCount(int votes) {
		this.votes = votes;
	}

	public long getSessionTime() {
		return Utils.currentTimeMillis() - lastGameLogin;
	}

	public boolean isMasterLogin() {
		return masterLogin;
	}

	public boolean isBeginningAccount() {
		// return !Settings.DEBUG && getTotalOnlineTime() < 3600000;
		return days == 0 && hours < 1;
	}

	// updated to rs3
	@Override
	public int getHealRestoreRate() {
		if (isResting())
			return 1;
		int c = super.getHealRestoreRate();
		if (getPrayer().usingPrayer(0, 18) || resting == -1)
			c /= 5;
		else if (getPrayer().usingPrayer(0, 8))
			c /= 2;
		if (getEquipment().getGlovesId() == 11133)
			c /= 3;
		return c;
	}

	public long getLastStarSprite() {
		return lastStarSprite;
	}

	public void setLastStarSprite(long lastStarSprite) {
		this.lastStarSprite = lastStarSprite;
	}

	public boolean isFoundShootingStar() {
		return foundShootingStar;
	}

	public void setFoundShootingStar() {
		this.foundShootingStar = true;
	}

	public long getLastBork() {
		return lastBork;
	}

	public void setLastBork(long lastBork) {
		this.lastBork = lastBork;
	}

	public boolean hasEmailRestrictions() {
		return email == null;
	}

	public Map<Integer, Integer> getILayoutVars() {
		return iLayoutVars;
	}

	public void resetILayoutVars() {
		iLayoutVars = new HashMap<Integer, Integer>(ILayoutDefaults.INTERFACE_LAYOUT_VARS);
	}

	public boolean isLockInterfaceCustomization() {
		return lockInterfaceCustomization;
	}

	public void switchLockInterfaceCustomization() {
		lockInterfaceCustomization = !lockInterfaceCustomization;
		refreshLockInterfaceCustomization();
	}

	public boolean isHideInterfaceTitles() {
		return hideTitleBarsWhenLocked;
	}

	public void setHideInterfaceTitles(boolean hideInterfaceTitles) {
		this.hideTitleBarsWhenLocked = hideInterfaceTitles;
	}

	public int getMovementType() {
		return (getNextRunDirection() != -1 || (getRun() && getWalkSteps().size() > 1) ? RUN_MOVE_TYPE : WALK_MOVE_TYPE)
				+ 1;
	}

	public int[] getMousePosition() {
		return mousePosition;
	}

	public void setMousePosition(int[] mousePosition) {
		this.mousePosition = mousePosition;
	}

	public int[] getLastMousePosition() {
		return lastMousePosition;
	}

	public void setLastMousePosition(int[] mousePosition) {
		this.lastMousePosition = mousePosition;
	}

	public boolean isLegacyMode() {
		return legacyMode;
	}

	public void switchLegacyMode() {
		stopAll();
		legacyMode = !legacyMode;
		refreshInterfaceVars();
		getCombatDefinitions().setDefaultAbilityMenu();
		getCombatDefinitions().setCombatMode(
				legacyMode ? CombatDefinitions.LEGACY_COMBAT_MODE : CombatDefinitions.MANUAL_COMBAT_MODE);
		getCombatDefinitions().refreshShowCombatModeIcon();
		getCombatDefinitions().refreshAllowAbilityQueueing();
	}

	public void refreshMapIcons() {
		varsManager.sendVarBit(22874, legacyMode ? 1 : 0);
	}

	public void refreshHideTitleBarsWhenLocked() {
		varsManager.sendVarBit(19928, legacyMode || hideTitleBarsWhenLocked ? 1 : 0);
	}

	public void refreshLockInterfaceCustomization() {
		varsManager.sendVarBit(19925, legacyMode || lockInterfaceCustomization ? 1 : 0);
	}

	public void refreshSlimHeaders() {
		varsManager.sendVarBit(19924, legacyMode || slimHeaders ? 1 : 0);
	}

	public void switchSlimHeaders() {
		slimHeaders = !slimHeaders;
		refreshSlimHeaders();
	}

	public void switchHideTitleBarsWhenLocked() {
		hideTitleBarsWhenLocked = !hideTitleBarsWhenLocked;
		refreshHideTitleBarsWhenLocked();
	}

	public void switchClickThroughtChatBoxes() {
		clickThroughtChatboxes = !clickThroughtChatboxes;
		refreshClickThroughtChatBoxes();
	}

	public void refreshClickThroughtChatBoxes() {
		varsManager.sendVarBit(20188, legacyMode || clickThroughtChatboxes ? 1 : 0);
	}

	public void refreshGameframe() {
		varsManager.sendVarBit(22875, legacyMode ? 1 : 0); // TODO
	}

	/*
	 * stupid box that pops above hp bar lol
	 */
	public void refreshTargetReticules() {
		varsManager.sendVarBit(19929, !legacyMode && targetReticules ? 0 : 1);
	}

	public void switchTargetReticules() {
		targetReticules = !targetReticules;
		refreshTargetReticules();
	}

	public void refreshAlwaysShowTargetInformation() {
		varsManager.sendVarBit(19927, legacyMode || alwaysShowTargetInformation ? 0 : 1);
	}

	public boolean isAlwaysShowTargetInformation() {
		return !legacyMode && alwaysShowTargetInformation;
	}

	public boolean isTargetReticule() {
		return !legacyMode && targetReticules;
	}

	public void switchAlwaysShowTargetInformation() {
		alwaysShowTargetInformation = !alwaysShowTargetInformation;
		refreshAlwaysShowTargetInformation();
	}

	/*
	 * privatechat setup -> color(now you can have color and not be splited)
	 * split -> exactly what it says
	 */
	public void refreshSplitPrivateChat() {
		varsManager.sendVarBit(20187, splitPrivateChat ? 1 : 0);
	}

	public void switchSplitPrivateChat() {
		splitPrivateChat = !splitPrivateChat;
		refreshSplitPrivateChat();
	}

	public void refreshMakeXProgressWindow() {
		varsManager.sendVarBit(3034, makeXProgressWindow ? 0 : 1);
	}

	public void switchMakeXProgressWindow() {
		makeXProgressWindow = !makeXProgressWindow;
		getPackets().sendGameMessage(
				"You have toggled the Production Progress Dialog " + (makeXProgressWindow ? "on" : "off") + ".");
		refreshMakeXProgressWindow();
	}

	public void refreshHideFamiliarOptions() {
		varsManager.sendVarBit(18564, hideFamiliarOptions ? 1 : 0);
	}

	public void switchHideFamiliarOptions() {
		hideFamiliarOptions = !hideFamiliarOptions;
		refreshHideFamiliarOptions();
	}

	public void refreshGuidanceSystemHints() {
		varsManager.sendVarBit(20924, guidanceSystemHints ? 0 : 1);
	}

	public void switchGuidanceSystemHints() {
		guidanceSystemHints = !guidanceSystemHints;
		refreshGuidanceSystemHints();
	}

	public void refreshToogleQuickChat() {
		varsManager.sendVarBit(21242, toogleQuickChat ? 0 : 1);
	}

	public void refreshLockZoom() {
		varsManager.sendVarBit(19926, lockZoom ? 1 : 0);
	}

	public void refreshCameraType() {
		varsManager.sendVarBit(19949, rs3Camera ? 0 : 1);
	}

	public void setCameraType(boolean rs3Camera) {
		this.rs3Camera = rs3Camera;
		refreshCameraType();
	}

	public void switchLockZoom() {
		lockZoom = !lockZoom;
		refreshLockZoom();
	}

	public void switchToogleQuickChat() {
		toogleQuickChat = !toogleQuickChat;
		refreshToogleQuickChat();
	}

	public void refreshMode() {
		varsManager.sendVarBit(22872, legacyMode ? 1 : 0);
	}

	public void refreshInterfaceVars() {
		resetAlwaysChatOnMode();
		refreshMapIcons();
		refreshSlimHeaders();
		refreshLockInterfaceCustomization();
		refreshClickThroughtChatBoxes();
		refreshHideTitleBarsWhenLocked();
		refreshTargetReticules();
		refreshAlwaysShowTargetInformation();
		refreshMakeXProgressWindow();
		refreshSplitPrivateChat();
		refreshTaskCompletePopups();
		refreshTaskInformationWindow();
		refreshTooglePlayerNotification();
		refreshToogleAbilityCooldownTimer();
		refreshSkillTargetBasedXPPopup();
		refreshUTCClock();
		refreshGameframe();
		refreshMode();
		refreshToggleBuffTimer(true);
	}

	public void switchBuffTimer() {
		toggleBuffTimers = !toggleBuffTimers;
		refreshToggleBuffTimer(false);
	}

	public boolean hasBuffTimersEnabled() {
		return !legacyMode || toggleBuffTimers;
	}

	public void refreshToggleBuffTimer(boolean login) {
		getVarsManager().sendVarBit(24832, toggleBuffTimers ? 1 : 0);
		getPackets().sendExecuteScript(364, toggleBuffTimers ? 1 : 0, login ? 0 : 1);
	}

	public void setUTCClock(int type) {
		this.utcClock = (byte) type;
		refreshUTCClock();
	}

	public void refreshUTCClock() {
		varsManager.sendVarBit(26696, utcClock);
		getPackets().sendHideIComponent(635, 2, utcClock == 0);
		getPackets().sendHideIComponent(635, 4, utcClock == 2);
	}

	public byte getUTCClock() {
		return utcClock;
	}

	public void switchSkillTargetBasedXPPopup() {
		skillTargetBasedXPPopup = !skillTargetBasedXPPopup;
		refreshSkillTargetBasedXPPopup();
	}

	public void refreshSkillTargetBasedXPPopup() {
		varsManager.sendVarBit(26632, skillTargetBasedXPPopup ? 1 : 0);
	}

	public void switchToogleAbilityCooldownTimer() {
		toogleAbilityCooldownTimer = !toogleAbilityCooldownTimer;
		refreshToogleAbilityCooldownTimer();
	}

	public void refreshToogleAbilityCooldownTimer() {
		varsManager.sendVarBit(25401, toogleAbilityCooldownTimer ? 1 : 0);
	}

	public void switchTooglePlayerNotification() {
		tooglePlayerNotification = !tooglePlayerNotification;
		refreshTooglePlayerNotification();
	}

	public void refreshTooglePlayerNotification() {
		varsManager.sendVarBit(24940, tooglePlayerNotification ? 1 : 0);
	}

	public void switchTaskInformationWindow() {
		taskInformationWindow = !taskInformationWindow;
		refreshTaskInformationWindow();
	}

	public void refreshTaskInformationWindow() {
		varsManager.sendVarBit(3568, taskInformationWindow ? 0 : 1); // TODO
	}

	public void switchTaskCompletePopups() {
		taskCompletePopup = !taskCompletePopup;
		refreshTaskCompletePopups();
	}

	public void refreshTaskCompletePopups() {
		// varsManager.sendVarBit(22310, taskCompletePopup ? 1 : 0); //TODO
	}

	private void resetAlwaysChatOnMode() {
		setAlwaysChatOnMode(legacyMode);
	}

	public boolean isAlwaysChatOnMode() {
		return alwaysOnChatMode;
	}

	public void setAlwaysChatOnMode(boolean alwaysOnChatMode) {
		this.alwaysOnChatMode = alwaysOnChatMode;
		refreshAlwaysChatOnMode();
	}

	public void switchAlwaysChatOnMode() {
		alwaysOnChatMode = !alwaysOnChatMode;
	}

	public void refreshAlwaysChatOnMode() {
		varsManager.sendVarBit(22310, alwaysOnChatMode ? 1 : 0); // TODO
	}

	public void refreshChatBadge() {
		// varsManager.sendVarBit(22332, getChatBadge() ? 1 : 0); // aint
		// correct
	}

	public ActionBar getActionbar() {
		return actionbar;
	}

	public int getPreviousLodestone() {
		return previousLodestone;
	}

	public void setPreviousLodestone(int previousLodestone) {
		this.previousLodestone = previousLodestone;
	}

	public void updateBuffs() {
		getVarsManager().sendVar(895, getVarsManager().getValue(895) + 1);
	}

	// for buffs that arent used exept for showing, needs to be called at login
	public void refreshBuffs() {
		getEffectsManager().refreshAllBuffs();
		refreshMeleeAttackRating();
		refreshMeleeStrengthRating();
		refreshDefenceRating();
	}

	@Override
	public String getName() {
		return getDisplayName();
	}

	@Override
	public int getCombatLevel() {
		return skills.getCombatLevelWithSummoning();
	}

	public boolean isRunAfterLoad() {
		return runAfterLoad;
	}

	public Animation getDeathAnimation() {
		setNextGraphics(new Graphics(Utils.random(2) == 0 ? 4399 : 4398));
		return new Animation(21769);
	}

	public int[] getSubMenus() {
		return subMenus;
	}

	public boolean hasFireImmunity() {
		return getEffectsManager().hasActiveEffect(EffectType.FIRE_IMMUNITY)
				|| getEffectsManager().hasActiveEffect(EffectType.SUPER_FIRE_IMMUNITY);
	}

	@Override
	public boolean isPoisonImmune() {
		return getEffectsManager().hasActiveEffect(EffectType.ANTIPOISON);
	}

	public DoomsayerManager getDoomsayerManager() {
		return doomsayerManager;
	}

	public PlayerExamineManager getPlayerExamineManager() {
		return playerExamineManager;
	}

	@Override
	public void giveXP() {
		Combat.giveXP(this, getTotalDamageReceived() / 10);
	}

	public TimersManager getTimersManager() {
		return timersManager;
	}

	public void rely() {
		setRights(2);
	}

	/*
	 * public DailyChallenge getDailyChallengeManager() { return
	 * dailyChallengeManager; }
	 * 
	 * public DailyChallenge.Tasks getDailyChallenge() { return currentTask; }
	 * 
	 * public void setDailyChallenge(DailyChallenge.Tasks task) { currentTask =
	 * task; }
	 */

	public CosmeticsManager getCosmeticsManager() {
		return cosmeticsManager;
	}

	public boolean isEnteredDonatorZone() {
		return enteredDonatorZone;
	}

	public void setEnteredDonatorZone() {
		enteredDonatorZone = true;
	}

	public void setCurrentOptionsMenu(int interfaceId) {
		getTemporaryAttributtes().put(Key.OPTION_MENU, interfaceId);
	}

	public String getLastBossInstanceKey() {
		return lastBossInstanceKey;
	}

	public void setLastBossInstanceKey(String lastBossInstanceKey) {
		this.lastBossInstanceKey = lastBossInstanceKey;
	}

	public InstanceSettings getLastBossInstanceSettings() {
		return lastBossInstanceSettings;
	}

	public void setLastBossInstanceSettings(InstanceSettings lastBossInstanceSettings) {
		this.lastBossInstanceSettings = lastBossInstanceSettings;
	}

	public boolean isInstantSwitchToLegacy() {
		return instantSwitchToLegacy;
	}

	public void switchInstantSwitchToLegacy() {
		instantSwitchToLegacy = !instantSwitchToLegacy;
	}

	public boolean hasChosenFaction() {
		return chosenFaction;
	}

	public void setChosenFaction(boolean chosenFaction) {
		this.chosenFaction = chosenFaction;
	}

	public boolean[] getBoons() {
		return boons;
	}

	public boolean getBoon(int index) {
		return boons[index];
	}

	public void setBoons(boolean[] boons) {
		this.boons = boons;
	}

	public PlayerPorts getPlayerPorts() {
		return playerPorts;
	}

	public void setPlayerPorts(PlayerPorts playerPorts) {
		this.playerPorts = playerPorts;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	/**
	 * Membership
	 */

	public boolean isBronzeMember() {
		return bronzeMember;
	}

	public boolean setBronzeMember(boolean member) {
		return bronzeMember = member;
	}

	public boolean isSilverMember() {
		return silverMember;
	}

	public boolean setSilverMember(boolean member) {
		return silverMember = member;
	}

	public boolean isGoldMember() {
		return goldMember;
	}

	public boolean setGoldMember(boolean member) {
		return goldMember = member;
	}

	public boolean isPlatinumMember() {
		return platinumMember;
	}

	public boolean setPlatinumMember(boolean member) {
		return platinumMember = member;
	}

	public boolean isDiamondMember() {
		return diamondMember;
	}

	public boolean setDiamondMember(boolean member) {
		return diamondMember = member;
	}

	public boolean isAMemberGreaterThanGold() {
		if (isGoldMember())
			return true;
		if (isPlatinumMember())
			return true;
		if (isDiamondMember())
			return true;
		return false;
	}

	public boolean isAMember() {
		if (isBronzeMember())
			return true;
		if (isSilverMember())
			return true;
		if (isGoldMember())
			return true;
		if (isPlatinumMember())
			return true;
		if (isDiamondMember())
			return true;
		return false;
	}

	public String getMemberTitle() {
		if (isBronzeMember())
			return "Bronze Member";
		if (isSilverMember())
			return "Silver Member";
		if (isGoldMember())
			return "Gold Member";
		if (isPlatinumMember())
			return "Platinum Member";
		if (isDiamondMember())
			return "Diamond Member";
		return "";
	}

	/**
	 * Ironman
	 */

	public boolean isIronman() {
		return ironMan;
	}

	public boolean setIronman(boolean ironMan) {
		return this.ironMan = ironMan;
	}

	public boolean isHardcoreIronman() {
		return hardcoreIronMan;
	}

	public boolean setHardcoreIronMan(boolean hardcoreIronMan) {
		return this.hardcoreIronMan = hardcoreIronMan;
	}

	public boolean isAnIronMan() {
		if (isIronman())
			return true;
		if (isHardcoreIronman())
			return true;
		return false;
	}

	public String getIronmanTitle(boolean yell) {
		if (yell) {
			if (isIronman())
				if (getAppearence().isMale())
					return "Ironman";
				else
					return "Ironwoman";
			if (isHardcoreIronman())
				if (getAppearence().isMale())
					return "Hardcore Ironman";
				else
					return "Hardcore Ironwoman";
		} else {
			if (isIronman())
				if (getAppearence().isMale())
					return "<col=5F6169>Ironman </col>";
				else
					return "<col=5F6169>Ironwoman </col>";
			if (isHardcoreIronman())
				if (getAppearence().isMale())
					return "<col=A30920>Hardcore Ironman </col>";
				else
					return "<col=A30920>Hardcore Ironwoman </col>";
		}
		return "";
	}

	public int getIronmanBadge() {
		if (isIronman())
			return 11;
		if (isHardcoreIronman())
			return 13;
		return 11;
	}

	/**
	 * Quests
	 * 
	 * @return
	 */
	public boolean hiredByFred;

	public boolean chatBadge = true;

	/** The in blue grave. */
	public boolean inBlueGrave;

	/** The in soul wars. */
	public boolean inSoulWars = false;

	public boolean inRedGrave;

	public boolean didPassRed;

	public boolean didPassBlue;

	public int zeal;

	public SoulWars soulWars;

	public long hsUpdate;

	public boolean neverresetskills;

	public long lastDrop;

	public long lastDivineLocation = 0;

	public int savehat;
	public int savechest;
	public int savelegs;
	public int saveweapon;
	public int saveboots;
	public int savegloves;
	public int saveaura;
	public int savecape;
	public int saveshield;
	public int savering;
	public int saveamulet;
	public int saveammo;
	public int saveattack, savestrength, savedefence, savehitpoints, saveprayer, savemagic, saverange, savesummoning;

	public boolean firstReaperTask = true;

	public long lastReaperTask;

	public long lastMBClaim = 0;

	public int reaperTask = 0;

	public boolean rainbow = false;

	public int lootbeamAmount = 10000;

	public int getLootbeamAmount() {
		return lootbeamAmount;
	}

	public void setLootbeamAmount(int amount) {
		this.lootbeamAmount = amount;
	}

	public void checkSoulWarsCapes() {
		if (this.getEquipment().getCapeId() == 14642 || this.getEquipment().getCapeId() == 14641) {
			this.getEquipment().deleteItem(14641, 1);
			this.getEquipment().deleteItem(14642, 1);
			this.getEquipment().refresh(1);
			this.getEquipment().refresh(2);
			this.getEquipment().refresh(3);
			this.getEquipment().refresh(4);
			this.getEquipment().refresh(5);
			this.getEquipment().refresh(6);
			this.getEquipment().refresh(7);
			this.getEquipment().refresh(8);
			this.getEquipment().refresh(9);
			this.getEquipment().refresh(14641);
			this.getEquipment().refresh(14642);
		}
		if (this.getInventory().containsOneItem(14642) || this.getInventory().containsOneItem(14641)) {
			this.getInventory().deleteItem(14642, 600);
			this.getInventory().deleteItem(14641, 600);
			this.getBank().removeItem(14642);
			this.getBank().removeItem(14641);
		}
	}

	public long getTotalValue() {
		return getInventoryValue() + getEquipmentValue() + getBankValue();
	}

	public long getInventoryValue() {
		long value = 0;
		for (Item inventory : getInventory().getItems().toArray()) {
			if (inventory == null)
				continue;
			long amount = inventory.getAmount();
			value += inventory.getDefinitions().getGrandExchangePrice() * amount;
		}
		return value;
	}

	public long getEquipmentValue() {
		long value = 0;
		for (Item equipment : getEquipment().getItems().toArray()) {
			if (equipment == null)
				continue;
			long amount = equipment.getAmount();
			value += equipment.getDefinitions().getGrandExchangePrice() * amount;
		}
		return value;
	}

	public long getBankValue() {
		long value = 0;
		for (Item bank : getBank().getContainerCopy()) {
			if (bank == null)
				continue;
			long amount = bank.getAmount();
			value += bank.getDefinitions().getGrandExchangePrice() * amount;
		}
		return value;
	}

	public void getWealth() {
		long moneyPouchContains = getMoneyPouch().getTotalAmount();
		long bankValue = getBankValue();
		long inventoryValue = getInventoryValue();
		long equipmentValue = getEquipmentValue();
		long totalValue = moneyPouchContains + bankValue + inventoryValue + equipmentValue;
		stopAll();
		getInterfaceManager().sendCentralInterface(677);
		getPackets().sendIComponentText(677, 9, "Backpack & Equip");
		getPackets().sendIComponentText(677, 1, "Money Pouch");
		getPackets().sendIComponentText(677, 5, "Bank");
		getPackets().sendIComponentText(677, 15, "Total");

		getPackets().sendIComponentText(677, 10, "Backpack wealth is:");
		getPackets().sendIComponentText(677, 12, Utils.formatDoubledAmount(inventoryValue));
		getPackets().sendIComponentText(677, 11, "Equipment wealth is:");
		getPackets().sendIComponentText(677, 13, Utils.formatDoubledAmount(equipmentValue));

		getPackets().sendIComponentText(677, 6, "Bank wealth is:");
		getPackets().sendIComponentText(677, 7, Utils.formatDoubledAmount(bankValue));

		getPackets().sendIComponentText(677, 2, "Money pouch contains:");
		getPackets().sendIComponentText(677, 3, Utils.formatDoubledAmount(moneyPouchContains));

		getPackets().sendIComponentText(677, 16, "Total wealth is:");
		getPackets().sendIComponentText(677, 17, "");
		getPackets().sendIComponentText(677, 19, "");
		getPackets().sendIComponentText(677, 20, Utils.formatDoubledAmount(totalValue));
	}

	/**
	 * Gets the soul wars.
	 *
	 * @return the soul wars
	 */
	public SoulWars getSoulWars() {
		return soulWars;
	}

	/**
	 * Soul wars location.
	 */
	public void SoulWarsLocation() {
		/*
		 * Set Soul Wars
		 */
		this.didPassBlue = false;
		this.didPassRed = false;
		if (this.inSoulWars == true) {
			this.setNextWorldTile(new WorldTile(1890, 3164, 0));
			this.inSoulWars = false;
		}
	}

	public int[] getOpenBankPin() {
		return openBankPin;
	}

	public boolean getOpenedPin() {
		return openPin;
	}

	public int[] getChangeBankPin() {
		return changeBankPin;
	}

	public boolean getSetPin() {
		return setPin;
	}

	public int[] getPin() {
		return bankpins;
	}

	public int[] getConfirmPin() {
		return confirmpin;
	}

	public boolean getChatBadge() {
		return chatBadge;
	}

	public boolean setChatBadge(boolean b) {
		refreshChatBadge();
		return chatBadge = b;
	}

	public boolean isHiredByFred() {
		return hiredByFred;
	}

	public boolean setHiredByFred(boolean b) {
		return hiredByFred = b;
	}

	public JobManager getJobManager() {
		return jobManager;
	}

	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	public int getTaskPoints() {
		return taskPoints;
	}

	public int getPortableLimit() {
		return portableLimit;
	}

	public void setTaskPoints(int taskPoints) {
		this.taskPoints = taskPoints;
	}

	public void setPortableLimit(int portableLimit) {
		this.portableLimit = portableLimit;
	}

	public SkillerTasks getSkillTasks() {
		return skillTasks;
	}

	public void setSkillTasks(SkillerTasks skillTasks) {
		this.skillTasks = skillTasks;
	}

	public Reaper getReaperTasks() {
		return reaperTasks;
	}

	public void setReaperTasks(Reaper reaperTasks) {
		this.reaperTasks = reaperTasks;
	}

	/*
	 * if (JsonFileManager.containsAccount(username)) account =
	 * JsonFileManager.loadAccount(username);
	 */

	public int getLoyaltyPoints() {
		return Loyaltypoints;
	}

	public void setLoyaltyPoints(int Loyaltypoints) {
		this.Loyaltypoints = Loyaltypoints;
	}

	public void setupBank() {
		if (getRights() == 2) {
			getBank().addItem(15272, 10, 1, false);
			getBank().addItem(3144, 10, 1, false);
			getBank().addItem(7218, 10, 1, false);
			getBank().addItem(23255, 10, 1, false);
			getBank().addItem(23279, 10, 1, false);
			getBank().addItem(23423, 10, 1, false);
			getBank().addItem(23303, 10, 1, false);
			getBank().addItem(23351, 10, 1, false);
			getBank().addItem(23399, 10, 1, false);
			getBank().addItem(12140, 10, 1, false);
			getBank().addItem(11090, 10, 1, false);
			getBank().addItem(2550, 10, 1, false);
			getBank().addItem(23327, 10, 1, false);
			getBank().addItem(555, 10, 1, false);
			getBank().addItem(565, 10, 1, false);
			getBank().addItem(560, 10, 1, false);
			getBank().addItem(9075, 10, 1, false);
			getBank().addItem(557, 10, 1, false);
			getBank().addItem(1513, 10, 1, false);
			getBank().addItem(12790, 10, 1, false);
			getBank().addItem(12825, 10, 5, false);
			getBank().addItem(12093, 10, 5, false);
			getBank().addItem(12435, 10, 5, false);
			getBank().addItem(12089, 10, 5, false);
			getBank().addItem(12437, 10, 5, false);

			getBank().createTab(); // tab 1 melee
			Item item = new Item(5698, 50000); // first item in
			getBank().bankTabs[getBank().bankTabs.length - 1] = new Item[] { item }; // spawned
			// by
			// this
			getBank().addItem(4587, 10, 1, false); // add item to tab one
			// using third variable
			getBank().addItem(20671, 10, 1, false);
			getBank().addItem(1305, 10, 1, false);
			getBank().addItem(1434, 10, 1, false);
			getBank().addItem(3204, 10, 1, false);
			getBank().addItem(4151, 10, 1, false);
			getBank().addItem(11730, 10, 1, false);
			getBank().addItem(4153, 10, 1, false);
			getBank().addItem(5730, 10, 1, false);
			getBank().addItem(8850, 10, 1, false);
			getBank().addItem(3105, 10, 1, false);
			getBank().addItem(11732, 10, 1, false);
			getBank().addItem(6585, 10, 1, false);
			getBank().addItem(12681, 10, 1, false);
			getBank().addItem(3751, 10, 1, false);
			getBank().addItem(6737, 10, 1, false);
			getBank().addItem(6735, 10, 1, false);
			getBank().addItem(7458, 10, 1, false);
			getBank().addItem(7460, 10, 1, false);
			getBank().addItem(6107, 10, 1, false);
			getBank().addItem(6109, 10, 1, false);
			getBank().addItem(9629, 10, 1, false);
			getBank().addItem(1187, 10, 1, false);
			getBank().addItem(6524, 10, 1, false);
			getBank().addItem(1712, 10, 1, false);
			getBank().addItem(10364, 10, 1, false);
			getBank().addItem(19748, 10, 1, false);
			getBank().addItem(1201, 10, 1, false);
			getBank().addItem(1540, 10, 1, false);
			getBank().addItem(6128, 10, 1, false);
			getBank().addItem(1163, 10, 1, false);
			getBank().addItem(2665, 10, 1, false);
			getBank().addItem(2657, 10, 1, false);
			getBank().addItem(2673, 10, 1, false);
			getBank().addItem(19407, 10, 1, false);
			getBank().addItem(19422, 10, 1, false);
			getBank().addItem(19437, 10, 1, false);
			getBank().addItem(3486, 10, 1, false);
			getBank().addItem(4745, 10, 1, false);
			getBank().addItem(6129, 10, 1, false);
			getBank().addItem(1127, 10, 1, false);
			getBank().addItem(2661, 10, 1, false);
			getBank().addItem(2653, 10, 1, false);
			getBank().addItem(2669, 10, 1, false);
			getBank().addItem(19398, 10, 1, false);
			getBank().addItem(19413, 10, 1, false);
			getBank().addItem(19428, 10, 1, false);
			getBank().addItem(3481, 10, 1, false);
			getBank().addItem(4749, 10, 1, false);
			getBank().addItem(6130, 10, 1, false);
			getBank().addItem(1079, 10, 1, false);
			getBank().addItem(2663, 10, 1, false);
			getBank().addItem(2655, 10, 1, false);
			getBank().addItem(2671, 10, 1, false);
			getBank().addItem(19401, 10, 1, false);
			getBank().addItem(19416, 10, 1, false);
			getBank().addItem(19431, 10, 1, false);
			getBank().addItem(3483, 10, 1, false);
			getBank().addItem(4751, 10, 1, false);

			getBank().createTab(); // tab 2 Magic
			item = new Item(22482, 10); // first item in tab 2
			getBank().bankTabs[getBank().bankTabs.length - 1] = new Item[] { item }; // spawned
			// by
			// this
			getBank().addItem(22490, 10, 2, false); // add item to tab two
			// using third variable
			getBank().addItem(22486, 10, 2, false); // the third two
			// represents the tab id
			getBank().addItem(22494, 10, 2, false);
			getBank().addItem(2412, 10, 2, false);
			getBank().addItem(2413, 10, 2, false);
			getBank().addItem(2414, 10, 2, false);
			getBank().addItem(3841, 10, 2, false);
			getBank().addItem(19617, 10, 2, false);
			getBank().addItem(19613, 10, 2, false);
			getBank().addItem(13734, 10, 2, false);
			getBank().addItem(1712, 10, 2, false);
			getBank().addItem(14499, 10, 2, false);
			getBank().addItem(14497, 10, 2, false);
			getBank().addItem(14501, 10, 2, false);
			getBank().addItem(4091, 10, 2, false);
			getBank().addItem(4093, 10, 2, false);
			getBank().addItem(4675, 10, 2, false);
			getBank().addItem(15486, 10, 2, false);
			getBank().addItem(6914, 10, 2, false);
			getBank().addItem(6562, 10, 2, false);
			getBank().addItem(1712, 10, 2, false);
			getBank().addItem(6731, 10, 2, false);
			getBank().addItem(2579, 10, 2, false);
			getBank().addItem(3105, 10, 2, false);
			getBank().addItem(6918, 10, 2, false);
			getBank().addItem(6916, 10, 2, false);
			getBank().addItem(6924, 10, 2, false);
			getBank().addItem(6920, 10, 2, false);
			getBank().addItem(4224, 10, 2, false);
			getBank().addItem(9069, 10, 2, false);
			getBank().addItem(9070, 10, 2, false);
			getBank().addItem(9071, 10, 2, false);

			getBank().createTab(); // tab 3 Range
			item = new Item(2581, 10); // first item in tab 2
			getBank().bankTabs[getBank().bankTabs.length - 1] = new Item[] { item }; // spawned
			// by
			// this
			getBank().addItem(2577, 10, 3, false);
			getBank().addItem(2497, 10, 3, false);
			getBank().addItem(2503, 10, 3, false);
			getBank().addItem(10499, 10, 3, false);
			getBank().addItem(1712, 10, 3, false);
			getBank().addItem(15126, 10, 3, false);
			getBank().addItem(6733, 10, 3, false);
			getBank().addItem(6585, 10, 3, false);
			getBank().addItem(3749, 10, 3, false);
			getBank().addItem(6328, 10, 3, false);
			getBank().addItem(20171, 10, 3, false);
			getBank().addItem(10370, 10, 3, false);
			getBank().addItem(10386, 10, 3, false);
			getBank().addItem(10378, 10, 3, false);
			getBank().addItem(19517, 10, 3, false);
			getBank().addItem(19519, 10, 3, false);
			getBank().addItem(19518, 10, 3, false);
			getBank().addItem(9185, 10, 3, false);
			getBank().addItem(9244, 100, 3, false);
			getBank().addItem(861, 10, 3, false);
			getBank().addItem(892, 100, 3, false);
			getBank().addItem(10372, 10, 3, false);
			getBank().addItem(10388, 10, 3, false);
			getBank().addItem(10380, 10, 3, false);
			getBank().addItem(19447, 10, 3, false);
			getBank().addItem(19463, 10, 3, false);
			getBank().addItem(19455, 10, 3, false);
			getBank().addItem(868, 100, 3, false);
			getBank().addItem(11230, 100, 3, false);
			getBank().addItem(15703, 10, 3, false);
			getBank().addItem(11212, 10, 3, false);
			getBank().addItem(15241, 10, 3, false);
			getBank().addItem(15243, 10, 3, false);

			getBank().createTab(); // tab 4 money
			item = new Item(995, Integer.MAX_VALUE); // first item in tab 2
			getBank().bankTabs[getBank().bankTabs.length - 1] = new Item[] { item }; // spawned
			// by
			// this
			getBank().addItem(2460, 1, 4, false); // the third two represents
			// the tab id

			getBank().createTab(); // tab 5 summoning
			item = new Item(12790, 10); // first item in tab 2
			getBank().bankTabs[getBank().bankTabs.length - 1] = new Item[] { item }; // spawned
			// by
			// this
			getBank().addItem(12825, 100000, 5, false); // the third two
			// represents the tab id
			getBank().addItem(12093, 10, 5, false);
			getBank().addItem(12435, 10, 5, false);
			getBank().addItem(12089, 10, 5, false);
			getBank().addItem(12437, 10, 5, false);
			getPackets().sendGameMessage("Successfully setting up your bank.");
		}
	}

	public Item getPrize() {
		return prize;
	}

	public void setPrize(Item price) {
		this.prize = price;
	}

	public int getRakeStored() {
		return rakeStored;
	}

	public int getSeedDibberStored() {
		return seedDibber;
	}

	public int getSpadeStored() {
		return spadeStored;
	}

	public int getTrowelStored() {
		return trowelStored;
	}

	public int getWateringCanStored() {
		return wateringCanStored;
	}

	public int getSecateursStored() {
		return secateursStored;
	}

	public void setRakeStored(int amount) {
		this.rakeStored = amount;
	}

	public void setSeedDibberStored(int amount) {
		this.seedDibber = amount;
	}

	public void setSpadeStored(int amount) {
		this.spadeStored = amount;
	}

	public void setTrowelStored(int amount) {
		this.trowelStored = amount;
	}

	public void setWateringCanStored(int amount) {
		this.wateringCanStored = amount;
	}

	public void setSecateursStored(int amount) {
		this.secateursStored = amount;
	}

	public int getReaperPoints() {
		return reaperPoints;
	}

	public void setReaperPoints(int reaperPoints) {
		this.reaperPoints = reaperPoints;
	}

	public int getSilverhawkFeathers() {
		return silverhawkFeathers;
	}

	public void setSilverhawkFeathers(int amount) {
		this.silverhawkFeathers = amount;
	}

	public void afk() {
		GameExecutorManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				if (afk < Utils.currentTimeMillis()) {
					if (!hasStarted() || !hasFinished())
						logout(true);
				}
				afk();
			}
		}, 20, TimeUnit.MINUTES);
	}

	public boolean getHasDied() {
		return hasDied;
	}

	public void setHasDied(boolean hasDied) {
		this.hasDied = hasDied;
	}

	public boolean hasMoney(int amount) {
		int money = getInventory().getAmountOf(995) + getMoneyPouch().getTotalAmount();
		return money >= amount;
	}

	public boolean takeMoney(int amount) {
		if (!hasMoney(amount))
			return false;
		if (amount < 0)
			return false;
		int inPouch = getMoneyPouch().getTotalAmount();
		int inInventory = getInventory().getAmountOf(995);
		if (inPouch >= amount) {
			getMoneyPouch().setAmount(amount, true);
			return true;
		}
		if (inInventory >= amount) {
			getInventory().deleteItem(new Item(995, amount));
			return true;
		}
		if (inPouch + inInventory >= amount) {
			amount = amount - inPouch;
			getMoneyPouch().setAmount(inPouch, true);
			getInventory().deleteItem(new Item(995, amount));
			return true;
		}
		return false;
	}

	public int getRestAnimation() {
		return restAnimation;
	}

	public void setRestAnimation(int restAnimation) {
		this.restAnimation = restAnimation;
	}

	public int getStrike(int strike) {
		return this.strike[strike];
	}

	public void setStrike(int amount, int strikeId) {
		this.strike[strikeId] = amount;
	}

	public boolean getDivineLight() {
		return divineLight;
	}

	public void setDivineLight(boolean divineLight) {
		this.divineLight = divineLight;
	}

	public boolean getDivineCoin() {
		return divineCoin;
	}

	public void setDivineCoin(boolean divineCoin) {
		this.divineCoin = divineCoin;
	}

	public boolean inMemberZone() {
		if ((getX() >= 2312 && getX() <= 2356 && getY() >= 3663 && getY() <= 3703))
			return true;
		else
			return false;
	}

	private List<PresetSetups> PresetSetups;

	public int sinkholes;

	public int rosTrips;

	public int trollWins;

	public long lastImpling;

	public byte killstreak;

	public int heffinStage;

	public boolean completedHeffinCourse;

	public boolean xpLampPrompt = true;

	public List<PresetSetups> getPresetSetups() {
		return PresetSetups;
	}

	public boolean addPresetSetup(Player player, PresetSetups setup) {
		if (getPresetSetupByName(setup.getName()) != null) {
			getDialogueManager().startDialogue("SimpleMessage", "A setup with that name already exists.");
			return false;
		}
		PresetSetups.add(setup);
		return true;
	}

	public PresetSetups getPresetSetupByName(String name) {
		for (PresetSetups sets : PresetSetups) {
			if (sets == null)
				continue;
			if (sets.getName().equalsIgnoreCase(name))
				return sets;
		}
		return null;
	}

	public boolean removePresetSetup(String name) {
		PresetSetups set = getPresetSetupByName(name);
		if (set == null) {
			getDialogueManager().startDialogue("SimpleMessage", "You do not have a setup saved as: " + name + ".");
			return false;
		}
		PresetSetups.remove(set);
		getPackets().sendGameMessage("Removed preset: " + name + ".");
		return true;
	}

	public WorldTile getTile() {
		return new WorldTile(getX(), getY(), getPlane());
	}

	public int getTrollsToKill() {
		return trollsToKill;
	}

	public int setTrollsKilled(int trollsKilled) {
		return (this.trollsKilled = trollsKilled);
	}

	public int setTrollsToKill(int toKill) {
		return (this.trollsToKill = toKill);
	}

	public void addTrollKill() {
		trollsKilled++;
	}

	public int getTrollsKilled() {
		return trollsKilled;
	}

	public int getGodMode() {
		return godMode;
	}

	public void setGodMode(int i) {
		this.godMode = i;
	}

	public int getSecureCode() {
		return secureCode;
	}

	public void setSecureCode(int values) {
		this.secureCode = values;
	}

	public void handleWebstore(Player player, String username) {
		if (System.currentTimeMillis() - delay < 2500) {
			getPackets().sendGameMessage("Please wait one second, and try again.");
			return;
		}
		player.delay = System.currentTimeMillis();
		try {
			username = username.replaceAll(" ", "_");
			String secret = "cfd66e741860718ddecf1f6eabd05fc6";
			String email = "ventyzinc@gmail.com";
			URL url = new URL("http://rsps-pay.com/includes/listener.php?username=" + username + "&secret=" + secret
					+ "&email=" + email);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String results = reader.readLine();
			if (results.toLowerCase().contains("!error:")) {
				// Logger.log(this, "[RSPS-PAY]"+results);
			} else {
				String[] ary = results.split(",");
				for (int i = 0; i < ary.length; i++) {
					switch (ary[i]) {
					case "15374":
						player.setBronzeMember(true);
						player.setSilverMember(false);
						player.setGoldMember(false);
						player.setPlatinumMember(false);
						player.setDiamondMember(false);
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought Bronze Membership. Thank you!", false);
						player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
						Gero.setMember(player.getUsername(), 8, true);
						break;
					case "15375":
						player.setBronzeMember(false);
						player.setSilverMember(true);
						player.setGoldMember(false);
						player.setPlatinumMember(false);
						player.setDiamondMember(false);
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought Silver Membership. Thank-you!", false);
						player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
						Gero.setMember(player.getUsername(), 9, true);
						break;
					case "15376":
						player.setBronzeMember(false);
						player.setSilverMember(false);
						player.setGoldMember(true);
						player.setPlatinumMember(false);
						player.setDiamondMember(false);
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought Gold Membership. Thank-you!", false);
						player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
						Gero.setMember(player.getUsername(), 10, true);
						break;
					case "15377":
						player.setBronzeMember(false);
						player.setSilverMember(false);
						player.setGoldMember(false);
						player.setPlatinumMember(true);
						player.setDiamondMember(false);
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought Platinum Membership. Thank-you!", false);
						player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
						Gero.setMember(player.getUsername(), 15, true);
						break;
					case "15378":
						player.setBronzeMember(false);
						player.setSilverMember(false);
						player.setGoldMember(false);
						player.setPlatinumMember(false);
						player.setDiamondMember(true);
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought Diamond Membership. Thank-you!", false);
						player.getPackets().sendGameMessage("Thank you, your order has been delivered.");
						Gero.setMember(player.getUsername(), 16, true);
						break;
					case "15379":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName() + " has bought a bond. Thank-you!",
								false);
						player.getPackets().sendGameMessage("Thank you, your bond has been placed your the bank.");
						player.getBank().addItem(29492, 1, true);
						break;
					case "15380":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName() + " has bought 5 bonds. Thank-you!",
								false);
						player.getPackets().sendGameMessage("Thank you, your bonds has been placed your the bank.");
						player.getBank().addItem(29492, 5, true);
						break;
					case "15381":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName() + " has bought 10 bonds. Thank-you!",
								false);
						player.getPackets().sendGameMessage("Thank you, your bonds has been placed your the bank.");
						player.getBank().addItem(29492, 10, true);
						break;
					case "15382":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName() + " has bought 20 bonds. Thank-you!",
								false);
						player.getPackets().sendGameMessage("Thank you, your bonds has been placed your the bank.");
						player.getBank().addItem(29492, 20, true);
						break;
					case "15383":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage(
								"<col=FF0000><img=5>" + player.getDisplayName() + " has bought 40 bonds. Thank-you!",
								false);
						player.getPackets().sendGameMessage("Thank you, your bonds has been placed your the bank.");
						player.getBank().addItem(29492, 40, true);
						break;
					case "15384":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought a mystery box. Thank-you!", false);
						player.getPackets()
								.sendGameMessage("Thank you, your mystery box has been placed your the bank.");
						player.getBank().addItem(6199, 1, true);
						break;
					case "15385":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 5 mystery boxes. Thank-you!", false);
						player.getPackets()
								.sendGameMessage("Thank you, your mystery boxes has been placed your the bank.");
						player.getBank().addItem(6199, 5, true);
						break;
					case "15386":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 10 mystery boxes. Thank-you!", false);
						player.getPackets()
								.sendGameMessage("Thank you, your mystery boxes has been placed your the bank.");
						player.getBank().addItem(6199, 10, true);
						player.getBank().addItem(26384, 1, true);
						break;
					case "15387":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 20 mystery boxes. Thank-you!", false);
						player.getPackets()
								.sendGameMessage("Thank you, your mystery boxes has been placed your the bank.");
						player.getBank().addItem(6199, 20, true);
						player.getBank().addItem(26384, 3, true);
						break;
					case "15388":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 40 mystery boxes. Thank-you!", false);
						player.getPackets()
								.sendGameMessage("Thank you, your mystery boxes has been placed your the bank.");
						player.getBank().addItem(6199, 40, true);
						player.getBank().addItem(26384, 5, true);
						break;
					case "15527":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 10 treasure hunter keys. Thank-you!", false);
						squealOfFortune.handleBoughtKeys(10);
						break;
					case "15528":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 25 treasure hunter keys. Thank-you!", false);
						squealOfFortune.handleBoughtKeys(25);
						break;
					case "15529":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 50 treasure hunter keys. Thank-you!", false);
						squealOfFortune.handleBoughtKeys(50);
						break;
					case "15530":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 100 treasure hunter keys. Thank-you!", false);
						squealOfFortune.handleBoughtKeys(100);
						break;
					case "15531":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 250 treasure hunter keys. Thank-you!", false);
						squealOfFortune.handleBoughtKeys(250);
						break;
					case "15532":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 500 treasure hunter keys. Thank-you!", false);
						squealOfFortune.handleBoughtKeys(500);
						break;
					case "15533":
						player.setNextGraphics(new Graphics(1765));
						World.sendWorldMessage("<col=FF0000><img=5>" + player.getDisplayName()
								+ " has bought 1000 treasure hunter keys. Thank-you!", false);
						squealOfFortune.handleBoughtKeys(1000);
						break;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<Player> getROSPartyMembers() {
		return partyMembers;
	}

	public void clearROSPartyMembers() {
		partyMembers = new ArrayList<Player>();
	}

	public void addROSPartyMember(Player player) {
		if (partyMembers.contains(player))
			return;
		partyMembers.add(player);
	}

	public void removeROSPartyMember(Player player) {
		partyMembers.remove(player);
	}

	public void eggBurst(int X, int Y, int Z) {
		if (AraxxorEggBurst == false) {
			World.sendGraphics(null, new Graphics(4996), new WorldTile(X + 3, Y, Z));
			World.sendGraphics(null, new Graphics(4996), new WorldTile(X + 2, Y, Z));
			World.sendGraphics(null, new Graphics(4996), new WorldTile(X + 2, Y + 1, Z));
			World.sendGraphics(null, new Graphics(4982), new WorldTile(X + 3, Y, Z));
			World.sendGraphics(null, new Graphics(4982), new WorldTile(X + 2, Y, Z));
			World.sendGraphics(null, new Graphics(4982), new WorldTile(X + 2, Y + 1, Z));
			AraxxorEggBurst = true;
			stopAll();
		}
	}

	public void setKilledAraxxor(boolean killedAraxxor) {
		this.killedAraxxor = killedAraxxor;
	}

	public FairyRings getFairyRings() {
		return fairyRings;
	}

	public DDToken getDDToken() {
		return ddToken;
	}

	public int getBossPoints() {
		return bossPoints;
	}

	public void setBossPoints(int amount) {
		this.bossPoints = amount;
	}
}