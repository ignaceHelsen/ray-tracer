package main.transformation;

import main.Utility;
import main.Vector;

public class Rotation extends Transformation {
    // TODO: add inverse transformation
    public Rotation() {
        super();
    }

    public Rotation rotateX(double angle) {
        // first normal transformation, then we determine the inverse
        angle = Math.toRadians(angle);

        double[][] rotation = { {1, 0, 0, 0},
                                {0, Math.cos(angle), -Math.sin(angle), 0},
                                {0, Math.sin(angle), Math.cos(angle), 0},
                                {0, 0, 0, 1}};

        setTransformation(Utility.multiplyMatrices(getTransformation(), rotation));

        // inverse
        double[][] inverseRotation = { {1, 0, 0, 0},
                                        {0, Math.cos(angle), Math.sin(angle), 0},
                                        {0, -Math.sin(angle), Math.cos(angle), 0},
                                        {0, 0, 0, 1}};
        setInverseTransformation(Utility.multiplyMatrices(getInverseTransformation(), inverseRotation));

        return this;
    }

    public Rotation rotateY(double angle) {
        // first normal transformation, then we determine the inverse

        angle = Math.toRadians(angle);

        double[][] rotation = { {Math.cos(angle), 0, Math.sin(angle), 0},
                                {0, 1, 0, 0},
                                {-Math.sin(angle), 0, Math.cos(angle), 0},
                                {0, 0, 0, 1}};

        setTransformation(Utility.multiplyMatrices(getTransformation(), rotation));

        // inverse
        double[][] inverseRotation = {  {Math.cos(angle), 0, -Math.sin(angle), 0},
                                        {0, 1, 0, 0},
                                        {Math.sin(angle), 0, Math.cos(angle), 0},
                                        {0, 0, 0, 1}};

        setInverseTransformation(Utility.multiplyMatrices(getInverseTransformation(), inverseRotation));

        return this;
    }

    public Rotation rotateZ(double angle) {
        // first normal transformation, then we determine the inverse

        angle = Math.toRadians(angle);

        double[][] rotation = { {Math.cos(angle), -Math.sin(angle), 0, 0},
                                {Math.sin(angle), Math.cos(angle), 0, 0},
                                {0, 0, 1, 0},
                                {0, 0, 0, 1}};

        setTransformation(Utility.multiplyMatrices(getTransformation(), rotation));

        // inverse
        double[][] inverseRotation = {  {Math.cos(angle), 0, Math.sin(angle), 0},
                                        {0, 1, 0, 0},
                                        {-Math.sin(angle), 0, Math.cos(angle), 0},
                                        {0, 0, 0, 1}};

        setInverseTransformation(Utility.multiplyMatrices(getInverseTransformation(), inverseRotation));

        return this;
    }
}
