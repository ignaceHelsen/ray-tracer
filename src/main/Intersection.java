package main;

public class Intersection {
    private Vector enter;
    private Vector exit;
    private double t1;
    private double t2;
    private double[] normalVector;

    public Intersection() {

    }

    public Intersection(Vector exit, double t2, double[] normalVector) {
        this.enter = null;
        this.exit = exit;
        this.t1 = -1; // invalid value, so that we know we can ignore it later
        this.t2 = t2;
        this.normalVector = normalVector;
    }

    public Intersection(Vector enter, Vector exit, double t1, double t2, double[] normalVector) {
        this.enter = enter;
        this.exit = exit;
        this.t1 = t1;
        this.t2 = t2;
        this.normalVector = normalVector;
    }

    public Vector getEnter() {
        return enter;
    }

    public void setEnter(Vector enter) {
        this.enter = enter;
    }

    public Vector getExit() {
        return exit;
    }

    public void setExit(Vector exit) {
        this.exit = exit;
    }

    public double getT1() {
        return t1;
    }

    public void setT1(double t1) {
        this.t1 = t1;
    }

    public double getT2() {
        return t2;
    }

    public void setT2(double t2) {
        this.t2 = t2;
    }

    public double[] getNormalVector() {
        return normalVector;
    }

    public void setNormalVector(double[] normalVector) {
        this.normalVector = normalVector;
    }
}
