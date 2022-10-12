package main;

public class Vector {
    private double x, y, z;
    private double type; // 1 for point, 0 for vector

    public Vector(double x, double y, double z, double type) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getType() {
        return type;
    }

    public void setType(double type) {
        this.type = type;
    }
}
