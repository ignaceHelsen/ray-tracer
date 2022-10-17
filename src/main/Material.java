package main;

import java.awt.*;

public class Material {
    private final Color color;
    private final double ambient;
    private final double diffuse;
    private final double specular;

    public Material(Color color, double ambient, double diffuse, double specular) {
        this.color = color;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    public Color getColor() {
        return color;
    }

    public double getAmbient() {
        return ambient;
    }

    public double getDiffuse() {
        return diffuse;
    }

    public double getSpecular() {
        return specular;
    }
}
