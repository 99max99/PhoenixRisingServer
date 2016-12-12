package net.kagani.game.player.controllers;

import java.io.Serializable;

import net.kagani.executor.WorldThread;
import net.kagani.game.Animation;
import net.kagani.game.World;
import net.kagani.game.WorldObject;
import net.kagani.game.minigames.Sawmill;
import net.kagani.game.npc.NPC;
import net.kagani.game.player.Skills;
import net.kagani.game.player.actions.sawmill.ConveyorBeltHopper;
import net.kagani.game.player.actions.sawmill.CutPlank;
import net.kagani.game.player.actions.sawmill.PlanksTake;
import net.kagani.game.player.actions.sawmill.StackOfLogs;
import net.kagani.game.player.content.construction.HouseConstants;
import net.kagani.network.decoders.WorldPacketsDecoder;

public class SawmillController extends Controller {

	private static final WorldObject CONVEYOR_BELT = new WorldObject(46298, 10,
			0, 3324, 3496, 0);
	private static long lastLogAnimation;

	private transient int planks;

	@Override
	public void start() {

	}

	private static class Job implements Serializable {

		private static final long serialVersionUID = 5956399597255319926L;

		private Jobs jobDetails;
		private int[] planks;

		private Job(Jobs jobDetails) {
			this.jobDetails = jobDetails;
			planks = new int[jobDetails.planks.length];
		}
	}

	private static enum Jobs {
		QUICK(15, 15, 20, 5, 10, 5), LARGE(30, 45, 20, 30, 30, 15);
		private int[] planks;

