package net.kagani.game.player.dialogues.impl.cities.draynor;

import java.io.IOException;

import net.kagani.game.player.dialogues.Dialogue;
//import com.arrow.game.content.dialogues.Mood;
//import com.arrow.game.content.item.Item;
//import com.arrow.game.content.quest.impl.princealirescue.PrinceAliRescue;
//import com.arrow.game.world.World;
//
///**
// * @Author Frostbite
// * @Contact<frostbitersps@gmail.com;skype:frostbitersps>
// */
public class Ned extends Dialogue {

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
//    @Override
//    public void start() {
//        stage = (Integer) parameters[0];
//        switch(stage) {
//            case 0:
//                sendNPCChat(Mood.NORMAL, "Why, hello there, lad. Me friends call me Ned. I was a " +
//                        "man of the sea, but it's past me now. Could I be " +
//                        "making or selling you some rope?");
//                stage = -1;
//                break;
//        }
//    }
//
//    @Override
//    public void run(int interfaceId, int componentId) {
//        switch(stage) {
//            case -1:
//                sendOptionsDialogue(DEFAULT, "Ned, could you make other things from wool?", "Yes, I would like some rope.", "No thanks Ned, I don't need any.");
//                stage = 1;
//                break;
//
//            case 1:
//                switch (componentId) {
//                    case OPTION_1:
//                        sendNPCChat(Mood.ASKING, "I am sure I can. What are you thinking of?");
//                        stage = 2;
//                        break;
//                    case OPTION_2:
//                        sendNPCChat(Mood.NORMAL, "Well, I can sell you some rope for 15 coins. Or I can " +
//                                "be making you some if you gets me 4 balls of wool. I " +
//                                "strands them together I does, makes em strong.");
//                        stage = 15;
//                        break;
//                    case OPTION_3:
//                        break;
//                }
//                break;
//
//            case 2:
//                sendOptionsDialogue(DEFAULT, "Could you knit me a sweater?", "How about some sort of a wig?", "Could you repair the arrow holes in the back of my shirt?");
//                stage = 3;
//                break;
//
//            case 3:
//                switch (componentId) {
//                    case OPTION_1:
//                        sendNPCChat(Mood.ANGRY, "Do I look like a member of a sewing circle? " +
//                                "Be off wi you. I have fought monsters " +
//                                "that woudl turn your hair blue.");
//                        stage = 4;
//                        break;
//                    case OPTION_2:
//                        sendNPCChat(Mood.NORMAL, "Well... That's an interesting thought. Yes, I think I " +
//                                "could do something. Give me 3 balls of wool and I " +
//                                "might be able to do it.");
//                        stage = 9;
//                        break;
//                    case OPTION_3:
//                        sendNPCChat(Mood.NORMAL, "Ah yes, it's a tough world these days. There's a few " +
//                                "brave enough to attack from 10 metres away.");
//                        stage = 5;
//                        break;
//                }
//                break;
//
//            case 4:
//                sendNPCChat(Mood.ANGRY, "I don't need to be laughed at just 'cos I am getting " +
//                        "a bit old.");
//                stage = -2;
//                break;
//
//            case 5:
//                sendDialogue("Ned pulls out a needle and attacks your shirt.");
//                stage = 6;
//                break;
//
//            case 6:
//            sendNPCChat(Mood.HAPPY, "There you go, good as new.");
//                stage = 7;
//            break;
//
//            case 7:
//                sendPlayerChat(Mood.HAPPY, "Thanks Ned. Maybe next time they will " +
//                        "attack me face to face.");
//                stage = -2;
//                break;
//
//            case 9:
//                if(!player.getInventory().containsItem(PrinceAliRescue.BALL_OF_WOOL, 3)) {
//                    sendPlayerChat(Mood.NORMAL, "Great, I will get some. I think a wig would be useful.");
//                    stage = -2;
//                } else {
//                    sendOptionsDialogue(DEFAULT, "I have that now. Please, make me a wig.", "I will come back when I need you to make me one.");
//                    stage = 10;
//                }
//                break;
//
//            case 10:
//                switch (componentId) {
//                    case OPTION_1:
//                        sendNPCChat(Mood.NORMAL, "Okay, I will have a go.");
//                        stage = 11;
//                        break;
//                    case OPTION_2:
//                    sendNPCChat(Mood.NORMAL, "Well, it sounds like a challenge. Come to me if you " +
//                            "need one.");
//                        stage = -2;
//                        break;
//                }
//                break;
//
//            case 11:
//                sendDialogue("You hand Ned 3 balls of wool. Ned works the wool. " +
//                        "His hands move with a speed you couldn't imagine.");
//                stage = 12;
//                break;
//
//            case 12:
//                sendNPCChat(Mood.HAPPY, "Here you go, how's that for a quick effort? " +
//                        "Not bad I think!");
//                stage = 13;
//                break;
//
//            case 13:
//                sendHandedItem(PrinceAliRescue.WIG, "Ned gives you a pretty good wig.");
//                stage = 14;
//                break;
//
//            case 14:
//                sendNPCChat(Mood.HAPPY, "Thanks Ned, there's more to you than meets the eye.");
//                stage = -2;
//                break;
//
//            case 15:
//                sendPlayerChat(Mood.ASKING, "You make rope from wool?");
//                stage = 16;
//                break;
//
//            case 16:
//                sendNPCChat(Mood.NORMAL, "Of course you can!");
//                stage = 17;
//                break;
//
//            case 17:
//                sendNPCChat(Mood.ASKING, "I thought you needed hemp or jute.");
//                stage = 18;
//                break;
//
//            case 18:
//                sendNPCChat(Mood.ASKING, "Do you want some rope or not?");
//                stage = 19;
//                break;
//
//            case 19:
//                sendOptionsDialogue(DEFAULT, "Okay, please sell me some rope.", "That's a little more than I want to pay.",
//                        (player.getInventory().containsItem(PrinceAliRescue.BALL_OF_WOOL, 4) ? "Luls make some u cunt." : "I will go and get some wool."));
//              stage = 20;
//                break;
//
//            case 20:
//                switch (componentId) {
//                    case OPTION_1:
//                        sendNPCChat(Mood.HAPPY, "There you go, Finest rope in " + Settings.SERVER_NAME);
//                        stage = 21;
//                        break;
//                    case OPTION_2:
//                        sendNPCChat(Mood.NORMAL, "Well, if you ever need rope that's the price. Sorry. " +
//                                "An old sailor needs money for a little drop o' rum.");
//                        stage = -2;
//                        break;
//                    case OPTION_3:
//                        if (player.getInventory().containsItem(PrinceAliRescue.BALL_OF_WOOL, 4)) {
//                        end();//TODO
//                        } else {
//                            sendNPCChat(Mood.NORMAL, "Aye, you do that. Remember, it takes 4 balls of wool to " +
//                                    "make strong rope.");
//                            stage = -2;
//                        }
//                        break;
//                }
//                break;
//
//            case 21:
//                sendHandedItem(PrinceAliRescue.ROPE, "You hand Ned 15 coins. Ned gives you a coil of rope.");
//                if(player.getInventory().getFreeSlots() > 0) {
//                    player.getInventory().addItem(PrinceAliRescue.ROPE, 1);
//                } else {
//                    World.addGroundItem(new Item(PrinceAliRescue.ROPE), new Location(player));
//                }
//                stage = -2;
//                break;
//
//            case -2:
//                end();
//                break;
//
//            /**
//             * player.sm("You dye the wig blonde.");
//             * Yellow dye - non dyed wig.
//             * display ("I have a wig suitable for disguise with me")
//             */
//        }
//    }
//
//    @Override
//    public void finish() {
//
//    }
}
