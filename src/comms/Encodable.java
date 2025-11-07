package comms;

import comms.packets.TLVPacket;

public interface Encodable {

    TLVPacket getEncodeData();
}
