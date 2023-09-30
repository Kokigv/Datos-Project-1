import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * ClienteHandler se encarga de manejar la comunicación con un cliente conectado.
 * Esto incluye recibir mensajes del cliente y enviar mensajes al cliente.
 */
class ClienteHandler extends Thread {
    private Socket conexionSock;
    private ComunicacionServer comServer;
    private DataOutputStream salida;

    /**
     * Constructor de ClienteHandler.
     *
     * @param conexionSock el socket para la comunicación con el cliente.
     * @param comServer    referencia al servidor principal que gestiona la comunicación.
     */
    public ClienteHandler(Socket conexionSock, ComunicacionServer comServer) {
        this.conexionSock = conexionSock;
        this.comServer = comServer;
        try {
            this.salida = new DataOutputStream(conexionSock.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envía un mensaje al cliente conectado.
     *
     * @param msg el mensaje a enviar.
     */
    public void enviarMensaje(String msg) {
        try {
            salida.writeUTF(msg);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * El método de ejecución principal para el hilo. Lee continuamente mensajes del cliente
     * y los envía a todos los clientes conectados.
     */
    public void run() {
        try {
            DataInputStream entrada = new DataInputStream(conexionSock.getInputStream());
            String dataMsg;
            while (true) {
                dataMsg = entrada.readUTF();
                comServer.enviarATodos(dataMsg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                conexionSock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            comServer.eliminarClienteHandler(this);
        }
    }
}

/**
 * ComunicacionServer configura un servidor que escucha conexiones de clientes y gestiona la comunicación
 * entre ellos. También muestra una ventana de interfaz gráfica para mostrar las actividades del servidor.
 */
public class ComunicacionServer {
    private JTextArea textoPanel;

    /**
     * Constructor de ComunicacionServer. Inicializa la ventana de interfaz gráfica.
     */
    public ComunicacionServer() {
        JFrame ventana = new JFrame();
        ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ventana.setSize(200, 200);
        ventana.setLayout(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelServer = new JPanel();
        panelServer.setLayout(null);
        panelServer.setSize(200, 200);
        panelServer.setBackground(Color.white);
        panelServer.setVisible(true);

        ventana.add(panelServer);

        textoPanel = new JTextArea(null, null, 20, 20);
        textoPanel.setBounds(100, 100, 200, 200);
        textoPanel.setEditable(false);
        panelServer.add(textoPanel);

        ventana.setVisible(true);
    }

    /**
     * Punto de entrada del programa. Crea una instancia de ComunicacionServer
     * y comienza a escuchar conexiones de clientes.
     *
     * @param args argumentos de línea de comando.
     */
    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> {
            ComunicacionServer servidor = new ComunicacionServer();
            new Thread(() -> {
                try {
                    ServerSocket serverSock = new ServerSocket(9527);
                    while (true) {
                        Socket conexionSock = serverSock.accept();
                        ClienteHandler gestor = new ClienteHandler(conexionSock, servidor);
                        servidor.agregarClienteHandler(gestor);
                        gestor.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    private List<ClienteHandler> listaClientes = new ArrayList<>();

    /**
     * Agrega un nuevo ClienteHandler a la lista de conexiones activas.
     *
     * @param gestor el ClienteHandler a agregar.
     */
    public synchronized void agregarClienteHandler(ClienteHandler gestor) {
        listaClientes.add(gestor);
    }

    /**
     * Elimina un ClienteHandler de la lista de conexiones activas.
     *
     * @param gestor el ClienteHandler a eliminar.
     */
    public synchronized void eliminarClienteHandler(ClienteHandler gestor) {
        listaClientes.remove(gestor);
    }

    /**
     * Envía un mensaje a todos los clientes conectados.
     *
     * @param msg el mensaje a enviar.
     */
    public synchronized void enviarATodos(String msg) {
        for(ClienteHandler gestor : listaClientes) {
            gestor.enviarMensaje(msg);
        }
    }
}
