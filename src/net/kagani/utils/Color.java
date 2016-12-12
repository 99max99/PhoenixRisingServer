package net.kagani.utils;

public enum Color {

	/**
	 * @author: Dylan Page
	 */

	RED("<col=FF0000>"),

	BLUE("<col=0000FF>"),

	DARK_BLUE("<col=0000A0>"),

	LIGHT_BLUE("<col=ADD8E6>"),

	CYAN("<col=00FFFF>"),

	YELLOW("<col=FFFF00>"),

	LIME("<col=00FF00>"),

	MAGNETA("<col=FF00FF>"),

	WHITE("<col=FFFFFF>"),

	SILVER("<col=C0C0C0>"),

	GRAY("<col=808080>"),

	BLACK("<col=000000>"),

	ORANGE("<col=ee7600>"),

	BROWN("<col=A52A2A>"),

	MAROON("<col=800000>"),

	GREEN("<col=008000>"),

	OLIVE("<col=808000>"),

	PINK("<col=ff69b4>"),

	PURPLE("<col=551A8B>"),

	;

	String hex;

	Color(String hex) {
		this.hex = hex;
	}

	public String getHex() {
		return hex;
	}
}