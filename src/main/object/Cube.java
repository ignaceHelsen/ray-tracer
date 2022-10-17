package main.object;

import main.Material;
import main.Ray;
import main.Vector;

import java.awt.*;

public class Cube extends Object {
    public Cube(Material material) {
        super(material);
    }

    @Override
    public Vector getFirstHitPoint(Ray ray) {
        if (getTransformation() != null)
            ray = new Ray(getTransformation().transform(ray.getS()), getTransformation().transform(ray.getDir()));

        double tHit, numer, denom;
        double tIn = -10000, tOut = 10000;

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

        double x = ray.getS().getX() + ray.getDir().getX() * tIn;
        double y = ray.getS().getY() + ray.getDir().getY() * tIn;
        double z = ray.getS().getZ() + ray.getDir().getZ() * tIn;

        Vector firstCollisionPoint = new Vector(x, y, z, 1);

        return firstCollisionPoint;
    }
}
