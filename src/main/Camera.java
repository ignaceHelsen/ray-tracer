package main;

public class Camera {
    Vector location;

    public Camera(double x, double y, double z) {
        this.location = new Vector(x, y, z, (byte) 1);
    }

    public Vector getLocation() {
        return location;
    }
}
