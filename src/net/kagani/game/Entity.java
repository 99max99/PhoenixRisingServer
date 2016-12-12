package net.kagani.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import net.kagani.Settings;
import net.kagani.cache.loaders.AnimationDefinitions;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.game.EffectsManager.Effect;
import net.kagani.game.EffectsManager.EffectType;
import net.kagani.game.Hit.HitLook;
import net.kagani.game.map.MapUtils;
import net.kagani.game.map.MapUtils.Structure;
import net.kagani.game.minigames.stealingcreation.StealingCreationController;
import net.kagani.game.npc.NPC;
import net.kagani.game.npc.familiar.Familiar;
import net.kagani.game.npc.nomad.Nomad;
import net.kagani.game.npc.qbd.TorturedSoul;
import net.kagani.game.player.Player;
import net.kagani.game.route.RouteFinder;
import net.kagani.game.route.strategy.EntityStrategy;
import net.kagani.game.route.strategy.FixedTileStrategy;
import net.kagani.game.route.strategy.ObjectStrategy;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

public abstract class Entity extends WorldTile {

	private static final long serialVersionUID = -3372926325008880753L;
	private final static AtomicInteger hashCodeGenerator = new AtomicInteger();

	// transient stuff
	protected transient int index;
	private transient int lastRegionId; // the last region the entity was at
	private transient int sceneBaseChunkId;
	private transient CopyOnWriteArrayList<Integer> mapRegionsIds; // called by
	// more than
	// 1thread
	// so
	// concurent
	private transient int direction;
	private transient WorldTile lastWorldTile;
	private transient WorldTile nextWorldTile;
	private transient int nextWalkDirection;
	private transient int nextRunDirection;
	private transient Rectangle nextFaceWorldTile;
	private transient int mapSize; // default 0, can be setted other value
	// usefull on
	private transient boolean teleported;
	protected transient ConcurrentLinkedQueue<Object[]> walkSteps;// called by
	// more
	// than 1thread
	// so concurent
	private transient ConcurrentLinkedQueue<Hit> receivedHits;
	private transient Map<Entity, Integer> receivedDamage;
	private transient boolean finished; // if removed
	// entity masks
	private transient long freezeDelay;
	private transient Colour nextColour;
	private transient Animation nextAnimation;
	private transient Graphics nextGraphics1;
	private transient Graphics nextGraphics2;
	private transient Graphics nextGraphics3;
	private transient Graphics nextGraphics4;
	private transient ArrayList<Hit> nextHits;
	private transient ArrayList<HitBar> nextHitBars;
	private transient ForceMovement nextForceMovement;
	private transient ForceTalk nextForceTalk;
	private transient int nextFaceEntity;
	private transient int lastFaceEntity;
	private transient boolean needTargetInformationUpdate;
	private transient boolean multiArea;
	private transient boolean forceMultiArea;

	private transient Entity attackedBy; // whos attacking you, used for single
	private transient long attackedByDelay; // delay till someone else can
	private transient long attackingDelay; // tells youre attacking someone
	// attack you
	private transient boolean isAtDynamicRegion;
	private transient long lastAnimationEnd;
	private transient long findTargetDelay;
	private transient ConcurrentHashMap<Object, Object> temporaryAttributes;
	private transient int hashCode;

	// saving stuff
	private int hitpoints;
	// static maps
	private boolean run;
	private EffectsManager effectsManager;

