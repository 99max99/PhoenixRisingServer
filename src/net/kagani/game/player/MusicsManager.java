package net.kagani.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.kagani.Settings;
import net.kagani.cache.loaders.ClientScriptMap;
import net.kagani.game.World;
import net.kagani.utils.MusicEffects;
import net.kagani.utils.MusicHints;
import net.kagani.utils.Utils;

public final class MusicsManager implements Serializable {

	private static final long serialVersionUID = 1020415702861567375L;

	private static final int[] CONFIG_IDS = new int[] { 37, 38, 39, 40, 41, 42,
			43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
			60, 61, 62, 63, -1, 64, 65, 66, 67, 68, 69, 70, 71, 3551, 3691,
			4359 };

	public static final int DEATH_MUSIC_EFFECT = 148;

	private static final int[] PLAY_LIST_CONFIG_IDS = new int[] { 76, 77, 78,
			79, 80, 81 };

	private transient Player player;
	private transient int playingMusic;
	private transient long playingMusicDelay;
	private transient boolean settedMusic;
	private ArrayList<Integer> unlockedMusics;
	private ArrayList<Integer> playList;
	private boolean shuffleOn;
	private boolean globalMute;

	private transient boolean playListOn;
	private transient int nextPlayListMusic;

	public MusicsManager() {
		unlockedMusics = new ArrayList<Integer>();
		playList = new ArrayList<Integer>(12);
		unlockMusics();
	}

	private static final int[] AUTO_UNLOCKED_MUSICS = { 200, 517, 518, 519,
			323, 1176, 931, 316, 336, 151, 411, 350, 360, 89, 321, 412, 1177,
			377, 150, 1179, 103, 153, 152, 602, 717, 482, 650, 520, 611, 318,
			196, 514 };

	private void unlockMusics() {
		for (int id : AUTO_UNLOCKED_MUSICS)
			unlockedMusics.add(id);
		int[] startZoneMusics = World.getRegion(
				Settings.HOME_LOCATION.getRegionId()).getMusicIds();
		if (startZoneMusics != null)
			for (int musicId : startZoneMusics)
				if (musicId >= 0)
					unlockedMusics.add(musicId);
	}

