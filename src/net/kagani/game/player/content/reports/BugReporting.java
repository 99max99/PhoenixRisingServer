package net.kagani.game.player.content.reports;

public class BugReporting {

	/**
	 * @author: Dylan Page
	 */

	private static enum Type {

		AUDIO_ATMOSPHERIC(1),

		AUDIO_JUKEBOX(2),

		AUDIO_MUSIC(3),

		AUDIO_VOICE_OVER(4),

		AUDIO_OTHER(5),

		CLANS(6),

		COMBAT_PVE(7),

		COMBAT_PVP(8),

		GRAPHICS_ANIMATION(9),

		GRAPHICS_ATMOSPHERIC_EFFECTS(10),

		GRAPHICS_CHARACTERS(11),

		GRAPHICS_ENVIRONMENT(12),

		GRAPHICS_INTERFACES(13),

		GRAPHICS_OBJECTS(14),

		GRAPHICS_TEXTURES(15),

		MINIGAMES(16),

		QUEST(17),

		TECHNICAL(18),

		RECENT_UPDATES(19),

		SKILL(20),

		TEXT_INGAME(21),

		TEXT_OTHER(23)

		;

		private int id;

		Type(int id) {
			this.id = id;
		}

		public static Type forId(int id) {
			for (Type t : Type.values()) {
				if (t.id == id) {
					return t;
				}
			}
			return null;
		}
	}

	private String name;
	private String description;
	private String reproduceSteps;

	private Type type;

	public BugReporting(String name, int type, String description,
			String reproduceSteps) {
		this.type = Type.forId(type);
		this.name = name;
		this.description = description.trim();
		this.reproduceSteps = reproduceSteps.trim();
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getReproduceSteps() {
		return reproduceSteps;
	}

	public Type getType() {
		return type;
	}
}