	// creates Entity and saved classes
	public Entity(WorldTile tile) {
		super(tile);
		effectsManager = new EffectsManager();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public final void initEntity() {
		hashCode = hashCodeGenerator.getAndIncrement();
		mapRegionsIds = new CopyOnWriteArrayList<Integer>();
		walkSteps = new ConcurrentLinkedQueue<Object[]>();
		receivedHits = new ConcurrentLinkedQueue<Hit>();
		receivedDamage = new ConcurrentHashMap<Entity, Integer>();
		temporaryAttributes = new ConcurrentHashMap<Object, Object>();
		nextHits = new ArrayList<Hit>();
		nextHitBars = new ArrayList<HitBar>();
		nextWalkDirection = nextRunDirection - 1;
		lastFaceEntity = -1;
		nextFaceEntity = -2;
		effectsManager.setEntity(this);
	}

	public int getReceivedDamage(Entity source) {
		int receivedDamage = 0;
		for (Hit hit : receivedHits) {
			if (hit.getSource() != source)
				continue;
			receivedDamage += hit.getDamage();
		}
		return receivedDamage;
	}

	public int getClientIndex() {
		return index + (this instanceof Player ? 32768 : 0);
	}

	public void applyHit(Hit hit) {
		if (isDead())
			return;
		Entity source = hit.getSource();

		if (source != null)
			hit = source.handleOutgoingHit(hit, this);
		// todo damage for who gets drop
		receivedHits.add(hit);
		handleIngoingHit(hit);
	}

	/**
	 * Where the hit came from.
	 * 
	 * @param hit
	 *            The hit.
	 */

	public Hit handleOutgoingHit(Hit hit, Entity target) {
		// EMPTY
		return hit;
	}

	public abstract void handleIngoingHit(Hit hit);

	public void reset(boolean attributes) {
		setHitpoints(getMaxHitpoints());
		receivedHits.clear();
		resetCombat();
		walkSteps.clear();
		effectsManager.resetEffects();
		resetReceivedDamage();
		setAttackedBy(null);
		setAttackedByDelay(0);
		setAttackingDelay(0);
		if (attributes)
			temporaryAttributes.clear();
	}

	public void reset() {
		reset(true);
	}

	public void resetCombat() {
		attackedBy = null;
		attackedByDelay = 0;
		effectsManager.removeEffect(EffectType.BOUND);
		effectsManager.removeEffect(EffectType.BOUND_IMMUNITY);
	}

	public void processReceivedHits() {
		if (this instanceof Player) {
			if (((Player) this).getEmotesManager().getNextEmoteEnd() >= Utils
					.currentTimeMillis())
				return;
		}
		Hit hit;
		int count = 0;
		while ((hit = receivedHits.poll()) != null && count++ < 10)
			processHit(hit);
	}

	public void processHit(Hit hit) {
		if (isDead())
			return;
		removeHitpoints(hit);
		nextHits.add(hit);
		if (nextHitBars.isEmpty())
			addHitBars();
	}

	public void addHitBars() {
		nextHitBars.add(new EntityHitBar(this));
	}

	public void resetReceivedHits() {
		nextHits.clear();
		receivedHits.clear();
	}

	public void removeHitpoints(final Hit hit) {
		if (isDead() || hit.getLook() == HitLook.ABSORB_DAMAGE
				|| hit.getLook() == HitLook.HEALED_DAMAGE)
			return;
		if (hit.getDamage() > hitpoints)
			hit.setDamage(hitpoints);
		addReceivedDamage(hit.getSource(), hit.getDamage());
		setHitpoints(hitpoints - hit.getDamage());
		if (hitpoints <= 0)
			sendDeath(hit.getSource());
	}

	public void resetReceivedDamage() {
		receivedDamage.clear();
	}

	public void reduceDamage(Entity entity) {
		int dmg = getDamageReceived(entity);
		if (dmg != -1)
			receivedDamage.put(entity, dmg / 5);
	}

	public Set<Entity> getReceivedDamageSources() {
		return receivedDamage.keySet();
	}

	public Map<Entity, Integer> getReceivedDamage() {
		return receivedDamage;
	}

	public int getTotalDamageReceived() {
		int totalDmg = 0;
		for (Integer i : receivedDamage.values())
			totalDmg += i;
		return totalDmg;
	}

	public Player getMostDamageReceivedSourcePlayer() {
		Player player = null;
		int damage = -1;
		for (Entity source : receivedDamage.keySet()) {
			if (!(source instanceof Player))
				continue;
			Integer d = receivedDamage.get(source);
			if (d == null || source.hasFinished()) {
				receivedDamage.remove(source);
				continue;
			}
			if (d > damage) {
				player = (Player) source;
				damage = d;
			}
		}
		return player;
	}

	public int getDamageReceived(Entity source) {
		Integer d = receivedDamage.get(source);
		if (d == null || source.hasFinished()) {
			receivedDamage.remove(source);
			return -1;
		}
		return d;
	}

	public void processReceivedDamage() {
		if (isDead())
			return;
		for (Entity source : receivedDamage.keySet()) {
			Integer damage = receivedDamage.get(source);
			if (damage == null || source.hasFinished()) {
				receivedDamage.remove(source);
				continue;
			}
			damage--;
			if (damage == 0) {
				receivedDamage.remove(source);
				continue;
			}
			receivedDamage.put(source, damage);
		}
	}

	public void addReceivedDamage(Entity source, int amount) {
		if (source == null || source == this)
			return;
		if (source instanceof Familiar)
			source = ((Familiar) source).getOwner();
		Integer damage = receivedDamage.get(source);
		damage = damage == null ? amount : damage + amount;
		if (damage < 0)
			receivedDamage.remove(source);
		else
			receivedDamage.put(source, damage);
	}

	public void heal(int amount) {
		heal(amount, 0);
	}

	public void heal(int amount, int extra) {
		heal(amount, extra, 0);
	}

	public void heal(int amount, int extra, int delay) {
		heal(amount, extra, delay, false);
	}

	public void heal(int amount, int extra, int delay, boolean displayMark) {
		if (isDead())
			return;
		boolean aboveMaxHP = hitpoints + amount >= getMaxHitpoints() + extra;
		int hp = aboveMaxHP ? getMaxHitpoints() + extra : hitpoints + amount;
		if (hitpoints > hp)
			return;
		if (displayMark) {
			int damage = hp - hitpoints;
			if (damage > 0)
				applyHit(new Hit(this, damage, HitLook.HEALED_DAMAGE, delay));
		}
		setHitpoints(hp);
	}

	public boolean hasWalkSteps() {
		return !walkSteps.isEmpty();
	}

	public abstract void sendDeath(Entity source);

	public Entity getCurrentFaceEntity() {
		return lastFaceEntity >= 0 ? (lastFaceEntity >= 32768 ? World
				.getPlayers().get(lastFaceEntity - 32768) : World.getNPCs()
				.get(lastFaceEntity)) : null;
	}

	public void processMovement() {
		lastWorldTile = new WorldTile(this);
		if (lastFaceEntity >= 0) {
			Entity target = lastFaceEntity >= 32768 ? World.getPlayers().get(
					lastFaceEntity - 32768) : World.getNPCs().get(
					lastFaceEntity);
			if (target != null) {
				int size = target.getSize();
				updateAngle(target, size, size);
			}
			// direction =
			// Utils.getFaceDirection(target.getCoordFaceX(target.getSize()) -
			// getCoordFaceX(getSize()), target.getCoordFaceY(target.getSize())
			// - getCoordFaceY(getSize()));
		}
		nextWalkDirection = nextRunDirection = -1;
		if (nextWorldTile != null) {
			// int lastPlane = getPlane();
			setLocation(nextWorldTile);
			nextWorldTile = null;
			teleported = true;
			// not required anymore due to new gpi change
			/*
			 * if (this instanceof Player && ((Player)
			 * this).getTemporaryMoveType() == -1) ((Player)
			 * this).setTemporaryMoveType(Player.TELE_MOVE_TYPE);
			 */
			World.updateEntityRegion(this);
			if (needMapUpdate())
				loadMapRegions();
			/*
			 * else if (this instanceof Player && lastPlane != getPlane())
			 * ((Player) this).setClientHasntLoadedMapRegion();
			 */
			resetWalkSteps();
			return;
		}
		teleported = false;
		if (walkSteps.isEmpty())
			return;
		if (this instanceof Player) { // emotes are special on rs, when using
			// one u will walk once emote done
			if (((Player) this).getEmotesManager().getNextEmoteEnd() >= Utils
					.currentTimeMillis())
				return;
		}
		if (this instanceof TorturedSoul) { // waste of process power personaly
			// but meh.
			if (((TorturedSoul) this).switchWalkStep()) {
				return;
			}
		}
		if (this instanceof Player && ((Player) this).getRunEnergy() <= 0)
			setRun(false);
		for (int stepCount = 0; stepCount < (run ? 2 : 1); stepCount++) {
			Object[] nextStep = getNextWalkStep();
			if (nextStep == null)
				break;
			int dir = (int) nextStep[0];
			if (((boolean) nextStep[3] && !World.checkWalkStep(getPlane(),
					getX(), getY(), dir, getSize()))
					|| (this instanceof NPC && !canWalkNPC(getX()
							+ Utils.DIRECTION_DELTA_X[dir], getY()
							+ Utils.DIRECTION_DELTA_Y[dir])) || !canMove(dir)) {
				resetWalkSteps();
				break;
			}
			if (stepCount == 0)
				nextWalkDirection = dir;
			else
				nextRunDirection = dir;
			moveLocation(Utils.DIRECTION_DELTA_X[dir],
					Utils.DIRECTION_DELTA_Y[dir], 0);
			if (run && stepCount == 0) { // fixes impossible steps
				Object[] previewStep = previewNextWalkStep();
				if (previewStep == null)
					break;
				int previewDir = (int) previewStep[0];
				if (Utils.getPlayerRunningDirection(
						Utils.DIRECTION_DELTA_X[dir]
								+ Utils.DIRECTION_DELTA_X[previewDir],
						Utils.DIRECTION_DELTA_Y[dir]
								+ Utils.DIRECTION_DELTA_Y[previewDir]) == -1)
					break;
			}
		}
		World.updateEntityRegion(this);
		if (needMapUpdate())
			loadMapRegions();
	}

	public abstract boolean canMove(int dir);

	@Override
	public void moveLocation(int xOffset, int yOffset, int planeOffset) {
		super.moveLocation(xOffset, yOffset, planeOffset);
		direction = Utils.getAngle(xOffset, yOffset);
	}

	private boolean needMapUpdate() {
		return needMapUpdate(this);
	}

	public boolean needMapUpdate(WorldTile tile) {
		int baseChunk[] = MapUtils.decode(Structure.CHUNK, sceneBaseChunkId);
		// chunks length - offset. if within 16 tiles of border it updates map
		int limit = Settings.MAP_SIZES[mapSize] / 8 - 2;

		int offsetX = tile.getChunkX() - baseChunk[0];
		int offsetY = tile.getChunkY() - baseChunk[1];

		return offsetX < 2 || offsetX >= limit || offsetY < 2
				|| offsetY >= limit;
	}

	// normal walk steps method
	public boolean addWalkSteps(int destX, int destY) {
		return addWalkSteps(destX, destY, -1);
	}

	public boolean clipedProjectile(WorldTile tile, boolean checkClose) {
		if (tile instanceof Entity) {
			Entity e = (Entity) tile;
			WorldTile me = this;
			if (tile instanceof NPC) {
				NPC n = (NPC) tile;
				tile = n.getMiddleWorldTile();
			} else if (this instanceof NPC) {
				NPC n = (NPC) this;
				me = n.getMiddleWorldTile();
			}
			return clipedProjectile(tile, checkClose, 1)
					|| e.clipedProjectile(me, checkClose, 1);
		}
		return clipedProjectile(tile, checkClose, 1);
	}

	public boolean clipedProjectile(WorldTile tile, boolean checkClose, int size) {
		int myX = getX();
		int myY = getY();
		if (this instanceof NPC) {
			NPC n = (NPC) this;
			WorldTile thist = n.getMiddleWorldTile();
			myX = thist.getX();
			myY = thist.getY();
		}
		int destX = tile.getX();
		int destY = tile.getY();
		if (myX == destX && destY == myY)
			return true;
		int lastTileX = myX;
		int lastTileY = myY;
		while (true) {
			if (myX < destX)
				myX++;
			else if (myX > destX)
				myX--;
			if (myY < destY)
				myY++;
			else if (myY > destY)
				myY--;
			int dir = Utils.getMoveDirection(myX - lastTileX, myY - lastTileY);
			if (dir == -1)
				return false;
			if (checkClose) {
				if (!World.checkWalkStep(getPlane(), lastTileX, lastTileY, dir,
						size))
					return false;
			} else if (!World.checkProjectileStep(getPlane(), lastTileX,
					lastTileY, dir, size))
				return false;
			lastTileX = myX;
			lastTileY = myY;
			if (lastTileX == destX && lastTileY == destY)
				return true;
		}
	}

	public boolean calcFollow(WorldTile target, boolean inteligent) {
		return calcFollow(target, -1, true, inteligent);
	}

	// checks collisions
	public boolean canWalkNPC(int toX, int toY) {
		// stucking nomad is part of strategy
		if (this instanceof Familiar || this instanceof Nomad
				|| ((NPC) this).isIntelligentRouteFinder()
				|| ((NPC) this).isForceWalking())
			return true;
		int size = getSize();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> npcIndexes = World.getRegion(regionId)
					.getNPCsIndexes();
			if (npcIndexes != null/* && npcIndexes.size() < 100 */) {
				for (int npcIndex : npcIndexes) {
					NPC target = World.getNPCs().get(npcIndex);
					if (target == null || target == this || target.isDead()
							|| target.hasFinished()
							|| target.getPlane() != getPlane()
							|| target instanceof Familiar)
						continue;
					int targetSize = target.getSize();
					// npc is under this target so skip checking it
					if (Utils.colides(this, target))
						continue;
					WorldTile tile = new WorldTile(target);
					// has to be checked aswell, cuz other one assumes npc will
					// manage to move no matter what
					if (Utils.colides(toX, toY, size, tile.getX(), tile.getY(),
							targetSize))
						return false;
					if (target.getNextWalkDirection() != -1) {
						tile.moveLocation(Utils.DIRECTION_DELTA_X[target
								.getNextWalkDirection()],
								Utils.DIRECTION_DELTA_Y[target
										.getNextWalkDirection()], 0);
						if (target.getNextRunDirection() != -1)
							tile.moveLocation(Utils.DIRECTION_DELTA_X[target
									.getNextRunDirection()],
									Utils.DIRECTION_DELTA_Y[target
											.getNextRunDirection()], 0);
						// target is at x,y
						if (Utils.colides(toX, toY, size, tile.getX(),
								tile.getY(), targetSize))
							return false;
					}
				}
			}
		}
		return true;
	}

