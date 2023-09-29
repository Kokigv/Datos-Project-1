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

class Queue<T> {
    private Nodo<T> head;
    private Nodo<T> rear; // Necesitamos un nodo final para encolar de manera eficiente

    public Queue() {
        this.head = null;
        this.rear = null;
    }

    // Encolar un nuevo elemento al final
    public void add (T data) {
        Nodo<T> newNode = new Nodo<>(data);
        if (head == null) {
            head = newNode;
            rear = newNode;
            return;
        }

        rear.next = newNode;
        rear = newNode;
    }

    // Desencolar el elemento del inicio
    public T delet() {
        if (head == null) {
            return null; // La cola está vacía
        }

        T data = head.data;
        head = head.next;
        if (head == null) {
            rear = null; // Si desencolamos el último elemento, también restablecemos la cola
        }
        return data;
    }

    // Ver el elemento del frente sin desencolar
    public T enseñar() {
        if (head == null) {
            return null; // La cola está vacía
        }
        return head.data;
    }

    // Verificar si la cola está vacía
    public boolean isEmpty() {
        return head == null;
    }

    // Obtener todos los datos como una lista para facilitar el manejo
    public List<T> end() {
        List<T> list = new ArrayList<>();
        Nodo<T> current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
}

public class ComunicacionServer {
    private JTextArea textoPanel;

    public ComunicacionServer() {
        JFrame ventana = new JFrame();
        ventana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        ventana.setSize(400, 400);
        ventana.setLayout(null);

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

        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                int respuesta = JOptionPane.showConfirmDialog(null, "¿Seguro que deseas salir?", "Confirmación", JOptionPane.YES_NO_OPTION);
                if (respuesta == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

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
