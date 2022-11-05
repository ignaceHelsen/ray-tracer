package main;

import main.object.Object;
import main.object.*;
import main.transformation.Rotation;
import main.transformation.Scale;
import main.transformation.Transformation;
import main.transformation.Translation;

public class Main {
    public static void main(String[] args) {
        final double SCREEN_WIDTH = 1920;
        final double SCREEN_HEIGHT = 1080;
        final double FOCALLENGTH = 500;
        final double CMAX = 1920;
        final double RMAX = 1080;

        /*
            /----------y
           /|
          / |
         /  |
        x   z

        */

        // scene
        Vector lightsourceWhite = new Vector(-30, 0, -100, 1); // location
        Vector lightsourceBlue = new Vector(500, 0, -1, 1); // location
        Vector lightsourceRed = new Vector(0, -1000, -10, 1); // location
        Vector lightsourceOrange = new Vector(-1000, 1000, -10, 1); // location
        Vector lightsourcePink = new Vector(-100, -10, -10, 1); // location

        Scene scene = new Scene(); // light color
        scene.addLightsource(lightsourceWhite, new double[]{255, 255, 255});
        scene.addLightsource(lightsourceBlue, new double[]{0, 0, 255});
        scene.addLightsource(lightsourceRed, new double[]{180, 0, 0});
        scene.addLightsource(lightsourceOrange, new double[]{200, 50, 0});
        scene.addLightsource(lightsourcePink, new double[]{255, 30, 122});

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 0, 0);
        scene.setCamera(camera);

        Transformation translationSphereRuby = new Translation(200, -100, -50);
        Transformation translationSphereChrome = new Translation(-50, 0, -50);
        Transformation translationCube = new Translation(120, 200, 0);
        Transformation translationCone = new Translation(-50, -50, -50);
        Transformation translationPlane = new Translation(0, 0, 30);
        Transformation rotateCube = new Rotation().rotateX(12).rotateY(30).rotateZ(54);
        Transformation rotateCone = new Rotation().rotateX(180);
        Transformation scaleSphereRuby = new Scale(30, 30, 30);
        Transformation scaleCube = new Scale(30, 30, 30);
        Transformation scaleCone = new Scale(300, 300, 300);
        Transformation scaleSphereChrome = new Scale(50, 50, 50);

        // MATERIALS
        Material ruby = new Material(new double[]{0.1745 * 15, 0.01175 * 10, 0.01175 * 10}, new double[]{0.61424 * 15, 0.04136 * 10, 0.04136 * 10}, new double[]{0.727811 * 15, 0.626959 * 10, 0.626959 * 10}, new double[]{1.762 * 1.2, 1.770 * 1.2, 1.778 * 1.2}, 0.2, new double[]{1, 0.5, 0.5});
        Material copper = new Material(new double[]{0.19125, 0.0735, 0.0225}, new double[]{0.7038, 0.27048, 0.0828}, new double[]{0.256777, 0.137622, 0.086014}, new double[]{fresnelToRefr(0.755), fresnelToRefr(0.49), fresnelToRefr(0.095)}, 0.2, new double[]{1, 0.5, 0.5});
        Material chrome = new Material(new double[]{0.25, 0.25, 0.25}, new double[]{0.4, 0.4, 0.4}, new double[]{3.1812, 3.1812, 3.1812}, new double[]{3.1812 * 2, 3.1812 * 2, 3.1812 * 2}, 0.2, new double[]{1, 0.5, 0.5});
        Material gold = new Material(new double[]{0.24725, 0.1995, 0.0745}, new double[]{0.75164, 0.60648, 0.22648}, new double[]{0.628281, 0.555802, 0.366065}, new double[]{fresnelToRefr(0.989) * 1.5, fresnelToRefr(0.876) * 1.5, fresnelToRefr(0.399) * 1.5}, 0.5, new double[]{1, 0.5, 0.5});

        // OBJECTS
        Object plane = new Plane(gold);
        Object sphere = new Sphere(ruby);
        Object cube = new Cube(copper);
        Object sphere2 = new Sphere(chrome);
        Object cone = new TaperedCylinder(copper, 0.95);

        plane.addTransformation(translationPlane);

        cone.addTransformation(scaleCone);
        cone.addTransformation(translationCone);
        cone.addTransformation(rotateCone);

        sphere.addTransformation(scaleSphereRuby);
        sphere.addTransformation(translationSphereRuby);

        sphere2.addTransformation(scaleSphereChrome);
        sphere2.addTransformation(translationSphereChrome);

        cube.addTransformation(rotateCube);
        cube.addTransformation(scaleCube);
        cube.addTransformation(translationCube);

        scene.addObject(sphere2);
        scene.addObject(plane);
        scene.addObject(sphere);
        scene.addObject(cube);
        scene.addObject(cone);

        Renderer renderer = new Renderer(FOCALLENGTH, SCREEN_WIDTH, SCREEN_HEIGHT, CMAX, RMAX);
        renderer.setScene(scene);

        Thread render = new Thread(renderer::startRender);
        render.start();

        Thread show = new Thread(() -> {
            while (render.isAlive()) {
                try {
                    Thread.sleep(1000);

                    renderer.show();
                    System.out.println("show");

                } catch (InterruptedException v) {
                    v.printStackTrace();
                }
            }
        });

        show.start();
    }

    public static double fresnelToRefr(double fresnel) {
        double sqrt = Math.sqrt(fresnel);
        return (1 + sqrt) / (1 - sqrt);
    }
}
