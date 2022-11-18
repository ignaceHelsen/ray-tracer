package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Utility;
import main.transformation.Transformation;

public abstract class Object {
    private Transformation transformation;
    private Transformation inverseTransformation;
    private final Material material;

    public Object(Material material) {
        this.material = material;
    }

    public abstract Intersection getFirstHitPoint(Ray ray);

    public void addTransformation(Transformation transformation) {
        if (this.transformation == null) this.transformation = new Transformation(transformation.getTransformation(), null);
        else this.transformation.setTransformation(Utility.multiplyMatrices(this.transformation.getTransformation(), transformation.getTransformation()));
    }

    public void addInverseTransformation(Transformation transformation) {
        if (this.inverseTransformation == null) this.inverseTransformation = new Transformation(null, transformation.getInverseTransformation());
        else this.inverseTransformation.setInverseTransformation(Utility.multiplyMatrices(this.inverseTransformation.getInverseTransformation(), transformation.getInverseTransformation()));
    }

    public Transformation getTransformation() {
        if (this.transformation == null) return new Transformation(); // Could be anything really, as long as it does no actual transformation
        else return transformation;
    }

    public Transformation getInverseTransformation() {
        if (this.inverseTransformation == null) return new Transformation(); // Could be anything really, as long as it does no actual transformation
        else return inverseTransformation;
    }

    public Material getMaterial() {
        return material;
    }
}
