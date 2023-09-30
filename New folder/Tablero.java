import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.json.JSONObject;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import org.json.JSONException;


/**
 * La clase Tablero representa un juego de Dots and Boxes con una interfaz gráfica
 * de usuario implementada en Swing. Permite a los jugadores hacer clic en puntos para
 * crear líneas y formar cuadrados.
 * La clase contiene una clase interna llamada DotPanel que extiende JPanel y se utiliza
 * para representar gráficamente el juego. También utiliza clases como LinkedList, Dot,
 * Line y Square para administrar y representar los elementos del juego.
 * El juego se puede jugar en modo local o en modo remoto, donde los movimientos se
 * envían a través de sockets a otro jugador.
 */
public class Tablero {

    JFrame window;
    LinkedList dotList;
    LinkedList lineList;
    LinkedList squareList;
    DotPanel dotPanel;
    LinkedList detectedSquares;
    private boolean isLocalAction = true;



 /**
  * Constructor de la clase Tablero. Inicializa la ventana de juego y crea
  * una cuadrícula de puntos en la interfaz.
  */
    Tablero() {
        window = new JFrame("Dots and Boxes");
        window.setSize(1000, 1000);
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true);
        window.setLocationRelativeTo(null);
        window.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Images/iconImage.png")));
        dotList = new LinkedList();
        dotPanel = new DotPanel(dotList);
        lineList = new LinkedList();
        squareList = new LinkedList();
        detectedSquares = new LinkedList();
        dotPanel.setSize(1000, 1000);
        dotPanel.setBackground(Color.white);
        window.add(dotPanel);
        window.add(dotPanel, BorderLayout.CENTER);

