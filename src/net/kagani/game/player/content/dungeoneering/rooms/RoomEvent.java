package net.kagani.game.player.content.dungeoneering.rooms;

import net.kagani.game.player.content.dungeoneering.DungeonManager;
import net.kagani.game.player.content.dungeoneering.RoomReference;

public interface RoomEvent {

	public void openRoom(DungeonManager dungeon, RoomReference reference);
}
