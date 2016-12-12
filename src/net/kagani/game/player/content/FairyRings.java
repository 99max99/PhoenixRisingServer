package net.kagani.game.player.content;

import java.io.Serializable;

import net.kagani.Settings;
import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.WorldObject;
import net.kagani.game.WorldTile;
import net.kagani.game.player.Player;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;

public class FairyRings implements Serializable {

	public enum Rings {

		HUB(2412, 4434, 0, "Fairy Home"),

		AIQ(2996, 3114, 0, "Mudskipper Point"),

		AJR(2780, 3613, 0, "Golden Apple Tree"),

		AKQ(2319, 3619, 0, "Pictoris Hunter Area"),

		AKS(2571, 2956, 0, "Feldip Hunter Area"),

		ALP(2472, 3027, 0, "JigJig"),

		ALS(2644, 3495, 0, "McGrubor's Woods"),

		BIP(3410, 3324, 0, "Polypore Entrance"),

		BIQ(3251, 3095, 0, "Kalphite Queen"),

		BJQ(1737, 5342, 0, "Ancient Cavern"),

		BJR(2741, 3235, 0, "Fisher Realm"),

		BKP(2385, 3035, 0, "Castle Wars"),

		BKR(3469, 3431, 0, "Mort Myre"),

		BLP(4622, 5147, 0, "Tzhaar"),

		BLR(2740, 3351, 0, "Legend's Guild"),

		CIP(2513, 3884, 0, "Miscellania"),

		CIQ(2528, 3127, 0, "Yanille"),

		CJR(2705, 3576, 0, "Sinclair Mansion"),

		DJR(2676, 3587, 0, "Sinclair Mansion 2"),

		CKS(3447, 3470, 0, "Canifis"),

		CLR(2735, 2742, 0, "Ape Atoll"),

		DIP(3763, 2930, 0, "MosLe Harmless"),

		DIS(3108, 3149, 0, "Wizard's Tower"),

		DJP(2658, 3230, 0, "Tower of Life"),

		DKP(2900, 3111, 0, "Musa Point"),

		DKQ(4183, 5726, 0, "Glacor Cave"),

		DKR(3129, 3496, 0, "Grand Exchange"),

		DKS(2744, 3719, 0, "Snowy Hunter Area"),

		DLQ(3423, 3016, 0, "Kharidian Desert"),

		AIR(2700, 3247, 0, "Witch Island"),

		AJS(2500, 3896, 0, "Penguin Island"),

		ALR(3059, 4875, 0, "Abyss"),

		BIR(2455, 4396, 0, "Sparse Plane"),

		BIS(2635, 3266, 0, "Ardougne Zoo"),

		BKQ(3041, 4532, 0, "Enchanted Valley"),

		DIR(3038, 5348, 0, "Gorak Plane"),

		DLR(2213, 3099, 0, "Poison Waste"),

		BLS(2412, 4434, 0, "Zanaris");

		private final int x;

		private final int y;

		private final int plane;

		private final String description;

		Rings(int x, int y, int plane, String description) {
			this.x = x;
			this.y = y;
			this.plane = plane;
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public WorldTile getTile() {
			return new WorldTile(x, y, plane);
		}

	}

	private static final long serialVersionUID = 1714313085013227278L;
	public final int FAIRY_INTERFACE = 734;
	public final int BOOK_INTERFACE = 788;
	private int firstRing;
	private int secondRing;
	private int thirdRing;
	private boolean fairyClickDelay;

	private Player player;

	public FairyRings(Player player) {
		this.setPlayer(player);
	}

	public void closeInterface() {
		player.getInterfaceManager().removeInterface(FAIRY_INTERFACE);
	}

	public boolean getFairyClickDelay() {
		return fairyClickDelay;
	}

	public int getFirstRing() {
		return firstRing;
	}

	public Player getPlayer() {
		return player;
	}

	public Rings getRing() {
		final char[] charsAtOne = { 'a', 'd', 'c', 'b' };
		final char[] charsAtTwo = { 'i', 'l', 'k', 'j' };
		final char[] charsAtThree = { 'p', 's', 'r', 'q' };
		final String code = Character.toString(charsAtOne[getFirstRing()])
				+ Character.toString(charsAtTwo[getSecondRing()])
				+ Character.toString(charsAtThree[getThirdRing()]);
		for (final Rings ring : Rings.values()) {
			if (ring.name().equalsIgnoreCase(code))
				return ring;
		}
		return null;

	}

	public int getSecondRing() {
		return secondRing;
	}

	public int getThirdRing() {
		return thirdRing;
	}

