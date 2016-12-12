package net.kagani.game.player.actions;

import java.util.HashMap;
import java.util.Map;

import net.kagani.Settings;
import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.item.Item;
import net.kagani.game.minigames.clanwars.ClanWars;
import net.kagani.game.minigames.clanwars.ClanWars.Rules;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.player.Player;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.Action;
import net.kagani.utils.Utils;


public class Summoning extends Action {

    public static final int HAMMER = 2347, DUNGEONEERING_HAMMER = 17883;

    private static final Animation POUCH_INFUSION_ANIMATION = new Animation(725);
    private static final Graphics POUCH_INFUSION_GRAPHICS = new Graphics(1207);
    public static final int GOLD = 12158;
    public static final int GREEN = 12159;
    public static final int CRIMSON = 12160;
    public static final int ABYSSAL = 12161;
    public static final int TALON = 12162;
    public static final int BLUE = 12163;
    public static final int RAVAGER = 12164;
    public static final int POUCH = 12155;
    public static final int SHARDS = 12183;

    public static void spawnFamiliar(Player player, Pouch pouch) {
        player.getPackets().sendGameMessage("SPAWNING FAMILIAR.");
        if (player.getFamiliar() != null || player.getPet() != null) {
            player.getPackets().sendGameMessage("You already have a follower.");
            return;
        }
        if (!player.getControlerManager().canSummonFamiliar() || player.getSkills().getLevel(Skills.SUMMONING) < pouch.getSummoningCost())
            return;
        int levelReq = getRequiredLevel(pouch.getRealPouchId());
        if (player.getSkills().getLevelForXp(Skills.SUMMONING) < levelReq) {
            player.getPackets().sendGameMessage("You need a summoning level of " + levelReq + " in order to use this pouch.");
            return;
        }
        if (player.getCurrentFriendsChat() != null) {
            ClanWars war = player.getCurrentFriendsChat().getClanWars();
            if (war != null) {
                if (war.get(Rules.NO_FAMILIARS) && (war.getFirstPlayers().contains(player) || war.getSecondPlayers().contains(player))) {
                    player.getPackets().sendGameMessage("You can't summon familiars during this war.");
                    return;
                }
            }
        }
        final Familiar npc = createFamiliar(player, pouch);
        if (npc == null) {
            player.getPackets().sendGameMessage("This familiar is not added yet.");
            return;
        }
        player.getInventory().deleteItem(pouch.getRealPouchId(), 1);
        player.getSkills().drainSummoning(pouch.getSummoningCost());
        player.setFamiliar(npc);
    }

    public static Familiar createFamiliar(Player player, Pouch pouch) {
        player.getPackets().sendGameMessage("Spawning Familiar.");
        try {
            return (Familiar) Class.forName("net.kagani.game.npc.familiar.impl." + (NPCDefinitions.getNPCDefinitions(getNPCId(pouch.getRealPouchId()) + 1)).getName().replace(" ", "").replace("ï", "i").replace("-", "").replace("(", "").replace(")", "")).getConstructor(Player.class, Pouch.class, WorldTile.class, int.class, boolean.class).newInstance(player, pouch, player, -1, true);
        } catch (Throwable e) {
            if (!Settings.HOSTED)
                e.printStackTrace();
            return null;
        }
    }

    public static int getNPCId(int id) {
        return ClientScriptMap.getMap(1320).getIntValue(id);
    }

    public static int getRequiredLevel(int id) {
        return ClientScriptMap.getMap(1185).getIntValue(id);
    }

    public static int getScrollId(int id) {
        return ClientScriptMap.getMap(1283).getIntValue(id);
    }


    public enum Pouch {

