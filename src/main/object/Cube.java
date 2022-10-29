package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Vector;

import java.awt.*;

public class Cube extends Object {
    public Cube(Material material) {
        super(material);
    }

    @Override
    public Intersection getFirstHitPoint(Ray originalRay) {
        Ray ray;

        if (getTransformation() != null)
            ray = new Ray(getTransformation().transform(originalRay.getS()), getTransformation().transform(originalRay.getDir()));
        else
            ray = originalRay;

        double tHit, numer, denom;
        double tIn = -10000, tOut = 10000;
        double[] normal = new double[4];

        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0 -> {
                    numer = 1.0 - ray.getS().getY();
                    denom = ray.getDir().getY();
                    normal = new double[]{0, 1, 0, 1};
                }
                case 1 -> {
                    numer = 1.0 + ray.getS().getY();
                    denom = -ray.getDir().getY();
                    normal = new double[]{0, -1, 0, 1};
                }
                case 2 -> {
                    numer = 1.0 - ray.getS().getX();
                    denom = ray.getDir().getX();
                    normal = new double[]{1, 0, 0, 1};
                }
                case 3 -> {
                    numer = 1.0 + ray.getS().getX();
                    denom = -ray.getDir().getX();
                    normal = new double[]{-1, 0, 0, 1};
                }
                case 4 -> {
                    numer = 1.0 - ray.getS().getZ();
                    denom = ray.getDir().getZ();
                    normal = new double[]{0, 0, 1, 1};
                }
                case 5 -> {
                    numer = 1.0 + ray.getS().getZ();
                    denom = -ray.getDir().getZ();
                    normal = new double[]{0, 0, -1, 1};
                }
                default -> {
                    numer = 0;
                    denom = 0;
                    normal = new double[]{0, 0, 0, 1};
                }
            }

            if (Math.abs(denom) < 0.00001) { // parallel ray
                if (numer < 0) return null; // ray is outside
            } else {
                tHit = numer / denom;
                if (denom > 0) { // exiting the side
                    if (tHit < tOut) {
                        tOut = tHit;
                    }
                } else { // entering the side
                    if (tHit > tIn) {
                        tIn = tHit;
                    }
                }
            }
            if (tIn >= tOut) return null;
        }

        if (tIn < 0.00001) return null;

        double xEnter = ray.getS().getX() + ray.getDir().getX() * tIn;
        double yEnter = ray.getS().getY() + ray.getDir().getY() * tIn;
        double zEnter = ray.getS().getZ() + ray.getDir().getZ() * tIn;

        Vector firstCollisionPoint = new Vector(xEnter, yEnter, zEnter, 1);

        double xExit = ray.getS().getX() + ray.getDir().getX() * tOut;
        double yExit = ray.getS().getY() + ray.getDir().getY() * tOut;
        double zExit = ray.getS().getZ() + ray.getDir().getZ() * tOut;

        Vector secondCollisionPoint = new Vector(xExit, yExit, zExit, 1);

        return new Intersection(firstCollisionPoint, secondCollisionPoint, tIn, tOut, normal);
    }
}
