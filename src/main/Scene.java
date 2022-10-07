package main;

import main.object.Object;

import java.util.ArrayList;
import java.util.List;

// holds all objects
public class Scene {
    private final List<Object> objects;

    public Scene() {
        this.objects = new ArrayList<>();
    }

    public void addObject(Object object) {
        objects.add(object);
    }

    public List<Object> getObjects() {
        return objects;
    }
}
