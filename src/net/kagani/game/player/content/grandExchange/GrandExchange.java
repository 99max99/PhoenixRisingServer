package net.kagani.game.player.content.grandExchange;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.kagani.Settings;
import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.item.Item;
import net.kagani.game.player.Player;
import net.kagani.game.player.content.Consumables;
import net.kagani.game.player.content.Drinkables;
import net.kagani.utils.SerializableFilesManager;
import net.kagani.utils.Utils;

public class GrandExchange {

	private static final Object LOCK = new Object();
	// offer uid
	private static HashMap<Long, Offer> OFFERS;
	private static ArrayList<OfferHistory> OFFERS_TRACK;
	private static HashMap<Integer, Integer> PRICES;

	private static int GE_LIMIT = 25;

	private static boolean edited;

	public static void init() {
		OFFERS = SerializableFilesManager.loadGEOffers();
		OFFERS_TRACK = SerializableFilesManager.loadGEHistory();
		PRICES = SerializableFilesManager.loadGEPrices();
	}

	public static void reset(boolean track, boolean price) {
		if (track)
			OFFERS_TRACK.clear();
		if (price)
			PRICES.clear();
		recalcPrices();
	}

	public static void recalcPrices() {
		ArrayList<OfferHistory> track = new ArrayList<OfferHistory>(
				OFFERS_TRACK);
		HashMap<Integer, BigInteger> averagePrice = new HashMap<Integer, BigInteger>();
		HashMap<Integer, BigInteger> averageQuantity = new HashMap<Integer, BigInteger>();
		for (OfferHistory o : track) {
			BigInteger price = averagePrice.get(o.getId());
			if (price != null) {
				BigInteger quantity = averageQuantity.get(o.getId());
				averagePrice.put(o.getId(),
						price.add(BigInteger.valueOf(o.getPrice())));
				averageQuantity.put(o.getId(),
						quantity.add(BigInteger.valueOf(o.getQuantity())));
			} else {
				averagePrice.put(o.getId(), BigInteger.valueOf(o.getPrice()));
				averageQuantity.put(o.getId(),
						BigInteger.valueOf(o.getQuantity()));
			}
		}

		for (int id : averagePrice.keySet()) {
			BigInteger price = averagePrice.get(id);
			BigInteger quantity = averageQuantity.get(id);

			long oldPrice = getPrice(id);
			long newPrice = price.divide(quantity).longValue();
			long min = (long) (oldPrice * 0.95 + -1);
			long max = (long) (oldPrice * 1.05 + 1);
			if (newPrice < min)
				newPrice = min;
			else if (newPrice > max)
				newPrice = max;
			if (newPrice < 1)
				newPrice = 1;
			else if (newPrice > Integer.MAX_VALUE)
				newPrice = Integer.MAX_VALUE;
			int shopValue = ItemDefinitions.getItemDefinitions(id).value;
			if (newPrice < shopValue)
				newPrice = shopValue;
			PRICES.put(id, (int) newPrice);
		}
		OFFERS_TRACK.clear();
		saveOffersTrack();
		savePrices();
	}

	public static void setPrice(int id, int price) {
		PRICES.put(id, price);
	}

	public static void savePrices() {
		SerializableFilesManager.saveGEPrices(new HashMap<Integer, Integer>(
				PRICES));
	}

	private static void saveOffersTrack() {
		SerializableFilesManager.saveGEHistory(new ArrayList<OfferHistory>(
				OFFERS_TRACK));
	}

	public static final void save() {
		if (!edited)
			return;
		SerializableFilesManager.saveGEOffers(new HashMap<Long, Offer>(OFFERS));
		saveOffersTrack();
		edited = false;
	}

	public static void linkOffers(Player player) {
		boolean itemsWaiting = false;
		for (int slot = 0; slot < player.getGeManager().getOfferUIds().length; slot++) {
			Offer offer = getOffer(player, slot);
			if (offer == null)
				continue;
			offer.link(slot, player);
			offer.update();
			if (!itemsWaiting && offer.hasItemsWaiting()) {
				itemsWaiting = true;
				player.getPackets()
						.sendGameMessage(
								"You have items from the Grand Exchange waiting in your collection box.");
			}
		}
	}

	public static Offer getOffer(Player player, int slot) {
		synchronized (LOCK) {
			long uid = player.getGeManager().getOfferUIds()[slot];
			if (uid == 0)
				return null;
			Offer offer = OFFERS.get(uid);
			if (offer == null) {
				player.getGeManager().getOfferUIds()[slot] = 0; // offer
				// disapeared
				// within time
				return null;
			}
			return offer;
		}

	}

