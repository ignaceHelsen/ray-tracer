package main;

public class Camera {
    Vector location;

    public Camera(double x, int y, int z) {
        this.location = new Vector(x, y, z, (byte) 1);
    }

    public Vector getLocation() {
        return location;
    }
}
