package net.kagani.game.player.content.pports;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class PortConstants {

	public enum Ports {

		PUBLIC_PORTS(-1, -1, -1, -1), PRIVATE_PORT(1528, 903, 8, 14);

		public int rx;
		public int ry;
		public int w;
		public int h;

		Ports(int rx, int ry, int w, int h) {
			this.rx = rx;
			this.ry = ry;
			this.w = w;
			this.h = h;
		}

		public int getRx() {
			return rx;
		}

		public int getRy() {
			return ry;
		}

		public int getWidth() {
			return w;
		}

		public int getHeight() {
			return h;
		}
	}
}