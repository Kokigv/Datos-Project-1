import javax.swing.*;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Tablero{

JFrame window;
JPanel panel_juego;

Tablero(){
    //para tableros prederminados note que el x es multiplo de 100 de la cant de 
    window = new JFrame();
    window.setSize(1000, 1000);
    window.setLayout(null);
    window.setResizable(false);
    window.setVisible(true);
    PanelDots paneldots = new PanelDots(10, 10);
    paneldots.setBounds(0, 0, 1000, 1000);
    window.add(paneldots);
    
}

    class PanelDots extends JPanel{
        private dot [][] matriz; 
        private dot dotSeleccionado1;
        private List<Line> lines = new ArrayList<>();
        
        public PanelDots(int filas, int columnas){
            this.matriz = new dot[filas][columnas];
            for(int i=0; i<filas; i++){
                    for(int j=0; j<columnas; j++){
                    matriz[i][j]= new dot(i*100,j*100); 
                    // System.out.println("Printeo un punto");  
                    
            }
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
        
                // Buscar el dot clickeado
                for (int i = 0; i < matriz.length; i++) {
                    for (int j = 0; j < matriz[i].length; j++) {
                        dot dotActual = matriz[i][j];
                        if (Math.abs(x - dotActual.getX()) <= 20 && Math.abs(y - dotActual.getY()) <= 20) {
                            if (dotSeleccionado1 == null) {
                                dotSeleccionado1 = dotActual; // Establece el primer punto seleccionado
                            } else {
                                // Check if the start and end points are adjacent
                                if ((Adyacentes(dotSeleccionado1, dotActual))) {
                                    // Check if a line already exists between these two dots
                                    if (!lineExists(dotSeleccionado1, dotActual)) {
                                        // Create a new line using the two selected points
                                        Line nuevaLinea = new Line(dotSeleccionado1, dotActual);
                                        lines.add(nuevaLinea);
                                        dotSeleccionado1 = null; // Reinicia la selección del primer punto
                                        repaint(); // Vuelve a dibujar el panel para mostrar la nueva línea
                                    }
                                    else{
                                        System.out.println("Esa línea ya existe");
                                        dotActual = null;
                                        dotSeleccionado1 = null;
                                        
                                    }
                                } else {
                                    System.out.println("Picha no adyacente");
                                    dotActual = null;
                                    dotSeleccionado1 = null;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

private boolean lineExists(dot dot1, dot dot2) {
    for (Line linea : lines) {
        if ((linea.getStartDot() == dot1 && linea.getEndDot() == dot2) ||
            (linea.getStartDot() == dot2 && linea.getEndDot() == dot1)) {
            return true; // A line already exists between these two dots
        }
    }
    return false; // No line exists between these two dots
}

private boolean Adyacentes(dot dot1, dot dot2) {
    int x1 = dot1.getX();
    int y1 = dot1.getY();
    int x2 = dot2.getX();
    int y2 = dot2.getY();

    return Math.abs(x1 - x2) == 100 && y1 == y2 || Math.abs(y1 - y2) == 100 && x1 == x2;
}
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        for(int i=0; i<matriz.length; i++){
                    for(int j=0; j<matriz[i].length; j++){
                        g.drawOval(matriz[i][j].getX(), matriz[i][j].getY(), 5, 5);
                    }
                }
                for (Line linea : lines) {
                    g.drawLine(linea.getStartDot().getX(), linea.getStartDot().getY(), 
                               linea.getEndDot().getX(), linea.getEndDot().getY());
                }
    }
    }
    
    class dot {
        private int x;
        private int y;
    
        public dot(int x, int y) {
            this.x = x;
            this.y = y;
        }
    
        // Getters y setters
        public int getX() {
            return x;
        }
    
        public void setX(int x) {
            this.x = x;
        }
    
        public int getY() {
            return y;
        }
    
        public void setY(int y) {
            this.y = y;
        }
    
        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    class Line {
        private dot startDot;
        private dot endDot;
    
        public Line(dot startDot, dot endDot) {
            this.startDot = startDot;
            this.endDot = endDot;
        }
    
        public dot getStartDot() {
            System.out.println("Se marco un dot");
            return startDot;
            
        }
    
        public dot getEndDot() {
            return endDot;
        }
    }

    public static void main(String[] args) {
       SwingUtilities.invokeLater(()->new Tablero());
       
    }
}