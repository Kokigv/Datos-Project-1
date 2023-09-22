import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Tablero {

    JFrame window;
    DotLinkedList dotList;
    DotPanel dotPanel;
    LineLinkedList lineList;
     SquareLinkedList squareList;

    Tablero() {
        window = new JFrame();
        window.setSize(1000, 1000);
        window.setLayout(null);
        window.setResizable(false);
        window.setVisible(true);
        dotList = new DotLinkedList();
        dotPanel = new DotPanel(dotList);
        lineList = new LineLinkedList();
        squareList = new SquareLinkedList();
        dotPanel.setSize(1000, 1000);
        window.add(dotPanel);
        window.add(dotPanel, BorderLayout.CENTER);

        for (int i = 0; i < 10; i++) { // Crea puntos 10x10
            for (int j = 0; j < 10; j++) {
                Dot newDot = new Dot(i * 100, j * 100);
                dotList.insertFirst(newDot);
            }
        }
    
    }

    class DotPanel extends JPanel {
        private DotLinkedList dotList;
        private Dot dotSeleccionado1;

        public DotPanel(DotLinkedList dotList) {
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
                                dotSeleccionado1 = null;
                                checkForSquares();
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

        private void checkForSquares() {
            System.out.println("La función se ejecutó");
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
                        System.out.println("Cuadrado Hecho");
                        Square square = new Square(dot, rightDot, bottomDot, bottomRightDot);
                        squareList.insertFirst(square);
                    }
                }
    
                current = current.getNext();
            }
    
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
                        checkForSquares();
                       
                    } else {
                        System.out.println("No se puede agregar la línea");
                        dotSeleccionado1 = null;
                    }
                }
                repaint();
            }
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Node squareCurrent = squareList.getHead();
            while (squareCurrent != null) {
                Square square = (Square) squareCurrent.getData();
                Dot upperLeft = square.getUpperLeft();
                int x1 = upperLeft.getX();
                int y1 = upperLeft.getY();
                g.fillRect(x1, y1, 98, 98); // You can adjust the size as needed
            squareCurrent = squareCurrent.getNext();
    }

            Node current = dotList.getHead();
            g.setColor(java.awt.Color.BLUE);
            while (current != null) {
                Dot dot = (Dot) current.getData();
                int x = dot.getX();
                int y = dot.getY();
                g.fillOval(x, y, 5, 5); // Pinta un círculo (puedes ajustar el tamaño)
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

    class DotLinkedList {
        private Node head;
        private int size;
    
        public DotLinkedList() {
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
                if (x >= dot.getX() && x <= dot.getX() + 25 // 5 es el tamaño del Dot en este ejemplo
                    && y >= dot.getY() && y <= dot.getY() + 25) {
                    return dot; // Devuelve el Dot si se encuentra dentro del rango
                }
                current = current.getNext();
            }
            return null; // Devuelve null si no se encuentra ningún Dot en las coordenadas (x, y)
        }
    
    }

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

    class LineLinkedList {
        private Node head;
        private int size;

        public LineLinkedList() {
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
    }

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

    class SquareLinkedList {
        private Node head;
        private int size;
    
        public SquareLinkedList() {
            this.head = null;
            this.size = 0;
        }
    
 
        public int size() {
            return this.size;
        }
    
        public void insertFirst(Square square) {
            Node newNode = new Node(square);
            newNode.setNext(this.head);
            this.head = newNode;
            this.size++;
        }
    
        public Node getHead() {
            return this.head;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Tablero());
    }
}