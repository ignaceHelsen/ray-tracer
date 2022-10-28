package main;

import main.object.Object;
import main.object.Plane;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class Renderer {
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
                            double red = 0;
                            double green = 0;
                            double blue = 0;

                            double[] ambient = closestObject.getMaterial().getAmbient();
                            double[] diffuse = closestObject.getMaterial().getDiffuse();
                            double[] specular = closestObject.getMaterial().getSpecular();

                            // m is the roughness of the material
                            double mRoughness = closestObject.getMaterial().getRoughness();
                            double[] normalVector = Utility.multiplyMatrices(closestIntersection.getNormalVector(), Utility.transpose(closestObject.getTransformation().getTransformation()));
                            normalVector = Utility.normalize(normalVector);

                            // for each lightsource
                            for (Map.Entry<Vector, double[]> lightsource : scene.getLightsources().entrySet()) {
                                double dw = 0.0001; // width lightbeam coming from source

                                double[] s;
                                if (closestIntersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit Sphere@44
                                    s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getExit().getCoords()));
                                else
                                    s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getEnter().getCoords()));

                                double mDots = Utility.dot(s, normalVector);
                                if (mDots > 0) {
                                    // hitpoint is pointed towards the light
                                    // first calculate the Fresnel coeff
                                    double angleOfIncidence = getAngle(normalVector, s);

                                    // the fresnel coeff is the fraction that is reflected and will be higher with higher refractionindices
                                    // fresnel at certain angle p(644)
                                    double cRefraction = Math.cos(angleOfIncidence);
                                    //double fresnelCoefficient = Math.pow(gRefraction - cRefraction, 2) / Math.pow(gRefraction + cRefraction, 2) * (1 + Math.pow((cRefraction * (gRefraction + cRefraction) - 1) / (cRefraction * (gRefraction - cRefraction) + 1), 2));
                                    // fresnel at angle of 0 (p.647)
                                    double[] fresnelCoefficient0RGB = new double[3];
                                    fresnelCoefficient0RGB[0] = Math.pow((closestObject.getMaterial().getRefractionIndex()[0] - 1), 2) / Math.pow((closestObject.getMaterial().getRefractionIndex()[0] + 1), 2);
                                    fresnelCoefficient0RGB[1] = Math.pow((closestObject.getMaterial().getRefractionIndex()[1] - 1), 2) / Math.pow((closestObject.getMaterial().getRefractionIndex()[1] + 1), 2);
                                    fresnelCoefficient0RGB[2] = Math.pow((closestObject.getMaterial().getRefractionIndex()[2] - 1), 2) / Math.pow((closestObject.getMaterial().getRefractionIndex()[2] + 1), 2);

                                    /*
                                     AMBIENT
                                     */

                                    red += ambient[0] * closestObject.getMaterial().getDistributionK()[0] * fresnelCoefficient0RGB[0];
                                    green += ambient[1] * closestObject.getMaterial().getDistributionK()[0] * fresnelCoefficient0RGB[1];
                                    blue += ambient[2] * closestObject.getMaterial().getDistributionK()[0] * fresnelCoefficient0RGB[2];

                                    // lambert term = diffuse part
                                    /*double[] diffuseColorRGB = Utility.multiplyMatrixFactorArray(Utility.multiplyMatrices(mDots, diffuse), lightsource.getValue());

                                    red += diffuseColorRGB[0] * dw * fresnelCoefficient;
                                    green += diffuseColorRGB[1] * dw * fresnelCoefficient;
                                    blue += diffuseColorRGB[2] * dw * fresnelCoefficient;*/

                                    /*
                                    DIFFUSE
                                     */

                                    double lambert = Math.max(0, Utility.dot(s, normalVector) / (Utility.norm(s) * Utility.norm(normalVector)));

                                    red += specular[0] * dw * closestObject.getMaterial().getDistributionK()[2] * fresnelCoefficient0RGB[0] * lambert;
                                    green += specular[1] * dw * closestObject.getMaterial().getDistributionK()[2] * fresnelCoefficient0RGB[1] * lambert;
                                    blue += specular[2] * dw * closestObject.getMaterial().getDistributionK()[2] * fresnelCoefficient0RGB[2] * lambert;

                                    /*
                                    SPECULAR
                                     */

                                    // specular part (Cook & Torrance)
                                    // angle between h and m (normal vector)
                                    // h: halfway vector (between incoming light and ray)
                                    double[] v = ray.getDir().getCoords().clone();
                                    v = new double[]{-v[0], -v[1], -v[2], -v[3]};

                                    double[] h;
                                    if (closestIntersection.getEnter() == null) // tangent hit or only exit hit = only one hitpoint which we have set as exit Sphere@44
                                        h = Utility.sum(Utility.normalize(v), Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getExit().getCoords()));
                                    else
                                        h = Utility.sum(Utility.normalize(v), Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getEnter().getCoords()));

                                    h = Utility.normalize(h);

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

                                        // g² = η² + c² - 1
                                        double[] gRefractionSquaredRGB = new double[3];

                                        gRefractionSquaredRGB[0] = Math.pow(closestObject.getMaterial().getRefractionIndex()[0], 2) + angleOfIncidence * angleOfIncidence - 1;
                                        gRefractionSquaredRGB[1] = Math.pow(closestObject.getMaterial().getRefractionIndex()[1], 2) + angleOfIncidence * angleOfIncidence - 1;
                                        gRefractionSquaredRGB[2] = Math.pow(closestObject.getMaterial().getRefractionIndex()[2], 2) + angleOfIncidence * angleOfIncidence - 1;
                                        double[] gRefraction = Arrays.stream(gRefractionSquaredRGB).map(Math::sqrt).toArray();

                                        // now we need the fresnel coeff at the angle non-zero
                                        double[] fresnelCoefficientAngleRGB = new double[3];
                                        fresnelCoefficientAngleRGB[0] = 0.5 * Math.pow(gRefraction[0] - cRefraction, 2) / Math.pow(gRefraction[0] + cRefraction, 2) * (1 + Math.pow((cRefraction * (gRefraction[0] + cRefraction) - 1) / (cRefraction * (gRefraction[0] - cRefraction) + 1), 2));
                                        fresnelCoefficientAngleRGB[1] = 0.5 * Math.pow(gRefraction[1] - cRefraction, 2) / Math.pow(gRefraction[1] + cRefraction, 2) * (1 + Math.pow((cRefraction * (gRefraction[1] + cRefraction) - 1) / (cRefraction * (gRefraction[1] - cRefraction) + 1), 2));
                                        fresnelCoefficientAngleRGB[2] = 0.5 * Math.pow(gRefraction[2] - cRefraction, 2) / Math.pow(gRefraction[2] + cRefraction, 2) * (1 + Math.pow((cRefraction * (gRefraction[2] + cRefraction) - 1) / (cRefraction * (gRefraction[2] - cRefraction) + 1), 2));

                                        double[] phongSpecularRGB = new double[3];
                                        phongSpecularRGB[0] = (fresnelCoefficientAngleRGB[0] * d * g) / (Utility.dot(normalVector, v));
                                        phongSpecularRGB[1] = (fresnelCoefficientAngleRGB[1] * d * g) / (Utility.dot(normalVector, v));
                                        phongSpecularRGB[2] = (fresnelCoefficientAngleRGB[2] * d * g) / (Utility.dot(normalVector, v));

                                        /*red += specular[0] * phongCookTerrace[0];
                                        green += specular[1] * phongCookTerrace[1];
                                        blue += specular[2] * phongCookTerrace[2];*/

                                        red += specular[0] * closestObject.getMaterial().getDistributionK()[2] * dw * phongSpecularRGB[0];
                                        green += specular[1] * closestObject.getMaterial().getDistributionK()[2] * dw * phongSpecularRGB[1];
                                        blue += specular[2] * closestObject.getMaterial().getDistributionK()[2] * dw * phongSpecularRGB[2];
                                    }
                                }
                            }

                            red *= 255;
                            green *= 255;
                            blue *= 255;

                            // get color of object
                            if (red < 0) {
                                red = 0;
                            }
                            if (green < 0) {
                                green = 0;
                            }
                            if (blue < 0) {
                                blue = 0;
                            }

                            if (red > 255) {
                                red = 255;
                            }
                            if (green > 255) {
                                green = 255;
                            }
                            if (blue > 255) {
                                blue = 255;
                            }

                            Color color = new Color((int) red, (int) green, (int) blue);
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
