package main.transformation;

import main.Utility;
import main.Vector;

public class Rotation extends Transformation {
    public Rotation() {
        super();
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
