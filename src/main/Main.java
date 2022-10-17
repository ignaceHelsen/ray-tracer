package main;

import main.object.Cube;
import main.object.Object;
import main.object.Sphere;
import main.transformation.Rotation;
import main.transformation.Scale;
import main.transformation.Transformation;
import main.transformation.Translation;

import java.awt.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final int SCREEN_WIDTH = 1920;
        final int SCREEN_HEIGHT = 1080;
        final double FOCALLENGTH = 1000;

        // scene
        Scene scene = new Scene();

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 1, 1);
        scene.setCamera(camera);

        Transformation translationSphere = new Translation(-105,-105, -105);
        Transformation translationSphere2 = new Translation(500,15, 5);
        Transformation translationCube = new Translation(200,100, 0);
        Transformation rotate = new Rotation().rotateX(45).rotateY(10).rotateZ(10);
        Transformation scale = new Scale(50, 50, 50);
        Transformation scale2 = new Scale(40, 40, 40);

        // OBJECTS
        Object sphere = new Sphere(Color.RED);
        Object cube = new Cube(Color.BLUE);
        Object sphere2 = new Sphere(Color.PINK);

        sphere.addTransformation(scale);
        sphere.addTransformation(translationSphere);

        sphere2.addTransformation(scale2);
        sphere2.addTransformation(translationSphere2);

        cube.addTransformation(rotate);
        cube.addTransformation(scale);
        cube.addTransformation(translationCube);

        scene.addObject(sphere);
        scene.addObject(cube);
        scene.addObject(sphere2);

        Renderer renderer = new Renderer(FOCALLENGTH, SCREEN_WIDTH, SCREEN_HEIGHT);
        renderer.startRender();
        renderer.setScene(scene);

        // pan around the space
        for (int i = 0; i < 100; i++) {
            camera.location.setY(camera.location.getY()+1);
            camera.location.setX(camera.location.getX()-2);
            Thread.sleep(10);
            renderer.setScene(scene);
            renderer.draw();
        }

        for (int i = 0; i < 150; i++) {
            camera.location.setY(camera.location.getY()-1);
            Thread.sleep(10);
            renderer.setScene(scene);
            renderer.draw();
        }

        for (int i = 0; i < 1000; i++) {
            camera.location.setX(camera.location.getX()-1);
            Thread.sleep(10);
            renderer.setScene(scene);
            renderer.draw();
        }
    }
}
