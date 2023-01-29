package main.object;

import main.*;
import main.texture.Texture;
import main.transformation.Transformation;

public abstract class Object {
    private Transformation transformation;
    private Material material;
    private Texture texture;

    public Object(Material material) {
        this.material = material;
    }

    public abstract Intersection getFirstHitPoint(Ray ray);

    // Reverse order for transformation as for inverse
    public void addTransformation(Transformation transformation) {
        if (this.transformation == null) this.transformation = new Transformation();

        // NORMAL transformation
        if (this.transformation.getTransformation() == null) this.transformation.setTransformation(transformation.getTransformation());
        else this.transformation.setTransformation(Utility.multiplyMatrices(transformation.getTransformation(), this.transformation.getTransformation()));

        // INVERSE transformation
        if (this.transformation.getInverseTransformation() == null) this.transformation.setInverseTransformation(transformation.getInverseTransformation());
        else this.transformation.setInverseTransformation(Utility.multiplyMatrices(this.transformation.getInverseTransformation(), transformation.getInverseTransformation()));
    }

    public Transformation getTransformation() {
        if (this.transformation == null) return new Transformation();
        else return transformation;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public double[] getTexture(double x, double y, double z) {
        if (this.texture == null) return new double[] {1, 1, 1};
        return texture.getTexture(x, y, z, 90);
    }

    public void setTexture(Texture texture) {
        // texture can be null
        this.texture = texture;
    }
}