	public boolean handleButtons(int interfaceId, int componentId) {
		if (interfaceId == FAIRY_INTERFACE) {
			Logger.log("FairyRing", "ComponentId: " + componentId);
			if (getFairyClickDelay()) {
				switch (componentId) {
				case 119:
					handleTeleport();
					return true;
				case 84:
					if (getFirstRing() == 2)
						return true;
					if (getFirstRing() == 1) {
						setFirstRing(3);
					} else {
						if (getFirstRing() + 1 == 4)
							setFirstRing(0);
						else
							setFirstRing(getFirstRing() + 1);

					}
					return true;
				case 85:
					// ringone end
					if (getFirstRing() - 1 < 0)
						setFirstRing(3);
					else
						setFirstRing(getFirstRing() - 1);
					return true;
				case 86:
					if (getSecondRing() == 2)
						return true;
					if (getSecondRing() == 1) {
						setSecondRing(3);
					} else {
						if (getSecondRing() + 1 == 4)
							setSecondRing(0);
						else
							setSecondRing(getSecondRing() + 1);
					}
					return true;
				case 87:
					// ringtwo end
					if (getSecondRing() - 1 < 0)
						setSecondRing(3);
					else
						setSecondRing(getSecondRing() - 1);
					return true;
				case 88:
					if (getThirdRing() == 2)
						return true;
					if (getThirdRing() == 1) {
						setThirdRing(3);
					} else {
						if (getThirdRing() + 1 == 4)
							setThirdRing(0);
						else
							setThirdRing(getThirdRing() + 1);
					}
					return true;
				case 89:
					// ringthree end
					if (getThirdRing() - 1 < 0)
						setThirdRing(3);
					else
						setThirdRing(getThirdRing() - 1);
					return true;
				default:
					return false;
				}
			}
		}
		return false;
	}

	public void handleObjects(final WorldObject object) {
		player.stopAll();
		player.addWalkSteps(object.getX(), object.getY());
		if (object.getX() == Rings.HUB.x && object.getY() == Rings.HUB.y
				&& object.getPlane() == Rings.HUB.plane)
			openInterface();
		else
			handleTeleportHub();
	}

	public void openInterface() {
		reset(player);
		player.getInterfaceManager().setFairyRingInterface(true,
				FAIRY_INTERFACE);
	}

	public void reset(Player player) {
		setFirstRing(0);
		setSecondRing(0);
		setThirdRing(0);
		fairyClickDelay = true;
	}

	public void sendBook() {
		player.getInterfaceManager().sendInventoryInterface(735);
		player.getPackets().sendIComponentText(735, 15,
				"          Asgarnia: Mudskipper Point");
		player.getPackets().sendIComponentText(735, 16,
				"          Islands: South of Witchaven");
		player.getPackets().sendIComponentText(735, 19,
				"          Dungeons: Dark cave south of Dorgesh-Kaan");
		player.getPackets().sendIComponentText(735, 20,
				"          Kandarin: Slayer cave south-east of Rellekka");
		player.getPackets().sendIComponentText(735, 21,
				"          Islands: Penguins near Miscellania");
		player.getPackets().sendIComponentText(735, 23,
				"          Kandarin: Piscatoris Hunter area");
		player.getPackets().sendIComponentText(735, 25,
				"          Feldip Hills: Feldip Hunter area");
		player.getPackets().sendIComponentText(735, 26,
				"          Kandarin: Feldip Hills");
		player.getPackets().sendIComponentText(735, 27,
				"          Morytania: Haunted Woods east of Canifis");
		player.getPackets().sendIComponentText(735, 28,
				"          Other Realms: Abyss");
		player.getPackets().sendIComponentText(735, 29,
				"          Kandarin: McGrubor's Wood");
		player.getPackets().sendIComponentText(735, 30,
				"          Islands: Polypore Dungeon");
		player.getPackets().sendIComponentText(735, 31,
				"          Kharidian Desert: Near Kalphite hive");
		player.getPackets().sendIComponentText(735, 32,
				"          Sparse Plane");
		player.getPackets().sendIComponentText(735, 33,
				"          Kandarin: Ardougne Zoo unicorns");
		player.getPackets().sendIComponentText(735, 35,
				"          Dungeons: Ancient Dungeon");
		player.getPackets().sendIComponentText(735, 36,
				"          Fisher Realm");
		player.getPackets().sendIComponentText(735, 38,
				"          Feldip Hills: South of Castle Wars");
		player.getPackets().sendIComponentText(735, 39,
				"          Other Realms: Enchanted Valley");
		player.getPackets().sendIComponentText(735, 40,
				"          Morytania: Mort Myre, south of Canifis");
		player.getPackets().sendIComponentText(735, 42,
				"          Dungeons: TzHaar area");
		player.getPackets().sendIComponentText(735, 43, "          Yu'biusk");
		player.getPackets().sendIComponentText(735, 44,
				"          Kandarin: Legend's Guild");
		player.getPackets().sendIComponentText(735, 46,
				"          North-west Miscellania");
		player.getPackets().sendIComponentText(735, 47,
				"          Kandarin: North-west of Yanille");
		player.getPackets().sendIComponentText(735, 52,
				"          Kandarin: Sinclair Mansion");
		player.getPackets().sendIComponentText(735, 56,
				"          Karamja: South of Tai Bwo Wannai Village");
		player.getPackets().sendIComponentText(735, 57,
				"          Morytania: Canifis");
		player.getPackets().sendIComponentText(735, 60,
				"          Ape Atoll: West of the Ape Atoll Agility Course");
		player.getPackets().sendIComponentText(735, 61,
				"          Islands: Jungle spiders near Yanille");
		player.getPackets()
				.sendIComponentText(735, 62,
						"          Mos Le'Harmless: Isle on the coast of Mos Le'Harmless");
		player.getPackets().sendIComponentText(735, 65,
				"          Misthalin: Wizards' Tower");
		player.getPackets().sendIComponentText(735, 66,
				"          Kandarin: Tower of Life");
		player.getPackets().sendIComponentText(735, 68,
				"          Kandarin: Sinclair Mansion");
		player.getPackets().sendIComponentText(735, 70,
				"          Karamja: South of Musa Point");
		player.getPackets()
				.sendIComponentText(735, 71, "          Glacor Cave");
		player.getPackets().sendIComponentText(735, 72,
				"          Misthalin: Edgeville");
		player.getPackets().sendIComponentText(735, 73,
				"          Kandarin: Snowy Hunter area");
		player.getPackets().sendIComponentText(735, 75,
				"          Kharidian Desert: North of Nardah");
		player.getPackets().sendIComponentText(735, 77,
				"          Dungeons: Myreque hideout under The Hollows");
		player.getPackets().sendIComponentText(735, 54,
				"          Other Realms: Cosmic Entity's plane");
		player.getPackets().sendIComponentText(735, 58,
				"          Islands: South of Draynor Village");
		player.getPackets().sendIComponentText(735, 64,
				"          Other Realms: The Gorak Plane");
		player.getPackets().sendIComponentText(735, 76,
				"          Islands: Poison Waste south of Isafdar");
		player.getPackets().sendIComponentText(BOOK_INTERFACE, 14,
				"Fairy Dossier");
		player.getPackets().sendIComponentText(BOOK_INTERFACE, 13,
				"List of known fairy ring codes.");
	}

