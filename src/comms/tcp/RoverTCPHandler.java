package comms.tcp;

import comms.Encoder;
import comms.packets.common.PacketType;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class RoverTCPHandler implements Runnable{

    private final Socket socket;
    private final BlockingQueue<ByteBuffer> packetQueue;

    byte[] buf = new byte[1024];

    public RoverTCPHandler(Socket socket, BlockingQueue<ByteBuffer> queue){
        this.socket = socket;
        packetQueue = queue;
    }


    @Override
    public void run() {
        try(DataInputStream in = new DataInputStream(socket.getInputStream())) {
            while(!socket.isClosed()){
                int bytesRead = in.read(buf);
                if (bytesRead > 0){
                    ByteBuffer buffer = ByteBuffer.wrap(Arrays.copyOf(buf, bytesRead));
                    PacketType t = PacketType.fromByte(Encoder.decodeByte(buffer));
                    if (t != PacketType.ROVERTELEMETRY) continue;
                    packetQueue.add(buffer);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
