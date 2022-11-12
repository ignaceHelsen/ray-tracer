package main.transformation;

import main.Utility;
import main.Vector;

public class Transformation {
    private double[][] transformation = {{1, 0, 0, 0},
                                        {0, 1, 0, 0},
                                        {0, 0, 1, 0},
                                        {0, 0, 0, 1}};

    public Transformation(double[][] transformation) {
        this.transformation = transformation;
    }

    public Transformation() {

    }

    /**
     * Will add an additional transformation to the current already applied transformation
     *
     * @param input: Vector
     * @return inverse transformed vector.
     */
    public Vector transform(Vector input) {
        return new Vector(Utility.multiplyMatrices(input.getCoords(), getTransformation()));
    }

    public double[][] getTransformation() {
        return transformation;
    }

    public void setTransformation(double[][] transformation) {
        this.transformation = transformation;
    }
}
