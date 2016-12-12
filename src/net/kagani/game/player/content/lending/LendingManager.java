package net.kagani.game.player.content.lending;

import java.io.Serializable;

public class LendingManager implements Serializable {

	/*
	 * private static final long serialVersionUID = 2496224027921198991L; public
	 * static CopyOnWriteArrayList<Lend> list;
	 * 
	 * @SuppressWarnings("unchecked") public static void init() { File file =
	 * new File("data/lentItems.ser"); if (file.exists()) { try { list =
	 * (CopyOnWriteArrayList<Lend>)
	 * SerializableFilesManager.loadSersializedFile(file); return; } catch
	 * (Exception e) { e.printStackTrace(); } } list = new
	 * CopyOnWriteArrayList<Lend>(); }
	 * 
	 * public static void lend(Lend lend) { list.add(lend); save(); }
	 * 
	 * public static boolean hasLendedItem(Player player) { for (Lend lend :
	 * list) { if (lend.getLendee().equals(player.getUsername())) return true; }
	 * return false; }
	 * 
	 * public static boolean hasLendedOut(Player player) { for (Lend lend :
	 * list) { if (lend.getLender().equals(player.getUsername())) return true; }
	 * return false; }
	 * 
	 * public static Lend getLend(Player player) { for (Lend lend : list) if
	 * (lend.getLendee().equals(player.getUsername())) return lend; return null;
	 * }
	 * 
	 * public static boolean hasLendedOn(Player player) { for (Item item :
	 * player.getInventory().getItems().toArray()) { if (item == null) continue;
	 * if (isLendedItem(player, item)) return true; } for (Item item :
	 * player.getEquipment().getItems().toArray()) { if (item == null) continue;
	 * if (isLendedItem(player, item)) return true; } if (player.getFamiliar()
	 * != null) { if (player.getFamiliar().getBob() != null) { for (Item item :
	 * player.getFamiliar().getBob().getBeastItems().toArray()) { if (item ==
	 * null) continue; if (isLendedItem(player, item)) return true; } } } return
	 * false; }
	 * 
	 * public static void remove(Lend lend) { list.remove(lend); save(); }
	 * 
	 * public static void save() { try {
	 * SerializableFilesManager.storeSerializableClass(list, new
	 * File("data/lentItems.ser")); } catch (IOException e) {
	 * e.printStackTrace(); } }
	 * 
	 * public static int getHoursLeft(long millis) { long mil = millis -
	 * Utils.currentTimeMillis(); long sec = (mil / 1000); long min = (sec /
	 * 60); int hour = 0; while (min > 60) { hour++; min -= 60; } return (int)
	 * hour; }
	 * 
	 * public static int getMinutesLeft(long millis) { long mil = millis -
	 * Utils.currentTimeMillis(); long sec = (mil / 1000); long min = (sec /
	 * 60); while (min > 60) { min -= 60; } return (int) min; }
	 * 
	 * public static boolean isLendedItem(Player player, Item item) { for (Lend
	 * lend : list) if (lend.getItem().getDefinitions().getLendId() ==
	 * item.getId() && lend.getLendee().equals(player.getUsername())) return
	 * true; return false; }
	 * 
	 * public static void unLend(Lend lend) throws ClassNotFoundException,
	 * IOException { boolean lenderLog = true; boolean lendeeLog = true; Player
	 * lender = World.getPlayer(lend.getLender()); Player lendee =
	 * World.getPlayer(lend.getLendee()); if (lender == null) { lenderLog =
	 * false; lender = (Player) SerializableFilesManager.loadSersializedFile(new
	 * File(SerializableFilesManager.PLAYER_DATA + lend.getLender() + ".acc"));
	 * } if (lendee == null) { lendeeLog = false; lendee = (Player)
	 * SerializableFilesManager.loadSersializedFile(new
	 * File(SerializableFilesManager.PLAYER_DATA + lend.getLendee() + ".acc"));
	 * } if
	 * (lendee.getInventory().containsItem(lend.getItem().getDefinitions().getLendId
	 * (), 1)) { lendee.getInventory().getItems().remove(new
	 * Item(lend.getItem().getDefinitions().getLendId())); if (lendeeLog)
	 * lendee.getInventory().refresh(); }
	 * 
	 * if
	 * (lendee.getEquipment().hasItem(lend.getItem().getDefinitions().getLendId
	 * ())) {
	 * 
	 * lendee.getEquipment().deleteItem(lend.getItem().getDefinitions().getLendId
	 * (), 1); if (lendeeLog) { lendee.getEquipment().init();
	 * lendee.getAppearence().generateAppearenceData(); } } if
	 * (lendee.getBank().
	 * containsItem(lend.getItem().getDefinitions().getLendId())) {
	 * lendee.getBank().removeItem(lend.getItem().getDefinitions().getLendId());
	 * if (lendeeLog) lendee.getBank().refreshItems(); }
	 * lender.getBank().addItem(lend.getItem().getId(), 1, lenderLog); if
	 * (lenderLog) lender.getPackets().sendGameMessage(
	 * "<col=FF0000>An item you lent out has been added back to your bank."); if
	 * (lendeeLog) lendee.getPackets().sendGameMessage(
	 * "<col=FF0000>An item you borrowed has been returned to the owner."); try
	 * { if (!lenderLog) { //lender.lendMessage = 1;
	 * SerializableFilesManager.storeSerializableClass(lender, new
	 * File("data/accounts/" + lend.getLender() + ".acc")); } if (!lendeeLog) {
	 * //lendee.lendMessage = 2;
	 * SerializableFilesManager.storeSerializableClass(lendee, new
	 * File("data/accounts/" + lend.getLendee() + ".acc")); } } catch
	 * (IOException e) { e.printStackTrace(); } list.remove(lend); save(); }
	 * 
	 * public static void process() throws ClassNotFoundException, IOException {
	 * List<Lend> toRemove = new ArrayList<Lend>(); for (Lend lend : list) { if
	 * (lend.getTime() <= Utils.currentTimeMillis()) { toRemove.add(lend);
	 * boolean lenderLog = true; boolean lendeeLog = true; Player lender =
	 * World.getPlayer(lend.getLender()); Player lendee =
	 * World.getPlayer(lend.getLendee()); if (lender == null) { lenderLog =
	 * false;
	 * 
	 * lender = (Player) SerializableFilesManager.loadSersializedFile(new
	 * File("data/accounts/" + lend.getLender() + ".acc")); } if (lendee ==
	 * null) { lendeeLog = false; lendee = (Player)
	 * SerializableFilesManager.loadSersializedFile(new
	 * File(SerializableFilesManager.PLAYER_DATA + lend.getLendee() + ".acc"));
	 * } if
	 * (lendee.getInventory().containsItem(lend.getItem().getDefinitions().getLendId
	 * (), 1)) { lendee.getInventory().getItems().remove(new
	 * Item(lend.getItem().getDefinitions().getLendId())); if (lendeeLog)
	 * lendee.getInventory().refresh(); } if
	 * (lendee.getEquipment().containsOneItem
	 * (lend.getItem().getDefinitions().getLendId())) {
	 * lendee.getEquipment().deleteItem
	 * (lend.getItem().getDefinitions().getLendId(), 1); if (lendeeLog) {
	 * lendee.getEquipment().init();
	 * lendee.getAppearence().getAppeareanceData(); } } if
	 * (lendee.getBank().containsItem
	 * (lend.getItem().getDefinitions().getLendId())) {
	 * lendee.getBank().removeItem(lend.getItem().getDefinitions().getLendId());
	 * if (lendeeLog) lendee.getBank().refreshItems(); }
	 * lender.getBank().addItem(lend.getItem().getId(), 1, lenderLog); if
	 * (lenderLog) lender.getPackets().sendGameMessage(
	 * "<col=FF0000>An item you lent out has been added back to your bank."); if
	 * (lendeeLog) lendee.getPackets().sendGameMessage(
	 * "<col=FF0000>An item you were lent has been returned to the owner."); try
	 * { if (!lenderLog) { //lender.lendMessage = 1;
	 * SerializableFilesManager.storeSerializableClass(lender, new
	 * File("data/characters/" + lend.getLender() + ".p")); } if (!lendeeLog) {
	 * //lendee.lendMessage = 2;
	 * SerializableFilesManager.storeSerializableClass(lendee, new
	 * File("data/characters/" + lend.getLendee() + ".p")); } } catch
	 * (IOException e) { e.printStackTrace(); } } } list.removeAll(toRemove); if
	 * (toRemove.size() > 0) save(); toRemove.clear(); }
	 * 
	 * public static void handleButtons(final Player player, int slotId) { Item
	 * item = player.getInventory().getItems().get(slotId); if (item == null)
	 * return; if (item.getDefinitions().getLendId() == -1) {
	 * player.getPackets().sendGameMessage( "You can't lend that item.");
	 * return; } if (!ItemConstants.isTradeable(item)) {
	 * player.getPackets().sendGameMessage("You can't lend that item."); return;
	 * } if(hasLendedOut(player)) {
	 * player.getPackets().sendGameMessage("You have already lent out an item."
	 * ); return; } if(player.getTrade().getLendedItem() != null) {
	 * player.getPackets
	 * ().sendGameMessage("You are already offering to lend an item."); return;
	 * } player.getTemporaryAttributtes().put("lend_item_time", slotId);
	 * player.getPackets().sendExecuteScript(108, new Object[] {
	 * "How long do you wish to lend this item (in hours)?" }); }
	 */

}