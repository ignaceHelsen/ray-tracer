package main.transformation;

public class Scale extends Transformation {
    /**
     * Scales all axis
     * @param scale: scale
     */
    public Scale(double scale) {
        setTransform(scale, scale, scale);
    }
    public Scale(double scaleX, double scaleY, double scaleZ) {
        setTransform(scaleX, scaleY, scaleZ);
    }

    private void setTransform(double scaleX, double scaleY, double scaleZ) {
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
