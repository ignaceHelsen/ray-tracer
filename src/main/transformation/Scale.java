package main.transformation;

public class Scale extends Transformation {
    /**
     * Scales all axis
     * @param scale: scale
     */
    public Scale(double scale) {
        createTransform(scale, scale, scale);
        createInverseTransformation(scale, scale, scale);
    }
    public Scale(double scaleX, double scaleY, double scaleZ) {
        createTransform(scaleX, scaleY, scaleZ);
        createInverseTransformation(scaleX, scaleY, scaleZ);
    }

    private void createTransform(double scaleX, double scaleY, double scaleZ) {
        double[][] transformation = {{1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};

        transformation[0][0] = scaleX;
        transformation[1][1] = scaleY;
        transformation[2][2] = scaleZ;

        setTransformation(transformation);
    }

    private void createInverseTransformation(double scaleX, double scaleY, double scaleZ) {
        double[][] transformation = {{1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}};

        transformation[0][0] = 1 / scaleX;
        transformation[1][1] = 1 / scaleY;
        transformation[2][2] = 1 / scaleZ;

        setInverseTransformation(transformation);
    }
}
