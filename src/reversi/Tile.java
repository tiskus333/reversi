package reversi;

import javax.swing.*;
//import java.awt.*;
import java.awt.Color;
import java.awt.Dimension;

public class Tile extends JButton {
    private static final int SIZE = 71;
    private boolean enabled;
    ImageIcon icon;

    public Tile(ImageIcon img) {
        icon = img;
        setBackground(Color.GREEN);
        setSize(new Dimension(SIZE, SIZE));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
}