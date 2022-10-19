package test;

import main.Material;
import main.Ray;
import main.Vector;
import main.object.Object;
import main.object.Plane;
import main.object.Sphere;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RayTest {
    @Test
    public void testSphere() {
        Object sphere = new Sphere(new Material(Color.red, 0.5, 0.5, 0.5));

        List<Ray> rays = new ArrayList<>();
        rays.add(new Ray(new Vector(4, 0, 0, 1), new Vector(-4, 3, 4, 0)));
        rays.add(new Ray(new Vector(4, 0, 1, 1), new Vector(-4, 0, 0, 0)));
        rays.add(new Ray(new Vector(4, -2, 2, 1), new Vector(-7, 3, -4, 0)));
        rays.add(new Ray(new Vector(4, -2, 2, 1), new Vector(7, -3, 4, 0)));
        rays.add(new Ray(new Vector(0.5, 0.3, 0.2, 0), new Vector(-3.5, 0.7, -2.2, 0)));
        rays.add(new Ray(new Vector(-7, 8, 2, 0), new Vector(6, 2, 4, 0)));
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
        rays.add(new Ray(new Vector(-3, 0, 6, 0), new Vector(2, 1.5, -2, 0)));

        for (Ray r : rays) {
            var coords = sphere.getFirstHitPoint(r);
            if (coords == null) {
                System.out.println("NO HIT");
                continue;
            }

            System.out.printf("%f %f %f\n", coords.getEnter().getX(), coords.getEnter().getY(), coords.getEnter().getZ());
        }
    }

    @Test
    public void testPlane() {
        Object plane = new Plane(new Material(Color.gray, 0.5, 0.5, 0.5));

        Ray ray = new Ray(new Vector(4, 1, 3, 1), new Vector(-3,-5,-3, 0));

        var coords = plane.getFirstHitPoint(ray);
        if (coords == null) {
            System.out.println("NO HIT");
        }

        System.out.printf("%f %f %f\n", coords.getEnter().getX(), coords.getEnter().getY(), coords.getEnter().getZ());

    }
}
