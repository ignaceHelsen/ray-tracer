package main;

import main.object.Object;
import main.object.Plane;
import main.object.Sphere;
import main.object.TaperedCylinder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

public class Renderer {
    private static final double LIGHTSOURCEFACTOR = 0.01;
    private static final double EPSILON = 0.01; // the difference that will be subtracted for shadowing
    private final JFrame frame;
    private final double focallength, screenWidth, screenHeight;
    private Scene scene;
    private final double cmax;
    private final double rmax;
    private final Canvas canvas;
    private final BufferedImage buffer;
    private final BufferStrategy strategy;

    public Renderer(double focallength, double screenWidth, double screenHeight, double cmax, double rmax) {
        this.focallength = focallength;
        this.frame = new JFrame();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.cmax = cmax;
        this.rmax = rmax;

        // Following code with thanks to Maarten Van Loo
        this.canvas = new Canvas();
        this.buffer = new BufferedImage(((int) screenWidth), ((int) screenHeight), BufferedImage.TYPE_3BYTE_BGR);
        this.canvas.setSize(new Dimension((int) screenWidth + 1, (int) screenHeight + 1));
        this.canvas.setPreferredSize(new Dimension((int) screenWidth + 1, (int) screenHeight + 1));
        this.canvas.setIgnoreRepaint(true);
        this.frame.add(canvas);
        this.frame.setTitle("Ray tracer");
        this.frame.setResizable(true);
        this.frame.setVisible(true);
        this.frame.pack();
        this.canvas.createBufferStrategy(2);
        this.strategy = canvas.getBufferStrategy();
    }

    void show() {
        Graphics2D graphics = (Graphics2D) strategy.getDrawGraphics();
        graphics.drawImage(buffer, 0, 0, (int) screenWidth, (int) screenHeight, null);
        graphics.dispose();
        strategy.show();
    }

    public void startRender() {
        final double h = screenHeight / 2;
        final double w = screenWidth / 2;

        float starttime = System.nanoTime();
        // shoot rays in a for loop
        for (double r = 0; r <= screenHeight - 1; r++) { // nRows
            for (double c = 0; c <= screenWidth - 1; c++) { //nColumns
                Vector dir = new Vector(-focallength, -(w - screenWidth * (c / cmax)), -(h - screenHeight * (r / rmax)), 0);
                //Vector dir = new Vector(-focallength, w * (y * c - 1), h * (z * r - 1), 0);
                //Vector dir = new Vector(-focallength, (w - screenWidth) * (y * c / cmax), (h - screenHeight) * (z * r / rmax), 0);

                // create rays
                // create normalized ray
                Vector normalizedDir = new Vector(Utility.normalize(dir.getCoords()));
                Ray normalizedRay = new Ray(scene.getCamera().location, normalizedDir);
                // create unnormalized ray
                Ray notNormalizedRay = new Ray(scene.getCamera().getLocation(), dir);

                // for every object, cast the ray and find the object nearest to us, that is the object where the collision time (t1) is lowest
                double minIntersectionTime = Integer.MAX_VALUE;
                Object closestObject = null; // object that was hit
                Intersection intersectionHit = null; // the closest intersection and which we will be using later on

                for (Object currentObject : scene.getObjects()) {
                    Intersection currentIntersection;

                    // only use the normalized ray for non-plane objects
                    if(currentObject instanceof Plane) {
                        // object is plane, use the not normalized ray
                        currentIntersection = currentObject.getFirstHitPoint(notNormalizedRay);
                    } else {
                        // object is not a plane, use the normalized ray
                        currentIntersection = currentObject.getFirstHitPoint(normalizedRay);
                    }

                    // we know that if only one hit is present this hit has been set at exit and the time at T2. (btw, if only one hit -> t1 has been set to -1)
                    // if, as normally, two hits are present (one enter and one exit) we know that T1 corresponds to the enter time and t2 to the exit time.
                    // there will always be a T2.
                    // currentIntersection times always have to be >= 0.
                    if (currentIntersection != null) {
                        if ((currentIntersection.getEnter() == null && currentIntersection.getT2() >= 0 && currentIntersection.getT2() < minIntersectionTime) || (currentIntersection.getEnter() != null && currentIntersection.getT1() >= 0 && currentIntersection.getT1() < minIntersectionTime)) {
                            // the current intersection is in front of the previous hit, which means we have to deal with this hit in further code
                            intersectionHit = currentIntersection;

                            // for priorities with objects that are closer by and should be painted instead of objects behind it
                            if (currentIntersection.getEnter() == null) // set min as exit time (=T2) (since only an exit has been registered)
                                minIntersectionTime = currentIntersection.getT2();
                            else
                                minIntersectionTime = currentIntersection.getT1();

                            closestObject = currentObject;
                        }
                    }
                }

                if (closestObject != null) {
                    // p 641
                    // shading
                    // Color: ambient, diffuse, specular
                    double[] rgb = new double[3];

                    if (closestObject instanceof Plane)
                        getShading(notNormalizedRay, closestObject, intersectionHit, rgb);
                    else
                        getShading(normalizedRay, closestObject, intersectionHit, rgb);

                    rgb = Arrays.stream(rgb).map(v -> v * 255).toArray();
                    rgb = Arrays.stream(rgb).map(v -> {
                        if (v > 255) v = 255;
                        else if (v < 0) v = 0;
                        return v;
                    }).toArray();

                    int color = ((int) rgb[0] << 16) | ((int) rgb[1] << 8) | (int) rgb[2];
                    buffer.setRGB((int) c, (int) r, color);
                } else {
                    if (r < w) {
                        if (new Random().nextDouble(100) < 0.1) {
                            int color = (145 << 16) | (211 << 8) | 255;
                            buffer.setRGB((int) c, (int) r, color);
                        }
                    } else {
                        buffer.setRGB((int) c, (int) r, 0);
                    }
                }

            }
        }

        float endtime = System.nanoTime();
        System.out.println(endtime - starttime);
    }

