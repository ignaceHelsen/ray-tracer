package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Vector;

public class Plane extends Object {
    public Plane(Material material) {
        super(material);
    }

    // TODO: cube, cilinder: originalRay -> ray & rotation: inverse and originalRay -> ray
    @Override
    public Intersection getFirstHitPoint(Ray originalRay) {
        Ray ray;

        if (getTransformation() != null)
            ray = new Ray(getTransformation().transformInverse(originalRay.getS()), getTransformation().transformInverse(originalRay.getDir()));
        else
            ray = originalRay.clone();

        double th = -(ray.getS().getZ() / ray.getDir().getZ());

        double x = ray.getS().getX() + ray.getDir().getX() * th;
        double y = ray.getS().getY() + ray.getDir().getY() * th;
        double z = ray.getS().getZ() + ray.getDir().getZ() * th;

        Vector collision = new Vector(x, y, z, 1);

        return new Intersection(null, collision, -1, th, new double[]{collision.getX(), collision.getY(), collision.getZ(), 0});
    }
}