	public static void sendOffer(Player player, int slot, int itemId,
			int amount, int price, boolean buy, boolean instant) {
		synchronized (LOCK) {
			Offer offer = new Offer(itemId, amount, price, buy);
			player.getGeManager().getOfferUIds()[slot] = createOffer(offer);
			offer.link(slot, player);
			findBuyerSeller(offer);
			if (instant) {
				if (ItemDefinitions.getItemDefinitions(itemId).getName()
						.contains("sapphire")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("emerald")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("ruby")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("diamond")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("dragonstone")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("onyx")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("hydrix")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("bones")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("mithril seeds")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("potion")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("rocktail")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("cooked karambwan")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("shark")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("raw")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains(" ore")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("partyhat")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("bolt rack")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("lucky")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("notepaper")
						|| ItemDefinitions.getItemDefinitions(itemId).getName()
								.contains("coal")
						|| ItemDefinitions.getItemDefinitions(itemId)
								.getPrice() >= 5000000
						|| Consumables.isConsumable(new Item(itemId))
						|| Drinkables.isDrinkable(player, new Item(itemId))
						|| ItemDefinitions.getItemDefinitions(itemId)
								.isStackable())
					return;
				if (player.grandExchangeLimit[itemId] >= GE_LIMIT) {
					player.getPackets()
							.sendGameMessage(
									"You have reached the daily limit on this item. You can still sell the item to other people that has daily limit on this item.");
					return;
				}
				if (price > ItemDefinitions.getItemDefinitions(itemId)
						.getPrice()) {
					player.getPackets().sendGameMessage(
							ItemDefinitions.getItemDefinitions(itemId)
									.getName()
									+ "'s "
									+ (buy ? "buys" : "sells")
									+ " instantly for "
									+ Utils.format(ItemDefinitions
											.getItemDefinitions(itemId)
											.getPrice()) + " gp.");
					return;
				}
				if (amount >= GE_LIMIT) {
					player.getPackets()
							.sendGameMessage(
									"You have reached the daily limit on this item. You can still sell the item to other people that has daily limit on this item.");
					player.grandExchangeLimit[itemId] = GE_LIMIT;
					return;
				}
				player.grandExchangeLimit[itemId] += amount;
				if (price != ItemDefinitions.getItemDefinitions(itemId)
						.getPrice()) {
					if (price >= ItemDefinitions.getItemDefinitions(itemId)
							.getPrice())
						price = ItemDefinitions.getItemDefinitions(itemId)
								.getPrice();
				}
				player.getPackets().sendGameMessage(
						ItemDefinitions.getItemDefinitions(itemId).getName()
								+ "'s " + (buy ? "buys" : "sells")
								+ " instantly - daily limit: "
								+ player.grandExchangeLimit[itemId] + "/"
								+ GE_LIMIT + " on this item.");
				Offer ignore = new Offer(itemId, amount >= GE_LIMIT ? GE_LIMIT
						: amount, ItemDefinitions.getItemDefinitions(itemId)
						.getPrice(), buy);
				offer.updateOffer(ignore);
				offer.update();
			}
		}
	}

	public static void abortOffer(Player player, int slot) {
		synchronized (LOCK) {
			Offer offer = getOffer(player, slot);
			if (offer == null)
				return;
			edited = true;
			OFFERS.remove(offer);
			if (offer.cancel() && offer.forceRemove())
				deleteOffer(player, slot); // shouldnt here happen anyway
		}
	}

	public static void collectItems(Player player, int slot, int invSlot,
			int option) {
		synchronized (LOCK) {
			Offer offer = getOffer(player, slot);
			if (offer == null)
				return;
			edited = true;
			if (offer.collectItems(invSlot, option) && offer.forceRemove()) {
				deleteOffer(player, slot); // should happen after none left and
				// offer completed
				if (offer.getTotalAmmountSoFar() != 0) {
					OfferHistory o = new OfferHistory(offer.getId(),
							offer.getTotalAmmountSoFar(),
							offer.getTotalPriceSoFar(), offer.isBuying());
					OFFERS_TRACK.add(o);
					player.getGeManager().addOfferHistory(o);
				}
			}
		}
	}

	private static void deleteOffer(Player player, int slot) {
		player.getGeManager().cancelOffer(); // sends back to original screen if
		// seeing an offer
		OFFERS.remove(player.getGeManager().getOfferUIds()[slot]);
		player.getGeManager().getOfferUIds()[slot] = 0;
	}

	private static void findBuyerSeller(Offer offer) {
		while (!offer.isCompleted()) {
			Offer bestOffer = null;
			if ((offer.isBuying() && offer.getPrice() >= GrandExchange.getPrice(offer.getId())) || (!offer.isBuying() && offer.getPrice() <= GrandExchange.getPrice(offer.getId()))) {
			    bestOffer = new Offer(offer.getId(), offer.getAmount(), GrandExchange.getPrice(offer.getId()), !offer.isBuying());
			}
			if (bestOffer == null)
				break;
			offer.updateOffer(bestOffer);
		}
		offer.update();
	}
//		while (!offer.isCompleted()) {
//			Offer bestOffer = null;
//			for (Offer o : OFFERS.values()) {
//				// owner is null when not logged in but u online its on so works
//				if (o.getOwner() == offer.getOwner()
//						|| o.isBuying() == offer.isBuying()
//						|| o.getId() != offer.getId()
//						|| o.isCompleted()
//						|| (offer.isBuying() && o.getPrice() > offer.getPrice())
//						|| (!offer.isBuying() && o.getPrice() < offer
//								.getPrice()) || offer.isOfferTooHigh(o))
//					continue;
//				if (bestOffer == null
//						|| (offer.isBuying() && o.getPrice() < bestOffer
//								.getPrice())
//						|| (!offer.isBuying() && o.getPrice() > bestOffer
//								.getPrice()))
//					bestOffer = o;
//			}
//			if (bestOffer == null)
//				break;
//			offer.updateOffer(bestOffer);
//		}
//		offer.update();
//	}

