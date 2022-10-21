package main;

import main.object.Object;

import java.awt.*;
import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// holds all objects
public class Scene {
    private final List<Object> objects;
    private final Vector lightsource; // point
    private double[] lightsourceColor;
    private Camera camera;

    public Scene(Vector lightsource, double[] lightsourceColor) {
        this.lightsource = lightsource;
        this.objects = new ArrayList<>();
        this.lightsourceColor = lightsourceColor;
    }

    public void addObject(Object object) {
        objects.add(object);
    }

    public List<Object> getObjects() {
        return objects;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera() {
        return camera;
    }

    public Vector getLightsource() {
        return lightsource;
    }

    public void setLightsourceColor(double[] lightsourceColor) {
        this.lightsourceColor = lightsourceColor;
    }

    public double[] getLightsourceColor() {
        return lightsourceColor;
    }
}
