package main.object;

import main.Intersection;

public class Tuple<I, T> {
    private I firstObject;
    private T secondObject;

    public Tuple(I firstObject, T secondObject) {
        this.firstObject = firstObject;
        this.secondObject = secondObject;
    }

    public Object getObject() {
        return (Object) firstObject;
    }

    public Intersection getIntersection() {
        return (Intersection) secondObject;
    }

    public double[] getLightColor() {
        return (double[]) firstObject;
    }

    public double getDw() {
        return (double) secondObject;
    }
}
