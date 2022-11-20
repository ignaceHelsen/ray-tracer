package main;

import java.util.Arrays;

public class Material {
    // all RGB values
    private final double[] ambient;
    private final double[] diffuse;
    private final double[] specular;
    private final double[] refractionIndex;
    private final double[] kDistribution;
    private double roughness;
    private double shininess;

    public Material(double[] ambient, double[] diffuse, double[] specular, double[] refractionIndex, double roughness, double[] distributionK, double shininess) {
        this.ambient = Arrays.stream(ambient).map(v -> v *= 1).toArray();
        this.diffuse = Arrays.stream(diffuse).map(v -> v *= 1).toArray();
        this.specular = Arrays.stream(specular).map(v -> v *= 1).toArray();
        this.refractionIndex = refractionIndex;
        this.kDistribution = distributionK;
        this.roughness = roughness;
        this.shininess = shininess;
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

    public double[] getRefractionIndex() {
        return refractionIndex;
    }

    public double getRoughness() {
        return this.roughness;
    }

    public void setRoughness(double roughness) {
        this.roughness = roughness;
    }

    public double[] getkDistribution() {
        return kDistribution;
    }

    public double getShininess() {
        return shininess;
    }
}
