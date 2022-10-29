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
        final double FOCALLENGTH = 400;
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
        Vector lightsourceWhite = new Vector(500, 0, -10000, 1); // location
        Vector lightsourceBlue = new Vector(500, 0, -1, 1);
        Vector lightsourceRed = new Vector(0, -1000, -10, 1);
        Vector lightsourceOrange = new Vector(-1000, 1000, -10, 1);
        Vector lightsourcePink = new Vector(-100, -10, -10, 1);

        Scene scene = new Scene(); // light color
        scene.addLightsource(lightsourceWhite, new double[]{255, 255, 255});
        scene.addLightsource(lightsourceBlue, new double[]{0, 0, 255});
        scene.addLightsource(lightsourceRed, new double[]{255, 0, 0});
        scene.addLightsource(lightsourceOrange, new double[]{200, 50, 0});
        scene.addLightsource(lightsourcePink, new double[]{255, 30, 122});

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 0, 0);
        scene.setCamera(camera);

        Transformation translationSphereArgon = new Translation(200, -100, 50);
        Transformation translationSphereChrome = new Translation(-50, 0, -50);
        Transformation translationCubeCopper = new Translation(-150, 200, 0);
        Transformation translationConeGold = new Translation(-50, -50, -50);
        Transformation rotate = new Rotation().rotateX(45).rotateY(45).rotateZ(45);
        Transformation rotateCone = new Rotation().rotateX(180);
        Transformation scaleSphereArgon = new Scale(30, 30, 30);
        Transformation scaleCone = new Scale(300, 300, 300);
        Transformation scaleSphereChrome = new Scale(50, 50, 50);
        Transformation translationPlane = new Translation(0, 0, 50);

        // MATERIALS
        Material argon = new Material(new double[]{0.0215, 0.1745, 0.0215}, new double[]{0.07568, 0.61424, 0.07568}, new double[]{0.633, 0.727811, 0.633}, new double[]{1.2636, 1.00028233, 1.00028426}, 0.2, new double[]{1, 0.5, 0.5});
        Material copper = new Material(new double[]{0.19125, 0.0735, 0.0225}, new double[]{0.7038, 0.27048, 0.0828}, new double[]{0.256777, 0.137622, 0.086014}, new double[]{fresnelToRefr(0.755), fresnelToRefr(0.49), fresnelToRefr(0.095)}, 0.2, new double[]{1, 0.5, 0.5});
        Material chrome = new Material(new double[]{0.25, 0.25, 0.25}, new double[]{0.4, 0.4, 0.4}, new double[]{3.1071, 3.1812, 2.5010}, new double[]{3.1812, 3.1071, 2.5010}, 0.2, new double[]{1, 0.5, 0.5});
        Material gold = new Material(new double[]{0.24725, 0.1995, 0.0745}, new double[]{0.75164, 0.60648, 0.22648}, new double[]{0.628281, 0.555802, 0.366065}, new double[]{fresnelToRefr(0.989), fresnelToRefr(0.876), fresnelToRefr(0.399)}, 0.2, new double[]{1, 0.5, 0.5});

        // OBJECTS
        Object plane = new Plane(gold);
        Object sphere = new Sphere(argon);
        Object cube = new Cube(gold);
        Object sphere2 = new Sphere(chrome);
        Object cone = new TaperedCylinder(gold, 0.95);

        plane.addTransformation(translationPlane);

        cone.addTransformation(scaleCone);
        cone.addTransformation(translationConeGold);
        cone.addTransformation(rotateCone);

        sphere.addTransformation(scaleSphereArgon);
        sphere.addTransformation(translationSphereArgon);

        sphere2.addTransformation(scaleSphereChrome);
        sphere2.addTransformation(translationSphereChrome);

        cube.addTransformation(rotate);
        cube.addTransformation(scaleSphereArgon);
        cube.addTransformation(translationCubeCopper);

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

    public static double fresnelToRefr(double fresnel) {
        double sqrt = Math.sqrt(fresnel);
        return (1 + sqrt) / (1 - sqrt);
    }
}
