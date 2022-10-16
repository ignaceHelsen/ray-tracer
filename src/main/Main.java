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
    public static void main(String[] args) {
        final int SCREEN_WIDTH = 1920 / 2;
        final int SCREEN_HEIGHT = 1080 / 2;
        final double FOCALLENGTH = 1000;

        // scene
        Scene scene = new Scene();

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 1, 1);

        // screen
        // every ray's S will equal the camera position
        Vector s = new Vector(camera.getLocation().getX(), camera.getLocation().getY(), camera.getLocation().getZ(), 1);

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

        new Renderer(FOCALLENGTH, SCREEN_WIDTH, SCREEN_HEIGHT, scene).startRender(s);
    }
}
