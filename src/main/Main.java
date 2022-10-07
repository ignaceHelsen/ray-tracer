package main;

import main.object.Object;
import main.object.Sphere;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        GraphicsContext graphicsContext = new GraphicsContext();

        // scene
        Scene scene = new Scene();
        // camera
        Camera camera = new Camera(0,0,0);
        // screen
        // every ray's S will equal the camera position
        Vector s = new Vector(camera.getLocation().getX(), camera.getLocation().getY(), camera.getLocation().getZ(), (byte) 0);

        Object sphere = new Sphere(Color.RED);
        scene.addObject(sphere);

        // shoot rays in a for loop
        for (int i = 0; i < 1920; i++) { // x
            for (int j = 0; j < 1080; j++) { // y
                // every ray's S will equal the camera position
                Ray ray = new Ray(s, new Vector(i, j, 100, (byte) 0));
                // for every object, cast the ray
                for (Object object: scene.getObjects()) {
                    Vector point = object.getCollision(ray);

                    if (point != null) {
                        // get color of object
                        graphicsContext.render();
                        // g2d[0].drawLine(i, i+1, j, j+1);
                    }
                }
            }
        }
    }
}
