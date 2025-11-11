package comms.gc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

class ServerHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "Hello, this is a simple HTTP server response!";
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

public class MsHTTP {
    private static final int PORT = 8080;
    private final HttpServer server;

    public MsHTTP() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", new ServerHandler());

            server.setExecutor(null);
            server.start();
            System.out.println("Server started on port " + PORT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void main(String[] args){
        MsHTTP httpServer = new MsHTTP();
    }
}
