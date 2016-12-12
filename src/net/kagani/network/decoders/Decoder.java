package net.kagani.network.decoders;

import net.kagani.network.Session;
import net.kagani.stream.InputStream;

public abstract class Decoder {

	protected Session session;

	public Decoder(Session session) {
		this.session = session;
	}

	public abstract int decode(InputStream stream);

}