package main;

import main.object.*;
import main.object.Object;
import main.transformation.Rotation;
import main.transformation.Scale;
import main.transformation.Transformation;
import main.transformation.Translation;

import java.awt.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int SCREEN_WIDTH = 1920/2;
        final int SCREEN_HEIGHT = 1080/2;
        final double FOCALLENGTH = 500;
        final int CMAX = SCREEN_WIDTH/2;
        final int RMAX = SCREEN_HEIGHT/2;

        /*
            /----------y
           /|
          / |
         /  |
        x   z

        */

        // scene
        Vector lightsource = new Vector(0, 0, 100, 1);
        Scene scene = new Scene(lightsource, new double[]{255, 0, 0});

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 0, 0);
        scene.setCamera(camera);

        Transformation translationSphereRed = new Translation(100, -100, 50);
        Transformation translationSpherePink = new Translation(-50, 0, -50);
        Transformation translationCubeBlue = new Translation(100, 200, 0);
        Transformation translationConeGreen = new Translation(-50, -50, -50);
        Transformation rotate = new Rotation().rotateX(45).rotateY(10).rotateZ(10);
        Transformation scaleSphereRed = new Scale(30, 30, 30);
        Transformation scaleCone = new Scale(300, 300, 300);
        Transformation scaleSpherePink = new Scale(50, 50, 50);
        Transformation translationPlane = new Translation(0,0, 500);

        // MATERIALS
        Material emerald = new Material(new double[]{0.0215, 0.1745, 0.0215}, new double[]{0.07568, 0.61424, 0.07568}, new double[]{0.633, 0.727811, 0.633});
        Material pearl = new Material(new double[]{0.25, 0.20725, 0.20725}, new double[]{1, 0.829, 0.829}, new double[]{0.296648, 0.296648, 0.296648});
        Material chrome = new Material(new double[]{0.25, 0.25, 0.25}, new double[]{0.4, 0.4, 0.4}, new double[]{0.774597, 0.774597, 0.774597});
        Material gold = new Material(new double[]{0.24725, 0.1995, 0.0745}, new double[]{0.75164, 0.60648, 0.22648}, new double[]{0.628281, 0.555802, 0.366065});

        // OBJECTS
        // Object plane = new Plane(gray);
        Object sphere = new Sphere(emerald);
        Object cube = new Cube(pearl);
        Object sphere2 = new Sphere(chrome);
        Object cone = new TaperedCylinder(gold, 0.95);

        // plane.addTransformation(translationPlane);

        cone.addTransformation(scaleCone);
        cone.addTransformation(translationConeGreen);

        sphere.addTransformation(scaleSphereRed);
        sphere.addTransformation(translationSphereRed);

        sphere2.addTransformation(scaleSpherePink);
        sphere2.addTransformation(translationSpherePink);

        cube.addTransformation(rotate);
        cube.addTransformation(scaleSphereRed);
        cube.addTransformation(translationCubeBlue);

        // scene.addObject(plane);
        scene.addObject(sphere);
        scene.addObject(cube);
        scene.addObject(sphere2);
        scene.addObject(cone);

        Renderer renderer = new Renderer(FOCALLENGTH, SCREEN_WIDTH, SCREEN_HEIGHT, CMAX, RMAX);
        renderer.setScene(scene);
        renderer.startRender();

        // pan around the space
        /*for (int i = 0; i < 1000; i++) {
            camera.location.setY(camera.location.getY() + 0.1);
            camera.location.setX(camera.location.getX() - 0.2);
            Thread.sleep(2);
            renderer.setScene(scene);
            renderer.draw();
        }

        for (int i = 0; i < 1500; i++) {
            camera.location.setY(camera.location.getY() - 0.1);
            Thread.sleep(2);
            renderer.setScene(scene);
            renderer.draw();
        }

        for (int i = 0; i < 5000; i++) {
            camera.location.setX(camera.location.getX() - 0.1);
            Thread.sleep(2);
            renderer.setScene(scene);
            renderer.draw();
        }*/
    }
}
