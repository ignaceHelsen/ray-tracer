package main;

import main.object.Object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private final List<Object> objects;
    private final List<Material> materials;
    private final Map<Vector, double[]> lightsources; // points
    private Camera camera;

    public Scene() {
        this.objects = new ArrayList<>();
        this.materials = new ArrayList<>();
        lightsources = new HashMap<>();
    }

    public void addObject(Object object) {
        objects.add(object);
    }

    public List<Object> getObjects() {
        return objects;
    }

    public List<Material> getMaterials() {
        return materials;
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

    public void addObjects(List<Object> objects) {
        this.objects.addAll(objects);
    }

    public void addMaterials(List<Material> materials) {
        this.materials.addAll(materials);
    }
}
