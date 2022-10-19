package main;

public class Intersection {
    private final Vector enter;
    private Vector exit;
    private final double t1;
    private double t2;

    public Intersection(Vector enter, double t1) {
        this.enter = enter;
        this.exit = null;
        this.t1 = t1;
        this.t2 = -1;
    }

    public Intersection(Vector enter, Vector exit, double t1, double t2) {
        this.enter = enter;
        this.exit = exit;
        this.t1 = t1;
        this.t2 = t2;
    }


    public void setExit(Vector exit) {
        this.exit = exit;
    }

    public void setT2(double t2) {
        this.t2 = t2;
    }

    public Vector getEnter() {
        return enter;
    }

    public Vector getExit() {
        return exit;
    }

    public double getT1() {
        return t1;
    }

    public double getT2() {
        return t2;
    }
}
