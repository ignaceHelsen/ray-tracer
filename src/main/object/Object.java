package main.object;

import main.Ray;
import main.Utility;
import main.Vector;
import main.transformation.Transformation;

import java.awt.*;

public abstract class Object {
    private Transformation transformation;

    private final Color color;

    public abstract Vector getFirstHitPoint(Ray ray);

    public Object(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void addTransformation(Transformation transformation) {
        if (this.transformation == null) this.transformation = transformation;
        else this.transformation.setTransformation(Utility.multiplyMatrices(this.transformation.getTransformation(), transformation.getTransformation()));
    }

    public Transformation getTransformation() {
        return transformation;
    }
}