        SPIRIT_WOLF(1, 4.8, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 7), new Item(2859, 1)}, 12047, 360000, 1),

        DREADFOWL(4, 9.3, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 8), new Item(2138, 1)}, 12043, 240000, 1),

        SPIRIT_SPIDER(10, 12.6, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 8), new Item(6291, 1)}, 12059, 900000, 2),

        THORNY_SNAIL(13, 12.6, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 9), new Item(3363, 1)}, 12019, 960000, 2),

        GRANITE_CRAB(16, 21.6, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 7), new Item(440, 1)}, 12009, 1080000, 2),

        SPIRIT_MOSQUITO(17, 46.5, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 1), new Item(6319, 1)}, 12778, 720000, 2), // gfx 1440

        DESERT_WYRM(18, 31.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 45), new Item(1783, 1)}, 12049, 1140000, 1),

        SPIRIT_SCORPIAN(19, 83.2, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 57), new Item(3095, 1)}, 12055, 1020000, 2),

        SPIRIT_TZ_KIH(22, 96.8, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 64), new Item(12168, 1)}, 12808, 1080000, 3),

        ALBINO_RAT(23, 100.4, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 75), new Item(2134, 1)}, 12067, 1320000, 3),

        SPIRIT_KALPHITE(25, 110, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 51), new Item(3138, 1)}, 12063, 1320000, 3),

        COMPOST_MOUNT(28, 49.8, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 47), new Item(6032, 1)}, 12091, 1440000, 6),

        GIANT_CHINCHOMPA(29, 50, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 84), new Item(9976, 1)}, 12800, 1860000, 1),

        VAMPYRE_BAT(31, 86, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 81), new Item(3325, 1)}, 12053, 1980000, 4),

        HONEY_BADGER(32, 90.8, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 84), new Item(12156, 1)}, 12065, 1500000, 4),

        BEAVER(33, 57.6, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 72), new Item(1519, 1)}, 12021, 1620000, 4),

        VOID_RAVAGER(34, 59.6, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 74), new Item(12164, 1)}, 12818, 1620000, 4),

        VOID_SPINNER(34, 59.6, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 74), new Item(12165, 1)}, 12780, 1620000, 4),

        VOID_TORCHER(34, 59.6, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 74), new Item(12166, 1)}, 12798, 5640000, 4),

        VOID_SHIFTER(34, 59.6, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 74), new Item(12167, 1)}, 12814, 5640000, 4),

        BRONZE_MINOTAUR(36, 79.8, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 102), new Item(2349, 1)}, 12073, 1800000, 9),

        BULL_ANT(40, 52.8, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 11), new Item(6010, 1)}, 12087, 1800000, 5),

        MACAW(41, 72.4, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 78), new Item(249, 1)}, 12071, 1860000, 5),

        EVIL_TURNIP(42, 184.8, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 104), new Item(12153, 1)}, 12051, 1800000, 5),

        SPIRIT_COCKATRICE(43, 75.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 88), new Item(12654, 1)}, 12095, 2160000, 5),

        SPIRIT_GUTHATRICE(43, 75.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 88), new Item(12111, 1)}, 12097, 2160000, 5),

        SPIRIT_SARATRICE(43, 75.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 88), new Item(12113, 1)}, 12099, 2160000, 5),

        SPIRIT_ZAMATRICE(43, 75.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 88), new Item(12115, 1)}, 12101, 2160000, 5),

        SPIRIT_PENGATRICE(43, 75.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 88), new Item(12117, 1)}, 12103, 2160000, 5),

        SPIRIT_CORAXATRICE(43, 75.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 88), new Item(12119, 1)}, 12105, 2160000, 5),

        SPIRIT_VULATRICE(43, 75.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 88), new Item(12121, 1)}, 12107, 2160000, 5),

        IRON_MINOTAUR(46, 404.8, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 125), new Item(2351, 1)}, 12075, 2220000, 9),

        PYRELORD(46, 202.4, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 111), new Item(2478, 1)}, 12816, 1920000, 5),

        MAGPIE(47, 83.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 88), new Item(1635, 1)}, 12041, 2040000, 5),

        BLOATED_LEECH(49, 115.2, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 117), new Item(2132, 1)}, 12061, 2040000, 5),

        SPIRIT_TERRORBIRD(52, 68.4, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 12), new Item(9978, 1)}, 12007, 2160000, 6),

        ABYSSAL_PARASITE(54, 94.8, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 106), new Item(12161, 1)}, 12035, 1800000, 6),

        SPIRIT_JELLY(55, 100, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 151), new Item(1937, 1)}, 12027, 2580000, 6),

        STEEL_MINOTAUR(56, 142.8, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 141), new Item(2353, 1)}, 12077, 2760000, 9),

        IBIS(56, 98.8, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 109), new Item(311, 1)}, 12531, 2280000, 6),

        SPIRIT_KYATT(57, 201.6, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 153), new Item(10103, 1)}, 12812, 2940000, 6),

        SPIRIT_LARUPIA(57, 201.6, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 155), new Item(10095, 1)}, 12784, 2940000, 6),

        SPIRIT_GRAAHK(57, 201.6, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 154), new Item(10099, 1)}, 12810, 2940000, 6),

        KARAMTHULU_OVERLOAD(58, 210.4, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 144), new Item(6723, 1)}, 12023, 2640000, 6),

        SMOKE_DEVIL(61, 268, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 141), new Item(9736, 1)}, 12085, 2880000, 7),

        ABYSSAL_LURKER(62, 109.6, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 119), new Item(12161, 1)}, 12037, 2460000, 7),

        SPIRIT_COBRA(63, 276.8, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 116), new Item(6287, 1)}, 12015, 3360000, 7),

        STRANGER_PLANT(64, 281.6, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 128), new Item(8431, 1)}, 12045, 2940000, 7),

        MITHRIL_MINOTAUR(66, 580.8, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 152), new Item(2359, 1)}, 12079, 3300000, 9),

        BARKER_TOAD(66, 580.8, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 11), new Item(2150, 1)}, 12123, 3300000, 7),

        WAR_TORTOISE(67, 87, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 1), new Item(7939, 1)}, 12031, 480000, 7),

        BUNYIP(68, 58.6, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 110), new Item(383, 1)}, 12029, 2580000, 7),

        FRUIT_BAT(69, 119.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 130), new Item(1963, 1)}, 12033, 2640000, 7),

        RAVENOUS_LOCUST(70, 121.2, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 79), new Item(2516, 1)}, 12820, 2700000, 4),

        ARCTIC_BEAR(71, 132.0, new Item[]{new Item(GOLD), new Item(POUCH), new Item(SHARDS, 128), new Item(10117, 1)}, 12057, 1440000, 8),

        PHEONIX(72, 93.2, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 165), new Item(14616, 1)}, 14623, 1680000, 8),

        OBSIDIAN_GOLEM(73, 342.4, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 195), new Item(12168, 1)}, 12792, 3300000, 8),

        GRANITE_LOBSTER(74, 325.6, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 166), new Item(6979, 1)}, 12069, 2920000, 8),

        PRAYING_MANTIS(75, 325.6, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 168), new Item(8938, 1)}, 12011, 2920000, 8),

        FORGE_REGENT(76, 329.6, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 141), new Item(10020, 1)}, 12782, 4140000, 9),

        ADAMANT_MINOTAUR(76, 134, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 144), new Item(2361, 1)}, 12081, 2700000, 9),

        TALON_BEAST(77, 668.8, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 174), new Item(12162, 1)}, 12794, 3960000, 9),

        GIANT_ENT(78, 105.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 124), new Item(5933, 1)}, 12013, 2940000, 8),

        FIRE_TITAN(79, 136.8, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 198), new Item(1442, 1)}, 12802, 2940000, 9),

        MOSS_TITAN(79, 395.2, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 202), new Item(1440, 1)}, 12804, 3720000, 9),

        ICE_TITAN(79, 395.2, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 198), new Item(1438, 1), new Item(14441, 1)}, 12806, 3720000, 9),

        HYDRA(80, 140.8, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 128), new Item(571, 1)}, 12025, 2940000, 8),

        SPIRIT_DAGANNOTH(83, 364.8, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 1), new Item(6155, 1)}, 12017, 3420000, 9),

        LAVA_TITAN(83, 330.4, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 219), new Item(12168, 1)}, 12788, 3660000, 9),

        SWAMP_TITAN(85, 373.6, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 150), new Item(10149)}, 12776, 3360000, 9),

        RUNE_MINOTAUR(86, 756.8, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 1), new Item(2363)}, 12083, 9060000, 9),

        UNICORN_STALLION(88, 154.4, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 140), new Item(237)}, 12039, 3240000, 9),

        GEYSER_TITAN(89, 383.2, new Item[]{new Item(BLUE), new Item(POUCH), new Item(SHARDS, 222), new Item(1444)}, 12786, 4140000, 10),

        WOLPERTINGER(92, 404.8, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 203), new Item(3226), new Item(7830)}, 12089, 3720000, 10),

        ABYSSAL_TITAN(93, 163.2, new Item[]{new Item(GREEN), new Item(POUCH), new Item(SHARDS, 113), new Item(ABYSSAL)}, 12796, 1920000, 10),

        IRON_TITAN(95, 417.6, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 198), new Item(1115)}, 12822, 3600000, 10),

        PACK_YAK(96, 422.2, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 211), new Item(10818)}, 12093, 3480000, 10),

        STEEL_TITAN(99, 435.2, new Item[]{new Item(CRIMSON), new Item(POUCH), new Item(SHARDS, 178), new Item(1119)}, 12790, 3840000, 10),;

        private static final Map<Integer, Pouch> pouches = new HashMap<Integer, Pouch>();

        static {
            for (Pouch pouch : Pouch.values()) {
                pouches.put(pouch.realPouchId, pouch);
            }
        }

        public static Pouch forId(int id) {
            return pouches.get(id);
        }

        public static Pouch getPouchByProduce(int id) {
            for (Pouch pouch : Pouch.values()) {
                if (pouch.getRealPouchId() == id)
                    return pouch;
            }
            return null;
        }

        public static Pouch getPouch(int id) {
            for (Pouch pouch : Pouch.values()) {
                for (Item item : pouch.getItemsRequired())
                    if (item.getId() == id)
                        return pouch;
            }
            return null;
        }

        public static Pouch getPouch(Player player) {
            for (Pouch pouch : Pouch.values()) {
                for (Item item : pouch.getItemsRequired())
                    if (player.getInventory().containsItems(new Item(BLUE)))
                        return ALBINO_RAT;
                if (player.getInventory().containsItems(new Item(GREEN)))
                    return DESERT_WYRM;
                if (player.getInventory().containsItems(new Item(CRIMSON)))
                    return SPIRIT_SCORPIAN;
                if (player.getInventory().containsItems(new Item(GOLD)))
                    return SPIRIT_WOLF;
            }
            return null;
        }

        public static boolean hasPouch(Player player) {
            for (Pouch pouch : Pouch.values())
                if (player.getInventory().containsOneItem(pouch.getRealPouchId()))
                    return true;
            return false;
        }


        private int levelRequired;
        private double experience;
        private Item[] incredientsRequired;
        private int realPouchId;
        private long pouchTime;
        private int summoningCost;

        private Pouch(int levelRequired, double experience, Item[] itemsRequired, int producedItem, long pouchTime, int summoningCost) {
            this.levelRequired = levelRequired;
            this.experience = experience;
            this.incredientsRequired = itemsRequired;
            this.realPouchId = producedItem;
            this.pouchTime = pouchTime;
            this.summoningCost = summoningCost;
        }

        public Item[] getItemsRequired() {
            return incredientsRequired;
        }

        public int getLevelRequired() {
            return levelRequired;
        }

        public int getRealPouchId() {
            return realPouchId;
        }

        public double getExperience() {
            return experience;
        }

        public int getSummoningCost() {
            return summoningCost;
        }

        public long getPouchTime() {
            return pouchTime;
        }

    }

    public Pouch pouch;
    public WorldObject object;
    public int ticks;

    public Summoning(Pouch pouch, WorldObject object, int ticks) {
        this.object = object;
        this.pouch = pouch;
        this.ticks = ticks;
    }


    @Override
    public boolean start(Player player) {
        if (!checkAll(player)) {
            return false;
        }
        return true;
    }

    public boolean checkAll(Player player) {
        if (pouch == null || player == null || object == null) {
            return false;
        }
        for (int i = 0; i < pouch.getItemsRequired().length; i++) {
            if (!player.getInventory().containsItems(pouch.getItemsRequired())) {
                return false;
            }
        }
        if (pouch.getItemsRequired().length > 1) {
            if (!player.getInventory().containsItemToolBelt(pouch.getItemsRequired()[0].getId(), pouch.getItemsRequired()[0].getAmount())) {
                StringBuilder sb = new StringBuilder();
                sb.append("You need ");
                for (int i = 0; i < pouch.getItemsRequired().length; i++) {
                    sb.append(pouch.getItemsRequired()[i] + " " + pouch.getItemsRequired()[i].getDefinitions().getName() + (i == pouch.getItemsRequired().length ? "" : ", "));
                }
                sb.append(" to create a " + new Item(pouch.getRealPouchId()).getDefinitions().getName() + ".");
                player.getPackets().sendGameMessage(sb.toString());
                return false;
            }
        }
        if (player.getSkills().getLevel(Skills.SUMMONING) < pouch.getLevelRequired()) {
            player.getPackets().sendGameMessage("You need a Summoning level of at least " + pouch.getLevelRequired() + " to create " + new Item(pouch.getRealPouchId()).getDefinitions().getName());
            return false;
        }
        return true;
    }

    @Override
    public boolean process(Player player) {
        player.setNextAnimation(POUCH_INFUSION_ANIMATION);
        player.setNextGraphics(POUCH_INFUSION_GRAPHICS);
        double xp = pouch.getExperience();
        int amount = new Item(pouch.getRealPouchId()).getAmount();
        if (new Item(pouch.getRealPouchId()).getDefinitions().isStackable())
            amount *= Settings.XP_RATE;
        for (int i = 0; i < ticks; i++) {
            for (int x = 0; x < pouch.getItemsRequired().length; x++) {
                if (!player.getInventory().containsItems(pouch.getItemsRequired())) {
                    return false;
                }
            }
            for (Item required : pouch.getItemsRequired()) {
                player.getInventory().deleteItem(required.getId(), required.getAmount());
            }
            player.getInventory().addItem(new Item(pouch.getRealPouchId()).getId(), amount);
            player.getSkills().addXp(Skills.SUMMONING, xp);
            
            
            int random = Utils.random(900);
		
		if (random == 57 && !player.getInventory().containsItem(32072, 1) && !player.getBank().containsItem(32072) && player.getInventory().getFreeSlots() >= 1 && player.getSkills().getLevelForXp(Skills.SUMMONING) >= 99){
			  player.getInventory().addItem(32072, 1);
			    player.getPackets().sendGameMessage("<col=33e5e1>As you infuse the creature's essence a part of it's essence gets trapped inside a crystal, you quick place the crystal in your backpack.");
			}
			
			else if (random == 57 && !player.getInventory().containsItem(32072, 1) && !player.getBank().containsItem(32072) && player.getInventory().getFreeSlots() == 0 && player.getSkills().getLevelForXp(Skills.SUMMONING) >= 99){
			    player.getBank().addItem(32072, 1, true);
			    player.getPackets().sendGameMessage("<col=33e5e1>As you infuse the creature's essence a part of it's essence gets trapped inside a crystal. The crystal has been sent to your bank.");
			}
            
            
            ticks--;
        }
        player.getPackets().sendGameMessage("You have successfully infused all your " + new Item(pouch.getRealPouchId()).getDefinitions().getName() + " pouches.", true);
        player.faceObject(object);
        return false;
    }

    @Override
    public int processWithDelay(Player player) {
        return -1;
    }

    @Override
    public void stop(Player player) {
      
    }
}