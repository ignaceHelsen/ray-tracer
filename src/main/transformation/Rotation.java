package main.transformation;

public class Rotation extends Transformation {
    public Rotation(double angleX, double angleY, double angleZ) {
        super();
        this.rotateX(angleX);
        this.rotateY(angleY);
        this.rotateZ(angleZ);
    }

    public Rotation rotateX(double angle) {
        // first normal transformation, then we determine the inverse
        angle = Math.toRadians(angle);

        double[][] rotation = { {1, 0, 0, 0},
                                {0, Math.cos(angle), -Math.sin(angle), 0},
                                {0, Math.sin(angle), Math.cos(angle), 0},
                                {0, 0, 0, 1}};


        addTransformation(rotation);

        // inverse
        double[][] inverseRotation = { {1, 0, 0, 0},
                                        {0, Math.cos(angle), Math.sin(angle), 0},
                                        {0, -Math.sin(angle), Math.cos(angle), 0},
                                        {0, 0, 0, 1}};

        addInverseTransformation(inverseRotation);

        return this;
    }

    public Rotation rotateY(double angle) {
        // first normal transformation, then we determine the inverse

        angle = Math.toRadians(angle);

        double[][] rotation = { {Math.cos(angle), 0, Math.sin(angle), 0},
                                {0, 1, 0, 0},
                                {-Math.sin(angle), 0, Math.cos(angle), 0},
                                {0, 0, 0, 1}};

        addTransformation(rotation);

        // inverse
        double[][] inverseRotation = {  {Math.cos(angle), 0, -Math.sin(angle), 0},
                                        {0, 1, 0, 0},
                                        {Math.sin(angle), 0, Math.cos(angle), 0},
                                        {0, 0, 0, 1}};

        addInverseTransformation(inverseRotation);

        return this;
    }

    public Rotation rotateZ(double angle) {
        // first normal transformation, then we determine the inverse

        angle = Math.toRadians(angle);

        double[][] rotation = { {Math.cos(angle), -Math.sin(angle), 0, 0},
                                {Math.sin(angle), Math.cos(angle), 0, 0},
                                {0, 0, 1, 0},
                                {0, 0, 0, 1}};

        addTransformation(rotation);

        // inverse
        double[][] inverseRotation = {{Math.cos(angle), Math.sin(angle), 0, 0},
                                    {-Math.sin(angle), Math.cos(angle), 0, 0},
                                    {0, 0, 1, 0},
                                    {0, 0, 0, 1}};

        addInverseTransformation(inverseRotation);

        return this;
    }
}
