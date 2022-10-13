package main;

import main.object.Object;
import main.transformation.Transformation;
import main.transformation.Translation;

import javax.swing.*;
import java.awt.*;

public class Renderer {
    private final JFrame frame;
    private final int focallength, screenWidth, screenHeight;
    private final Scene scene;

    public Renderer(int focallength, int screenWidth, int screenHeight, Scene scene) {
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

                // shoot rays in a for loop
                for (int i = -(screenWidth / 2); i <= screenWidth / 2; i++) { // x
                    for (int j = -(screenHeight / 2); j <= screenHeight / 2; j++) { // y
                        // every ray's S will equal the camera position
                        // the c vector will target the pixel hole and shoot through that
                        // TODO shoot rays also to negative i's and j's --> transform s of every ray to outside
                        Ray ray = new Ray(origin, new Vector(focallength, i, j, 0));
                        // for every object, cast the ray
                        for (Object object : scene.getObjects()) {
                            Vector point = object.getFirstHitPoint(ray);

                            if (point != null) {
                                // we got a hit
                                Transformation translation = new Translation(1,1);
                                Ray transformedRay = new Ray(translation.transform(ray.getS()), translation.transform(ray.getDir()));
                                // get color of object
                                graph2d.setColor(Color.RED);
                                graph2d.drawLine(i, j, i, j);
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
