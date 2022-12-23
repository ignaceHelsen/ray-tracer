package main;

import main.object.*;
import main.object.Object;

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
    private final double LIGHTSOURCEFACTOR = 0.1; // or a bit of contrast
    private final double EPSILON = 0.01; // the difference that will be subtracted for shadowing
    private final int MAXRECURSELEVEL = 1; // TODO: move to SDL parameter
    private final double DW = 0.1; // width lightbeam coming from source

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

                // create normalized ray
                Vector normalizedDir = new Vector(Utility.normalize(dir.getCoords()));
                Ray normalizedRay = new Ray(scene.getCamera().location, normalizedDir);

                Tuple<Object, Intersection> objectIntersection = getHit(normalizedRay);

                Object closestObject = objectIntersection.getObject();
                Intersection intersectionHit = objectIntersection.getIntersection();

                if (closestObject != null) {
                    // p 641
                    // shading
                    // Color: ambient, diffuse, specular
                    double[] rgb = new double[3];

                    int recurseLevel = 0;
                    double air = 299_792_458 * 0.9997;
                    getShading(normalizedRay, closestObject, intersectionHit, rgb, recurseLevel, air);

                    // rgb is now a value between 0 and 1
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

    private Tuple<Object, Intersection> getHit(Ray normalizedRay) {
        // for every object, cast the ray and find the object nearest to us, that is the object where the collision time (t1) is lowest
        double minIntersectionTime = Integer.MAX_VALUE;
        Object closestObject = null; // object that was hit
        Intersection intersectionHit = null; // the closest intersection and which we will be using later on

        for (Object currentObject : scene.getObjects()) {
            Intersection currentIntersection;

            currentIntersection = currentObject.getFirstHitPoint(normalizedRay);

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

        return new Tuple<>(closestObject, intersectionHit);
    }

    /**
     * Will calculate the shading of a ray hitting an object. Will recursively spawn rays for reflection and refraction. Will also take into account shadow.
     *
     * @param ray:            the ray that hit an object.
     * @param currentObject:  the object that the ray hit;
     * @param intersection:   the intersection of the ray.
     * @param rgb:            the current rgb value.
     * @param recurseLevel:   the level of ray.
     * @param previousObject: the speed of light in the previous object.
     * @return: updated rgb value.
     */
    private double[] getShading(Ray ray, Object currentObject, Intersection intersection, double[] rgb, int recurseLevel, double previousObject) {
        double[] ambient = currentObject.getMaterial().getAmbient();
        double[] diffuse = currentObject.getMaterial().getDiffuse(); // We don't use this one anymore
        double[] specular = currentObject.getMaterial().getSpecular();

        double[] v = ray.getDir().getCoords().clone();
        v = Arrays.stream(v).map(value -> value * -1).toArray();

        double mRoughness = currentObject.getMaterial().getRoughness();

        int debug;

        //re-transform the hitpoint
        Vector hitpoint;
        if (intersection.getEnter() == null) // tangent hit or only exit hit ==> only one hitpoint which we have set as exit
            hitpoint = intersection.getExit();
        else
            hitpoint = intersection.getEnter();

        double[] normalVector = Utility.multiplyMatrices(intersection.getNormalVector(), currentObject.getTransformation().getTransformation());
        if (currentObject instanceof Sphere) {
            //normalvector - center of sphere
            double[] center = new double[]{0, 0, 0, 1}; // the center of the circle (a point)
            center = Utility.multiplyMatrices(center, Utility.transpose(currentObject.getTransformation().getTransformation())); // will always remain 0 0 0 1
            normalVector = Utility.subtract(normalVector, center);
        }
        normalVector = Utility.normalize(normalVector);
        //normalVector[3] = 0;

        hitpoint = new Vector(Utility.multiplyMatrices(hitpoint.getCoords(), currentObject.getTransformation().getTransformation()));

        // the fresnel coeff is the fraction that is reflected and will be higher with higher refractionindices

        // fresnel at certain angle p(644)
        //double fresnelCoefficient = Math.pow(gRefraction - cRefraction, 2) / Math.pow(gRefraction + cRefraction, 2) * (1 + Math.pow((cRefraction * (gRefraction + cRefraction) - 1) / (cRefraction * (gRefraction - cRefraction) + 1), 2));
        // fresnel at angle of 0 (p.647)
        double[] fresnelCoefficient0RGB = new double[3];
        for (int i = 0; i < 3; i++) {
            fresnelCoefficient0RGB[i] = Math.pow((currentObject.getMaterial().getRefractionIndex()[i] - 1), 2) / Math.pow((currentObject.getMaterial().getRefractionIndex()[i] + 1), 2);
        }

        /*
          AMBIENT
        */

        for (int i = 0; i < 3; i++) {
            rgb[i] += ambient[i] * currentObject.getMaterial().getkDistribution()[0] * fresnelCoefficient0RGB[i];
        }

        /*
            TEXTURE
         */

        double[] textureRgb = getTexture(currentObject.getTexture(), hitpoint.getX(), hitpoint.getY(), hitpoint.getZ());

        for (int i = 0; i < 3; i++) {
            rgb[i] *= textureRgb[i];
        }

        // shadows
        Vector start = Utility.subtract(hitpoint, Utility.multiplyElementWise(EPSILON, new Vector(normalVector)));

        // for each lightsource
        for (Map.Entry<Vector, double[]> lightsource : scene.getLightsources().entrySet()) {
            // check first for possible shadow spots
            Vector dir = new Vector(lightsource.getKey().getX() - hitpoint.getX(), lightsource.getKey().getY() - hitpoint.getY(), lightsource.getKey().getZ() - hitpoint.getZ(), 0);

            if (isInShadow(start, dir)) {
                for (int i = 0; i < 3; i++) {
                    rgb[i] -= rgb[i] * 0.1 * LIGHTSOURCEFACTOR; // dim the scene a bit
                }
                continue;
            }

            // continue onto diffuse and specular
            double[] s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), hitpoint.getCoords()));

            double mDots = Utility.dot(s, normalVector);
            double cRefraction = mDots;

            if (mDots >= 0.0001) {
                // hitpoint is pointed towards the light

                double lambert = mDots;
                for (int i = 0; i < 3; i++) {
                    rgb[i] += specular[i] * DW * currentObject.getMaterial().getkDistribution()[1] * fresnelCoefficient0RGB[i] * lambert * (lightsource.getValue()[i] * LIGHTSOURCEFACTOR);
                }
            }

            /*
            SPECULAR
            */

            // h: halfway vector (between incoming light and ray)
            double[] h = Utility.normalize(Utility.sum(v, s));

            // angle between h and m (normal vector)
            double mDoth = Utility.dot(h, normalVector);

            if (mDoth >= 0.0001) {
                // angle between h and normalVector
                double angle = getAngle(normalVector, h);
                if (Double.isNaN(angle))
                    System.out.println("Nan found");

                double d = Math.exp(-Math.pow(Math.tan(angle) / mRoughness, 2)) / (4 * mRoughness * mRoughness * Math.pow(angle, 4));

                // G will scale the strength of the specular component
                // fraction of light that is not shadowed.
                double gs = (2 * Utility.dot(normalVector, h) * Utility.dot(normalVector, v)) / Utility.dot(h, s);
                // fraction of light that is not masked.
                double gm = (2 * Utility.dot(normalVector, h) * Utility.dot(normalVector, s)) / Utility.dot(h, s);
                double g = Math.min(Math.min(1, gm), gs);

                // calculate the Fresnel coeff
                double angleOfIncidence = getAngle(s, normalVector);

                if (Double.isNaN(angleOfIncidence))
                    debug = 0;

                // gRefractionSquaredRGB = g² = η² + c² - 1
                double[] gRefractionSquaredRGB = new double[3];
                for (int i = 0; i < 3; i++) {
                    gRefractionSquaredRGB[i] = Math.pow(currentObject.getMaterial().getRefractionIndex()[i], 2) + angleOfIncidence * angleOfIncidence - 1;
                }

                double[] gRefraction = Arrays.stream(gRefractionSquaredRGB).map(Math::sqrt).toArray();

                // now we need the fresnel coeff at the angle non-zero
                double[] fresnelCoefficientAngleRGB = new double[3];
                for (int i = 0; i < 3; i++) {
                    fresnelCoefficientAngleRGB[i] = 0.5 * (Math.pow(gRefraction[i] - cRefraction, 2) / Math.pow(gRefraction[i] + cRefraction, 2)) * (1 + Math.pow((cRefraction * (gRefraction[i] + cRefraction) - 1) / (cRefraction * (gRefraction[i] - cRefraction) + 1), 2));
                }

                double[] torranceSpecularRGB = new double[3];
                double mDotV = Utility.dot(normalVector, v);
                if (mDotV == 0)
                    mDotV = 0.0001; // for when we are looking straight to an object. (when normalvector dot v == 0) we can't use this in the denominator up next.

                for (int i = 0; i < 3; i++) {
                    torranceSpecularRGB[i] = (fresnelCoefficientAngleRGB[i] * d * g) / mDotV;
                }

                for (int i = 0; i < 3; i++) {
                    rgb[i] += specular[i] * currentObject.getMaterial().getkDistribution()[2] * DW * torranceSpecularRGB[i];
                }
            }
        }

        /*
            REFLECTION & TRANSPARENCY
        */

        if (Double.isNaN(rgb[0])) {
            //getShading(ray, currentObject, intersection, previousRgb, recurseLevel);
            System.out.println("Nan shading Found");
        }

        // REFLECTION
        if (recurseLevel + 1 <= MAXRECURSELEVEL) {
            Vector vectorNormalVector = new Vector(normalVector);
            double dirDotNormalvector = Utility.dot(ray.getDir(), vectorNormalVector);

            recurseLevel++;

            if (currentObject.getMaterial().getShininess() >= 0.6) {
                // spawn ray from hitpoint and call getShade()
                Vector r = Utility.subtract(ray.getDir(), Utility.multiplyElementWise(2*dirDotNormalvector, vectorNormalVector));

                Ray reflection = new Ray(Utility.normalize(start), r);

                Tuple<Object, Intersection> objectIntersection = getHit(reflection);

                Object reflectedObjectHit = objectIntersection.getObject();
                Intersection reflectedIntersectionHit = objectIntersection.getIntersection();

                if (reflectedObjectHit != null && reflectedObjectHit != currentObject) {
                        double[] reflectedColors = getShading(reflection, reflectedObjectHit, reflectedIntersectionHit, rgb.clone(), recurseLevel, currentObject.getMaterial().getSpeedOfLight());

                    for (int i = 0; i < 3; i++)
                        rgb[i] += (float)(1/recurseLevel) * currentObject.getMaterial().getShininess() * reflectedColors[i];
                }
            }

            /* REFRACTION */ // Inside the object (hit enter) and outside (hit exit)
            /*if (currentObject.getMaterial().getTransparency() > 0.1) {
                // spawn ray from hitpoint and call getShade()
                double[] t = new double[4];

                double c1 = previousObject;
                double c2 = currentObject.getMaterial().getSpeedOfLight();
                double factor = c2 / c1;
                double thetaOne = getAngle(ray.getDir().getCoords(), normalVector);
                double thetaTwo = Math.asin(Math.sin(thetaOne) * factor);

                for (int i = 0; i < t.length; i++) {
                    t[i] = factor * ray.getDir().getCoords()[i] + (factor * dirDotNormalvector - Math.cos(thetaTwo)) * normalVector[i];
                }

                Vector newDir = new Vector(Utility.normalize(Utility.normalize(t)));
                Ray refraction = new Ray(Utility.normalize(start), newDir);

                Tuple<Object, Intersection> objectIntersection = getHit(refraction);

                Object refractedObjectHit = objectIntersection.getObject();
                Intersection refractedIntersectionHit = objectIntersection.getIntersection();

                if (refractedObjectHit != null && refractedObjectHit != currentObject) {
                    double[] reflectedColors = getShading(refraction, refractedObjectHit, refractedIntersectionHit, rgb.clone(), recurseLevel, currentObject.getMaterial().getSpeedOfLight());

                    for (int i = 0; i < 3; i++)
                        rgb[i] += currentObject.getMaterial().getTransparency() * reflectedColors[i];
                }
            }*/
        }

        return rgb;
    }

    private double[] getTexture(Texture texture, double x, double y, double z) {
        if (texture == Texture.CHECKERBOARD) {
            boolean u = ((int) (x * 0.125)) % 2 == 0;
            boolean v = ((int) (y * 0.125)) % 2 == 0;
            boolean w = ((int) (z * 0.125)) % 2 == 0;

            if (u ^ v ^ w) {
                if ((x < 0 && y > 0) || (x > 0 && y < 0)) {
                    return new double[]{0, 0, 0};
                }
                else {
                    return new double[]{1, 1, 1};
                }
            } else if ((x < 0 && y > 0) || (x > 0 && y < 0)) {
                return new double[]{1, 1, 1};
            }
            else {
                return new double[]{0, 0, 0};
            }
        }

        return new double[]{1, 1, 1};
    }

    private boolean isInShadow(Vector start, Vector dir) {
        Ray shadowFeeler = new Ray(start, dir);
        for (Object o : scene.getObjects()) { //shoot the ray and check if we got a hit with any object
            Intersection intersection = o.getFirstHitPoint(shadowFeeler);
            if (intersection != null && intersection.getT1() >= 0.0001 && intersection.getT2() >= 0.0001) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get angle between two vectors (in Radians)
     *
     * @param vectorOne: 1st vector
     * @param vectorTwo: 2nd vector
     * @return Double: angle in Radians
     */
    private double getAngle(double[] vectorOne, double[] vectorTwo) {
        double cosine = Utility.dot(vectorOne, vectorTwo) / (Utility.norm(vectorOne) * Utility.norm(vectorTwo));
        return Math.acos(cosine);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void saveBuffer() throws IOException {
        File outputfile = new File("image.png");
        ImageIO.write(buffer, "png", outputfile);
    }
}
