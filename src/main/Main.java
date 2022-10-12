package main;

import main.object.Object;
import main.object.Sphere;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        final int SCREEN_WIDTH = 1920/2;
        final int SCREEN_HEIGHT = 1080/2;
        final int FOCALLENGTH = 1000;

        /*Ray ray = new Ray(new Vector(-5,0,0, 1), new Vector(1,0,0, 0));
        Object sphere = new Sphere(Color.RED);
        Vector point = sphere.getCollision(ray);*/

        // scene
        Scene scene = new Scene();

        // camera in center of screen
        Camera camera = new Camera(0, 0,0);

        // screen
        // every ray's S will equal the camera position
        Vector s = new Vector(camera.getLocation().getX(), camera.getLocation().getY(), camera.getLocation().getZ(), 1);

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
                for (int i = -(SCREEN_WIDTH/2); i <= SCREEN_WIDTH/2; i++) { // x
                    for (int j = -(SCREEN_HEIGHT/2); j <= SCREEN_HEIGHT/2; j++) { // y
                        // every ray's S will equal the camera position
                        // the c vector will target the pixel hole and shoot through that
                        // TODO shoot rays also to negative i's and j's --> transform s of every ray to outside
                        Ray ray = new Ray(s, new Vector(FOCALLENGTH, i, j, 0));
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
