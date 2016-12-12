package net.kagani.game.player.quest;

import java.util.HashMap;
import java.util.Map;

public enum QuestState {

	NOT_STARTED(0), STARTED(1), COMPLETED(2);

	private int state;

	private static Map<Integer, QuestState> map = new HashMap<Integer, QuestState>();

	static {
		for (QuestState stateEnum : QuestState.values()) {
			map.put(stateEnum.state, stateEnum);
		}
	}

	private QuestState(final int value) {
		state = value;
	}

	public static QuestState valueOf(int value) {
		return map.get(value);
	}
}