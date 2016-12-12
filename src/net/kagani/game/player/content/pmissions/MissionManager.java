package net.kagani.game.player.content.pmissions;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.player.Player;
import net.kagani.game.player.content.pmissions.impl.LearningRopes;

public class MissionManager {

	private Player player;
	private List<Mission> mission = new ArrayList<Mission>(
			Missions.values().length);

	public MissionManager(Player player) {
		this.setPlayer(player);
		init();
	}

	public void add(Missions missions, Mission mission) {
		getMissions().add(missions.index(), mission);
	}

	public Mission get(Missions key) {
		return getMissions().get(key.index());
	}

	public MissionManager init() {
		add(Missions.LEARNING_THE_ROPES, new LearningRopes(player));
		return this;
	}

	public List<Mission> getMissions() {
		return mission;
	}

	public MissionManager process() {
		return this;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
