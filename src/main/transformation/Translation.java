package main.transformation;

import main.Vector;

public class Translation extends Transformation {
    private int translateX;
    private int translateY;

    private final double transformation[][] = {{1,0,9}, {0, 1, 9}, {0, 0, 1}};

    public Translation(int translateX, int translateY) {
        this.translateX = translateX;
        this.translateY = translateY;
    }

    @Override
    public Vector transformation(Vector input) {
        transformation[0][2] = this.translateX;
        transformation[1][2] = this.translateY;

        double[] inputVector = {input.getX(), input.getY(), input.getZ()};
        double[] outputVector = new double[transformation.length];

        for (int i = 0; i < outputVector.length; i++) {
            int sum = 0;
            for (int j = 0; j < transformation[0].length; j++) {
                sum += transformation[i][j] * inputVector[j];
            }
            outputVector[i] = sum;
        }

        return new Vector(outputVector[0], outputVector[1], outputVector[2], 0);
    }
}
