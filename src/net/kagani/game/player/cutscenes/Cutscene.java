package net.kagani.game.player.cutscenes;

import net.kagani.Settings;
import net.kagani.executor.GameExecutorManager;
import net.kagani.game.WorldTile;
import net.kagani.game.map.MapBuilder;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.InterfaceManager;
import net.kagani.game.player.Player;
import net.kagani.game.player.cutscenes.actions.CutsceneAction;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Logger;

public abstract class Cutscene {

	public abstract boolean hiddenMinimap();

	public boolean showYourselfToOthers() {
		return true;
	}

	public int getMapSize() {
		return 0;
	}

	public boolean allowSkipCutscene() {
		return true;
	}

	public abstract CutsceneAction[] getActions(Player player);

	private int stage;
	private Object[] cache;
	private CutsceneAction[] actions;
	private int delay;
	private boolean constructingRegion;
	private int[] currentMapData;
	private int mapSize;

	public Cutscene() {
		mapSize = -1;
	}

	private WorldTile endTile;

	public void stopCutscene(Player player) {
		if (player.getX() != endTile.getX() || player.getY() != endTile.getY()
				|| player.getPlane() != endTile.getPlane())
			player.setNextWorldTile(endTile);
		if (hiddenMinimap())
			player.getPackets().sendBlackOut(0); // unblack
		player.closeInterfaces();
		setCutscene(player, false, false); // sets cutscene(hides inters) and
											// doesnt let open menus
		player.getPackets().sendResetCamera();
		player.setLargeSceneView(false);
		player.unlock();
		deleteCache();
		if (currentMapData != null) {
			GameExecutorManager.slowExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						if (currentMapData != null)
							MapBuilder.destroyMap(currentMapData[0],
									currentMapData[1], currentMapData[1],
									currentMapData[2]);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			});
		}
		if (mapSize != -1 && mapSize != player.getMapSize()) {
			player.setForceNextMapLoadRefresh(true);
			player.setMapSize(mapSize);
			mapSize = -1;
		}
	}

	public void startCutscene(Player player) {
		if (hiddenMinimap())
			player.getPackets().sendBlackOut(2); // minimap
		if (getMapSize() != player.getMapSize()) {
			mapSize = player.getMapSize();
			player.setForceNextMapLoadRefresh(true);
			player.setMapSize(getMapSize());
		}
		// Dialogue.sendEmptyDialogue(player);
		setCutscene(player, true, allowSkipCutscene()); // sets cutscene(hides
														// inters) and doesnt
														// let open menus
		player.setLargeSceneView(true);
		player.lock();
		player.stopAll(true, false);
	}

	public static void setCutscene(Player player, boolean open,
			boolean allowSkip) {
		player.getVarsManager().sendVarBit(3028, open ? 1 : 0); // sets
																// cutscene(hides
																// inters) and
																// doesnt let
																// open menus

		player.getPackets().sendHideIComponent(1477, 21, !open || !allowSkip);
		player.getPackets().sendHideIComponent(1477, 22, !open || !allowSkip);
		player.getPackets().sendHideIComponent(1477, 23, !open || !allowSkip);

	}

	public void constructArea(final Player player, final int baseChunkX,
			final int baseChunkY, final int widthChunks, final int heightChunks) {
		constructingRegion = true;
		player.getPackets().sendRootInterface(56, 0);
		GameExecutorManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					final int[] oldData = currentMapData;
					int[] mapBaseChunks = MapBuilder.findEmptyChunkBound(
							widthChunks, heightChunks);
					MapBuilder.copyAllPlanesMap(baseChunkX, baseChunkY,
							mapBaseChunks[0], mapBaseChunks[1], widthChunks,
							heightChunks);
					currentMapData = new int[] { mapBaseChunks[0],
							mapBaseChunks[1], widthChunks, heightChunks };
					player.setNextWorldTile(new WorldTile(getBaseX()
							+ widthChunks * 4, +getBaseY() + heightChunks * 4,
							0));
					constructingRegion = false;
					if (Settings.DEBUG)
						Logger.log(this, "Bases: " + getBaseX() + ", "
								+ getBaseY());
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {

							GameExecutorManager.slowExecutor
									.execute(new Runnable() {
										@Override
										public void run() {
											try {
												player.getPackets()
														.sendRootInterface(
																player.getInterfaceManager()
																		.hasRezizableScreen() ? InterfaceManager.RESIZABLE_WINDOW_ID
																		: InterfaceManager.FIXED_WINDOW_ID,
																0);
												if (oldData != null)
													MapBuilder.destroyMap(
															oldData[0],
															oldData[1],
															oldData[1],
															oldData[2]);
											} catch (Throwable e) {
												Logger.handle(e);
											}
										}
									});
						}

					}, 1);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		});
	}

	public int getLocalX(Player player, int x) {
		return getX(player, (currentMapData == null ? 0 : getBaseX()) + x);
	}

	public int getLocalY(Player player, int y) {
		return getY(player, (currentMapData == null ? 0 : getBaseY()) + y);
	}

	public int getBaseX() {
		return currentMapData == null ? 0 : currentMapData[0] << 3;
	}

	public int getBaseY() {
		return currentMapData == null ? 0 : currentMapData[1] << 3;
	}

	public final void logout(Player player) {
		stopCutscene(player);
	}

	public final boolean process(Player player) {
		if (delay > 0) {
			delay--;
			return true;
		}
		while (true) {
			if (constructingRegion
					|| (player.getNextWorldTile() != null && player
							.needMapUpdate(player.getNextWorldTile()))
					|| !player.clientHasLoadedMapRegion()

					|| player.getDialogueManager().getLast() != null) // waiting
																		// dialogue
																		// to
																		// get
																		// over
				return true;
			if (stage == actions.length) {
				stopCutscene(player);
				return false;
			} else if (stage == 0)
				startCutscene(player);
			CutsceneAction action = actions[stage++];
			action.process(player, cache);
			int delay = action.getActionDelay();
			if (delay == -1)
				continue;
			this.delay = delay;
			return true;
		}
	}

	public void deleteCache() {
		for (Object object : cache)
			destroyCache(object);
	}

	public void destroyCache(Object object) {
		if (object instanceof NPC) {
			NPC n = (NPC) object;
			n.finish();
		}
	}

	public final void createCache(Player player) {
		actions = getActions(player);
		endTile = new WorldTile(player);
		int lastIndex = 0;
		for (CutsceneAction action : actions) {
			if (action.getCachedObjectIndex() > lastIndex)
				lastIndex = action.getCachedObjectIndex();
		}
		cache = new Object[lastIndex + 1];
		cache[0] = this;
	}

	public static int getX(Player player, int x) {
		return new WorldTile(x, 0, 0).getXInScene(player);
	}

	public static int getY(Player player, int y) {
		return new WorldTile(0, y, 0).getYInScene(player);
	}

	public final void updateDelay(int delay) {
		this.delay = delay;
	}
}
