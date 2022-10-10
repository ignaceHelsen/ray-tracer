package main;

import main.object.Object;
import main.object.Sphere;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        final int SCREEN_WIDTH = 1920/2;
        final int SCREEN_HEIGHT = 1080/2;
        final int FOCALLENGTH = 1;

        // scene
        Scene scene = new Scene();

        // camera in center of screen
        Camera camera = new Camera(SCREEN_WIDTH/2,SCREEN_HEIGHT/2,0);

        // screen
        // every ray's S will equal the camera position
        Vector s = new Vector(camera.getLocation().getX(), camera.getLocation().getY(), camera.getLocation().getZ(), (byte) 0);

        Object sphere = new Sphere(Color.RED);
        scene.addObject(sphere);

        JFrame frame = new JFrame();
        JPanel panel = new JPanel(true) {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D graph2d = (Graphics2D) g;
                Toolkit.getDefaultToolkit().sync();

                // shoot rays in a for loop
                for (int i = 0; i <= SCREEN_WIDTH; i++) { // x
                    for (int j = 0; j <= SCREEN_HEIGHT; j++) { // y
                        // every ray's S will equal the camera position
                        // the c vector will target the pixel hole and shoot through that
                        Ray ray = new Ray(s, new Vector(-i, -j, FOCALLENGTH, (byte) 0)); // camera seems to be behind screen?
                        // for every object, cast the ray
                        for (Object object: scene.getObjects()) {
                            Vector point = object.getCollision(ray);

                            if (point != null) {
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

        frame.setFocusable(true);
        frame.add(panel);
        frame.setTitle("Ray tracer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
    }
}
