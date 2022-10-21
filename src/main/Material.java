package main;

public class Material {
    private final double[] ambient;
    private final double[] diffuse;
    private final double[] specular;

    public Material(double[] ambient, double[] diffuse, double[] specular) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    public double[] getAmbient() {
        return ambient;
    }

    public double[] getDiffuse() {
        return diffuse;
    }

    public double[] getSpecular() {
        return specular;
    }
}
