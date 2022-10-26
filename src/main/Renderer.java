package main;

import main.object.Object;
import main.object.Plane;

import javax.swing.*;
import java.awt.*;
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
                        Object closestObject = null;
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

                                // TODO: check if point in shadow
                                /*// shoot ray towards lightsource, if object in way, darken the point

                                Vector direction = lightsource.getKey();
                                direction.setType(0);
                                Ray checkShadow = new Ray(closestIntersection.getEnter(), direction);
                                // check for every object if we intersect it*/

                                double[] s;
                                if (closestIntersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit Sphere@44
                                    s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getExit().getCoords()));
                                else
                                    s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getEnter().getCoords()));

                                // lambert term = diffuse part
                                double mDots = Utility.dot(s, normalVector);
                                if (mDots > 0.0001) {
                                    // hitpoint is pointed towards the light
                                    // first calculate the Fresnel coeff
                                    double angleOfIncidence = getAngle(normalVector, s);
                                    //angleOfIncidence = Math.toRadians(angleOfIncidence);

                                    double gRefractionSquared = Math.pow(closestObject.getMaterial().getRefractionIndex(), 2) + angleOfIncidence * angleOfIncidence - 1;
                                    double gRefraction = Math.sqrt(gRefractionSquared);
                                    // the fresnel coeff will be higher with higher refractionindices
                                    double fresnelCoefficient = (Math.pow(gRefraction - angleOfIncidence, 2) / Math.pow(gRefraction + angleOfIncidence, 2) * (1 + Math.pow((angleOfIncidence * (gRefraction + angleOfIncidence) - 1) / (angleOfIncidence * (gRefraction - angleOfIncidence) + 1), 2)));
                                    fresnelCoefficient = fresnelCoefficient * 0.5;

                                    red += ambient[0] * fresnelCoefficient;
                                    green += ambient[1] * fresnelCoefficient;
                                    blue += ambient[2] * fresnelCoefficient;


                                    double[] diffuseColorRGB = Utility.multiplyMatrixFactorArray(Utility.multiplyMatrices(mDots, diffuse), lightsource.getValue());

                                    red += diffuseColorRGB[0] * dw * fresnelCoefficient;
                                    green += diffuseColorRGB[1] * dw * fresnelCoefficient;
                                    blue += diffuseColorRGB[2] * dw * fresnelCoefficient;

                                    // specular part (Cook & Torrance)
                                    // angle between h and m (normal vector)
                                    // h: halfway vector (between incoming light and ray)
                                    double[] v = ray.getDir().getCoords().clone();
                                    v = new double[]{-v[0], -v[1], -v[2], -v[3]};

                                    double[] h;
                                    if (closestIntersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit Sphere@44
                                        h = Utility.sum(Utility.normalize(v), Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getExit().getCoords()));
                                    else
                                        h = Utility.sum(Utility.normalize(v), Utility.subtract(lightsource.getKey().getCoords(), closestIntersection.getEnter().getCoords()));


                                    h = Utility.normalize(h);

                                    // angle between h and transposedNormalVector
                                    double angle = getAngle(normalVector, h);
                                    double d = Math.exp(-Math.pow(Math.tan(angle) / mRoughness, 2)) / (4 * mRoughness * mRoughness * Math.pow(Math.cos(angle), 4));
                                    //double[] phongCookTerrace = Utility.multiplyMatrices(d, Utility.multiplyMatrixFactorArray(lightsource.getValue(), specular));


                                    // fraction of light that is not shadowed.
                                    double gs = (2 * Utility.dot(normalVector, h) * Utility.dot(normalVector, v)) / Utility.dot(h, s);
                                    // fraction of light that is not masked.
                                    double gm = (2 * Utility.dot(normalVector, h) * Utility.dot(normalVector, s)) / Utility.dot(h, s);
                                    double g = Math.min(Math.min(1, gm), gs);

                                    double phongSpecular = (fresnelCoefficient * d * g) / (Utility.dot(normalVector, v));

                                    /*red += specular[0] * phongCookTerrace[0];
                                    green += specular[1] * phongCookTerrace[1];
                                    blue += specular[2] * phongCookTerrace[2];*/

                                    // TODO add the k-factor (p.646)
                                    red += specular[0] * dw * phongSpecular;
                                    green += specular[1] * dw * phongSpecular;
                                    blue += specular[2] * dw * phongSpecular;

                                    red *= phongSpecular;
                                    green *= phongSpecular;
                                    blue *= phongSpecular;
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
        double normVectorOne = Utility.norm(vectorOne);
        return Math.toRadians(Math.acos(Utility.dot(vectorOne, vectorTwo) / normVectorOne * Utility.norm(vectorTwo)));
    }

    public void draw() {
        this.panel.repaint();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
