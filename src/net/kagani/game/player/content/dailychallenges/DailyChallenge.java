package net.kagani.game.player.content.dailychallenges;

/*
 * @author Dylan Page
 * @author 
 */

public class DailyChallenge {

 /*protected Player player;

 public DailyChallenge(Player player) {
 this.player = player;
 }

 public enum Tasks {
 AIRUT_BONES(5, 60, 1050, 15, "Bury 15 Airut Bones"),

 DRAGON_BONES(5, 50, 1050, 25, "Bury 25 Dragon Bones"),

 FINISH_DUNGEON(24, 20, 1050, 2, "Delve into Daemonheim"),

 CUT_MAGIC_LOGS(5, 50, 1050, 25, "Cut 25 Magic Logs"),

 CUT_YEW_LOGS(5, 50, 1050, 40, "Cut 40 Yew Logs"),

 BURN_MAGIC_LOGS(5, 50, 1050, 25, "Burn 25 Magic Logs"),

 BURN_YEW_LOGS(5, 50, 1050, 40, "Burn 40 Yew Logs");

 public int skillId, skillLevel, reward, amount;
 public String name;

 Tasks(int skillId, int skillLevel, int reward, int amount, String name) {
 this.skillId = skillId;
 this.skillLevel = skillLevel;
 this.reward = reward;
 this.amount = amount;
 this.name = name;
 }

 public int getSkillId() {
 return skillId;
 }

 public int getSkillLevel() {
 return skillLevel;
 }

 public int getReward() {
 return reward;
 }

 public int getAmount() {
 return amount;
 }

 public String getName() {
 return name;
 }
 }

 public void assignTask(Tasks task) {
 //player.setDailyChallenge(task);
 }

 public boolean doesPlayerHaveTask() {
 /*if (player.getDailyChallenge() == null)
 return false;*/
/*return true;
 }

 public void updateTask() {
 if (!doesPlayerHaveTask())
 generateRandomTask();
 }

 public void generateRandomTask() { // Just to be safer.
 assignTask(Tasks.values()[Utils.random(Tasks.values().length)]);
 }*/
}