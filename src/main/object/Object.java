package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Utility;
import main.transformation.Scale;
import main.transformation.Transformation;

public abstract class Object {
    private Transformation transformation; // already the inverse!
    private final Material material;

    public Object(Material material) {
        this.material = material;
    }

    public abstract Intersection getFirstHitPoint(Ray ray);

    public void addTransformation(Transformation transformation) {
        if (this.transformation == null) this.transformation = new Transformation(transformation.getTransformation());
        else this.transformation.setTransformation(Utility.multiplyMatrices(this.transformation.getTransformation(), transformation.getTransformation()));
    }

    public Transformation getTransformation() {
        if (this.transformation == null) return new Transformation(); // Could be anything really, as long as it does no actual transformation
        else return transformation;
    }

    public Material getMaterial() {
        return material;
    }
}
