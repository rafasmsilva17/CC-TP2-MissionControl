package comms.missionlink;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class MothershipTS {

    // Porta em que a Nave-Mãe (Servidor) escuta
    private static final int TS_PORT = 4000;
    
    // Um mapa para rastrear os handlers de cada rover (para organização)
    // Usamos ConcurrentHashMap por ser seguro para threads
    private ConcurrentHashMap<String, RoverHandler> rovers = new ConcurrentHashMap<>();

    public MothershipTS() {
        System.out.println("MothershipTS (SERVIDOR): Iniciando...");
    }

    /**
     * Inicia o servidor para escutar por conexões de rovers.
     */
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(TS_PORT)) {
            System.out.println("MothershipTS: Aguardando conexões de rovers na porta " + TS_PORT);

            // Loop infinito para aceitar novas conexões
            while (true) {
                // Bloqueia até que um novo rover (Cliente) se conecte
                Socket roverSocket = serverSocket.accept(); 
                System.out.println("MothershipTS: Novo rover conectado: " + roverSocket.getInetAddress());

                // Cria um novo handler para este rover em uma thread separada
                RoverHandler handler = new RoverHandler(roverSocket, this);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("MothershipTS: Erro no ServerSocket: " + e.getMessage());
        }
    }

    /**
     * Método chamado pelo RoverHandler para registrar o rover após receber seu ID.
     */
    public void registerRover(String roverId, RoverHandler handler) {
        rovers.put(roverId, handler);
        System.out.println("MothershipTS: Rover " + roverId + " registrado.");
    }

    /**
     * Método chamado pelo RoverHandler para processar a telemetria.
     */
    public void processTelemetry(String roverId, String data) {
        // Aqui você "organiza corretamente os dados"
        System.out.println("[TELEMETRIA de " + roverId + "]: " + data);
    }
    
    /**
     * Ponto de entrada para iniciar o servidor.
     */
    public static void main(String[] args) {
        MothershipTS server = new MothershipTS();
        server.startServer(); // Inicia o servidor
    }
}

/**
 * Classe interna que gerencia a conexão de UM rover específico.
 * Implementa Runnable para ser executada em sua própria thread.
 */
class RoverHandler implements Runnable {
    private Socket socket;
    private MothershipTS server; // Referência ao servidor principal
    private PrintWriter out;
    private BufferedReader in;
    private String roverId = null;

    public RoverHandler(Socket socket, MothershipTS server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 1. Processo de "Handshake": Esperar o Rover enviar seu ID
            // O requisito diz que os dados incluem "identificador do rover"
            String idMessage = in.readLine();
            if (idMessage != null && idMessage.startsWith("ID:")) {
                this.roverId = idMessage.substring(3);
                server.registerRover(this.roverId, this);
                out.println("REGISTADO"); // Envia confirmação
            } else {
                System.err.println("Rover não enviou ID. Fechando conexão.");
                socket.close();
                return;
            }

            // 2. Loop de Telemetria: Ler dados periódicos do rover
            String telemetryData;
            while ((telemetryData = in.readLine()) != null) {
                // Envia os dados para o servidor principal processar
                server.processTelemetry(this.roverId, telemetryData);
            }

        } catch (IOException e) {
            System.out.println("MothershipTS: Conexão com " 
                + (roverId != null ? roverId : "Rover desconhecido") + " perdida: " + e.getMessage());
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}