package main.transformation;

import main.Vector;

public abstract class Transformation {
    // punt meegeven, transformatie punt terug
    // translation
    // scale
    // rotation
    private final double[][] transformation = new double[3][3];

    public abstract Vector transform(Vector input);
}
