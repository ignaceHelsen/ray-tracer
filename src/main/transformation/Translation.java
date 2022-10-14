package main.transformation;

import main.Utility;
import main.Vector;

public class Translation extends Transformation {
    private final int translateY, translateZ;

    private final double[][] transformation = {{1, 0, 0, 0}, {0, 1, 0, 9}, {0, 0, 1, 9}, {0, 0, 0, 1}}; // '9' is placeholder for y & z translation

    public Translation(int translateY, int translateZ) {
        this.translateY = translateY;
        this.translateZ = translateZ;
    }

    @Override
    public Vector transform(Vector input) {
        transformation[1][3] = this.translateY;
        transformation[2][3] = this.translateZ;

        // TODO: inverse matrix

        return new Vector(Utility.multiplyVectors(input.getCoords(), transformation));
    }
}
