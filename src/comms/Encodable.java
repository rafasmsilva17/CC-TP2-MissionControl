package comms;

import java.nio.ByteBuffer;

public interface Encodable {

    TLVPacket getEncodeData();
}
