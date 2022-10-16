package main.transformation;

import main.Utility;
import main.Vector;

public class Translation extends Transformation {
    public Translation(int translateX, int translateY, int translateZ) {
        double[][] transformation = { {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};

        transformation[0][3] = -translateX;
        transformation[1][3] = -translateY;
        transformation[2][3] = -translateZ;

        setTransformation(transformation);
    }

    @Override
    public Vector transform(Vector input) {
        return new Vector(Utility.multiplyMatrices(input.getCoords(), getTransformation()));
    }
}
