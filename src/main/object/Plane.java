package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Vector;
import main.transformation.Transformation;

public class Plane extends Object {
    private Transformation transformation;

    public Plane(Material material) {
        super(material);
    }

    @Override
    public Intersection getFirstHitPoint(Ray originalRay) {
        Ray ray;

        if (getTransformation() != null)
            ray = new Ray(getTransformation().transform(originalRay.getS()), getTransformation().transform(originalRay.getDir()));
        else
            ray = originalRay;

        double th = -(ray.getS().getZ() / ray.getDir().getZ());

        double x = ray.getS().getX() + ray.getDir().getX() * th;
        double y = ray.getS().getY() + ray.getDir().getY() * th;
        double z = ray.getS().getZ() + ray.getDir().getZ() * th;

        Vector firstCollisionPoint = new Vector(x, y, z, 1);

        return new Intersection(firstCollisionPoint, null, th, -1, new double[]{firstCollisionPoint.getX(), firstCollisionPoint.getY(), firstCollisionPoint.getZ(), 0});
    }
}
