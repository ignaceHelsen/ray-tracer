package main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicsContext {
    private final JFrame frame;
    private final JPanel panel;

    public GraphicsContext() {
        frame = new JFrame();
        panel = new JPanel(true) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                doDrawing(g);
            }
        };
        frame.setFocusable(true);
        frame.add(panel);
        frame.setTitle("Super Mario Bros");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(1920,1080);
    }

    private void doDrawing(Graphics g) {
        Graphics2D graph2d = (Graphics2D) g;
        Toolkit.getDefaultToolkit().sync();
        // graph2d.setColor();
        graph2d.drawLine(0,0, 500, 500);
        graph2d.dispose();
    }

    public void render() {
        panel.repaint();
    }
}
