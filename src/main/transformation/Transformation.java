package main.transformation;

import main.Vector;

public abstract class Transformation {
    private double[][] transformation = {{1, 0, 0, 0},
                                         {0, 1, 0, 0},
                                         {0, 0, 1, 0},
                                         {0, 0, 0, 1}};

    public abstract Vector transform(Vector input);

    public double[][] getTransformation() {
        return transformation;
    }

    public void setTransformation(double[][] transformation) {
        this.transformation = transformation;
    }
}
