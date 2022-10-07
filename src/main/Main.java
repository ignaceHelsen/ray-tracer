package main;

import main.object.Object;
import main.object.Sphere;

public class Main {
    public static void main(String[] args) {
        Vector s = new Vector(3F, 2F,3F, (byte) 0);
        Vector c = new Vector(-3F, -2F, -3F, (byte) 0);

        Ray ray = new Ray(s, c);
        Object sphere = new Sphere();

        System.out.println(sphere.getCollision(ray).toString());
    }
}