		private Jobs(int shortPlank, int longPlank, int diagonalPlank,
				int toothPlank, int groovePlank, int curvedPlank) {
			planks = new int[] { shortPlank, longPlank, diagonalPlank,
					toothPlank, groovePlank, curvedPlank };
		}
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if (interfaceId == 901) {
			if (componentId == 18)
				player.closeInterfaces();
			else if (componentId == 25 || componentId == 34) {
				takeJob(componentId == 34);
				player.closeInterfaces();
			}
			return false;
		}
		if (interfaceId == 902) {
			if (componentId == 54)
				player.closeInterfaces();
			else if (componentId == 36) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					cutPlank(0, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					cutPlank(0, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					cutPlank(0, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					player.getTemporaryAttributtes().put("PlankMake", 0);
					player.getPackets().sendInputIntegerScript("Enter amount:");
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					cutPlank(0, Integer.MAX_VALUE);
			} else if (componentId == 24) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					cutPlank(1, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					cutPlank(1, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					cutPlank(1, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					player.getTemporaryAttributtes().put("PlankMake", 1);
					player.getPackets().sendInputIntegerScript("Enter amount:");
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					cutPlank(1, Integer.MAX_VALUE);
			} else if (componentId == 30) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					cutPlank(2, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					cutPlank(2, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					cutPlank(2, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					player.getTemporaryAttributtes().put("PlankMake", 2);
					player.getPackets().sendInputIntegerScript("Enter amount:");
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					cutPlank(2, Integer.MAX_VALUE);
			} else if (componentId == 19) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					cutPlank(3, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					cutPlank(3, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					cutPlank(3, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					player.getTemporaryAttributtes().put("PlankMake", 3);
					player.getPackets().sendInputIntegerScript("Enter amount:");
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					cutPlank(3, Integer.MAX_VALUE);
			}
			return false;
		}
		if (interfaceId == 903) {
			if (componentId == 18)
				player.closeInterfaces();
			else {
				for (int i = 0; i < INVESTIGATE_COMPONENT_IDS.length; i++) {
					if (componentId == INVESTIGATE_COMPONENT_IDS[i] + 1) {
						if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
							withdrawFromCart(i, 1);
						else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
							withdrawFromCart(i, 5);
						else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
							withdrawFromCart(i, 10);
						else {
							player.getTemporaryAttributtes().put(
									"PlankWithdraw", i);
							player.getPackets().sendInputIntegerScript(
									"Enter amount:");
						}
						return false;
					}
				}
			}
			return false;
		}
		return true;
	}

	public void cutPlank(int type, int amount) {
		player.closeInterfaces();
		player.getActionManager().setAction(new CutPlank(type, amount, this));
	}

	private void takeJob(boolean quick) {
		if (getJob() != null) {
			player.getPackets().sendGameMessage("You already have a job!");
			return;
		}
		this.setJob(new Job(quick ? Jobs.QUICK : Jobs.LARGE));
		refreshVars();
		sendInterfaces();
	}

	public void addPlank(int type, int amount) {
		Job job = getJob();
		if (job == null)
			return;
		job.planks[type] += amount;
		player.getVarsManager().sendVarBit(type == 5 ? 5008 : 4216 + type,
				job.planks[type]);
	}

	private void refreshVars() {
		Job job = getJob();
		if (job == null)
			return;
		player.getVarsManager().sendVarBit(5007,
				job.jobDetails == Jobs.QUICK ? 0 : 1);
		for (int i = 0; i < job.jobDetails.planks.length; i++)
			player.getVarsManager().sendVarBit(4208 + i,
					((job.jobDetails.planks[i] - 5) / 5));
		for (int i = 0; i < job.planks.length; i++)
			player.getVarsManager().sendVarBit(i == 5 ? 5008 : 4216 + i,
					job.planks[i]);
	}

	/*
	 * this.shortPlank = shortPlank; this.longPlank = longPlank;
	 * this.diagonalPlank = diagonalPlank; this.toothPlank = toothPlank;
	 * this.groovePlank = groovePlank; this.curvedPlank = curvedPlank;
	 */

	@Override
	public void sendInterfaces() {
		if (getJob() != null)
			player.getInterfaceManager().sendMinigameInterface(766);
	}

	private void leave() {
		player.getInterfaceManager().removeMinigameInterface();
	}

	private void openCutPlanks() {
		player.getInterfaceManager().sendCentralInterface(902);
	}

	private static int[] INVESTIGATE_COMPONENT_IDS = { 75, 67, 57, 49, 41, 33 };

	private void inspectCart() {
		player.getInterfaceManager().sendCentralInterface(903);
		for (int i = 0; i < 6; i++)
			refreshCart(i);
	}

	private void refreshCart(int i) {
		Job job = getJob();
		String colour;
		if (job.planks[i] < job.jobDetails.planks[i])
			colour = "<col=FF0000>";
		else if (job.planks[i] > job.jobDetails.planks[i])
			colour = "<col=00FF00>";
		else
			colour = "<col=FFFF00>";
		player.getPackets().sendIComponentText(903,
				INVESTIGATE_COMPONENT_IDS[i], colour + "x" + job.planks[i]);
	}

	public void finishJob() {
		Job job = getJob();
		if (job == null)
			return;
		player.getSkills().addXp(Skills.WOODCUTTING,
				job.jobDetails == Jobs.QUICK ? 4395 : 8580);
		setJob(null);
		player.getInterfaceManager().removeMinigameInterface();
	}

	public boolean isOrderCompleted() {
		Job job = getJob();
		if (job == null)
			return false;
		for (int i = 0; i < job.planks.length; i++) {
			if (job.planks[i] < job.jobDetails.planks[i])
				return false;
		}
		return true;

	}

	public void withdrawFromCart(int type, int quantity) {
		Job job = getJob();
		if (quantity > job.planks[type]) {
			quantity = job.planks[type];
			player.getPackets().sendGameMessage(
					"You have no planks to withdraw.");
		}
		job.planks[type] -= quantity;
		player.getVarsManager().sendVarBit(type == 5 ? 5008 : 4216 + type,
				job.planks[type]);
		refreshCart(type);
	}

	private void openSawmillBoard() {
		player.getInterfaceManager().sendCentralInterface(901);
		Job job = getJob();
		if (job != null) {
			player.getPackets().sendHideIComponent(901, 25, true);
			player.getPackets().sendHideIComponent(901, 26, true);

			player.getPackets().sendHideIComponent(901, 34, true);
			player.getPackets().sendHideIComponent(901, 35, true);

			player.getPackets().sendHideIComponent(901,
					job.jobDetails == Jobs.QUICK ? 22 : 20, true);

			player.getPackets().sendIComponentText(
					901,
					job.jobDetails == Jobs.QUICK ? 33 : 24,
					job.planks[0] + "/" + job.jobDetails.planks[0]
							+ " Short plank" + "<br>" + job.planks[1] + "/"
							+ job.jobDetails.planks[1] + " Long plank" + "<br>"
							+ job.planks[2] + "/" + job.jobDetails.planks[2]
							+ " Diagonal plank" + "<br>" + job.planks[3] + "/"
							+ job.jobDetails.planks[3] + " Tooth plank"
							+ "<br>" + job.planks[4] + "/"
							+ job.jobDetails.planks[4] + " Groove plank"
							+ "<br>" + job.planks[5] + "/"
							+ job.jobDetails.planks[5] + " Curved plank");
		}

	}

	private Job getJob() {
		if (this.getArguments() == null || this.getArguments().length == 0)
			return null;
		return (Job) this.getArguments()[0];
	}

	private void setJob(Job job) {
		setArguments(job == null ? null : new Object[] { job });
	}

	@Override
	public boolean processNPCClick1(final NPC npc) {
		if (npc.getId() == Sawmill.OVERSEER) {
			npc.faceEntity(player);
			player.getDialogueManager().startDialogue("Overseer", npc.getId(),
					null);
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick2(final NPC npc) {
		if (npc.getId() == Sawmill.OVERSEER) {
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			npc.faceEntity(player);
			player.getDialogueManager().startDialogue("Overseer", npc.getId(),
					this);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 46317) {
			if (player.getInventory().getFreeSlots() == 0) {
				player.getPackets()
						.sendGameMessage(
								"This crate is full of saws, but you don't have enough space to take one.");
				return false;
			}
			player.lock(2);
			player.getInventory().addItem(HouseConstants.SAW, 1);
			player.getPackets().sendGameMessage("In the crate you find a saw.");
			return false;
		}
		if (object.getId() == 46296) { // job board
			openSawmillBoard();
			return false;
		}
		if (object.getId() == 46297) {// take logs
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			player.getActionManager().setAction(
					new StackOfLogs(Integer.MAX_VALUE));
			return false;
		}
		if (object.getId() == 46304) {
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			player.getActionManager().setAction(
					new ConveyorBeltHopper(Integer.MAX_VALUE, this));
			return false;
		}
		if (object.getId() == 46309) {
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			player.getActionManager().setAction(
					new PlanksTake(Integer.MAX_VALUE, this));
			return false;
		}
		if (object.getId() == 46303) {
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			inspectCart();
			return false;
		}
		if (object.getId() == 46300) {
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			openCutPlanks();
			return false;
		}
		if (object.getId() == 46307 && object.getX() == 3311
				&& object.getY() == 3491) {
			if (Sawmill.hasPlanksOrLogs(player)) {
				player.getDialogueManager().startDialogue("SimpleNPCMessage",
						Sawmill.OVERSEER,
						"Oi! That's our wood you've got there! Give it  back!");
				return false;
			}
			player.lock(2);
			player.addWalkSteps(object.getX(), object.getY(), 1, false);
			leave();
			removeControler();
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(final WorldObject object) {
		if (object.getId() == 46296) { // job board
			takeJob(true);
			return false;
		}
		if (object.getId() == 46297) {// take logs
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			player.getActionManager().setAction(new StackOfLogs(1));
			return false;
		}
		if (object.getId() == 46304) {
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			player.getActionManager()
					.setAction(new ConveyorBeltHopper(1, this));
			return false;
		}
		if (object.getId() == 46309) {
			if (!hasJob()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You should get a job from the job board first.");
				return false;
			}
			player.getActionManager().setAction(new PlanksTake(1, this));
			return false;
		}
		return true;
	}

	public void addPlank() {
		planks += 2;
		refreshConveyorAnimation();
		refreshPlanks();
	}

	private void refreshConveyorAnimation() {
		if (WorldThread.WORLD_CYCLE > lastLogAnimation + 5) {
			lastLogAnimation = WorldThread.WORLD_CYCLE;
			World.sendObjectAnimation(player, CONVEYOR_BELT, new Animation(
					12394));
			World.sendObjectAnimation(player, CONVEYOR_BELT, new Animation(
					12395));
		}
	}

	public boolean isPlanksFull() {
		return planks >= 100;
	}

	public boolean hasPlanks() {
		return planks > 0;
	}

	public void removePlank() {
		planks--;
		refreshPlanks();
	}

	private void refreshPlanks() {
		player.getVarsManager().sendVarBit(4214, planks);
	}

	@Override
	public boolean processObjectClick3(final WorldObject object) {
		if (object.getId() == 46296) { // job board
			takeJob(false);
			return false;
		}
		if (object.getId() == 46297) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You throw the logs back onto the pile.");
			player.getInventory().deleteItem(1511, Integer.MAX_VALUE);
			return false;
		}
		return true;
	}

	private boolean hasJob() {
		return getJob() != null;
	}

	@Override
	public boolean sendDeath() {
		leave();
		removeControler();
		return true;
	}

	@Override
	public boolean login() {
		refreshVars();
		sendInterfaces();
		return false; // so doesnt remove script
	}

	@Override
	public boolean logout() {
		return false; // so doesnt remove script
	}

	@Override
	public void magicTeleported(int type) {
		leave();
		removeControler();
	}

	@Override
	public void forceClose() {
		leave();
	}
}