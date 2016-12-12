package net.kagani.game.player.dialogues;

import java.io.IOException;
import java.security.InvalidParameterException;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.cache.loaders.NPCDefinitions;
import net.kagani.game.player.Player;
import net.kagani.utils.Utils;

public abstract class Dialogue {

	protected Player player;
	protected int stage = -1;
	protected int npcId;

	public Dialogue() {

	}

	public Object[] parameters;

	public void setPlayer(Player player) {
		this.player = player;
	}

	public abstract void start();

	public abstract void run(int interfaceId, int componentId)
			throws ClassNotFoundException, IOException;

	public abstract void finish();

	protected final void end() {
		player.getDialogueManager().finishDialogue();
	}

	public static final String[] RANDOM_GREETINGS = { "Hello there.", "Howdy.",
			"Good day.", "Salutations!", "Greetings.", "Nice to meet you." };

	public static final String[] RANDOM_RESPONSES = {
			"Good day to you, adventurer.", "Well met, adventurer.",
			"Well, hello there." };

	public static final int NORMAL = 9827, QUESTIONS = 9827, MAD = 9789,
			MOCK = 9878, LAUGHING = 9851, WORRIED = 9775, HAPPY = 9850,
			CONFUSED = 9830, DRUNK = 9835, ANGERY = 9790, SAD = 9775,
			SCARED = 9780;
	protected static final String DEFAULT_OPTIONS_TITLE = "Select an Option";
	protected static final String DEFAULT = "Select an Option";
	protected static final short SEND_1_TEXT_CHAT = 241;
	protected static final short SEND_2_TEXT_CHAT = 242;
	protected static final short SEND_3_TEXT_CHAT = 243;
	protected static final short SEND_4_TEXT_CHAT = 244;
	protected static final short SEND_1_TEXT_INFO = 210;
	protected static final short SEND_2_TEXT_INFO = 211;
	protected static final short SEND_3_TEXT_INFO = 212;
	protected static final short SEND_4_TEXT_INFO = 213;
	protected static final short SEND_NO_CONTINUE_1_TEXT_CHAT = 245;
	protected static final short SEND_NO_CONTINUE_2_TEXT_CHAT = 246;
	protected static final short SEND_NO_CONTINUE_3_TEXT_CHAT = 247;
	protected static final short SEND_NO_CONTINUE_4_TEXT_CHAT = 248;
	protected static final short SEND_2_LARGE_OPTIONS = 229;
	protected static final short SEND_3_LARGE_OPTIONS = 231;
	protected static final short SEND_2_OPTIONS = 236;
	protected static final short SEND_3_OPTIONS = 230;
	protected static final short SEND_4_OPTIONS = 237;
	protected static final short SEND_5_OPTIONS = 238;
	protected static final byte IS_NOTHING = -1, IS_PLAYER = 0;
	public static final byte IS_NPC = 1;
	protected static final byte IS_ITEM = 2;
	public static final int HAPPY_FACE = 9843;
	public static final int ASKING_FACE = 9829;
	public static final int BLANK_FACE = 9772;
	public static final int SAD_FACE = 9768;
	public static final int UPSET_FACE = 9776;
	public static final int SCARED_FACE = 9780;
	public static final int MILDLY_ANGRY_FACE = 9784;
	public static final int ANGRY_FACE = 9788;
	public static final int VERY_ANGRY_FACE = 9792;
	public static final int MANIAC_FACE = 9800;
	public static final int NOT_TALKING_JUST_LISTENING_FACE = 9804;
	public static final int PLAIN_TALKING_FACE = 9808;
	public static final int WTF_FACE = 9820;
	public static final int SHAKING_NO_FACE = 9824;
	public static final int UNSURE_FACE = 9836;
	public static final int LISTENS_THEN_LAUGHS_FACE = 9840;
	public static final int GOOFY_LAUGH_FACE = 9851;
	public static final int THINKING_THEN_TALKING_FACE = 9859;
	public static final int NONONO_FACE = 9844;
	public static final int SAD_WTF_FACE = 9776;
	public static final int ANGRY = 9790, ASKING = 9829, BLANK = 9772,
			UPSET = 9776, MILDLY_ANGRY = 9784, VERY_ANGRY = 9792,
			MANIAC = 9800, NOT_TALKING_JUST_LISTENING = 9804,
			PLAIN_TALKING = 9808, UNSURE = 9836, LISTENS_THEN_LAUGHS = 9840,
			GOOFY_LAUGH = 9851, THINKING_THEN_TALKING = 9859, NONONO = 9844,
			SAD_SHOCKED = 9776;

