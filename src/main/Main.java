package main;

import main.sdl.SDL;
import main.sdl.Settings;

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
        // scene.addLightsource(lightsourcePink, new double[]{255, 30, 122}, 0.1);

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 0, 0);
        scene.setCamera(camera);
        Settings settings = new Settings();
        List<Material> materials = new ArrayList<>();

        try {
            settings = SDL.parseSettings("scenes/sdlChristmas.sdl", 0);
            materials = SDL.parseMaterial("scenes/materials.sdl", 0);
            scene.addMaterials(materials);
            scene.addObjects(SDL.parseObjects("scenes/sdlChristmas.sdl", scene.getMaterials(), 7));
        } catch (IOException e) {
            System.out.println("Problem reading sdl: " + e.getMessage());
        }

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
}
