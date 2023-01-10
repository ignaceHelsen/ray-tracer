package main.transformation;

import main.Utility;
import main.Vector;

public class Transformation {
    private double[][] transformation = {{1, 0, 0, 0},
                                        {0, 1, 0, 0},
                                        {0, 0, 1, 0},
                                        {0, 0, 0, 1}};

    private double[][] inverseTransformation = {{1, 0, 0, 0},
                                                {0, 1, 0, 0},
                                                {0, 0, 1, 0},
                                                {0, 0, 0, 1}};

    public Transformation(double[][] transformation, double[][] inverseTransformation) {
        this.transformation = transformation;
        this.inverseTransformation = inverseTransformation;
    }

    public Transformation() {
        // just creates the identity matrices
    }

    /**
     * Will add an additional transformation to the current already applied transformation
     *
     * @param input: Vector
     * @return transformed vector.
     */
    public Vector transform(Vector input) {
        return new Vector(Utility.multiplyMatrices(input.getCoords(), getTransformation()));
    }

    /**
     * Will add an additional inverse transformation to the current already applied transformation
     *
     * @param input: Vector
     * @return inverse transformed vector.
     */
    public Vector transformInverse(Vector input) {
        return new Vector(Utility.multiplyMatrices(input.getCoords(), getInverseTransformation()));
    }

    public double[][] getTransformation() {
        return transformation;
    }

    public void setTransformation(double[][] transformation) {
        this.transformation = transformation;
    }

    public void setInverseTransformation(double[][] transformation) {
        this.inverseTransformation = transformation;
    }

    public double[][] getInverseTransformation() {
        return inverseTransformation;
    }

    public void addTransformation(double[][] transformation) {
        this.transformation = Utility.multiplyMatrices(transformation, this.transformation);
    }

    public void addInverseTransformation(double[][] inverseTransformation) {
        this.inverseTransformation = Utility.multiplyMatrices(inverseTransformation, this.inverseTransformation);
    }
}