	public boolean hasMusic(int id) {
		return unlockedMusics.contains(id);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void switchShuffleOn() {
		if (shuffleOn) {
			playListOn = false;
			refreshPlayListConfigs();
		}
		shuffleOn = !shuffleOn;
		refreshShuffle();
		player.getPackets().sendGameMessage(
				"Music Mode: "
						+ (shuffleOn ? "<col=FF9900>Shuffle"
								: "<col=00FF00>Default"));
	}

	public void refreshShuffle() {
		player.getPackets().sendCSVarInteger(2746, shuffleOn ? 1 : 0);
	}

	public void skipMusic() {
		if (!shuffleOn)
			return;
		if (Utils.currentTimeMillis() - playingMusicDelay < 3000) {
			player.getPackets().sendGameMessage(
					"You have to wait a little while to do that again.");
			return;
		}
		replayMusic();
	}

	public void switchPlayListOn() {
		if (playListOn) {
			playListOn = false;
			shuffleOn = false;
			refreshPlayListConfigs();
		} else {
			if (playList.isEmpty()) {
				player.getPackets().sendGameMessage(
						"There are no songs in your playlist.");
				return;
			}
			playListOn = true;
			nextPlayListMusic = 0;
			replayMusic();
		}
		player.getPackets().sendGameMessage(
				"Music Mode: "
						+ (playListOn ? "<col=0099FF>Playlist"
								: "<col=00FF00>Default"));
	}

	public void clearPlayList() {
		if (playList.isEmpty())
			return;
		playList.clear();
		refreshPlayListConfigs();
	}

	public void addPlayingMusicToPlayList() {
		addToPlayList(playingMusic);
	}

	public int getArchiveId(int musicId) {
		return ClientScriptMap.getMap(1351).getIntValue(musicId);
	}

	public int getMusicId(int archiveId) {
		return (int) ClientScriptMap.getMap(1351).getKeyForValue(archiveId);
	}

	public void addToPlayList(int musicId) {
		if (playList.size() == PLAY_LIST_CONFIG_IDS.length * 2)
			return;
		if (musicId != -1 && unlockedMusics.contains(musicId)
				&& !playList.contains(musicId)) {
			playList.add(musicId);
			if (playListOn)
				switchPlayListOn();
			else
				refreshPlayListConfigs();
		}
	}

	public void removeFromPlayListByIndex(int listIndex) {
		if (listIndex >= PLAY_LIST_CONFIG_IDS.length * 2)
			listIndex -= PLAY_LIST_CONFIG_IDS.length * 2;
		if (listIndex >= playList.size())
			return;
		playList.remove(listIndex);
		if (playListOn)
			switchPlayListOn();
		else
			refreshPlayListConfigs();
	}

	public void removeFromPlayList(int musicId) {
		if (musicId != -1 && unlockedMusics.contains(musicId)
				&& playList.contains(musicId)) {
			playList.remove((Integer) musicId);
			if (playListOn)
				switchPlayListOn();
			else
				refreshPlayListConfigs();
		}
	}

	public void refreshPlayListConfigs() {
		int[] configValues = new int[PLAY_LIST_CONFIG_IDS.length];
		for (int i = 0; i < configValues.length; i++)
			configValues[i] = -1;
		for (int i = 0; i < playList.size(); i += 2) {
			Integer musicId1 = playList.get(i);
			Integer musicId2 = (i + 1) >= playList.size() ? null : playList
					.get(i + 1);
			if (musicId1 == null && musicId2 == null)
				break;
			int configValue = musicId1
					| (musicId2 == null ? -1 : musicId2) << 15;
			configValues[i / 2] = configValue;
		}
		for (int i = 0; i < PLAY_LIST_CONFIG_IDS.length; i++)
			player.getVarsManager().sendVar(PLAY_LIST_CONFIG_IDS[i],
					configValues[i]);
	}

	public void refreshListConfigs() {
		int[] configValues = new int[CONFIG_IDS.length];
		for (int musicId : unlockedMusics) {
			int index = getConfigIndex(musicId);
			if (index >= CONFIG_IDS.length)
				continue;
			configValues[index] |= 1 << (musicId - (index * 32));
		}
		for (int i = 0; i < CONFIG_IDS.length; i++) {
			if (CONFIG_IDS[i] != -1)
				player.getVarsManager().sendVar(CONFIG_IDS[i], configValues[i]);
		}
	}

	public void addMusic(int musicId) {
		unlockedMusics.add(musicId);
		refreshListConfigs();
		if (unlockedMusics.size() >= Settings.AIR_GUITAR_MUSICS_COUNT)
			player.getEmotesManager().unlockEmote(41);
	}

	public int getConfigIndex(int musicId) {
		return musicId / 32;
	}

	public void unlockMusicPlayer(boolean menu) {
		player.getPackets().sendUnlockIComponentOptionSlots(menu ? 187 : 1416,
				menu ? 1 : 3, 0, CONFIG_IDS.length * 64, 0, 1, 2, 3);
		player.getPackets().sendUnlockIComponentOptionSlots(menu ? 187 : 1416,
				menu ? 9 : 11, 0, PLAY_LIST_CONFIG_IDS.length * 4, 0, 1, 2, 3);
	}

	public void init() {
		// unlock music inter all options
		sendAudioSettings();
		refreshListConfigs();
		refreshPlayListConfigs();
		refreshShuffle();
		player.getMusicsManager().checkMusic(
				World.getRegion(player.getRegionId()).getRandomMusicId());
	}

	public void sendAudioSettings() {
		player.getPackets().sendExecuteScript(8045, globalMute ? 1 : 0); // turns
																			// music
																			// on
																			// ^^
	}

	public void switchGlobalMute() {
		globalMute = !globalMute;
	}

	private static final long MUSIC_DELAY = 180000;

	public boolean musicEnded() {
		return playingMusic != -2
				&& playingMusicDelay + MUSIC_DELAY < Utils.currentTimeMillis();
	}

	public void replayMusic() {
		if (playListOn && playList.size() > 0) {
			if (shuffleOn)
				playingMusic = playList.get(Utils.random(playList.size()));
			else {
				if (nextPlayListMusic >= playList.size())
					nextPlayListMusic = 0;
				playingMusic = playList.get(nextPlayListMusic++);
			}
		} else if (unlockedMusics.size() > 0) {// random music
			if (shuffleOn)
				playingMusic = unlockedMusics.get(Utils.random(unlockedMusics
						.size()));
			else
				playingMusic = getGenreMusic();
		}
		playMusic(playingMusic);
	}

	private static String[] getHint(int id) {
		String hint = MusicHints.getHint(id).replace(".", "");
		String[] words = hint.split(" ");
		if (words.length == 0)
			return null;
		String s = "";
		;
		for (String w : words) {
			if (Character.isUpperCase(w.charAt(0)))
				s += w + " ";
		}
		return s.split(" ");
	}

	/*
	 * might be too slow. if so improve
	 */
	public int getGenreMusic() {
		if (playingMusic > -1) {
			// int currentMusic = playingMusic;
			String[] hints = getHint(playingMusic);
			if (hints.length > 0) {
				List<Integer> combs = new ArrayList<Integer>();
				for (int id : unlockedMusics) {
					String[] hint = getHint(id);
					if (hint.length == 0)
						continue;
					l: for (String h : hint) {
						for (String h2 : hints) {
							if (h2.equals(h)) {
								combs.add(id);
								break l;
							}
						}
					}
				}
				if (combs.size() > 0)
					return combs.get(Utils.random(combs.size()));
			}
		}
		return unlockedMusics.get(Utils.random(unlockedMusics.size()));

	}

	public void checkMusic(int requestMusicId) {
		if (playListOn || settedMusic
				&& playingMusicDelay + MUSIC_DELAY >= Utils.currentTimeMillis())
			return;
		settedMusic = false;
		if (playingMusic != requestMusicId)
			playMusic(requestMusicId);
	}

	public void forcePlayMusic(int musicIndex) {
		settedMusic = true;
		playMusic(musicIndex);
	}

	public void reset() {
		settedMusic = false;
		player.getMusicsManager().checkMusic(
				World.getRegion(player.getRegionId()).getRandomMusicId());
	}

	public void sendHint(int musicId) {
		if (musicId != -1) {
			player.getPackets()
					.sendGameMessage(
							"This track "
									+ (unlockedMusics.contains(musicId) ? "was unlocked"
											: "unlocks") + " "
									+ MusicHints.getHint(musicId));
		}
	}

	public void playAnotherMusicFromPlayListByIndex(int listIndex) {
		if (listIndex >= PLAY_LIST_CONFIG_IDS.length * 2)
			listIndex -= PLAY_LIST_CONFIG_IDS.length * 2;
		if (listIndex >= playList.size())
			return;
		playAnotherMusic(playList.get(listIndex));
	}

	public void playAnotherMusic(int musicId) {
		if (musicId != -1 && unlockedMusics.contains(musicId)) {
			settedMusic = true;
			if (playListOn)
				switchPlayListOn();
			playMusic(musicId);
		}
	}

	public void refreshMusicInterface(boolean settings) {
		player.getPackets().sendIComponentText(settings ? 187 : 1416,
				settings ? 4 : 6, getMusicName());
	}

	public String getMusicName() {
		if (playingMusic == -2)
			return "";
		return ClientScriptMap.getMap(1345).getStringValue(playingMusic);
	}

	public void playMusicByArchive(int archiveId) {
		int musicId = getMusicId(archiveId);
		if (playingMusic != musicId)
			return;
		playMusic(musicId);
	}

	public void resetMusicDelay(int musicId) {
		if (musicId != playingMusic)
			return;
		playingMusicDelay = Utils.currentTimeMillis();
		player.getPackets().sendMusic(getArchiveId(musicId), 255);
	}

	public void playMusicEffect(int effectId) {
		player.getPackets().sendMusicEffect(
				MusicEffects.getArchiveId(effectId), 255);
	}

	private void refreshMusicInterface() {
		refreshMusicInterface(false);
		if (player.getInterfaceManager().containsInterface(187))
			refreshMusicInterface(true);
	}

	/*
	 * music should never be -1
	 */
	public void playMusic(int musicId) {
		if (!player.hasStarted() || musicId == -1)
			return;

		playingMusicDelay = Utils.currentTimeMillis();
		if (musicId == -2) {
			playingMusic = musicId;
			player.getPackets().sendMusic(-1, 255);
			refreshMusicInterface();
			return;
		}
		playingMusic = musicId;
		player.getPackets().sendMusic(getArchiveId(musicId), 255);
		refreshMusicInterface();
		String musicName = getMusicName();
		if (!musicName.replace(" ", "").equals("")
				&& !unlockedMusics.contains(musicId)) {
			addMusic(musicId);
			player.getPackets().sendGameMessage(
					"<col=ff0000>You have unlocked a new music track: "
							+ musicName + ".");
		}
	}

	public void searchMusic() {
		player.getInterfaceManager().sendInputTextInterface();
		player.getPackets().sendCSVarInteger(3497, 1);
	}

}
