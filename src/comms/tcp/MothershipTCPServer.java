package comms.tcp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MothershipTCPServer extends Thread{
    private final ServerSocket serverSocket;

    ExecutorService threadPool = Executors.newCachedThreadPool();
    BlockingQueue<ByteBuffer> packetsBuffer = new LinkedBlockingQueue<>();

    public MothershipTCPServer(int port){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(){
        try {
            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Rover just connected! " + clientSocket.getInetAddress());
                threadPool.submit(new RoverTCPHandler(clientSocket, packetsBuffer));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ByteBuffer> flushPackets(){
        try{
            ArrayList<ByteBuffer> packets = new ArrayList<>();
            packetsBuffer.drainTo(packets);
            return packets;
        } finally {
            packetsBuffer.clear();
        }
    }
}
