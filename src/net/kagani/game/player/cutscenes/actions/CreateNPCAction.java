package net.kagani.game.player.cutscenes.actions;

import net.kagani.game.WorldTile;
import net.kagani.game.npc.others.NPCActor;
import net.kagani.game.player.Player;
import net.kagani.game.player.cutscenes.Cutscene;

public class CreateNPCAction extends CutsceneAction {

	private int id, x, y, plane;
	private String name;
	private int combat;

	public CreateNPCAction(int cachedObjectIndex, int id, int x, int y,
			int plane, int actionDelay) {
		this(cachedObjectIndex, id, x, y, plane, null, -1, actionDelay);
	}

	public CreateNPCAction(int cachedObjectIndex, int id, int x, int y,
			int plane, String name, int combat, int actionDelay) {
		super(cachedObjectIndex, actionDelay);
		this.id = id;
		this.x = x;
		this.y = y;
		this.plane = plane;
		this.name = name;
		this.combat = combat;
	}

	@Override
	public void process(Player player, Object[] cache) {
		Cutscene scene = (Cutscene) cache[0];
		if (cache[getCachedObjectIndex()] != null)
			scene.destroyCache(cache[getCachedObjectIndex()]);
		cache[getCachedObjectIndex()] = new NPCActor(
				id,
				name,
				combat,
				new WorldTile(scene.getBaseX() + x, scene.getBaseY() + y, plane),
				player);
	}

}