	public void hideContinueOption(int type) {
		player.getPackets().sendHideIComponent(type == IS_PLAYER ? 1191 : 1184,
				18, true);
	}

	public void sendContinueOption(int type) {
		player.getPackets().sendHideIComponent(type == IS_PLAYER ? 1191 : 1184,
				18, false);
	}

	public boolean sendNPCDialogue(int npcId, int animationId, String... text) {
		return sendEntityDialogue(IS_NPC, npcId, animationId, text);
	}

	public boolean sendNPCChat(int animationId, String... text) {
		return sendEntityDialogue(IS_NPC, npcId, animationId, text);
	}

	public boolean npc(int animationId, String... text) {
		return sendEntityDialogue(IS_NPC, npcId, animationId, text);
	}

	public boolean sendItemDialogue(int itemId, String... text) {
		return sendEntityDialogue(IS_ITEM, itemId, -1, text);
	}

	public boolean sendItemDialogues(int itemId, String title, String... text) {
		return sendEntityDialogue(IS_ITEM, title, itemId, -1, text);
	}

	public boolean sendHandedItem(int itemId, String... text) {
		return sendEntityDialogue(IS_ITEM, itemId, -1, text);
	}

	public boolean sendPlayerDialogue(int animationId, String... text) {
		return sendEntityDialogue(IS_PLAYER, -1, animationId, text);
	}

	public boolean player(int animationId, String... text) {
		return sendEntityDialogue(IS_PLAYER, -1, animationId, text);
	}

	public boolean sendPlayerChat(int animationId, String... text) {
		return sendEntityDialogue(IS_PLAYER, -1, animationId, text);
	}

	/*
	 * 
	 * auto selects title, new dialogues
	 */
	public boolean sendEntityDialogue(int type, int entityId, int animationId,
			String... text) {
		String title = "";
		if (type == IS_PLAYER) {
			title = player.getDisplayName();
		} else if (type == IS_NPC) {
			title = NPCDefinitions.getNPCDefinitions(entityId).getName();
		} else if (type == IS_ITEM)
			title = ItemDefinitions.getItemDefinitions(entityId).getName();
		return sendEntityDialogue(type, title, entityId, animationId, text);
	}

	public static final int OPTION_1 = 3, OPTION_2 = 4, OPTION_3 = 5,
			OPTION_4 = 6, OPTION_5 = 7;

	public boolean sendOptionsDialogue(String title, String... options) {
		if (options.length > 5) {
			throw new InvalidParameterException("The max options length is 5.");
		}
		String[] newopts = new String[5];
		for (int i = 0; i < 5; i++)
			newopts[i] = "";
		int ptr = 0;
		for (String s : options) {
			if (s != null) {
				newopts[ptr++] = s;
			}
		}
		player.getInterfaceManager().sendDialogueInterface(1188);
		player.getPackets().sendIComponentText(1188, 14, title);
		player.getPackets().sendExecuteScriptReverse(5589, newopts[4],
				newopts[3], newopts[2], newopts[1], newopts[0], options.length);
		return true;
	}

	public boolean options(String title, String... options) {
		if (options.length > 5) {
			throw new InvalidParameterException("The max options length is 5.");
		}
		String[] newopts = new String[5];
		for (int i = 0; i < 5; i++)
			newopts[i] = "";
		int ptr = 0;
		for (String s : options) {
			if (s != null) {
				newopts[ptr++] = s;
			}
		}
		player.getInterfaceManager().sendDialogueInterface(1188);
		player.getPackets().sendIComponentText(1188, 14, title);
		player.getPackets().sendExecuteScriptReverse(5589, newopts[4],
				newopts[3], newopts[2], newopts[1], newopts[0], options.length);
		return true;
	}

	public static boolean sendNPCDialogueNoContinue(Player player, int npcId,
			int animationId, String... text) {
		return sendEntityDialogueNoContinue(player, IS_NPC, npcId, animationId,
				text);
	}

	public static boolean sendPlayerDialogueNoContinue(Player player,
			int animationId, String... text) {
		return sendEntityDialogueNoContinue(player, IS_PLAYER, -1, animationId,
				text);
	}

