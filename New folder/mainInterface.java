import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class mainInterface {

    private JFrame mainScreen;
  
    public mainInterface() {
        mainScreen = new JFrame("Dots and Boxes");
        mainScreen.setSize(800, 600);
        mainScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainScreen.setLocationRelativeTo(null);
        mainScreen.setResizable(false);
        mainScreen.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("Images/iconImage.png")));

        JButton playButton = new JButton("Play");
        playButton.setBounds(535, 250, 150, 50);
        playButton.setBackground(Color.white);
        playButton.setFont(new Font("Arial", Font.PLAIN, 32));
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createLineBorder(Color.black, 3));

        playButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                playButton.setBackground(Color.lightGray);
            }

            public void mouseExited(MouseEvent e) {
                playButton.setBackground(Color.white);
            }
        });

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainScreen.dispose();
                SwingUtilities.invokeLater(() -> new Tablero());
            }
        });

        JPanel Panel = new JPanel();
        Panel.setSize(800, 700);
        Panel.setLayout(null);
        Panel.setBackground(Color.white);
        mainScreen.add(Panel);
        Panel.add(playButton);

        JLabel logo = new JLabel("");
        logo.setIcon(new ImageIcon(mainInterface.class.getResource("Images/logo_DotsBoxes.png")));
        logo.setBounds(10, 20, 500, 500);
        Panel.add(logo);

        // Set the menu bar for the main frame
        mainScreen.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new mainInterface());
    }
}