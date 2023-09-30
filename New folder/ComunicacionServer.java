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


class ClienteHandler extends Thread {
    private Socket conexionSock;
    private ComunicacionServer comServer;
    private DataOutputStream salida;

    public ClienteHandler(Socket conexionSock, ComunicacionServer comServer) {
        this.conexionSock = conexionSock;
        this.comServer = comServer;
        try {
            this.salida = new DataOutputStream(conexionSock.getOutputStream());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String msg) {
        try {
            salida.writeUTF(msg);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            DataInputStream entrada = new DataInputStream(conexionSock.getInputStream());
            String dataMsg;
            while (true) {
                dataMsg = entrada.readUTF();
                comServer.anadirMensaje(dataMsg);
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



public class ComunicacionServer {
    private JTextArea textoPanel;

    public ComunicacionServer() {
        JFrame ventana = new JFrame();
        ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ventana.setSize(400, 400);
        ventana.setLayout(null);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panelServer = new JPanel();
        panelServer.setLayout(null);
        panelServer.setSize(400, 400);
        panelServer.setBackground(Color.white);
        panelServer.setVisible(true);

        ventana.add(panelServer);

        textoPanel = new JTextArea(null, null, 20, 20);
        textoPanel.setBounds(100, 100, 400, 400);
        textoPanel.setEditable(false);
        panelServer.add(textoPanel);

        ventana.setVisible(true);
    }

    public void anadirMensaje(String msg) {
        textoPanel.append(msg + "\n");
    }

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

    public synchronized void agregarClienteHandler(ClienteHandler gestor) {
        listaClientes.add(gestor);
    }

    public synchronized void eliminarClienteHandler(ClienteHandler gestor) {
        listaClientes.remove(gestor);
    }

    public synchronized void enviarATodos(String msg) {
        for(ClienteHandler gestor : listaClientes) {
            gestor.enviarMensaje(msg);
            
        }
    }
}