	/*
	 * 
	 * auto selects title, new dialogues
	 */
	public static boolean sendEntityDialogueNoContinue(Player player, int type,
			int entityId, int animationId, String... text) {
		String title = "";
		if (type == IS_PLAYER) {
			title = player.getDisplayName();
		} else if (type == IS_NPC) {
			title = NPCDefinitions.getNPCDefinitions(entityId).getName();
		} else if (type == IS_ITEM)
			title = ItemDefinitions.getItemDefinitions(entityId).getName();
		return sendEntityDialogueNoContinue(player, type, title, entityId,
				animationId, text);
	}

	public static boolean sendEntityDialogueNoContinue(Player player, int type,
			String title, int entityId, int animationId, String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append(" " + texts[line]);
		String text = builder.toString();

		player.getInterfaceManager().sendDialogueInterface(1184);
		player.getPackets().sendHideIComponent(1184, 11, true);
		player.getPackets().sendIComponentText(1184, 10, title);
		player.getPackets().sendIComponentText(1184, 9, text);
		player.getPackets().sendEntityOnIComponent(type == IS_PLAYER, entityId,
				1184, 2); // there
		// is
		// a
		// config
		// for
		// this
		if (animationId != -1)
			player.getPackets().sendIComponentAnimation(animationId, 1184, 2);

		/*
		 * player.getInterfaceManager().sendDialogueInterface(1192);
		 * player.getPackets().sendIComponentText(1192, 4, title);
		 * player.getPackets().sendIComponentText(1192, 3, text);
		 * player.getPackets().sendEntityOnIComponent(type == IS_PLAYER,
		 * entityId, 1192, 11); if (animationId != -1)
		 * player.getPackets().sendIComponentAnimation(animationId, 1192, 11);
		 */
		return true;
	}

	public static boolean sendEmptyDialogue(Player player) {
		player.getInterfaceManager().sendDialogueInterface(89);
		return true;
	}

	public static void closeNoContinueDialogue(Player player) {
		player.getInterfaceManager().removeDialogueInterface();
	}

	public boolean sendEntityDialogues(short interId, String[] talkDefinitons,
			byte type, int entityId, int animationId) {
		if (type == IS_PLAYER || type == IS_NPC) { // auto convert to new
													// dialogue all old
													// dialogues
			String[] texts = new String[talkDefinitons.length - 1];
			for (int i = 0; i < texts.length; i++)
				texts[i] = talkDefinitons[i + 1];
			sendEntityDialogue(type, talkDefinitons[0], entityId, animationId,
					texts);
			return true;
		}
		int[] componentOptions = getIComponentsIds(interId);
		if (componentOptions == null)
			return false;
		player.getInterfaceManager().sendDialogueInterface(interId);
		if (talkDefinitons.length != componentOptions.length)
			return false;
		for (int childOptionId = 0; childOptionId < componentOptions.length; childOptionId++)
			player.getPackets().sendIComponentText(interId,
					componentOptions[childOptionId],
					talkDefinitons[childOptionId]);
		if (type == IS_PLAYER || type == IS_NPC) {
			player.getPackets().sendEntityOnIComponent(type == IS_PLAYER,
					entityId, interId, 2);
			if (animationId != -1)
				player.getPackets().sendIComponentAnimation(animationId,
						interId, 2);
		} else if (type == IS_ITEM)
			player.getPackets().sendItemOnIComponent(interId, 2, entityId,
					animationId);
		return true;
	}

	public boolean sendEntityDialogue(int type, String title, int entityId,
			int animationId, String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append(" " + texts[line]);
		String text = builder.toString();
		if (type == IS_NPC) {
			player.getInterfaceManager().sendDialogueInterface(1184);
			player.getPackets().sendHideIComponent(1184, 11, false);
			player.getPackets().sendIComponentText(1184, 10, title);
			player.getPackets().sendIComponentText(1184, 9, text);
			player.getPackets().sendNPCHeadOnIComponent(1184, 2, entityId);
			if (animationId != -1)
				player.getPackets().sendIComponentAnimation(animationId, 1184,
						2);
		} else if (type == IS_PLAYER) {
			player.getInterfaceManager().sendDialogueInterface(1191);
			player.getPackets().sendIComponentText(1191, 2, title);
			player.getPackets().sendIComponentText(1191, 6, text);
			player.getPackets().sendPlayerHeadOnIComponent(1191, 13);
			if (animationId != -1)
				player.getPackets().sendIComponentAnimation(animationId, 1191,
						13);
		} else if (type == IS_ITEM) {
			player.getInterfaceManager().sendDialogueInterface(1184);
			/*
			 * for (int i = 0; i < 3; i++)
			 * player.getPackets().sendHideIComponent(1184, 14 + i, true);
			 */
			player.getPackets().sendIComponentText(1184, 10, title);
			player.getPackets().sendIComponentText(1184, 9, text);
			player.getPackets().sendItemOnIComponent(1184, 2, entityId, 1); // there
			// is
			// a
			// config
			// for
			// this
			if (animationId != -1)
				player.getPackets().sendIComponentAnimation(animationId, 1184,
						2);
		}
		return true;
	}

