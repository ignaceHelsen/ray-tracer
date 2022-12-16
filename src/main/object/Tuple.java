package main.object;

import main.Intersection;

public class Tuple<I, T> {
    private main.object.Object object;
    private Intersection intersection;

    public Tuple(main.object.Object object, Intersection intersection) {
        this.object = object;
        this.intersection = intersection;
    }

    public main.object.Object getObject() {
        return object;
    }

    public Intersection getIntersection() {
        return intersection;
    }
}