	private static long createOffer(Offer offer) {
		edited = true;
		long uid = getUId();
		OFFERS.put(uid, offer);
		return uid;
	}

	private static long getUId() {
		while (true) {
			long uid = ThreadLocalRandom.current().nextLong();
			if (OFFERS.containsKey(uid))
				continue;
			return uid;
		}
	}

	public static void showInstantOffers(Player player) {
		if (player.isAnIronMan()) {
			player.getPackets().sendGameMessage(
					"You are an " + player.getIronmanTitle(true)
							+ ", you stand alone.");
			return;
		}
		player.getInterfaceManager().sendCentralInterface(1166);
		player.getPackets().sendIComponentText(1166, 23, "Instant Offers");
		player.getPackets().sendIComponentText(1166, 2,
				"<col=00FF00>496</col> items can be bought/sold instantly");
		String list = "";
		int index = 0;
		for (int i = 0; i < Settings.GRAND_EXCHANGE_INSTANT_BUYABLES.size(); i++) {
			if (!ItemDefinitions.getItemDefinitions(
					Settings.GRAND_EXCHANGE_INSTANT_BUYABLES.get(i))
					.isStackable()
					|| Consumables.isConsumable(new Item(
							Settings.GRAND_EXCHANGE_INSTANT_BUYABLES.get(i)))
					|| Drinkables.isDrinkable(player, new Item(
							Settings.GRAND_EXCHANGE_INSTANT_BUYABLES.get(i)))
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("sapphire")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("emerald")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName().contains("ruby")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("diamond")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("dragonstone")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName().contains("onyx")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("hydrix")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("bones")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("mithril seeds")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("potion")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("rocktail")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("cooked karambwan")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("shark")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName().contains("raw")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName().contains(" ore")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("partyhat")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("bolt rack")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("lucky")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName()
							.contains("notepaper")
					|| ItemDefinitions
							.getItemDefinitions(
									Settings.GRAND_EXCHANGE_INSTANT_BUYABLES
											.get(i)).getName().contains("coal")
					|| ItemDefinitions.getItemDefinitions(
							Settings.GRAND_EXCHANGE_INSTANT_BUYABLES.get(i))
							.getPrice() >= 5000000)
				continue;
			index++;
			if (index > 49)
				break;
			String itemName = ItemDefinitions.getItemDefinitions(
					Settings.GRAND_EXCHANGE_INSTANT_BUYABLES.get(i)).getName();
			list += index + ". " + itemName + "<br>";
		}
		player.getPackets().sendIComponentText(1166, 1, list.trim());
		player.getPackets().sendGameMessage(
				"A full detailed list can be found at ::topic 583.");
	}

	public static void showOffers(Player player) {
		if (player.isAnIronMan()) {
			player.getPackets().sendGameMessage(
					"You are an " + player.getIronmanTitle(true)
							+ ", you stand alone.");
			return;
		}
		player.getInterfaceManager().sendCentralInterface(1166);
		player.getPackets().sendIComponentText(1166, 23, "GE Offers");
		player.getPackets().sendIComponentText(
				1166,
				2,
				"<col=00FF00>" + OFFERS.values().size()
						+ "</col> items for sale");
		String list = "";

		int index = 0;
		for (final Offer offers : OFFERS.values()) {
			index++;
			if (index > 49)
				break;
			if (offers.isBuying())
				list += "<col=FF0000>[Buy]</col> " + offers.getAmount() + "x "
						+ offers.getName() + " - "
						+ Utils.format(offers.getPrice()) + " gp<br>";
			else
				list += "<col=FF0000>[Sell]</col> " + offers.getAmount() + "x "
						+ offers.getName() + " - "
						+ Utils.format(offers.getPrice()) + " gp<br>";
		}
		player.getPackets().sendIComponentText(1166, 1, list.trim());
	}

	public static int getPrice(int itemId) {
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		if (defs.isNoted())
			itemId = defs.getCertId();
		else if (defs.isLended())
			itemId = defs.getLendId();
		Integer price = defs.getPrice();
		// return price != null && price != 0 ? price : defs.getValue();
		return defs.getGrandExchangePrice();
	}

	// in order not to keep player saved on memory in offers after player leaves
	// <.<
	public static void unlinkOffers(Player player) {
		for (int slot = 0; slot < player.getGeManager().getOfferUIds().length; slot++) {
			Offer offer = getOffer(player, slot);
			if (offer == null)
				continue;
			offer.unlink();
		}
	}

	public static List<OfferHistory> getHistory() {
		return OFFERS_TRACK;
	}
}