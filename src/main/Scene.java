package main;

import main.object.Object;

import java.util.ArrayList;
import java.util.List;

// holds all objects
public class Scene {
    private final List<Object> objects;
    private final Vector lightsource; // point
    private Camera camera;

    public Scene(Vector lightsource) {
        this.lightsource = lightsource;
        this.objects = new ArrayList<>();
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
}
