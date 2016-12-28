package net.kagani.game.player.content;

import net.kagani.cache.loaders.ItemDefinitions;
import net.kagani.game.player.Player;

public class ItemColouring {
	
	private static final int barrows = 33294, third = 33298, shadows = 33296;
	private static final int rapier = 26579, ohrapier = 26583, mace = 26595;
	private static final int ohmace = 26599, longsword = 26587, ohlongsword = 26591;
	private static final int ascension = 28437, ohascension = 28441, wand = 28617, singularity = 28621;
	private static final int longbow = 31733, scythe = 31725, staff = 31729;
	private static final int mask = 28600, hauberk = 28601, chaps = 28602;
	private static final int tmask = 28608, robetop = 28611, robeleg = 28614;
	private static final int helm = 30005, plate = 30008, platelegs = 30011;
	private static final int cloth = 3188;
	private static final String add = "You add a new colour to your item.", remove = "You remove the colour from your item, returning it to its original state.";
	
	public enum Colourables {
		
		/**
		 * @author: Dylan Page
		 * @author: Blaze
		 */
		
		/*
		 * Drygore
		 */
		BARROW_RAPIER(barrows, rapier, 33306, ""),
		BARROW_OH_RAPIER(barrows, ohrapier, 33309, ""),
		SHADOW_RAPIER(shadows, rapier, 33372, ""),
		SHADOW_OH_RAPIER(shadows, ohrapier, 33375, ""),
		THIRD_RAPIER(third, rapier, 33438, ""),
		THIRD_OH_RAPIER(third, ohrapier, 33441, ""),
		
		BARROW_MACE(barrows, mace, 33300, ""),
		BARROW_OH_MACE(barrows, ohmace, 33303, ""),
		SHADOW_MACE(shadows, mace, 33366, ""),
		SHADOW_OH_MACE(shadows, ohmace, 33369, ""),
		THIRD_MACE(third, mace, 33432, ""),
		THIRD_OH_MACE(third, ohmace, 33435, ""),
		
		BARROW_LONGSWORD(barrows, longsword, 33312, ""),
		BARROW_OH_LONGSWORD(barrows, ohlongsword, 33315, ""),
		SHADOW_LONGSWORD(shadows, longsword, 33378, ""),
		SHADOW_OH_LONGSWORD(shadows, ohlongsword, 33381, ""),
		THIRD_LONGSWORD(third, longsword, 33444, ""),
		THIRD_OH_LONGSWORD(third, ohlongsword, 33447, ""),
		
		/*
		 * Noxious
		 */
		BARROW_SCYTHE(barrows, scythe, 33331, ""),
		SHADOW_SCYTHE(shadows, scythe, 33396, ""),
		THIRD_SCYTHE(third, scythe, 33462, ""),
		
		BARROW_STAFF(barrows, staff, 33333, ""),
		SHADOW_STAFF(shadows, staff, 33399, ""),
		THIRD_STAFF(third, staff, 33465, ""),
		
		BARROW_LONG(barrows, longbow, 33336, ""),
		SHADOW_LONG(shadows, longbow, 33402, ""),
		THIRD_LONG(third, longbow, 33468, ""),
		
		/*
		 * Seismics
		 */
		BARROW_WAND(barrows, wand, 33324, ""),
		SHADOW_WAND(shadows, wand, 33390, ""),
		THIRD_WAND(third, wand, 33456, ""),

		BARROW_SINGULARITY(barrows, singularity, 33327, ""),
		SHADOW_SINGULARITY(shadows, singularity, 33393, ""),
		THIRD_SINGULARITY(third, singularity, 33459, ""),
		
		/*
		 * Ascensions
		 */
		BARROW_ASCENSION(barrows, ascension, 33318, ""),
		SHADOW_ASCENSION(shadows, ascension, 33384, ""),
		THIRD_ASCENSION(third, ascension, 33450, ""),
		
		BARROW_OH_ASCENSION(barrows, ohascension, 33321, ""),
		SHADOW_OH_ASCENSION(shadows, ohascension, 33387, ""),
		THIRD_OH_ASCENSION(third, ohascension, 33453, ""),
		
		/*
		 * Malevolent
		 */
		BARROW_HELM(barrows, helm, 33357, ""),
		BARROW_PLATE(barrows, plate, 33360, ""),
		BARROW_PLATELEGS(barrows, platelegs, 33363, ""),
		
