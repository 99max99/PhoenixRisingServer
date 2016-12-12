package net.kagani.network;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;

import net.kagani.Settings;
import net.kagani.stream.InputStream;
import net.kagani.utils.Logger;

public final class GameChannelsManager extends SimpleChannelHandler {

	private static ChannelGroup channels;
	private static ServerBootstrap bootstrap;
	private static ExecutorService workerExecutor, bossExecutor;

	public static final void init() {
		channels = new DefaultChannelGroup();
		workerExecutor = new OrderedMemoryAwareThreadPoolExecutor(2, 0, 0);// Executors.newFixedThreadPool(2);
		bossExecutor = new OrderedMemoryAwareThreadPoolExecutor(1, 0, 0);// Executors.newSingleThreadExecutor();
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				bossExecutor, workerExecutor, 2));
		bootstrap.getPipeline().addLast("handler", new GameChannelsManager());

		bootstrap.setOption("reuseAddress", true);
		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.sendBufferSize", Settings.WRITE_BUFFER_SIZE);
		bootstrap.setOption("child.receiveBufferSize",
				Settings.READ_BUFFER_SIZE);

		bootstrap.bind(new InetSocketAddress(Settings.GAME_ADDRESS_BASE
				.getAddress(), Settings.GAME_ADDRESS_BASE.getPort()
				+ Settings.WORLD_ID));
	}

	public static final void shutdown() {
		channels.close().awaitUninterruptibly();
		bootstrap.releaseExternalResources();
		workerExecutor.shutdown();
		bossExecutor.shutdown();
	}

	@Override
	public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) {
		channels.add(e.getChannel());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		channels.remove(e.getChannel());

	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		ctx.setAttachment(new Session(e.getChannel()));
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) {
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
		if (!(e.getMessage() instanceof ChannelBuffer)) {
			return;
		}
		ChannelBuffer buf = (ChannelBuffer) e.getMessage();
		Object sessionObject = ctx.getAttachment();
		if (sessionObject != null && sessionObject instanceof Session) {
			Session session = (Session) sessionObject;
			/*
			 * if (Settings.AR_PROTECTION && session.client == null) { if
			 * (session.iplen == -1 && buf.readableBytes() > 0) { session.iplen
			 * = buf.readByte() & 0xFF; session.ipdata = new
			 * byte[session.iplen]; } if (session.iplen > 0 &&
			 * buf.readableBytes() > 0) { int pos = session.ipdata.length -
			 * session.iplen; int amt = Math.min(buf.readableBytes(),
			 * session.iplen); for (int i = 0; i < amt; i++)
			 * session.ipdata[pos++] = buf.readByte(); session.iplen -= amt; }
			 * if (session.iplen == 0) { String[] ip = new
			 * String(session.ipdata).split("\\:"); session.client = new
			 * InetSocketAddress(ip[0], Integer.parseInt(ip[1])); }
			 * 
			 * if (session.client == null || buf.readableBytes() < 1) return; }
			 */

			if (session.getDecoder() == null) {
				return;
			}

			byte[] b = new byte[(session.buffer.length - session.bufferOffset)
					+ buf.readableBytes()];
			if ((session.buffer.length - session.bufferOffset) > 0)
				System.arraycopy(session.buffer, session.bufferOffset, b, 0,
						session.buffer.length - session.bufferOffset);
			buf.readBytes(b, session.buffer.length - session.bufferOffset,
					b.length - (session.buffer.length - session.bufferOffset));

			session.buffer = b;
			session.bufferOffset = 0;

			try {
				InputStream is = new InputStream(b);
				session.bufferOffset = session.getDecoder().decode(is);
				if (session.bufferOffset < 0) { // drop
					session.buffer = new byte[0];
					session.bufferOffset = 0;
				}
			} catch (Throwable er) {
				Logger.handle(er);
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent ee)
			throws Exception {

	}

}
