package main.object;

import main.*;
import main.transformation.Transformation;

public abstract class Object {
    private Transformation transformation; // already the inverse!
    private final Material material;

    public Object(Material material) {
        this.material = material;
    }

    public abstract Intersection getFirstHitPoint(Ray ray);

    public void addTransformation(Transformation transformation) {
        if (this.transformation == null) this.transformation = transformation;
        else this.transformation.setTransformation(Utility.multiplyMatrices(this.transformation.getTransformation(), transformation.getTransformation()));
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public Material getMaterial() {
        return material;
    }
}
