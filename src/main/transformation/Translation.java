package main.transformation;

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
}