		SHADOW_HELM(shadows, helm, 33423, ""),
		SHADOW_PLATE(shadows, plate, 33426, ""),
		SHADOW_PLATELEGS(shadows, platelegs, 33429, ""),
		
		THIRD_HELM(third, helm, 33489, ""),
		THIRD_PLATE(third, plate, 33492, ""),
		THIRS_PLATELEGS(third, platelegs, 33495, ""),
		
		/*
		 * Sirenic
		 */
		BARROW_MASK(barrows, mask, 33348, ""),
		BARROW_HAUBERK(barrows, hauberk, 33351, ""),
		BARROW_CHAPS(barrows, chaps, 33354, ""),
		
		SHADOW_MASK(shadows, mask, 33414, ""),
		SHADOW_HAUBERK(shadows, hauberk, 33417, ""),
		SHADOW_CHAPS(shadows, chaps, 33420, ""),
		
		THIRD_MASK(third, mask, 33480, ""),
		THIRD_HAUBERK(third, hauberk, 33483, ""),
		THIRD_CHAPS(third, chaps, 33486, ""),
		
		/*
		 * Tectonic
		 */
		BARROW_TMASK(barrows, tmask, 33339, ""),
		BARROW_ROBETOP(barrows, robetop, 33342, ""),
		BARROW_ROBELEG(barrows, robeleg, 33345, ""),
		
		SHADOW_TMASK(shadows, tmask, 33405, ""),
		SHADOW_ROBETOP(shadows, robetop, 33408, ""),
		SHADOW_ROBELEG(shadows, robeleg, 33411, ""),
		
		THIRD_TMASK(third, tmask, 33471, ""),
		THIRD_ROBETOP(third, robetop, 33474, ""),
		THIRD_ROBELEG(third, robeleg, 33477, "");
		
		private int dye, item, product;
		private String message;
		
		/**
		 * Used to obtain the value based off of original item and dye
		 */
		public static Colourables forItem(int item, int dye) {//used for dying
			for (Colourables product : Colourables.values()) {
				if (product.item == item && product.dye == dye || product.item == dye && product.dye == item) {
					return product;
				}
			}
			return null;
		}
		
		/**
		 * Used to obtain the value based off of the product
		 */
		public static Colourables forProduct(int item) {
			for (Colourables product : Colourables.values()) {
				if (product.product == item) {
					return product;
				}
			}
			return null;
		}
		
		public int getId() {
			return item;
		}
		
		public int getDye() {
			return dye;
		}
		
		public int getProduct() {
			return product;
		}
		
		public String getMessage() {
			return message;
		}
		
		private Colourables(int dye, int item, int product, String message) {
			this.dye = dye;
			this.item = item;
			this.product = product;
			this.message = message;
		}
	}
	
	public static boolean ApplyDyeToItems(Player player, int used, int usedWith) {
		if (!player.getInventory().containsItem(used, 1) || !player.getInventory().containsItem(usedWith, 1)) {
			return false;
		}
		player.getInventory().deleteItem(used, 1);
		player.getInventory().deleteItem(usedWith, 1);
		player.getInventory().addItem(Colourables.forItem(used, usedWith).getProduct(), 1);
		if (Colourables.forItem(used,  usedWith).getMessage().equals(""))
			player.getPackets().sendGameMessage("You carefully coat your "
					+(ItemDefinitions.getItemDefinitions(Colourables.forItem(used, usedWith).getId()).getName().toLowerCase())+
					" with the "+(ItemDefinitions.getItemDefinitions(Colourables.forItem(used, usedWith).getDye()).getName().toLowerCase())+".", true);
		else
			player.getPackets().sendGameMessage(Colourables.forItem(used, usedWith).getMessage(), true);
		return true;
	}
	
	public static boolean RemoveDyeFromItems(Player player, int used, int usedWith) {
		if (!player.getInventory().containsItem(used, 1) || !player.getInventory().containsItem(usedWith, 1)) {
			return false;
		}
		if (used != cloth && usedWith != cloth)
			return false;
		if (used == cloth)
			player.getInventory().deleteItem(usedWith, 1);
		else
			player.getInventory().deleteItem(used, 1);
		player.getInventory().addItem(Colourables.forProduct(used).getId(), 1);
		if (Colourables.forProduct(used).getMessage().equals(""))
			player.getPackets().sendGameMessage("You carefully remove the dye from your "+(ItemDefinitions.getItemDefinitions(Colourables.forProduct(used).getId()).getName().toLowerCase())+".", true);
		else
			player.getPackets().sendGameMessage(remove, true);
		return true;
	}
	
}
