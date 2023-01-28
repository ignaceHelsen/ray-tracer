package main;

import main.object.Object;
import main.object.Sphere;
import main.object.Tuple;
import main.sdl.Settings;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.DoubleStream;

public class Renderer {
    private final boolean SPIRAL_RENDER = true;
    private final boolean REVERSE_SPIRAL = true;
    private final boolean RANDOM_RENDER = false;
    private final int SSA = 9; // antialiasing

    private final double lightsourceFactor; // or a bit of contrast
    private final double epsilon; // the difference that will be subtracted for shadowing
    private final int maxRecurseLevel;
    private final boolean shadowsEnabled;
    private final boolean reflection;
    private final boolean refraction;

    private final int threads;

    private BufferedImage skybox;

    private final JFrame frame;
    private final double focallength, screenWidth, screenHeight;
    private Scene scene;
    private final double cmax;
    private final double rmax;
    private final Canvas canvas;
    private final BufferedImage buffer;
    private final BufferStrategy strategy;

    private final double air = 299_792_458 * 0.9997;

    public Renderer(double focallength, double screenWidth, double screenHeight, double cmax, double rmax, Settings settings) {
        this.focallength = focallength;
        this.frame = new JFrame();
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.cmax = cmax;
        this.rmax = rmax;

        this.threads = 1;

        /*
        SETTINGS
         */

        this.lightsourceFactor = settings.getLightsourceFactor();
        this.epsilon = settings.getEpsilon();
        this.maxRecurseLevel = settings.getMaxRecurseLevel();
        this.shadowsEnabled = settings.isShadowsEnabled();
        this.reflection = settings.isReflection();
        this.refraction = settings.isRefraction();

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
        this.skybox = new BufferedImage((int) screenWidth, (int) screenHeight, BufferedImage.TYPE_INT_RGB);
        try {
            this.skybox = ImageIO.read(new File("skybox.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExecutorService es = Executors.newFixedThreadPool(threads);

        int rowIndex = 0;
        int columnIndex = 0;

        try {
            if (SPIRAL_RENDER) {
                int rows = 60;
                int columns = 60;

                List<int[]> spiralOrder = RenderSettings.getSpiralOrder(columns, rows);
                if (REVERSE_SPIRAL)
                    Collections.reverse(spiralOrder);

                double threadedSpiralScreenHeight = screenHeight / rows;
                double threadedSpiralScreenWidth = screenWidth / columns;

                for (int[] ints : spiralOrder) {
                    rowIndex = ints[0];
                    columnIndex = ints[1];

                    int finalColumnIndex1 = columnIndex;
                    int finalRowIndex1 = rowIndex;

                    es.execute(() -> {
                        double startColumn = finalColumnIndex1 * threadedSpiralScreenWidth;
                        double finishColumn = threadedSpiralScreenWidth + (finalColumnIndex1 * threadedSpiralScreenWidth);

                        double startRow = finalRowIndex1 * threadedSpiralScreenHeight;
                        double finishRow = threadedSpiralScreenHeight + (finalRowIndex1 * threadedSpiralScreenHeight);

                        traceRegion(startColumn, finishColumn, startRow, finishRow, h, w);
                    });
                }
            } else {
                // normal order
                double threadedScreenHeight = screenHeight / (threads * 2);
                double threadedScreenWidth = screenWidth / (threads * 2);

                while (columnIndex * threadedScreenWidth < screenWidth) {
                    int finalColumnIndex = columnIndex;
                    rowIndex = 0;

                    while (rowIndex * threadedScreenHeight < screenHeight) {
                        int finalRowIndex = rowIndex;

                        es.execute(() -> {
                            double startColumn;
                            if (RANDOM_RENDER) {
                                // random rendering (does work but does not perform a complete render). It's quite funny tho
                                startColumn = ThreadLocalRandom.current().nextInt((int) screenWidth);
                            } else {
                                startColumn = finalColumnIndex * threadedScreenWidth;
                            }
                            double finishColumn = threadedScreenWidth + (finalColumnIndex * threadedScreenWidth);

                            double startRow = finalRowIndex * threadedScreenHeight;
                            double finishRow = threadedScreenHeight + (finalRowIndex * threadedScreenHeight);

                            traceRegion(startColumn, finishColumn, startRow, finishRow, h, w);
                        });
                        rowIndex++;
                    }
                    columnIndex++;
                }
            }

            es.shutdown();
            es.awaitTermination(10, TimeUnit.MINUTES);
        } catch (
                InterruptedException e) {
            e.printStackTrace();
        }

        float endtime = System.nanoTime();
        System.out.println(endtime - starttime);

    }

    private void traceRegion(double startColumn, double endColumn, double startRow, double finishRow, double h, double w) {
        for (double r = startRow; r <= finishRow - 1; r++) { // nRows
            for (double c = startColumn; c <= endColumn - 1; c++) { // nColumns
                double[][] rgbValues = new double[SSA][3];

                double x = c;
                double y = r;

                for (int i = 0; i < SSA; i++) {
                    if (SSA == 4) {
                        x = c + RenderSettings.aaFourMap[i][0];
                        y = r + RenderSettings.aaFourMap[i][1];
                    } else if (SSA == 9) {
                        x = c + RenderSettings.aaNineMap[i][0];
                        y = r + RenderSettings.aaNineMap[i][1];
                    }

                    Vector dir = new Vector(-focallength, -(w - screenWidth * (x / cmax)), -(h - screenHeight * (y / rmax)), 0);
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
                        rgbValues[i] = getShading(normalizedRay, closestObject, intersectionHit, rgb, recurseLevel, air);

                    } else {
                        rgbValues[i] = new double[]{0, 0, 0};
                    }
                }

                // get average from all rgb values
                double[] averageRgbValues = new double[3];
                // red
                averageRgbValues[0] = Arrays.stream(rgbValues).flatMapToDouble(v -> DoubleStream.of(v[0])).average().getAsDouble();

                // green
                averageRgbValues[1] = Arrays.stream(rgbValues).flatMapToDouble(v -> DoubleStream.of(v[1])).average().getAsDouble();

                // blue
                averageRgbValues[2] = Arrays.stream(rgbValues).flatMapToDouble(v -> DoubleStream.of(v[2])).average().getAsDouble();


                // rgb is now a value between 0 and 1
                averageRgbValues = Arrays.stream(averageRgbValues).map(v -> v * 255).toArray();
                averageRgbValues = Arrays.stream(averageRgbValues).map(v -> {
                    if (v > 255) v = 255;
                    else if (v < 0) v = 0;
                    return v;
                }).toArray();

                int color = ((int) averageRgbValues[0] << 16) | ((int) averageRgbValues[1] << 8) | (int) averageRgbValues[2];
                buffer.setRGB((int) c, (int) r, color);
            }
        }
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
                if ((currentIntersection.getEnter() == null && currentIntersection.getT2() >= 0.0001 && currentIntersection.getT2() < minIntersectionTime) || (currentIntersection.getEnter() != null && currentIntersection.getT1() >= 0.0001 && currentIntersection.getT1() < minIntersectionTime)) {
                    // the current intersection is in front of the previous hit, which means we have to deal with this hit in further code
                    intersectionHit = currentIntersection;
                    closestObject = currentObject;

                    // for priorities with objects that are closer by and should be painted instead of objects behind it
                    if (currentIntersection.getEnter() == null) // set min as exit time (=T2) (since only an exit has been registered)
                        minIntersectionTime = currentIntersection.getT2();
                    else
                        minIntersectionTime = currentIntersection.getT1();
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
    private double[] getShading(Ray ray, Object currentObject, Intersection intersection, double[] rgb,
                                int recurseLevel, double previousObject) {
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
        Vector start = Utility.sum(hitpoint, Utility.multiplyElementWise(epsilon, new Vector(normalVector)));

        // for each lightsource
        for (Map.Entry<Vector, Tuple<double[], Double>> lightsource : scene.getLightsources().entrySet()) {
            // check first for possible shadow spots
            Vector dir = new Vector(lightsource.getKey().getX() - hitpoint.getX(), lightsource.getKey().getY() - hitpoint.getY(), lightsource.getKey().getZ() - hitpoint.getZ(), 0);

            if (shadowsEnabled) {
                if (isInShadow(start, dir)) {
                    for (int i = 0; i < 3; i++) {
                        rgb[i] -= rgb[i] * 0.01 * lightsourceFactor; // dim the scene a bit
                    }
                    continue;
                }
            }

            // continue onto diffuse and specular
            double[] s = Utility.normalize(Utility.subtract(lightsource.getKey().getCoords(), hitpoint.getCoords()));

            double mDots = Utility.dot(s, normalVector);
            double cRefraction = mDots;

            if (mDots >= 0.0001) {
                // hitpoint is pointed towards the light

                double lambert = mDots;
                for (int i = 0; i < 3; i++) {
                    rgb[i] += specular[i] * lightsource.getValue().getDw() * currentObject.getMaterial().getkDistribution()[1] * fresnelCoefficient0RGB[i] * lambert * (lightsource.getValue().getLightColor()[i] * lightsourceFactor);
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
                    rgb[i] += specular[i] * currentObject.getMaterial().getkDistribution()[2] * lightsource.getValue().getDw() * torranceSpecularRGB[i];
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
        if (recurseLevel + 1 <= maxRecurseLevel) {
            Vector vectorNormalVector = new Vector(normalVector);
            double dirDotNormalvector = Utility.dot(ray.getDir(), vectorNormalVector);

            recurseLevel++;

            if (currentObject.getMaterial().getShininess() >= 0.6 && reflection) {
                // spawn ray from hitpoint and call getShade()
                Vector r = Utility.subtract(ray.getDir(), Utility.multiplyElementWise(2 * dirDotNormalvector, vectorNormalVector));

                r.setType(0);
                Ray reflection = new Ray(start, Utility.normalize(r));

                Tuple<Object, Intersection> hit = getHit(reflection);

                Object reflectedObjectHit = hit.getObject();
                Intersection reflectedIntersectionHit = hit.getIntersection();

                if (reflectedObjectHit != null && reflectedObjectHit != currentObject) {
                    // this code does not take into account boolean objects, we can assume that any reflection from an object to another will be cast through air
                    double[] reflectedColors = getShading(reflection, reflectedObjectHit, reflectedIntersectionHit, rgb.clone(), recurseLevel, air);

                    for (int i = 0; i < 3; i++)
                        rgb[i] += (float) (1 / recurseLevel) * currentObject.getMaterial().getShininess() * reflectedColors[i];
                }
            }

            /* REFRACTION */ // Inside the object (hit enter) and outside (hit exit)
            if (currentObject.getMaterial().getTransparency() > 0.1 && refraction) {
                //since we want to spawn a ray inside the object we slightly adjust the ray to start from inside the object: notice the subtract()
                Vector startInnerRefraction = Utility.subtract(hitpoint, Utility.multiplyElementWise(epsilon, new Vector(normalVector)));

                // spawn ray from hitpoint and call getShade()
                double[] t = new double[4];

                double c1 = previousObject;
                double c2 = currentObject.getMaterial().getSpeedOfLight();
                double factor = c2 / c1;
                //double thetaOne = getAngle(ray.getDir().getCoords(), normalVector);
                //double thetaTwo = Math.asin(Math.sin(thetaOne) * factor);
                double cosThetaTwo = Math.sqrt(1 - Math.pow(factor, 2) * (1 - Math.pow(dirDotNormalvector, 2)));

                for (int i = 0; i < t.length; i++) {
                    t[i] = factor * ray.getDir().getCoords()[i] + (factor * dirDotNormalvector - cosThetaTwo) * normalVector[i];
                }

                Vector NewDir = new Vector(Utility.normalize(t));
                Ray refraction = new Ray(startInnerRefraction, NewDir);

                Tuple<Object, Intersection> objectIntersection = getHit(refraction);

                Object refractedObjectHit = objectIntersection.getObject();
                Intersection refractedIntersectionHit = objectIntersection.getIntersection();

                if (refractedObjectHit != null) {
                    if (refractedObjectHit == currentObject) {
                        // TODO, change the above if when boolean objects have been added -> then the next hit will be the inner object.
                        // ray from inside the object has hit the outer edge of the current object
                        // now we spawn a ray starting from this exit hit
                        // we know that the next medium the ray will travel through will be air

                        // start the ray from just outside the object
                        Vector exitPoint = new Vector(Utility.multiplyMatrices(refractedIntersectionHit.getExit().getCoords(), currentObject.getTransformation().getTransformation()));
                        Vector startOuterRefraction = Utility.subtract(exitPoint, Utility.multiplyElementWise(epsilon, new Vector(refractedIntersectionHit.getNormalVector())));

                        dirDotNormalvector = Utility.dot(refraction.getDir().getCoords(), refractedIntersectionHit.getNormalVector());

                        // same as before:
                        t = new double[4];

                        c1 = currentObject.getMaterial().getSpeedOfLight();
                        c2 = air;
                        factor = c2 / c1;
                        cosThetaTwo = Math.sqrt(1 - Math.pow(factor, 2) * (1 - Math.pow(dirDotNormalvector, 2)));

                        for (int i = 0; i < t.length; i++) {
                            t[i] = factor * refraction.getDir().getCoords()[i] + (factor * dirDotNormalvector - cosThetaTwo) * refractedIntersectionHit.getNormalVector()[i];
                        }

                        Vector newDir = new Vector(Utility.normalize(t));
                        Ray outerrefraction = new Ray(startOuterRefraction, newDir);

                        Tuple<Object, Intersection> outerObjectIntersection = getHit(outerrefraction);

                        Object outerRefractedObjectHit = outerObjectIntersection.getObject();
                        Intersection outerRefractedIntersectionHit = outerObjectIntersection.getIntersection();

                        if (outerRefractedObjectHit != null && outerRefractedObjectHit != currentObject) {
                            double[] reflectedColors = getShading(outerrefraction, outerRefractedObjectHit, outerRefractedIntersectionHit, rgb.clone(), recurseLevel, currentObject.getMaterial().getSpeedOfLight());

                            for (int i = 0; i < 3; i++)
                                rgb[i] += 0.1 * currentObject.getMaterial().getTransparency() * reflectedColors[i];
                        }
                    }
                }
            }
        }

        return rgb;
    }

    private double[] getTexture(Texture texture, double x, double y, double z) {
        if (texture == Texture.CHECKERBOARD) {
            boolean u = ((int) (x * 0.0125)) % 2 == 0;
            boolean v = ((int) (y * 0.0125)) % 2 == 0;
            boolean w = ((int) (z * 0.0125)) % 2 == 0;

            if (u ^ v ^ w) {
                if ((x < 0 && y > 0) || (x > 0 && y < 0)) {
                    return new double[]{0, 0, 0};
                } else {
                    return new double[]{1, 1, 1};
                }
            } else if ((x < 0 && y > 0) || (x > 0 && y < 0)) {
                return new double[]{1, 1, 1};
            } else {
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
