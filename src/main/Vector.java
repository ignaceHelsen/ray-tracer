package main;

public class Vector {
    private final double[] coords;
    // private double type; // 1 for point, 0 for vector

    public Vector(double[] coords) {
        this.coords = coords;
    }

    public Vector(double x, double y, double z, double type) {
        coords = new double[] {x, y, z, type};
    }

    public double[] getCoords() {
        return coords;
    }

    public double getX() {
        return coords[0];
    }

    public void setX(double x) {
        coords[0] = x;
    }

    public double getY() {
        return coords[1];
    }

    public void setY(double y) {
        coords[1] = y;
    }

    public double getZ() {
        return coords[2];
    }

    public void setZ(double z) {
        coords[2] = z;
    }

    public double getType() {
        return coords[3];
    }

    public void setType(double type) {
        coords[3] = type;
    }
}
