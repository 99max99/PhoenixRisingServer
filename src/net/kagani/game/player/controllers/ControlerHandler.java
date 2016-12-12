package net.kagani.game.player.controllers;

import java.util.HashMap;

import net.kagani.game.minigames.BrimhavenAgility;
import net.kagani.game.minigames.PuroPuro;
import net.kagani.game.minigames.WarriorsGuild;
import net.kagani.game.minigames.clanwars.ClanWarRequestController;
import net.kagani.game.minigames.clanwars.FfaZone;
import net.kagani.game.minigames.clanwars.WarControler;
import net.kagani.game.minigames.duel.DuelArena;
import net.kagani.game.minigames.duel.DuelControler;
import net.kagani.game.minigames.riseofthesix.RiseOfTheSix;
import net.kagani.game.minigames.soulwars.SoulWars;
import net.kagani.game.minigames.stealingcreation.StealingCreationController;
import net.kagani.game.minigames.stealingcreation.StealingCreationLobbyController;
import net.kagani.game.player.content.surpriseevents.LastManStandingController;
import net.kagani.game.player.content.surpriseevents.TeamVsTeamController;
import net.kagani.game.player.controllers.SongFromTheDepths;
import net.kagani.game.player.controllers.SongFromTheDepthsSurface;
import net.kagani.game.player.controllers.bossInstance.BossInstanceController;
import net.kagani.game.player.controllers.bossInstance.CorporealBeastInstanceController;
import net.kagani.game.player.controllers.bossInstance.DagannothKingsInstanceController;
import net.kagani.game.player.controllers.bossInstance.EvilChickenInstanceController;
import net.kagani.game.player.controllers.bossInstance.KalphiteKingInstanceController;
import net.kagani.game.player.controllers.bossInstance.KalphiteQueenInstanceController;
import net.kagani.game.player.controllers.bossInstance.VoragoInstanceController;
import net.kagani.game.player.controllers.bossInstance.godwars.GeneralGraadorInstanceController;
import net.kagani.game.player.controllers.castlewars.CastleWarsPlaying;
import net.kagani.game.player.controllers.castlewars.CastleWarsWaiting;
import net.kagani.game.player.controllers.events.DeathEvent;
import net.kagani.game.player.controllers.fightpits.FightPitsArena;
import net.kagani.game.player.controllers.fightpits.FightPitsLobby;
import net.kagani.game.player.controllers.pestcontrol.PestControlGame;
import net.kagani.game.player.controllers.pestcontrol.PestControlLobby;
import net.kagani.game.player.controllers.trollinvasion.TrollInvasion;
import net.kagani.utils.Logger;

public class ControlerHandler {

	private static final HashMap<Object, Class<? extends Controller>> handledControlers = new HashMap<Object, Class<? extends Controller>>();

