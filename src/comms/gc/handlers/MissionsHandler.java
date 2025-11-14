package comms.gc.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import comms.gc.MsHTTP;
import comms.telemetry.MissionTelemetry;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class MissionsHandler implements HttpHandler {
    private final MsHTTP server;

    public MissionsHandler(MsHTTP httpServer){
        this.server = httpServer;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        ArrayList<MissionTelemetry> missions = (ArrayList<MissionTelemetry>) server.getMissions();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element root = doc.createElement("missions");
            doc.appendChild(root);
            for (MissionTelemetry mission : missions) {
                Element e = doc.createElement("mission");
                ArrayList<Attr> attributes = (ArrayList<Attr>) mission.getAttributes();
                for (Attr attribute : attributes) {
                    Attr newAttr = doc.createAttribute(attribute.getName());
                    newAttr.setValue(attribute.getValue());
                    e.setAttributeNode(newAttr);
                }
                root.appendChild(e);
            }

            MsHTTP.sendXML(httpExchange, doc);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
