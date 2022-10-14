package main;

import main.object.Object;
import main.object.Sphere;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        final int SCREEN_WIDTH = 1920 / 2;
        final int SCREEN_HEIGHT = 1080 / 2;
        final int FOCALLENGTH = 2000;


        // scene
        Scene scene = new Scene();

        // camera in center of screen
        Camera camera = new Camera(FOCALLENGTH, 0, 0);

        // screen
        // every ray's S will equal the camera position
        Vector s = new Vector(camera.getLocation().getX(), camera.getLocation().getY(), camera.getLocation().getZ(), 1);

        Object sphere = new Sphere(Color.RED, 1);
        scene.addObject(sphere);

        new Renderer(FOCALLENGTH, SCREEN_WIDTH, SCREEN_HEIGHT, scene).startRender(s);
    }
}
