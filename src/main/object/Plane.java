package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Vector;

public class Plane extends Object {
    public Plane(Material material) {
        super(material);
    }

    @Override
    public Intersection getFirstHitPoint(Ray originalRay) {
        Ray ray;

        if (getTransformation() != null)
            ray = new Ray(getTransformation().transformInverse(originalRay.getS()), getTransformation().transformInverse(originalRay.getDir()));
        else
            ray = originalRay.clone();

        double th = -(ray.getS().getZ() / ray.getDir().getZ());

        if (th < 0) return null;

        double x = ray.getS().getX() + ray.getDir().getX() * th;
        double y = ray.getS().getY() + ray.getDir().getY() * th;
        double z = ray.getS().getZ() + ray.getDir().getZ() * th;

        Vector collision = new Vector(x, y, z, 1);

        return new Intersection(null, collision, -1, th, new double[]{0, 0, -1, 0});
    }
}
