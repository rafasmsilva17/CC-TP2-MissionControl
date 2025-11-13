package comms.gc.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import comms.gc.MsHTTP;
import comms.rovertelemetry.RoverTelemetry;
import comms.telemetry.MissionTelemetry;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class RoversHandler implements HttpHandler {
    private final MsHTTP server;

    public RoversHandler(MsHTTP httpServer){
        this.server = httpServer;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        ArrayList<RoverTelemetry> rovers = (ArrayList<RoverTelemetry>) server.getRovers();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element root = doc.createElement("rovers");
            doc.appendChild(root);
            for (RoverTelemetry rover : rovers ) {
                Element e = doc.createElement("rover");
                ArrayList<Attr> attributes = (ArrayList<Attr>) rover.getAttributes();
                for (Attr attribute : attributes) {
                    e.setAttributeNode(attribute);
                }
                root.appendChild(e);
            }

            MsHTTP.sendXML(httpExchange, doc);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
