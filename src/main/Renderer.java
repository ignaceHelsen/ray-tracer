package main;

import main.object.Object;
import main.transformation.Scale;
import main.transformation.Transformation;
import main.transformation.Translation;

import javax.swing.*;
import java.awt.*;

public class Renderer {
    private final JFrame frame;
    private final double focallength, screenWidth, screenHeight;
    private final Scene scene;

    public Renderer(double focallength, int screenWidth, int screenHeight, Scene scene) {
        this.focallength = focallength;
        this.frame = new JFrame();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.scene = scene;

        frame.setFocusable(true);
        frame.setTitle("Ray tracer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(screenWidth, screenHeight);
    }

    void startRender(Vector origin) {
        JPanel panel = new JPanel(true) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D graph2d = (Graphics2D) g;
                Toolkit.getDefaultToolkit().sync();

                final double H = screenHeight / 2;
                final double W = screenWidth / 2;

                final double y = 2/screenWidth;
                final double z = 2/screenHeight;

                // shoot rays in a for loop
                for (int c = 0; c <= screenWidth; c++) { //nColumns
                    for (int r = 0; r <= screenHeight; r++) { // nRows
                        Vector dir = new Vector(-focallength, W*(y*c-1), H*(z*r-1), 0);

                        Ray ray = new Ray(origin, dir);

                        // for every object, cast the ray
                        for (Object object : scene.getObjects()) {
                            Vector point = object.getFirstHitPoint(ray);

                            if (point != null) {
                                // get normal vector
                                Vector m =
                                // get color of object
                                graph2d.setColor(object.getColor());
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
}
