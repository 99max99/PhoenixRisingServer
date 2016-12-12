package net.kagani.game.map.bossInstance;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.kagani.game.WorldTile;
import net.kagani.game.map.MapInstance;
import net.kagani.game.map.bossInstance.BossInstanceHandler.Boss;
import net.kagani.game.player.Player;

public abstract class BossInstance {

	/*
	 * spawn speed
	 */
	public static final int STANDARD = 1, FAST = 2, FASTEST = 4;
	public static final int FFA = 0, PIN = 1, FRIENDS_ONLY = 2;

	public static int STARTING = 0, RUNNING = 1, FINISHED = 2;

	// null if real world
	private MapInstance map;
	private Player owner;
	private List<Player> players;
	private InstanceSettings settings;
	private int stage;

	/*
	 * creates instance
	 */
	public BossInstance(Player owner, InstanceSettings settings) {
		this.owner = owner;
		this.settings = settings;
		players = new CopyOnWriteArrayList<Player>();
		if (!isPublic())
			owner.setLastBossInstanceSettings(settings);
		init();
	}

	public InstanceSettings getSettings() {
		return settings;
	}

	public int getPlayersCount() {
		return players.size();
	}

	public void init() {
		if (owner != null) {
			owner.lock();
			int[] pos = getMapPos();
			int[] size = getMapSize();
			setMapInstance(pos[0], pos[1], size[0], size[1]);
			map.load(new Runnable() {
				@Override
				public void run() {
					loadMapInstance();
					stage = RUNNING;
					enterInstance(owner, false);
				}
			});
		} else {
			loadMapInstance();
			stage = RUNNING;
		}
	}

	public boolean isInstanceReady() {
		return stage == RUNNING;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean isPlayerInside(Player player) {
		synchronized (BossInstanceHandler.LOCK) {
			return players.contains(player);
		}
	}

	public abstract int[] getMapPos();

	// return 1,1 by default
	public abstract int[] getMapSize();

	public void setMapInstance(int chunkX, int chunkY, int ratioX, int ratioY) {
		map = new MapInstance(chunkX, chunkY, ratioX, ratioY);
	}

	public abstract void loadMapInstance();

	public void enterInstance(Player player, boolean login) {
		synchronized (BossInstanceHandler.LOCK) {
			if (!login)
				player.useStairs(-1, getTile(settings.getBoss().getInsideTile()), 0, 2);
			players.add(player);
			playMusic(player);
			if (!isPublic())
				player.getPackets().sendGameMessage("Welcome to this session agaisnt <col=00FFFF>" + getInstanceName()
						+ "</col>. This arena will expire in " + (settings.getTimeRemaining() / 60000) + " minutes.");
			if (!login)
				player.getControlerManager().startControler(settings.getBoss().getControllerName(), this);
			player.setLastBossInstanceKey(owner == null ? null : owner.getUsername());
		}
	}

	public void playMusic(Player player) {
		player.getMusicsManager().forcePlayMusic(settings.getBoss().getMusicId());
	}

	public String getInstanceName() {
		return settings.getBoss().name().replace("_", " ");
	}

	public static final int TELEPORTED = 0, LOGGED_OUT = 1, EXITED = 2, DIED = 3;

	public void leaveInstance(Player player, int type) {
		synchronized (BossInstanceHandler.LOCK) {
			if (type == EXITED)
				player.useStairs(-1, settings.getBoss().getOutsideTile(), 0, 2);
			else if (type == LOGGED_OUT && !isPublic()) // if public no need to
														// move the player to
														// entrance :p(exept for
														// instance vorago but
														// thats done at vorago
														// instance extend)
				player.setLocation(settings.getBoss().getOutsideTile());
			player.getMusicsManager().reset();
			players.remove(player);
			if (players.isEmpty()) // public version
				finish();
		}
	}

	public void finish() {
		if (!isPublic()) {
			stage = FINISHED;
			map.destroy(null);
			settings.getBoss().getCachedInstances().remove(getOwnerUsername());
		}
	}

	public WorldTile getTile(WorldTile tile) {
		return getTile(tile.getX(), tile.getY(), tile.getPlane());
	}

	// gets the instanced coords of the world tile(notice if the area instanced
	// differs from coords it will give wrong tile, the ratio part is to make
	// sure the coords are inside map
	public WorldTile getTile(int x, int y, int plane) {
		WorldTile tile;
		if (owner != null) {
			int[] originalPos = map.getOriginalPos();
			tile = map.getTile((x - originalPos[0] * 8) % (map.getRatioX() * 64),
					(y - originalPos[1] * 8) % (map.getRatioY() * 64));
			tile.moveLocation(0, 0, plane);
		} else
			tile = new WorldTile(x, y, plane);
		return tile;
	}

	public String getOwnerUsername() {
		return owner == null ? "" : owner.getUsername();
	}

	public boolean isPublic() {
		return owner == null;
	}

	public Boss getBoss() {
		return settings.getBoss();
	}

	public boolean isFinished() {
		return stage == FINISHED;
	}
}