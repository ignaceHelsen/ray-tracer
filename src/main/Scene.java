package main;

import main.object.Object;

import java.awt.*;
import java.awt.image.ColorModel;
import java.util.*;
import java.util.List;

// holds all objects
public class Scene {
    private final List<Object> objects;
    private final Map<Vector, double[]> lightsources; // points
    private Camera camera;

    public Scene() {
        this.objects = new ArrayList<>();
        lightsources = new HashMap<>();
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


    public void addLightsource(Vector lightsource, double[] color) {
        this.lightsources.put(lightsource, color);
    }

    public Map<Vector, double[]> getLightsources() {
        return this.lightsources;
    }
}
