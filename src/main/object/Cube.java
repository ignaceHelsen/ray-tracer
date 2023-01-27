package main.object;

import main.Intersection;
import main.Material;
import main.Ray;
import main.Vector;

public class Cube extends Object {
    private final double[][] normalVectors;

    public Cube(Material material) {
        super(material);
        this.normalVectors = new double[6][4];

        normalVectors[5] = new double[]{0, 0, -1, 0}; // top
        normalVectors[4] = new double[]{0, 0, 1, 0}; // bottom
        normalVectors[0] = new double[]{0, 1, 0, 0}; // right
        normalVectors[1] = new double[]{0, -1, 0, 0}; // left
        normalVectors[2] = new double[]{1, 0, 0, 0}; // front
        normalVectors[3] = new double[]{-1, 0, 0, 0}; // back
    }

    @Override
    public Intersection getFirstHitPoint(Ray originalRay) {
        Ray ray;

        if (getTransformation() != null)
            ray = new Ray(getTransformation().transformInverse(originalRay.getS()), getTransformation().transformInverse(originalRay.getDir()));
        else
            ray = originalRay.clone();

        double tHit, numer, denom;
        double tIn = -10000, tOut = 10000;
        int inSurf = 0, outSurf = 0; // the surface which is hit

        Vector firstCollisionPoint = null;
        Vector secondCollisionPoint = null;

        for (int i = 0; i < 6; i++) {
            switch (i) {
                case 0 -> {
                    numer = 1.0 - ray.getS().getY();
                    denom = ray.getDir().getY();
                }
                case 1 -> {
                    numer = 1.0 + ray.getS().getY();
                    denom = -ray.getDir().getY();
                }
                case 2 -> {
                    numer = 1.0 - ray.getS().getX();
                    denom = ray.getDir().getX();
                }
                case 3 -> {
                    numer = 1.0 + ray.getS().getX();
                    denom = -ray.getDir().getX();
                }
                case 4 -> {
                    numer = 1.0 - ray.getS().getZ();
                    denom = ray.getDir().getZ();
                }
                case 5 -> {
                    numer = 1.0 + ray.getS().getZ();
                    denom = -ray.getDir().getZ();
                }
                default -> {
                    numer = 0;
                    denom = 0;
                }
            }

            if (Math.abs(denom) < 0.00001) { // parallel ray
                if (numer < 0) return null; // ray is outside
            } else {
                tHit = numer / denom;
                if (denom > 0) { // exiting the side
                    if (tHit < tOut) {
                        tOut = tHit;
                        outSurf = i;
                    }
                } else { // entering the side
                    if (tHit > tIn) {
                        tIn = tHit;
                        inSurf = i;
                    }
                }
            }
            if (tIn >= tOut)
                return null;
        }

        if (tOut < 0.00001) return null;

        if (tOut > 0.00001) {
            // only an exit hit, so the ray started from inside the cube
            double xExit = ray.getS().getX() + ray.getDir().getX() * tOut;
            double yExit = ray.getS().getY() + ray.getDir().getY() * tOut;
            double zExit = ray.getS().getZ() + ray.getDir().getZ() * tOut;

            secondCollisionPoint = new Vector(xExit, yExit, zExit, 1);
        }

        if (tIn > 0.00001) {
            double xEnter = ray.getS().getX() + ray.getDir().getX() * tIn;
            double yEnter = ray.getS().getY() + ray.getDir().getY() * tIn;
            double zEnter = ray.getS().getZ() + ray.getDir().getZ() * tIn;

            firstCollisionPoint = new Vector(xEnter, yEnter, zEnter, 1);
        }

        return new Intersection(firstCollisionPoint, secondCollisionPoint, tIn, tOut, normalVectors[inSurf]);
    }
}