        // Creación de puntos en la cuadrícula
        for (int i = 0; i < 10; i++) { 
            for (int j = 0; j < 10; j++) {
                Dot newDot = new Dot(i * 100, j * 100);
                dotList.insertFirst(newDot);
            }
        }
    // Iniciar un hilo para la comunicación remota
    ClientRunner ClientRunner = new ClientRunner(dotPanel);
    ClientRunner.start();
    
    }
    
    /**
     * Clase interna DotPanel que extiende JPanel y se utiliza para representar
     * gráficamente el juego de Dots and Boxes.
     */
    class DotPanel extends JPanel {
        private LinkedList dotList;
        private Dot dotSeleccionado1;

        /**
         * Constructor de la clase DotPanel que recibe la lista de puntos como argumento.
         * Configura el panel y agrega un MouseListener para manejar los clics de los jugadores.
         */
        public DotPanel(LinkedList dotList) {
            this.dotList = dotList;
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int x = e.getX();
                    int y = e.getY();
                    Dot clickedDot = dotList.findDot(x, y);
                    if (clickedDot != null) {
                        if (dotSeleccionado1 == null) {
                            dotSeleccionado1 = clickedDot;
                        } else {
                            if (Adjacent(dotSeleccionado1, clickedDot) && !lineExists(dotSeleccionado1, clickedDot)) {
                                Line line = new Line(dotSeleccionado1, clickedDot);
                                lineList.insertFirst(line); 
                                isLocalAction = true; 
                                Msenvio(dotSeleccionado1, clickedDot);  
                                dotSeleccionado1 = null;
                                if (isLocalAction) {
                                    detectSquares(); 
                                    
                                }
                                repaint(); 
                            } else {
                                System.out.println("No se puede agregar la línea");
                                dotSeleccionado1 = null;
                            }
                        }
                    }
                }
            });
        }
        
    
        private boolean Adjacent(Dot dot1, Dot dot2) {
            return Math.abs(dot1.getX() - dot2.getX()) == 100 && dot1.getY() == dot2.getY() ||
                    Math.abs(dot1.getY() - dot2.getY()) == 100 && dot1.getX() == dot2.getX();
        }

        private boolean lineExists(Dot dot1, Dot dot2) {
            Node current = lineList.getHead();
            while (current != null) {
                Line line = (Line) current.getData();
                Dot lineDot1 = line.getDot1();
                Dot lineDot2 = line.getDot2();
                if ((lineDot1 == dot1 && lineDot2 == dot2) || (lineDot1 == dot2 && lineDot2 == dot1)) {
                    return true;
                }
                
                current = current.getNext();
            }
            return false;
            
        }

        private void detectSquares() {
            Node current = dotList.getHead();
        
            while (current != null) {
                Dot dot = (Dot) current.getData();
                int x = dot.getX();
                int y = dot.getY();
                Dot rightDot = dotList.findDot(x + 100, y);
                Dot bottomDot = dotList.findDot(x, y + 100);
                Dot bottomRightDot = dotList.findDot(x + 100, y + 100);
        
                if (rightDot != null && bottomDot != null && bottomRightDot != null) {
                    if (lineExists(dot, rightDot) && lineExists(rightDot, bottomRightDot) &&
                        lineExists(bottomRightDot, bottomDot) && lineExists(bottomDot, dot)) {
        
                        Square square = new Square(dot, rightDot, bottomDot, bottomRightDot);
        
                        if (!squareExists(square)) {
                            squareList.insertFirst(square);
                            detectedSquares.insertFirst(square);
                            drawSquare(dot);
                            System.out.println("Tamaño de detectedSquares: " + detectedSquares.size());
                           
                        }
                    }
                }
                current = current.getNext();
            }
        }
        
        private void drawSquare(Dot upperLeft) {
            int x1 = upperLeft.getX();
            int y1 = upperLeft.getY();
            // Aquí puedes agregar el código para dibujar el cuadrado si es necesario.
            repaint();
        }
        public void Conect2(int x1, int y1, int x2, int y2) {
            Dot dot1 = dotList.findDot(x1, y1);
            Dot dot2 = dotList.findDot(x2, y2);
        
            if (dot1 != null && dot2 != null) {
                lineList.insertFirst(new Line(dot1, dot2));
        
                // Detecta cuadrados solo si es una acción local.
                if (isLocalAction) {
                    dotPanel.detectSquares(); 
                }
        
                repaint();
            }
        }
        

        private boolean squareExists(Square square) {
            Node current = detectedSquares.getHead();
            while (current != null) {
                Square existingSquare = (Square) current.getData();
                // Compare squares to check if they are equal
                if (areSquaresEqual(existingSquare, square)) {
                    return true; 
                }
                current = current.getNext();
            }
            return false; 
        }
        
        private boolean areSquaresEqual(Square square1, Square square2) {
            return (square1.getUpperLeft() == square2.getUpperLeft() &&
                    square1.getUpperRight() == square2.getUpperRight() &&
                    square1.getLowerLeft() == square2.getLowerLeft() &&
                    square1.getLowerRight() == square2.getLowerRight());
        }

        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            Dot clickedDot = dotList.findDot(x, y);
            if (clickedDot != null) {
                if (dotSeleccionado1 == null) {
                    dotSeleccionado1 = clickedDot;
                } else {
                    if (Adjacent(dotSeleccionado1, clickedDot) && !lineExists(dotSeleccionado1, clickedDot)) {
                        
                        Line line = new Line(dotSeleccionado1, clickedDot);
                        dotList.insertFirst(line);
                        dotSeleccionado1 = null;
                        detectSquares();
                       
                    } else {
                        System.out.println("No se puede agregar la línea");
                        dotSeleccionado1 = null;
                    }
                }
                repaint();
            }
        }
        

     
        /**
         * Método para enviar información sobre un movimiento a través de sockets (cuadrados).
         */
        private void Msenvio(Dot p1, Dot p2) {
        try {
        JSONObject ObjJason = new JSONObject();
        ObjJason.put("x1", p1.getX());
        ObjJason.put("y1", p1.getY());
        ObjJason.put("x2", p2.getX());
        ObjJason.put("y2", p2.getY());

        dotPanel.detectSquares();
        Node current = detectedSquares.getHead();
        if (current != null) {
            Square square = (Square) current.getData();
            ObjJason.put("square", true);
            ObjJason.put("upperLeftX", square.getUpperLeft().getX());
            ObjJason.put("upperLeftY", square.getUpperLeft().getY());
            // Añade las demás esquinas del cuadrado
        }

        Socket socketclient = new Socket("localhost", 9527);
        DataOutputStream dos = new DataOutputStream(socketclient.getOutputStream());
        dos.writeUTF(ObjJason.toString());
        dos.flush();
        dos.close();
        socketclient.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
}       
/**
 * Método para pintar lineas y los dots.
 */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Node squareCurrent = squareList.getHead();
            while (squareCurrent != null) {
                Square square = (Square) squareCurrent.getData();
                Dot upperLeft = square.getUpperLeft();
                int x1 = upperLeft.getX();
                int y1 = upperLeft.getY();
                g.fillRect(x1+3, y1+3, 95, 95); 
            squareCurrent = squareCurrent.getNext();
    }

            Node current = dotList.getHead();
            while (current != null) {
                Dot dot = (Dot) current.getData();
                int x = dot.getX();
                int y = dot.getY();
                g.fillOval(x, y, 8, 8); 
                current = current.getNext();
            }

            Node lineNode = lineList.getHead();
            while (lineNode != null) {
                Line line = (Line) lineNode.getData();
                int x1 = line.getX1();
             int y1 = line.getY1();
             int x2 = line.getX2();
                int y2 = line.getY2();
            g.drawLine(x1, y1, x2, y2);
            lineNode = lineNode.getNext();
        }

        
        }
    }

    /**
     * Clase Dot que representa un punto en el juego.
     */
    class Dot {
        private int x;
        private int y;

        public Dot(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }


    /**
     * Clase Node que representa un nodo en una lista enlazada.
     */
    class Node {
        private Object data;
        private Node next;
        public Node(Object data) {
        this.next = null;
        this.data = data;
        }
        public Object getData() {
        return this.data;
        }
        public void setData(Object data) {
        this.data = data;
        }

        public Node getNext() {
            return this.next;
            }
            public void setNext(Node node) {
            this.next = node;
            }
    
    }

    /**
     * Clase LinkedList que representa una lista enlazada.
     */
    class LinkedList {
        private Node head;
        private int size;
    
        public LinkedList() {
            this.head = null;
            this.size = 0;
        }
    
        public int size() {
            return this.size;
        }
    
        public void insertFirst(Object data) {
            Node newNode = new Node(data);
            newNode.setNext(this.head);
            this.head = newNode;
            this.size++;
        }
    
        public Node getHead() {
            return this.head;
        }
     
        public Dot findDot(int x, int y) {
            Node current = head;
            while (current != null) {
                Dot dot = (Dot) current.getData();
                // Comprueba si las coordenadas (x, y) están dentro del rango del Dot
                if (x >= dot.getX() && x <= dot.getX() + 200 
                    && y >= dot.getY() && y <= dot.getY() + 200 ) {
                    return dot; // Devuelve el Dot si se encuentra dentro del rango
                }
                current = current.getNext();
            }
            return null; // Devuelve null si no se encuentra ningún Dot en las coordenadas (x, y)
        }
    
    }

    /**
     * Clase Line que representa una línea en el juego.
     */
    class Line {
        private Dot dot1;
        private Dot dot2;
    
        public Line(Dot dot1, Dot dot2) {
            this.dot1 = dot1;
            this.dot2 = dot2;
        }
    
        public Dot getDot1() {
            return dot1;
        }
    
        public Dot getDot2() {
            return dot2;
        }

        public int getX1() {
            return dot1.getX();
        }

        public int getY1() {
            return dot1.getY();
        }

        public int getX2() {
            return dot2.getX();
        }

        public int getY2() {
            return dot2.getY();
        }
    }
    
    /**
     * Clase Square que representa un cuadrado formado en el juego.
     */
    class Square {
        private Dot upperLeft;
        private Dot upperRight;
        private Dot lowerLeft;
        private Dot lowerRight;
    
        public Square(Dot upperLeft, Dot upperRight, Dot lowerLeft, Dot lowerRight) {
            this.upperLeft = upperLeft;
            this.upperRight = upperRight;
            this.lowerLeft = lowerLeft;
            this.lowerRight = lowerRight;
        }
    
        public Dot getUpperLeft() {
            return upperLeft;
        }
    
        public Dot getUpperRight() {
            return upperRight;
        }
    
        public Dot getLowerLeft() {
            return lowerLeft;
        }
    
        public Dot getLowerRight() {
            return lowerRight;
        }
    }

/**
     * Clase ClientRunner que se encarga de la comunicación remota a través de sockets.
     */
class ClientRunner extends Thread {
    private Socket socket;
    private DotPanel panel;



    public void run() {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());

            while (true) {
                String MS = in.readUTF();
                Msentrada(MS);
                
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void Msentrada(String MS) {
        try {
            JSONObject ObjJason = new JSONObject(MS);
            int x1 = ObjJason.getInt("x1");
            int y1 = ObjJason.getInt("y1");
            int x2 = ObjJason.getInt("x2");
            int y2 = ObjJason.getInt("y2");
    
            isLocalAction = false;
    
            SwingUtilities.invokeLater(() -> {
                panel.Conect2(x1, y1, x2, y2);
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            isLocalAction = true;  // Volver a configurar a true al final.
        }
    }
    
    
    public ClientRunner(DotPanel panel) {
        this.panel = panel;
        try {
            this.socket = new Socket("localhost", 9527);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Tablero());
    }
}


