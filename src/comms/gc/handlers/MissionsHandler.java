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
    private boolean all = false;

    public MissionsHandler(MsHTTP httpServer){
        this.server = httpServer;
    }

    public MissionsHandler(MsHTTP httpServer, boolean isAll){
        this.server = httpServer;
        this.all = isAll;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        ArrayList<MissionTelemetry> missions = null;
        if (all){
            missions = (ArrayList<MissionTelemetry>) server.getAllMissions();
        } else {
            missions = (ArrayList<MissionTelemetry>) server.getMissions();
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            Element root = doc.createElement("missions");
            doc.appendChild(root);
            for (MissionTelemetry mission : missions) {
                Element e = mission.getElement(doc);
                root.appendChild(e);
            }

            MsHTTP.sendXML(httpExchange, doc);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
