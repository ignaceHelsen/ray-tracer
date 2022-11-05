package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Vector;

public class Plane extends Object {
    private final double DIVISION_FACTOR = 100;

    public Plane(Material material) {
        super(material);
    }

    @Override
    public Intersection getFirstHitPoint(Ray originalRay) {
        Ray ray;

        if (getTransformation() != null)
            ray = new Ray(getTransformation().transform(originalRay.getS()), getTransformation().transform(originalRay.getDir()));
        else
            ray = originalRay.clone();

        double th = -(ray.getS().getZ() / ray.getDir().getZ());

        double x = originalRay.getS().getX() + originalRay.getDir().getX() * th;
        double y = originalRay.getS().getY() + originalRay.getDir().getY() * th;
        double z = originalRay.getS().getZ() + originalRay.getDir().getZ() * th;

        Vector collision = new Vector(x, y, z, 1);

        return new Intersection(null, collision, -1, th / DIVISION_FACTOR, new double[]{collision.getX(), collision.getY(), collision.getZ(), 0});
    }
}
