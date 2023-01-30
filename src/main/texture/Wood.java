package main.texture;

import java.util.Random;

public class Wood extends Texture {
    private final double D = 0.2;
    private final double A = D * Math.random();
    private final double M = new Random().nextDouble(0.8-0.6) + 0.6; // thickness

    @Override
    public double[] getTexture(double x, double y, double z, double angle) {
        double r = Math.sqrt(y*y + z*z);
        double texture = D + A * rings(r/M + new Random().nextDouble(5-4) + 4 * Math.sin((Math.toRadians(angle)/5) + 1 * x));

        return new double[] {texture, texture, texture};
    }

    private double rings(double r) {
        return r % 2;
    }
}