	@SuppressWarnings("unchecked")
	public static final void init() {
		try {
			Class<Controller> value1 = (Class<Controller>) Class
					.forName(Wilderness.class.getCanonicalName());
			handledControlers.put("Wilderness", value1);
			Class<Controller> value2 = (Class<Controller>) Class
					.forName(Kalaboss.class.getCanonicalName());
			handledControlers.put("Kalaboss", value2);
			Class<Controller> value4 = (Class<Controller>) Class
					.forName(GodWars.class.getCanonicalName());
			handledControlers.put("GodWars", value4);
			Class<Controller> value5 = (Class<Controller>) Class
					.forName(ZGDControler.class.getCanonicalName());
			handledControlers.put("ZGDControler", value5);
			Class<Controller> value9 = (Class<Controller>) Class
					.forName(DuelArena.class.getCanonicalName());
			handledControlers.put("DuelArena", value9);
			Class<Controller> value10 = (Class<Controller>) Class
					.forName(DuelControler.class.getCanonicalName());
			handledControlers.put("DuelControler", value10);
			Class<Controller> value14 = (Class<Controller>) Class
					.forName(DTControler.class.getCanonicalName());
			handledControlers.put("DTControler", value14);
			Class<Controller> value17 = (Class<Controller>) Class
					.forName(CastleWarsPlaying.class.getCanonicalName());
			handledControlers.put("CastleWarsPlaying", value17);
			Class<Controller> value18 = (Class<Controller>) Class
					.forName(CastleWarsWaiting.class.getCanonicalName());
			handledControlers.put("CastleWarsWaiting", value18);
			handledControlers.put("DungeonControler", (Class<Controller>) Class
					.forName(DungeonController.class.getCanonicalName()));
			handledControlers.put("clan_wars_request",
					(Class<Controller>) Class
							.forName(ClanWarRequestController.class
									.getCanonicalName()));
			handledControlers.put("clan_war", (Class<Controller>) Class
					.forName(WarControler.class.getCanonicalName()));
			handledControlers.put("clan_wars_ffa", (Class<Controller>) Class
					.forName(FfaZone.class.getCanonicalName()));
			handledControlers.put("NomadsRequiem", (Class<Controller>) Class
					.forName(NomadsRequiem.class.getCanonicalName()));
			handledControlers.put("BorkController", BorkController.class);
			handledControlers.put("BrimhavenAgility", (Class<Controller>) Class
					.forName(BrimhavenAgility.class.getCanonicalName()));
			handledControlers.put("FightCavesControler",
					(Class<Controller>) Class.forName(FightCaves.class
							.getCanonicalName()));
			handledControlers.put("FightKilnControler",
					(Class<Controller>) Class.forName(FightKiln.class
							.getCanonicalName()));
			handledControlers.put("FightPitsLobby", (Class<Controller>) Class
					.forName(FightPitsLobby.class.getCanonicalName()));
			handledControlers.put("FightPitsArena", (Class<Controller>) Class
					.forName(FightPitsArena.class.getCanonicalName()));
			handledControlers.put("PestControlGame", (Class<Controller>) Class
					.forName(PestControlGame.class.getCanonicalName()));
			handledControlers.put("PestControlLobby", (Class<Controller>) Class
					.forName(PestControlLobby.class.getCanonicalName()));
			handledControlers.put("Barrows", (Class<Controller>) Class
					.forName(Barrows.class.getCanonicalName()));
			handledControlers.put("Falconry", (Class<Controller>) Class
					.forName(Falconry.class.getCanonicalName()));
			handledControlers.put("QueenBlackDragonControler",
					(Class<Controller>) Class
							.forName(QueenBlackDragonController.class
									.getCanonicalName()));
			handledControlers.put("HouseControler", (Class<Controller>) Class
					.forName(HouseControler.class.getCanonicalName()));
			handledControlers.put("RuneSpanControler",
					(Class<Controller>) Class.forName(RunespanControler.class
							.getCanonicalName()));
			handledControlers.put("DeathEvent", (Class<Controller>) Class
					.forName(DeathEvent.class.getCanonicalName()));
			handledControlers.put("PyramidPlunder", (Class<Controller>) Class
					.forName(PyramidPlunder.class.getCanonicalName()));
			handledControlers.put("SorceressGarden", (Class<Controller>) Class
					.forName(SorceressGarden.class.getCanonicalName()));
			handledControlers.put("CrucibleControler",
					(Class<Controller>) Class.forName(CrucibleControler.class
							.getCanonicalName()));
			handledControlers.put("StealingCreationLobby",
					(Class<Controller>) Class
							.forName(StealingCreationLobbyController.class
									.getCanonicalName()));
			handledControlers.put("StealingCreationGame",
					(Class<Controller>) Class
							.forName(StealingCreationController.class
									.getCanonicalName()));
			handledControlers.put("NewPlayerController",
					(Class<Controller>) Class.forName(NewPlayerController.class
							.getCanonicalName()));
			handledControlers.put("PuroPuro", (Class<Controller>) Class
					.forName(PuroPuro.class.getCanonicalName()));
			handledControlers.put("RuneEssenceController",
					(Class<Controller>) Class
							.forName(RuneEssenceController.class
									.getCanonicalName()));
			handledControlers.put("UnderGroundDungeon",
					(Class<Controller>) Class.forName(UnderGroundDungeon.class
							.getCanonicalName()));
			handledControlers.put("TerrorDogsTarnsLairController",
					(Class<Controller>) Class
							.forName(TerrorDogsTarnsLairController.class
									.getCanonicalName()));
			handledControlers.put("WarriorsGuild", WarriorsGuild.class);
			handledControlers.put("JadinkoLair", (Class<Controller>) Class
					.forName(JadinkoLair.class.getCanonicalName()));
			handledControlers.put("KuradalDungeon", (Class<Controller>) Class
					.forName(KuradalDungeon.class.getCanonicalName()));
			handledControlers.put("SawmillController",
					(Class<Controller>) Class.forName(SawmillController.class
							.getCanonicalName()));
			handledControlers.put("NewSpawnPlayerController",
					(Class<Controller>) Class
							.forName(NewSpawnPlayerController.class
									.getCanonicalName()));
			handledControlers.put("LastManStandingController",
					LastManStandingController.class);
			handledControlers.put("TeamVsTeamController",
					TeamVsTeamController.class);
			handledControlers.put("SoulWarsController",
					(Class<Controller>) Class.forName(SoulWars.class
							.getCanonicalName()));
			// handledControlers.put("SoulWarsController", SoulWars.class);
			// boss instance
			handledControlers.put("BossInstanceController",
					BossInstanceController.class);
			handledControlers.put("KalphiteQueenInstanceController",
					KalphiteQueenInstanceController.class);
			handledControlers.put("DagannothKingsInstanceController",
					DagannothKingsInstanceController.class);
			handledControlers.put("CorporealBeastInstanceController",
					CorporealBeastInstanceController.class);
			handledControlers.put("GeneralGraadorInstanceController",
					GeneralGraadorInstanceController.class);
			handledControlers.put("EvilChickenInstanceController",
					EvilChickenInstanceController.class);
			handledControlers.put("KalphiteKingInstanceController",
					KalphiteKingInstanceController.class);
			handledControlers.put("VoragoInstanceController",
					VoragoInstanceController.class);
			handledControlers.put("FistOfGuthixControler",
					FistOfGuthixControler.class);
			handledControlers.put("RiseOfTheSixController",
					RiseOfTheSixController.class);
			handledControlers.put("TrollInvasion", TrollInvasion.class);
			handledControlers.put("RiseOfTheSix", (Class<Controller>) Class
					.forName(RiseOfTheSix.class.getCanonicalName()));
			handledControlers.put("AraxxorControler", (Class<Controller>) Class
					.forName(AraxxorControler.class.getCanonicalName()));
			handledControlers.put("AraxxiControler", (Class<Controller>) Class
					.forName(AraxxiControler.class.getCanonicalName()));
			handledControlers.put("SongFromTheDepths", (Class<Controller>) Class
					.forName(SongFromTheDepths.class.getCanonicalName()));
			handledControlers.put("SongFromTheDepthsSurface", (Class<Controller>) Class
					.forName(SongFromTheDepthsSurface.class.getCanonicalName()));

			// handledControlers.put("SlaughterFieldsControler",
			// (Class<Controller>)
			// Class.forName(SlaughterFieldsControler.class.getCanonicalName()));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static final void reload() {
		handledControlers.clear();
		init();
	}

	public static final Controller getControler(Object key) {
		if (key instanceof Controller)
			return (Controller) key;
		Class<? extends Controller> classC = handledControlers.get(key);
		if (classC == null)
			return null;
		try {
			return classC.newInstance();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}
}