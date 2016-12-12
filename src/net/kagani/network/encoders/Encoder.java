package net.kagani.network.encoders;

import net.kagani.network.Session;

public abstract class Encoder {

	protected Session session;

	public Encoder(Session session) {
		this.session = session;
	}

}