	public WorldTile getMiddleWorldTile() {
		int size = getSize();
		return size == 1 ? this : new WorldTile(getCoordFaceX(size),
				getCoordFaceY(size), getPlane());
	}

	public static boolean findBasicRoute(Entity src, WorldTile dest,
			int maxStepsCount, boolean calculate) {
		int[] srcPos = src.getLastWalkTile();
		int[] destPos = { dest.getX(), dest.getY() };
		int srcSize = src.getSize();
		// set destSize to 0 to walk under it else follows
		int destSize = dest instanceof Entity ? ((Entity) dest).getSize() : 1;
		int[] destScenePos = { destPos[0] + destSize - 1,
				destPos[1] + destSize - 1 };// Arrays.copyOf(destPos,
		// 2);//destSize
		// ==
		// 1
		// ?
		// Arrays.copyOf(destPos,
		// 2)
		// :
		// new
		// int[]
		// {WorldTile.getCoordFaceX(destPos[0],
		// destSize,
		// destSize,
		// -1),
		// WorldTile.getCoordFaceY(destPos[1],
		// destSize,
		// destSize,
		// -1)};
		while (maxStepsCount-- != 0) {
			int[] srcScenePos = { srcPos[0] + srcSize - 1,
					srcPos[1] + srcSize - 1 };// srcSize
			// ==
			// 1
			// ?
			// Arrays.copyOf(srcPos,
			// 2)
			// :
			// new
			// int[]
			// {
			// WorldTile.getCoordFaceX(srcPos[0],
			// srcSize,
			// srcSize,
			// -1),
			// WorldTile.getCoordFaceY(srcPos[1],
			// srcSize,
			// srcSize,
			// -1)};
			if (!Utils.isOnRange(srcPos[0], srcPos[1], srcSize, destPos[0],
					destPos[1], destSize, 0)) {
				if (srcScenePos[0] < destScenePos[0]
						&& srcScenePos[1] < destScenePos[1]
						&& src.addWalkStep(srcPos[0] + 1, srcPos[1] + 1,
								srcPos[0], srcPos[1], true)) {
					srcPos[0]++;
					srcPos[1]++;
					continue;
				}
				if (srcScenePos[0] > destScenePos[0]
						&& srcScenePos[1] > destScenePos[1]
						&& src.addWalkStep(srcPos[0] - 1, srcPos[1] - 1,
								srcPos[0], srcPos[1], true)) {
					srcPos[0]--;
					srcPos[1]--;
					continue;
				}
				if (srcScenePos[0] < destScenePos[0]
						&& srcScenePos[1] > destScenePos[1]
						&& src.addWalkStep(srcPos[0] + 1, srcPos[1] - 1,
								srcPos[0], srcPos[1], true)) {
					srcPos[0]++;
					srcPos[1]--;
					continue;
				}
				if (srcScenePos[0] > destScenePos[0]
						&& srcScenePos[1] < destScenePos[1]
						&& src.addWalkStep(srcPos[0] - 1, srcPos[1] + 1,
								srcPos[0], srcPos[1], true)) {
					srcPos[0]--;
					srcPos[1]++;
					continue;
				}
				if (srcScenePos[0] < destScenePos[0]
						&& src.addWalkStep(srcPos[0] + 1, srcPos[1], srcPos[0],
								srcPos[1], true)) {
					srcPos[0]++;
					continue;
				}
				if (srcScenePos[0] > destScenePos[0]
						&& src.addWalkStep(srcPos[0] - 1, srcPos[1], srcPos[0],
								srcPos[1], true)) {
					srcPos[0]--;
					continue;
				}
				if (srcScenePos[1] < destScenePos[1]
						&& src.addWalkStep(srcPos[0], srcPos[1] + 1, srcPos[0],
								srcPos[1], true)) {
					srcPos[1]++;
					continue;
				}
				if (srcScenePos[1] > destScenePos[1]
						&& src.addWalkStep(srcPos[0], srcPos[1] - 1, srcPos[0],
								srcPos[1], true)) {
					srcPos[1]--;
					continue;
				}
				return false;
			}
			break; // for now nothing between break and return
		}
		return true;
	}

