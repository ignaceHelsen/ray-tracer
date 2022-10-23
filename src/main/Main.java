package main;

import main.object.Object;
import main.object.*;
import main.transformation.Rotation;
import main.transformation.Scale;
import main.transformation.Transformation;
import main.transformation.Translation;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final double SCREEN_WIDTH = 1920;
        final double SCREEN_HEIGHT = 1080;
        final double FOCALLENGTH = 500;
        final double CMAX = SCREEN_WIDTH;
        final double RMAX = SCREEN_HEIGHT;

        /*
            /----------y
           /|
          / |
         /  |
        x   z

        */

        // scene
        Vector lightsourceWhite = new Vector(0, 0, -6000, 1); // location
        Vector lightsourceBlue = new Vector(500, 0, -1, 1);
        Vector lightsourceRed = new Vector(0, -1000, -10, 1);
        Vector lightsourceOrange = new Vector(-1000, 1000, -10, 1);
        Vector lightsourcePink = new Vector(-10000, -10, -10, 1);

        Scene scene = new Scene(); // light color
        //scene.addLightsource(lightsourceWhite, new double[]{0.5, 0.5, 0.5});
        scene.addLightsource(lightsourceBlue, new double[]{0, 0, 0.5});
        scene.addLightsource(lightsourceRed, new double[]{0.5, 0, 0});
        scene.addLightsource(lightsourceOrange, new double[]{0.5, 0.1, 0});
        //scene.addLightsource(lightsourcePink, new double[]{1, 0.1, 0.5});

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 0, 0);
        scene.setCamera(camera);

        Transformation translationSphereEmerald = new Translation(100, -100, 50);
        Transformation translationSphereChrome = new Translation(-50, 0, -50);
        Transformation translationCubePearl = new Translation(-100, 200, 0);
        Transformation translationConeGold = new Translation(-50, -50, -50);
        Transformation rotate = new Rotation().rotateX(45).rotateY(10).rotateZ(10);
        Transformation rotateCone = new Rotation().rotateX(180);
        Transformation scaleSphereEmerald = new Scale(30, 30, 30);
        Transformation scaleCone = new Scale(300, 300, 300);
        Transformation scaleSphereChrome = new Scale(50, 50, 50);
        Transformation translationPlane = new Translation(0, 0, 50);

        // MATERIALS
        Material emerald = new Material(new double[]{0.0215, 0.1745, 0.0215}, new double[]{0.07568, 0.61424, 0.07568}, new double[]{0.633, 0.727811, 0.633}, 0.2);
        Material pearl = new Material(new double[]{0.25, 0.20725, 0.20725}, new double[]{1, 0.829, 0.829}, new double[]{0.296648, 0.296648, 0.296648}, 0.2);
        Material chrome = new Material(new double[]{0.25, 0.25, 0.25}, new double[]{0.4, 0.4, 0.4}, new double[]{0.774597, 0.774597, 0.774597}, 0.2);
        Material gold = new Material(new double[]{0.24725, 0.1995, 0.0745}, new double[]{0.75164, 0.60648, 0.22648}, new double[]{0.628281, 0.555802, 0.366065}, 0.2);

        // OBJECTS
        Object plane = new Plane(gold);
        Object sphere = new Sphere(emerald);
        Object cube = new Cube(pearl);
        Object sphere2 = new Sphere(chrome);
        Object cone = new TaperedCylinder(gold, 0.95);

        plane.addTransformation(translationPlane);

        cone.addTransformation(scaleCone);
        cone.addTransformation(translationConeGold);
        cone.addTransformation(rotateCone);

        sphere.addTransformation(scaleSphereEmerald);
        sphere.addTransformation(translationSphereEmerald);

        sphere2.addTransformation(scaleSphereChrome);
        sphere2.addTransformation(translationSphereChrome);

        cube.addTransformation(rotate);
        cube.addTransformation(scaleSphereEmerald);
        cube.addTransformation(translationCubePearl);

        scene.addObject(plane);
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
