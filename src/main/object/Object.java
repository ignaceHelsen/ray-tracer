package main.object;

import main.Material;
import main.Ray;
import main.Utility;
import main.Vector;
import main.transformation.Transformation;

public abstract class Object {
    private Transformation transformation;
    private final Material material;

    public Object(Material material) {
        this.material = material;
    }

    public abstract Vector getFirstHitPoint(Ray ray);


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
