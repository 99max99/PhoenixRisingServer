package net.kagani.game.player.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.kagani.game.player.Player;

public final class Notes implements Serializable {

	private static final long serialVersionUID = 5564620907978487391L;

	private List<Note> notes;
	private transient Player player;

	public Notes() {
		notes = new ArrayList<Note>(30);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		removeCurrentNote();
		refresh();
		enable();
	}

	public void unlockNotes(boolean menuInterface) {
		player.getPackets().sendIComponentSettings(menuInterface ? 34 : 1417,
				menuInterface ? 16 : 16, 0, 30, 2621470); // notes
															// options
		player.getPackets().sendHideIComponent(menuInterface ? 34 : 1417,
				menuInterface ? 7 : 6, false); // button
		refreshNotesText();
	}

	public void refreshNotesText() {
		for (int i = 0; i < 30; i++)
			player.getPackets().sendCSVarString(2254 + i,
					notes.size() <= i ? "" : notes.get(i).text);
	}

	private void refresh() {
		player.getVarsManager().sendVar(99, getPrimaryColour(this));
		player.getVarsManager().sendVar(100, getSecondaryColour(this));
	}

	private void enable() {
		player.getVarsManager().sendVar(97, 1); // unlocks notes
	}

	public int getCurrentNote() {
		Integer note = (Integer) player.getTemporaryAttributtes().get(
				"CURRENT_NOTE");
		if (note == null)
			return -1;
		return note;
	}

	/*
	 * BitConfig: 6371, from bitshift:0, till bitshift: 7, 98 BitConfig: 6372,
	 * from bitshift:8, till bitshift: 15, 98 BitConfig: 6373, from bitshift:16,
	 * till bitshift: 23, 98 BitConfig: 6374, from bitshift:24, till bitshift:
	 * 31, 98
	 */

	public void setCurrentNote(int id) {
		if (id >= 30)
			return;
		player.getTemporaryAttributtes().put("CURRENT_NOTE", id);
		player.getVarsManager().sendVar(98, id);
	}

	public void removeCurrentNote() {
		player.getTemporaryAttributtes().remove("CURRENT_NOTE");
		player.getVarsManager().sendVar(98, -1);
	}

	public boolean add(String text) {
		if (notes.size() >= 30) {
			player.getPackets().sendGameMessage("You may only have 30 notes!");
			return false;
		}
		if (text.length() > 50) {
			player.getPackets().sendGameMessage(
					"You can only enter notes up to 50 characters!");
			return false;
		}
		player.getPackets().sendCSVarString(2254 + notes.size(), text);
		setCurrentNote(notes.size());
		return notes.add(new Note(text));
	}

	public boolean edit(String text) {
		if (text.length() > 50) {
			player.getPackets().sendGameMessage(
					"You can only enter notes up to 50 characters!");
			return false;
		}
		int id = getCurrentNote();
		if (id == -1 || notes.size() <= id)
			return false;
		notes.get(id).setText(text);
		player.getPackets().sendCSVarString(2254 + id, text);
		return true;
	}

	public boolean colour(int colour) {
		int id = getCurrentNote();
		if (id == -1 || notes.size() <= id)
			return false;
		notes.get(id).setColour(colour);
		if (id < 16)
			player.getVarsManager().sendVar(99, getPrimaryColour(this));
		else
			player.getVarsManager().sendVar(100, getSecondaryColour(this));
		return true;
	}

	public void switchNotes(int from, int to) {
		if (to == 65535) {
			delete(from);
			return;
		}
		if (notes.size() <= from || notes.size() <= to)
			return;
		notes.set(to, notes.set(from, notes.get(to)));
		refresh();
		refreshNotesText();
	}

	public void delete() {
		delete(getCurrentNote());
	}

	public void delete(int id) {
		if (id == -1 || notes.size() <= id)
			return;
		notes.remove(id);
		removeCurrentNote();
		refresh();
		refreshNotesText();
	}

	public void deleteAll() {
		notes.clear();
		removeCurrentNote();
		refresh();
		refreshNotesText();
	}

	/**
	 * Gets the primary colour of the notes.
	 * 
	 * @param notes
	 *            The notes.
	 * @return
	 */
	public static int getPrimaryColour(Notes notes) {
		int color = 0;
		for (int i = 0; i < 16; i++) {
			if (notes.notes.size() <= i)
				break;
			color += colourize(notes.notes.get(i).colour, i);
		}
		return color;
	}

	/**
	 * Gets the secondary colour of the notes.
	 * 
	 * @param notes
	 *            The notes.
	 * @return
	 */
	public static int getSecondaryColour(Notes notes) {
		int color = 0;
		for (int i = 0; i < 14; i++) {
			if (notes.notes.size() - 16 <= i)
				break;
			color += colourize(notes.notes.get(i + 16).colour, i);
		}
		return color;
	}

	public static int colourize(int colour, int noteId) {
		return (int) (Math.pow(4, noteId) * colour);
	}

	public List<Note> getNotes() {
		return notes;
	}

	public static final class Note implements Serializable {

		private static final long serialVersionUID = -3867862135920019512L;

		private String text;
		private int colour;

		public Note(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public int getColour() {
			return colour;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setColour(int colour) {
			this.colour = colour;
		}
	}
}
