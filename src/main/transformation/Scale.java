package main.transformation;

import main.Utility;
import main.Vector;

public class Scale extends Transformation {
    public Scale(double scaleX, double scaleY, double scaleZ) {
        double[][] transformation = {{1, 0, 0, 0},
                                     {0, 1, 0, 0},
                                     {0, 0, 1, 0},
                                     {0, 0, 0, 1}};

        transformation[0][0] = 1 / scaleX;
        transformation[1][1] = 1 / scaleY;
        transformation[2][2] = 1 / scaleZ;

        setTransformation(transformation);
    }
}
