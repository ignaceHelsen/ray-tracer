package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Utility;
import main.transformation.Transformation;
import main.transformation.Translation;

public abstract class Object {
    private Transformation transformation; // already the inverse!
    private final Material material;

    public Object(Material material) {
        this.material = material;
    }

    public abstract Intersection getFirstHitPoint(Ray ray);

    public void addTransformation(Transformation transformation) {
        if (this.transformation == null) this.transformation = transformation;
        else
            this.transformation.setTransformation(Utility.multiplyMatrices(this.transformation.getTransformation(), transformation.getTransformation()));
    }

    public Transformation getTransformation() {
        if (this.transformation == null) return new Translation(0, 0, 0); // Could be anything really, as long as it does no transformation
        else return transformation;
    }

    public Material getMaterial() {
        return material;
    }
}
