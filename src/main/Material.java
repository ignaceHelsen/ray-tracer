package main;

public class Material {
    private final double[] ambient;
    private final double[] diffuse;
    private final double[] specular;
    private final double refractionIndex;
    private double roughness;

    public Material(double[] ambient, double[] diffuse, double[] specular, double refractionIndex, double roughness) {
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.refractionIndex = refractionIndex;
        this.roughness = roughness;
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

    public double getRefractionIndex() {
        return refractionIndex;
    }

    public double getRoughness() {
        return this.roughness;
    }

    public void setRoughness(double roughness) {
        this.roughness = roughness;
    }

}
