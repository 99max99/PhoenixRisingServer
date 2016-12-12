package net.kagani.game.player.quest;

import java.util.ArrayList;
import java.util.List;

import net.kagani.game.player.Player;

public class QuestManager {

	private Player player;
	private Quest current;

	private int questPoints;
	private List<Quest> quests = new ArrayList<Quest>(Quests.values().length);

	public QuestManager(Player player) {
		this.setPlayer(player);
		init();
	}

	public void handleButton(int interfaceId, int componentId, int slotId) {
		switch (interfaceId) {
		case 1243:
			switch (componentId) {
			case 46:
				if (getCurrent() != null)
					getCurrent().acceptQuest();
				break;
			case 51:
				if (getCurrent() != null) {
					player.closeInterfaces();
					getCurrent().decline();
				}
				break;
			/*
			 * requirements
			 */
			case 26:
				if (getCurrent() != null)
					getCurrent().showRequirements();
				break;
			/*
			 * rewards
			 */
			case 38:
				if (getCurrent() != null)
					getCurrent().showRewards();
				break;
			}
			break;
		case 190:
			switch (componentId) {
			case 15:
				for (Quest q : quests) {
					if (q.getSlotId() == slotId)
						q.openJournal();
				}
			}
			break;
		}
	}

	public void startDialogue(int id, int i) {
		for (Quest q : quests) {
			if (q.getDialogues().get(id) != null) {
				player.getDialogueManager().startDialogue(
						q.getDialogues().get(id), i);
			}
		}
	}

	public void add(Quests quests, Quest quest) {
		getQuests().add(quests.index(), quest);
	}

	public Quest get(Quests key) {
		return getQuests().get(key.index());
	}

	public QuestManager init() {
		// add(Quests.QUEST_NAME, new Quest(player));
		return this;
	}

	public QuestManager process() {
		return this;
	}

	public List<Quest> getQuests() {
		return quests;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public void update() {
		int totalQuestPoints = 333;
		player.getPackets().sendConfig(904, totalQuestPoints);
		player.getPackets().sendConfig(101,
				player.getQuestManager().getQuestPoints());
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Quest getCurrent() {
		return current;
	}

	public void setCurrent(Quest current) {
		this.current = current;
	}
}