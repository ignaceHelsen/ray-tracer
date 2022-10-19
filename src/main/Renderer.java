package main;

import main.object.Object;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class Renderer {
    private final JFrame frame;
    private JPanel panel;
    private final double focallength, screenWidth, screenHeight;
    private Scene scene;

    public Renderer(double focallength, int screenWidth, int screenHeight) {
        this.focallength = focallength;
        this.frame = new JFrame();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        frame.setFocusable(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setTitle("Ray tracer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(screenWidth, screenHeight);
    }

    void startRender() {
        this.panel = new JPanel(true) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D graph2d = (Graphics2D) g;
                Toolkit.getDefaultToolkit().sync();

                final double H = screenHeight / 2;
                final double W = screenWidth / 2;

                final double y = 2 / screenWidth;
                final double z = 2 / screenHeight;

                // shoot rays in a for loop
                for (int c = 0; c <= screenWidth; c++) { //nColumns
                    for (int r = 0; r <= screenHeight; r++) { // nRows
                        Vector dir = new Vector(-focallength, W * (y * c - 1), H * (z * r - 1), 0);

                        Ray ray = new Ray(scene.getCamera().getLocation(), dir);

                        // for every object, cast the ray
                        List<Intersection> intersections = scene.getObjects().stream().map(o -> o.getFirstHitPoint(ray)).sorted(o -> o.getT1()).collect(Collectors.toList());
                        for (Object object : scene.getObjects()) {
                            Vector point = object.getFirstHitPoint(ray);

                            if (point != null) {
                                // get shade

                                // get color of object
                                graph2d.setColor(object.getMaterial().getColor());
                                graph2d.drawLine(c, r, c, r);
                            }
                        }
                    }
                }

                graph2d.dispose();
            }
        };

        frame.add(panel);
    }

    public void draw() {
        this.panel.repaint();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
