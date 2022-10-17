package main.object;

import main.Material;
import main.Ray;
import main.Utility;
import main.Vector;

import java.util.Arrays;

public class Sphere extends Object {
    public Sphere(Material material) {
        super(material);
    }

    @Override
    public Vector getFirstHitPoint(Ray ray) {
        if (getTransformation() != null)
            ray = new Ray(getTransformation().transform(ray.getS()), getTransformation().transform(ray.getDir()));

        double a = Utility.dot(ray.getDir(), ray.getDir());
        double b = Utility.dot(ray.getS(), ray.getDir());
        double c = Utility.dot(ray.getS(), ray.getS()) - 1;

        double discrim = b * b - a * c;
        if (discrim < 0) return null;

        double discRoot = Math.sqrt(discrim);
        double t1 = (-b - discRoot) / a; // time of hitpoint 1
        double t2 = (-b + discRoot) / a; // time of hitpoint 2

        double[] th = Arrays.stream(new double[]{t1, t2}).filter(x -> x >= 0).toArray(); // timestamps need to be positive

        if (th.length == 0) return null;

        double x = ray.getS().getX() + ray.getDir().getX() * th[0];
        double y = ray.getS().getY() + ray.getDir().getY() * th[0];
        double z = ray.getS().getZ() + ray.getDir().getZ() * th[0];

        Vector firstCollisionPoint = new Vector(x, y, z, 1);

        // Vector secondCollisionPoint = new Vector(x, y, z, 1);

        return firstCollisionPoint;
    }
}
