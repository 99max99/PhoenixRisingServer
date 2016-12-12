package net.kagani.tools;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

import net.kagani.cache.Cache;
import net.kagani.cache.loaders.Floor2Definitions;
import net.kagani.cache.loaders.FloorDefinitions;
import net.kagani.cache.loaders.IndexedColorImageFile;
import net.kagani.cache.loaders.MapSpriteDefinitions;
import net.kagani.cache.loaders.ObjectDefinitions;
import net.kagani.stream.InputStream;
import net.kagani.utils.Utils;

public class MapGenerator extends JFrame implements MouseListener,
		MouseMotionListener, MouseWheelListener {

	private static final long serialVersionUID = -6054276817868497393L;

	public static void main(String[] args) throws IOException {
		Cache.init();
		Frame f = new MapGenerator();

	}

	public MapGenerator() {
		xPos = 1500;
		yPos = 1500;
		scale = 1;
		ratio = 20;
		setTitle("Dragonkk map viewer");
		setSize(640, 640);
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		checkLoad();

	}

	private int xPos, yPos, planePos, maxRX, maxRY, rxPos, ryPos, ratio;
	private double scale;

	private BufferedImage map;
	private BufferedImage screen;
	private boolean loading;
	private boolean forceNoRefresh;

	public void checkLoad() {
		if (!loading) {
			final int newRX = xPos / 64;
			final int newRY = yPos / 64;
			final int newMaxRX = (int) ((getWidth() / 64) / scale)
					+ (int) (1 * scale) + ratio * 2;
			final int newMaxRY = (int) ((getHeight() / 64) / scale)
					+ (int) (1 * scale) + ratio * 2;
			if (Math.abs(newRX - rxPos) >= ratio / 2
					|| Math.abs(newRY - ryPos) >= ratio / 2
					|| newMaxRX != maxRX || newMaxRY != maxRY || forceNoRefresh) {
				loading = true;
				repaint();
				Thread t = new Thread() {

					@Override
					public void run() {
						map = getMap(newRX, newRY, newMaxRX, newMaxRY);
						rxPos = newRX;
						ryPos = newRY;
						maxRX = newMaxRX;
						maxRY = newMaxRY;
						loading = false;
						forceNoRefresh = false;
						repaint();
					}

				};
				t.setPriority(Thread.MIN_PRIORITY);
				t.setDaemon(true);
				t.start();
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		/*
		 * for(int x = 0; x < 10; x++) { for(int y = 0; y < 10; y++) { int
		 * regionId = ((x+xPos) << 8) | (y + yPos); BufferedImage[] data =
		 * getMap(regionId); if(data == null) continue;
		 * g.drawImage(data[planePos], x*64, 640-y*64, null);
		 * 
		 * } }
		 */
		screen = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics g2 = screen.getGraphics();
		int localX = xPos & 63;
		int localY = yPos & 63;
		// System.out.println(localX+", "+localY);

		if (map != null && !forceNoRefresh)
			g2.drawImage(
					map,
					(int) (((-localX) + (rxPos - xPos / 64) * 64 - ratio * 64) * scale),
					(int) ((-((64 + (ryPos - yPos / 64) * 64 + ratio * 64) - localY)) * scale),
					this);
		g2.setColor(Color.PINK);
		if (loading)
			g2.drawString("Loading", 50, 50);
		if (mouseLocation2 != null) {
			int[] pos = getPos();
			g2.drawString("Position: " + pos[0] + ", " + pos[1], 50, 60);
		}
		g2.dispose();
		g.drawImage(screen, 0, 0, this);
	}

	public int[] getPos() {
		return new int[] {
				(int) (xPos + ratio * 64 + mouseLocation2.getX() / scale),
				(int) (yPos + ratio * 64 + (getHeight() - mouseLocation2.getY()
						/ scale)) };
	}

	public BufferedImage getMap(int rxPos, int ryPos, int maxRX, int maxRY) {
		int[][] rgb = new int[(int) (maxRX * 64 * scale)][(int) (maxRY * 64 * scale)];
		int[] a1 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		int[] a2 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		int[] a3 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		int[] a4 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		int[] a5 = new int[(int) ((64 * maxRX) * (64 * maxRY) * scale)];
		BufferedImage img = new BufferedImage(rgb.length, rgb[0].length,
				BufferedImage.TYPE_INT_RGB);
		int[][] dataA = new int[64 * maxRX][64 * maxRY];
		int[][] dataB = new int[64 * maxRX][64 * maxRY];

		// part1 grab data

		for (int rx = 0; rx < maxRX; rx++) {
			for (int ry = 0; ry < maxRY; ry++) {
				int regionId = ((rx + rxPos) << 8) | (ry + ryPos);
				int regionX = (regionId >> 8);
				int regionY = (regionId & 0xff);
				int archiveId = Utils.getMapArchiveId(regionX, regionY);
				byte[] data = Cache.STORE.getIndexes()[5].getFile(archiveId, 3);
				if (data == null)
					continue;

				InputStream stream = new InputStream(data);
				for (int plane = 0; plane < 4; plane++) {
					for (int x = 0; x < 64; x++) {
						for (int y = 0; y < 64; y++) { // edges / street
							int value = stream.readUnsignedByte();
							if ((value & 0x1) != 0) {
								int v = stream.readUnsignedByte();
								int v2 = stream.readUnsignedSmart();
								if (plane == planePos)
									dataA[rx * 64 + x][ry * 64 + y] = v2;
							}
							if ((value & 0x2) != 0) {
								byte v = (byte) stream.readByte();
							}
							if ((value & 0x4) != 0) { // floor tiles
								int v = stream.readUnsignedSmart(); // setted to
																	// 30
								if (plane == planePos)
									dataB[rx * 64 + x][ry * 64 + y] = v;
							}
							if ((value & 0x8) != 0) {
								stream.readUnsignedByte();
							}
						}
					}
				}
			}
		}

		// part2 work data

		for (int x = -5; x < rgb.length; x++) {
			for (int y = 0; y < rgb[0].length; y++) {
				int x2 = 5 + x;
				if (x2 < rgb.length) {
					int i_98_ = ((dataB[(int) (x2 / scale)][(int) (y / scale)]) & 0x7fff);
					if (i_98_ > 0) {
						FloorDefinitions defs = FloorDefinitions
								.getFloorDefinitions(i_98_ - 1);
						a1[y] += defs.anInt6001;
						a2[y] += defs.anInt6007;
						a3[y] += defs.anInt6002;
						a4[y] += defs.anInt6008;
						a5[y]++;
					}
				}
				int x3 = x - 5;
				if (x3 >= 0) {
					int i_100_ = ((dataB[(int) (x3 / scale)][(int) (y / scale)]) & 0x7fff);
					if (i_100_ > 0) {
						FloorDefinitions defs = FloorDefinitions
								.getFloorDefinitions(i_100_ - 1);
						a1[y] -= defs.anInt6001;
						a2[y] -= defs.anInt6007;
						a3[y] -= defs.anInt6002;
						a4[y] -= defs.anInt6008;
						a5[y]--;
					}
				}
			}

			if (x >= 0) {
				int c1 = 0;
				int c2 = 0;
				int c3 = 0;
				int r2 = 0;
				int r3 = 0;
				for (int y = -5; y < rgb[0].length; y++) {
					int i_107_ = y + 5;
					if (i_107_ < rgb[0].length) {
						c1 += a1[i_107_];
						c2 += a2[i_107_];
						c3 += a3[i_107_];
						r2 += a4[i_107_];
						r3 += a5[i_107_];
					}
					int i_108_ = y - 5;
					if (i_108_ >= 0) {
						c1 -= a1[i_108_];
						c2 -= a2[i_108_];
						c3 -= a3[i_108_];
						r2 -= a4[i_108_];
						r3 -= a5[i_108_];
					}
					// sets map
					if (y >= 0 && r3 > 0) {

						rgb[x][y] = new Color(c1 / r3, c2 / r3, c3 / r3)
								.getRGB();
					}
				}
			}
		}
		for (int x = 0; x < rgb.length; x++) {
			for (int y = 0; y < rgb[0].length; y++) {
				if (((dataA[(int) (x / scale)][(int) (y / scale)]) & 0x7fff) > 0) {
					Floor2Definitions defs = Floor2Definitions
							.getFloorDefinitions(((dataA[(int) (x / scale)][(int) (y / scale)]) & 0x7fff) - 1);
					int r = defs.anInt6323 != -1 ? defs.anInt6323
							: defs.anInt6320;
					if (r == 16711935) // rs turns pink into black
						r = 0;
					int red = (r >> 16 & 0xff);
					int green = (r >> 8 & 0xff);
					int blue = (r & 0xff);

					img.setRGB(x, (rgb[0].length - 1) - y, new Color(red,
							green, blue).getRGB());
				} else {
					img.setRGB(x, (rgb[0].length - 1) - y, rgb[x][y]);
				}
			}
		}

		// set map sprites
		for (int rx = 0; rx < maxRX; rx++) {
			for (int ry = 0; ry < maxRY; ry++) {
				int regionId = ((rx + rxPos) << 8) | (ry + ryPos);
				int regionX = (regionId >> 8);
				int regionY = (regionId & 0xff);
				int archiveId = Utils.getMapArchiveId(regionX, regionY);
				byte[] data = Cache.STORE.getIndexes()[5].getFile(archiveId, 0);
				if (data == null)
					continue;
				InputStream landStream = new InputStream(data);
				int objectId = -1;
				int incr;
				while ((incr = landStream.readSmart2()) != 0) {
					objectId += incr;
					int location = 0;
					int incr2;
					while ((incr2 = landStream.readUnsignedSmart()) != 0) {
						location += incr2 - 1;
						int localX = (location >> 6 & 0x3f);
						int localY = (location & 0x3f);
						int plane = location >> 12;
						int objectData = landStream.readUnsignedByte();
						int type = objectData >> 2;
						int rotation = objectData & 0x3;
						if (localX < 0 || localX >= 64 || localY < 0
								|| localY >= 64)
							continue;
						/*
						 * int objectPlane = plane; if (mapSettings != null &&
						 * (mapSettings[1][localX][localY] & 2) == 2)
						 * objectPlane--; if (objectPlane < 0 || objectPlane >=
						 * 4 || plane < 0 || plane >= 4) continue;
						 */

						// System.out.println(spriteId);
						if (plane == planePos) {
							int mapSpriteId = ObjectDefinitions
									.getObjectDefinitions(objectId).mapSpriteId;
							if (mapSpriteId == -1)
								continue;
							int spriteId = MapSpriteDefinitions
									.getMapSpriteDefinitions(mapSpriteId).spriteId;
							if (spriteId == -1)
								continue;
							IndexedColorImageFile sprite = new IndexedColorImageFile(
									Cache.STORE, spriteId, 0);
							BufferedImage s = sprite.getImages()[0];
							int width = (int) (s.getWidth() / 2 * scale);
							int height = (int) (s.getHeight() / 2 * scale);
							if (width == 0 || height == 0)
								continue;
							/*
							 * if(width > s.getWidth() || height >
							 * s.getHeight()) { width = s.getWidth(); height =
							 * s.getHeight();
							 * 
							 * }
							 */

							img.getGraphics()
									.drawImage(
											s.getScaledInstance(width, height,
													0),
											(int) ((rx * 64 + localX) * scale),
											(int) (((maxRY * 64 - 1) - (ry * 64 + localY)) * scale),
											null);
						}

						// spawnObject(new WorldObject(objectId, type, rotation,
						// localX + regionX*64, localY + regionY*64,
						// objectPlane), objectPlane, localX, localY, true);
					}
				}
			}
		}

		return img;

	}

	public static final int getV(int i, int i_1_, int i_2_) {
		if (i_2_ > 243)
			i_1_ >>= 4;
		else if (i_2_ > 217)
			i_1_ >>= 3;
		else if (i_2_ > 192)
			i_1_ >>= 2;
		else if (i_2_ > 179)
			i_1_ >>= 1;
		return (i_2_ >> 1) + (((i & 0xff) >> 2 << 10) + (i_1_ >> 5 << 7));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mouseLocation = e.getPoint();
		int[] pos = getPos();
		StringSelection selection = new StringSelection(pos[0] + " " + pos[1]
				+ " " + planePos);

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
		e.consume();
	}

	private Point mouseLocation;

	private Point mouseLocation2;

	@Override
	public void mousePressed(MouseEvent e) {
		mouseLocation = e.getPoint();
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		drag(e.getPoint());
		e.consume();
	}

	public void drag(Point newLoc) {
		int diffX = (int) ((mouseLocation.x - newLoc.x) / scale);
		int diffY = (int) ((mouseLocation.y - newLoc.y) / scale);
		if (diffX != 0 || diffY != 0) {
			xPos += diffX;
			yPos -= diffY;
			mouseLocation = newLoc;
			checkLoad();
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		drag(e.getPoint());
		e.consume();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseLocation2 = e.getPoint();
		this.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (!loading) {
			if (e.getUnitsToScroll() > 0) {
				if (e.isControlDown())
					planePos = (planePos - 1) & 0x3;
				else
					scale /= 1.5;

			} else {
				if (e.isControlDown())
					planePos = (planePos + 1) & 0x3;
				else {
					scale *= 1.5;
				}
			}
			forceNoRefresh = true;
			checkLoad();
		}

	}

}
