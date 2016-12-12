package net.kagani.network;

import java.net.InetSocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;

import net.kagani.game.player.Player;
import net.kagani.grab.Grab;
import net.kagani.network.decoders.ClientPacketsDecoder;
import net.kagani.network.decoders.Decoder;
import net.kagani.network.decoders.GrabPacketsDecoder;
import net.kagani.network.decoders.LoginPacketsDecoder;
import net.kagani.network.decoders.WorldLoginPacketsDecoder;
import net.kagani.network.decoders.WorldPacketsDecoder;
import net.kagani.network.encoders.Encoder;
import net.kagani.network.encoders.GrabPacketsEncoder;
import net.kagani.network.encoders.LoginPacketsEncoder;
import net.kagani.network.encoders.WorldPacketsEncoder;
import net.kagani.stream.OutputStream;

public class Session {

	private Channel channel;
	private Decoder decoder;
	private Encoder encoder;

	// hammershield mod
	protected int iplen = -1;
	protected byte[] ipdata = null;
	protected InetSocketAddress client = null;
	// ----------------

	protected byte[] buffer = new byte[0];
	protected int bufferOffset = 0;

	public Session(Channel channel) {
		this.channel = channel;
		setDecoder(0);
	}

	public final ChannelFuture write(OutputStream outStream) {
		if (outStream == null || !channel.isConnected())
			return null;
		return channel.write(ChannelBuffers.copiedBuffer(outStream.getBuffer(), 0, outStream.getOffset()));
	}

	public final ChannelFuture write(ChannelBuffer outStream) {
		if (outStream == null || !channel.isConnected())
			return null;
		return channel.write(outStream);
	}

	public final Channel getChannel() {
		return channel;
	}

	public final Decoder getDecoder() {
		return decoder;
	}

	public GrabPacketsDecoder getGrabPacketsDecoder() {
		return (GrabPacketsDecoder) decoder;
	}

	public final Encoder getEncoder() {
		return encoder;
	}

	public final void setDecoder(int stage) {
		setDecoder(stage, null);
	}

	public final void setDecoder(int stage, Object attachement) {
		switch (stage) {
		case 0:
			decoder = new ClientPacketsDecoder(this);
			break;
		case 1:
			decoder = new GrabPacketsDecoder(this, (Grab) attachement);
			break;
		case 2:
			decoder = new LoginPacketsDecoder(this);
			break;
		case 3:
			decoder = new WorldPacketsDecoder(this, (Player) attachement);
			break;
		case 4:
			decoder = new WorldLoginPacketsDecoder(this, (Player) attachement);
			break;
		case -1:
		default:
			decoder = null;
			break;
		}
	}

	public final void setEncoder(int stage) {
		setEncoder(stage, null);
	}

	public final void setEncoder(int stage, Object attachement) {
		switch (stage) {
		case 0:
			encoder = new GrabPacketsEncoder(this);
			break;
		case 1:
			encoder = new LoginPacketsEncoder(this);
			break;
		case 2:
			encoder = new WorldPacketsEncoder(this, (Player) attachement);
			break;
		case -1:
		default:
			encoder = null;
			break;
		}
	}

	public LoginPacketsEncoder getLoginPackets() {
		return (LoginPacketsEncoder) encoder;
	}

	public GrabPacketsEncoder getGrabPackets() {
		return (GrabPacketsEncoder) encoder;
	}

	public WorldPacketsEncoder getWorldPackets() {
		return (WorldPacketsEncoder) encoder;
	}

	public String getIP() {
		/*
		 * if (Settings.AR_PROTECTION) { if (client == null) return null; return
		 * client.getAddress().getHostAddress(); } else {
		 */
		if (channel == null || !(channel.getRemoteAddress() instanceof InetSocketAddress))
			return null;
		InetSocketAddress addr = (InetSocketAddress) channel.getRemoteAddress();
		return addr.getAddress().getHostAddress();
		// }
	}

	public String getLocalAddress() {
		return channel.getLocalAddress().toString();
	}
}