	public boolean sendDialogue(String... texts) {
		StringBuilder builder = new StringBuilder();
		for (int line = 0; line < texts.length; line++)
			builder.append((line == 0 ? "<p=1>" : "<br>") + texts[line]);
		String text = builder.toString();
		player.getInterfaceManager().sendDialogueInterface(1186);
		player.getPackets().sendIComponentText(1186, 2, text);
		return true;
	}

	public boolean sendEntityDialogue(short interId, String[] talkDefinitons,
			byte type, int entityId, int animationId) {
		if (type == IS_PLAYER || type == IS_NPC) { // auto convert to new
			// dialogue all old dialogues
			String[] texts = new String[talkDefinitons.length - 1];
			for (int i = 0; i < texts.length; i++)
				texts[i] = talkDefinitons[i + 1];
			sendEntityDialogue(type, talkDefinitons[0], entityId, animationId,
					texts);
			return true;
		}
		return true;
	}

	public int getOrdinal(int componentId) {
		return componentId == OPTION_1 ? 0 : componentId - 12;
	}

	public String generateRandomGreetings() {
		return RANDOM_GREETINGS[Utils.random(4)];
	}

	public String generateRandomResponses() {
		return RANDOM_RESPONSES[Utils.random(2)];
	}

	private static int[] getIComponentsIds(short interId) {
		int[] childOptions;
		switch (interId) {
		case SEND_1_TEXT_INFO:
			childOptions = new int[1];
			childOptions[0] = 1;
			break;
		case SEND_2_TEXT_INFO:
			childOptions = new int[2];
			childOptions[0] = 1;
			childOptions[1] = 2;
			break;
		case SEND_3_TEXT_INFO:
			childOptions = new int[3];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			break;
		case SEND_4_TEXT_INFO:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_2_LARGE_OPTIONS:
			childOptions = new int[3];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			break;
		case SEND_3_LARGE_OPTIONS:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_2_OPTIONS:
			childOptions = new int[3];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			break;
		case SEND_3_OPTIONS:
			childOptions = new int[4];
			childOptions[0] = 1;
			childOptions[1] = 2;
			childOptions[2] = 3;
			childOptions[3] = 4;
			break;
		case SEND_4_OPTIONS:
			childOptions = new int[5];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			childOptions[3] = 3;
			childOptions[4] = 4;
			break;
		case SEND_5_OPTIONS:
			childOptions = new int[6];
			childOptions[0] = 0;
			childOptions[1] = 1;
			childOptions[2] = 2;
			childOptions[3] = 3;
			childOptions[4] = 4;
			childOptions[5] = 5;
			break;
		case SEND_1_TEXT_CHAT:
		case SEND_NO_CONTINUE_1_TEXT_CHAT:
			childOptions = new int[2];
			childOptions[0] = 3;
			childOptions[1] = 4;
			break;
		case SEND_2_TEXT_CHAT:
		case SEND_NO_CONTINUE_2_TEXT_CHAT:
			childOptions = new int[3];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			break;
		case SEND_3_TEXT_CHAT:
		case SEND_NO_CONTINUE_3_TEXT_CHAT:
			childOptions = new int[4];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			break;
		case SEND_4_TEXT_CHAT:
		case SEND_NO_CONTINUE_4_TEXT_CHAT:
			childOptions = new int[5];
			childOptions[0] = 3;
			childOptions[1] = 4;
			childOptions[2] = 5;
			childOptions[3] = 6;
			childOptions[4] = 7;
			break;
		default:
			return null;
		}
		return childOptions;
	}
}