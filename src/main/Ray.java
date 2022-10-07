package main;

public class Ray {
    private Vector s; // origin
    private Vector c; // direction

    public Ray(Vector s, Vector c) {
        this.s = s;
        this.c = c;

        // normalize c
        double v = Math.sqrt(Utility.dot(c, c));
        c.setX(c.getX()/v);
        c.setY(c.getY()/v);
        c.setZ(c.getZ()/v);
    }

    public Vector getS() {
        return s;
    }

    public void setS(Vector s) {
        this.s = s;
    }

    public Vector getC() {
        return c;
    }

    public void setC(Vector c) {
        this.c = c;
    }
}