	// used for normal npc follow int maxStepsCount, boolean calculate used to
	// save mem on normal path
	public boolean calcFollow(WorldTile target, int maxStepsCount,
			boolean calculate, boolean inteligent) {
		if (inteligent) {
			int steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER,
					getX(), getY(), getPlane(), getSize(),
					target instanceof WorldObject ? new ObjectStrategy(
							(WorldObject) target)
							: target instanceof Entity ? new EntityStrategy(
									(Entity) target) : new FixedTileStrategy(
									target.getX(), target.getY()), true);
			if (steps == -1)
				return false;
			if (steps == 0)
				return true;
			int[] bufferX = RouteFinder.getLastPathBufferX();
			int[] bufferY = RouteFinder.getLastPathBufferY();
			for (int step = steps - 1; step >= 0; step--) {
				if (!addWalkSteps(bufferX[step], bufferY[step], 25, true))
					break;
			}
			return true;
		}
		return findBasicRoute(this, target, maxStepsCount, true);
		/*
		 * else if (true == true) { //just keeping old code
		 * System.out.println("test"); return findBasicRoute(this, (Entity)
		 * target, maxStepsCount, true); }
		 * 
		 * int[] lastTile = getLastWalkTile(); int myX = lastTile[0]; int myY =
		 * lastTile[1]; int stepCount = 0; int size = getSize(); int destX =
		 * target.getX(); int destY = target.getY(); int sizeX = target
		 * instanceof WorldObject ?
		 * ((WorldObject)target).getDefinitions().getSizeX() :
		 * ((Entity)target).getSize(); int sizeY = target instanceof WorldObject
		 * ? ((WorldObject)target).getDefinitions().getSizeY() : sizeX; while
		 * (true) { stepCount++; int myRealX = myX; int myRealY = myY; if
		 * (Utils.isOnRange(myX, myY, size, destX, destY, sizeX, 0)) return
		 * true; if (myX < destX) myX++; else if (myX > destX) myX--; if (myY <
		 * destY) myY++; else if (myY > destY) myY--; if ((this instanceof NPC
		 * && !canWalkNPC(myX, myY)) || !addWalkStep(myX, myY, lastTile[0],
		 * lastTile[1], true)) { if (!calculate) return false; myX = myRealX;
		 * myY = myRealY; int[] myT = calculatedStep(myRealX, myRealY, destX,
		 * destY, lastTile[0], lastTile[1], sizeX, sizeY); if (myT == null)
		 * return false; myX = myT[0]; myY = myT[1]; } if (stepCount ==
		 * maxStepsCount) return true; lastTile[0] = myX; lastTile[1] = myY; if
		 * (lastTile[0] == destX && lastTile[1] == destY) return true; }
		 */
	}

	// used for normal npc follow
	@SuppressWarnings("unused")
	private int[] calculatedStep(int myX, int myY, int destX, int destY,
			int lastX, int lastY, int sizeX, int sizeY) {
		if (myX < destX) {
			myX++;
			if ((this instanceof NPC && !canWalkNPC(myX, myY))
					|| !addWalkStep(myX, myY, lastX, lastY, true))
				myX--;
			else if (!(myX - destX > sizeX || myX - destX < -1
					|| myY - destY > sizeY || myY - destY < -1)) {
				if (myX == lastX || myY == lastY)
					return null;
				return new int[] { myX, myY };
			}
		} else if (myX > destX) {
			myX--;
			if ((this instanceof NPC && !canWalkNPC(myX, myY))
					|| !addWalkStep(myX, myY, lastX, lastY, true))
				myX++;
			else if (!(myX - destX > sizeX || myX - destX < -1
					|| myY - destY > sizeY || myY - destY < -1)) {
				if (myX == lastX || myY == lastY)
					return null;
				return new int[] { myX, myY };
			}
		}
		if (myY < destY) {
			myY++;
			if ((this instanceof NPC && !canWalkNPC(myX, myY))
					|| !addWalkStep(myX, myY, lastX, lastY, true))
				myY--;
			else if (!(myX - destX > sizeX || myX - destX < -1
					|| myY - destY > sizeY || myY - destY < -1)) {
				if (myX == lastX || myY == lastY)
					return null;
				return new int[] { myX, myY };
			}
		} else if (myY > destY) {
			myY--;
			if ((this instanceof NPC && !canWalkNPC(myX, myY))
					|| !addWalkStep(myX, myY, lastX, lastY, true)) {
				myY++;
			} else if (!(myX - destX > sizeX || myX - destX < -1
					|| myY - destY > sizeY || myY - destY < -1)) {
				if (myX == lastX || myY == lastY)
					return null;
				return new int[] { myX, myY };
			}
		}
		if (myX == lastX || myY == lastY)
			return null;
		return new int[] { myX, myY };
	}

	/*
	 * return added all steps
	 */
	public boolean addWalkSteps(final int destX, final int destY,
			int maxStepsCount) {
		return addWalkSteps(destX, destY, maxStepsCount, true);
	}

	/*
	 * return added all steps
	 */
	public boolean addWalkSteps(final int destX, final int destY,
			int maxStepsCount, boolean check) {
		int[] lastTile = getLastWalkTile();
		int myX = lastTile[0];
		int myY = lastTile[1];
		int stepCount = 0;
		while (true) {
			stepCount++;
			if (myX < destX)
				myX++;
			else if (myX > destX)
				myX--;
			if (myY < destY)
				myY++;
			else if (myY > destY)
				myY--;
			if (!addWalkStep(myX, myY, lastTile[0], lastTile[1], check))
				return false;
			if (stepCount == maxStepsCount)
				return true;
			lastTile[0] = myX;
			lastTile[1] = myY;
			if (lastTile[0] == destX && lastTile[1] == destY)
				return true;
		}
	}

	public abstract int[] getBonuses();

	private int[] getLastWalkTile() {
		Object[] objects = walkSteps.toArray();
		if (objects.length == 0)
			return new int[] { getX(), getY() };
		Object step[] = (Object[]) objects[objects.length - 1];
		return new int[] { (int) step[1], (int) step[2] };
	}

	public boolean addWalkStep(int nextX, int nextY, int lastX, int lastY,
			boolean check) {
		int dir = Utils.getMoveDirection(nextX - lastX, nextY - lastY);
		if (dir == -1)
			return false;
		if (check
				&& !World.checkWalkStep(getPlane(), lastX, lastY, dir,
						getSize())
				|| (this instanceof NPC && !canWalkNPC(getX()
						+ Utils.DIRECTION_DELTA_X[dir], getY()
						+ Utils.DIRECTION_DELTA_Y[dir]))) // double
															// check
															// must
															// be
			// done sadly cuz of
			// npc under check, can
			// be improved later to
			// only check when we
			// want
			return false;
		if (this instanceof Player) {
			if (!((Player) this).getControlerManager().addWalkStep(lastX,
					lastY, nextX, nextY))
				return false;
		}
		walkSteps.add(new Object[] { dir, nextX, nextY, check });
		return true;
	}

	public ConcurrentLinkedQueue<Object[]> getWalkSteps() {
		return walkSteps;
	}

	public void resetWalkSteps() {
		walkSteps.clear();
	}

	private Object[] getNextWalkStep() {
		Object[] step = walkSteps.poll();
		if (step == null)
			return null;
		return step;
	}

	private Object[] previewNextWalkStep() {
		Object[] step = walkSteps.peek();
		if (step == null)
			return null;
		return step;
	}

	public boolean restoreHitPoints() {
		int maxHp = getMaxHitpoints();
		// hp doesnt go down in rs when above max it seems
		/*
		 * if (hitpoints > maxHp) { if (this instanceof Player) { Player player
		 * = (Player) this; if (player.getPrayer().usingPrayer(1, 9) &&
		 * Utils.random(100) <= 15) return false; } setHitpoints(hitpoints - 1);
		 * return true; } else
		 */if (hitpoints < maxHp) {
			if (this instanceof Player && World.isSafeZone(this))
				setHitpoints(maxHp - hitpoints < 1000 ? maxHp
						: (hitpoints + 1000));
			else
				setHitpoints(hitpoints + 1);
			// done differently now
			/*
			 * if (this instanceof Player) { Player player = (Player) this; if
			 * (player.getPrayer().usingPrayer(0, 9) && hitpoints < maxHp)
			 * setHitpoints(hitpoints + 1); else if
			 * (player.getPrayer().usingPrayer(0, 26) && hitpoints < maxHp)
			 * setHitpoints(hitpoints + (hitpoints + 4 > maxHp ? maxHp -
			 * hitpoints : 4));
			 * 
			 * }
			 */
			return true;
		}
		return false;
	}

	public int getHealRestoreRate() {
		return 10;
	}

	public boolean needMasksUpdate() {
		return nextColour != null
				|| nextFaceEntity != -2
				|| nextAnimation != null
				|| nextGraphics1 != null
				|| nextGraphics2 != null
				|| nextGraphics3 != null
				|| nextGraphics4 != null
				|| (nextWalkDirection == -1 && nextRunDirection == -1 && nextFaceWorldTile != null)
				|| !nextHits.isEmpty() || !nextHitBars.isEmpty()
				|| nextForceMovement != null || nextForceTalk != null
				|| needTargetInformationUpdate;
	}

	public boolean isDead() {
		return hitpoints == 0;
	}

	public void resetMasks() {
		nextAnimation = null;
		nextGraphics1 = null;
		nextGraphics2 = null;
		nextGraphics3 = null;
		nextGraphics4 = null;
		if (nextWalkDirection == -1)
			nextFaceWorldTile = null;
		nextForceMovement = null;
		nextForceTalk = null;
		nextColour = null;
		nextFaceEntity = -2;
		nextHits.clear();
		nextHitBars.clear();
		needTargetInformationUpdate = false;
	}

	public abstract void finish();

	public abstract int getMaxHitpoints();

	public void processEntityUpdate() {
		processMovement();
		processReceivedHits();
		processReceivedDamage();
	}

	public void processEntity() {
		effectsManager.processEffects();
	}

	public void loadMapRegions() {
		mapRegionsIds.clear();
		isAtDynamicRegion = false;
		int chunkX = getChunkX();
		int chunkY = getChunkY();
		int sceneChunksRadio = Settings.MAP_SIZES[mapSize] / 16; // 16 cuz a
		// chunk is 8.
		// radio is
		// half
		int sceneBaseChunkX = (chunkX - sceneChunksRadio);
		int sceneBaseChunkY = (chunkY - sceneChunksRadio);
		if (sceneBaseChunkX < 0)
			sceneBaseChunkX = 0;
		if (sceneBaseChunkY < 0)
			sceneBaseChunkY = 0;
		int fromRegionX = sceneBaseChunkX / 8; // doesnt go negative.
		int fromRegionY = sceneBaseChunkY / 8;
		int toRegionX = (chunkX + sceneChunksRadio) / 8;
		int toRegionY = (chunkY + sceneChunksRadio) / 8;

		for (int regionX = fromRegionX; regionX <= toRegionX; regionX++)
			for (int regionY = fromRegionY; regionY <= toRegionY; regionY++) {
				int regionId = MapUtils.encode(Structure.REGION, regionX,
						regionY);
				if (World.getRegion(regionId, this instanceof Player) instanceof DynamicRegion)
					isAtDynamicRegion = true;
				mapRegionsIds.add(regionId);
			}
		sceneBaseChunkId = MapUtils.encode(Structure.CHUNK, sceneBaseChunkX,
				sceneBaseChunkY);
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public int getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(int hitpoints) {
		if (hitpoints == this.hitpoints)
			return;
		this.hitpoints = hitpoints;
		needTargetInformationUpdate = true;
	}

	public void setLastRegionId(int lastRegionId) {
		this.lastRegionId = lastRegionId;
	}

	public int getLastRegionId() {
		return lastRegionId;
	}

	public int getMapSize() {
		return mapSize;
	}

	public void setMapSize(int size) {
		this.mapSize = size;
		loadMapRegions();
	}

	public CopyOnWriteArrayList<Integer> getMapRegionsIds() {
		return mapRegionsIds;
	}

	public void setNextAnimation(Animation nextAnimation) {
		if (nextAnimation != null && nextAnimation.getIds()[0] >= 0)
			lastAnimationEnd = Utils.currentTimeMillis()
					+ AnimationDefinitions.getAnimationDefinitions(
							nextAnimation.getIds()[0]).getEmoteTime()
					+ (nextAnimation.getDelay() * 10);
		this.nextAnimation = nextAnimation;
	}

	public void setNextAnimationNoPriority(Animation nextAnimation) {
		// adding 1 tick cuz of emote delays
		if (lastAnimationEnd + 600 >= Utils.currentTimeMillis())
			return;
		setNextAnimation(nextAnimation);
	}

	public Animation getNextAnimation() {
		return nextAnimation;
	}

	public void setNextGraphics(Graphics nextGraphics) {
		if (nextGraphics == null) {
			if (nextGraphics4 != null)
				nextGraphics4 = null;
			else if (nextGraphics3 != null)
				nextGraphics3 = null;
			else if (nextGraphics2 != null)
				nextGraphics2 = null;
			else
				nextGraphics1 = null;
		} else {
			if (nextGraphics.equals(nextGraphics1)
					|| nextGraphics.equals(nextGraphics2)
					|| nextGraphics.equals(nextGraphics3)
					|| nextGraphics.equals(nextGraphics4))
				return;
			if (nextGraphics1 == null)
				nextGraphics1 = nextGraphics;
			else if (nextGraphics2 == null)
				nextGraphics2 = nextGraphics;
			else if (nextGraphics3 == null)
				nextGraphics3 = nextGraphics;
			else if (nextGraphics4 == null)
				nextGraphics4 = nextGraphics;
		}
	}

	public Graphics getNextGraphics1() {
		return nextGraphics1;
	}

	public Graphics getNextGraphics2() {
		return nextGraphics2;
	}

	public Graphics getNextGraphics3() {
		return nextGraphics3;
	}

	public Graphics getNextGraphics4() {
		return nextGraphics4;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean hasFinished() {
		return finished;
	}

	public void setNextWorldTile(WorldTile nextWorldTile) {
		this.nextWorldTile = nextWorldTile;
	}

	public WorldTile getNextWorldTile() {
		return nextWorldTile;
	}

	public boolean hasTeleported() {
		return teleported;
	}

	public int getSceneBaseChunkId() {
		return sceneBaseChunkId;
	}

	public int getNextWalkDirection() {
		return nextWalkDirection;
	}

	public int getNextRunDirection() {
		return nextRunDirection;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public boolean getRun() {
		return run;
	}

	public Rectangle getNextFaceWorldTile() {
		return nextFaceWorldTile;
	}

	// @Deprecated used to face simply a tiel
	// "use setNextFaceRectanglePrecise(tile, 1, 1)
	public void setNextFaceWorldTile(WorldTile nextFaceWorldTile) {
		// who the hell made setNextFaceworldTile() ??!!! fucking incorrect
		// calcs.....
		// also the thing you guys call direction is actually angle
		setNextFaceRectanglePrecise(nextFaceWorldTile, 1, 1);
	}

	public void setNextFaceRectanglePrecise(WorldTile base, int sizeX, int sizeY) {
		if (nextFaceWorldTile != null
				&& nextFaceWorldTile.getX() == base.getX()
				&& nextFaceWorldTile.getY() == base.getY()
				&& nextFaceWorldTile.getSizeX() == sizeX
				&& nextFaceWorldTile.getSizeY() == sizeY)
			return;
		nextFaceWorldTile = new Rectangle(base.getX(), base.getY(), sizeX,
				sizeY);
		updateAngle(base, sizeX, sizeY);
	}

	/*
	 * avoid using it :p
	 */
	@Deprecated
	public void updateAngle(WorldTile base, int sizeX, int sizeY) {
		WorldTile from = nextWorldTile != null ? nextWorldTile : this;
		int srcX = (from.getX() * 512) + (getSize() * 256);
		int srcY = (from.getY() * 512) + (getSize() * 256);
		int dstX = (base.getX() * 512) + (sizeX * 256);
		int dstY = (base.getY() * 512) + (sizeY * 256);
		int deltaX = srcX - dstX;
		int deltaY = srcY - dstY;
		direction = deltaX != 0 || deltaY != 0 ? (int) (Math.atan2(deltaX,
				deltaY) * 2607.5945876176133) & 0x3FFF : 0;
	}

	public abstract int getSize();

	public void cancelFaceEntityNoCheck() {
		nextFaceEntity = -2;
		lastFaceEntity = -1;
	}

	public void setNextFaceEntity(Entity entity) {
		if (entity == null) {
			nextFaceEntity = -1;
			lastFaceEntity = -1;
		} else {
			nextFaceEntity = entity.getClientIndex();
			lastFaceEntity = nextFaceEntity;
		}
	}

	public int getNextFaceEntity() {
		return nextFaceEntity;
	}

	/*
	 * override this for npcs
	 */
	public boolean isStunImmune() {
		return effectsManager.hasActiveEffect(EffectType.ANTICIPATION)
				|| effectsManager.hasActiveEffect(EffectType.FREEDOM);
	}

	public int getLastFaceEntity() {
		return lastFaceEntity;
	}

	public boolean isBoundImmune() {
		// size 2+ = too big to be frozen
		return effectsManager.hasActiveEffect(EffectType.FREEDOM)
				|| effectsManager.hasActiveEffect(EffectType.BOUND_IMMUNITY)
				|| getSize() > 2;
	}

	public abstract double getMagePrayerMultiplier();

	public abstract double getRangePrayerMultiplier();

	public abstract double getMeleePrayerMultiplier();

	public Entity getAttackedBy() {
		return attackedBy;
	}

	public void setAttackedBy(Entity attackedBy) {
		this.attackedBy = attackedBy;
	}

	public long getAttackedByDelay() {
		return attackedByDelay;
	}

	public void setAttackedByDelay(long attackedByDelay) {
		this.attackedByDelay = attackedByDelay;
	}

	public boolean isAtDynamicRegion() {
		return isAtDynamicRegion;
	}

	public ForceMovement getNextForceMovement() {
		return nextForceMovement;
	}

	public void setNextForceMovement(ForceMovement nextForceMovement) {
		this.nextForceMovement = nextForceMovement;
	}

	public EffectsManager getEffectsManager() {
		return effectsManager;
	}

	public ForceTalk getNextForceTalk() {
		return nextForceTalk;
	}

	public void setNextForceTalk(ForceTalk nextForceTalk) {
		this.nextForceTalk = nextForceTalk;
	}

	public void faceEntity(Entity target) {
		if (getName().contains("musician"))
			return;
		setNextFaceRectanglePrecise(new WorldTile(target.getX(), target.getY(),
				target.getPlane()), target.getSize(), target.getSize());
	}

	public void faceObject(WorldObject object) {
		ObjectDefinitions def = object.getDefinitions();
		int x = -1, y = -1;
		int sizeX = 1, sizeY = 1;
		if (object.getType() == 0) { // wall
			if (object.getRotation() == 0) { // west
				x = object.getX() - 1;
				y = object.getY();
			} else if (object.getRotation() == 1) { // north
				x = object.getX();
				y = object.getY() + 1;
			} else if (object.getRotation() == 2) { // east
				x = object.getX() + 1;
				y = object.getY();
			} else if (object.getRotation() == 3) { // south
				x = object.getX();
				y = object.getY() - 1;
			}
		} else if (object.getType() == 1 || object.getType() == 2) { // corner
			// and
			// cornerwall
			if (object.getRotation() == 0) { // nw
				x = object.getX() - 1;
				y = object.getY() + 1;
			} else if (object.getRotation() == 1) { // ne
				x = object.getX() + 1;
				y = object.getY() + 1;
			} else if (object.getRotation() == 2) { // se
				x = object.getX() + 1;
				y = object.getY() - 1;
			} else if (object.getRotation() == 3) { // sw
				x = object.getX() - 1;
				y = object.getY() - 1;
			}
		} else if (object.getType() == 3) { // inverted corner
			if (object.getRotation() == 0) { // se
				x = object.getX() + 1;
				y = object.getY() - 1;
			} else if (object.getRotation() == 1) { // sw
				x = object.getX() - 1;
				y = object.getY() - 1;
			} else if (object.getRotation() == 2) { // nw
				x = object.getX() - 1;
				y = object.getY() + 1;
			} else if (object.getRotation() == 3) { // ne
				x = object.getX() + 1;
				y = object.getY() + 1;
			}
		} else if (object.getType() < 10) { // walldeco's
			if (object.getRotation() == 0) { // west
				x = object.getX() - 1;
				y = object.getY();
			} else if (object.getRotation() == 1) { // north
				x = object.getX();
				y = object.getY() + 1;
			} else if (object.getRotation() == 2) { // east
				x = object.getX() + 1;
				y = object.getY();
			} else if (object.getRotation() == 3) { // south
				x = object.getX();
				y = object.getY() - 1;
			}
		} else if (object.getType() == 10 || object.getType() == 11
				|| object.getType() == 22) { // multisized
			// rect
			// objs
			if (object.getRotation() == 0 || object.getRotation() == 2) {
				x = object.getX();
				y = object.getY();
				sizeX = def.getSizeX();
				sizeY = def.getSizeY();
			} else {
				x = object.getX();
				y = object.getY();
				sizeX = def.getSizeY();
				sizeY = def.getSizeX();
			}
		} else {
			// rest
			x = object.getX();
			y = object.getY();
		}

		setNextFaceRectanglePrecise(new WorldTile(x, y, getPlane()), sizeX,
				sizeY);
	}

	public long getLastAnimationEnd() {
		return lastAnimationEnd;
	}

	public ConcurrentHashMap<Object, Object> getTemporaryAttributtes() {
		return temporaryAttributes;
	}

	public WorldTile getLastWorldTile() {
		return lastWorldTile;
	}

	public ArrayList<Hit> getNextHits() {
		return nextHits;
	}

	public void playSoundEffect(int soundId) {
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId)
					.getPlayerIndexes();
			if (playerIndexes != null) {
				for (int playerIndex : playerIndexes) {
					Player player = World.getPlayers().get(playerIndex);
					if (player == null || !player.isRunning()
							|| !withinDistance(player))
						continue;
					player.getPackets().sendSoundEffect(soundId);
				}
			}
		}
	}

	public long getFindTargetDelay() {
		return findTargetDelay;
	}

	public void setFindTargetDelay(long findTargetDelay) {
		this.findTargetDelay = findTargetDelay;
	}

	public void sendSoulSplit(final Hit hit, final Entity user) {
		if (hit.getDamage() > 0) {
			Projectile projectile = World.sendProjectileNew(this, user, 2263,
					11, 11, 30, -1, 20, 0);
			if (this instanceof Player)
				((Player) Entity.this).getPrayer().drainPrayer(
						hit.getDamage() / 85);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					setNextGraphics(new Graphics(2264));
				}
			});
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (user.isDead())
						return;
					user.heal(
							(int) (hit.getDamage() > 2000 ? (200 + ((hit
									.getDamage() - 2000) * 0.05)) : hit
									.getDamage() * 0.1), 0, 0, false);
				}
			}, Utils.projectileTimeToCycles(projectile.getEndTime()));
		}
	}

	public ArrayList<HitBar> getNextHitBars() {
		return nextHitBars;
	}

	public long getAttackingDelay() {
		return attackingDelay;
	}

	public void setAttackingDelay(long attackingDelay) {
		this.attackingDelay = attackingDelay;
	}

	public boolean isUnderCombat() {
		return getAttackedByDelay() + 6000 >= Utils.currentTimeMillis()
				|| getAttackingDelay() + 6000 >= Utils.currentTimeMillis();
	}

	public abstract String getName();

	public abstract int getCombatLevel();

	public boolean isNeedTargetInformationUpdate() {
		return needTargetInformationUpdate;
	}

	public boolean isPoisonImmune() {
		return false;
	}

	public boolean isBound() {
		return effectsManager.hasActiveEffect(EffectType.BOUND)
				|| effectsManager.hasActiveEffect(EffectType.BINDING_SHOT);
	}

	public boolean isStunned() {
		return effectsManager.hasActiveEffect(EffectType.STUNNED);
	}

	public void setStunDelay(int delay) {
		effectsManager.startEffect(new Effect(EffectType.STUNNED, delay));
	}

	public void setBoundDelay(int delay) {
		setBoundDelay(delay, false);
	}

	public void setBoundDelay(int delay, boolean frozen) {
		setBoundDelay(delay, frozen, 3);
	}

	public void setBoundDelay(int delay, boolean frozen, int immunityDelay) {
		effectsManager.startEffect(new Effect(EffectType.BOUND, delay, frozen,
				immunityDelay));
	}

	public abstract void giveXP();

	public Colour getNextColour() {
		return nextColour;
	}

	public void setNextColour(Colour nextColour) {
		this.nextColour = nextColour;
	}

	public boolean isForceMultiArea() {
		return forceMultiArea;
	}

	public void setForceMultiArea(boolean forceMultiArea) {
		this.forceMultiArea = forceMultiArea;
		checkMultiArea();
	}

	public boolean isAtMultiArea() {
		return multiArea;
	}

	public void setAtMultiArea(boolean multiArea) {
		this.multiArea = multiArea;
	}

	public void checkMultiArea() {
		multiArea = forceMultiArea ? true : World.isMultiArea(this);
	}

	public void addFreezeDelay(long time) {
		addFreezeDelay(time, false);
	}

	public void addFreezeDelay(long time, boolean entangleMessage) {
		long currentTime = Utils.currentTimeMillis();
		if (currentTime > this.freezeDelay) {
			if (this instanceof Player) {
				Player p = (Player) this;
				if (!entangleMessage)
					p.getPackets().sendGameMessage("You have been frozen.");
				if (p.getControlerManager().getControler() != null
						&& p.getControlerManager().getControler() instanceof StealingCreationController)
					time /= 3;
			}
			resetWalkSteps();
			freezeDelay = time + currentTime;
		}
	}

	public boolean isFrozen() {
		return freezeDelay >= Utils.currentTimeMillis();
	}

	public long getFreezeDelay() {
		return freezeDelay; // 2500 delay
	}

	public void setFreezeDelay(int time) {
		this.freezeDelay = time;
	}
}