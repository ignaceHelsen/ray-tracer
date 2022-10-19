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
        final int SCREEN_WIDTH = 1920 / 2;
        final int SCREEN_HEIGHT = 1080 / 2;
        final double FOCALLENGTH = 1000;

        // scene
        Scene scene = new Scene();

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 0, -100);
        scene.setCamera(camera);

        Transformation translationSphere = new Translation(-205, -105, -105);
        Transformation translationSphere2 = new Translation(500, 15, 5);
        Transformation translationCube = new Translation(0, 100, 0);
        Transformation translationCone = new Translation(700, 0, 0);
        Transformation rotate = new Rotation().rotateX(45).rotateY(10).rotateZ(10);
        Transformation scale = new Scale(50, 50, 50);
        Transformation scale2 = new Scale(40, 40, 40);

        // MATERIALS
        Material red = new Material(Color.red, 0.5, 0.5, 0.5);
        Material blue = new Material(Color.blue, 0.5, 0.5, 0.5);
        Material pink = new Material(Color.pink, 0.5, 0.5, 0.5);
        Material green = new Material(Color.green, 0.5, 0.5, 0.5);
        Material gray = new Material(Color.gray, 0.5, 0.5, 0.5);

        // OBJECTS
        Object plane = new Plane(gray);
        Object sphere = new Sphere(red);
        Object cube = new Cube(blue);
        Object sphere2 = new Sphere(pink);
        Object cone = new TaperedCylinder(green, 0.05);

        cone.addTransformation(scale);
        cone.addTransformation(translationCone);

        sphere.addTransformation(scale);
        sphere.addTransformation(translationSphere);

        sphere2.addTransformation(scale2);
        sphere2.addTransformation(translationSphere2);

        cube.addTransformation(rotate);
        cube.addTransformation(scale);
        cube.addTransformation(translationCube);

        scene.addObject(plane);
        scene.addObject(sphere);
        scene.addObject(cube);
        scene.addObject(sphere2);
        scene.addObject(cone);

        Renderer renderer = new Renderer(FOCALLENGTH, SCREEN_WIDTH, SCREEN_HEIGHT);
        renderer.startRender();
        renderer.setScene(scene);

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

        for (int i = 0; i < 10000; i++) {
            camera.location.setX(camera.location.getX() - 0.1);
            Thread.sleep(2);
            renderer.setScene(scene);
            renderer.draw();
        }*/
    }
}
