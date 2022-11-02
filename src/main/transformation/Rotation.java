package main.transformation;

import main.Utility;
import main.Vector;

public class Rotation extends Transformation {
    public Rotation() {
        super();
    }

    /**
     * Will add an additional transformation to the current already applied transformation
     * @param input: Vector
     * @return inverse transformed vector.
     */
    @Override
    public Vector transform(Vector input) {
        return new Vector(Utility.multiplyMatrices(input.getCoords(), getTransformation()));
    }

    public Rotation rotateX(double angle) {
        angle = Math.toRadians(angle);

        double[][] rotation = { {1, 0, 0, 0},
                                {0, Math.cos(angle), -Math.sin(angle), 0},
                                {0, Math.sin(angle), Math.cos(angle), 0},
                                {0, 0, 0, 1}};

        setTransformation(Utility.multiplyMatrices(getTransformation(), rotation));

        return this;
    }

    public Rotation rotateY(double angle) {
        angle = Math.toRadians(angle);

        double[][] rotation = { {Math.cos(angle), 0, Math.sin(angle), 0},
                                {0, 1, 0, 0},
                                {-Math.sin(angle), 0, Math.cos(angle), 0},
                                {0, 0, 0, 1}};

        setTransformation(Utility.multiplyMatrices(getTransformation(), rotation));

        return this;
    }

    public Rotation rotateZ(double angle) {
        angle = Math.toRadians(angle);

        double[][] rotation = { {Math.cos(angle), -Math.sin(angle), 0, 0},
                                {Math.sin(angle), Math.cos(angle), 0, 0},
                                {0, 0, 1, 0},
                                {0, 0, 0, 1}};

        setTransformation(Utility.multiplyMatrices(getTransformation(), rotation));

        return this;
    }
}
