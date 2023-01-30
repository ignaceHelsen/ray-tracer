package main;

import main.object.Object;
import main.object.TaperedCylinder;
import main.sdl.SDL;
import main.sdl.Settings;
import main.transformation.Rotation;
import main.transformation.Scale;
import main.transformation.Transformation;
import main.transformation.Translation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final double SCREEN_WIDTH = 1920;
        final double SCREEN_HEIGHT = 1080;
        final double FOCALLENGTH = 1920;
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
        // refraction         Vector lightsourceWhite = new Vector(1800, 200, -200, 1); // location
        Vector lightsourceWhite = new Vector(500, 0, -20000, 1); // location
        Vector lightsourceBlue = new Vector(5000, -500, -10000, 1); // location
        Vector lightsourceRed = new Vector(500, 500, -10000, 1); // location
        Vector lightsourceOrange = new Vector(-1000, 1000, -40000, 1); // location
        Vector lightsourcePink = new Vector(0, 400, -2000, 1); // location

        Scene scene = new Scene(); // light color
        // refraction scene.addLightsource(lightsourceWhite, new double[]{155, 155, 155}, 0.000002);
        scene.addLightsource(lightsourceWhite, new double[]{155, 155, 155}, 0.1);
        /*scene.addLightsource(lightsourceBlue, new double[]{0, 0, 255}, 0.1);
        scene.addLightsource(lightsourceRed, new double[]{180, 0, 0}, 0.1);
        scene.addLightsource(lightsourceOrange, new double[]{200, 50, 0}, 0.1);*/
        //scene.addLightsource(lightsourcePink, new double[]{255, 30, 122}, 0.1);

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 0, 0);
        scene.setCamera(camera);
        Settings settings = new Settings();
        List<Material> materials = new ArrayList<>();

        try {
            settings = SDL.parseSettings("scenes/sdlAllObjects.sdl", 0);
            materials = SDL.parseMaterial("scenes/materials.sdl", 0);
            scene.addMaterials(materials);
            scene.addObjects(SDL.parseObjects("scenes/sdlAllObjects.sdl", scene.getMaterials(), 7));
        } catch (IOException e) {
            System.out.println("Problem reading sdl: " + e.getMessage());
        }

        // MATERIALS
        /*Material ruby = new Material(new double[]{0.1745, 0.01175, 0.01175}, new double[]{0.61424, 0.04136, 0.04136}, new double[]{0.727811, 0.626959, 0.626959}, new double[]{1.762, 1.770, 1.778}, 0.2, new double[]{0.6, 0.3, 0.1}, 0.6);
        Material copper = new Material(new double[]{0.19125, 0.0735, 0.0225}, new double[]{0.7038, 0.27048, 0.0828}, new double[]{0.256777, 0.137622, 0.086014}, new double[]{fresnelToRefr(0.755), fresnelToRefr(0.49), fresnelToRefr(0.095)}, 0.2, new double[]{0.6, 0.3, 0.1}, 0.1);
        Material chrome = new Material(new double[]{0.25, 0.25, 0.25}, new double[]{0.4, 0.4, 0.4}, new double[]{3.1812, 3.1812, 3.1812}, new double[]{3.1812, 3.1812, 3.1812}, 0.2, new double[]{0.6, 0.3, 0.1}, 0.6);
        Material gold = new Material(new double[]{0.54725, 0.4995, 0.3745}, new double[]{0.95164, 0.80648, 0.52648}, new double[]{0.928281, 0.855802, 0.666065}, new double[]{fresnelToRefr(0.989), fresnelToRefr(0.876), fresnelToRefr(0.399)}, 0.2, new double[]{0.5, 0.3, 0.1}, 0.4);
*/
        Renderer renderer = new Renderer(FOCALLENGTH, SCREEN_WIDTH, SCREEN_HEIGHT, CMAX, RMAX, settings);
        renderer.setScene(scene);

        Thread render = new Thread(renderer::startRender);
        render.start();

        Thread show = new Thread(() -> {
            while (render.isAlive()) {
                try {
                    Thread.sleep(64);

                    renderer.show();
                    //System.out.println("show");

                } catch (InterruptedException v) {
                    v.printStackTrace();
                }
            }

            try {
                renderer.saveBuffer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        show.start();
    }


    public static double fresnelToRefr(double fresnel) {
        double sqrt = Math.sqrt(fresnel);
        return (1 + sqrt) / (1 - sqrt);
    }
}
