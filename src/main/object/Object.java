package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Utility;
import main.transformation.Transformation;

public abstract class Object {
    private Transformation transformation;
    private Material material;

    public Object(Material material) {
        this.material = material;
    }

    public abstract Intersection getFirstHitPoint(Ray ray);

    // Reverse order for transformation as for inverse
    public void addTransformation(Transformation transformation) {
        if (this.transformation == null) this.transformation = new Transformation();

        if (this.transformation.getTransformation() == null) this.transformation.setTransformation(transformation.getTransformation());
        // A x B
        else this.transformation.setTransformation(Utility.multiplyMatrices(transformation.getTransformation(), this.transformation.getTransformation()));

        if (this.transformation.getInverseTransformation() == null) this.transformation.setInverseTransformation(transformation.getInverseTransformation());
        // B x A
        else this.transformation.setInverseTransformation(Utility.multiplyMatrices(this.transformation.getInverseTransformation(), transformation.getInverseTransformation()));
    }

    public Transformation getTransformation() {
        if (this.transformation == null) return new Transformation(); // Could be anything really, as long as it does no actual transformation
        else return transformation;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }
}
