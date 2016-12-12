package net.kagani.game.npc.dungeonnering;

import net.kagani.game.Animation;
import net.kagani.game.Graphics;
import net.kagani.game.WorldTile;
import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;
import net.kagani.game.tasks.WorldTask;
import net.kagani.game.tasks.WorldTasksManager;
import net.kagani.utils.Utils;

@SuppressWarnings("serial")
public class HobgoblinGeomancer extends DungeonBoss {

	public HobgoblinGeomancer(int id, WorldTile tile, DungeonManager manager,
			RoomReference reference) {
		super(id, tile, manager, reference);
	}

	public void sendTeleport(final WorldTile tile, final RoomReference room) {
		setCantInteract(true);
		setNextAnimation(new Animation(12991, 70));
		setNextGraphics(new Graphics(1576, 70, 0));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setCantInteract(false);
				setNextAnimation(new Animation(-1));
				setNextWorldTile(Utils.getFreeTile(getManager()
						.getRoomCenterTile(room), 6));
				resetReceivedHits();
			}
		}, 5);
	}
}
