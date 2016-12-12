package net.kagani.network.decoders;

import net.kagani.grab.Grab;
import net.kagani.network.Session;
import net.kagani.stream.InputStream;

public final class GrabPacketsDecoder extends Decoder {

	private Grab grab;

	public GrabPacketsDecoder(Session connection, Grab attachement) {
		super(connection);
		grab = attachement;
	}

	@Override
	public final int decode(InputStream stream) {
		while (stream.getRemaining() >= 6) {
			int packetId = stream.readUnsignedByte();
			switch (packetId) {
			case 0:
			case 1:
				grab.addArchive(stream.readUnsignedByte(), stream.readInt(),
						packetId == 1);
				break;
			case 2:
			case 3:
				grab.setLoggedIn(packetId == 2);
				stream.skip(5);
				break;
			case 4:
				session.getGrabPackets().setEncryptionValue(
						stream.readUnsignedByte());
				stream.skip(4);
				break;
			case 6:
				grab.init();
				stream.skip(5);
				break;
			case 7:
			default:
				grab.finish();
				stream.skip(5);
				break;
			}
		}
		return stream.getOffset();
	}
}
