package comms;

import java.io.*;
import java.net.*;

public class RoverTS {
    // O Socket do Cliente para a conexão TCP
    private Socket socket; 
    private BufferedReader in;
    private PrintWriter out;

    // Endereço do Servidor (Nave-Mãe)
    private static final String MOTHERSHIP_HOST = "localhost";
    // Certifique-se que o seu MothershipTS (Servidor) está a escutar nesta porta
    private static final int MOTHERSHIP_PORT = 4000;
    
    private String roverId;

    /**
     * Construtor do Rover (Cliente). Tenta conectar ao servidor.
     */
    public RoverTS(String roverId) throws IOException {
        this.roverId = roverId;
        System.out.println("RoverTS [" + roverId + "]: Tentando conectar à Mothership em " 
                                + MOTHERSHIP_HOST + ":" + MOTHERSHIP_PORT);

        // Ação do Cliente: Cria e conecta o Socket
        socket = new Socket(MOTHERSHIP_HOST, MOTHERSHIP_PORT);
        
        System.out.println("RoverTS [" + roverId + "]: Conexão estabelecida.");

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        // Envia a mensagem de identificação obrigatória
        out.println("ID:" + this.roverId);
        
        // Espera a confirmação de registro
        String confirmacao = in.readLine();
        if (confirmacao != null && confirmacao.equals("REGISTADO")) {
            System.out.println("RoverTS [" + roverId + "]: Registado com sucesso!");
        } else {
            throw new IOException("Falha ao registar com a Mothership.");
        }
    }

    
    public void sendMsg(String msg) {
        if (out != null) {
            out.println(msg);
            // System.out.println("RoverTS [" + roverId + "]: Telemetria enviada: " + msg);
        }
    }

    
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
        System.out.println("RoverTS [" + roverId + "]: Conexão fechada.");
    }
    
    // --- PONTO DE ENTRADA PARA TESTE ---
    // Este método permite que você execute este ficheiro diretamente
    
    /**
     * Ponto de entrada (main) para testar o RoverTS como um cliente.
     * Inicia um rover e começa a enviar telemetria periodicamente.
     */
    public static void main(String[] args) {
        try {
            // 1. Cria e conecta o rover
            RoverTS rover = new RoverTS("R1"); // Tenta conectar na porta 4000

            // 2. Simulação de envio periódico de dados
            int posX = 10;
            while (true) {
                posX++;
                String telemetria = "POS:" + posX + ",15;ESTADO:OPERACIONAL";
                rover.sendMsg(telemetria);
                
                // Pausa por 2 segundos
                Thread.sleep(2000); 
            }

        } catch (IOException e) {
            System.err.println("Erro de conexão do Rover R1: " + e.getMessage());
            System.err.println("Verifique se o MothershipTS (Servidor) está em execução na porta " + MOTHERSHIP_PORT);
        } catch (InterruptedException e) {
            System.err.println("Thread do Rover R1 interrompida.");
        }
    }
}