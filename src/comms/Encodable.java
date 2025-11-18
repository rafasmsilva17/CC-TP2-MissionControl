package comms;

import comms.packets.common.TLVPacket;

public interface Encodable {

    TLVPacket getEncodeData();
}