	public void setFairyClickDelay(boolean fairyClickDelay) {
		this.fairyClickDelay = fairyClickDelay;
	}

	public void setFirstRing(int firstRing) {
		this.firstRing = firstRing;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setSecondRing(int secondRing) {
		this.secondRing = secondRing;
	}

	public void setThirdRing(int thirdRing) {
		this.thirdRing = thirdRing;
	}

	public void handleTeleport() {
		if (getRing() == null) {
			player.getPackets().sendGameMessage("This combination is invalid");
			return;
		}
		if (Settings.DEBUG)
			Logger.log("FairyRings", "Ring code: " + getRing());
		WorldTasksManager.schedule(new WorldTask() {
			int steps;

			@Override
			public void run() {
				if (steps == 0) {
					closeInterface();
					player.setNextAnimation(new Animation(3254));
					player.setNextGraphics(new Graphics(2670));
				} else if (steps == 2) {
					if (getRing() == null || getRing().getTile() == null)
						player.getPackets().sendGameMessage(
								"Something went wrong.");
					else
						player.setNextWorldTile(new WorldTile(getRing()
								.getTile()));
				} else if (steps == 3) {
					player.setNextFaceWorldTile(new WorldTile(player.getX(),
							player.getY() - 1, player.getPlane()));
				} else if (steps == 4) {
					player.setNextAnimation(new Animation(3255));
					player.setNextGraphics(new Graphics(2671));
				} else if (steps >= 5) {
					stop();
				}
				steps++;
			}
		}, 0, 1 / 2);
	}

	public void handleTeleportHub() {
		WorldTasksManager.schedule(new WorldTask() {
			int steps;

			@Override
			public void run() {
				if (steps == 0) {
					closeInterface();
					player.setNextAnimation(new Animation(3254));
					player.setNextGraphics(new Graphics(2670));
				} else if (steps == 2) {
					player.setNextWorldTile(Rings.HUB.getTile());
				} else if (steps == 3) {
					player.setNextFaceWorldTile(new WorldTile(player.getX(),
							player.getY() - 1, player.getPlane()));
				} else if (steps == 4) {
					player.setNextAnimation(new Animation(3255));
					player.setNextGraphics(new Graphics(2671));
				} else if (steps >= 5) {
					stop();
				}
				steps++;
			}
		}, 0, 1 / 2);
	}
}