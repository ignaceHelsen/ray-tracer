package main;

import java.util.Arrays;

public class Material {
    // all RGB values
    private String name;
    private double[] ambient;
    private double[] diffuse;
    private double[] specular;
    private double[] refractionIndex;
    private double[] kDistribution;
    private double roughness;
    private double shininess;
    private double transparency;
    private double fractionOfSpeedOfLight;

    // TODO: change k distribution so that the total equals to 1

    public Material(String name, double[] ambient, double[] diffuse, double[] specular, double[] refractionIndex, double roughness, double[] distributionK, double shininess, double transparency, double fractionOfSpeedOfLight) {
        this.name = name;
        this.ambient = Arrays.stream(ambient).map(v -> v *= 1).toArray();
        this.diffuse = Arrays.stream(diffuse).map(v -> v *= 1).toArray();
        this.specular = Arrays.stream(specular).map(v -> v *= 1).toArray();
        this.refractionIndex = refractionIndex;
        this.kDistribution = distributionK;
        this.roughness = roughness;
        this.shininess = shininess;
        this.transparency = transparency;
        this.fractionOfSpeedOfLight = fractionOfSpeedOfLight;
    }

    public Material() {

    }

    public String getName() {
        return name;
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

    public double[] getkDistribution() {
        return kDistribution;
    }

    public double getShininess() {
        return shininess;
    }

    public double getSpeedOfLight() {
        return 299_792_458 * this.fractionOfSpeedOfLight;
    }

    public double getTransparency() {
        return transparency;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmbient(double[] ambient) {
        this.ambient = ambient;
    }

    public void setDiffuse(double[] diffuse) {
        this.diffuse = diffuse;
    }

    public void setSpecular(double[] specular) {
        this.specular = specular;
    }

    public void setRefractionIndex(double[] refractionIndex) {
        this.refractionIndex = refractionIndex;
    }

    public void setkDistribution(double[] kDistribution) {
        this.kDistribution = kDistribution;
    }

    public void setRoughness(double roughness) {
        this.roughness = roughness;
    }

    public void setShininess(double shininess) {
        this.shininess = shininess;
    }

    public void setTransparency(double transparency) {
        this.transparency = transparency;
    }

    public void setFractionOfSpeedOfLight(double fractionOfSpeedOfLight) {
        this.fractionOfSpeedOfLight = fractionOfSpeedOfLight;
    }
}
