package main.texture;

public class Checkerboard extends Texture {
    public double[] getTexture(double x, double y, double z, double angle) {
        boolean u = ((int) (x * 0.0125)) % 2 == 0;
        boolean v = ((int) (y * 0.0125)) % 2 == 0;
        boolean w = ((int) (z * 0.0125)) % 2 == 0;

        if (u ^ v ^ w) {
            if ((x < 0 && y > 0) || (x > 0 && y < 0)) {
                return new double[]{0, 0, 0};
            } else {
                return new double[]{1, 1, 1};
            }
        } else if ((x < 0 && y > 0) || (x > 0 && y < 0)) {
            return new double[]{1, 1, 1};
        } else {
            return new double[]{0, 0, 0};
        }
    }
}
