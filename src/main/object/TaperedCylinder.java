package main.object;

import main.Ray;
import main.Utility;
import main.Vector;

import java.awt.*;
import java.util.Arrays;

public class TaperedCylinder extends Object {
    private final double ratio; // closer to 1: cylinder, closer to 0: cone

    public TaperedCylinder(Color color, double ratio) {
        super(color);
        this.ratio = ratio;
    }

    @Override
    public Vector getFirstHitPoint(Ray ray) {
        if (getTransformation() != null)
            ray = new Ray(getTransformation().transform(ray.getS()), getTransformation().transform(ray.getDir()));

        double d = (this.ratio - 1)*ray.getDir().getZ();
        double f = 1+(this.ratio - 1)*ray.getS().getZ();

        double a = Math.pow(ray.getDir().getX(), 2) + Math.pow(ray.getDir().getY(), 2) - d*d;
        double b = ray.getS().getX()*ray.getDir().getX() + ray.getS().getY()*ray.getDir().getY() - f*d;
        double c = Math.pow(ray.getS().getX(), 2) + Math.pow(ray.getS().getY(), 2) * f*f;

        double discrim = b * b - a * c;
        if (discrim < 0) return null;

        // if z-component lies between 0 and 1: hit

        double discRoot = Math.sqrt(discrim);
        double t1 = (-b - discRoot) / a; // time of hitpoint 1
        double t2 = (-b + discRoot) / a; // time of hitpoint 2

        double[] th = Arrays.stream(new double[]{t1, t2}).filter(x -> x >= 0).toArray(); // timestamps need to be positive

        if (th.length == 0) return null;

        double x = ray.getS().getX() + ray.getDir().getX() * th[0];
        double y = ray.getS().getY() + ray.getDir().getY() * th[0];
        double z = ray.getS().getZ() + ray.getDir().getZ() * th[0];

        if (z > 2 || z < 0) return null;

        Vector firstCollisionPoint = new Vector(x, y, z, 1);

        // Vector secondCollisionPoint = new Vector(x, y, z, 1);

        return firstCollisionPoint;
    }
}
