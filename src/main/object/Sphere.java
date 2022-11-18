package main.object;

import main.*;

import java.util.Arrays;

public class Sphere extends Object {
    public Sphere(Material material) {
        super(material);
    }

    @Override
    public Intersection getFirstHitPoint(Ray originalRay) {
        Ray ray;

        if (getTransformation() != null)
            ray = new Ray(getTransformation().transformInverse(originalRay.getS()), getTransformation().transformInverse(originalRay.getDir()));
        else
            ray = originalRay.clone();

        double a = Utility.dot(ray.getDir(), ray.getDir());
        double b = Utility.dot(ray.getS(), ray.getDir());
        double c = Utility.dot(ray.getS(), ray.getS()) - 1;

        double discrim = (b * b) - (a * c);
        if (discrim < 0) return null;

        double discRoot = Math.sqrt(discrim);
        double t1 = (-b - discRoot) / a; // time of hitpoint 1
        double t2 = (-b + discRoot) / a; // time of hitpoint 2

        if (t1 < 0 && t2 < 0) return null;

        double[] th = Arrays.stream(new double[]{t1, t2}).filter(x -> x >= 0).toArray(); // timestamps need to be positive

        // we got at least one hit, calculate x y & z
        double x = ray.getS().getX() + ray.getDir().getX() * th[0];
        double y = ray.getS().getY() + ray.getDir().getY() * th[0];
        double z = ray.getS().getZ() + ray.getDir().getZ() * th[0];

        Intersection intersection = new Intersection();
        Vector point = new Vector(x, y, z, 1);

        if (th.length == 1) {  // only one hit, could be either a tangent hit or an exit hit
            // 1 hit found, the previous calculated x y & z are now the exit points
            // just regard it as an exit hit
            intersection.setExit(point);
            intersection.setT2(th[0]);
        } else {
            // 2 hits found, the previous calculated x y & z are now the enter points
            intersection.setEnter(point);
            intersection.setT1(th[0]);

            double xExit = ray.getS().getX() + ray.getDir().getX() * th[1];
            double yExit = ray.getS().getY() + ray.getDir().getY() * th[1];
            double zExit = ray.getS().getZ() + ray.getDir().getZ() * th[1];

            Vector exit = new Vector(xExit, yExit, zExit, 1);
            intersection.setExit(exit);
            intersection.setT2(th[1]);
        }

        if (th.length == 1) intersection.setNormalVector(intersection.getExit().getCoords());
        else intersection.setNormalVector(intersection.getEnter().getCoords());

        // reset type of vector
        intersection.setNormalVector(new double[] {intersection.getNormalVector()[0], intersection.getNormalVector()[1], intersection.getNormalVector()[2], 0});

        return intersection;
    }
}
