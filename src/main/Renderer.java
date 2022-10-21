package main;

import main.object.Object;

import javax.swing.*;
import java.awt.*;

public class Renderer {
    private final JFrame frame;
    private JPanel panel;
    private final double focallength, screenWidth, screenHeight;
    private Scene scene;
    private int cmax;
    private int rmax;

    public Renderer(double focallength, int screenWidth, int screenHeight, int cmax, int rmax) {
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
        frame.setSize(screenWidth, screenHeight);
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

                // shoot rays in a for loop
                float starttime = System.nanoTime();
                for (int c = 0; c <= screenWidth; c++) { //nColumns
                    for (int r = 0; r <= screenHeight; r++) { // nRows
                        //Vector dir = new Vector(-focallength, W-screenWidth * (y*c/cmax), H-screenHeight * (z*r/rmax), 0);
                        Vector dir = new Vector(-focallength, W * (y * c - 1), H * (z * r - 1), 0);

                        Ray ray = new Ray(scene.getCamera().getLocation(), dir);

                        // for every object, cast the ray and find the object nearest to us, that is the object where the collision time (t1) is lowest
                        double minIntersectionTime = Integer.MAX_VALUE;
                        Object closestObject = null;
                        Intersection closestIntersection = null;

                        for(Object object : scene.getObjects()) {
                            Intersection intersection = object.getFirstHitPoint(ray);

                            if(intersection != null && intersection.getT1() < minIntersectionTime && intersection.getT1() >= 0) {
                                closestIntersection = intersection;
                                minIntersectionTime = intersection.getT1();
                                closestObject = object;
                            }
                        }

                        if(closestObject != null) {
                            // p 641
                            // shading
                            // Color: ambient, diffuse, specular
                            double[] ambient = closestObject.getMaterial().getAmbient();
                            double[] diffuse = closestObject.getMaterial().getDiffuse();
                            double[] specular = closestObject.getMaterial().getSpecular();

                            // m is the roughness of the material
                            double mRoughness = 0.2;
                            double[] normalVector = closestIntersection.getNormalVector();
                            double[] transposedNormalVector = Utility.multiplyMatrices(normalVector, Utility.transpose(closestObject.getTransformation().getTransformation()));
                            // angle between h and m (normal vector)
                            // h: halfway vector (between incoming light and ray)
                            double[] h = Utility.sum(ray.getDir().getCoords(), Utility.subtract(scene.getLightsource().getCoords(), closestIntersection.getEnter().getCoords())); // TODO: either getExit or getEnter

                            // angle between h and transposedNormalVector
                            double angle = Math.acos(Utility.dot(transposedNormalVector, h)/ Utility.norm(transposedNormalVector) * Utility.norm(h));
                            double fraction = Math.exp(-Math.pow(Math.tan(angle)/mRoughness, 2))/(4*mRoughness*mRoughness*Math.pow(Math.cos(angle), 4));

                            specular[0] = specular[0]*fraction;
                            specular[1] = specular[1]*fraction;
                            specular[2] = specular[2]*fraction;

                            int red = (int) (ambient[0]*255 + diffuse[0]*255 + specular[0]*255);
                            int green = (int) (ambient[1]*255 + diffuse[1]*255 + specular[1]*255);
                            int blue = (int) (ambient[2]*255 + diffuse[2]*255 + specular[2]*255);

                            // get color of object
                            Color color = new Color(red, green, blue);
                            graph2d.setColor(color);
                            graph2d.drawLine(c, r, c, r);
                        }
                    }
                }

                graph2d.dispose();

                float endtime = System.nanoTime();
                System.out.println(endtime-starttime);
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
