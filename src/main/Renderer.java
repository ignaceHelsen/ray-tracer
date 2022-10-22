package main;

import main.object.Object;
import main.object.Plane;

import javax.swing.*;
import java.awt.*;

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
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D graph2d = (Graphics2D) g;
                Toolkit.getDefaultToolkit().sync();

                final double H = screenHeight / 2;
                final double W = screenWidth / 2;

                final double y = 2 / screenWidth;
                final double z = 2 / screenHeight;

                double max1 = 0;
                double max2 = 0;

                // shoot rays in a for loop
                float starttime = System.nanoTime();
                for (double c = 0; c <= screenWidth; c++) { //nColumns
                    for (double r = 0; r <= screenHeight; r++) { // nRows
                        //Vector dir = new Vector(-focallength, -(W - screenWidth * (y * c / cmax)), -(H - screenHeight * (z * r / rmax)), 0);
                        Vector dir = new Vector(-focallength, W * (y * c - 1), H * (z * r - 1), 0);
                        //Vector dir = new Vector(-focallength, (W - screenWidth) * (y * c / cmax), (H - screenHeight) * (z * r / rmax), 0);

                        if (max1 > dir.getCoords()[1]) max1 = dir.getCoords()[1];
                        if (max2 > dir.getCoords()[2]) max2 = dir.getCoords()[2];

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

                            red += ambient[0];
                            green += ambient[1];
                            blue += ambient[2];

                            // m is the roughness of the material
                            double mRoughness = closestObject.getMaterial().getRoughness();
                            double[] normalVector = Utility.multiplyMatrices(closestIntersection.getNormalVector(), Utility.transpose(closestObject.getTransformation().getTransformation()));
                            normalVector = Utility.normalize(normalVector);

                            // for each lightsource
                            double[] s;
                            if (closestIntersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit Sphere@44
                                s = Utility.normalize(Utility.subtract(scene.getLightsource().getCoords(), closestIntersection.getExit().getCoords()));
                            else
                                s = Utility.normalize(Utility.subtract(scene.getLightsource().getCoords(), closestIntersection.getEnter().getCoords()));

                            // lambert term = diffuse part
                            double mDots = Utility.dot(s, normalVector);
                            double[] diffuseColorRGB = Utility.multiplyMatrixFactorArray(Utility.multiplyMatrices(mDots, diffuse), scene.getLightsourceColor());

                            red += diffuseColorRGB[0];
                            green += diffuseColorRGB[1];
                            blue += diffuseColorRGB[2];

                            // specular part (Cook & Torrance)
                            // angle between h and m (normal vector)
                            // h: halfway vector (between incoming light and ray)
                            double[] rayDir = ray.getDir().getCoords().clone();

                            double[] h;
                            if (closestIntersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit Sphere@44
                                h = Utility.sum(Utility.normalize(new double[]{-rayDir[0], -rayDir[1], -rayDir[2], -rayDir[3]}), Utility.subtract(scene.getLightsource().getCoords(), closestIntersection.getExit().getCoords()));
                            else
                                h = Utility.sum(Utility.normalize(new double[]{-rayDir[0], -rayDir[1], -rayDir[2], -rayDir[3]}), Utility.subtract(scene.getLightsource().getCoords(), closestIntersection.getEnter().getCoords()));


                            h = Utility.normalize(h);

                            // angle between h and transposedNormalVector
                            double angle = Math.acos(Utility.dot(normalVector, h) / Utility.norm(normalVector) * Utility.norm(h));
                            double fraction = Math.exp(-Math.pow(Math.tan(angle) / mRoughness, 2)) / (4 * mRoughness * mRoughness * Math.pow(Math.cos(angle), 4));
                            double[] phongCookTerrace = Utility.multiplyMatrices(fraction, Utility.multiplyMatrixFactorArray(scene.getLightsourceColor(), specular));

                            red += specular[0] * phongCookTerrace[0];
                            green += specular[1] * phongCookTerrace[1];
                            blue += specular[2] * phongCookTerrace[2];

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
                System.out.println(max1);
                System.out.println(max2);
            }
        };

        frame.add(panel);
    }

    public void draw() {
        this.panel.repaint();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
}