    private void getShading(Ray ray, Object objectHit, Intersection intersection, double[] rgb) {
        double[] ambient = objectHit.getMaterial().getAmbient();
        double[] diffuse = objectHit.getMaterial().getDiffuse();
        double[] specular = objectHit.getMaterial().getSpecular();

        double[] v = ray.getDir().getCoords().clone();
        v = Arrays.stream(v).map(value -> value * -1).toArray();

        // m is the roughness of the material
        double mRoughness = objectHit.getMaterial().getRoughness();
        double[] normalVector = Utility.multiplyMatrices(intersection.getNormalVector(), Utility.transpose(objectHit.getTransformation().getTransformation()));
        normalVector = Utility.normalize(normalVector);

        // the fresnel coeff is the fraction that is reflected and will be higher with higher refractionindices

        // fresnel at certain angle p(644)
        //double fresnelCoefficient = Math.pow(gRefraction - cRefraction, 2) / Math.pow(gRefraction + cRefraction, 2) * (1 + Math.pow((cRefraction * (gRefraction + cRefraction) - 1) / (cRefraction * (gRefraction - cRefraction) + 1), 2));
        // fresnel at angle of 0 (p.647)
        double[] fresnelCoefficient0RGB = new double[3];
        for (int i = 0; i < 3; i++) {
            fresnelCoefficient0RGB[i] = Math.pow((objectHit.getMaterial().getRefractionIndex()[i] - 1), 2) / Math.pow((objectHit.getMaterial().getRefractionIndex()[i] + 1), 2);
        }

        /*
          AMBIENT
        */

        for (int i = 0; i < 3; i++) {
            rgb[i] += ambient[i] * objectHit.getMaterial().getkDistribution()[0] * fresnelCoefficient0RGB[i];
        }

        Vector hitpoint;
        if (intersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit Sphere@44
            hitpoint = intersection.getExit();
        else
            hitpoint = intersection.getEnter();

        // TODO recurselevel (shadows) (p.672)

        // for each lightsource
        for (Map.Entry<Vector, double[]> lightsource : scene.getLightsources().entrySet()) {
            // check first for possible shadow spots
            Vector start = new Vector(Utility.subtract(hitpoint.getCoords(), Utility.multiplyElementWise(EPSILON, ray.getDir().getCoords())));
            Vector dir = Utility.subtract(lightsource.getKey(), hitpoint);

            /*if (isInShadow(start, dir) && objectHit instanceof Plane) {
                continue; // skip diffusive and specular part
            }*/

            // now onto diffuse and specular

            double dw = 1; // width lightbeam coming from source

            double[] s;
            if (intersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we know we have set as exit
                s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), intersection.getExit().getCoords()));
            else
                s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), intersection.getEnter().getCoords()));

            double mDots = Utility.dot(s, normalVector);
            double cRefraction = mDots;

            // first calculate the Fresnel coeff
            double angleOfIncidence = getAngle(normalVector, s);

            if (mDots > 0.0001) {
                // hitpoint is pointed towards the light
                /*
                  DIFFUSE
                */

                double lambert = Math.max(0, mDots / (Utility.norm(s) * Utility.norm(normalVector)));
                for (int i = 0; i < 3; i++) {
                    rgb[i] += specular[i] * dw * objectHit.getMaterial().getkDistribution()[1] * fresnelCoefficient0RGB[i] * lambert * (lightsource.getValue()[i] * LIGHTSOURCEFACTOR);
                }

                /*
                SPECULAR
                 */

                // angle between h and m (normal vector)
                // h: halfway vector (between incoming light and ray)
                double[] h = Utility.normalize(Utility.sum(Utility.normalize(v), s));

                double mDoth = Utility.dot(h, normalVector);
                if (mDoth > 0.0001) {
                    // angle between h and transposedNormalVector
                    double angle = getAngle(normalVector, h);
                    double d = Math.exp(-Math.pow(Math.tan(angle) / mRoughness, 2)) / (4 * mRoughness * mRoughness * Math.pow(Math.cos(angle), 4));

                    // G will scale the strength of the specular component
                    // fraction of light that is not shadowed.
                    double gs = (2 * Utility.dot(normalVector, h) * Utility.dot(normalVector, v)) / Utility.dot(h, s);
                    // fraction of light that is not masked.
                    double gm = (2 * Utility.dot(normalVector, h) * Utility.dot(normalVector, s)) / Utility.dot(h, s);
                    double g = Math.min(Math.min(1, gm), gs);

                    // gRefractionSquaredRGB = g² = η² + c² - 1
                    double[] gRefractionSquaredRGB = new double[3];
                    for (int i = 0; i < 3; i++) {
                        gRefractionSquaredRGB[i] = Math.pow(objectHit.getMaterial().getRefractionIndex()[i], 2) + angleOfIncidence * angleOfIncidence - 1;
                    }

                    double[] gRefraction = Arrays.stream(gRefractionSquaredRGB).map(Math::sqrt).toArray();

                    // now we need the fresnel coeff at the angle non-zero
                    double[] fresnelCoefficientAngleRGB = new double[3];
                    for (int i = 0; i < 3; i++) {
                        fresnelCoefficientAngleRGB[i] = 0.5 * (Math.pow(gRefraction[i] - cRefraction, 2) / Math.pow(gRefraction[i] + cRefraction, 2)) * (1 + Math.pow((cRefraction * (gRefraction[i] + cRefraction) - 1) / (cRefraction * (gRefraction[i] - cRefraction) + 1), 2));
                    }

                    double[] phongSpecularRGB = new double[3];
                    for (int i = 0; i < 3; i++) {
                        phongSpecularRGB[i] = (fresnelCoefficientAngleRGB[i] * d * g) / (Utility.dot(normalVector, v));
                    }

                    for (int i = 0; i < 3; i++) {
                        rgb[i] += specular[i] * objectHit.getMaterial().getkDistribution()[2] * dw * phongSpecularRGB[i];
                    }
                }
            }
        }
    }

    private boolean isInShadow(Vector start, Vector dir) {
        Ray shadowFeeler = new Ray(start, dir);
        for (Object o : scene.getObjects()) {
            Intersection intersection = o.getFirstHitPoint(shadowFeeler); //shoot the ray and check if we got a hitpoint
            if (intersection != null && intersection.getExit() != null)
                return true;
        }

        return false;
    }

    private double getAngle(double[] vectorOne, double[] vectorTwo) {
        return Math.acos(Utility.dot(vectorOne, vectorTwo) / Utility.norm(vectorOne) * Utility.norm(vectorTwo));
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void saveBuffer() throws IOException {
        File outputfile = new File("image.png");
        ImageIO.write(buffer, "png", outputfile);
    }
}
