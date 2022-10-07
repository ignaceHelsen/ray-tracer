package main.object;

import main.Ray;
import main.Utility;
import main.Vector;

import java.awt.*;
import java.util.Arrays;

public class Sphere extends Object{
    public Sphere(Color color) {
        super(color);
    }

    @Override
    public Vector getCollision(Ray ray) {
        double a = Math.pow(ray.getC().getX(), 2) + Math.pow(ray.getC().getY(), 2) + Math.pow(ray.getC().getZ(), 2);
        double b = Utility.dot(ray.getS(), ray.getC());
        double c = Math.pow(ray.getS().getX(), 2) + Math.pow(ray.getS().getY(), 2) + Math.pow(ray.getS().getZ(), 2) - 1;

        double discrim = b*b - a*c;
        if (discrim < 0) return null;

        double enumerator = Math.sqrt(b * b - a * c) / a;
        double t1 = -(b / a) + enumerator;
        double t2 = -(b / a) - enumerator;

        double[] th = Arrays.stream(new double[]{t1, t2}).filter(x -> x >= 0).toArray(); // timestamps need to be positive

        double x = ray.getS().getX()*(1-th[0]);
        double y = ray.getS().getY()*(1-th[0]);
        double z = ray.getS().getZ()*(1-th[0]);

        Vector firstCollisionPoint = new Vector(x, y, z, (byte) 1); // type = 1 since point

        /* x = ray.getS().getX()*(1-th[1]);
        y = ray.getS().getY()*(1-th[1];
        z = ray.getS().getZ()*(1-th[1]);

        Vector secondCollisionPoint = new Vector(x, y, z, (byte) 1); // type = 1 since point */

        return firstCollisionPoint;

        // TODO: create java 2d, and paint pixel (drawline)
        // TODO: normalize c of vector
    }
}
