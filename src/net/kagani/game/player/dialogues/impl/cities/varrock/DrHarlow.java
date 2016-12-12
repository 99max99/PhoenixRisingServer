package net.kagani.game.player.dialogues.impl.cities.varrock;

import java.io.IOException;

import net.kagani.game.World;
import net.kagani.game.item.Item;
import net.kagani.game.player.QuestManager.Quests;
import net.kagani.game.player.dialogues.Dialogue;

import sun.reflect.annotation.TypeAnnotation.LocationInfo.Location;

public class DrHarlow extends Dialogue {

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

//    /**
//     * Upstairs morgnas house, - Find garlic
//     * If getInv.hasitem garlic - "The cupboard conatins garlic. You take a clove.";
//     * else
//     * "You take a clove of garlic.";
//     */
//	
//	@Override
//	public void start() {
//		npcId = (Integer) parameters[0];
//		sendNPCDialogue(npcId, DRUNK, "Buy me a drrink pleassh.");
//	}
//
//	@Override
//	public void run(int interfaceId, int componentId) {
//		int progress = player.getQuestManager().get(Quests.VAMPIRE_SLAYER).getStage();
//		switch (stage) {
//		case -1:
//            if(progress > 1) {
//                if (player.getInventory().containsItem(VampireSlayer.BEER, 1)) {
//                    sendPlayerChat(Mood.NORMAL, "Here you go.");
//                    stage = 8;
//                } else {
//                    sendPlayerChat(Mood.NORMAL, "I'll just go and buy one.");
//                    stage = -2;
//                }
//            }
//            if(progress > 0) {
//                sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
//                        "No, you've had enough.",
//                        "Are you Dr Harlow, the famous vampyre slayer?",
//                        "You couldn't possibly be Dr Harlow, you're just a drunk.");
//                stage = 0;
//            } else {
//                sendOptionsDialogue(DEFAULT, "No, you've had enough.", "Ok mate.");
//                stage = 0;
//            }
//			break;
//		case 0:
//            if(progress > 0) {
//                switch (componentId) {
//                    case OPTION_1:
//                        sendPlayerDialogue(UPSET, "No, you've had enough.");
//                        stage = 1;
//                        break;
//                    case OPTION_2:
//                        sendPlayerDialogue(NORMAL,
//                                "Are you the Dr Harlow, the famous vampyre slayer?");
//                        stage = 2;
//                        break;
//                    case OPTION_3:
//                        sendPlayerDialogue(NORMAL,
//                                "You couldn't possibly be Dr Harlow, you're just a drunk.");
//                        stage = -2;
//                        break;
//                }
//            } else {
//                switch (componentId) {
//                    case OPTION_1:
//                        sendPlayerDialogue(UPSET, "No, you've had enough.");
//                        stage = 1;
//                        break;
//                    case OPTION_2:
//                        if (player.getInventory().containsItem(VampireSlayer.BEER, 1)) {
//                            sendPlayerChat(Mood.NORMAL, "Here you go.");
//                            stage = 8;
//                        } else {
//                            sendPlayerChat(Mood.NORMAL, "I'll just go and buy one.");
//                            stage = -2;
//                        }
//                        break;
//                }
//            }
//			break;
//		case 1:
//			sendNPCDialogue(npcId, DRUNK, "Pssssh, I never have enough!");
//			stage = -2;
//			break;
//		case 2:
//			sendNPCDialogue(npcId, UNSURE, "Dependsh whose ashking.");
//			stage = 3;
//			break;
//		case 3:
//			sendPlayerDialogue(UPSET,
//					"Your friend Morgan sent me. He said you could teach me",
//					"how to slay a vampyre.");
//			stage = 4;
//			break;
//		case 4:
//			sendNPCDialogue(npcId, DRUNK,
//					"Shure, I can teash you. I wash the best vampyre shhlayer",
//					"ever.");
//			stage = 5;
//			break;
//		case 5:
//			sendNPCDialogue(npcId, HAPPY, "Buy me a beer and I'll teash you.");
//			stage = 6;
//			break;
//		case 6:
//			sendPlayerDialogue(
//					ANGRY,
//					"Your good friend Morgan is living in fear of a vampyre and",
//					"all you can think about is beer?");
//			stage = 7;
//			break;
//		case 7:
//			if(player.getInventory().containsItem(VampireSlayer.BEER, 1)) {
//				sendItemDialogue(VampireSlayer.BEER, "You give a beer to Dr. Harlow.");
//                stage = 9;
//			} else {
//			sendNPCDialogue(npcId, DRUNK, "Buy ush a drink anyway.");
//			stage = -2;
//			}
//			break;
//
//
//            case 8:
//                sendItemDialogue(VampireSlayer.BEER, "You give a beer to Dr. Harlow.");
//                stage = 9;
//                break;
//
//            case 9:
//                sendNPCDialogue(npcId, DRUNK, "Cheersh matey...");
//                if(progress == 0 || progress == 3 || player.hasItem(VampireSlayer.STAKE)) {
//                    stage = -2;
//                } else {
//                    stage = 10;
//                }
//                break;
//
//            case 10:
//                sendPlayerChat(Mood.ASKING, "So tell me how to kill vampires then.");
//                stage = 11;
//                break;
//
//            case 11:
//            	sendNPCDialogue(npcId, DRUNK, "Yesh Yesh vampires, I was very good at " +
//                        "killing em once...");
//                stage = 12;
//                break;
//
//            case 12:
//                sendDialogue("Dr Harlow appears to sober up slightly.");
//                stage = 13;
//                break;
//
//            case 13:
//            	sendNPCDialogue(npcId, NORMAL, "Well you're gonna need a stake, otherwise he'll just " +
//                        "regenerate. Yes, you must have a stake to finish it of... " +
//                        "I just happen to have one with me.");
//                stage = 14;
//                break;
//
//            case 14:
//                sendItemDialogue(VampireSlayer.STAKE, "Dr harlow hands you a stake.");
//                if(player.getInventory().getFreeSlots() > 0) {
//                    player.getInventory().addItem(VampireSlayer.STAKE, 1);
//                } else {
//                    World.addGroundItem(new Item(VampireSlayer.STAKE), new Location(player.getX(), player.getY(), player.getPlane()));
//                }
//                stage = 15;
//                break;
//
//            case 15:
//            	sendNPCDialogue(npcId, NORMAL, "You'll need a hammer as well, to drive it in properly, " +
//                        "your everyday general store hammer will do. One last " +
//                        "thing... It's wise to carry garlic with you. vampires are " +
//                        "somewhat weakened if they can smell garlic. Morgan ");
//                stage = 16;
//                break;
//
//            case 16:
//            	sendNPCDialogue(npcId, NORMAL, "always liked garlic, you should try his house. But " +
//                         "remember, a vampire is still a dangerous foe!");
//                player.getQuestManager().get(Quests.VAMPIRE_SLAYER).setStage(3);
//                stage = 17;
//                break;
//
//            case 17:
//                sendPlayerChat(Mood.HAPPY, "Thank you very much!");
//                stage = -2;
//                break;
//
//
//		case -2:
//			end();
//			break;
//		}
//	}
//
//	@Override
//	public void finish() {
//		// TODO Auto-generated method stub
//
//	}

 }
