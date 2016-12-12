package net.kagani.game.player.dialogues.impl.cities.draynor;

import java.io.IOException;

import net.kagani.game.player.dialogues.Dialogue;
//
///**
// * 
// * @author Frostbite
// *<email@frostbitersps@gmail.com><skype:frostbiterps>
// */
//
public class JailGuard extends Dialogue {

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(int interfaceId, int componentId) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
//
//	private int npcId;
//
//	@Override
//	public void start() {
//		stage = (Integer) parameters[0];
//		QuestState state = player.getQuestManager().get(Quests.PRINCE_ALI_RESUCE).getState();
//		if(state == QuestState.STARTED)
//			stage = 1;
//		switch(stage) {
//		case 1:
//			sendPlayerChat(Mood.ASKING, "Hi, who are you guarding here?");
//			stage = 2;
//			break;
//		}
//	}
//
//	@Override
//	public void run(int interfaceId, int componentId) {
//		switch(stage) {
//		case 2:
//			sendNPCChat(Mood.NORMAL, "Can't say, all very secret. You should get out of here. "
//					+ "I am not supposed to talk while I guard.");
//			stage = 3;
//			break;
//
//		case 3:
//			sendOptionsDialogue(DEFAULT, "Hey, chill out, I won't cause you trouble.", "I had better leave, I don't want trouble.");
//			stage = 4;
//			break;
//
//		case 4:
//			switch(componentId){
//			case OPTION_1:
//				sendNPCChat(Mood.ANGRY, "You never relax with these people, but it's a good "
//						+ "career for a young man.");
//				stage = -2;
//				break;
//			case OPTION_2:
//				sendNPCChat(Mood.HAPPY, "Thanks, I appreciate that. Talking on duty can be"
//						+ " punishable by having your mouth stitched up. "
//						+ "These are tough people, no mistake.");
//				stage = -2;
//				break;
//			}
//			break;
//
//		case -2:
//			end();
//			break;
//
//		}
//	}
//
//	@Override
//	public void finish() {
//		// TODO Auto-generated method stub
//
//	}
//
 }
