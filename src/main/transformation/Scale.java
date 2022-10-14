package main.transformation;

import main.Utility;
import main.Vector;

public class Scale extends Transformation {
    private final double scaleY, scaleZ;

    private final double[][] transformation = {{1, 0, 0, 0}, {0, 9, 0, 0}, {0, 0, 9, 0}, {0, 0, 0, 1}}; // '9' is placeholder for y & z translation

    public Scale(double scaleY, double scaleZ) {
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
    }

    @Override
    public Vector transform(Vector input) {
        transformation[1][1] = this.scaleY;
        transformation[2][2] = this.scaleZ;

        return new Vector(Utility.multiplyVectors(input.getCoords(), transformation));
    }
}
