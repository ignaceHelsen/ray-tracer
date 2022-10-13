package main.object;

import main.Ray;
import main.Utility;
import main.Vector;

import java.awt.*;
import java.util.Arrays;

public class Sphere extends Object {
    public Sphere(Color color) {
        super(color);
    }

    @Override
    public Vector getFirstHitPoint(Ray ray) {
        double a = Utility.dot(ray.getDir(), ray.getDir()); //Math.pow(ray.getDir().getX(), 2) + Math.pow(ray.getDir().getY(), 2) + Math.pow(ray.getDir().getZ(), 2);
        double b = Utility.dot(ray.getS(), ray.getDir());
        double c = Utility.dot(ray.getS(), ray.getS()) - 1; //Math.pow(ray.getS().getX(), 2) + Math.pow(ray.getS().getY(), 2) + Math.pow(ray.getS().getZ(), 2) - 1;

        double discrim = b * b - a * c;
        if (discrim < 0) return null;

        double enumerator = Math.sqrt(discrim);
        double t1 = (-b - enumerator) / a; // time of hitpoint 1
        double t2 = (-b + enumerator) / a; // time of hitpoint 2

        double[] th = Arrays.stream(new double[]{t1, t2}).filter(x -> x >= 0).toArray(); // timestamps need to be positive

        if (th.length == 0) return null;

        double x = ray.getS().getX() + ray.getDir().getX() * th[0];
        double y = ray.getS().getY() + ray.getDir().getY() * th[0];
        double z = ray.getS().getZ() + ray.getDir().getZ() * th[0];

        Vector firstCollisionPoint = new Vector(x, y, z, 1); // type = 1 since point

        // Vector secondCollisionPoint = new Vector(x, y, z, (byte) 1); // type = 1 since point */

        return firstCollisionPoint;
    }
}
