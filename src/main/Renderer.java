package main;

import main.object.Cube;
import main.object.Object;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class Renderer {
    private static final double LIGHTSOURCEFACTOR = 0.004;
    private static final double EPSILON = 10; // the difference that will be subtracted for shadowing
    private final JFrame frame;
    private JPanel panel;
    private final double focallength, screenWidth, screenHeight;
    private Scene scene;
    private final double cmax;
    private final double rmax;

    public Renderer(double focallength, double screenWidth, double screenHeight, double cmax, double rmax) {
        this.focallength = focallength;
        this.frame = new JFrame();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.cmax = cmax;
        this.rmax = rmax;

        frame.setFocusable(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setTitle("Ray tracer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setSize((int) screenWidth, (int) screenHeight);
    }

    void startRender() {
        this.panel = new JPanel(true) {
            @Override
            public void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);

                Graphics2D graph2d = (Graphics2D) graphics;
                Toolkit.getDefaultToolkit().sync();

                final double H = screenHeight / 2;
                final double W = screenWidth / 2;

                final double y = 2 / screenWidth;
                final double z = 2 / screenHeight;

                // shoot rays in a for loop
                float starttime = System.nanoTime();
                for (double c = 0; c <= screenWidth; c++) { //nColumns
                    for (double r = 0; r <= screenHeight; r++) { // nRows
                        //Vector dir = new Vector(-focallength, -(W - screenWidth * (y * c / cmax)), -(H - screenHeight * (z * r / rmax)), 0);
                        Vector dir = new Vector(-focallength, W * (y * c - 1), H * (z * r - 1), 0);
                        //Vector dir = new Vector(-focallength, (W - screenWidth) * (y * c / cmax), (H - screenHeight) * (z * r / rmax), 0);

                        Ray ray = new Ray(scene.getCamera().getLocation(), dir);

                        // for every object, cast the ray and find the object nearest to us, that is the object where the collision time (t1) is lowest
                        double minIntersectionTime = Integer.MAX_VALUE;
                        Object closestObject = null; // object that was hit
                        Intersection closestIntersection = null;

                        for (Object object : scene.getObjects()) {
                            Intersection intersection = object.getFirstHitPoint(ray);

                            if (intersection != null && intersection.getT1() < minIntersectionTime && intersection.getT1() >= 0) {
                                closestIntersection = intersection;
                                minIntersectionTime = intersection.getT1();
                                closestObject = object;
                            }
                        }

                        if (closestObject != null) {
                            // p 641
                            // shading
                            // Color: ambient, diffuse, specular
                            double[] rgb = new double[3];

                            getShading(ray, closestObject, closestIntersection, rgb);

                            rgb = Arrays.stream(rgb).map(v -> v * 255).toArray();
                            rgb = Arrays.stream(rgb).map(v -> {
                                if (v > 255) v = 255;
                                else if (v < 0) v = 0;
                                return v;
                            }).toArray();

                            Color color = new Color((int) rgb[0], (int) rgb[1], (int) rgb[2]);
                            graph2d.setColor(color);
                            graph2d.drawLine((int) c, (int) r, (int) c, (int) r);
                        }
                    }
                }

                graph2d.dispose();

                float endtime = System.nanoTime();
                System.out.println(endtime - starttime);
            }
        };

        frame.add(panel);
    }

    private void getShading(Ray ray, Object closestObject, Intersection closestIntersection, double[] rgb) {
        double[] ambient = closestObject.getMaterial().getAmbient();
        double[] diffuse = closestObject.getMaterial().getDiffuse();
        double[] specular = closestObject.getMaterial().getSpecular();

        double[] v = ray.getDir().getCoords().clone();
        v = Arrays.stream(v).map(value -> value * -1).toArray();

        // m is the roughness of the material
        double mRoughness = closestObject.getMaterial().getRoughness();
        double[] normalVector = Utility.multiplyMatrices(closestIntersection.getNormalVector(), Utility.transpose(closestObject.getTransformation().getTransformation()));
        normalVector = Utility.normalize(normalVector);

        // the fresnel coeff is the fraction that is reflected and will be higher with higher refractionindices

        // fresnel at certain angle p(644)
        //double fresnelCoefficient = Math.pow(gRefraction - cRefraction, 2) / Math.pow(gRefraction + cRefraction, 2) * (1 + Math.pow((cRefraction * (gRefraction + cRefraction) - 1) / (cRefraction * (gRefraction - cRefraction) + 1), 2));
        // fresnel at angle of 0 (p.647)
        double[] fresnelCoefficient0RGB = new double[3];
        for (int i = 0; i < 3; i++) {
            fresnelCoefficient0RGB[i] = Math.pow((closestObject.getMaterial().getRefractionIndex()[i] - 1), 2) / Math.pow((closestObject.getMaterial().getRefractionIndex()[i] + 1), 2);
        }

        /*
          AMBIENT
        */

        for (int i = 0; i < 3; i++) {
            rgb[i] += ambient[i] * closestObject.getMaterial().getkDistribution()[0] * fresnelCoefficient0RGB[i];
        }

        Vector hitpoint;
        if (closestIntersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit Sphere@44
            hitpoint = closestIntersection.getExit();
        else
            hitpoint = closestIntersection.getEnter();

        // TODO recurselevel (shadows) (p.672)

        // for each lightsource
        for (Map.Entry<Vector, double[]> lightsource : scene.getLightsources().entrySet()) {
            // check first for possible shadow spots
            Vector start = new Vector(Utility.subtract(hitpoint.getCoords(), Utility.multiplyElementWise(EPSILON, ray.getDir().getCoords())));
            Vector dir = Utility.subtract(lightsource.getKey(), hitpoint);

            if (isInShadow(start, dir)) {
                rgb = Arrays.stream(rgb).map(value -> value * 0.1).toArray();
                continue; // skip diffusive and specular part
            }

            // now onto diffuse and specular

            double dw = 1; // width lightbeam coming from source

            double[] s;
            if (closestIntersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit Sphere@44
                s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getExit().getCoords()));
            else
                s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getEnter().getCoords()));

            double mDots = Utility.dot(s, normalVector);
            double cRefraction = mDots;

            // first calculate the Fresnel coeff
            double angleOfIncidence = getAngle(normalVector, s);

            if (mDots > 0) { // = mdots in book
                // hitpoint is pointed towards the light
                /*
                  DIFFUSE
                */

                double lambert = Math.max(0, mDots / (Utility.norm(s) * Utility.norm(normalVector)));
                for (int i = 0; i < 3; i++) {
                    rgb[i] += specular[i] * dw * closestObject.getMaterial().getkDistribution()[1] * fresnelCoefficient0RGB[i] * lambert * (lightsource.getValue()[i] * LIGHTSOURCEFACTOR);
                }

                /*
                SPECULAR
                 */

                // angle between h and m (normal vector)
                // h: halfway vector (between incoming light and ray)
                double[] h = Utility.normalize(Utility.sum(Utility.normalize(v), s));

                double mDoth = Utility.dot(h, normalVector);
                if (mDoth > 0) {
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
                        gRefractionSquaredRGB[i] = Math.pow(closestObject.getMaterial().getRefractionIndex()[i], 2) + angleOfIncidence * angleOfIncidence - 1;
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
                        rgb[i] += specular[i] * closestObject.getMaterial().getkDistribution()[2] * dw * phongSpecularRGB[i];
                    }
                }
            }
        }
    }

    private boolean isInShadow(Vector start, Vector dir) {
        Ray shadowFeeler = new Ray(start, dir);
        for (Object o : scene.getObjects()) {
            Intersection intersection = o.getFirstHitPoint(shadowFeeler);
            if (intersection != null && intersection.getExit() != null)
                return true;
        }

        return false;
    }

    private double getAngle(double[] vectorOne, double[] vectorTwo) {
        return Math.acos(Utility.dot(vectorOne, vectorTwo) / Utility.norm(vectorOne) * Utility.norm(vectorTwo));
    }

    public void draw() {
        this.panel.repaint();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
