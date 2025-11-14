package comms.gc;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import comms.gc.handlers.MissionsHandler;
import comms.rovertelemetry.RoverTelemetry;
import comms.telemetry.MissionTelemetry;
import comms.gc.handlers.RoversHandler;
import org.w3c.dom.Document;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MsHTTP{
    private static final int PORT = 8080;
    private final HttpServer server;

    // ID do rover como key em ambos os maps
    private static final ConcurrentHashMap<Integer, RoverTelemetry> rovers = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, MissionTelemetry> missions = new ConcurrentHashMap<>();

    public MsHTTP() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/api/rovers", new RoversHandler(this));
            server.createContext("/api/missions", new MissionsHandler(this));

            server.setExecutor(Executors.newCachedThreadPool());

            server.start();
            System.out.println("HTTP API started on port: " + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addMissionTelemetry(MissionTelemetry telem){
        missions.put(telem.roverID, telem);
    }

    public List<MissionTelemetry> getMissions(){
        return new ArrayList<>(missions.values());
    }
    public List<RoverTelemetry> getRovers(){
        return new ArrayList<>(rovers.values());
    }

    public static void sendXML(HttpExchange exchange, Document doc) throws IOException {
        try {
            String xml = xmlToString(doc);
            System.out.println(xml);
            exchange.getResponseHeaders().add("Content-Type", "application/xml; charset=UTF-8");
            exchange.sendResponseHeaders(200, xml.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(xml.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String xmlToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return writer.toString();
    }

}

