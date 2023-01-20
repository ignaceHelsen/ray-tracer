package test;

import main.*;
import main.object.Object;
import main.object.Plane;
import main.object.Sphere;
import main.transformation.Translation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RayTest {
    @Test
    public void testShadow() {
        Material emerald = new Material("emerald", new double[]{0.0215, 0.1745, 0.0215}, new double[]{0.07568, 0.61424, 0.07568}, new double[]{0.633, 0.727811, 0.633}, new double[]{2.417, 2.417, 2.417}, 0.2, new double[]{0.2, 0.49, 0.49}, 0.6, 0.5, 0.6249);

        Object plane = new Plane(emerald);
        plane.addTransformation(new Translation(0, 0, 100));

        Ray ray = new Ray(new Vector(0, 0, 1000, 1), new Vector(0, 0, -1000, 0));

        Intersection intersection = plane.getFirstHitPoint(ray);

        System.out.print(intersection);
    }

    @Test
    public void testSphere() {
        Material emerald = new Material("emerald", new double[]{0.0215, 0.1745, 0.0215}, new double[]{0.07568, 0.61424, 0.07568}, new double[]{0.633, 0.727811, 0.633}, new double[]{2.417, 2.417, 2.417}, 0.2, new double[]{0.2, 0.49, 0.49}, 0.6, 0.5, 0.6249);

        Object sphere = new Sphere(emerald);

        List<Ray> rays = new ArrayList<>();
        rays.add(new Ray(new Vector(4, 0, 0, 1), new Vector(-4, 3, 4, 0)));
        rays.add(new Ray(new Vector(4, 0, 1, 1), new Vector(-4, 0, 0, 0)));
        rays.add(new Ray(new Vector(4, -2, 2, 1), new Vector(-7, 3, -4, 0)));
        rays.add(new Ray(new Vector(4, -2, 2, 1), new Vector(7, -3, 4, 0)));
        rays.add(new Ray(new Vector(0.5, 0.3, 0.2, 0), new Vector(-3.5, 0.7, -2.2, 0)));
        /*rays.add(new Ray(new Vector(-7, 8, 2, 0), new Vector(6, 2, 4, 0)));
        rays.add(new Ray(new Vector(-5, -5, 0, 0), new Vector(0, 5, 0, 0)));
        rays.add(new Ray(new Vector(-7, 8, 2, 0), new Vector(13, -7, 1, 0)));
        rays.add(new Ray(new Vector(-7, 8, 2, 0), new Vector(-13, 7, -1, 0)));
        rays.add(new Ray(new Vector(0.5, 0.3, 0.2, 0), new Vector(-3.5, 0.7, -2.2, 0)));
        rays.add(new Ray(new Vector(-2, 3, 5, 0), new Vector(3, -1, -2, 0)));
        rays.add(new Ray(new Vector(-3, 3, 3, 0), new Vector(4, 0, 0, 0)));
        rays.add(new Ray(new Vector(-3, 3, 2, 0), new Vector(4, 0, 1, 0)));
        rays.add(new Ray(new Vector(-3, 3, 2, 0), new Vector(-4, 0, -1, 0)));
        rays.add(new Ray(new Vector(2.5, 3.5, 3.5, 0), new Vector(-1.5, -0.5, -0.5, 0)));
        rays.add(new Ray(new Vector(-3, 2, -1, 0), new Vector(-4, -4, 4, 0)));
        rays.add(new Ray(new Vector(-3, 1.5, 5, 0), new Vector(2, 0, 0, 0)));
        rays.add(new Ray(new Vector(-3, 2, -1, 0), new Vector(2, -5, 5, 0)));
        rays.add(new Ray(new Vector(-3, 2, -1, 0), new Vector(-2, 5, -5, 0)));
        rays.add(new Ray(new Vector(-3, 0, 6, 0), new Vector(2, 1.5, -2, 0)));*/

        for (Ray r : rays) {
            Intersection coords = sphere.getFirstHitPoint(r);
            if (coords == null) {
                System.out.println("NO HIT");
                continue;
            }

            if (coords.getEnter() != null) {
                System.out.printf("Enter: %f %f %f\t", coords.getEnter().getX(), coords.getEnter().getY(), coords.getEnter().getZ());
            } else {
                System.out.print("\t\t\t\t\t\t\t\t\t");
            }
            System.out.printf("Exit: %f %f %f\n", coords.getExit().getX(), coords.getExit().getY(), coords.getExit().getZ());
        }
    }

    @Test
    public void testPlane() {
        Material emerald = new Material("emerald", new double[]{0.0215, 0.1745, 0.0215}, new double[]{0.07568, 0.61424, 0.07568}, new double[]{0.633, 0.727811, 0.633}, new double[]{2.417, 2.417, 2.417}, 0.2, new double[]{0.2, 0.49, 0.49}, 0.6, 0.5, 0.6249);

        Object plane = new Plane(emerald);

        Ray ray = new Ray(new Vector(4, 1, 3, 1), new Vector(-3, -5, -3, 0));

        var coords = plane.getFirstHitPoint(ray);
        if (coords == null) {
            System.out.println("NO HIT");
        }

        System.out.printf("%f %f %f\n", coords.getEnter().getX(), coords.getEnter().getY(), coords.getEnter().getZ());
    }

    @Test
    public void testRefraction() {
        Material emerald = new Material("ruby", new double[]{0.0215, 0.1745, 0.0215}, new double[]{0.07568, 0.61424, 0.07568}, new double[]{0.633, 0.727811, 0.633}, new double[]{2.417, 2.417, 2.417}, 0.2, new double[]{0.2, 0.49, 0.49}, 0.6, 0.5, 0.6249);

        double[] t = new double[4];
        Vector vectorNormalVector = new Vector(0, 0, -1, 0);
        Vector dir = new Vector(0, 0, 1, 0);
        double dirDotNormalvector = Utility.dot(dir, vectorNormalVector);

        double c1 = 299_792_458 * 0.9997; // previous object is air
        double c2 = 299_792_458 * 0.9997;
        double factor = c2 / c1;
        double thetaOne = getAngle(dir.getCoords(), vectorNormalVector.getCoords());
        double thetaTwo = Math.asin(Math.sin(thetaOne) * factor);

        for (int i = 0; i < t.length; i++) {
            t[i] = factor * dir.getCoords()[i] + (factor * dirDotNormalvector - Math.cos(thetaTwo)) * vectorNormalVector.getCoords()[i];
        }

        t = Utility.normalize(t);

        System.out.println();
    }

    private double getAngle(double[] vectorOne, double[] vectorTwo) {
        double cosine = Utility.dot(vectorOne, vectorTwo) / (Utility.norm(vectorOne) * Utility.norm(vectorTwo));
        return Math.acos(cosine);
    }
}
