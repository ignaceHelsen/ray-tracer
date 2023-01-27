package main;

import main.object.Object;
import main.object.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private final List<Object> objects;
    private final List<Material> materials;
    private final Map<Vector, Tuple<double[], Double>> lightsources; // points
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


    public void addLightsource(Vector lightsource, double[] color, double dw) {
        this.lightsources.put(lightsource, new Tuple<>(color, dw));
    }

    public Map<Vector, Tuple<double[], Double>> getLightsources() {
        return this.lightsources;
    }

    public void addObjects(List<Object> objects) {
        this.objects.addAll(objects);
    }

    public void addMaterials(List<Material> materials) {
        this.materials.addAll(materials);
    }
}
